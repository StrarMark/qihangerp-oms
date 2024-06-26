package com.qihang.tao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qihang.tao.domain.SShop;
import com.qihang.tao.service.SShopService;
import com.qihang.tao.mapper.SShopMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【s_shop(电商平台店铺表)】的数据库操作Service实现
* @createDate 2024-06-04 15:20:42
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




