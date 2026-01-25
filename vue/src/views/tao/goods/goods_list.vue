<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="平台商品ID" prop="numIid">
        <el-input
          v-model="queryParams.numIid"
          placeholder="请输入平台商品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="商家编码" prop="outerId">
        <el-input
          v-model="queryParams.outerId"
          placeholder="请输入商家编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ERP商品ID" prop="erpGoodsId">
        <el-input
          v-model="queryParams.erpGoodsId"
          placeholder="请输入ERP skuId"
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
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          :loading="pullLoading"
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handlePull"
        >API拉取商品数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :disabled="multiple"
          icon="el-icon-refresh"
          size="mini"
          @click="handlePushOms"
        >推送到商品库</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="平台商品ID" align="center" prop="numIid" width="138"/>
      <el-table-column label="主图" width="60">
        <template slot-scope="scope">
<!--          <el-image  style="width: 70px; height: 70px;" :src="scope.row.picUrl"></el-image>-->
          <image-preview :src="scope.row.picUrl" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="left" prop="title" width="350"/>

      <el-table-column label="商家编码" align="center" prop="outerId" />
      <el-table-column label="价格" align="left" prop="price" :formatter="amountFormatter" />
      <el-table-column label="SKU" align="center" >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleViewSkuList(scope.row)"
          >{{scope.row.skus.length +' 个SKU'}}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="ERP商品Id" align="center" prop="erpGoodsId" />
      <el-table-column label="库存" align="center" prop="num" />
      <el-table-column label="销量" align="center" prop="soldQuantity" />

      <el-table-column label="最后修改时间" align="center" prop="modified" >
        <template slot-scope="scope">
          {{parseTime(scope.row.modified)}}
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

    <el-dialog title="Sku List" :visible.sync="skuOpen" width="1200px" append-to-body>
      <el-table v-loading="loading" :data="skuList" :row-class-name="rowIndex">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="序号" align="center" prop="index" width="50"/>
        <el-table-column label="SKU编码" align="left" prop="outerId" />
        <el-table-column label="平台SkuId" align="center" prop="skuId" />
<!--        <el-table-column label="图片" align="center" prop="colorImage" width="100">-->
<!--          <template slot-scope="scope">-->
<!--            <image-preview :src="scope.row.colorImage" :width="50" :height="50"/>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column label="商品名称" align="left" prop="goodsName" width="288px"/>-->
        <el-table-column label="属性" align="center" prop="propertiesName" >
          <template slot-scope="scope">
            {{getSkuProper(scope.row.propertiesName)}}
          </template>
        </el-table-column>
        <el-table-column label="价格" align="center" prop="price" :formatter="amountFormatter"/>
        <el-table-column label="数量" align="center" prop="quantity" />
        <el-table-column label="ERP SKU ID" align="center" prop="erpGoodsSkuId" />
        <el-table-column label="状态" align="center" prop="status" >
          <template slot-scope="scope">
            <el-tag size="small" v-if="scope.row.status === 'normal'">正常</el-tag>
            <el-tag size="small" v-else>{{scope.row.status}}</el-tag>
<!--            <el-tag size="small" v-if="scope.row.status === 20">供应商发货</el-tag>-->
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-share"
              @click="handleLink(scope.row)"
            >关联ERP</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 关联ERP -->
    <el-dialog title="关联ERP商品SKU" :visible.sync="detailOpen" width="560px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" inline>
        <el-form-item label="ERP商品SkuId" prop="erpGoodsSkuId">
          <el-input v-model.number="form.erpGoodsSkuId" placeholder="请输入ERP商品SkuId" style="width:250px" />
<!--            <el-select v-model="form.erpGoodsSkuId" filterable remote reserve-keyword placeholder="搜索（sku编码、skuId）" style="width: 350px;"-->
<!--                       :remote-method="searchSku" :loading="skuListLoading" @change="skuChanage(scope.row)">-->
<!--              <el-option v-for="item in skuList" :key="item.id"-->
<!--                         :label="item.name + ' - ' + item.colorValue + ' ' + item.sizeValue + ' ' + item.styleValue"-->
<!--                         :value="item.id">-->
<!--              </el-option>-->
<!--            </el-select>-->
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listShop } from "@/api/shop/shop";
import { searchSku } from "@/api/goods/goods";
import {MessageBox} from "element-ui";
import {getGoodsSku, linkErpGoodsSkuId, listGoods, pullGoodsList,pushToOms} from "@/api/tao/goods";
import {amountFormatter, parseTime, rowIndex} from "@/utils/zhijian";

export default {
  name: "GoodsListTao",
  data() {
    return {
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      pullLoading: false,
      goodsList:[],
      shopList:[],
      skuList:[],

      // 是否显示弹出层
      detailOpen:false,
      skuListLoading:false,
      skuOpen:false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopId: null,
        numIid: null,
        skuId: null,
        outerId: null,
        erpGoodsSkuId: null
      },
      // 表单参数
      form: {
        erpGoodsSkuId:null,
        id:null
      },
      rules: {
        id: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
        erpGoodsSkuId: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    listShop({type: 100}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });
    // this.getList();
  },
  mounted() {
    if(this.$route.query.shopId){
        this.queryParams.shopId = this.$route.query.shopId
    }
  },
  methods: {
    rowIndex,
    parseTime,
    amountFormatter,
    getSkuProper(proper){
      const pArr =proper.split(';');
      console.log('====',pArr)
      let skuName=""
      for(let p of pArr){
        skuName+=p.split(":")[3]+'  '
      }
      return skuName;
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 查询淘宝订单列表 */
    getList() {
      this.loading = true;
      console.log('====商品list==',this.queryParams)
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.detailOpen = false;
      this.skuOpen = false
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        erpGoodsSkuId: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handlePull() {
      if(this.queryParams.shopId){
        this.pullLoading = true
        pullGoodsList({shopId:this.queryParams.shopId}).then(response => {
          console.log('拉取淘宝商品接口返回=====',response)
          if(response.code === 1401) {
              MessageBox.confirm('Token已过期，需要重新授权！请前往店铺列表重新获取授权！', '系统提示', { confirmButtonText: '前往授权', cancelButtonText: '取消', type: 'warning' }).then(() => {
                // isRelogin.show = false;
                this.$router.push({path:"/shop/shop_list",query:{platform:4}})
                // store.dispatch('LogOut').then(() => {
                // location.href = response.data.tokenRequestUrl+'?shopId='+this.queryParams.shopId
                // })
              }).catch(() => {
                isRelogin.show = false;
              });

            // return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
          }else{
            this.$modal.msgSuccess(JSON.stringify(response));
            this.getList()
          }


          this.pullLoading = false
        })
      }else{
        this.$modal.msgSuccess("请先选择店铺");
      }

      // this.$modal.msgSuccess("请先配置API");
    },
    /** 查看SKU List*/
    handleViewSkuList(row){
      this.skuList = row.skus
      this.skuOpen = true;

    },
    handlePushOms(){
      this.$confirm('确认同步所有商品到商品库吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        pushToOms( this.ids ).then(response => {
          this.$message.success('商品同步成功')
          this.getList()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          linkErpGoodsSkuId(this.form).then(response => {
            this.$modal.msgSuccess("关联成功");
            this.detailOpen = false;
            this.skuOpen = false;
            this.getList();
          });
        }
      });
    },
    handleLink(row) {
      this.reset();
      const id = row.id || this.ids
      getGoodsSku(id).then(response => {
        console.log('=====00000000============',response)
        this.form = response.data;
        this.detailOpen = true;
      });
    },
    // 搜索SKU
    searchSku(query) {
      this.shopLoading = true;
      const qw = {
        keyword: query
      }
      searchSku(qw).then(res => {
        this.skuList = res.rows;
        this.skuListLoading = false;
      })
    },
  }
};
</script>
