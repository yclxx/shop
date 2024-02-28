<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="upMissionGroupId">
        <el-select v-model="queryParams.upMissionGroupId" placeholder="请选择任务组" filterable clearable
          style="width: 100%;">
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户电话" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户电话" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
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
          v-hasPermi="['zlyyh:unionpayMissionUser:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:unionpayMissionUser:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:unionpayMissionUser:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:unionpayMissionUser:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unionpayMissionUserList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="银联任务用户ID" align="center" prop="upMissionUserId" v-if="true" /> -->
      <el-table-column label="任务组" align="center" prop="upMissionGroupId" :formatter="changeMissionGroup" />
      <el-table-column label="用户" align="center" prop="userVo.mobile" />
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="changePlatform" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionUser:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionUser:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改银联任务用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="银联任务组ID" prop="upMissionGroupId">
          <el-input v-model="form.upMissionGroupId" placeholder="请输入银联任务组ID" />
        </el-form-item>
        <el-form-item label="用户Id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户Id" />
        </el-form-item>
        <el-form-item label="平台标识" prop="platformKey">
          <el-input v-model="form.platformKey" placeholder="请输入平台标识" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
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
    listUnionpayMissionUser,
    getUnionpayMissionUser,
    delUnionpayMissionUser,
    addUnionpayMissionUser,
    updateUnionpayMissionUser
  } from "@/api/zlyyh/unionpayMissionUser";
  import {
    selectListMissionGroup
  } from "@/api/zlyyh/unionpayMissionGroup";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "UnionpayMissionUser",
    dicts: ['sys_normal_disable'],
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
        // 银联任务用户表格数据
        unionpayMissionUserList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          upMissionGroupId: undefined,
          userId: undefined,
          platformKey: undefined,
          status: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc',
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
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
          userId: [{
            required: true,
            message: "用户Id不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态  0-正常  1-停用不能为空",
            trigger: "change"
          }],
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
      /** 查询银联任务用户列表 */
      getList() {
        this.loading = true;
        listUnionpayMissionUser(this.queryParams).then(response => {
          this.unionpayMissionUserList = response.rows;
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
      //平台下拉列表查询
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
          upMissionUserId: undefined,
          upMissionGroupId: undefined,
          userId: undefined,
          platformKey: undefined,
          status: undefined,
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
        this.ids = selection.map(item => item.upMissionUserId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加银联任务用户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const upMissionUserId = row.upMissionUserId || this.ids
        getUnionpayMissionUser(upMissionUserId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改银联任务用户";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.upMissionUserId != null) {
              updateUnionpayMissionUser(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addUnionpayMissionUser(this.form).then(response => {
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
        const upMissionUserIds = row.upMissionUserId || this.ids;
        this.$modal.confirm('是否确认删除银联任务用户编号为"' + upMissionUserIds + '"的数据项？').then(() => {
          this.loading = true;
          return delUnionpayMissionUser(upMissionUserIds);
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
        this.download('zlyyh/unionpayMissionUser/export', {
          ...this.queryParams
        }, `unionpayMissionUser_${new Date().getTime()}.xlsx`)
      },
      //平台转换
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
    }
  };
</script>