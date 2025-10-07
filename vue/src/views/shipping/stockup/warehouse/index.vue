<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="card">
      <el-tab-pane label="发货订单" name="stockShip">
        <warehouse-shipment></warehouse-shipment>
      </el-tab-pane>
      <el-tab-pane label="发货订单商品" name="stockShipItem" lazy>
        <warehouse-shipment-item></warehouse-shipment-item>
      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>
import warehouseShipment  from "@/views/shipping/stockup/warehouse/order_list.vue";
import warehouseShipmentItem  from "@/views/shipping/stockup/warehouse/order_item_list.vue";


export default {
  name: "stocking",
  components:{warehouseShipment,warehouseShipmentItem},
  data() {
    return {
      activeName: 'stockShip'
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
