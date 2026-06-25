<template>
  <div class="app-container">
    <el-tabs v-if="shopType!==911" v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="按商品展示" name="goodsList">
        <goods-list :shop-type="shopType" :refresh="refresh" :target-shop-id="targetShopId" :platform-list="platformList" />
      </el-tab-pane>
      <el-tab-pane label="按规格展示" name="skuList" lazy>
        <goods-sku :shop-type="shopType" />
      </el-tab-pane>
    </el-tabs>
    <!--    <goods-sku-offline :shopType="shopType"></goods-sku-offline>-->
  </div>
</template>

<script>
import GoodsList from '@/views/shop/goods/goods_list.vue'
import GoodsSku from '@/views/shop/goods/goods_sku.vue'
import GoodsSkuOffline from '@/views/shop/goods/goods_sku_offline.vue'

export default {
  name: 'ShopGoodsIndex',
  components: {
    GoodsList, GoodsSku, GoodsSkuOffline
  },
  props: {
    shopType: {
      type: Number,
      required: false
    },
    refresh: {
      type: String,
      required: false,
      default: ''
    },
    targetShopId: {
      type: String,
      required: false,
      default: ''
    },
    platformList: {
      type: Array,
      required: false,
      default: () => []
    }
  },
  data() {
    return {
      activeName: 'goodsList'
    }
  },
  created() {

  },
  mounted() {
    console.log('=====shopType', this.shopType)
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event)
    }
  }
}
</script>
