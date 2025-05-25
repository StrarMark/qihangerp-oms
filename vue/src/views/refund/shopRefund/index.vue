<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <refund-offline v-if="item.id === 999"></refund-offline>
        <refund-tao v-if="item.id === 100"></refund-tao>
        <refund-jd v-if="item.id === 200"></refund-jd>
        <refund-pdd v-if="item.id === 300"></refund-pdd>
        <refund-dou v-if="item.id === 400"></refund-dou>
        <refund-wei v-if="item.id === 500"></refund-wei>

      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>
import RefundOffline  from "@/views/refund/private/index";
import RefundTao  from "@/views/tao/refund/index";
import RefundJd  from "@/views/jd/refund/index";
import RefundDou from "@/views/dou/refund/index.vue";
import RefundPdd from "@/views/pdd/refund/index.vue";
import RefundWei from "@/views/wei/refund/index.vue";

import {listPlatform} from "@/api/shop/shop";

export default {
  name: "refund",
  components:{RefundPdd, RefundDou, RefundTao,RefundJd,RefundOffline,RefundWei},
  data() {
    return {
      activeName: 'OFFLINE',
      typeList: [],
    };
  },
  created() {

  },
  mounted() {
    listPlatform({status:0}).then(res => {
      this.typeList = res.rows;
      this.activeName = this.typeList[res.rows.length-1].code
    })
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
