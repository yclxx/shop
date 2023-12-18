<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" clearable placeholder="请选择商品" style="width: 100%;">
          <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
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
          v-hasPermi="['zlyyh:productAmount:add']">新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:productAmount:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:productAmount:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:productAmount:export']">导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productAmountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="amountId" v-if="true" />
      <el-table-column label="商品" align="center" prop="productId" :formatter="changeProductName" />
      <el-table-column label="发放金额" align="center" prop="externalProductSendValue" />
      <el-table-column label="中奖概率" align="center" prop="drawProbability" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:productAmount:edit']">修改
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:productAmount:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商品价格配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择商品" style="width: 100%;">
            <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option v-for="dict in dict.type.t_product_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放金额" prop="externalProductSendValue">
          <el-input v-model="form.externalProductSendValue" placeholder="请输入发放金额">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>

        <el-form-item label="中奖概率" prop="drawProbability">
          <el-input-number v-model="form.drawProbability" controls-position="right" :precision="6" :min="0.000001"
            :step="0.000001" :max="1">
          </el-input-number>
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
    listProductAmount,
    getProductAmount,
    delProductAmount,
    addProductAmount,
    updateProductAmount
  } from "@/api/zlyyh/productAmount";
  import {
    selectListProduct
  } from "@/api/zlyyh/product"

  export default {
    name: "ProductAmount",
    dicts: ['t_product_status'],
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
        // 商品价格配置表格数据
        productAmountList: [],
        productList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          productId: undefined,
          externalProductSendValue: undefined,
          drawProbability: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          amountId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "商品ID不能为空",
            trigger: "blur"
          }],
          externalProductSendValue: [{
            required: true,
            message: "发放金额，不能为空",
            trigger: "blur"
          }],
          drawProbability: [{
            required: true,
            message: "中奖概率不能为空",
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
      this.getProductList();
    },
    methods: {
      /** 查询商品价格配置列表 */
      getList() {
        this.loading = true;
        listProductAmount(this.queryParams).then(response => {
          this.productAmountList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      getProductList() {
        selectListProduct({
          status: '0',
          productType: '10',
          searchStatus: '1',
        }).then(res => {
          this.productList = res.data;
        })
      },
      changeProductName(row) {
        let productName = ''
        this.productList.forEach(item => {
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
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          amountId: undefined,
          productId: undefined,
          externalProductSendValue: undefined,
          drawProbability: undefined,
          status: undefined,
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
        this.ids = selection.map(item => item.amountId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加商品价格配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const amountId = row.amountId || this.ids
        getProductAmount(amountId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改商品价格配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.amountId != null) {
              updateProductAmount(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addProductAmount(this.form).then(response => {
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
        const amountIds = row.amountId || this.ids;
        this.$modal.confirm('是否确认删除商品价格配置编号为"' + amountIds + '"的数据项？').then(() => {
          this.loading = true;
          return delProductAmount(amountIds);
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
        this.download('zlyyh/productAmount/export', {
          ...this.queryParams
        }, `productAmount_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>