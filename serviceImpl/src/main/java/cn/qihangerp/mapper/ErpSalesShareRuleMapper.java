package cn.qihangerp.mapper;

import cn.qihangerp.model.entity.ErpSalesShareRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分账规则Mapper
 */
public interface ErpSalesShareRuleMapper extends BaseMapper<ErpSalesShareRule> {

    @Select("SELECT * FROM erp_sales_order_share_rule WHERE status = 1 ORDER BY priority ASC")
    List<ErpSalesShareRule> selectActiveRules();
}
