<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分销商" prop="distributorName">
        <el-input v-model="queryParams.distributorName" placeholder="请输入分销商名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:distributor:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:distributor:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:distributor:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:distributor:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="distributorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分销商ID" width="200" align="center" prop="distributorId" v-if="true" />
      <el-table-column label="分销商名称" min-width="60" align="center" prop="distributorName" />
      <el-table-column label="通知地址" align="center" prop="backUrl" />
      <el-table-column label="状态" width="88" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="50" align="center" prop="remark" />
      <el-table-column label="操作" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:distributor:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:distributor:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改分销商信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="分销商ID" prop="distributorId" v-if="add">
              <el-input v-model="form.distributorId" placeholder="请输入分销商ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分销商" prop="distributorName">
              <el-input v-model="form.distributorName" placeholder="请输入分销商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="sysDeptId">
              <treeselect v-model="form.sysDeptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公钥" prop="publicKey">
              <el-input v-model="form.publicKey" placeholder="请输入公钥" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="私钥" prop="privateKey">
              <el-input v-model="form.privateKey" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知地址" prop="backUrl">
              <el-input v-model="form.backUrl" placeholder="请输入系统通知地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
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
    listDistributor,
    getDistributor,
    delDistributor,
    addDistributor,
    updateDistributor
  } from "@/api/zlyyh/distributor";
  import {
    deptTreeSelect
  } from "@/api/system/user";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";

  export default {
    name: "Distributor",
    dicts: ['sys_normal_disable'],
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
        // 部门树选项
        deptOptions: undefined,
        // 分销商信息表格数据
        distributorList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          distributorName: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          distributorName: [{
            required: true,
            message: "分销商名称不能为空",
            trigger: "blur"
          }],
        },
        add: true,
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
      /** 查询分销商信息列表 */
      getList() {
        this.loading = true;
        listDistributor(this.queryParams).then(response => {
          this.distributorList = response.rows;
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
          distributorId: undefined,
          publicKey: undefined,
          privateKey: undefined,
          distributorName: undefined,
          sysDeptId: undefined,
          backUrl: undefined,
          status: '0',
          delFlag: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          remark: undefined
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
        this.ids = selection.map(item => item.distributorId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.add = true;
        this.title = "添加分销商信息";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.add = false;
        this.reset();
        const distributorId = row.distributorId || this.ids
        getDistributor(distributorId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改分销商信息";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (!this.add) {
              updateDistributor(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addDistributor(this.form).then(response => {
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
        const distributorIds = row.distributorId || this.ids;
        this.$modal.confirm('是否确认删除分销商信息编号为"' + distributorIds + '"的数据项？').then(() => {
          this.loading = true;
          return delDistributor(distributorIds);
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
        this.download('zlyyh-admin/distributor/export', {
          ...this.queryParams
        }, `distributor_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>