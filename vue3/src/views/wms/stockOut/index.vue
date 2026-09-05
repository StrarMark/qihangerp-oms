<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryFormRef" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="出库单号" prop="outNum">
        <el-input v-model="queryParams.outNum" placeholder="请输入出库单号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="源单号" prop="sourceNum">
        <el-input v-model="queryParams.sourceNum" placeholder="请输入源单号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable @change="handleQuery">
          <el-option v-for="item in warehouseList" :key="item.id" :label="item.warehouseName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="出库类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="出库类型" clearable @change="handleQuery">
          <el-option label="订单发货出库" value="1" />
          <el-option label="采购退货出库" value="2" />
          <el-option label="盘亏出库" value="3" />
          <el-option label="报损出库" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建日期" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择创建日期" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd">
          <el-icon><Plus /></el-icon>新建商品出库单
        </el-button>
      </el-col>
      <RightToolbar :showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="stockOutEntryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="出库单号" align="center" prop="outNum" width="180" />
      <el-table-column label="源单号" align="center" prop="sourceNum" width="180" />
      <el-table-column label="出库类型" align="center" prop="type" width="130">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 1" size="small">订单发货出库</el-tag>
          <el-tag v-else-if="scope.row.type === 2" size="small">采购退货出库</el-tag>
          <el-tag v-else-if="scope.row.type === 3" size="small">盘亏出库</el-tag>
          <el-tag v-else-if="scope.row.type === 4" size="small">报损出库</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="出库仓库" align="center" prop="warehouseName" width="200">
        <template #default="scope"><el-tag type="info" v-if="scope.row.warehouseName">{{ scope.row.warehouseName }}</el-tag></template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" size="small">待出库</el-tag>
          <el-tag v-else-if="scope.row.status === 1" size="small" type="warning">部分出库</el-tag>
          <el-tag v-else-if="scope.row.status === 2" size="small" type="success">全部出库</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建日期" align="center" prop="createTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="completeTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.completeTime) }}</template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="operatorName" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="商品数" align="center" prop="goodsUnit" width="70" />
      <el-table-column label="规格数" align="center" prop="specUnit" width="70" />
      <el-table-column label="总件数" align="center" prop="specUnitTotal" width="70" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template #default="scope">
          <el-button v-if="scope.row.status !== 2" type="primary" plain size="small" @click="handleStockOut(scope.row)">出库</el-button>
          <el-button v-else type="primary" link size="small" @click="handleStockOut(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 出库操作对话框 -->
    <el-dialog :title="title" v-model="open" width="1200px" append-to-body :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-descriptions title="出库单详情" :column="3">
          <el-descriptions-item label="单号">{{ form.outNum }}</el-descriptions-item>
          <el-descriptions-item label="来源">
            <el-tag v-if="form.type === 1" size="small">订单发货出库</el-tag>
            <el-tag v-else-if="form.type === 2" size="small">采购退货出库</el-tag>
            <el-tag v-else-if="form.type === 3" size="small">盘亏出库</el-tag>
            <el-tag v-else-if="form.type === 4" size="small">报损出库</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="仓库">{{ form.warehouseName }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ form.remark }}</el-descriptions-item>
          <el-descriptions-item label="商品数">{{ form.goodsUnit }}</el-descriptions-item>
          <el-descriptions-item label="规格数">{{ form.specUnit }}</el-descriptions-item>
          <el-descriptions-item label="总件数">{{ form.specUnitTotal }}</el-descriptions-item>
        </el-descriptions>
        <el-divider content-position="center">出库商品明细</el-divider>
        <el-table :data="form.itemList || []">
          <el-table-column label="序号" align="center" type="index" width="50" />
          <el-table-column label="商品名" prop="goodsName" min-width="120" show-overflow-tooltip />
          <el-table-column label="规格编码" prop="skuCode" width="120" />
          <el-table-column label="规格" prop="skuName" width="100" />
          <el-table-column label="应出" prop="originalQuantity" width="60" align="center" />
          <el-table-column label="已出" prop="outQuantity" width="60" align="center" />
          <el-table-column label="批次策略" width="130" align="center">
            <template #default="scope">
              <el-select v-if="scope.row.status < 2" v-model="scope.row.batchStrategy" size="small" placeholder="批次策略">
                <el-option label="自动分配(FIFO)" value="auto" />
                <el-option label="手动选批次" value="manual" />
              </el-select>
              <span v-else style="color:#909399">-</span>
            </template>
          </el-table-column>
          <el-table-column label="已选批次" width="160">
            <template #default="scope">
              <el-tag v-if="scope.row.selectedBatch" size="small" closable
                @close="scope.row.selectedBatch = null">
                {{ scope.row.selectedBatch.batchNum }}
              </el-tag>
              <el-tag v-else-if="scope.row.batchNum" size="small" type="info">
                {{ scope.row.batchNum }}
              </el-tag>
              <span v-else-if="scope.row.status >= 2" style="color:#909399">-</span>
              <span v-else-if="scope.row.batchStrategy === 'auto'" style="color:#909399">自动</span>
              <el-button v-else type="primary" link size="small" @click="openBatchDialog(scope.row, scope.$index)">
                选择批次
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="本次出库" width="100" align="center">
            <template #default="scope">
              <el-input-number v-if="scope.row.status < 2" v-model="scope.row.outQty"
                :min="0" :max="(scope.row.originalQuantity||0)-(scope.row.outQuantity||0)"
                size="small" controls-position="right" style="width:90px" />
              <span v-else>{{ scope.row.outQuantity }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="scope">
              <el-button v-if="scope.row.status < 2" size="small" type="danger" plain
                @click="stockOutSubmit(scope.row)" :loading="scope.row._loading"
                :disabled="(scope.row.outQty||0) <= 0 || (scope.row.batchStrategy==='manual' && !scope.row.selectedBatch)">
                出库
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </el-dialog>

    <!-- 批次选择弹窗 -->
    <el-dialog title="选择出库批次" v-model="batchDialogVisible" width="750px" append-to-body :close-on-click-modal="false">
      <div style="margin-bottom:10px">
        <el-tag size="small">{{ batchDialogItem?.goodsName }} {{ batchDialogItem?.skuName }}</el-tag>
        <el-tag size="small" type="info" style="margin-left:5px">
          可出: {{ ((batchDialogItem?.originalQuantity||0) - (batchDialogItem?.outQuantity||0)) }}
        </el-tag>
      </div>
      <el-table :data="batchList" border stripe highlight-current-row
        @current-change="onBatchRowClick">
        <el-table-column label="批次号" prop="batchNum" width="160" />
        <el-table-column label="SKU编码" prop="skuCode" width="120" />
        <el-table-column label="采购单价" prop="purPrice" width="100" align="right">
          <template #default="scope">{{ amountFormatter(scope.row.purPrice) }}</template>
        </el-table-column>
        <el-table-column label="批次库存" prop="currentQty" width="80" align="center" />
        <el-table-column label="仓位" prop="positionNum" width="100" />
        <el-table-column label="入库时间" prop="createTime" width="160">
          <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div style="margin-top:10px">
        <span>出库数量：</span>
        <el-input-number v-model="batchDialogQty" :min="1"
          :max="(batchDialogItem?.originalQuantity||0) - (batchDialogItem?.outQuantity||0)"
          size="small" controls-position="right" style="width:120px" />
      </div>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchSelect" :disabled="!selectedBatchRow || batchDialogQty <= 0">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { listStockOut, getStockOutEntry, stockOut, getInventoryBatches } from '@/api/wms/stockOut'
import { listWarehouse } from '@/api/wms/warehouse'
import { getUserProfile } from '@/api/system/user'
import { parseTime, amountFormatter } from '@/utils/zhijian'
import Pagination from '@/components/Pagination/index.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'

const router = useRouter()
const loading=ref(true);const showSearch=ref(true);const total=ref(0)
const stockOutEntryList=ref<any[]>([]);const warehouseList=ref<any[]>([]);const ids:any[]=[]
const open=ref(false);const title=ref('')

const queryParams=reactive({pageNum:1,pageSize:10,outNum:null as string|null,sourceNum:null as string|null,warehouseId:null as number|null,type:null as string|null,createTime:null as string|null})
const form=reactive<Record<string,any>>({})

function getList(){loading.value=true;listStockOut(queryParams).then((res:any)=>{stockOutEntryList.value=res.rows||[];total.value=res.total||0;loading.value=false}).catch(()=>{loading.value=false})}
function handleQuery(){queryParams.pageNum=1;getList()}
function resetQuery(){queryParams.outNum=null;queryParams.sourceNum=null;queryParams.warehouseId=null;queryParams.type=null;queryParams.createTime=null;handleQuery()}
function handleSelectionChange(selection:any[]){ids.length=0;ids.push(...selection.map((item:any)=>item.id))}
function handleAdd(){router.push({path:'/wms/stock_out/create'})}
function handleStockOut(row:any){
  getStockOutEntry(row.id).then((res:any)=>{
    Object.assign(form,res.data||{})
    if(form.itemList){
      form.itemList.forEach((item:any)=>{
        item.outQty=(item.originalQuantity||0)-(item.outQuantity||0)
        item.batchStrategy = item.batchId ? 'auto' : 'auto'
        item.selectedBatch = null
        item._loading = false
      })
    }
    open.value=true;title.value='出库操作'
  })
}

// 批次选择弹窗
const batchDialogVisible=ref(false)
const batchDialogItem=ref<any>(null)
const batchDialogItemIndex=ref(-1)
const batchList=ref<any[]>([])
const selectedBatchRow=ref<any>(null)
const batchDialogQty=ref(1)

function openBatchDialog(row:any, index:number){
  if(!form.warehouseId){ElMessage.warning('出库单未关联仓库');return}
  batchDialogItem.value=row
  batchDialogItemIndex.value=index
  selectedBatchRow.value=null
  batchDialogQty.value=(row.originalQuantity||0)-(row.outQuantity||0)
  batchDialogVisible.value=true
  getInventoryBatches(row.skuId, form.warehouseId).then((res:any)=>{
    batchList.value=(res.data||[]).filter((b:any)=>b.currentQty>0)
  })
}
function onBatchRowClick(row:any){selectedBatchRow.value=row}
function confirmBatchSelect(){
  if(!selectedBatchRow.value||batchDialogQty.value<=0)return
  const item=form.itemList[batchDialogItemIndex.value]
  if(item){
    item.selectedBatch={...selectedBatchRow.value}
    item.outQty=batchDialogQty.value
  }
  batchDialogVisible.value=false
}

function stockOutSubmit(row:any){
  if((row.outQty||0)<=0){ElMessage.warning('请填写出库数量');return}
  if(row.batchStrategy==='manual'&&!row.selectedBatch){ElMessage.warning('请先选择批次');return}
  row._loading=true
  stockOut({
    entryItemId:row.id,
    entryId:row.entryId||form.id,
    skuId:row.skuId,
    originalQuantity:row.originalQuantity,
    outQuantity:row.outQuantity,
    outQty:row.outQty,
    batchId:row.selectedBatch?.id||null
  }).then((res:any)=>{
    row._loading=false
    if(res.code===200||res.code===0){
      ElMessage.success('出库成功')
      getList()
      handleStockOut({id:row.entryId||form.id})
    }else ElMessage.error(res.msg||'出库失败')
  }).catch(()=>{row._loading=false})
}

onMounted(()=>{
  getUserProfile().then((res:any)=>{
    const user=res.data||res.user
    if(user?.userType===0){listWarehouse({pageSize:50,warehouseType:'LOCAL'}).then((res:any)=>{warehouseList.value=res.rows||[];getList()})}
    else{
      listWarehouse({pageSize:50,warehouseType:'LOCAL'}).then((res:any)=>{warehouseList.value=res.rows||[];getList()})
    }
  })
})
</script>
