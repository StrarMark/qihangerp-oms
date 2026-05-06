package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 平台物流电子面单打印模板
 * @TableName oms_platform_logistics_waybill_template
 */
@TableName(value ="oms_platform_logistics_waybill_template")
@Data
public class PlatformLogisticsWaybillTemplate {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台id
     */
    private Integer platformId;

    /**
     * 平台类别
     */
    private String platformType;

    /**
     * 物流单打印模板的面单类型:未知-NONE,快递标准面单STANDARD,TRIPLE快递三联面单,PORTABLE_TRIPLE快递便携式三联单,EX_STANDARD快运标准面单,EX_TRIPLE快运三联面单,ONE快递一联单,PORTABLE_ONE快递便携式一联单,CUSTOM_ONE快递定制一联单,EX_SINGLE快运一联单,EX_DOUBLE快运二联单
     */
    private String templateWaybillType;

    /**
     * 物流ID
     */
    private String logisticsId;

    /**
     * 打印模板对应物流公司的平台编码
     */
    private String cpCode;

    /**
     * 模板的总宽度，单位mm。
     */
    private Float width;

    /**
     * 模板的总高度，单位mm。
     */
    private Float height;

    /**
     * 打印模板来源:CAINIAO菜鸟云打印,PINDUODUO拼多多云打印,JOS_YUN京东云打印,DOUYIN抖音云打印,VIP_YUN唯品会云打印,KS_YUN快手云打印,SHUNFENG_YUN顺丰云打印,XHS_YUN小红书云打印,WX_VS_YUN微信视频号云打印,DW_YUN得物云打印,MT_YUN美团云打印
     */
    private String templateSource;

    /**
     * 打印模板预览图片的URL
     */
    private String perviewUrl;

    /**
     * 标准打印模板编码，和templateId 两者至少有一个
     */
    private String templateCode;

    /**
     * 标准打印模板ID
     */
    private Long templateId;

    /**
     * 打印模板的名称
     */
    private String templateName;

    /**
     * 
     */
    private Integer templateType;

    /**
     * 打印模板的在线URL
     */
    private String templateUrl;
    private String customerTemplateUrl;//自定义模板URL，templateCustomerType=0时该字段为空，支持的大小是76*30和100*40两种尺寸;isv可根据标记语言规则自己实现自定义区域，新版是小红书自研的标记语言，语法格式是json；旧版使用的菜鸟的标记语言，语法格式是xml
    private String customerPrintItems;//自定义打印项参数列表，注意格式是List<String>，示例：["order","buyerMemo"]
    private String templateCustomerType;//自定义类型 0-标准 1-订单号 2-商品名称/规格/数量 3-商品名称/规格/数量 + 买家留言 + 商家备注 4-订单号 + 商品名称/规格/数量 + 买家留言 + 商家备注 10-商家云打印系统自定义

    /**
     * 
     */
    private Integer version;

    /**
     * 备注
     */
    private String remark;
}