<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="订单号" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable @change="handleQuery">
         <el-option
            v-for="item in shopList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
            <span style="float: left">{{ item.name }}</span>

          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间" prop="orderTime">
          <el-date-picker clearable
            v-model="orderTime" value-format="yyyy-MM-dd"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :disabled="single"
          icon="el-icon-download"
          size="mini"
          @click="handleShip"
        >手动发货</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type=""
          plain
          :disabled="single"
          icon="el-icon-download"
          size="mini"
          @click="allocateShipmentToSupplier"
        >分配给供应商发货</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="订单ID" align="center" prop="id" />-->
<!--      <el-table-column label="订单号" align="center" prop="orderNum" />-->
      <el-table-column label="订单号" align="left" prop="orderNum" width="200px">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >{{scope.row.orderNum}} </el-button>
          <i class="el-icon-copy-document tag-copy" :data-clipboard-text="scope.row.orderNum" @click="copyActiveCode($event,scope.row.orderNum)" ></i>
          <br/>
          <el-tag type="info">{{ shopList.find(x=>x.id === scope.row.shopId) ? shopList.find(x=>x.id === scope.row.shopId).name : '' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="商品明细" align="center" width="750px" >
        <template slot="header">
          <table>
            <th>
              <td width="50px">图片</td>
              <td width="250px" align="left">标题</td>
              <td width="200" align="left">规格</td>
              <td width="150" align="left">Sku编码</td>
              <td width="50" align="left">数量</td>
            </th>
          </table>
        </template>
        <template slot-scope="scope" >
          <el-table :data="scope.row.itemVoList" :show-header="false" :cell-style="{border:0 + 'px' }"  :row-style="{border:0 + 'px' }" >
            <el-table-column label="商品图片" width="50px">
              <template slot-scope="scope">
                <!--                <el-image  style="width: 40px; height: 40px;" :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>-->
                <image-preview :src="scope.row.goodsImg" :width="40" :height="40"/>
              </template>
            </el-table-column>
            <el-table-column label="商品名" align="left" width="250px" prop="goodsTitle" >
              <template slot-scope="scope">
                {{scope.row.goodsTitle}}
                <!--                <el-tag size="small" v-if="scope.row.refundStatus === 1">无售后或售后关闭</el-tag>-->
                <el-tag size="small" v-if="scope.row.refundStatus === 2">售后处理中</el-tag>
                <el-tag size="small" v-if="scope.row.refundStatus === 3">退款中</el-tag>
                <el-tag size="small" v-if="scope.row.refundStatus === 4">退款成功</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="SKU名" align="left" prop="goodsSpec" width="150"  :show-overflow-tooltip="true">
              <el-table-column label="规格" align="left" prop="goodsSpec" width="200">
                <template slot-scope="scope">
                  {{ scope.row.goodsSpec}}
                </template>
              </el-table-column>
            </el-table-column>
            <el-table-column label="Sku编码" align="left" prop="skuNum" width="150"/>

            <el-table-column label="商品数量" align="center" prop="quantity" width="50px">
              <template slot-scope="scope">
                <el-tag size="small" type="danger">{{scope.row.quantity}}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column label="收件信息" prop="receiverName" >
        <template slot-scope="scope">
          {{scope.row.receiverName}}&nbsp;
          {{scope.row.receiverMobile}} <br />
          {{scope.row.province}} {{scope.row.city}} {{scope.row.town}} <br />
          {{scope.row.address}}
        </template>
      </el-table-column>
      <el-table-column label="快递单号" prop="shippingNumber" >
      </el-table-column>
      <el-table-column label="下单时间" align="center" prop="orderTime" >
        <template slot-scope="scope">
          {{ parseTime(scope.row.orderTime) }}
        </template>
      </el-table-column>


    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 订单详情对话框 -->
    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="1100px" append-to-body>
      <el-tabs v-model="activeName" >
        <el-tab-pane label="订单详情" name="orderDetail">
          <el-form ref="form" :model="form" :rules="rules" label-width="80px" inline>
            <el-descriptions title="订单信息">
              <el-descriptions-item label="ID">{{form.id}}</el-descriptions-item>
              <el-descriptions-item label="订单号">{{form.orderNum}}</el-descriptions-item>

              <el-descriptions-item label="店铺">
                {{ shopList.find(x=>x.id === form.shopId)?shopList.find(x=>x.id === form.shopId).name:'' }}
              </el-descriptions-item>
              <el-descriptions-item label="买家留言">
                {{form.buyerMemo}}
              </el-descriptions-item>
              <el-descriptions-item label="卖家留言">
                {{form.sellerMemo}}
              </el-descriptions-item>

              <el-descriptions-item label="备注">
                {{form.remark}}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ parseTime(form.createTime) }}
                <!-- <el-date-picker
                disabled
                  v-model="form.orderCreateTime"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="请选择订单创建时间">
                </el-date-picker> -->
              </el-descriptions-item>
              <el-descriptions-item label="支付时间"> {{ form.payTime }}</el-descriptions-item>
              <el-descriptions-item label="最后更新时间"> {{ form.updateTime }}</el-descriptions-item>

              <el-descriptions-item label="订单状态">
                <el-tag v-if="form.orderStatus === 1" style="margin-bottom: 6px;">待发货</el-tag>
                <el-tag v-if="form.orderStatus === 2" style="margin-bottom: 6px;">已出库</el-tag>
                <el-tag v-if="form.orderStatus === 3" style="margin-bottom: 6px;">已发货</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="退款状态">
                <el-tag v-if="form.refundStatus === 1">无售后或售后关闭</el-tag>
                <el-tag v-if="form.refundStatus === 2">售后处理中</el-tag>
                <el-tag v-if="form.refundStatus === 3">退款中</el-tag>
                <el-tag v-if="form.refundStatus === 4">退款成功</el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <el-descriptions title="收货信息">
              <el-descriptions-item label="收件人姓名">{{form.receiverName}}</el-descriptions-item>
              <el-descriptions-item label="收件人手机号">{{form.receiverMobile}}</el-descriptions-item>
              <el-descriptions-item label="省市区">{{form.province}}{{form.city}}{{form.town}}</el-descriptions-item>
              <el-descriptions-item label="详细地址">{{form.address}}</el-descriptions-item>
            </el-descriptions>


          </el-form>

        </el-tab-pane>
        <el-tab-pane label="商品列表" name="orderItems" lazy>
          <el-table :data="form.itemVoList"  style="margin-bottom: 10px;">
            <!-- <el-table-column type="selection" width="50" align="center" /> -->
            <el-table-column label="序号" align="center" type="index" width="50"/>

            <el-table-column label="商品图片" prop="goodsImg" width="80">
              <template slot-scope="scope">
                <el-image style="width: 70px; height: 70px" :src="scope.row.goodsImg"></el-image>
              </template>
            </el-table-column>
            <el-table-column label="商品标题" prop="goodsTitle" ></el-table-column>
            <el-table-column label="SKU" prop="goodsSpec" width="150"></el-table-column>
            <el-table-column label="sku编码" prop="skuNum"></el-table-column>
            <el-table-column label="外部ERP Sku编码" prop="outerErpSkuId"></el-table-column>
            <el-table-column label="单价" prop="goodsPrice"></el-table-column>
            <el-table-column label="子订单金额" prop="itemAmount"></el-table-column>
            <el-table-column label="实付金额" prop="payment"></el-table-column>
            <el-table-column label="数量" prop="quantity"></el-table-column>
            <el-table-column label="状态" prop="orderStatus">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.orderStatus === 1">待发货</el-tag>
                <el-tag v-if="scope.row.orderStatus === 2">已发货</el-tag>
                <el-tag v-if="scope.row.orderStatus === 3">已完成</el-tag>
                <el-tag v-if="scope.row.orderStatus === 11">已取消</el-tag>
              </template>
            </el-table-column>
<!--            <el-table-column label="退款状态" prop="refundStatus">-->
<!--              <template slot-scope="scope">-->
<!--                &lt;!&ndash; 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 &ndash;&gt;-->
<!--                <el-tag v-if="scope.row.refundStatus === 1">无售后或售后关闭</el-tag>-->
<!--                <el-tag v-if="scope.row.refundStatus === 2">售后处理中</el-tag>-->
<!--                <el-tag v-if="scope.row.refundStatus === 3">退款中</el-tag>-->
<!--                <el-tag v-if="scope.row.refundStatus === 4">退款成功</el-tag>-->
<!--              </template>-->
<!--            </el-table-column>-->
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="优惠明细" name="orderCou" lazy>
          <el-table :data="form.discounts"  style="margin-bottom: 10px;">
            <el-table-column label="序号" align="center" type="index" width="50"/>
            <el-table-column label="优惠名称" prop="name" ></el-table-column>
            <el-table-column label="优惠金额" prop="discountAmount"></el-table-column>
            <el-table-column label="优惠描述" prop="description"></el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="物流单" name="orderLog" lazy>

        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 打包发货对话框 -->
    <el-dialog title="打包发货" :visible.sync="shipOpen" width="1100px" append-to-body>

      <el-form ref="form" :model="form" :rules="rules" label-width="80px" >
        <el-descriptions title="订单信息">
          <el-descriptions-item label="ID">{{form.id}}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{form.orderNum}}</el-descriptions-item>

          <el-descriptions-item label="店铺">
            {{ shopList.find(x=>x.id == form.shopId)?shopList.find(x=>x.id == form.shopId).name:'' }}
            <el-tag size="small" v-if="form.shopType === 100">淘宝天猫</el-tag>
            <el-tag size="small" v-if="form.shopType === 200">京东POP</el-tag>
            <el-tag size="small" v-if="form.shopType === 300">拼多多</el-tag>
            <el-tag size="small" v-if="form.shopType === 400">抖店</el-tag>
            <el-tag size="small" v-if="form.shopType === 500">微信小店</el-tag>
            <el-tag size="small" v-if="form.shopType === 999">线下渠道</el-tag>

          </el-descriptions-item>
          <el-descriptions-item label="买家留言">
            {{form.buyerMemo}}
          </el-descriptions-item>
          <el-descriptions-item label="备注">
            {{form.remark}}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">
            {{ parseTime(form.orderTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="收件人姓名">{{form.receiverName}}</el-descriptions-item>
          <el-descriptions-item label="收件人手机号">{{form.receiverMobile}}</el-descriptions-item>
          <el-descriptions-item label="省市区">{{form.province}}{{form.city}}{{form.town}}</el-descriptions-item>
          <el-descriptions-item label="详细地址">{{form.address}}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="center">商品明细</el-divider>
        <el-table :data="form.itemVoList"  style="margin-bottom: 10px;">
          <!-- <el-table-column type="selection" width="50" align="center" /> -->
          <el-table-column label="序号" align="center" type="index" width="50"/>

          <el-table-column label="商品图片" prop="goodsImg" width="80">
            <template slot-scope="scope">
              <el-image style="width: 70px; height: 70px" :src="scope.row.goodsImg"></el-image>
            </template>
          </el-table-column>
          <el-table-column label="商品标题" prop="goodsTitle" ></el-table-column>
          <el-table-column label="规格" prop="goodsSpec" width="350"></el-table-column>
          <el-table-column label="sku编码" prop="skuNum"></el-table-column>
          <el-table-column label="数量" prop="quantity"></el-table-column>
        </el-table>
        <el-form-item label="包裹尺寸" prop="height">
          <el-input type="number" v-model.number="form.length" placeholder="长mm" style="width:90px" /> x
          <el-input type="number"  v-model.number="form.width" placeholder="宽mm" style="width:90px" /> x
          <el-input type="number" v-model.number="form.height" placeholder="高mm" style="width:90px" />
        </el-form-item>
        <el-form-item label="包裹重量" prop="weight">
          <el-input type="number" v-model.number="form.weight" placeholder="请输入包裹重量（单位g）" style="width:300px" />
        </el-form-item>
        <el-row>
          <el-col>
            <el-form-item label="物流公司" prop="shippingCompany">
              <!--              <el-input v-model="form.shippingCompany" placeholder="请输入物流公司" style="width:300px" />-->
              <el-select v-model="form.shippingCompany" filterable r placeholder="选择快递公司" style="width:300px">
                <el-option v-for="item in logisticsList" :key="item.id" :label="item.name" :value="item.id">
                  <span style="float: left">{{ item.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px" >{{item.number}}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="物流单号" prop="shippingNumber">
              <el-input v-model="form.shippingNumber" placeholder="请输入物流单号" style="width:300px" />
            </el-form-item>
            <el-form-item label="物流费用" prop="shippingCost">
              <el-input v-model="form.shippingCost" placeholder="请输入物流费用" style="width:300px" />
            </el-form-item>
            <el-form-item label="发货人" prop="shippingMan">
              <el-input v-model="form.shippingMan" placeholder="请输入发货人" style="width:300px" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitShipForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {listOrder, getOrder,manualShipmentOrder} from "@/api/order/order";
import {listLogisticsStatus, listShop} from "@/api/shop/shop";
import Clipboard from "clipboard";


export default {
  name: "printOffline",
  data() {
    return {
      // 遮罩层
      loading: true,
      pushLoading: false,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedSShopOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 店铺订单表格数据
      orderList: [],
      logisticsList: [],
      shopList:[],
      // 弹出层标题
      detailTitle:'订单详情',
      detailOpen:false,
      shipOpen:false,
      isAudit:false,
      activeName: 'orderDetail',
      orderTime: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNum: null,
        shopId: null,
        shopType: 999,
        startTime: null,
        endTime: null,
        refundStatus: 1,
        orderStatus: 1
      },
      // 表单参数
      form: {
        id:null,
        length:null,
        width:null,
        height:null,
        weight:0,
        shippingCompany:null,
        shippingNumber:null,
        shippingCost:null,
        shippingMan:null,

      },
      // 表单校验
      rules: {
        height: [{ required: true, message: '不能为空' }],
        weight: [{ required: true, message: '不能为空' }],
        shippingNumber: [{ required: true, message: '不能为空' }],
        shippingCompany: [{ required: true, message: '不能为空' }],
        shippingCost: [{ required: true, message: '不能为空' }],
      }
    };
  },
  created() {
    listShop({type: 999}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });

  },
  methods: {
    copyActiveCode(event,queryParams) {
      console.log(queryParams)
      const clipboard = new Clipboard(".tag-copy")
      clipboard.on('success', e => {
        this.$message({ type: 'success', message: '复制成功' })
        // 释放内存
        clipboard.destroy()
      })
      clipboard.on('error', e => {
        // 不支持复制
        this.$message({ type: 'waning', message: '该浏览器不支持自动复制' })
        // 释放内存
        clipboard.destroy()
      })
    },


    /** 查询店铺订单列表 */
    getList() {
      console.log('=====搜索条件：=====',this.queryParams)
      if(this.orderTime){
        this.queryParams.startTime = this.orderTime[0]
        this.queryParams.endTime = this.orderTime[1]
      }else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.orderTime=null
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    // 取消按钮
    cancel() {
      this.getCodeOpen = false;
      this.shipOpen = false;
      this.allocateShipmentOpen = false;
      this.reset();
    },
    reset(){

    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        // this.$nextTick(()=>{
        //   this.form.shipType = response.data.shipType
        // })
        this.detailOpen = true;
        this.detailTitle = "订单详情";
      });
      this.isAudit = false
    },
    // 手动发货按钮
    handleShip(row){
      this.reset();
      const id = row.id || this.ids
      console.log('======',id)
      getOrder(id).then(response => {
        this.form = response.data;
        this.form.length=0
        this.form.width=0
        this.form.height=0
        this.form.weight=0.0
        this.form.shippingCost=0.0
        listLogisticsStatus({}).then(resp=>{
          this.logisticsList = resp.rows
        })
        this.shipOpen = true;
        // this.detailTitle = "订单详情";
      });
    },
    // 手动发货表单
    submitShipForm(){
      this.$refs["form"].validate(valid => {
        if (valid) {
          manualShipmentOrder(this.form).then(resp =>{
            this.$modal.msgSuccess("手动发货成功");
            this.shipOpen = false
            this.getList()
          })
        }
      })
    },
  }
};
</script>
