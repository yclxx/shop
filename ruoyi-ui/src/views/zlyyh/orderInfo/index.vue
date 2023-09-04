<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="62会员状态：00-普通用户 01-会员用户 02-会员冻结用户 03-试用用户" prop="vip62Status">
        <el-select v-model="queryParams.vip62Status" placeholder="请选择62会员状态：00-普通用户 01-会员用户 02-会员冻结用户 03-试用用户"
          clearable>
          <el-option v-for="dict in dict.type.t_order_info_vip62_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="62会员类型 00-试用 01-月卡 02-季卡 03-年卡 04-普通用户" prop="vip62MemberType">
        <el-select v-model="queryParams.vip62MemberType" placeholder="请选择62会员类型 00-试用 01-月卡 02-季卡 03-年卡 04-普通用户"
          clearable>
          <el-option v-for="dict in dict.type.t_order_info_vip62_member_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="62会员到期时间" prop="vip62EndTime">
        <el-input v-model="queryParams.vip62EndTime" placeholder="请输入62会员到期时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="62会员首次开通时间" prop="vip62BeginTime">
        <el-input v-model="queryParams.vip62BeginTime" placeholder="请输入62会员首次开通时间" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="txnTime">
        <el-input v-model="queryParams.txnTime" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="queryId">
        <el-input v-model="queryParams.queryId" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="traceTime">
        <el-input v-model="queryParams.traceTime" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="traceNo">
        <el-input v-model="queryParams.traceNo" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="txnAmt">
        <el-input v-model="queryParams.txnAmt" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="" prop="issAddnData">
        <el-input v-model="queryParams.issAddnData" placeholder="请输入" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品快照信息json字符串" prop="commodityJson">
        <el-input v-model="queryParams.commodityJson" placeholder="请输入商品快照信息json字符串" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:orderInfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:orderInfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:orderInfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:orderInfo:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="number" v-if="true" />
      <el-table-column label="62会员状态：00-普通用户 01-会员用户 02-会员冻结用户 03-试用用户" align="center" prop="vip62Status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_info_vip62_status" :value="scope.row.vip62Status" />
        </template>
      </el-table-column>
      <el-table-column label="62会员类型 00-试用 01-月卡 02-季卡 03-年卡 04-普通用户" align="center" prop="vip62MemberType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_info_vip62_member_type" :value="scope.row.vip62MemberType" />
        </template>
      </el-table-column>
      <el-table-column label="62会员到期时间" align="center" prop="vip62EndTime" />
      <el-table-column label="62会员首次开通时间" align="center" prop="vip62BeginTime" />
      <el-table-column label="" align="center" prop="txnTime" />
      <el-table-column label="" align="center" prop="queryId" />
      <el-table-column label="" align="center" prop="traceTime" />
      <el-table-column label="" align="center" prop="traceNo" />
      <el-table-column label="" align="center" prop="txnAmt" />
      <el-table-column label="" align="center" prop="issAddnData" />
      <el-table-column label="商品快照信息json字符串" align="center" prop="commodityJson" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:orderInfo:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:orderInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改订单扩展信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="62会员状态：00-普通用户 01-会员用户 02-会员冻结用户 03-试用用户" prop="vip62Status">
          <el-select v-model="form.vip62Status" placeholder="请选择62会员状态：00-普通用户 01-会员用户 02-会员冻结用户 03-试用用户">
            <el-option v-for="dict in dict.type.t_order_info_vip62_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="62会员类型 00-试用 01-月卡 02-季卡 03-年卡 04-普通用户" prop="vip62MemberType">
          <el-select v-model="form.vip62MemberType" placeholder="请选择62会员类型 00-试用 01-月卡 02-季卡 03-年卡 04-普通用户">
            <el-option v-for="dict in dict.type.t_order_info_vip62_member_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="62会员到期时间" prop="vip62EndTime">
          <el-input v-model="form.vip62EndTime" placeholder="请输入62会员到期时间" />
        </el-form-item>
        <el-form-item label="62会员首次开通时间" prop="vip62BeginTime">
          <el-input v-model="form.vip62BeginTime" placeholder="请输入62会员首次开通时间" />
        </el-form-item>
        <el-form-item label="" prop="txnTime">
          <el-input v-model="form.txnTime" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="queryId">
          <el-input v-model="form.queryId" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="traceTime">
          <el-input v-model="form.traceTime" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="traceNo">
          <el-input v-model="form.traceNo" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="txnAmt">
          <el-input v-model="form.txnAmt" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="" prop="issAddnData">
          <el-input v-model="form.issAddnData" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="商品快照信息json字符串" prop="commodityJson">
          <el-input v-model="form.commodityJson" type="textarea" placeholder="请输入内容" />
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
    listOrderInfo,
    getOrderInfo,
    delOrderInfo,
    addOrderInfo,
    updateOrderInfo
  } from "@/api/zlyyh/orderInfo";

  export default {
    name: "OrderInfo",
    dicts: ['t_order_info_vip62_member_type', 't_order_info_vip62_status'],
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
        // 订单扩展信息表格数据
        orderInfoList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          vip62Status: undefined,
          vip62MemberType: undefined,
          vip62EndTime: undefined,
          vip62BeginTime: undefined,
          txnTime: undefined,
          queryId: undefined,
          traceTime: undefined,
          traceNo: undefined,
          txnAmt: undefined,
          issAddnData: undefined,
          commodityJson: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          number: [{
            required: true,
            message: "订单号不能为空",
            trigger: "blur"
          }],
          vip62Status: [{
            required: true,
            message: "62会员状态：00-普通用户 01-会员用户 02-会员冻结用户 03-试用用户不能为空",
            trigger: "change"
          }],
          vip62MemberType: [{
            required: true,
            message: "62会员类型 00-试用 01-月卡 02-季卡 03-年卡 04-普通用户不能为空",
            trigger: "change"
          }],
          vip62EndTime: [{
            required: true,
            message: "62会员到期时间不能为空",
            trigger: "blur"
          }],
          vip62BeginTime: [{
            required: true,
            message: "62会员首次开通时间不能为空",
            trigger: "blur"
          }],
          txnTime: [{
            required: true,
            message: "不能为空",
            trigger: "blur"
          }],
          queryId: [{
            required: true,
            message: "不能为空",
            trigger: "blur"
          }],
          traceTime: [{
            required: true,
            message: "不能为空",
            trigger: "blur"
          }],
          traceNo: [{
            required: true,
            message: "不能为空",
            trigger: "blur"
          }],
          txnAmt: [{
            required: true,
            message: "不能为空",
            trigger: "blur"
          }],
          issAddnData: [{
            required: true,
            message: "不能为空",
            trigger: "blur"
          }],
          commodityJson: [{
            required: true,
            message: "商品快照信息json字符串不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询订单扩展信息列表 */
      getList() {
        this.loading = true;
        listOrderInfo(this.queryParams).then(response => {
          this.orderInfoList = response.rows;
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
          vip62Status: undefined,
          vip62MemberType: undefined,
          vip62EndTime: undefined,
          vip62BeginTime: undefined,
          txnTime: undefined,
          queryId: undefined,
          traceTime: undefined,
          traceNo: undefined,
          txnAmt: undefined,
          issAddnData: undefined,
          commodityJson: undefined,
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
        this.ids = selection.map(item => item.number)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加订单扩展信息";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const number = row.number || this.ids
        getOrderInfo(number).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改订单扩展信息";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.number != null) {
              updateOrderInfo(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addOrderInfo(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除订单扩展信息编号为"' + numbers + '"的数据项？').then(() => {
          this.loading = true;
          return delOrderInfo(numbers);
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
        this.download('zlyyh-admin/orderInfo/export', {
          ...this.queryParams
        }, `orderInfo_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>