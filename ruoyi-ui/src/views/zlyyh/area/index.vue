<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="行政区名称" prop="areaName" label-width="110px">
        <el-input v-model="queryParams.areaName" placeholder="请输入行政区名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="行政编码" prop="adcode" label-width="110px">
        <el-input v-model="queryParams.adcode" placeholder="请输入行政编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:area:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-sort" size="mini" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="areaList" row-key="adcode"
      :default-expand-all="isExpandAll" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column label="行政区名称" prop="areaName" />
      <el-table-column label="行政编码" prop="adcode" />
      <el-table-column label="行政区级别" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_area_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="创建人" prop="createBy" align="center" />
      <el-table-column label="创建时间" prop="createTime" align="center" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:area:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
            v-hasPermi="['zlyyh:area:add']">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:area:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改行政区对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="行政区名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入行政区名称" />
        </el-form-item>
        <el-form-item label="行政编码" prop="adcode">
          <el-input v-model="form.adcode" placeholder="请输入行政编码" />
        </el-form-item>
        <el-form-item label="上级行政区" prop="parentCode">
          <treeselect v-model="form.parentCode" :options="areaOptions" :normalizer="normalizer"
            placeholder="请选择上级行政区" />
        </el-form-item>
        <el-form-item label="行政区级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_area_level" :key="dict.value" :label="dict.label" :value="dict.value">
            </el-option>
          </el-select>
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
    listArea,
    getArea,
    delArea,
    addArea,
    updateArea
  } from "@/api/zlyyh/area";
  import Treeselect from "@riophae/vue-treeselect";
  import {
    titleCase
  } from '@/utils/index'
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";

  export default {
    name: "Area",
    dicts: ['t_area_level'],
    components: {
      Treeselect
    },
    data() {
      return {
        // 按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        // 显示搜索条件
        showSearch: true,
        // 行政区表格数据
        areaList: [],
        // 行政区树选项
        areaOptions: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 是否展开，默认全部展开
        isExpandAll: true,
        // 重新渲染表格状态
        refreshTable: true,
        // 查询参数
        queryParams: {
          areaName: undefined,
          level: undefined,
          firstLetter: undefined,
          parentCode: undefined,
          orderByColumn: 'adcode',
          isAsc: 'asc'
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          adcode: [{
            required: true,
            message: "行政编码不能为空",
            trigger: "blur"
          }],
          areaName: [{
            required: true,
            message: "行政区名称不能为空",
            trigger: "blur"
          }],
          level: [{
            required: true,
            message: "行政区级别不能为空",
            trigger: "blur"
          }],
          parentCode: [{
            required: true,
            message: "上级行政编码不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询行政区列表 */
      getList() {
        this.loading = true;
        listArea(this.queryParams).then(response => {
          this.areaList = this.handleTree(response.data, "adcode", "parentCode");
          this.loading = false;
        });
      },
      /** 转换行政区数据结构 */
      normalizer(node) {
        if (node.children && !node.children.length) {
          delete node.children;
        }
        return {
          id: node.adcode,
          label: node.areaName,
          children: node.children
        };
      },
      /** 查询行政区下拉树结构 */
      getTreeselect() {
        listArea().then(response => {
          this.areaOptions = [];
          const data = {
            adcode: 0,
            areaName: '顶级节点',
            children: []
          };
          data.children = this.handleTree(response.data, "adcode", "parentCode");
          this.areaOptions.push(data);
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
          id: null,
          adcode: null,
          areaName: null,
          level: null,
          firstLetter: null,
          parentCode: null,
          createBy: null,
          createTime: null,
          updateBy: null,
          updateTime: null
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd(row) {
        this.reset();
        this.getTreeselect();
        if (row != null && row.adcode) {
          this.form.parentCode = row.adcode;
        } else {
          this.form.parentCode = 0;
        }
        this.open = true;
        this.title = "添加行政区";
      },
      /** 展开/折叠操作 */
      toggleExpandAll() {
        this.refreshTable = false;
        this.isExpandAll = !this.isExpandAll;
        this.$nextTick(() => {
          this.refreshTable = true;
        });
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        this.getTreeselect();
        if (row != null) {
          this.form.parentCode = row.adcode;
        }
        getArea(row.id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改行政区";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateArea(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addArea(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除行政区编号为"' + row.id + '"的数据项？').then(() => {
          this.loading = true;
          return delArea(row.id);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      }
    }
  };
</script>
