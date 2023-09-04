<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="券包" prop="productId">
        <el-select v-model="queryParams.productId" clearable placeholder="请选择券包" style="width: 100%;">
          <el-option v-for="item in productPackageSelectList" :key="item.id" :value="item.id"
            :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="券包商品" prop="extProductId">
        <el-select v-model="queryParams.extProductId" clearable placeholder="请选择券包商品" style="width: 100%;">
          <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
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
          v-hasPermi="['zlyyh:productPackage:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:productPackage:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:productPackage:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:productPackage:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productPackageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="packageId" v-if="true" />
      <el-table-column label="券包" align="center" prop="productId" :formatter="changeProductPackageName" />
      <el-table-column label="券包商品" align="center" prop="extProductId" :formatter="changeProductName" />
      <el-table-column label="发放数量" width="88" align="center" prop="sendCount" />
      <el-table-column label="状态" width="88" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
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
            v-hasPermi="['zlyyh:productPackage:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:productPackage:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商品券包对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="券包" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择券包" style="width: 100%;">
            <el-option v-for="item in productPackageSelectList" :key="item.id" :value="item.id"
              :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="券包商品" prop="extProductId">
          <el-select v-model="form.extProductId" placeholder="请选择券包商品" style="width: 100%;">
            <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放数量" prop="sendCount">
          <el-input v-model="form.sendCount" placeholder="请输入发放数量" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
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
    listProductPackage,
    getProductPackage,
    delProductPackage,
    addProductPackage,
    updateProductPackage
  } from "@/api/zlyyh/productPackage";
  import {
    selectListProduct
  } from "@/api/zlyyh/product"

  export default {
    name: "ProductPackage",
    dicts: ['sys_normal_disable'],
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
        // 商品券包表格数据
        productPackageList: [],
        productList: [],
        productPackageSelectList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          productId: undefined,
          extProductId: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          packageId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "券包ID不能为空",
            trigger: "blur"
          }],
          extProductId: [{
            required: true,
            message: "券包商品不能为空",
            trigger: "blur"
          }],
          sendCount: [{
            required: true,
            message: "发放数量不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
        }
      };
    },
    created() {
      this.getList();
      this.getProductPackageList()
      this.getProductList()
    },
    methods: {
      getProductList() {
        selectListProduct({
          status: '0'
        }).then(res => {
          this.productList = res.data;
        })
      },
      getProductPackageList() {
        selectListProduct({
          status: '0',
          productType: '9',
        }).then(res => {
          this.productPackageSelectList = res.data;
        })
      },
      changeProductPackageName(row) {
        let productName = ''
        this.productPackageSelectList.forEach(item => {
          if (row.productId == item.id) {
            productName = item.label;
          }
        })
        if (productName && productName.length > 0) {
          row.productName = productName;
          return productName;
        }
        return row.productId;
      },
      changeProductName(row) {
        let productName = ''
        this.productList.forEach(item => {
          if (row.extProductId == item.id) {
            productName = item.label;
          }
        })
        if (productName && productName.length > 0) {
          row.productName = productName;
          return productName;
        }
        return row.extProductId;
      },
      /** 查询商品券包列表 */
      getList() {
        this.loading = true;
        listProductPackage(this.queryParams).then(response => {
          this.productPackageList = response.rows;
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
          packageId: undefined,
          productId: undefined,
          extProductId: undefined,
          sendCount: undefined,
          status: '0',
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
        this.ids = selection.map(item => item.packageId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加商品券包";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const packageId = row.packageId || this.ids
        getProductPackage(packageId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改商品券包";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.packageId != null) {
              updateProductPackage(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addProductPackage(this.form).then(response => {
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
        const packageIds = row.packageId || this.ids;
        this.$modal.confirm('是否确认删除商品券包编号为"' + packageIds + '"的数据项？').then(() => {
          this.loading = true;
          return delProductPackage(packageIds);
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
        this.download('zlyyh-admin/productPackage/export', {
          ...this.queryParams
        }, `productPackage_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>