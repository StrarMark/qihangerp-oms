package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI分析记录实体类
 */
@Data
@TableName("ai_analysis_record")
public class AiAnalysisRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分析类型：sales-销售分析，inventory-库存优化，customer-客户洞察，operation-运营效率，custom-自定义分析
     */
    private String analysisType;

    /**
     * 分析输入内容
     */
    private String analysisContent;

    /**
     * 提示词内容
     */
    private String promptContent;

    /**
     * 分析结果
     */
    private String analysisResult;

    /**
     * 状态：0-分析中，1-已完成，2-失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
