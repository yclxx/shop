<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品订单号" prop="prodTn">
        <el-input
          v-model="queryParams.prodTn"
          placeholder="请输入商品订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="券码流水号" prop="bondSerlNo">
        <el-input
          v-model="queryParams.bondSerlNo"
          placeholder="请输入券码流水号"
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
          v-hasPermi="['zlyyh:orderUnionSend:add']"
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
          v-hasPermi="['zlyyh:orderUnionSend:edit']"
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
          v-hasPermi="['zlyyh:orderUnionSend:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:orderUnionSend:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderUnionSendList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="number" v-if="true"/>
      <el-table-column label="商品订单号" align="center" prop="prodTn" />
      <el-table-column label="卡券类型" align="center" prop="prodTp" />
      <el-table-column label="券码流水号" align="center" prop="bondSerlNo" />
      <el-table-column label="券码" align="center" prop="bondNo" />
      <!--<el-table-column label="券密" align="center" prop="bondEncNo" />-->
      <el-table-column label="生效时间" align="center" prop="effectDtTm" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.effectDtTm, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" align="center" prop="exprDtTm" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.exprDtTm, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="购买须知" align="center" prop="cusIstr" />
      <el-table-column label="退券订单号" align="center" prop="rfdTn" />
      <el-table-column label="券状态" align="center" prop="bondSt" />
      <!--<el-table-column label="创建时间" align="center" prop="createTime" width="180">-->
      <!--  <template slot-scope="scope">-->
      <!--    <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>-->
      <!--  </template>-->
      <!--</el-table-column>-->
      <!--<el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
      <!--  <template slot-scope="scope">-->
      <!--    <el-button-->
      <!--      size="mini"-->
      <!--      type="text"-->
      <!--      icon="el-icon-edit"-->
      <!--      @click="handleUpdate(scope.row)"-->
      <!--      v-hasPermi="['zlyyh:orderUnionSend:edit']"-->
      <!--    >修改</el-button>-->
      <!--    <el-button-->
      <!--      size="mini"-->
      <!--      type="text"-->
      <!--      icon="el-icon-delete"-->
      <!--      @click="handleDelete(scope.row)"-->
      <!--      v-hasPermi="['zlyyh:orderUnionSend:remove']"-->
      <!--    >删除</el-button>-->
      <!--  </template>-->
      <!--</el-table-column>-->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改银联分销订单卡券对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrderUnionSend, getOrderUnionSend, delOrderUnionSend, addOrderUnionSend, updateOrderUnionSend } from "@/api/zlyyh/orderUnionSend";

export default {
  name: "OrderUnionSend",
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
      // 银联分销订单卡券表格数据
      orderUnionSendList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        number: undefined,
        prodTn: undefined,
        bondSerlNo: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询银联分销订单卡券列表 */
    getList() {
      this.loading = true;
      listOrderUnionSend(this.queryParams).then(response => {
        this.orderUnionSendList = response.rows;
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
        number: undefined,
        prodTn: undefined,
        prodTp: undefined,
        bondSerlNo: undefined,
        bondNo: undefined,
        bondEncNo: undefined,
        effectDtTm: undefined,
        exprDtTm: undefined,
        cusIstr: undefined,
        rfdTn: undefined,
        bondSt: undefined,
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
      this.ids = selection.map(item => item.number)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加银联分销订单卡券";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const number = row.number || this.ids
      getOrderUnionSend(number).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改银联分销订单卡券";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.number != null) {
            updateOrderUnionSend(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addOrderUnionSend(this.form).then(response => {
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
      const numbers = row.number || this.ids;
      this.$modal.confirm('是否确认删除银联分销订单卡券编号为"' + numbers + '"的数据项？').then(() => {
        this.loading = true;
        return delOrderUnionSend(numbers);
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
      this.download('zlyyh-admin/orderUnionSend/export', {
        ...this.queryParams
      }, `orderUnionSend_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
