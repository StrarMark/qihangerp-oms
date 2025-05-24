<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="线下渠道" name="printOffline">
        <print-offline></print-offline>
      </el-tab-pane>
      <el-tab-pane label="淘宝天猫" name="printTao" lazy>
        <print-tao></print-tao>
      </el-tab-pane>
      <el-tab-pane label="京东POP" name="printJd" lazy>
        <print-jd></print-jd>
      </el-tab-pane>
      <el-tab-pane label="拼多多" name="printPdd" lazy>
        <print-pdd></print-pdd>
      </el-tab-pane>
      <el-tab-pane label="抖店" name="printDou" lazy>
        <print-dou></print-dou>
      </el-tab-pane>
      <el-tab-pane label="微信小店" name="printWei" lazy>
        <print-wei></print-wei>
      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>
import printOffline  from "@/views/order/private/shipPrintOrderList.vue";
import printWei  from "@/views/wei/ewaybill/index.vue";
import printTao from "@/views/tao/ewaybill/index.vue";
import printPdd from "@/views/pdd/ewaybill/index.vue";
import printDou from "@/views/dou/ewaybill/index.vue";
import PrintJd from "@/views/jd/ewaybill/index.vue";


export default {
  name: "print",
  components:{PrintJd, printWei,printTao,printPdd,printDou,printOffline},
  data() {
    return {
      activeName: 'printOffline'
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
