<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="number">
        <el-input v-model="queryParams.number" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="退款类型" prop="pickupMethod">
        <el-select v-model="queryParams.pickupMethod" placeholder="请选择退款类型" clearable>
          <el-option v-for="dict in dict.type.t_pickup_method" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="交易金额" prop="refund">
        <el-input
          v-model="queryParams.refund"
          placeholder="请输入交易金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="第三方退款订单号" prop="refundId">
        <el-input
          v-model="queryParams.refundId"
          placeholder="请输入第三方退款订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="退款渠道" prop="channel">
        <el-select v-model="queryParams.channel" placeholder="请选择退款渠道" clearable>
          <el-option v-for="dict in dict.type.t_order_back_trans_channel" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="取当前退款单的退款入账方，有以下几种情况：1）退回银行卡：{银行名称}{卡类型}{卡尾号}2）退回支付用户零钱:支付用户零钱3）退还商户:商户基本账户商户结算银行账户4）退回支付用户零钱通:支付用户零钱通" prop="userReceivedAccount">
        <el-input
          v-model="queryParams.userReceivedAccount"
          placeholder="请输入取当前退款单的退款入账方，有以下几种情况：1）退回银行卡：{银行名称}{卡类型}{卡尾号}2）退回支付用户零钱:支付用户零钱3）退还商户:商户基本账户商户结算银行账户4）退回支付用户零钱通:支付用户零钱通"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="订单发送时间" prop="txnTime">
        <el-input
          v-model="queryParams.txnTime"
          placeholder="请输入订单发送时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易传输时间" prop="traceTime">
        <el-input
          v-model="queryParams.traceTime"
          placeholder="请输入交易传输时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="交易流水号" prop="queryId">
        <el-input
          v-model="queryParams.queryId"
          placeholder="请输入退货的交易流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="原始消费交易的queryId" prop="origQryId">
        <el-input
          v-model="queryParams.origQryId"
          placeholder="请输入原始消费交易的queryId"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="系统跟踪号" prop="traceNo">
        <el-input
          v-model="queryParams.traceNo"
          placeholder="请输入系统跟踪号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="退款成功时间" prop="successTime">
        <el-date-picker clearable
          v-model="queryParams.successTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择退款成功时间">
        </el-date-picker>
      </el-form-item> -->
      <el-form-item label="订单状态" prop="orderBackTransState">
        <el-select v-model="queryParams.orderBackTransState" placeholder="请选择订单状态" clearable>
          <el-option v-for="dict in dict.type.t_order_back_trans_state" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable v-model="createTime" size="small" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zlyyh:orderBackTrans:add']"
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
          v-hasPermi="['zlyyh:orderBackTrans:edit']"
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
          v-hasPermi="['zlyyh:orderBackTrans:remove']"
        >删除</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:orderBackTrans:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderBackTransList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="退货订单编号" align="center" prop="thNumber" v-if="true" width="150" />
      <el-table-column label="订单号" align="center" prop="number" width="150" />
      <el-table-column label="退款类型" align="center" prop="pickupMethod">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_pickup_method" :value="scope.row.pickupMethod" />
        </template>
      </el-table-column>
      <el-table-column label="交易金额" align="center" prop="refund" />
      <el-table-column label="第三方退款订单号" align="center" prop="refundId" width="150" />
      <el-table-column label="退款渠道" align="center" prop="channel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_back_trans_channel" :value="scope.row.channel" />
        </template>
      </el-table-column>
      <el-table-column label="退款入账方" align="center" prop="userReceivedAccount" width="150" />
      <el-table-column label="订单时间" align="left" prop="txnTime" width="250">
        <template slot-scope="scope">
          <span>订单发送时间：{{ scope.row.txnTime }}</span><br>
          <span>交易传输时间：{{ scope.row.traceTime }}</span><br>
          <span>退款成功时间：{{ parseTime(scope.row.successTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span><br>
          <span>创建时间：{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span><br>
        </template>
      </el-table-column>
      <el-table-column label="交易流水号" align="left" prop="queryId" width="250">
        <template slot-scope="scope">
          <span>交易流水号：{{ scope.row.queryId }}</span><br>
          <span>原消费交易流水号：{{ scope.row.origQryId }}</span><br>
          <span>系统跟踪号：{{ scope.row.traceNo }}</span><br>
        </template>
      </el-table-column>
      <el-table-column label="退款原因" align="center" prop="refundReason" width="130" />
      <el-table-column label="订单状态" align="center" prop="orderBackTransState" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_back_trans_state" :value="scope.row.orderBackTransState" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:orderBackTrans:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:orderBackTrans:remove']"
          >删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改退款订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单号" prop="number">
          <el-input v-model="form.number" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="退款类型：1-金额，2-积点" prop="pickupMethod">
          <el-select v-model="form.pickupMethod" placeholder="请选择退款类型：1-金额，2-积点">
            <el-option v-for="dict in dict.type.t_pickup_method" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="交易金额" prop="refund">
          <el-input v-model="form.refund" placeholder="请输入交易金额" />
        </el-form-item>
        <el-form-item label="第三方退款订单号" prop="refundId">
          <el-input v-model="form.refundId" placeholder="请输入第三方退款订单号" />
        </el-form-item>
        <el-form-item label="退款渠道,ORIGINAL：原路退款；BALANCE：退回到余额；OTHER_BALANCE：原账户异常退到其他余额账户；OTHER_BANKCARD：原银行卡异常退到其他银行卡"
          prop="channel">
          <el-select v-model="form.channel"
            placeholder="请选择退款渠道,ORIGINAL：原路退款；BALANCE：退回到余额；OTHER_BALANCE：原账户异常退到其他余额账户；OTHER_BANKCARD：原银行卡异常退到其他银行卡">
            <el-option v-for="dict in dict.type.t_order_back_trans_channel" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="取当前退款单的退款入账方，有以下几种情况：1）退回银行卡：{银行名称}{卡类型}{卡尾号}2）退回支付用户零钱:支付用户零钱3）退还商户:商户基本账户商户结算银行账户4）退回支付用户零钱通:支付用户零钱通"
          prop="userReceivedAccount">
          <el-input v-model="form.userReceivedAccount"
            placeholder="请输入取当前退款单的退款入账方，有以下几种情况：1）退回银行卡：{银行名称}{卡类型}{卡尾号}2）退回支付用户零钱:支付用户零钱3）退还商户:商户基本账户商户结算银行账户4）退回支付用户零钱通:支付用户零钱通" />
        </el-form-item>
        <el-form-item label="订单发送时间" prop="txnTime">
          <el-input v-model="form.txnTime" placeholder="请输入订单发送时间" />
        </el-form-item>
        <el-form-item label="交易传输时间" prop="traceTime">
          <el-input v-model="form.traceTime" placeholder="请输入交易传输时间" />
        </el-form-item>
        <el-form-item label="退货交易的交易流水号，供查询用" prop="queryId">
          <el-input v-model="form.queryId" placeholder="请输入退货交易的交易流水号，供查询用" />
        </el-form-item>
        <el-form-item label="原始消费交易的queryId" prop="origQryId">
          <el-input v-model="form.origQryId" placeholder="请输入原始消费交易的queryId" />
        </el-form-item>
        <el-form-item label="系统跟踪号" prop="traceNo">
          <el-input v-model="form.traceNo" placeholder="请输入系统跟踪号" />
        </el-form-item>
        <el-form-item label="退款成功时间" prop="successTime">
          <el-date-picker clearable v-model="form.successTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择退款成功时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="订单状态 0-退款中 1-退款失败 2-退款成功" prop="orderBackTransState">
          <el-select v-model="form.orderBackTransState" placeholder="请选择订单状态 0-退款中 1-退款失败 2-退款成功">
            <el-option v-for="dict in dict.type.t_order_back_trans_state" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
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
    listOrderBackTrans,
    getOrderBackTrans,
    delOrderBackTrans,
    addOrderBackTrans,
    updateOrderBackTrans
  } from "@/api/zlyyh/orderBackTrans";

  export default {
    name: "OrderBackTrans",
    dicts: ['t_order_back_trans_channel', 't_order_back_trans_state', 't_pickup_method'],
    data() {
      return {
        //创建时间范围
        createTime: [],
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
        // 退款订单表格数据
        orderBackTransList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'th_number',
          isAsc: 'desc',
          number: undefined,
          pickupMethod: undefined,
          refund: undefined,
          refundId: undefined,
          channel: undefined,
          userReceivedAccount: undefined,
          txnTime: undefined,
          traceTime: undefined,
          queryId: undefined,
          origQryId: undefined,
          traceNo: undefined,
          successTime: undefined,
          orderBackTransState: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          thNumber: [{
            required: true,
            message: "退货订单编号不能为空",
            trigger: "blur"
          }],
          number: [{
            required: true,
            message: "订单号不能为空",
            trigger: "blur"
          }],
          pickupMethod: [{
            required: true,
            message: "退款类型：1-金额，2-积点不能为空",
            trigger: "change"
          }],
          refund: [{
            required: true,
            message: "交易金额不能为空",
            trigger: "blur"
          }],
          refundId: [{
            required: true,
            message: "第三方退款订单号不能为空",
            trigger: "blur"
          }],
          channel: [{
            required: true,
            message: "退款渠道,ORIGINAL：原路退款；BALANCE：退回到余额；OTHER_BALANCE：原账户异常退到其他余额账户；OTHER_BANKCARD：原银行卡异常退到其他银行卡不能为空",
            trigger: "change"
          }],
          userReceivedAccount: [{
            required: true,
            message: "取当前退款单的退款入账方，有以下几种情况：1）退回银行卡：{银行名称}{卡类型}{卡尾号}2）退回支付用户零钱:支付用户零钱3）退还商户:商户基本账户商户结算银行账户4）退回支付用户零钱通:支付用户零钱通不能为空",
            trigger: "blur"
          }],
          txnTime: [{
            required: true,
            message: "订单发送时间不能为空",
            trigger: "blur"
          }],
          traceTime: [{
            required: true,
            message: "交易传输时间不能为空",
            trigger: "blur"
          }],
          queryId: [{
            required: true,
            message: "退货交易的交易流水号，供查询用不能为空",
            trigger: "blur"
          }],
          origQryId: [{
            required: true,
            message: "原始消费交易的queryId不能为空",
            trigger: "blur"
          }],
          traceNo: [{
            required: true,
            message: "系统跟踪号不能为空",
            trigger: "blur"
          }],
          successTime: [{
            required: true,
            message: "退款成功时间不能为空",
            trigger: "blur"
          }],
          orderBackTransState: [{
            required: true,
            message: "订单状态 0-退款中 1-退款失败 2-退款成功不能为空",
            trigger: "change"
          }],
        },
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询退款订单列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.createTime && '' != this.createTime) {
          this.queryParams.params["beginCreateTime"] = this.createTime[0];
          this.queryParams.params["endCreateTime"] = this.createTime[1];
        }
        listOrderBackTrans(this.queryParams).then(response => {
          this.orderBackTransList = response.rows;
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
          thNumber: undefined,
          number: undefined,
          pickupMethod: undefined,
          refund: undefined,
          refundId: undefined,
          channel: undefined,
          userReceivedAccount: undefined,
          txnTime: undefined,
          traceTime: undefined,
          queryId: undefined,
          origQryId: undefined,
          traceNo: undefined,
          successTime: undefined,
          orderBackTransState: undefined,
          createTime: undefined,
          createBy: undefined,
          updateTime: undefined,
          updateBy: undefined
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
        this.createTime = null;
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.thNumber)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加退款订单";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const thNumber = row.thNumber || this.ids
        getOrderBackTrans(thNumber).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改退款订单";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.thNumber != null) {
              updateOrderBackTrans(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addOrderBackTrans(this.form).then(response => {
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
        const thNumbers = row.thNumber || this.ids;
        this.$modal.confirm('是否确认删除退款订单编号为"' + thNumbers + '"的数据项？').then(() => {
          this.loading = true;
          return delOrderBackTrans(thNumbers);
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
        this.download('zlyyh-admin/orderBackTrans/export', {
          ...this.queryParams
        }, `orderBackTrans_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>