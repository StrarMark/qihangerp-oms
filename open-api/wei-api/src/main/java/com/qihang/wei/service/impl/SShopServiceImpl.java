package com.qihang.wei.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qihang.wei.domain.SShop;
import com.qihang.wei.service.SShopService;
import com.qihang.wei.mapper.SShopMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【s_shop(电商平台店铺表)】的数据库操作Service实现
* @createDate 2024-06-11 15:13:13
*/
@AllArgsConstructor
@Service
public class SShopServiceImpl extends ServiceImpl<SShopMapper, SShop>
    implements SShopService{
    private final SShopMapper mapper;
    @Override
    public SShop selectShopById(Long shopId) {
        return mapper.selectById(shopId);
    }

    @Override
    public void updateSessionKey(Long shopId, String sessionKey) {
        SShop shop = new SShop();
        shop.setId(shopId);
        shop.setAccessToken(sessionKey);
        mapper.updateById(shop);
    }
}




