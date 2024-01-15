<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="页面标题" prop="pageTitle">
        <el-input v-model="queryParams.pageTitle" placeholder="请输入页面标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="文件名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入文件名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="数据数量" prop="count">
        <el-input v-model="queryParams.count" placeholder="请输入数据数量" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:fileImportLog:add']">新增</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:fileImportLog:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:fileImportLog:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:fileImportLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="fileImportLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录id" align="center" prop="importLogId" v-if="true" />
      <!-- <el-table-column label="文件地址" align="center" prop="url" /> -->
      <el-table-column label="文件名" align="center" prop="name" />
      <el-table-column label="页面标题" align="center" prop="pageTitle" />
      <el-table-column label="数据数量" align="center" prop="count" />
      <el-table-column label="导入数量" align="center" prop="importCount" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:fileImportLog:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:fileImportLog:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改文件导入记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!-- <el-form-item label="文件地址" prop="url">
          <el-input v-model="form.url" placeholder="请输入文件地址" />
        </el-form-item> -->
        <el-form-item label="文件名" prop="name">
          <el-input v-model="form.name" placeholder="请输入文件名" disabled />
        </el-form-item>
        <el-form-item label="数据数量" prop="count">
          <el-input v-model="form.count" placeholder="请输入数据数量" disabled />
        </el-form-item>
        <el-form-item label="导入数量" prop="importCount">
          <el-input v-model="form.importCount" placeholder="请输入导入数量" disabled />
        </el-form-item>
        <el-form-item label="页面标题" prop="pageTitle">
          <el-input v-model="form.pageTitle" placeholder="请输入页面标题" />
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
    listFileImportLog,
    getFileImportLog,
    delFileImportLog,
    addFileImportLog,
    updateFileImportLog
  } from "@/api/zlyyh/fileImportLog";

  export default {
    name: "FileImportLog",
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
        // 文件导入记录表格数据
        fileImportLogList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          url: undefined,
          name: undefined,
          count: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          importLogId: [{
            required: true,
            message: "文件导入记录id不能为空",
            trigger: "blur"
          }],
          url: [{
            required: true,
            message: "文件地址不能为空",
            trigger: "blur"
          }],
          name: [{
            required: true,
            message: "文件名不能为空",
            trigger: "blur"
          }],
          count: [{
            required: true,
            message: "数据数量不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询文件导入记录列表 */
      getList() {
        this.loading = true;
        listFileImportLog(this.queryParams).then(response => {
          this.fileImportLogList = response.rows;
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
          importLogId: undefined,
          url: undefined,
          name: undefined,
          count: undefined,
          delFlag: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined
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
        this.ids = selection.map(item => item.importLogId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加文件导入记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const importLogId = row.importLogId || this.ids
        getFileImportLog(importLogId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改文件导入记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.importLogId != null) {
              updateFileImportLog(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addFileImportLog(this.form).then(response => {
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
        const importLogIds = row.importLogId || this.ids;
        this.$modal.confirm('是否确认删除文件导入记录编号为"' + importLogIds + '"的数据项？').then(() => {
          this.loading = true;
          return delFileImportLog(importLogIds);
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
        this.download('zlyyh/fileImportLog/export', {
          ...this.queryParams
        }, `fileImportLog_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>