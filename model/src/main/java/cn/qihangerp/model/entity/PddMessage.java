package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName oms_pdd_message
 */
@TableName(value ="oms_pdd_message")
@Data
public class PddMessage {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺id
     */
    private Long mallId;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    private Integer status;
    private Long createdTime;
    private Long updatedTime;
}