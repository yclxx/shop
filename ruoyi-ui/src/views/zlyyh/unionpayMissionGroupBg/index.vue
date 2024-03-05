<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="背景类型" prop="imgType">
        <el-select v-model="queryParams.imgType" placeholder="请选择背景类型" clearable>
          <el-option v-for="dict in dict.type.t_unmission_group_bg_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
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
          v-hasPermi="['zlyyh:unionpayMissionGroupBg:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:unionpayMissionGroupBg:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:unionpayMissionGroupBg:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:unionpayMissionGroupBg:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unionpayMissionGroupBgList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="任务组背景id" align="center" prop="missionBgId" v-if="true" /> -->
      <el-table-column label="任务组" align="center" prop="missionGroupId" :formatter="changeMissionGroup" />
      <el-table-column label="名称" align="center" prop="bgName" />
      <el-table-column label="背景图片" align="center" prop="bgImg">
        <template slot-scope="scope">
          <image-preview :src="scope.row.bgImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="背景类型" align="center" prop="imgType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_unmission_group_bg_type" :value="scope.row.imgType" />
        </template>
      </el-table-column>
      <el-table-column label="是否跳转" align="center" prop="isToLink">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_upmission_bg_is_to_link" :value="scope.row.isToLink" />
        </template>
      </el-table-column>
      <el-table-column label="页面地址" align="center" prop="toUrl" />
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionGroupBg:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:unionpayMissionGroupBg:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务组背景对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务组" prop="missionGroupId">
          <el-select v-model="form.missionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
            <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="背景名称" prop="bgName">
          <el-input v-model="form.bgName" placeholder="请输入背景名称" />
        </el-form-item>
        <el-form-item label="背景图片" prop="bgImg">
          <image-upload :limit="1" v-model="form.bgImg" />
        </el-form-item>
        <el-form-item label="背景类型" prop="imgType">
          <el-select v-model="form.imgType" placeholder="请选择背景类型">
            <el-option v-for="dict in dict.type.t_unmission_group_bg_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否跳转" prop="isToLink">
          <el-radio-group v-model="form.isToLink">
            <el-radio v-for="dict in dict.type.t_upmission_bg_is_to_link" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="页面地址" prop="toUrl" v-if="form.isToLink == 1">
          <el-input v-model="form.toUrl" placeholder="请输入页面地址" />
        </el-form-item>
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
            <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
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
    listUnionpayMissionGroupBg,
    getUnionpayMissionGroupBg,
    delUnionpayMissionGroupBg,
    addUnionpayMissionGroupBg,
    updateUnionpayMissionGroupBg
  } from "@/api/zlyyh/unionpayMissionGroupBg";
  import {
    selectListMissionGroup
  } from "@/api/zlyyh/unionpayMissionGroup";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "UnionpayMissionGroupBg",
    dicts: ['t_unmission_group_bg_type', 'sys_normal_disable', 't_upmission_bg_is_to_link'],
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
        // 任务组背景表格数据
        unionpayMissionGroupBgList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          missionGroupId: undefined,
          bgImg: undefined,
          imgType: undefined,
          platformKey: undefined,
          status: undefined,
          sort: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          missionBgId: [{
            required: true,
            message: "任务组背景id不能为空",
            trigger: "blur"
          }],
          missionGroupId: [{
            required: true,
            message: "任务组不能为空",
            trigger: "blur"
          }],
          bgImg: [{
            required: true,
            message: "背景图片不能为空",
            trigger: "blur"
          }],
          imgType: [{
            required: true,
            message: "背景类型",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态",
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
      /** 查询任务组背景列表 */
      getList() {
        this.loading = true;
        listUnionpayMissionGroupBg(this.queryParams).then(response => {
          this.unionpayMissionGroupBgList = response.rows;
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
          missionBgId: undefined,
          missionGroupId: undefined,
          bgImg: undefined,
          imgType: undefined,
          platformKey: undefined,
          status: '0',
          delFlag: undefined,
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          isToLink: '0'
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
        this.ids = selection.map(item => item.missionBgId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加任务组背景";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const missionBgId = row.missionBgId || this.ids
        getUnionpayMissionGroupBg(missionBgId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改任务组背景";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.missionBgId != null) {
              updateUnionpayMissionGroupBg(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addUnionpayMissionGroupBg(this.form).then(response => {
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
        const missionBgIds = row.missionBgId || this.ids;
        this.$modal.confirm('是否确认删除任务组背景编号为"' + missionBgIds + '"的数据项？').then(() => {
          this.loading = true;
          return delUnionpayMissionGroupBg(missionBgIds);
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
        this.download('zlyyh/unionpayMissionGroupBg/export', {
          ...this.queryParams
        }, `unionpayMissionGroupBg_${new Date().getTime()}.xlsx`)
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
          if (row.missionGroupId == item.id) {
            groupName = item.label;
          }
        })
        if (groupName && groupName.length > 0) {
          row.groupName = groupName;
          return groupName;
        }
        return row.missionGroupId;
      },
    }
  };
</script>