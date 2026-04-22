package cn.qihangerp.open.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 平台
 */
@Data
public class PlatformResponse implements Serializable {
    /**
     * ID
     */

    private Integer id;

    /**
     * 平台名
     */
    private String name;

    /**
     * 平台编码
     */
    private String code;

    private static final long serialVersionUID = 1L;
}