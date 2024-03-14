<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="类别名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入类别名称"
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
          v-hasPermi="['zlyyh:categoryPlatform:add']"
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
          v-hasPermi="['zlyyh:categoryPlatform:edit']"
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
          v-hasPermi="['zlyyh:categoryPlatform:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:categoryPlatform:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryPlatformList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" v-if="true"/>
      <el-table-column label="类别名称" align="center" prop="name" />
      <el-table-column label="类别ID" align="center" prop="categoryIds" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button  size="mini" type="text" icon="el-icon-edit"
                     @click="handleProductByCategory(scope.row)" v-hasPermi="['zlyyh:categoryPlatform:add']">商品维护</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:categoryPlatform:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:categoryPlatform:remove']"
          >删除</el-button>
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

    <!-- 添加或修改多平台类别对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类别名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类别名称" />
        </el-form-item>

        <el-form :model="queryCategoryParams" ref="queryCategoryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
          <el-form-item label="栏目类型" prop="categoryListType">
            <el-select v-model="queryCategoryParams.categoryListType" placeholder="请选择搜索的栏目内容类型" clearable>
              <el-option v-for="dict in dict.type.t_category_list_type" :key="dict.value" :label="dict.label"
                         :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="搜索状态" prop="status">
            <el-select v-model="queryCategoryParams.status" placeholder="请选择搜索栏目的状态" clearable>
              <el-option v-for="dict in dict.type.t_category_status" :key="dict.value" :label="dict.label"
                         :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="搜索平台" prop="platformKey">
            <el-select v-model="queryCategoryParams.platformKey" placeholder="请选择搜索的平台标识" clearable>
              <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
            </el-select>
          </el-form-item>
          <div style="margin-left: 18%">
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="getCategoryList">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetCategoryQuery">重置</el-button>
            </el-form-item>
          </div>

        </el-form>
        <el-form-item v-if="categoryList.length > 0" label="包含类别" prop="categoryIds">
          <treeselect v-model="form.categoryIds" multiple :options="categoryList" :normalizer="normalizer" size="small"
                      :flat="true" :default-expand-level="1"  :disable-branch-nodes="false" :show-count="true" placeholder="请选择分类" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="栏目商品" :visible.sync="isProduct" width="90%">
      <Product v-bind:categoryPlatformId=categoryPlatformId></Product>
    </el-dialog>
  </div>
</template>

<script>
import { listCategoryPlatform, getCategoryPlatform, delCategoryPlatform, addCategoryPlatform, updateCategoryPlatform } from "@/api/zlyyh/categoryPlatform";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {
  listCategory
} from "@/api/zlyyh/category";

import Product from "@/views/zlyyh/product/categoryProductPlatform.vue";
import {
  selectListPlatform
} from "@/api/zlyyh/platform";
export default {
  name: "CategoryPlatform",
  dicts: ['t_category_list_type', 't_category_status', 't_product_assign_date', 't_grad_period_date_list',
    't_show_index', 'channel_type'
  ],
  components: {
    Treeselect,
    Product
  },
  data() {
    return {
      platformList: [],
      isProduct: false,
      categoryPlatformId: undefined,
      // 类别下拉列表
      categoryList: [],
      queryCategoryParams: {
        categoryName: undefined,
        categoryListType: '0',
        status: '0',
        platformKey: undefined,
      },
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
      // 多平台类别表格数据
      categoryPlatformList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        categoryIds: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "类别名称不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
    this.getCategoryList();
  },
  methods: {
    resetCategoryQuery() {
      this.resetForm("queryCategoryForm");
      this.getCategoryList();
    },
    // 商品维护
    handleProductByCategory(row) {
      this.categoryPlatformId = row.id;
      this.isProduct = true;
    },
    /** 获取类别下拉列表 */
    getCategoryList() {
      this.categoryList = [];
      listCategory(
        this.queryCategoryParams
      ).then(response => {
        this.categoryList = this.handleTree(response.data, "categoryId", "parentId");
      });
    },
    /** 转换数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.categoryId,
        label: node.categoryName,
        children: node.children
      };
    },
    //平台标识下拉列表
    getPlatformSelectList() {
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
    },
    /** 查询多平台类别列表 */
    getList() {
      this.loading = true;
      listCategoryPlatform(this.queryParams).then(response => {
        this.categoryPlatformList = response.rows;
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
        name: undefined,
        categoryIds: undefined,
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加多平台类别";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getCategoryPlatform(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.form.categoryIds = this.strToArr(this.form.categoryIds);
        this.open = true;
        this.title = "修改多平台类别";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          let form = JSON.parse(JSON.stringify(this.form));
          form.categoryIds = this.arrToStr(form.categoryIds);
          if (this.form.id != null) {
            updateCategoryPlatform(form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addCategoryPlatform(form).then(response => {
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
      this.$modal.confirm('是否确认删除多平台类别编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delCategoryPlatform(ids);
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
      this.download('zlyyh/categoryPlatform/export', {
        ...this.queryParams
      }, `categoryPlatform_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
