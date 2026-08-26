<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" size="default">
      <el-row :gutter="16">
        <!-- 左侧主内容 -->
        <el-col :span="17">
          <el-card shadow="never" class="mb16">
            <template #header><span class="card-title">基本信息</span></template>
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="订单号" prop="orderNum">
                  <el-input v-model="form.orderNum" placeholder="请输入订单号" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="店铺" prop="shopId">
                  <el-select v-model="form.shopId" placeholder="请选择店铺" filterable style="width: 100%">
                    <el-option v-for="item in shopList" :key="item.id" :label="item.name" :value="item.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="支付方式" prop="payMethod">
                  <el-select v-model="form.payMethod" placeholder="请选择" style="width: 100%">
                    <el-option label="微信" value="WEIXIN" />
                    <el-option label="支付宝" value="ALIPAY" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <el-card shadow="never" class="mb16">
            <template #header>
              <div class="card-header-flex">
                <span class="card-title">商品信息</span>
                <el-button size="small" type="primary" plain :disabled="!form.shopId" @click="openSkuDialog">
                  <el-icon><Plus /></el-icon>添加商品
                </el-button>
              </div>
            </template>
            <div v-if="!form.shopId" class="tip-text">请先在「基本信息」中选择店铺，再添加商品</div>
            <el-table :data="form.itemList" border size="small">
              <el-table-column type="index" label="#" width="45" align="center" />
              <el-table-column label="图片" width="70" align="center">
                <template #default="scope">
                  <ImagePreview :src="scope.row.img" :width="40" :height="40" />
                </template>
              </el-table-column>
              <el-table-column label="商品标题" min-width="180" show-overflow-tooltip>
                <template #default="scope">{{ scope.row.productTitle }}</template>
              </el-table-column>
              <el-table-column label="规格" min-width="110" show-overflow-tooltip>
                <template #default="scope">{{ scope.row.skuName || '无' }}</template>
              </el-table-column>
              <el-table-column prop="skuCode" label="编码" width="130" show-overflow-tooltip />
              <el-table-column label="单价(元)" width="140" align="center">
                <template #default="scope">
                  <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" :step="1" size="small" controls-position="right" style="width: 110px" />
                </template>
              </el-table-column>
              <el-table-column label="数量" width="130" align="center">
                <template #default="scope">
                  <el-input-number v-model="scope.row.quantity" :min="1" :step="1" size="small" controls-position="right" style="width: 100px" />
                </template>
              </el-table-column>
              <el-table-column label="小计(元)" width="90" align="center">
                <template #default="scope">{{ itemAmount(scope.row).toFixed(2) }}</template>
              </el-table-column>
              <el-table-column label="赠品" width="70" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.isGift" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60" align="center">
                <template #default="scope">
                  <el-button size="small" type="danger" link @click="removeItem(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <el-card shadow="never" class="mb16">
            <template #header><span class="card-title">收货信息</span></template>
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="收件人" prop="receiverName">
                  <el-input v-model="form.receiverName" placeholder="收件人姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="手机号" prop="receiverPhone">
                  <el-input v-model="form.receiverPhone" placeholder="收件人手机号" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="省市区" prop="provinces">
                  <el-cascader v-model="form.provinces" :options="pcaTextArr" placeholder="请选择省市区" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="详细地址" prop="address">
                  <el-input v-model="form.address" placeholder="详细地址（不含省市区）" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <el-card shadow="never" class="mb16">
            <template #header><span class="card-title">备注信息</span></template>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="买家留言" prop="buyerMemo">
                  <el-input v-model="form.buyerMemo" type="textarea" :rows="2" placeholder="买家留言（选填）" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商家备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="商家备注（选填）" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-col>

        <!-- 右侧金额汇总 -->
        <el-col :span="7">
          <el-card shadow="never" class="summary-card">
            <template #header><span class="card-title">金额汇总</span></template>
            <div class="summary-row">
              <span>商品金额</span>
              <span class="amount-text">¥{{ money(goodsAmount) }}</span>
            </div>
            <div class="summary-row">
              <span>运费</span>
              <el-input-number v-model="form.postage" :min="0" :precision="2" controls-position="right" size="small" style="width: 120px" />
            </div>
            <div class="summary-row">
              <span>手动优惠</span>
              <el-input-number v-model="form.changePrice" :min="0" :precision="2" controls-position="right" size="small" style="width: 120px" />
            </div>
            <div class="summary-row">
              <span>折扣金额</span>
              <el-input-number v-model="form.discountAmount" :min="0" :precision="2" controls-position="right" size="small" style="width: 120px" />
            </div>
            <el-divider style="margin: 14px 0" />
            <div class="summary-row">
              <span>共 {{ totalQuantity }} 件商品</span>
              <span>已优惠 <span class="discount-text">¥{{ money(totalDiscount) }}</span></span>
            </div>
            <div class="payable-box">
              <span>应付金额</span>
              <span class="payable-amount">¥{{ money(payable) }}</span>
            </div>
            <el-button type="primary" style="width: 100%" :loading="submitLoading" @click="submitForm">提交订单</el-button>
            <el-button style="width: 100%; margin: 10px 0 0" @click="router.back()">取消</el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-form>

    <!-- 商品SKU选择弹窗 -->
    <el-dialog title="选择店铺商品" v-model="skuOpen" width="900px" append-to-body>
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="skuQuery.keyword" placeholder="商品标题" clearable @keyup.enter="searchSku" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="searchSku"><el-icon><Search /></el-icon>搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="skuLoading" :data="skuList" border height="400" size="small">
        <el-table-column label="图片" width="70" align="center">
          <template #default="scope">
            <el-image v-if="scope.row.img" :src="scope.row.img" style="width: 40px; height: 40px;" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column label="商品标题" prop="productTitle" min-width="200" show-overflow-tooltip />
        <el-table-column label="规格" prop="skuName" min-width="100" show-overflow-tooltip />
        <el-table-column label="编码" prop="skuCode" width="130" show-overflow-tooltip />
        <el-table-column label="库存" prop="stockNum" width="70" align="center" />
        <el-table-column label="单价(元)" width="90" align="center">
          <template #default="scope">{{ ((scope.row.price || 0) / 100).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="addItem(scope.row)">添加</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="skuTotal > 0" :total="skuTotal" v-model:page="skuQuery.pageNum" v-model:limit="skuQuery.pageSize" @pagination="loadSkuList" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { listShop } from '@/api/shop/shop'
import { listGoodsSku } from '@/api/shop/goods'
import { createShopOrder } from '@/api/shop/order'
import { pcaTextArr } from '@/utils/chinaAreaData'
import Pagination from '@/components/Pagination/index.vue'
import ImagePreview from '@/components/ImagePreview/index.vue'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const shopList = ref<any[]>([])

// 生成默认订单号：SO + 年月日时分秒 + 3位随机数
function genOrderNum(): string {
  const d = new Date()
  const p = (n: number, l = 2) => String(n).padStart(l, '0')
  const time = `${d.getFullYear()}${p(d.getMonth() + 1)}${p(d.getDate())}${p(d.getHours())}${p(d.getMinutes())}${p(d.getSeconds())}`
  return 'SO' + time + p(Math.floor(Math.random() * 1000), 3)
}

const form = reactive<Record<string, any>>({
  orderNum: genOrderNum(),
  shopId: null,
  payMethod: 'WEIXIN',
  postage: 0,
  changePrice: 0,
  discountAmount: 0,
  itemList: [],
  receiverName: '',
  receiverPhone: '',
  provinces: [],
  address: '',
  buyerMemo: '',
  remark: ''
})

const rules = {
  orderNum: [{ required: true, message: '订单号不能为空', trigger: 'blur' }],
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  payMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
  receiverName: [{ required: true, message: '收件人不能为空', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  provinces: [{ required: true, message: '请选择省市区', trigger: 'change' }],
  address: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }]
}

// 明细行小计（元）
function itemAmount(row: any): number {
  return Math.round((row.unitPrice || 0) * (row.quantity || 0) * 100) / 100
}

// 商品金额 = 明细小计之和
const goodsAmount = computed(() => {
  return Math.round(form.itemList.reduce((sum: number, row: any) => sum + itemAmount(row) * 100, 0)) / 100
})

const totalQuantity = computed(() => {
  return form.itemList.reduce((sum: number, row: any) => sum + (row.quantity || 0), 0)
})

const totalDiscount = computed(() => {
  return Math.round(((form.changePrice || 0) + (form.discountAmount || 0)) * 100) / 100
})

// 应付金额 = 商品金额 + 运费 - 优惠
const payable = computed(() => {
  return Math.round((goodsAmount.value + (form.postage || 0) - totalDiscount.value) * 100) / 100
})

function money(v: number): string {
  return (v || 0).toFixed(2)
}

function loadShops() {
  const params: Record<string, any> = {}
  if (route.query.shopType) params.type = Number(route.query.shopType)
  listShop(params).then((res: any) => {
    shopList.value = res.rows || []
    if (route.query.shopId && shopList.value.some((s: any) => s.id === Number(route.query.shopId))) {
      form.shopId = Number(route.query.shopId)
    }
  })
}

// SKU 选择弹窗
const skuOpen = ref(false)
const skuLoading = ref(false)
const skuList = ref<any[]>([])
const skuTotal = ref(0)
const skuQuery = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

function openSkuDialog() {
  if (!form.shopId) return
  skuOpen.value = true
  searchSku()
}

function searchSku() {
  skuQuery.pageNum = 1
  loadSkuList()
}

function loadSkuList() {
  skuLoading.value = true
  listGoodsSku({
    pageNum: skuQuery.pageNum,
    pageSize: skuQuery.pageSize,
    shopId: form.shopId,
    productTitle: skuQuery.keyword || undefined
  }).then((res: any) => {
    skuList.value = res.rows || []
    skuTotal.value = res.total || 0
    skuLoading.value = false
  }).catch(() => {
    skuLoading.value = false
  })
}

function addItem(row: any) {
  if (form.itemList.some((x: any) => x.id === row.id)) {
    ElMessage.warning('该商品已添加')
    return
  }
  form.itemList.push({
    id: row.id,
    productTitle: row.productTitle,
    skuName: row.skuName,
    skuCode: row.skuCode,
    barcode: row.barcode,
    img: row.img,
    unitPrice: Math.round(row.price || 0) / 100,
    quantity: 1,
    isGift: false
  })
}

function removeItem(index: number) {
  form.itemList.splice(index, 1)
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    if (!form.itemList || form.itemList.length === 0) {
      ElMessage.warning('请至少添加一个商品')
      return
    }
    if (payable.value < 0) {
      ElMessage.warning('优惠金额不能大于商品金额与运费之和')
      return
    }
    submitLoading.value = true
    createShopOrder({
      orderNum: form.orderNum,
      shopId: form.shopId,
      payMethod: form.payMethod,
      // 后端以元为单位接收，内部转换为分
      goodsAmount: goodsAmount.value,
      postage: form.postage || 0,
      changePrice: form.changePrice || null,
      discountAmount: form.discountAmount || null,
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      province: form.provinces[0] || '',
      city: form.provinces[1] || '',
      town: form.provinces[2] || '',
      address: form.address,
      buyerMemo: form.buyerMemo,
      remark: form.remark,
      itemList: form.itemList.map((x: any) => ({
        id: String(x.id),
        barcode: x.barcode,
        quantity: x.quantity,
        itemAmount: itemAmount(x),
        isGift: x.isGift ? 1 : 0
      }))
    }).then(() => {
      ElMessage.success('订单创建成功')
      router.push('/sale/shop_order_list')
    }).catch(() => {
      submitLoading.value = false
    })
  })
}

onMounted(() => {
  loadShops()
})
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}
.card-title {
  font-weight: 600;
}
.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tip-text {
  color: #909399;
  font-size: 13px;
  margin-bottom: 10px;
}
.summary-card .summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  font-size: 14px;
  color: #606266;
}
.amount-text {
  font-weight: 600;
}
.discount-text {
  color: #e6a23c;
}
.payable-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fef0f0;
  border-radius: 4px;
  padding: 10px 12px;
  margin-bottom: 16px;
}
.payable-amount {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 700;
}
</style>
