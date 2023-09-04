<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="权益包" prop="equityId">
        <el-select style="width: 100%;" clearable v-model="queryParams.equityId" placeholder="请选择权益包">
          <el-option v-for="item in equityList" :key="item.id" :label="item.label" :value="item.id">
            <span style="float: left">{{ item.label }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select style="width: 100%;" clearable v-model="queryParams.productId" placeholder="请选择商品">
          <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
            <span style="float: left">{{ item.label }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="产品归属" prop="equityType">
        <el-select v-model="queryParams.equityType" placeholder="请选择产品归属" clearable>
          <el-option v-for="dict in dict.type.equity_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['zlyyh:equityProduct:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:equityProduct:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:equityProduct:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:equityProduct:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="equityProductList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" width="180" align="center" prop="id" v-if="true" />
      <el-table-column label="权益包" min-width="120" align="center" prop="equityId" :formatter="formatterEquity" />
      <el-table-column label="商品" min-width="120" align="center" prop="productId" :formatter="formatterProduct" />
      <el-table-column label="商品价值" align="center" prop="productAmount" />
      <el-table-column label="产品归属" align="center" prop="equityType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.equity_type" :value="scope.row.equityType" />
        </template>
      </el-table-column>
      <el-table-column label="可领数量" align="center" prop="sendCount" />
      <el-table-column label="排序" width="68" align="center" prop="sort" />
      <el-table-column label="状态" width="68" align="center" prop="status">
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
            v-hasPermi="['zlyyh:equityProduct:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:equityProduct:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改权益包商品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="权益包" prop="equityId">
          <el-select style="width: 100%;" v-model="form.equityId" placeholder="请选择权益包">
            <el-option v-for="item in equityList" :key="item.id" :label="item.label" :value="item.id">
              <span style="float: left">{{ item.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="productId">
          <el-select style="width: 100%;" v-model="form.productId" placeholder="请选择商品">
            <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
              <span style="float: left">{{ item.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品价值" prop="productAmount">
          <el-input v-model="form.productAmount" placeholder="请输入商品价值" />
        </el-form-item>
        <el-form-item label="产品归属" prop="equityType">
          <el-select style="width: 100%;" v-model="form.equityType" placeholder="请选择产品归属">
            <el-option v-for="dict in dict.type.equity_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可领数量" prop="sendCount">
          <el-input v-model="form.sendCount" placeholder="请输入可领数量" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
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
    listEquityProduct,
    getEquityProduct,
    delEquityProduct,
    addEquityProduct,
    updateEquityProduct
  } from "@/api/zlyyh/equityProduct";
  import {
    selectListProduct
  } from "@/api/zlyyh/product";
  import {
    selectListEquity
  } from "@/api/zlyyh/equity";

  export default {
    name: "EquityProduct",
    dicts: ['equity_type', 'sys_normal_disable'],
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
        // 权益包商品表格数据
        equityProductList: [],
        equityList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          equityId: undefined,
          productId: undefined,
          equityType: undefined,
          status: undefined,
        },
        //商品下拉列表
        productList: [],
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          equityId: [{
            required: true,
            message: "权益包ID不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "商品ID不能为空",
            trigger: "blur"
          }],
          productAmount: [{
            required: true,
            message: "商品价值不能为空",
            trigger: "blur"
          }],
          equityType: [{
            required: true,
            message: "产品归属不能为空",
            trigger: "change"
          }],
          sendCount: [{
            required: true,
            message: "可领数量不能为空",
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
      //商品下拉列表
      selectListProduct({
        status: '0'
      }).then(response => {
        this.productList = response.data;
      });
      selectListEquity({
        status: '0'
      }).then(response => {
        this.equityList = response.data;
      });
    },
    methods: {
      formatterEquity(row) {
        let name = ''
        this.equityList.forEach(item => {
          if (row.equityId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          return name;
        }
        return row.equityId;
      },
      formatterProduct(row) {
        let name = ''
        this.productList.forEach(item => {
          if (row.productId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          return name;
        }
        return row.productId;
      },
      /** 查询权益包商品列表 */
      getList() {
        this.loading = true;
        listEquityProduct(this.queryParams).then(response => {
          this.equityProductList = response.rows;
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
          equityId: undefined,
          productId: undefined,
          productAmount: undefined,
          equityType: undefined,
          sendCount: undefined,
          sort: undefined,
          status: '0',
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
        this.title = "添加权益包商品";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getEquityProduct(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改权益包商品";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateEquityProduct(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addEquityProduct(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除权益包商品编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delEquityProduct(ids);
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
        this.download('zlyyh-admin/equityProduct/export', {
          ...this.queryParams
        }, `equityProduct_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>