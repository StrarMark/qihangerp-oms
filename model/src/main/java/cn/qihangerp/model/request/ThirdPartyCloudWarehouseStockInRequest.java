package cn.qihangerp.model.request;

import java.util.List;

/**
 * 第三方云仓手动确认入库请求
 */
public class ThirdPartyCloudWarehouseStockInRequest {
    private Long stockInId; // 入库单ID
    private Long warehouseId; // 仓库ID
    private List<Item> items; // 商品明细

    public Long getStockInId() {
        return stockInId;
    }

    public void setStockInId(Long stockInId) {
        this.stockInId = stockInId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * 商品明细
     */
    public static class Item {
        private Long skuId; // 商品SKU ID
        private Long goodsId; // 商品ID
        private Integer quantity; // 实际入库数量
        private Double purPrice; // 采购价格
        private Integer inventoryMode; // 库存模式

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public Long getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(Long goodsId) {
            this.goodsId = goodsId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getPurPrice() {
            return purPrice;
        }

        public void setPurPrice(Double purPrice) {
            this.purPrice = purPrice;
        }

        public Integer getInventoryMode() {
            return inventoryMode;
        }

        public void setInventoryMode(Integer inventoryMode) {
            this.inventoryMode = inventoryMode;
        }
    }
}