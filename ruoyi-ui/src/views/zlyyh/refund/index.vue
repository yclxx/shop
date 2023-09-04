<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="退款用户" prop="refundApplicant">
        <el-input
          v-model="queryParams.refundApplicant"
          placeholder="请输入退款用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['zlyyh:refund:add']"
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
          v-hasPermi="['zlyyh:refund:edit']"
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
          v-hasPermi="['zlyyh:refund:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:refund:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="refundList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="refundId" v-if="true"/>
      <el-table-column label="订单号" align="center" prop="number" />
      <el-table-column label="退款金额" align="center" prop="refundAmount" />
      <el-table-column label="退款用户ID" align="center" prop="refundApplicant" />
      <el-table-column label="退款审核人" align="center" prop="refundReviewer" />
      <el-table-column label="审核状态" align="center" prop="status" fixed="right" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_refund_status" :value="scope.row.status" />
        </template>
      </el-table-column>

      <el-table-column label="审核拒绝原因" align="center" prop="refuseReason" />
      <el-table-column label="退款原因" align="center" prop="refundRemark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status == '0'" size="mini" type="text" icon="el-icon-check"
                     @click="handleAgree(scope.row)" v-hasPermi="['zlyyh:refund:edit']">审核通过</el-button>
          <el-button v-if="scope.row.status == '0'" size="mini" type="text" icon="el-icon-close"
                     @click="handleRefuse(scope.row)" v-hasPermi="['zlyyh:refund:edit']">审核拒绝</el-button>

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

    <!-- 添加或修改退款订单登记对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单号" prop="number">
          <el-input v-model="form.number" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="退款金额" prop="refundAmount">
          <el-input v-model="form.refundAmount" placeholder="请输入退款金额" />
        </el-form-item>
        <el-form-item label="退款申请人" prop="refundApplicant">
          <el-input v-model="form.refundApplicant" placeholder="请输入退款申请人" />
        </el-form-item>
        <el-form-item label="退款审核人" prop="refundReviewer">
          <el-input v-model="form.refundReviewer" placeholder="请输入退款审核人" />
        </el-form-item>
        <el-form-item label="审核拒绝原因" prop="refuseReason">
          <el-input v-model="form.refuseReason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="退款原因" prop="refundRemark">
          <el-input v-model="form.refundRemark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRefund, getRefund, delRefund, addRefund, updateRefund,agreeSubmit,refuseSubmit } from "@/api/zlyyh/refund";

export default {
  name: "Refund",
  dicts: ['t_refund_status'],
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 退款订单登记表格数据
      refundList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        number: undefined,
        refundAmount: undefined,
        refundApplicant: undefined,
        refundReviewer: undefined,
        status: undefined,
        refuseReason: undefined,
        refundRemark: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        refundId: [
          { required: true, message: "退款订单ID不能为空", trigger: "blur" }
        ],
        number: [
          { required: true, message: "订单号不能为空", trigger: "blur" }
        ],
        refundAmount: [
          { required: true, message: "退款金额不能为空", trigger: "blur" }
        ],
        refundApplicant: [
          { required: true, message: "退款申请人不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "0=审核中，1=审核通过，2=审核不通过不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询退款订单登记列表 */
    getList() {
      this.loading = true;
      listRefund(this.queryParams).then(response => {
        this.refundList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        refundId: undefined,
        number: undefined,
        refundAmount: undefined,
        refundApplicant: undefined,
        refundReviewer: undefined,
        status: undefined,
        refuseReason: undefined,
        refundRemark: undefined,
        createTime: undefined,
        createBy: undefined,
        updateTime: undefined,
        updateBy: undefined
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.refundId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    //审核拒绝
    handleRefuse(row) {
      const refundId = row.refundId;
      this.$modal.confirm("是否确认审核拒绝退款申请编号为：‘" + refundId + "数据项?").then(() => {
        this.loading = true;
        return refuseSubmit(refundId);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    //审核通过
    handleAgree(row) {
      const refundId = row.refundId;
      this.$modal.confirm("是否确认审核通过退款申请编号为：‘" + refundId + "数据项?").then(() => {
        this.loading = true;
        return agreeSubmit(refundId);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加退款订单登记";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const refundId = row.refundId || this.ids
      getRefund(refundId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改退款订单登记";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.refundId != null) {
            updateRefund(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addRefund(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const refundIds = row.refundId || this.ids;
      this.$modal.confirm('是否确认删除退款订单登记编号为"' + refundIds + '"的数据项？').then(() => {
        this.loading = true;
        return delRefund(refundIds);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh/refund/export', {
        ...this.queryParams
      }, `refund_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
