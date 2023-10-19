<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务" prop="missionId">
        <el-select v-model="queryParams.missionId" placeholder="请选择任务" filterable clearable>
          <el-option v-for="item in missionList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="被邀用户" prop="inviteUserId">
        <el-input v-model="queryParams.inviteUserId" placeholder="请输入被邀请用户ID" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单号" prop="number">
        <el-input v-model="queryParams.number" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:inviteUserLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="inviteUserLogList" @selection-change="handleSelectionChange">
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="客户端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type" :value="scope.row.supportChannel" />
        </template>
      </el-table-column>
      <el-table-column label="任务" align="center" prop="missionId" :formatter="formatterMission" />
      <el-table-column label="用户ID" align="center" prop="userId" width="180" />
      <el-table-column label="被邀用户ID" align="center" prop="inviteUserId" width="180" />
      <el-table-column label="被邀用户城市" align="center" prop="inviteCityName" />
      <el-table-column label="行政区号" align="center" prop="inviteCityCode" width="88" />
      <el-table-column label="订单号" align="center" prop="number" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
  import {
    listInviteUserLog,
    getInviteUserLog,
    delInviteUserLog,
    addInviteUserLog,
    updateInviteUserLog
  } from "@/api/zlyyh/inviteUserLog";

  import {
    listMissionSelect
  } from "@/api/zlyyh/mission"

  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";

  export default {
    name: "InviteUserLog",
    dicts: ['channel_type'],
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
        // 邀请记录表格数据
        inviteUserLogList: [],
        missionList: [],
        //平台下拉列表
        platformList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'id',
          isAsc: 'desc',
          userId: undefined,
          inviteUserId: undefined,
          inviteCityName: undefined,
          number: undefined,
          missionId: undefined,
          platformKey: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {}
      };
    },
    created() {
      this.getList();
      listMissionSelect({}).then(res => {
        this.missionList = res.data;
      })
      this.getPlatformSelectList();
    },
    methods: {
      //平台下拉列表
      getPlatformSelectList() {
        selectListPlatform({}).then(response => {
          this.platformList = response.data;
        });
      },
      platformFormatter(row) {
        let name = '';
        this.platformList.forEach(item => {
          if (item.id == row.platformKey) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.platformName = name;
          return name;
        }
        return row.platformKey;
      },
      formatterMission(row) {
        let name = ''
        this.missionList.forEach(item => {
          if (row.missionId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.name = name;
          return name;
        }
        return row.missionId;
      },
      /** 查询邀请记录列表 */
      getList() {
        this.loading = true;
        listInviteUserLog(this.queryParams).then(response => {
          this.inviteUserLogList = response.rows;
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
          userId: undefined,
          inviteUserId: undefined,
          inviteCityName: undefined,
          inviteCityCode: undefined,
          number: undefined,
          missionId: undefined,
          platformKey: undefined,
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
        this.title = "添加邀请记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getInviteUserLog(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改邀请记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateInviteUserLog(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addInviteUserLog(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除邀请记录编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delInviteUserLog(ids);
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
        this.download('zlyyh-admin/inviteUserLog/export', {
          ...this.queryParams
        }, `inviteUserLog_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
