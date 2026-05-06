package cn.qihangerp.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ShopWaybillCodeVo<T> {
    private int total;
    private int fail;
    private int isSend;
    private int success;
    private List<T> list;
}
