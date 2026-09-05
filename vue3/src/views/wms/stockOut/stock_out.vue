<template>
  <div class="outbound-work">
    <el-card class="work-card" shadow="never">
      <template #header>
        <span>出库作业 - {{ outboundOrder.outNum || outboundOrder.id }}</span>
        <el-button size="small" style="float:right" @click="goBack">
          <el-icon><Back /></el-icon>返回
        </el-button>
      </template>

      <el-form :model="outboundOrder" label-width="100px" size="small" disabled inline>
        <el-form-item label="源单号">{{ outboundOrder.sourceNo }}</el-form-item>
        <el-form-item label="仓库">{{ outboundOrder.warehouseName }}</el-form-item>
        <el-form-item label="出库类型">
          <el-tag v-if="outboundOrder.type === 1" size="small">订单发货出库</el-tag>
          <el-tag v-else-if="outboundOrder.type === 2" size="small">采购退货出库</el-tag>
          <el-tag v-else-if="outboundOrder.type === 3" size="small">盘亏出库</el-tag>
          <el-tag v-else-if="outboundOrder.type === 4" size="small">报损出库</el-tag>
        </el-form-item>
        <el-form-item label="创建时间">{{ parseTime(outboundOrder.createTime) }}</el-form-item>
      </el-form>

      <el-table :data="outboundItems" border stripe style="margin-top:20px">
        <el-table-column prop="skuCode" label="SKU编码" width="120" />
        <el-table-column prop="goodsName" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="skuName" label="规格" width="120" show-overflow-tooltip />
        <el-table-column prop="originalQuantity" label="应出数量" width="80" align="center" />
        <el-table-column prop="outQuantity" label="已出数量" width="80" align="center" />
        <el-table-column label="本次出库" width="320" align="center">
          <template #default="scope">
            <div v-if="scope.row.remainingQuantity === 0" style="color:#909399">已完成</div>
            <div v-else>
              <el-select v-model="scope.row.batchStrategy" size="small" placeholder="批次策略" style="width:120px"
                :disabled="scope.row.remainingQuantity === 0">
                <el-option label="自动分配(FIFO)" value="auto" />
                <el-option label="手动选批次" value="manual" />
              </el-select>
              <el-input-number
                v-model="scope.row.thisQuantity"
                :min="0"
                :max="scope.row.remainingQuantity"
                size="small"
                controls-position="right"
                style="margin-left:8px;width:100px"
                :disabled="scope.row.remainingQuantity === 0"
              />
              <el-button v-if="scope.row.batchStrategy === 'manual'" type="primary" link size="small"
                @click="openBatchSelection(scope.row, scope.$index)" style="margin-left:5px"
                :disabled="scope.row.remainingQuantity === 0">
                选批次
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="已选批次" min-width="180">
          <template #default="scope">
            <el-tag v-if="scope.row.selectedBatch" size="small" closable @close="clearBatch(scope.row)">
              {{ scope.row.selectedBatch.batchNum }} (余{{ scope.row.selectedBatch.currentQty }})
            </el-tag>
            <span v-else-if="scope.row.batchStrategy === 'auto'" style="color:#909399">系统自动分配</span>
            <span v-else style="color:#E6A23C">未选批次</span>
          </template>
        </el-table-column>
        <el-table-column prop="remainingQuantity" label="剩余可出" width="80" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.remainingQuantity === 0 }">{{ scope.row.remainingQuantity }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="submit-bar" style="margin-top:20px;text-align:center">
        <el-button type="primary" size="large" @click="submitOutbound" :loading="submitting" :disabled="!canSubmit">
          确认出库
        </el-button>
      </div>
    </el-card>

    <!-- 批次选择弹窗 -->
    <el-dialog title="选择出库批次" v-model="batchDialogVisible" width="800px" append-to-body :close-on-click-modal="false">
      <div style="margin-bottom:10px">
        <el-tag size="small">{{ batchDialogItem?.goodsName }} {{ batchDialogItem?.skuName }}</el-tag>
        <el-tag size="small" type="info" style="margin-left:5px">需出库: {{ batchDialogItem?.remainingQuantity }}</el-tag>
      </div>
      <el-table :data="batchList" border stripe highlight-current-row
        @current-change="handleBatchRowClick" style="width:100%">
        <el-table-column label="批次号" prop="batchNum" width="160" />
        <el-table-column label="SKU编码" prop="skuCode" width="120" />
        <el-table-column label="采购单价" prop="purPrice" width="100" align="right">
          <template #default="scope">{{ amountFormatter(scope.row.purPrice) }}</template>
        </el-table-column>
        <el-table-column label="批次库存" prop="currentQty" width="80" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.currentQty <= 0 }">{{ scope.row.currentQty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="仓位" prop="positionNum" width="100" />
        <el-table-column label="入库时间" prop="createTime" width="160">
          <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="模式" width="90" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.inventoryMode === 1" size="small" type="warning">一物一码</el-tag>
            <el-tag v-else size="small">传统</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:10px">
        <span>出库数量：</span>
        <el-input-number v-model="batchSelectQty" :min="1"
          :max="batchDialogItem?.remainingQuantity || 9999"
          size="small" controls-position="right" style="width:120px" />
      </div>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchSelection" :disabled="!selectedBatchRow || batchSelectQty <= 0">
          确认选择
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import { getStockOutEntry, stockOut, getInventoryBatches } from '@/api/wms/stockOut'
import { parseTime, amountFormatter } from '@/utils/zhijian'

const route = useRoute()
const router = useRouter()

const outboundOrder = reactive<Record<string, any>>({})
const outboundItems = ref<any[]>([])
const submitting = ref(false)

const canSubmit = computed(() => {
  return outboundItems.value.some((item: any) => {
    if ((item.thisQuantity || 0) <= 0) return false
    if (item.batchStrategy === 'manual' && !item.selectedBatch) return false
    return true
  })
})

function loadData(id: number) {
  getStockOutEntry(id).then((res: any) => {
    const data = res.data || {}
    Object.assign(outboundOrder, data)
    outboundItems.value = (data.itemList || data.items || []).map((item: any) => ({
      ...item,
      thisQuantity: 0,
      remainingQuantity: (item.originalQuantity || 0) - (item.outQuantity || 0),
      batchStrategy: item.batchId ? 'auto' : 'auto',
      selectedBatch: null,
    }))
  })
}

// 批次选择弹窗相关
const batchDialogVisible = ref(false)
const batchDialogItem = ref<any>(null)
const batchDialogItemIndex = ref(-1)
const batchList = ref<any[]>([])
const selectedBatchRow = ref<any>(null)
const batchSelectQty = ref(1)

function openBatchSelection(row: any, index: number) {
  if (!outboundOrder.warehouseId) {
    ElMessage.warning('出库单未关联仓库')
    return
  }
  batchDialogItem.value = row
  batchDialogItemIndex.value = index
  selectedBatchRow.value = null
  batchSelectQty.value = row.remainingQuantity || 1
  batchDialogVisible.value = true

  getInventoryBatches(row.skuId, outboundOrder.warehouseId).then((res: any) => {
    batchList.value = (res.data || []).filter((b: any) => b.currentQty > 0)
  })
}

function handleBatchRowClick(row: any) {
  selectedBatchRow.value = row
}

function clearBatch(row: any) {
  row.selectedBatch = null
}

function confirmBatchSelection() {
  if (!selectedBatchRow.value || batchSelectQty.value <= 0) return
  if (batchSelectQty.value > selectedBatchRow.value.currentQty) {
    ElMessage.warning('出库数量不能超过批次库存')
    return
  }
  const item = outboundItems.value[batchDialogItemIndex.value]
  if (item) {
    item.selectedBatch = { ...selectedBatchRow.value }
    item.thisQuantity = batchSelectQty.value
  }
  batchDialogVisible.value = false
}

function submitOutbound() {
  const items = outboundItems.value
    .filter((item: any) => (item.thisQuantity || 0) > 0)
    .map((item: any) => ({
      entryItemId: item.id,
      entryId: item.entryId || outboundOrder.id,
      skuId: item.skuId,
      outQty: item.thisQuantity,
      originalQuantity: item.originalQuantity,
      outQuantity: item.outQuantity,
      batchId: item.selectedBatch?.id || null,
    }))

  if (items.length === 0) {
    ElMessage.warning('请填写出库数量')
    return
  }

  // 校验手动选批次模式是否都选了批次
  const manualItems = outboundItems.value.filter((item: any) =>
    (item.thisQuantity || 0) > 0 && item.batchStrategy === 'manual' && !item.selectedBatch
  )
  if (manualItems.length > 0) {
    ElMessage.warning('手动选批次模式下，请先选择批次')
    return
  }

  submitting.value = true
  // 逐条提交出库
  let completed = 0
  let failed = false
  items.forEach((item: any) => {
    stockOut(item).then((res: any) => {
      if (res.code === 200 || res.code === 0) {
        completed++
        if (completed === items.length) {
          ElMessage.success('出库成功')
          submitting.value = false
          loadData(outboundOrder.id)
        }
      } else {
        failed = true
        ElMessage.error(res.msg || '出库失败')
        submitting.value = false
      }
    }).catch(() => {
      failed = true
      submitting.value = false
    })
  })
}

function goBack() {
  router.back()
}

onMounted(() => {
  const id = route.query.id || route.params.id
  if (id) loadData(Number(id))
})
</script>

<style scoped>
.text-danger { color: #f56c6c; font-weight: bold; }
</style>
