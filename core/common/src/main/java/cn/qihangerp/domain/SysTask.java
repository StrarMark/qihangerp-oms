package cn.qihangerp.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 后台任务配置表
 * @TableName sys_task
 */
@Data
public class SysTask implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String taskName;

    /**
     * 
     */
    private String cron;

    /**
     * 
     */
    private String method;

    /**
     * 
     */
    private String remark;

    /**
     * 
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}