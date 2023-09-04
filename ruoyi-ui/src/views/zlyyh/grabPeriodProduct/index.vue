<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="秒杀活动" prop="grabPeriodId">
        <el-select v-model="queryParams.grabPeriodId" placeholder="请选择秒杀活动" clearable filterable>
          <el-option v-for="item in grabPeriodList" :key="item.id" :value="item.id" :label="item.label" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入商品编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品名称" prop="productId">
        <el-select v-model="queryParams.productId" filterable clearable>
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
          v-hasPermi="['zlyyh:grabPeriodProduct:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:grabPeriodProduct:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:grabPeriodProduct:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:grabPeriodProduct:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="grabPeriodProductList">
      <el-table-column label="秒杀编号" align="center" prop="grabPeriodId" />
      <el-table-column label="秒杀活动" align="center" prop="grabPeriodName" :formatter="changeGradPeriodName" />
      <el-table-column label="商品编号" align="center" prop="productId" />
      <el-table-column label="商品名称" align="center" prop="productName" :formatter="changeProductName" />
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="150" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:grabPeriodProduct:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:grabPeriodProduct:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改秒杀商品配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="秒杀活动" prop="grabPeriodId">
          <el-select v-model="form.grabPeriodId" filterable clearable style="width: 100%;">
            <el-option v-for="item in grabPeriodList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="productIds">
          <el-select v-model="form.productIds" placeholder="请选择商品" filterable clearable multiple style="width: 100%;">
            <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序：从小到大" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改秒杀商品配置对话框 -->
    <el-dialog :title="updateTitle" :visible.sync="updateOpen" width="500px" append-to-body>
      <el-form ref="updateForm" :model="updateForm" :rules="updateRules" label-width="80px">
        <el-form-item label="秒杀活动" prop="grabPeriodId">
          <el-select v-model="updateForm.grabPeriodId" filterable clearable style="width: 100%;">
            <el-option v-for="item in grabPeriodList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="productId">
          <el-select v-model="updateForm.productId" placeholder="请选择商品" filterable clearable style="width: 100%;">
            <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="updateForm.sort" placeholder="请输入排序：从小到大" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitUpdateForm">确 定</el-button>
        <el-button @click="updateCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listGrabPeriodProduct,
    getGrabPeriodProduct,
    delGrabPeriodProduct,
    addGrabPeriodProduct,
    updateGrabPeriodProduct
  } from "@/api/zlyyh/grabPeriodProduct";

  import {
    listSelectGrabPeriod
  } from "@/api/zlyyh/grabPeriod"

  import {
    selectListProduct
  } from "@/api/zlyyh/product"

  export default {
    name: "GrabPeriodProduct",
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
        // 秒杀商品配置表格数据
        grabPeriodProductList: [],
        grabPeriodList: [],
        productList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        updateTitle: "",
        updateOpen: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          grabPeriodId: undefined,
          productId: undefined,
          sort: undefined,
          orderByColumn: 'grab_period_id,sort',
          isAsc: 'desc,asc'
        },
        // 表单参数
        form: {},
        updateForm: {},
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          grabPeriodId: [{
            required: true,
            message: "秒杀活动不能为空",
            trigger: "blur"
          }],
          productIds: [{
            required: true,
            message: "商品不能为空",
            trigger: "blur"
          }],
        },
        updateRules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          grabPeriodId: [{
            required: true,
            message: "秒杀活动不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "商品不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      listSelectGrabPeriod({}).then(res => {
        this.grabPeriodList = res.data;
      })
      selectListProduct({
        status: '0'
      }).then(res => {
        this.productList = res.data;
      })
    },
    methods: {
      changeGradPeriodName(row) {
        let gradPeriodName = ''
        this.grabPeriodList.forEach(item => {
          if (row.grabPeriodId == item.id) {
            gradPeriodName = item.label;
          }
        })
        if (gradPeriodName && gradPeriodName.length > 0) {
          row.gradPeriodName = gradPeriodName;
          return gradPeriodName;
        }
        return row.grabPeriodId;
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
      /** 查询秒杀商品配置列表 */
      getList() {
        this.loading = true;
        listGrabPeriodProduct(this.queryParams).then(response => {
          this.grabPeriodProductList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      updateCancel() {
        this.updateOpen = false;
        this.updateReset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: undefined,
          grabPeriodId: undefined,
          productId: undefined,
          productIds: undefined,
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined
        };
        this.resetForm("form");
      },
      updateReset() {
        this.updateForm = {
          id: undefined,
          grabPeriodId: undefined,
          productId: undefined,
          productIds: undefined,
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined
        };
        this.resetForm("updateForm");
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
        this.title = "添加秒杀商品配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.updateReset();
        const id = row.id || this.ids
        getGrabPeriodProduct(id).then(response => {
          this.loading = false;
          this.updateForm = response.data;
          this.updateOpen = true;
          this.updateTitle = "修改秒杀商品配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            this.form.productIds = this.form.productIds.toString();
            if (this.form.id != null) {
              updateGrabPeriodProduct(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addGrabPeriodProduct(this.form).then(response => {
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
      /** 提交按钮 */
      submitUpdateForm() {
        this.$refs["updateForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            updateGrabPeriodProduct(this.updateForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.updateOpen = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除秒杀商品配置编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delGrabPeriodProduct(ids);
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
        this.download('zlyyh-admin/grabPeriodProduct/export', {
          ...this.queryParams
        }, `grabPeriodProduct_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>