<template>
  <div class="app-container">
    <order-list-wait />
  </div>
</template>

<script>
import OrderListWait from '@/views/shipping/manual_ship/order_list.vue'
import PrintHome from '@/views/shipping/ewaybillPrint/index.vue'

import { listPlatform } from '@/api/shop/shop'

export default {
  name: 'Print',
  components: {
    PrintHome, OrderListWait },
  data() {
    return {
      activeName: 'waitShipping',
      typeList: []
    }
  },
  created() {

  },
  mounted() {
    listPlatform({ status: 0 }).then(res => {
      this.typeList = res.rows.filter(x => x.id < 900)
      // this.activeName = this.typeList[0].code
    })
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event)
    }
  }
}
</script>
