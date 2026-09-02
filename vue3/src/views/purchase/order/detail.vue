<template>
  <div class="app-container">
    <el-card class="form-card">
      <template #header>
        <span>采购单详情</span>
      </template>
      <el-form ref="formRef" :model="form" size="small" style="width: 100%" :inline="true" label-width="128px">
        <el-col :span="8">
          <el-form-item label="采购单Id">
            <el-input v-model="form.id" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="采购单号">
            <el-input v-model="form.orderNum" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="采购状态">
            <el-tag :type="statusType">{{ statusText }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="创建时间">
            <el-input v-model="form.createTime" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="创建人">
            <el-input v-model="form.createBy" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="总金额">
            <el-input v-model="form.orderAmount" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="供应商">
            <el-input v-model="supplierName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="审核人">
            <el-input v-model="form.auditUser" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="审核时间">
            <el-input v-model="form.auditTimeText" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="供应商发货日期">
            <el-input v-model="form.supplierDeliveryTime" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="收货时间">
            <el-input v-model="form.receivedTimeText" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="入库时间">
            <el-input v-model="form.stockInTimeText" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="备注">
            <el-input v-model="form.remark" disabled />
          </el-form-item>
        </el-col>
      </el-form>
    </el-card>

    <el-card class="ship-card" style="margin-top: 20px;" v-if="shipData.id">
      <template #header>
        <span>物流信息</span>
      </template>
      <el-form size="small" :inline="true" label-width="128px">
        <el-col :span="8">
          <el-form-item label="物流公司">
            <el-input v-model="shipData.shipCompany" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="物流单号">
            <el-input v-model="shipData.shipNum" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="运费">
            <el-input v-model="shipData.freight" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="发货时间">
            <el-input v-model="shipData.shipTimeText" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="物流状态">
            <el-tag :type="shipStatusType">{{ shipStatusText }}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="收货时间">
            <el-input v-model="shipData.receiptTimeText" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="入库时间">
            <el-input v-model="shipData.stockInTimeText" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="入库仓库">
            <el-input v-model="shipData.warehouseName" disabled />
          </el-form-item>
        </el-col>
      </el-form>
    </el-card>

    <el-card class="items-card" style="margin-top: 20px;">
      <template #header>
        <span>商品明细</span>
      </template>
      <el-table :data="itemList" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="colorImage" label="商品图片" width="80">
          <template #default="scope">
            <image-preview :src="scope.row.colorImage" :width="50" :height="50" />
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" label="商品名称" min-width="150" />
        <el-table-column prop="specNum" label="SKU" width="120" />
        <el-table-column prop="colorValue" label="颜色" width="80" />
        <el-table-column prop="sizeValue" label="尺码" width="80" />
        <el-table-column prop="styleValue" label="款式" width="80" />
        <el-table-column prop="price" label="采购价" width="80" />
        <el-table-column prop="quantity" label="采购数量" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPurchaseOrder } from '@/api/purchase/purchaseOrder'
import { getPurchaseOrderShip } from '@/api/purchase/purchaseOrderShip'
import { listAllSupplier } from '@/api/goods/supplier'
import { parseTime } from '@/utils/zhijian'
import ImagePreview from '@/components/ImagePreview/index.vue'

const route = useRoute()

const form = reactive<Record<string, any>>({
  id: null,
  orderNum: null,
  createBy: null,
  orderAmount: null,
  supplierId: null,
  contactId: null,
  auditUser: null,
  auditTime: null,
  supplierDeliveryTime: null,
  createTime: null,
  status: null,
  receivedTime: null,
  stockInTime: null,
  remark: null,
  auditTimeText: null,
  receivedTimeText: null,
  stockInTimeText: null
})

const shipData = reactive<Record<string, any>>({
  id: null,
  orderId: null,
  shipCompany: null,
  shipNum: null,
  freight: null,
  shipTime: null,
  status: null,
  receiptTime: null,
  stockInTime: null,
  warehouseName: null,
  shipTimeText: null,
  receiptTimeText: null,
  stockInTimeText: null
})

const itemList = ref<any[]>([])
const supplierList = ref<any[]>([])
const supplierName = ref('')

const statusText = computed(() => {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已审核',
    101: '供应商已确认',
    102: '供应商已发货',
    2: '已收货',
    3: '已入库',
    99: '已取消'
  }
  return map[form.status] || '未知状态'
})

const statusType = computed(() => {
  if (form.status === 3) return 'success'
  if (form.status === 99) return 'danger'
  if (form.status === 102) return 'warning'
  return 'info'
})

const shipStatusText = computed(() => {
  const map: Record<number, string> = {
    0: '待收货',
    1: '已收货',
    2: '已入库'
  }
  return map[shipData.status] ?? '未知'
})

const shipStatusType = computed(() => {
  if (shipData.status === 2) return 'success'
  if (shipData.status === 1) return 'warning'
  return 'info'
})

function formatTime(val: any) {
  if (!val) return ''
  if (typeof val === 'number') {
    return parseTime(val * 1000) || ''
  }
  return parseTime(val) || ''
}

function getDetail() {
  const id = route.query.id as string
  if (!id) {
    ElMessage.error('缺少采购单ID参数')
    return
  }

  getPurchaseOrder(id).then((response: any) => {
    if (response.data) {
      Object.assign(form, response.data)
      form.auditTimeText = formatTime(response.data.auditTime)
      form.receivedTimeText = formatTime(response.data.receivedTime)
      form.stockInTimeText = formatTime(response.data.stockInTime)
      form.createTime = formatTime(response.data.createTime)
      itemList.value = response.data.itemList || []

      const supplierId = form.supplierId || form.contactId
      if (supplierId) {
        const supplier = supplierList.value.find((s: any) => s.id === supplierId)
        supplierName.value = supplier ? supplier.name : ''
      }

      if (form.status !== 99) {
        getPurchaseOrderShip(id).then((response: any) => {
          if (response.data) {
            Object.assign(shipData, response.data)
            shipData.shipTimeText = formatTime(response.data.shipTime)
            shipData.receiptTimeText = formatTime(response.data.receiptTime)
            shipData.stockInTimeText = formatTime(response.data.stockInTime)
          }
        })
      }
    }
  })
}

onMounted(() => {
  listAllSupplier({}).then((response: any) => {
    supplierList.value = response.rows || []
    getDetail()
  })
})
</script>

<style scoped>
</style>
