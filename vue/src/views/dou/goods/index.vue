<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="店铺商品" name="LogisticsCompany" >
       <goods-list-dou></goods-list-dou>
      </el-tab-pane>
      <el-tab-pane label="店铺商品SKU" name="EwaybillAccount" lazy>
        <goods-sku-dou></goods-sku-dou>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import GoodsListDou from "@/views/dou/goods/goods_list.vue";
import GoodsSkuDou from "@/views/dou/goods/goods_sku.vue";

export default {
  name: "GoodsDou",
  components:{
    GoodsListDou,GoodsSkuDou
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
