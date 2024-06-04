<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable size="small" class="filter-item" style="width: 200px;" placeholder="请输入用户名"/>
      <el-input v-model="listQuery.mobile" clearable size="small" class="filter-item" style="width: 200px;" placeholder="请输入手机号"/>
      <el-select v-model="listQuery.statusArray" multiple size="small" style="width: 200px" class="filter-item" placeholder="请选择提现状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="记录ID" prop="id" sortable/>

      <el-table-column align="center" label="申请流水" prop="traceSn"/>

      <el-table-column align="center" label="类型" prop="type">
        <template slot-scope="scope">
          {{ scope.row.type | typeFilter }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="用户编号" prop="id"/>

      <el-table-column align="center" label="手机号码" prop="mobile"/>

      <el-table-column align="center" label="提现金额" prop="amount"/>

      <el-table-column align="center" label="已提总额" prop="totalAmount"/>

      <el-table-column align="center" label="审批状态" prop="status">
        <template slot-scope="scope">
          {{ scope.row.status | statusFilter }}
        </template>
      </el-table-column>

      <el-table-column align="center" label="审批备注" prop="traceMsg"/>

      <el-table-column align="center" label="操作" width="120" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/brokerage/approve']" v-if="scope.row.status==0 || scope.row.status==2" type="primary" size="mini" @click="handleApprove(scope.row)">审批</el-button>
          <el-button v-permission="['GET /admin/brokerage/approve']" v-else type="info" size="mini" >已审批</el-button>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

    <!-- 审批窗口 -->
    <el-dialog :visible.sync="approveDialogVisible" title="提现审批">
      <el-form ref="approveForm" :rules="rules" :model="approveForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="是否通过" prop="status">
          <el-radio-group v-model="approveForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批备注" prop="traceMsg">
          <el-input v-model="approveForm.traceMsg"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApprove">审批</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { fetchList, approveTrace } from '@/api/business/brokerage'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import checkPermission from '@/utils/permission' // 权限判断函数

const statusMap = {
  0: '提现申请',
  1: '审批通过',
  2: '审批拒绝'
}

const typeMap = {
  0: '系统结算',
  1: '用户申请'
}

export default {
  name: 'Account',
  components: { Pagination },
  filters: {
    statusFilter(status) {
      return statusMap[status]
    },
    typeFilter(type) {
      return typeMap[type]
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        mobile: undefined,
        statusArray: [],
        sort: 'add_time',
        order: 'desc'
      },
      statusMap,
      typeMap,
      rules: {
        status: [
          { required: true, message: '审批状态不能为空！', trigger: 'blur' }
        ]
      },
      approveDialogVisible: false,
      approveForm: {
        id: undefined,
        status: undefined,
        traceMsg: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    checkPermission,
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleApprove(row) {
      this.approveForm.id = row.id
      this.approveDialogVisible = true
      this.$nextTick(() => {
        this.$refs['approveForm'].clearValidate()
      })
    },
    confirmApprove() {
      this.$refs['approveForm'].validate((valid) => {
        if (valid) {
          approveTrace(this.approveForm).then(response => {
            this.approveDialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '审批成功'
            })
            this.getList()
          }).catch(response => {
            this.$notify.error({
              title: '审批失败',
              message: response.data.errmsg
            })
          })
        }
      })
    }
  }
}
</script>
