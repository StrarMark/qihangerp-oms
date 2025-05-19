<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="店铺商品" name="LogisticsCompany" >
       <goods-tao-index></goods-tao-index>
      </el-tab-pane>
      <el-tab-pane label="店铺商品SKU" name="EwaybillAccount" lazy>
        <goods-tao-sku></goods-tao-sku>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import GoodsTaoIndex from "@/views/tao/goods/goods_list.vue";
import GoodsTaoSku from "@/views/tao/goods/sku_list.vue";

export default {
  name: "GoodsTao",
  components:{
    GoodsTaoIndex,GoodsTaoSku
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
