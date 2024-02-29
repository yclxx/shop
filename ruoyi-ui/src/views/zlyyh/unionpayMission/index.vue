<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="upMissionGroupId">
        <el-select v-model="queryParams.upMissionGroupId" placeholder="请选择任务组" filterable clearable
          style="width: 100%;">
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务名称" prop="upMissionName">
        <el-input v-model="queryParams.upMissionName" placeholder="请输入银联任务名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="奖励产品ID" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入发放奖励产品Id" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="开始时间" prop="startDate">
        <el-date-picker clearable v-model="queryParams.startDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate">
        <el-date-picker clearable v-model="queryParams.endDate" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item> -->
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="用户每日限参与次数" prop="userCountDay">
        <el-input v-model="queryParams.userCountDay" placeholder="请输入用户每日限参与次数" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户每周限参与次数" prop="userCountWeek">
        <el-input v-model="queryParams.userCountWeek" placeholder="请输入用户每周限参与次数" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户每月限参与次数" prop="userCountMonth">
        <el-input v-model="queryParams.userCountMonth" placeholder="请输入用户每月限参与次数" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户活动期间限参与次数" prop="userCountActivity">
        <el-input v-model="queryParams.userCountActivity" placeholder="请输入用户活动期间限参与次数" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:unionpayMission:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:unionpayMission:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:unionpayMission:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:unionpayMission:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unionpayMissionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="ID" align="center" prop="upMissionId" v-if="true" /> -->
      <el-table-column label="任务组" align="center" prop="upMissionGroupId" width="100" :formatter="changeMissionGroup" />
      <el-table-column label="任务名称" align="center" prop="upMissionName" width="100" />
      <el-table-column label="任务编号" align="center" prop="upMissionUpid" width="130" />
      <el-table-column label="发放奖励" align="center" prop="productVo.productName" width="125" />
      <el-table-column label="开始时间" align="center" prop="startDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="每日限参与次数" align="center" prop="userCountDay" />
      <el-table-column label="每周限参与次数" align="center" prop="userCountWeek" />
      <el-table-column label="每月限参与次数" align="center" prop="userCountMonth" />
      <el-table-column label="限总参与次数" align="center" prop="userCountActivity" />
      <el-table-column label="交易规则" align="left" prop="tranType" width="130">
        <template slot-scope="scope">
          <div style="display: flex;">类型：<dict-tag :options="dict.type.t_upmission_tran_type"
              :value="scope.row.tranType" /></div>
          <div v-if="scope.row.tranType == 1">限制笔数：{{scope.row.tranCount}}</div>
          <div v-if="scope.row.tranType == 2">限制金额：{{(scope.row.tranCount / 100).toFixed(2)}}</div>
          <div>单笔金额：{{(scope.row.payAmount / 100).toFixed(2)}}</div>
        </template>
      </el-table-column>
      <el-table-column label="规则说明" align="center" prop="remark" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:unionpayMission:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:unionpayMission:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改银联任务配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="950px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="115px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="任务组" prop="upMissionGroupId">
              <el-select v-model="form.upMissionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
                <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id"
                  :label="item.label"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务名称" prop="upMissionName">
              <el-input v-model="form.upMissionName" placeholder="请输入银联任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务编号" prop="upMissionUpid">
              <span slot="label">
                任务编号
                <el-tooltip content="银联任务编号" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.upMissionUpid" placeholder="请输入银联任务编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发放奖励" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入发放奖励产品id" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开始时间" prop="startDate">
              <el-date-picker clearable v-model="form.startDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择开始时间" style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束时间" prop="endDate">
              <el-date-picker clearable v-model="form.endDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择结束时间" style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每日参与次数" prop="userCountDay">
              <span slot="label">
                每日参与次数
                <el-tooltip content="'0'为不限" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountDay" placeholder="请输入用户每日限参与次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每周参与次数" prop="userCountWeek">
              <span slot="label">
                每周参与次数
                <el-tooltip content="'0'为不限" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountWeek" placeholder="请输入用户每周限参与次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每月参与次数" prop="userCountMonth">
              <span slot="label">
                每月参与次数
                <el-tooltip content="'0'为不限" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountMonth" placeholder="请输入用户每月限参与次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总参与次数" prop="userCountActivity">
              <span slot="label">
                总参与次数
                <el-tooltip content="'0'为不限" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountActivity" placeholder="请输入用户活动期间限参与次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="交易类型" prop="tranType">
              <el-radio-group v-model="form.tranType">
                <el-radio v-for="dict in dict.type.t_upmission_tran_type" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="交易限制" prop="tranCount">
              <el-input v-model="form.tranCount" placeholder="请输入交易限制" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单笔限制金额" prop="payAmount">
              <el-input v-model="form.payAmount" placeholder="请输入单笔限制金额" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规则说明" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入规则说明" />
            </el-form-item>
          </el-col>
        </el-row>
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
    listUnionpayMission,
    getUnionpayMission,
    delUnionpayMission,
    addUnionpayMission,
    updateUnionpayMission
  } from "@/api/zlyyh/unionpayMission";
  import {
    selectListMissionGroup
  } from "@/api/zlyyh/unionpayMissionGroup";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "UnionpayMission",
    dicts: ['sys_normal_disable', 't_upmission_tran_type'],
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
        // 银联任务配置表格数据
        unionpayMissionList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          upMissionGroupId: undefined,
          upMissionName: undefined,
          productId: undefined,
          status: undefined,
          startDate: undefined,
          endDate: undefined,
          platformKey: undefined,
          userCountDay: undefined,
          userCountWeek: undefined,
          userCountMonth: undefined,
          userCountActivity: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc',
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          upMissionId: [{
            required: true,
            message: "银联任务ID不能为空",
            trigger: "blur"
          }],
          upMissionGroupId: [{
            required: true,
            message: "任务组不能为空",
            trigger: "blur"
          }],
          upMissionName: [{
            required: true,
            message: "任务名称不能为空",
            trigger: "blur"
          }],
          upMissionUpid: [{
            required: true,
            message: "任务编号不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "发放奖励产品id不能为空",
            trigger: "blur"
          }],
          startDate: [{
            required: true,
            message: "开始时间不能为空",
            trigger: "blur"
          }],
          endDate: [{
            required: true,
            message: "结束时间不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }],
          // userCountDay: [{
          //   required: true,
          //   message: "用户每日限参与次数不能为空",
          //   trigger: "blur"
          // }],
          // userCountWeek: [{
          //   required: true,
          //   message: "用户每周限参与次数不能为空",
          //   trigger: "blur"
          // }],
          // userCountMonth: [{
          //   required: true,
          //   message: "用户每月限参与次数不能为空",
          //   trigger: "blur"
          // }],
          // userCountActivity: [{
          //   required: true,
          //   message: "用户活动期间限参与次数不能为空",
          //   trigger: "blur"
          // }],
        },
        platformList: [],
        missionGroupList: [],
      };
    },
    created() {
      this.getList();
      this.getMissionGroupList();
      this.getPlatFormList();
    },
    methods: {
      /** 查询银联任务配置列表 */
      getList() {
        this.loading = true;
        listUnionpayMission(this.queryParams).then(response => {
          this.unionpayMissionList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      //平台下拉列表查询
      getPlatFormList() {
        selectListPlatform({}).then(res => {
          this.platformList = res.data;
        })
      },
      //任务组下拉列表查询
      getMissionGroupList() {
        selectListMissionGroup({}).then(res => {
          this.missionGroupList = res.data;
        })
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          upMissionId: undefined,
          upMissionGroupId: undefined,
          upMissionName: undefined,
          productId: undefined,
          status: '0',
          tranType: '1',
          startDate: undefined,
          endDate: undefined,
          platformKey: undefined,
          userCountDay: undefined,
          userCountWeek: undefined,
          userCountMonth: undefined,
          userCountActivity: undefined,
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
        this.ids = selection.map(item => item.upMissionId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加银联任务配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const upMissionId = row.upMissionId || this.ids
        getUnionpayMission(upMissionId).then(response => {
          this.loading = false;
          this.form = response.data;
          if (this.form.tranType == 2) {
            this.form.tranCount = Number(this.form.tranCount / 100).toFixed(2);
          }
          this.form.payAmount = (this.form.payAmount / 100).toFixed(2);
          this.open = true;
          this.title = "修改银联任务配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.tranType == 2) {
              this.form.tranCount = (this.form.tranCount * 100).toFixed(0);
            }
            if (this.form.payAmount) {
              this.form.payAmount = (this.form.payAmount * 100).toFixed(0);
            }
            if (this.form.upMissionId != null) {
              updateUnionpayMission(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addUnionpayMission(this.form).then(response => {
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
        const upMissionIds = row.upMissionId || this.ids;
        this.$modal.confirm('是否确认删除银联任务配置编号为"' + upMissionIds + '"的数据项？').then(() => {
          this.loading = true;
          return delUnionpayMission(upMissionIds);
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
        this.download('zlyyh/unionpayMission/export', {
          ...this.queryParams
        }, `unionpayMission_${new Date().getTime()}.xlsx`)
      },
      changePlatform(row) {
        let platformName = ''
        this.platformList.forEach(item => {
          if (row.platformKey == item.id) {
            platformName = item.label;
          }
        })
        if (platformName && platformName.length > 0) {
          row.platformName = platformName;
          return platformName;
        }
        return row.platformKey;
      },
      changeMissionGroup(row) {
        let groupName = ''
        this.missionGroupList.forEach(item => {
          if (row.upMissionGroupId == item.id) {
            groupName = item.label;
          }
        })
        if (groupName && groupName.length > 0) {
          row.groupName = groupName;
          return groupName;
        }
        return row.upMissionGroupId;
      },
    }
  };
</script>
<style scoped>
  ::v-deep .el-form-item--small .el-form-item__label {
    white-space: nowrap;
    width: auto !important;
  }
</style>