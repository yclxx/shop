<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="blockName">
        <el-input v-model="queryParams.blockName" placeholder="请输入版块模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="主键名称" prop="mainField">
        <el-input v-model="queryParams.mainField" placeholder="请输入版块主键名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_page_block_status" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:pageBlock:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:pageBlock:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:pageBlock:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:pageBlock:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pageBlockList">
      <el-table-column label="模板名称" align="center" prop="blockName" />
      <el-table-column label="主键名称" align="center" prop="mainField" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_page_block_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-picture-outline-round" @click="handleLook(scope.row)"
            v-hasPermi="['zlyyh:pageBlock:look']">查看模板字段</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:pageBlock:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:pageBlock:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改版块模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="模板名称" prop="blockName">
          <el-input v-model="form.blockName" placeholder="请输入版块模板名称" />
        </el-form-item>
        <el-form-item label="主键名称" prop="mainField">
          <el-input v-model="form.mainField" placeholder="请输入版块主键名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_page_block_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序，从小到大" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="columnTitle" :visible.sync="columnOpen" width="1000px" append-to-body>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleColumnAdd">新增</el-button>
        </el-col>
      </el-row>
      <el-table v-loading="columnLoading" :data="columnList">
        <el-table-column label="字段名称" align="center" prop="columnName" />
        <el-table-column label="JAVA字段" align="center" prop="javaField" />
        <el-table-column label="是否必填" align="center" prop="isRequired">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_page_block_column_required" :value="scope.row.isRequired" />
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="sort" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleColumnUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleColumnDelete(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="columnTotal>0" :total="columnTotal" :page.sync="columnQueryParams.pageNum"
        :limit.sync="columnQueryParams.pageSize" @pagination="getColumnList" />
    </el-dialog>

    <!-- 添加或修改版块模板字段对话框 -->
    <el-dialog :title="columnChangeTitle" :visible.sync="columnChangeOpen" width="500px" append-to-body>
      <el-form ref="columnForm" :model="columnForm" :rules="columnRules" label-width="100px">
        <el-form-item label="字段名称" prop="columnName">
          <el-input v-model="columnForm.columnName" placeholder="请输入字段名称" />
        </el-form-item>
        <el-form-item label="JAVA字段名" prop="javaField">
          <el-input v-model="columnForm.javaField" placeholder="请输入JAVA字段名" />
        </el-form-item>
        <el-form-item label="是否必填" prop="isRequired">
          <el-select v-model="columnForm.isRequired">
            <el-option v-for="dict in dict.type.t_page_block_column_required" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="显示类型" prop="htmlType">
          <el-select v-model="columnForm.htmlType">
            <el-option v-for="dict in dict.type.t_page_block_column_html_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="columnForm.dictType" placeholder="请输入字段类型" />
        </el-form-item>
        <el-form-item label="字段描述" prop="columnComment">
          <el-input v-model="columnForm.columnComment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="columnForm.sort" placeholder="请输入排序" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitColumnForm">确 定</el-button>
        <el-button @click="columnCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listPageBlock,
    getPageBlock,
    delPageBlock,
    addPageBlock,
    updatePageBlock
  } from "@/api/zlyyh/pageBlock";

  import {
    listPageBlockColumn,
    getPageBlockColumn,
    delPageBlockColumn,
    addPageBlockColumn,
    updatePageBlockColumn
  } from "@/api/zlyyh/pageBlockColumn";

  export default {
    name: "PageBlock",
    dicts: ['t_page_block_status', 't_page_block_column_required', 't_page_block_column_html_type'],
    data() {
      return {
        // 按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        columnLoading: true,
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
        // 版块模板表格数据
        pageBlockList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        //模板字段
        columnOpen: false,
        columnTitle: "",
        columnList: [],
        columnQueryParams: {
          pageNum: 1,
          pageSize: 10
        },
        columnTotal: 0,
        columnForm: {},
        columnChangeOpen: false,
        columnChangeTitle: '',
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          blockName: undefined,
          status: undefined,
          sort: undefined,
          orderByColumn: 'sort',
          isAsc: 'asc'
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
          blockName: [{
            required: true,
            message: "模板名称不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          mainField: [{
            required: true,
            message: "主键名称不能为空",
            trigger: "blur"
          }]
        },
        columnRules: {
          columnId: [{
            required: true,
            message: "编号不能为空",
            trigger: "blur"
          }],
          columnName: [{
            required: true,
            message: "字段名称不能为空",
            trigger: "blur"
          }],
          javaField: [{
            required: true,
            message: "JAVA字段名不能为空",
            trigger: "blur"
          }],
          isRequired: [{
            required: true,
            message: "是否必填不能为空",
            trigger: "blur"
          }],
          htmlType: [{
            required: true,
            message: "显示类型不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      handleColumnUpdate(row) {
        this.columnLoading = true;
        this.resetColumn();
        const columnId = row.columnId || this.ids
        getPageBlockColumn(columnId).then(response => {
          this.columnLoading = false;
          this.columnForm = response.data;
          this.columnChangeOpen = true;
          this.columnChangeTitle = "修改版块模板字段";
        });
      },
      handleColumnAdd() {
        this.resetColumn();
        this.columnChangeOpen = true;
        this.columnChangeTitle = "添加板块模板字段";
        this.columnForm.blockId = this.blockId;
      },
      // 表单重置
      resetColumn() {
        this.columnForm = {
          columnId: undefined,
          blockId: undefined,
          columnName: undefined,
          columnComment: undefined,
          javaField: undefined,
          isRequired: undefined,
          htmlType: undefined,
          dictType: undefined,
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined
        };
        this.resetForm("columnForm");
      },
      // 取消按钮
      columnCancel() {
        this.columnChangeOpen = false;
        this.resetColumn();
      },
      submitColumnForm() {
        this.$refs["columnForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.columnForm.columnId != null) {
              updatePageBlockColumn(this.columnForm).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.columnChangeOpen = false;
                this.getColumnList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addPageBlockColumn(this.columnForm).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.columnChangeOpen = false;
                this.getColumnList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            }
          }
        });
      },
      handleColumnDelete(row) {
        const columnIds = row.columnId || this.ids;
        this.$modal.confirm('是否确认删除版块模板字段编号为"' + columnIds + '"的数据项？').then(() => {
          this.columnLoading = true;
          return delPageBlockColumn(columnIds);
        }).then(() => {
          this.columnLoading = false;
          this.getColumnList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.columnLoading = false;
        });
      },
      /** 查询版块模板列表 */
      getList() {
        this.loading = true;
        listPageBlock(this.queryParams).then(response => {
          this.pageBlockList = response.rows;
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
          blockName: undefined,
          mainField: undefined,
          status: undefined,
          sort: undefined,
          remark: undefined,
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
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加版块模板";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getPageBlock(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改版块模板";
        });
      },
      /**
       * 查看模板字段
       */
      handleLook(row) {
        this.blockId = row.id;
        this.getColumnList()
      },
      getColumnList() {
        this.columnQueryParams.blockId = this.blockId;
        this.columnLoading = true;
        listPageBlockColumn(this.columnQueryParams).then(response => {
          this.columnList = response.rows;
          this.columnTotal = response.total;
          this.columnOpen = true;
          this.columnLoading = false;
          this.columnTitle = "查看模板字段"
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updatePageBlock(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addPageBlock(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除版块模板编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delPageBlock(ids);
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
        this.download('zlyyh-admin/pageBlock/export', {
          ...this.queryParams
        }, `pageBlock_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
