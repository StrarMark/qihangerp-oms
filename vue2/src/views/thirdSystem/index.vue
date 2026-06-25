<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="首页" name="thirdSysHome">
        <third-system-home />
      </el-tab-pane>
      <el-tab-pane label="飞书参数配置" name="feishu" lazy>
        <FeiShuConfig />
      </el-tab-pane>
      <el-tab-pane label="飞书Wiki多维表格读取" name="cloudWarehouse" lazy>
        <WikiTablePage />
      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>

import PostBackSetting from '@/views/thirdSystem/post_back_setting.vue'
import PushSetting from '@/views/thirdSystem/push_setting.vue'
import ThirdSystemHome from '@/views/thirdSystem/home.vue'

export default {
  name: 'Print',
  components: {
    PushSetting, PostBackSetting, ThirdSystemHome, FeiShuConfig, WikiTablePage },
  data() {
    return {
      activeName: 'thirdSysHome',
      typeList: []
    }
  },
  created() {

  },
  mounted() {
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event)
    }
  }
}
</script>
