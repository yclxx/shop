<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="供应商编号" prop="supplierId">
        <el-input
          v-model="queryParams.supplierId"
          placeholder="请输入供应商编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商多级分类" prop="fullName">
        <el-input
          v-model="queryParams.fullName"
          placeholder="请输入供应商多级分类"
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
          v-hasPermi="['zlyyh:categorySupplier:add']"
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
          v-hasPermi="['zlyyh:categorySupplier:edit']"
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
          v-hasPermi="['zlyyh:categorySupplier:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:categorySupplier:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categorySupplierList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" v-if="true"/>
      <el-table-column width="100" label="供应商编号" align="center" prop="supplierId" />
      <el-table-column label="供应商名称" align="center" prop="supplierName" />
      <el-table-column label="供应商多级分类" align="center" prop="fullName" />
<!--      <el-table-column label="分类Id" align="center" prop="categoryId" />-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:categorySupplier:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:categorySupplier:remove']"
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

    <!-- 添加或修改供应商产品分类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="供应商编号" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="供应商多级分类" prop="fullName">
          <el-input v-model="form.fullName" placeholder="请输入供应商多级分类" />
        </el-form-item>
        <el-form-item label="包含类别" prop="categoryId">
          <treeselect v-model="form.categoryId" multiple :options="categoryList" :normalizer="normalizer" size="small"
                      :flat="true" :default-expand-level="1" :disable-branch-nodes="false" :show-count="true" placeholder="请选择分类" />
        </el-form-item>
        <el-form-item label="统一分类" prop="accountNumberType">
              <span slot="label">
                统一分类
                <el-tooltip content="统一分类,一个分类对应多个平台类别,具体对应类别请前往多平台分类中配置" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
          <el-checkbox-group @change="platformCategoryChange" v-model="form.platformCategory">
            <el-checkbox v-for="(item, index) in platformCategoryList" :key="index" :label="index">{{item.name}}</el-checkbox>
          </el-checkbox-group>
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
import { listCategorySupplier, getCategorySupplier, delCategorySupplier, addCategorySupplier, updateCategorySupplier } from "@/api/zlyyh/categorySupplier";
import { listCategoryPlatformAll } from "@/api/zlyyh/categoryPlatform";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {
  listCategory
} from "@/api/zlyyh/category";
export default {
  name: "CategorySupplier",
  components: {
    Treeselect
  },
  data() {
    return {
      // 类别下拉列表
      categoryList: [],
      //多平台分类下拉列表
      platformCategoryList: [],
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
      // 供应商产品分类表格数据
      categorySupplierList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierId: undefined,
        supplierName: undefined,
        fullName: undefined,
        categoryId: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
        supplierId: [
          { required: true, message: "供应商编号不能为空", trigger: "blur" }
        ],
        supplierName: [
          { required: true, message: "供应商名称不能为空", trigger: "blur" }
        ],
        fullName: [
          { required: true, message: "供应商多级分类不能为空", trigger: "blur" }
        ],

      }
    };
  },
  created() {
    this.getList();
    this.getCategoryList();
    this.getCategoryPlatform();
  },
  methods: {
    getCategoryPlatform(){
      listCategoryPlatformAll({
        status: '0',
      }).then(response => {
        this.platformCategoryList = response.data;
      });
    },
    /** 获取类别下拉列表 */
    getCategoryList() {
      listCategory({
        status: '0',
        categoryListType: '0'
      }).then(response => {
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
    platformCategoryChange(vu) {
      let se = "";
      this.form.platformCategory.forEach(idx => {
        se = se + this.platformCategoryList[idx].categoryIds + ",";
      })
      let ar = this.strToArr(se);
      this.form.categoryId = [...new Set(ar)];
    },
    /** 查询供应商产品分类列表 */
    getList() {
      this.loading = true;
      listCategorySupplier(this.queryParams).then(response => {
        this.categorySupplierList = response.rows;
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
        supplierId: undefined,
        supplierName: undefined,
        fullName: undefined,
        categoryId: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        accountNumberType: null,
        platformCategory: []
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
      this.title = "添加供应商产品分类";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {

      this.loading = true;
      this.reset();

      const id = row.id || this.ids
      getCategorySupplier(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.form.platformCategory = [];
        this.form.categoryId = this.strToArr(this.form.categoryId);
        this.open = true;
        this.title = "修改供应商产品分类";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          let form = JSON.parse(JSON.stringify(this.form));
          form.categoryId = this.arrToStr(form.categoryId);
          if (this.form.id != null) {
            updateCategorySupplier(form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addCategorySupplier(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除供应商产品分类编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delCategorySupplier(ids);
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
      this.download('zlyyh/categorySupplier/export', {
        ...this.queryParams
      }, `categorySupplier_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
