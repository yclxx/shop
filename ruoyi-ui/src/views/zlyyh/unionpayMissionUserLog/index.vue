<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="银联任务用户ID" prop="upMissionUserId">
        <el-input v-model="queryParams.upMissionUserId" placeholder="请输入银联任务用户ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="银联任务组ID" prop="upMissionGroupId">
        <el-input v-model="queryParams.upMissionGroupId" placeholder="请输入银联任务组ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="银联任务ID" prop="upMissionId">
        <el-input v-model="queryParams.upMissionId" placeholder="请输入银联任务ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="奖励产品ID" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入奖励产品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态  0-未发放  1-发放中  2-发放成功  3-发放失败" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态  0-未发放  1-发放中  2-发放成功  3-发放失败" clearable>
          <el-option
            v-for="dict in dict.type.t_unionpay_mission_log_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发放账号" prop="account">
        <el-input
          v-model="queryParams.account"
          placeholder="请输入发放账号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发放时间" prop="sendTime">
        <el-date-picker clearable
          v-model="queryParams.sendTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发放时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发放数量" prop="sendCount">
        <el-input
          v-model="queryParams.sendCount"
          placeholder="请输入发放数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="失败原因" prop="failReason">
        <el-input
          v-model="queryParams.failReason"
          placeholder="请输入失败原因"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:unionpayMissionUserLog:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:unionpayMissionUserLog:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:unionpayMissionUserLog:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:unionpayMissionUserLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unionpayMissionUserLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="奖励记录ID" align="center" prop="upMissionUserLog" v-if="true" />
      <el-table-column label="银联任务用户ID" align="center" prop="upMissionUserId" />
      <el-table-column label="银联任务组ID" align="center" prop="upMissionGroupId" />
      <el-table-column label="银联任务ID" align="center" prop="upMissionId" />
      <!-- <el-table-column label="奖励产品ID" align="center" prop="productId" /> -->
      <el-table-column label="订单号" align="center" prop="number" />
      <!-- <el-table-column label="状态  0-未发放  1-发放中  2-发放成功  3-发放失败" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_unionpay_mission_log_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="发放账号" align="center" prop="account" />
      <el-table-column label="发放时间" align="center" prop="sendTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="发放数量" align="center" prop="sendCount" />
      <el-table-column label="失败原因" align="center" prop="failReason" /> -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionUserLog:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionUserLog:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改银联任务奖励发放记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="银联任务用户ID" prop="upMissionUserId">
          <el-input v-model="form.upMissionUserId" placeholder="请输入银联任务用户ID" />
        </el-form-item>
        <el-form-item label="银联任务组ID" prop="upMissionGroupId">
          <el-input v-model="form.upMissionGroupId" placeholder="请输入银联任务组ID" />
        </el-form-item>
        <el-form-item label="银联任务ID" prop="upMissionId">
          <el-input v-model="form.upMissionId" placeholder="请输入银联任务ID" />
        </el-form-item>
        <!-- <el-form-item label="奖励产品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入奖励产品ID" />
        </el-form-item> -->
        <el-form-item label="订单号" prop="productId">
          <el-input v-model="form.number" placeholder="请输入订单号" />
        </el-form-item>
        <!-- <el-form-item label="状态  0-未发放  1-发放中  2-发放成功  3-发放失败" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.t_unionpay_mission_log_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发放账号" prop="account">
          <el-input v-model="form.account" placeholder="请输入发放账号" />
        </el-form-item>
        <el-form-item label="发放时间" prop="sendTime">
          <el-date-picker clearable
            v-model="form.sendTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择发放时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="发放数量" prop="sendCount">
          <el-input v-model="form.sendCount" placeholder="请输入发放数量" />
        </el-form-item>
        <el-form-item label="失败原因" prop="failReason">
          <el-input v-model="form.failReason" placeholder="请输入失败原因" />
        </el-form-item> -->
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
    listUnionpayMissionUserLog,
    getUnionpayMissionUserLog,
    delUnionpayMissionUserLog,
    addUnionpayMissionUserLog,
    updateUnionpayMissionUserLog
  } from "@/api/zlyyh/unionpayMissionUserLog";

  export default {
    name: "UnionpayMissionUserLog",
    dicts: ['t_unionpay_mission_log_status'],
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
        // 银联任务奖励发放记录表格数据
        unionpayMissionUserLogList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          upMissionUserId: undefined,
          upMissionGroupId: undefined,
          upMissionId: undefined,
          productId: undefined,
          status: undefined,
          account: undefined,
          sendTime: undefined,
          amount: undefined,
          sendCount: undefined,
          failReason: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          upMissionUserLog: [{
            required: true,
            message: "奖励记录ID不能为空",
            trigger: "blur"
          }],
          upMissionUserId: [{
            required: true,
            message: "银联任务用户ID不能为空",
            trigger: "blur"
          }],
          upMissionGroupId: [{
            required: true,
            message: "银联任务组ID不能为空",
            trigger: "blur"
          }],
          upMissionId: [{
            required: true,
            message: "银联任务ID不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "奖励产品ID不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态  0-未发放  1-发放中  2-发放成功  3-发放失败不能为空",
            trigger: "change"
          }],
          account: [{
            required: true,
            message: "发放账号不能为空",
            trigger: "blur"
          }],
          sendTime: [{
            required: true,
            message: "发放时间不能为空",
            trigger: "blur"
          }],
          amount: [{
            required: true,
            message: "金额不能为空",
            trigger: "blur"
          }],
          sendCount: [{
            required: true,
            message: "发放数量不能为空",
            trigger: "blur"
          }],
          failReason: [{
            required: true,
            message: "失败原因不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询银联任务奖励发放记录列表 */
      getList() {
        this.loading = true;
        listUnionpayMissionUserLog(this.queryParams).then(response => {
          this.unionpayMissionUserLogList = response.rows;
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
          upMissionUserLog: undefined,
          upMissionUserId: undefined,
          upMissionGroupId: undefined,
          upMissionId: undefined,
          productId: undefined,
          status: undefined,
          account: undefined,
          sendTime: undefined,
          amount: undefined,
          sendCount: undefined,
          failReason: undefined,
          delFlag: undefined,
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
        this.ids = selection.map(item => item.upMissionUserLog)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加银联任务奖励发放记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const upMissionUserLog = row.upMissionUserLog || this.ids
        getUnionpayMissionUserLog(upMissionUserLog).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改银联任务奖励发放记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.upMissionUserLog != null) {
              updateUnionpayMissionUserLog(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addUnionpayMissionUserLog(this.form).then(response => {
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
        const upMissionUserLogs = row.upMissionUserLog || this.ids;
        this.$modal.confirm('是否确认删除银联任务奖励发放记录编号为"' + upMissionUserLogs + '"的数据项？').then(() => {
          this.loading = true;
          return delUnionpayMissionUserLog(upMissionUserLogs);
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
        this.download('zlyyh/unionpayMissionUserLog/export', {
          ...this.queryParams
        }, `unionpayMissionUserLog_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>