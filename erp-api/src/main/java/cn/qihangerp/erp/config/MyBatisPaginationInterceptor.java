package cn.qihangerp.erp.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * MyBatis-Plus 3.5.16 分页拦截器
 * 替代已移除的 PaginationInnerInterceptor
 */
public class MyBatisPaginationInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
        if (page == null || ms.getSqlCommandType() != SqlCommandType.SELECT) {
            return;
        }
        long size = page.getSize();
        if (size <= 0) return;

        // 1. COUNT 查询
        String countSql = "SELECT COUNT(*) FROM (" + boundSql.getSql() + ") tmp_count";
        Connection connection = executor.getTransaction().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(countSql)) {
            List<ParameterMapping> mappings = boundSql.getParameterMappings();
            Object paramObj = boundSql.getParameterObject();
            for (int i = 0; i < mappings.size(); i++) {
                String propName = mappings.get(i).getProperty();
                Object value = resolveParamValue(propName, paramObj);
                ps.setObject(i + 1, value);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    page.setTotal(rs.getLong(1));
                }
            }
        }

        // 2. LIMIT
        long offset = (page.getCurrent() > 0) ? (page.getCurrent() - 1) * size : 0;
        try {
            java.lang.reflect.Field sqlField = BoundSql.class.getDeclaredField("sql");
            sqlField.setAccessible(true);
            sqlField.set(boundSql, boundSql.getSql() + " LIMIT " + offset + ", " + size);
        } catch (Exception e) {
            throw new RuntimeException("SQL分页改写失败", e);
        }
    }

    private Object resolveParamValue(String property, Object paramObj) {
        if (paramObj == null) return null;
        // 简单类型直接返回
        if (property.contains(".")) {
            String[] parts = property.split("\\.");
            Object obj = resolveParamValue(parts[0], paramObj);
            return resolveParamValue(parts[1], obj);
        }
        // MyBatis 封装的 ParamMap 或直接参数
        if (paramObj instanceof java.util.Map) {
            return ((java.util.Map<String, ?>) paramObj).get(property);
        }
        // 简单属性通过反射获取
        try {
            java.lang.reflect.Field field = paramObj.getClass().getDeclaredField(property);
            field.setAccessible(true);
            return field.get(paramObj);
        } catch (Exception e) {
            // 尝试 getter
            try {
                String getter = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
                return paramObj.getClass().getMethod(getter).invoke(paramObj);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    @Override public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {}
    @Override public void setProperties(Properties properties) {}
}
