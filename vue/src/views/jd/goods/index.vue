<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="店铺商品" name="LogisticsCompany" >
       <goods-list-jd></goods-list-jd>
      </el-tab-pane>
      <el-tab-pane label="店铺商品SKU" name="EwaybillAccount" lazy>
        <goods-sku-jd></goods-sku-jd>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import GoodsListJd from "@/views/jd/goods/goods_list.vue";
import GoodsSkuJd from "@/views/jd//goods/sku_list.vue";

export default {
  name: "GoodsJd",
  components:{
    GoodsListJd,GoodsSkuJd
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
