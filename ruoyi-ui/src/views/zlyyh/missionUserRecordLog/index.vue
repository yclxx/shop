<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="记录ID" prop="missionUserRecordId">
        <el-input v-model="queryParams.missionUserRecordId" placeholder="请输入活动记录ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="取码单号" prop="pushNumber">
        <el-input v-model="queryParams.pushNumber" placeholder="请输入取码(充值)订单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:missionUserRecordLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionUserRecordLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" v-if="true" />
      <el-table-column label="活动记录ID" align="center" prop="missionUserRecordId" />
      <el-table-column label="取码单号" align="center" prop="pushNumber" />
      <el-table-column label="供应商单号" align="center" prop="externalOrderNumber" />
      <el-table-column label="取码编号" align="center" prop="externalProductId" />
      <el-table-column label="发放金额" align="center" prop="sendValue" />
      <el-table-column label="订单状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_push_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改活动订单取码记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="活动记录ID" prop="missionUserRecordId">
          <el-input v-model="form.missionUserRecordId" placeholder="请输入活动记录ID" />
        </el-form-item>
        <el-form-item label="取码订单号" prop="pushNumber">
          <el-input v-model="form.pushNumber" placeholder="请输入取码(充值)订单号" />
        </el-form-item>
        <el-form-item label="供应商订单号" prop="externalOrderNumber">
          <el-input v-model="form.externalOrderNumber" placeholder="请输入供应商订单号" />
        </el-form-item>
        <el-form-item label="取码提交供应商产品编号" prop="externalProductId">
          <el-input v-model="form.externalProductId" placeholder="请输入取码提交供应商产品编号" />
        </el-form-item>
        <el-form-item label="发放金额，" prop="sendValue">
          <el-input v-model="form.sendValue" placeholder="请输入发放金额，" />
        </el-form-item>
        <el-form-item label="交易失败原因" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
    listMissionUserRecordLog,
    getMissionUserRecordLog,
    delMissionUserRecordLog,
    addMissionUserRecordLog,
    updateMissionUserRecordLog
  } from "@/api/zlyyh/missionUserRecordLog";

  export default {
    name: "MissionUserRecordLog",
    dicts: ['t_order_send_status', 't_order_push_status'],
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
        // 活动订单取码记录表格数据
        missionUserRecordLogList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          missionUserRecordId: undefined,
          pushNumber: undefined,
          externalOrderNumber: undefined,
          externalProductId: undefined,
          sendValue: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询活动订单取码记录列表 */
      getList() {
        this.loading = true;
        listMissionUserRecordLog(this.queryParams).then(response => {
          this.missionUserRecordLogList = response.rows;
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
          missionUserRecordId: undefined,
          pushNumber: undefined,
          externalOrderNumber: undefined,
          externalProductId: undefined,
          sendValue: undefined,
          status: undefined,
          remark: undefined,
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
        this.title = "添加活动订单取码记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getMissionUserRecordLog(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改活动订单取码记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateMissionUserRecordLog(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMissionUserRecordLog(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除活动订单取码记录编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delMissionUserRecordLog(ids);
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
        this.download('zlyyh-admin/missionUserRecordLog/export', {
          ...this.queryParams
        }, `missionUserRecordLog_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>