<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="平台店铺" name="OnlineShop">
        <online-shop></online-shop>
      </el-tab-pane>
      <el-tab-pane label="私域店铺" name="OfflineShop" lazy>
        <offline-shop></offline-shop>
      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>
import OfflineShop  from "@/views/shop/offline_shop.vue";
import OnlineShop  from "@/views/shop/online_shop.vue";

export default {
  name: "ShopIndex",
  components:{OfflineShop,OnlineShop},
  data() {
    return {
      activeName: 'OnlineShop'
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
