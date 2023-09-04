<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模版名称" prop="blockId">
        <el-select v-model="queryParams.blockId" clearable filterable>
          <el-option v-for="item in pageBlockList" :key="item.id" :label="item.label" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="字段名称" prop="columnName">
        <el-input v-model="queryParams.columnName" placeholder="请输入字段名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:pageBlockColumn:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:pageBlockColumn:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:pageBlockColumn:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:pageBlockColumn:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pageBlockColumnList">
      <el-table-column label="模板名称" align="center" prop="blockName" :formatter="changeBlock" />
      <el-table-column label="字段名称" align="center" prop="columnName" />
      <el-table-column label="JAVA字段" align="center" prop="javaField" />
      <el-table-column label="是否必填" align="center" prop="isRequired">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_page_block_column_required" :value="scope.row.isRequired" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:pageBlockColumn:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:pageBlockColumn:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改版块模板字段对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模版名称" prop="blockId">
          <el-select v-model="form.blockId">
            <el-option v-for="item in pageBlockList" :key="item.id" :label="item.label" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字段名称" prop="columnName">
          <el-input v-model="form.columnName" placeholder="请输入字段名称" />
        </el-form-item>
        <el-form-item label="JAVA字段名" prop="javaField">
          <el-input v-model="form.javaField" placeholder="请输入JAVA字段名" />
        </el-form-item>
        <el-form-item label="是否必填" prop="isRequired">
          <el-select v-model="form.isRequired">
            <el-option v-for="dict in dict.type.t_page_block_column_required" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="显示类型" prop="htmlType">
          <el-select v-model="form.htmlType">
            <el-option v-for="dict in dict.type.t_page_block_column_html_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字段类型" />
        </el-form-item>
        <el-form-item label="字段描述" prop="columnComment">
          <el-input v-model="form.columnComment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
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
    listPageBlockColumn,
    getPageBlockColumn,
    delPageBlockColumn,
    addPageBlockColumn,
    updatePageBlockColumn
  } from "@/api/zlyyh/pageBlockColumn";

  import {
    selectListPageBlock
  } from "@/api/zlyyh/pageBlock";

  export default {
    name: "PageBlockColumn",
    dicts: ['t_page_block_column_html_type', 't_page_block_column_required'],
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
        // 版块模板字段表格数据
        pageBlockColumnList: [],
        pageBlockList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          blockId: undefined,
          columnName: undefined,
          columnComment: undefined,
          javaField: undefined,
          isRequired: undefined,
          htmlType: undefined,
          dictType: undefined,
          sort: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc'
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          columnId: [{
            required: true,
            message: "编号不能为空",
            trigger: "blur"
          }],
          blockId: [{
            required: true,
            message: "归属模板编号不能为空",
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
      selectListPageBlock({}).then(response => {
        this.pageBlockList = response.data;
      })
    },
    methods: {
      changeBlock(row) {
        let blockName = ''
        this.pageBlockList.forEach(item => {
          if (row.blockId == item.id) {
            blockName = item.label;
          }
        })
        if (blockName && blockName.length > 0) {
          row.blockName = blockName;
          return blockName;
        }
        return row.blockId;
      },
      /** 查询版块模板字段列表 */
      getList() {
        this.loading = true;
        listPageBlockColumn(this.queryParams).then(response => {
          this.pageBlockColumnList = response.rows;
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
        this.ids = selection.map(item => item.columnId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加版块模板字段";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const columnId = row.columnId || this.ids
        getPageBlockColumn(columnId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改版块模板字段";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.columnId != null) {
              updatePageBlockColumn(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addPageBlockColumn(this.form).then(response => {
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
        const columnIds = row.columnId || this.ids;
        this.$modal.confirm('是否确认删除版块模板字段编号为"' + columnIds + '"的数据项？').then(() => {
          this.loading = true;
          return delPageBlockColumn(columnIds);
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
        this.download('zlyyh-admin/pageBlockColumn/export', {
          ...this.queryParams
        }, `pageBlockColumn_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
