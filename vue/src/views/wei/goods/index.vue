<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="店铺商品" name="LogisticsCompany" >
       <goods-list-wei></goods-list-wei>
      </el-tab-pane>
      <el-tab-pane label="店铺商品SKU" name="EwaybillAccount" lazy>
        <goods-sku-wei></goods-sku-wei>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import GoodsListWei from "@/views/wei/goods/goods_list.vue";
import GoodsSkuWei from "@/views/wei/goods/goods_sku.vue";

export default {
  name: "GoodsWei",
  components:{
    GoodsListWei,GoodsSkuWei
  },
  data() {
    return {
      activeName: 'LogisticsCompany',
      typeList: [],
    };
  },
  created() {

  },
  mounted() {

  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
