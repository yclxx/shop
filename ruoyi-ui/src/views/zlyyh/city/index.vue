<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="城市编码" prop="citycode">
        <el-input v-model="queryParams.citycode" placeholder="请输入城市编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="行政区名称" prop="areaName">
        <el-input v-model="queryParams.areaName" placeholder="请输入行政区名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="行政区级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择行政区划级别" clearable>
          <el-option v-for="dict in dict.type.t_city_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="上级区域编码" prop="parentCode">
        <el-input v-model="queryParams.parentCode" placeholder="请输入上级区域编码" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="首字母" prop="firstLetter">
        <el-input v-model="queryParams.firstLetter" placeholder="请输入首字母" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:city:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-sort" size="mini" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="cityList" row-key="adcode"
      :default-expand-all="isExpandAll" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column label="行政区名称" prop="areaName" />
      <el-table-column label="区域编码" align="center" prop="adcode" />
      <el-table-column label="城市编码" align="center" prop="citycode" />
      <el-table-column label="行政区级别" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_city_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="上级区域编码" align="center" prop="parentCode" />
      <el-table-column label="首字母" align="center" prop="firstLetter" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:city:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
            v-hasPermi="['zlyyh:city:add']">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:city:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改行政区对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="城市编码" prop="citycode">
          <el-input v-model="form.citycode" placeholder="请输入城市编码" />
        </el-form-item>
        <el-form-item label="行政区名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入行政区名称" />
        </el-form-item>
        <el-form-item label="行政区划级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择行政区划级别">
            <el-option v-for="dict in dict.type.t_city_level" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上级区域编码" prop="parentCode">
          <treeselect v-model="form.parentCode" :options="cityOptions" :normalizer="normalizer"
            placeholder="请选择上级区域编码" />
        </el-form-item>
        <el-form-item label="首字母" prop="firstLetter">
          <el-input v-model="form.firstLetter" placeholder="请输入首字母" />
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
    listCity,
    getCity,
    delCity,
    addCity,
    updateCity
  } from "@/api/zlyyh/city";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";

  export default {
    name: "City",
    dicts: ['t_city_level'],
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
        cityList: [],
        // 行政区树选项
        cityOptions: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 是否展开，默认全部展开
        isExpandAll: false,
        // 重新渲染表格状态
        refreshTable: true,
        // 查询参数
        queryParams: {
          citycode: undefined,
          areaName: undefined,
          level: undefined,
          parentCode: undefined,
          firstLetter: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          adcode: [{
            required: true,
            message: "区域编码不能为空",
            trigger: "blur"
          }],
          citycode: [{
            required: true,
            message: "城市编码不能为空",
            trigger: "blur"
          }],
          areaName: [{
            required: true,
            message: "行政区名称不能为空",
            trigger: "blur"
          }],
          level: [{
            required: true,
            message: "行政区划级别  country:国家  province:省份不能为空",
            trigger: "change"
          }],
          parentCode: [{
            required: true,
            message: "上级区域编码  000000-代表无上级不能为空",
            trigger: "blur"
          }],
          firstLetter: [{
            required: true,
            message: "首字母不能为空",
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
        listCity(this.queryParams).then(response => {
          this.cityList = this.handleTree(response.data, "adcode", "parentCode");
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
        listCity().then(response => {
          this.cityOptions = [];
          const data = {
            adcode: 0,
            areaName: '顶级节点',
            children: []
          };
          data.children = this.handleTree(response.data, "adcode", "parentCode");
          this.cityOptions.push(data);
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
          adcode: null,
          citycode: null,
          areaName: null,
          level: null,
          parentCode: null,
          firstLetter: null,
          delFlag: null,
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
        getCity(row.adcode).then(response => {
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
            if (this.form.adcode != null) {
              updateCity(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addCity(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除行政区编号为"' + row.adcode + '"的数据项？').then(() => {
          this.loading = true;
          return delCity(row.adcode);
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