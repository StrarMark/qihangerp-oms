<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="店铺商品" name="LogisticsCompany" >
       <goods-list-tao></goods-list-tao>
      </el-tab-pane>
      <el-tab-pane label="店铺商品SKU" name="EwaybillAccount" lazy>
        <goods-sku-tao></goods-sku-tao>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import GoodsListTao from "@/views/tao/goods/goods_list.vue";
import GoodsSkuTao from "@/views/tao/goods/sku_list.vue";

export default {
  name: "GoodsTao",
  components:{
    GoodsListTao,GoodsSkuTao
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
