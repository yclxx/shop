<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用户信息" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID或手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商圈名称" prop="businessDistrictName">
        <el-input v-model="queryParams.businessDistrictName" placeholder="请输入商圈名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="品牌名称" prop="commercialTenantName">
        <el-input v-model="queryParams.commercialTenantName" placeholder="请输入品牌名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="门店名称" prop="shopName">
        <el-input v-model="queryParams.shopName" placeholder="请输入门店名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable>
          <el-option v-for="dict in dict.type.audit_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:shareUser:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-sort" size="mini" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="shareUserList" row-key="userId"
      :default-expand-all="isExpandAll" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column label="商圈名称" prop="businessDistrictName" width="150" />
      <el-table-column label="用户信息" align="left" prop="userId" width="230">
        <template slot-scope="scope">
          <span>ID：{{ scope.row.userId }}</span><br>
          <span v-if="scope.row.userMobile">手机号：{{ scope.row.userMobile }}</span>
        </template>
      </el-table-column>
      <el-table-column label="云闪付手机号" align="center" prop="upMobile" width="120" />
      <el-table-column label="姓名" align="center" prop="userName" width="68" />
      <el-table-column label="性别" align="center" prop="sex" width="68">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sex_type" :value="scope.row.sex" />
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="ageType" width="108">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.age_type_list" :value="scope.row.ageType" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="68" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="88" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.audit_status" :value="scope.row.auditStatus" />
        </template>
      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="分销开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span><br />
        </template>
      </el-table-column>
      <el-table-column label="分销结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span><br />
        </template>
      </el-table-column>
      <el-table-column label="品牌名称" align="center" prop="commercialTenantName" width="150" />
      <el-table-column label="门店名称" align="center" prop="shopName" width="150" />
      <el-table-column label="创建时间" fixed="right" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" width="100" :show-overflow-tooltip="true" align="center" prop="remark" />
      <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shareUser:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
            v-hasPermi="['zlyyh:shareUser:add']">新增</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改分销员对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台" style="width: 100%;">
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="云闪付手机号" prop="upMobile">
          <el-input v-model="form.upMobile" placeholder="请输入云闪付手机号" />
        </el-form-item>
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio v-for="dict in dict.type.sex_type" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="ageType">
          <el-select v-model="form.ageType" placeholder="请选择年龄" style="width: 100%;" clearable>
            <el-option v-for="dict in dict.type.age_type_list" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属商圈" prop="parentId">
          <treeselect v-model="form.parentId" :options="shareUserOptions" :normalizer="normalizer"
            placeholder="请选择所属商圈" />
        </el-form-item>
        <el-form-item label="商圈名称" prop="businessDistrictName">
          <el-input v-model="form.businessDistrictName" placeholder="请输入商圈名称" />
        </el-form-item>
        <el-form-item label="品牌名称" prop="commercialTenantName">
          <el-input v-model="form.commercialTenantName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="门店名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入门店名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="form.auditStatus">
            <el-radio v-for="dict in dict.type.audit_status" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable v-model="form.startTime" type="datetime" style="width: 100%;"
            value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择分销开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable v-model="form.endTime" type="datetime" style="width: 100%;"
            value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择分销结束时间" default-time="23:59:59">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
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
    listShareUser,
    getShareUser,
    delShareUser,
    addShareUser,
    updateShareUser
  } from "@/api/zlyyh/shareUser";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "ShareUser",
    dicts: ['audit_status', 'sys_normal_disable', 'sex_type', 'age_type_list'],
    components: {
      Treeselect
    },
    data() {
      return {
        // 按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        // 显示搜索条件
        showSearch: true,
        // 分销员表格数据
        shareUserList: [],
        // 分销员树选项
        shareUserOptions: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 是否展开，默认全部展开
        isExpandAll: true,
        // 重新渲染表格状态
        refreshTable: true,
        // 平台标识时间范围
        daterangeCreateTime: [],
        platformList: [],
        // 查询参数
        queryParams: {
          userId: undefined,
          businessDistrictName: undefined,
          commercialTenantName: undefined,
          shopName: undefined,
          upMobile: undefined,
          status: undefined,
          auditStatus: undefined,
          parentId: undefined,
          platformKey: undefined,
          createTime: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          userId: [{
            required: true,
            message: "用户ID不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
    },
    methods: {
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
      /** 查询分销员列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
          this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
          this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
        }
        listShareUser(this.queryParams).then(response => {
          this.shareUserList = this.handleTree(response.data, "userId", "parentId");
          this.loading = false;
        });
      },
      /** 转换分销员数据结构 */
      normalizer(node) {
        if (node.children && !node.children.length) {
          delete node.children;
        }
        return {
          id: node.userId,
          label: node.businessDistrictName,
          children: node.children
        };
      },
      /** 查询分销员下拉树结构 */
      getTreeselect() {
        listShareUser().then(response => {
          this.shareUserOptions = [];
          const data = {
            userId: 0,
            businessDistrictName: '顶级节点',
            children: []
          };
          data.children = this.handleTree(response.data, "userId", "parentId");
          this.shareUserOptions.push(data);
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
          userId: null,
          businessDistrictName: null,
          commercialTenantName: null,
          shopName: null,
          upMobile: null,
          status: '0',
          auditStatus: null,
          remark: null,
          parentId: null,
          platformKey: null,
          startTime: null,
          endTime: null,
          createBy: null,
          createTime: null,
          updateBy: null,
          updateTime: null,
          delFlag: null,
          sysDeptId: null,
          sysUserId: null
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.daterangeCreateTime = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd(row) {
        this.reset();
        this.getTreeselect();
        if (row != null && row.userId) {
          this.form.parentId = row.userId;
        } else {
          this.form.parentId = 0;
        }
        this.open = true;
        this.title = "添加分销员";
      },
      /** 展开/折叠操作 */
      toggleExpandAll() {
        this.refreshTable = false;
        this.isExpandAll = !this.isExpandAll;
        this.$nextTick(() => {
          this.refreshTable = true;
        });
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        this.getTreeselect();
        if (row != null) {
          this.form.parentId = row.userId;
        }
        getShareUser(row.userId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改分销员";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.userId != null) {
              updateShareUser(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addShareUser(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除分销员编号为"' + row.userId + '"的数据项？').then(() => {
          this.loading = true;
          return delShareUser(row.userId);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      }
    }
  };
</script>
