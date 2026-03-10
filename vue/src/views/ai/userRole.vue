<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否默认" prop="isDefault">
        <el-select
          v-model="queryParams.isDefault"
          placeholder="是否默认角色"
          clearable
          style="width: 240px"
        >
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aiUserRoleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="角色编号" align="center" prop="id" v-if="columns[0].visible" />
      <el-table-column label="用户ID" align="center" prop="userId" v-if="columns[1].visible" />
      <el-table-column label="用户名称" align="center" prop="userName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
      <el-table-column label="角色名称" align="center" prop="roleName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
      <el-table-column label="角色描述" align="center" prop="description" v-if="columns[4].visible" :show-overflow-tooltip="true" />
      <el-table-column label="是否默认" align="center" prop="isDefault" v-if="columns[5].visible">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isDefault === 1 ? 'success' : 'info'">
            {{ scope.row.isDefault === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[6].visible" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        width="180"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row.id)"
          >删除</el-button>
          <el-button
            v-if="scope.row.isDefault !== 1"
            size="mini"
            type="text"
            icon="el-icon-star-on"
            @click="handleSetDefault(scope.row.id)"
          >设为默认</el-button>
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

    <!-- 添加或修改用户AI角色对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="500px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入角色描述" :rows="3" />
        </el-form-item>
        <el-form-item label="是否默认">
          <el-switch v-model="form.isDefault" active-value="1" inactive-value="0" />
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
import { listAiUserRole, getAiUserRole, addAiUserRole, updateAiUserRole, delAiUserRole, setDefaultRole } from '@/api/ai/userRole'

export default {
  name: 'AiUserRole',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户AI角色表格数据
      aiUserRoleList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        roleName: null,
        isDefault: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: '用户ID不能为空', trigger: 'blur' }
        ],
        roleName: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' },
          { min: 1, max: 200, message: '角色名称长度不能超过200个字符', trigger: 'blur' }
        ],
        description: [
          { max: 500, message: '角色描述长度不能超过500个字符', trigger: 'blur' }
        ]
      },
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示列
      columns: [
        { label: '角色编号', prop: 'id', visible: true },
        { label: '用户ID', prop: 'userId', visible: true },
        { label: '用户名称', prop: 'userName', visible: true },
        { label: '角色名称', prop: 'roleName', visible: true },
        { label: '角色描述', prop: 'description', visible: true },
        { label: '是否默认', prop: 'isDefault', visible: true },
        { label: '创建时间', prop: 'createTime', visible: true }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户AI角色列表 */
    getList() {
      this.loading = true
      listAiUserRole(this.queryParams).then(response => {
        this.aiUserRoleList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增用户AI角色'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAiUserRole(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改用户AI角色'
      })
    },
    /** 设置默认角色 */
    handleSetDefault(id) {
      this.$confirm('是否确认将该角色设置为默认角色?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        return setDefaultRole(id)
      }).then(() => {
        this.getList()
        this.$message({
          message: '设置成功',
          type: 'success'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消设置'
        })
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: null,
        userId: null,
        userName: null,
        roleName: null,
        description: null,
        isDefault: 0
      }
      this.resetForm('form')
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAiUserRole(this.form).then(response => {
              this.$message({
                message: '修改成功',
                type: 'success'
              })
              this.open = false
              this.getList()
            })
          } else {
            addAiUserRole(this.form).then(response => {
              this.$message({
                message: '新增成功',
                type: 'success'
              })
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(id) {
      const ids = id || this.ids
      this.$confirm('是否确认删除用户AI角色?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        return delAiUserRole(ids)
      }).then(() => {
        this.getList()
        this.$message({
          message: '删除成功',
          type: 'success'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false
      this.reset()
    }
  }
}
</script>
