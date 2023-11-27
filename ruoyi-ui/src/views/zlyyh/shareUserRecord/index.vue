<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分销员" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID或手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="被分销用户" prop="inviteeUserId" label-width="100">
        <el-input v-model="queryParams.inviteeUserId" placeholder="请输入用户ID或手机号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单号" prop="number">
        <el-input v-model="queryParams.number" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="分销状态" prop="inviteeStatus">
        <el-select v-model="queryParams.inviteeStatus" placeholder="请选择分销状态" clearable>
          <el-option v-for="dict in dict.type.invitee_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="奖励状态" prop="awardStatus">
        <el-select v-model="queryParams.awardStatus" placeholder="请选择奖励状态" clearable>
          <el-option v-for="dict in dict.type.award_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="daterangeCreateTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:shareUserRecord:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:shareUserRecord:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:shareUserRecord:remove']">删除</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:shareUserRecord:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shareUserRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="recordId" v-if="false" />
      <el-table-column label="分销员" align="center" prop="userId" width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.userId }}</span><br>
          <span v-if="scope.row.userMobile">手机号：{{ scope.row.userMobile }}</span>
        </template>
      </el-table-column>
      <el-table-column label="被分销用户" align="center" prop="inviteeUserId" width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.inviteeUserId }}</span><br>
          <span v-if="scope.row.inviteeUserMobile">手机号：{{ scope.row.inviteeUserMobile }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" prop="productName" :show-overflow-tooltip="true" width="180" />
      <el-table-column label="订单号" align="center" prop="number" width="180" />
      <el-table-column label="奖励金额" align="center" prop="awardAmount" />
      <el-table-column label="分销状态" align="center" prop="inviteeStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.invitee_status" :value="scope.row.inviteeStatus" />
        </template>
      </el-table-column>
      <el-table-column label="奖励状态" align="center" prop="awardStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.award_status" :value="scope.row.awardStatus" />
        </template>
      </el-table-column>
      <el-table-column label="核销时间" align="center" prop="orderUsedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderUsedTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="奖励时间" align="center" prop="awardTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发放账号" align="center" prop="awardAccount" width="110" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" fixed="right">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shareUserRecord:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:shareUserRecord:remove']">删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改分销记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分销员用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入分销员用户ID" />
        </el-form-item>
        <el-form-item label="被分销用户ID" prop="inviteeUserId">
          <el-input v-model="form.inviteeUserId" placeholder="请输入被分销用户ID" />
        </el-form-item>
        <el-form-item label="订单号" prop="number">
          <el-input v-model="form.number" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="订单核销时间" prop="orderUsedTime">
          <el-date-picker clearable v-model="form.orderUsedTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择订单核销时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="奖励金额" prop="awardAmount">
          <el-input v-model="form.awardAmount" placeholder="请输入奖励金额" />
        </el-form-item>
        <el-form-item label="分销状态" prop="inviteeStatus">
          <el-radio-group v-model="form.inviteeStatus">
            <el-radio v-for="dict in dict.type.invitee_status" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="奖励状态" prop="awardStatus">
          <el-radio-group v-model="form.awardStatus">
            <el-radio v-for="dict in dict.type.award_status" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="奖励时间" prop="awardTime">
          <el-date-picker clearable v-model="form.awardTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择奖励时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="奖励发放账号" prop="awardAccount">
          <el-input v-model="form.awardAccount" placeholder="请输入奖励发放账号" />
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
    listShareUserRecord,
    getShareUserRecord,
    delShareUserRecord,
    addShareUserRecord,
    updateShareUserRecord
  } from "@/api/zlyyh/shareUserRecord";

  export default {
    name: "ShareUserRecord",
    dicts: ['invitee_status', 'award_status'],
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
        // 分销记录表格数据
        shareUserRecordList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 奖励发放账号时间范围
        daterangeCreateTime: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'recordId',
          isAsc: 'desc',
          userId: undefined,
          inviteeUserId: undefined,
          number: undefined,
          orderUsedTime: undefined,
          awardAmount: undefined,
          inviteeStatus: undefined,
          awardStatus: undefined,
          awardTime: undefined,
          awardAccount: undefined,
          createTime: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          recordId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          userId: [{
            required: true,
            message: "分销员用户ID不能为空",
            trigger: "blur"
          }],
          inviteeUserId: [{
            required: true,
            message: "被分销用户ID不能为空",
            trigger: "blur"
          }],
          number: [{
            required: true,
            message: "订单号不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询分销记录列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
          this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
          this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
        }
        listShareUserRecord(this.queryParams).then(response => {
          this.shareUserRecordList = response.rows;
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
          recordId: undefined,
          userId: undefined,
          inviteeUserId: undefined,
          number: undefined,
          orderUsedTime: undefined,
          awardAmount: undefined,
          inviteeStatus: undefined,
          awardStatus: undefined,
          awardTime: undefined,
          awardAccount: undefined,
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
        this.daterangeCreateTime = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.recordId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加分销记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const recordId = row.recordId || this.ids
        getShareUserRecord(recordId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改分销记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.recordId != null) {
              updateShareUserRecord(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addShareUserRecord(this.form).then(response => {
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
        const recordIds = row.recordId || this.ids;
        this.$modal.confirm('是否确认删除分销记录编号为"' + recordIds + '"的数据项？').then(() => {
          this.loading = true;
          return delShareUserRecord(recordIds);
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
        this.download('zlyyh/shareUserRecord/export', {
          ...this.queryParams
        }, `shareUserRecord_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
