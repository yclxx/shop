<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务" prop="upMissionId">
        <el-select v-model="queryParams.upMissionId" placeholder="请选择任务" filterable clearable style="width: 100%;">
          <el-option v-for="item in missionList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务组" prop="upMissionGroupId">
        <el-select v-model="queryParams.upMissionGroupId" placeholder="请选择任务组" filterable clearable
          style="width: 100%;">
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户电话" prop="upMissionUserId">
        <el-input v-model="queryParams.upMissionUserId" placeholder="请输入银联任务用户电话" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="当日进度" prop="dayProgress">
        <el-input v-model="queryParams.dayProgress" placeholder="请输入当日进度" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="本周进度" prop="weekProgress">
        <el-input v-model="queryParams.weekProgress" placeholder="请输入本周进度" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="本月进度" prop="monthProgress">
        <el-input v-model="queryParams.monthProgress" placeholder="请输入本月进度" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="活动总进度" prop="activityProgress">
        <el-input v-model="queryParams.activityProgress" placeholder="请输入活动总进度" clearable
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
          v-hasPermi="['zlyyh:unionpayMissionProgress:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:unionpayMissionProgress:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:unionpayMissionProgress:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:unionpayMissionProgress:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unionpayMissionProgressList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="任务进度ID" align="center" prop="progressId" v-if="true" /> -->
      <el-table-column label="任务" align="center" prop="upMissionId" :formatter="changeMission" />
      <el-table-column label="任务组" align="center" prop="upMissionGroupId" :formatter="changeMissionGroup" />
      <el-table-column label="任务用户" align="center" prop="userVo.mobile" />
      <el-table-column label="当日进度" align="center" prop="dayProgress" />
      <el-table-column label="本周进度" align="center" prop="weekProgress" />
      <el-table-column label="本月进度" align="center" prop="monthProgress" />
      <el-table-column label="活动总进度" align="center" prop="activityProgress" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionProgress:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionProgress:remove']">删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改银联任务进度对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="银联任务ID" prop="upMissionId">
          <el-input v-model="form.upMissionId" placeholder="请输入银联任务ID" />
        </el-form-item>
        <el-form-item label="银联任务组ID" prop="upMissionGroupId">
          <el-input v-model="form.upMissionGroupId" placeholder="请输入银联任务组ID" />
        </el-form-item>
        <el-form-item label="银联任务用户id" prop="upMissionUserId">
          <el-input v-model="form.upMissionUserId" placeholder="请输入银联任务用户id" />
        </el-form-item>
        <el-form-item label="当日进度" prop="dayProgress">
          <el-input v-model="form.dayProgress" placeholder="请输入当日进度" />
        </el-form-item>
        <el-form-item label="本周进度" prop="weekProgress">
          <el-input v-model="form.weekProgress" placeholder="请输入本周进度" />
        </el-form-item>
        <el-form-item label="本月进度" prop="monthProgress">
          <el-input v-model="form.monthProgress" placeholder="请输入本月进度" />
        </el-form-item>
        <el-form-item label="活动总进度" prop="activityProgress">
          <el-input v-model="form.activityProgress" placeholder="请输入活动总进度" />
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
    listUnionpayMissionProgress,
    getUnionpayMissionProgress,
    delUnionpayMissionProgress,
    addUnionpayMissionProgress,
    updateUnionpayMissionProgress
  } from "@/api/zlyyh/unionpayMissionProgress";
  import {
    selectListMissionGroup
  } from "@/api/zlyyh/unionpayMissionGroup";
  import {
    selectMissionList
  } from "@/api/zlyyh/unionpayMission";

  export default {
    name: "UnionpayMissionProgress",
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
        // 银联任务进度表格数据
        unionpayMissionProgressList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          upMissionId: undefined,
          upMissionGroupId: undefined,
          upMissionUserId: undefined,
          dayProgress: undefined,
          weekProgress: undefined,
          monthProgress: undefined,
          activityProgress: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc',
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          progressId: [{
            required: true,
            message: "任务进度ID不能为空",
            trigger: "blur"
          }],
          upMissionId: [{
            required: true,
            message: "银联任务ID不能为空",
            trigger: "blur"
          }],
          upMissionGroupId: [{
            required: true,
            message: "银联任务组ID不能为空",
            trigger: "blur"
          }],
          upMissionUserId: [{
            required: true,
            message: "银联任务用户id不能为空",
            trigger: "blur"
          }],
          dayProgress: [{
            required: true,
            message: "当日进度不能为空",
            trigger: "blur"
          }],
          weekProgress: [{
            required: true,
            message: "本周进度不能为空",
            trigger: "blur"
          }],
          monthProgress: [{
            required: true,
            message: "本月进度不能为空",
            trigger: "blur"
          }],
          activityProgress: [{
            required: true,
            message: "活动总进度不能为空",
            trigger: "blur"
          }],
        },
        missionGroupList: [],
        missionList: [],
      };
    },
    created() {
      this.getList();
      this.getMissionGroupList();
      this.getMissionList();
    },
    methods: {
      /** 查询银联任务进度列表 */
      getList() {
        this.loading = true;
        listUnionpayMissionProgress(this.queryParams).then(response => {
          this.unionpayMissionProgressList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      //任务组下拉列表查询
      getMissionGroupList() {
        selectListMissionGroup({}).then(res => {
          this.missionGroupList = res.data;
        })
      },
      //任务下拉列表查询
      getMissionList() {
        selectMissionList({}).then(res => {
          this.missionList = res.data;
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
          progressId: undefined,
          upMissionId: undefined,
          upMissionGroupId: undefined,
          upMissionUserId: undefined,
          dayProgress: undefined,
          weekProgress: undefined,
          monthProgress: undefined,
          activityProgress: undefined,
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
        this.ids = selection.map(item => item.progressId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加银联任务进度";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const progressId = row.progressId || this.ids
        getUnionpayMissionProgress(progressId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改银联任务进度";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.progressId != null) {
              updateUnionpayMissionProgress(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addUnionpayMissionProgress(this.form).then(response => {
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
        const progressIds = row.progressId || this.ids;
        this.$modal.confirm('是否确认删除银联任务进度编号为"' + progressIds + '"的数据项？').then(() => {
          this.loading = true;
          return delUnionpayMissionProgress(progressIds);
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
        this.download('zlyyh/unionpayMissionProgress/export', {
          ...this.queryParams
        }, `unionpayMissionProgress_${new Date().getTime()}.xlsx`)
      },
      //任务组转换
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
      //任务转换
      changeMission(row) {
        let missiomName = ''
        this.missionList.forEach(item => {
          if (row.upMissionId == item.id) {
            missiomName = item.label;
          }
        })
        if (missiomName && missiomName.length > 0) {
          row.missiomName = missiomName;
          return missiomName;
        }
        return row.upMissionId;
      },
    }
  };
</script>