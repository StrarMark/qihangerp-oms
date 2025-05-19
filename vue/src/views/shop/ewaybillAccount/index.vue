<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
<!--        <waybill-account-dou v-if="item.id === 100"></waybill-account-dou>-->
<!--        <print-jd v-if="item.id === 200"></print-jd>-->
<!--        <print-jd-vc v-if="item.id === 280"></print-jd-vc>-->
<!--        <print-pdd v-if="item.id === 300"></print-pdd>-->
        <waybill-account-dou  v-if="item.id === 400"></waybill-account-dou >
        <waybill-account-wei v-if="item.id === 500"></waybill-account-wei>
<!--        <print-offline v-if="item.id === 999"></print-offline>-->
      </el-tab-pane>

<!--      <el-tab-pane label="淘宝天猫" name="printTao" lazy>-->
<!--        <print-tao></print-tao>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="京东" name="printJd" lazy>-->
<!--        <print-jd></print-jd>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="拼多多" name="printPdd" lazy>-->
<!--        <print-pdd></print-pdd>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="抖店" name="printDou" lazy>-->
<!--        <print-dou></print-dou>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="视频号" name="printWei" lazy>-->
<!--        <print-wei></print-wei>-->
<!--      </el-tab-pane>-->
    </el-tabs>

  </div>
</template>

<script>

import waybillAccountDou from "@/views/shop/ewaybillAccount/dou.vue";
import waybillAccountWei from "@/views/shop/ewaybillAccount/wei.vue";

import {listPlatform} from "@/api/shop/shop";


export default {
  name: "print",
  components:{
    waybillAccountDou,waybillAccountWei},
  data() {
    return {
      activeName: '',
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
