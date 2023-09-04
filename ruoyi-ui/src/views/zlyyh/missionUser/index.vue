<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable>
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleQuery" />
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
          v-hasPermi="['zlyyh:missionUser:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:missionUser:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:missionUser:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:missionUser:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionUserList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务用户ID" width="190" align="center" prop="missionUserId" v-if="true" />
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="任务组" align="center" prop="missionGroupId" :formatter="formatterMissionGroup" />
      <el-table-column label="用户ID" width="190" align="center" prop="userId" />
      <el-table-column label="状态" width="68" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:missionUser:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:missionUser:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
            <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务组" prop="missionGroupId">
          <el-select v-model="form.missionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
            <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
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
    listMissionUser,
    getMissionUser,
    delMissionUser,
    addMissionUser,
    updateMissionUser
  } from "@/api/zlyyh/missionUser";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"
  import {
    listMissionGroupSelect
  } from "@/api/zlyyh/missionGroup"

  export default {
    name: "MissionUser",
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
        // 任务用户表格数据
        missionUserList: [],
        missionGroupList: [],
        platformList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'mission_user_id',
          isAsc: 'desc',
          missionGroupId: undefined,
          userId: undefined,
          platformKey: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          missionUserId: [{
            required: true,
            message: "任务用户ID不能为空",
            trigger: "blur"
          }],
          missionGroupId: [{
            required: true,
            message: "任务组ID不能为空",
            trigger: "blur"
          }],
          userId: [{
            required: true,
            message: "用户ID不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
        }
      };
    },
    created() {
      this.getList();
      selectListPlatform({}).then(res => {
        this.platformList = res.data;
      })
      listMissionGroupSelect({}).then(res => {
        this.missionGroupList = res.data;
      })
    },
    methods: {
      formatterMissionGroup(row) {
        let name = ''
        this.missionGroupList.forEach(item => {
          if (row.missionGroupId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.name = name;
          return name;
        }
        return row.missionGroupId;
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
      /** 查询任务用户列表 */
      getList() {
        this.loading = true;
        listMissionUser(this.queryParams).then(response => {
          this.missionUserList = response.rows;
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
          missionUserId: undefined,
          missionGroupId: undefined,
          userId: undefined,
          platformKey: undefined,
          status: '0',
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
        this.ids = selection.map(item => item.missionUserId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加任务用户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const missionUserId = row.missionUserId || this.ids
        getMissionUser(missionUserId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改任务用户";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.missionUserId != null) {
              updateMissionUser(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMissionUser(this.form).then(response => {
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
        const missionUserIds = row.missionUserId || this.ids;
        this.$modal.confirm('是否确认删除任务用户编号为"' + missionUserIds + '"的数据项？').then(() => {
          this.loading = true;
          return delMissionUser(missionUserIds);
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
        this.download('zlyyh-admin/missionUser/export', {
          ...this.queryParams
        }, `missionUser_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>