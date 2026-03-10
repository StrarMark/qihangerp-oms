package cn.qihangerp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI聊天历史表
 * @TableName ai_conversation_history
 */
@TableName(value = "ai_conversation_history")
@Data
public class AiConversationHistory implements Serializable {
    /**
     * 主键id，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 角色：user或assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息时间戳
     */
    private Long timestamp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}