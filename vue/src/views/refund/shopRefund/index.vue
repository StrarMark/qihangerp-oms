<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <order-tao v-if="item.id === 100"></order-tao>
        <order-jd v-if="item.id === 200"></order-jd>
        <!--        <order-jd-vc v-if="item.id === 280"></order-jd-vc>-->
        <order-pdd v-if="item.id === 300"></order-pdd>
        <order-dou v-if="item.id === 400"></order-dou>
        <order-wei v-if="item.id === 500"></order-wei>
        <!--        <order-offline v-if="item.id === 999"></order-offline>-->
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>
import TaoRefund  from "@/views/tao/refund/index";
import JdRefund  from "@/views/jd/refund/index";
import DouRefund from "@/views/dou/refund/index.vue";
import PddRefund from "@/views/pdd/refund/index.vue";
import OrderTao from "@/views/tao/order/index.vue";
import OrderPdd from "@/views/pdd/order/index.vue";
import OrderDou from "@/views/dou/order/index.vue";
import OrderWei from "@/views/wei/order/index.vue";
import OrderJd from "@/views/jd/order/index.vue";
import {listPlatform} from "@/api/shop/shop";

export default {
  name: "refund",
  components:{OrderJd, OrderWei, OrderDou, OrderPdd, OrderTao, PddRefund, DouRefund, TaoRefund,JdRefund},
  data() {
    return {
      activeName: 'taoOrder',
      typeList: [],
    };
  },
  created() {

  },
  mounted() {
    listPlatform({status:0}).then(res => {
      this.typeList = res.rows;
      this.activeName = this.typeList[0].code
    })
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
