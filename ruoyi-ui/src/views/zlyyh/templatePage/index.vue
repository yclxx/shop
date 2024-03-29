<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="落地页" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入落地页描述" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="显示标题" prop="showTitle">
        <el-select v-model="queryParams.showTitle" placeholder="请选择显示标题" clearable>
          <el-option v-for="dict in dict.type.t_template_page_show_title" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="页面标题" prop="pageTitle">
        <el-input v-model="queryParams.pageTitle" placeholder="请输入页面标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:templatePage:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:templatePage:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:templatePage:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:templatePage:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templatePageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="templateId" v-if="true" />
      <el-table-column label="落地页" align="center" prop="templateName" />
      <el-table-column label="显示标题" align="center" prop="showTitle">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_template_page_show_title" :value="scope.row.showTitle" />
        </template>
      </el-table-column>
      <el-table-column label="页面标题" align="center" prop="pageTitle" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:templatePage:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:templatePage:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改落地页对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="落地页" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入落地页描述" />
        </el-form-item>
        <el-form-item label="显示标题" prop="showTitle">
          <el-radio-group v-model="form.showTitle">
            <el-radio v-for="dict in dict.type.t_template_page_show_title" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
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
    listTemplatePage,
    getTemplatePage,
    delTemplatePage,
    addTemplatePage,
    updateTemplatePage
  } from "@/api/zlyyh/templatePage";

  export default {
    name: "TemplatePage",
    dicts: ['t_template_page_show_title'],
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
        // 落地页表格数据
        templatePageList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'template_id',
          isAsc: 'desc',
          templateName: undefined,
          showTitle: undefined,
          pageTitle: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          templateId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          templateName: [{
            required: true,
            message: "落地页描述不能为空",
            trigger: "blur"
          }],
          showTitle: [{
            required: true,
            message: "显示标题不能为空",
            trigger: "change"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询落地页列表 */
      getList() {
        this.loading = true;
        listTemplatePage(this.queryParams).then(response => {
          this.templatePageList = response.rows;
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
          templateId: undefined,
          templateName: undefined,
          showTitle: undefined,
          pageTitle: undefined,
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
        this.ids = selection.map(item => item.templateId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加落地页";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const templateId = row.templateId || this.ids
        getTemplatePage(templateId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改落地页";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.templateId != null) {
              updateTemplatePage(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addTemplatePage(this.form).then(response => {
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
        const templateIds = row.templateId || this.ids;
        this.$modal.confirm('是否确认删除落地页编号为"' + templateIds + '"的数据项？').then(() => {
          this.loading = true;
          return delTemplatePage(templateIds);
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
        this.download('zlyyh-admin/templatePage/export', {
          ...this.queryParams
        }, `templatePage_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>