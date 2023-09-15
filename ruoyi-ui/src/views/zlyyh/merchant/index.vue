<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商户名称" prop="merchantName">
        <el-input v-model="queryParams.merchantName" placeholder="请输入商户名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商户号" prop="merchantNo">
        <el-input v-model="queryParams.merchantNo" placeholder="请输入商户号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_merchant_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:merchant:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:merchant:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:merchant:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:merchant:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="merchantList">
      <el-table-column label="商户名称" align="center" prop="merchantName" />
      <el-table-column label="商户号" align="center" prop="merchantNo" />
      <el-table-column label="证书地址" align="center" prop="certPath" />
      <el-table-column label="证书密码" align="center" prop="merchantKey" />
      <el-table-column label="状态" align="center" prop="status" width="70px">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_merchant_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="支付回调" align="center" prop="payCallbackUrl" />
      <el-table-column label="退款回调" align="center" prop="refundCallbackUrl" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:merchant:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:merchant:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商户号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="归属部门" prop="sysDeptId">
          <treeselect v-model="form.sysDeptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
        </el-form-item>
        <el-form-item label="商户名称" prop="merchantName">
          <el-input v-model="form.merchantName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="商户号" prop="merchantNo">
          <el-input v-model="form.merchantNo" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="证书地址" prop="certPath">
          <el-input v-model="form.certPath" placeholder="请输入证书地址" />
          <fileUpload :fileType="['pfx']" v-model="form.certPath" :limit="1" />
        </el-form-item>
        <el-form-item label="证书密码" prop="merchantKey">
          <el-input v-model="form.merchantKey" placeholder="请输入证书密码" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_merchant_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支付回调" prop="payCallbackUrl">
          <el-input v-model="form.payCallbackUrl" placeholder="请输入支付成功回调通知地址" />
        </el-form-item>
        <el-form-item label="退款回调" prop="refundCallbackUrl">
          <el-input v-model="form.refundCallbackUrl" placeholder="请输入退款成功回调通知地址" />
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
  import {
    listMerchant,
    getMerchant,
    delMerchant,
    addMerchant,
    updateMerchant
  } from "@/api/zlyyh/merchant";
  import {
    deptTreeSelect
  } from "@/api/system/user";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";

  export default {
    name: "Merchant",
    dicts: ['t_merchant_status'],
    components: {
      Treeselect
    },
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
        // 商户号表格数据
        merchantList: [],
        // 部门树选项
        deptOptions: undefined,
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          merchantName: undefined,
          merchantNo: undefined,
          certPath: undefined,
          merchantKey: undefined,
          status: undefined,
          payCallbackUrl: undefined,
          refundCallbackUrl: undefined,
          orderByColumn: 'id',
          isAsc: 'desc'
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          merchantName: [{
            required: true,
            message: "商户名称不能为空",
            trigger: "blur"
          }],
          merchantNo: [{
            required: true,
            message: "商户号不能为空",
            trigger: "blur"
          }],
          certPath: [{
            required: true,
            message: "证书地址不能为空",
            trigger: "blur"
          }],
          merchantKey: [{
            required: true,
            message: "证书密码不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          payCallbackUrl: [{
            required: true,
            message: "支付成功回调通知地址不能为空",
            trigger: "blur"
          }],
          refundCallbackUrl: [{
            required: true,
            message: "退款成功回调通知地址不能为空",
            trigger: "blur"
          }],

        }
      };
    },
    created() {
      this.getList();
      this.getDeptTree();
    },
    methods: {
      /** 查询部门下拉树结构 */
      getDeptTree() {
        deptTreeSelect().then(response => {
          this.deptOptions = response.data;
        });
      },
      /** 查询商户号列表 */
      getList() {
        this.loading = true;
        listMerchant(this.queryParams).then(response => {
          this.merchantList = response.rows;
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
          id: undefined,
          merchantName: undefined,
          merchantNo: undefined,
          certPath: undefined,
          merchantKey: undefined,
          sysDeptId: undefined,
          status: "0",
          payCallbackUrl: undefined,
          refundCallbackUrl: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined
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
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加商户号";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getMerchant(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改商户号";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateMerchant(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMerchant(this.form).then(response => {
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
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除商户号编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delMerchant(ids);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('zlyyh-admin/merchant/export', {
          ...this.queryParams
        }, `merchant_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>