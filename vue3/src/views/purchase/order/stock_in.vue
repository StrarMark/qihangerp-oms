<template>
  <div class="app-container">
    <el-card class="form-card">
      <template #header>
        <span>采购单信息</span>
      </template>
      <el-form ref="formRef" :model="form" size="small" :inline="true" label-width="128px">
        <el-col :span="24">
          <el-form-item label="采购单号">
            <el-input v-model="form.orderNum" disabled style="width: 220px" />
          </el-form-item>
          <el-form-item label="供应商">
            <el-input v-model="supplierName" disabled style="width: 220px" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="采购金额">
            <el-input v-model="form.orderAmount" disabled style="width: 220px" />
          </el-form-item>
          <el-form-item label="采购日期">
            <el-input v-model="form.orderDate" disabled style="width: 220px" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="发货物流公司">
            <el-input v-model="ship.shipCompany" disabled style="width: 220px" />
          </el-form-item>
          <el-form-item label="发货物流单号">
            <el-input v-model="ship.shipNum" disabled style="width: 220px" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="发货日期">
            <el-input v-model="form.supplierDeliveryTime" disabled style="width: 220px" />
          </el-form-item>
          <el-form-item label="物流状态">
            <el-tag :type="shipStatusType">{{ shipStatusText }}</el-tag>
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
        <el-table-column prop="specNum" label="SKU编码" width="120" />
        <el-table-column prop="skuCode" label="SKU编码" width="120" />
        <el-table-column prop="colorValue" label="规格1" width="80" />
        <el-table-column prop="sizeValue" label="规格2" width="80" />
        <el-table-column prop="styleValue" label="规格3" width="80" />
        <el-table-column prop="price" label="单价" width="80" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="amount" label="总金额" width="100" />
        <el-table-column label="库存模式" width="100">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.inventoryMode === 1 ? 'success' : 'info'">
              {{ scope.row.inventoryMode === 1 ? '一物一码' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="stockin-card" style="margin-top: 20px;" v-if="canCreateStockIn">
      <template #header>
        <span>生成入库单</span>
      </template>
      <el-form ref="stockInFormRef" :model="stockInForm" size="small" :rules="stockInRules" :inline="true" label-width="128px">
        <el-row>
          <el-form-item label="收货日期" prop="receiptTime">
            <el-date-picker v-model="stockInForm.receiptTime" clearable type="date" value-format="yyyy-MM-dd" placeholder="请选择收货日期" style="width: 220px" />
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="入库仓库" prop="warehouseId">
            <el-select v-model="stockInForm.warehouseId" filterable placeholder="请选择入库仓库" style="width: 220px">
              <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id">
                <span style="float: left">{{ item.warehouseName }}</span>
                <span v-if="item.warehouseType=='LOCAL'" style="float: right; color: #8492a6; font-size: 13px">本地仓</span>
                <span v-else-if="item.warehouseType=='JDYC'" style="float: right; color: #8492a6; font-size: 13px">京东云仓</span>
                <span v-else-if="item.warehouseType=='JKYYC'" style="float: right; color: #8492a6; font-size: 13px">吉客云云仓</span>
                <span v-else-if="item.warehouseType=='CLOUD'" style="float: right; color: #8492a6; font-size: 13px">系统云仓</span>
                <span v-else style="float: right; color: #8492a6; font-size: 13px">其他</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="备注">
            <el-input v-model="stockInForm.remark" type="textarea" style="width: 220px" />
          </el-form-item>
        </el-row>
      </el-form>
    </el-card>

    <el-card v-else style="margin-top: 20px;">
      <el-empty>
        <template #description>
          <span v-if="ship.status === 0">待收货，请先确认收货后再入库</span>
          <span v-else-if="ship.status === 1">已收货，可以入库</span>
          <span v-else-if="ship.status === 2">已入库</span>
          <span v-else>物流状态异常</span>
        </template>
      </el-empty>
    </el-card>

    <div class="submit-bar" v-if="canCreateStockIn">
      <el-button type="primary" style="margin-left: 128px;" :loading="submitting" @click="submitForm">生成采购入库单</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getPurchaseOrder } from '@/api/purchase/purchaseOrder'
import { getPurchaseOrderShip, createStockInEntry } from '@/api/purchase/purchaseOrderShip'
import { listAllSupplier } from '@/api/goods/supplier'
import { myAvailableList } from '@/api/wms/warehouse'
import ImagePreview from '@/components/ImagePreview/index.vue'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const stockInFormRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive<Record<string, any>>({
  id: null,
  orderNum: null,
  supplierId: null,
  contactId: null,
  orderAmount: null,
  orderDate: null,
  supplierDeliveryTime: null,
  createBy: null,
  auditUser: null,
  status: null
})

const ship = reactive<Record<string, any>>({
  id: null,
  orderId: null,
  shipCompany: null,
  shipNum: null,
  orderSpecUnitTotal: null,
  status: null
})

const stockInForm = reactive<Record<string, any>>({
  receiptTime: null,
  warehouseId: null,
  remark: null
})

const itemList = ref<any[]>([])
const supplierList = ref<any[]>([])
const warehouseList = ref<any[]>([])
const supplierName = ref('')

const rules = reactive<Record<string, any>>({})

const stockInRules = reactive<Record<string, any>>({
  receiptTime: [{ required: true, trigger: 'blur', message: '请选择收货日期' }],
  warehouseId: [{ required: true, trigger: 'blur', message: '请选择入库仓库' }]
})

const canCreateStockIn = computed(() => {
  return ship.status === 0 || ship.status === 1
})

const shipStatusText = computed(() => {
  const map: Record<number, string> = {
    0: '待收货',
    1: '已收货',
    2: '已入库'
  }
  return map[ship.status] ?? '未知'
})

const shipStatusType = computed(() => {
  if (ship.status === 2) return 'success'
  if (ship.status === 1) return 'warning'
  return 'info'
})

function getDetail() {
  const id = route.query.id as string
  if (!id) {
    ElMessage.error('缺少ID参数')
    return
  }

  getPurchaseOrderShip(id).then((response: any) => {
    if (response.data) {
      Object.assign(ship, response.data)
      if (ship.orderId) {
        loadItemList(String(ship.orderId))
      }
    }
  }).catch(() => {
    ElMessage.error('物流记录不存在')
  })
}

function loadItemList(orderId: string) {
  getPurchaseOrder(orderId).then((res: any) => {
    if (res.data) {
      itemList.value = res.data.itemList || []
      form.orderNum = res.data.orderNum
      form.orderAmount = res.data.orderAmount
      form.orderDate = res.data.orderDate
      form.supplierDeliveryTime = res.data.supplierDeliveryTime

      const supplierId = res.data.supplierId || res.data.contactId
      if (supplierId) {
        const supplier = supplierList.value.find((s: any) => s.id === supplierId)
        supplierName.value = supplier ? supplier.name : ''
      }
    }
  })
}

function loadWarehouses() {
  myAvailableList().then((response: any) => {
    warehouseList.value = response.data || []
  })
}

function submitForm() {
  stockInFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      submitting.value = true
      const params = {
        id: ship.id,
        receiptTime: stockInForm.receiptTime,
        warehouseId: stockInForm.warehouseId,
        remark: stockInForm.remark,
        goodsList: itemList.value
      }
      createStockInEntry(params).then((res: any) => {
        if (res.code === 200) {
          ElMessage.success('入库单创建成功')
          router.push('/purchase/purchase_ship_list')
        } else {
          ElMessage.error(res.msg || '创建失败')
          submitting.value = false
        }
      }).catch(() => {
        submitting.value = false
      })
    }
  })
}

onMounted(() => {
  stockInForm.receiptTime = new Date().toISOString().slice(0, 10)
  listAllSupplier({}).then((response: any) => {
    supplierList.value = response.rows || []
    loadWarehouses()
    getDetail()
  })
})
</script>

<style scoped>
.submit-bar {
  margin-top: 20px;
  text-align: center;
}
</style>
