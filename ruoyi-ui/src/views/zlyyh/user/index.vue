<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="openId" prop="openId">
        <el-input v-model="queryParams.openId" placeholder="请输入openId" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_user_stauts" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="权益会员" prop="vipUser">
        <el-select v-model="queryParams.vipUser" placeholder="请选择是否权益会员" clearable>
          <el-option v-for="dict in dict.type.t_user_vip" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="关注状态" prop="followStatus">
        <el-select v-model="queryParams.followStatus" placeholder="请选择小程序关注状态" clearable>
          <el-option v-for="dict in dict.type.t_user_follow_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="handleImport"
          v-hasPermi="['zlyyh:user:refresh']">同步用户数据</el-button>
      </el-col> -->
      <!--<el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:user:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:user:remove']">删除</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:user:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList">
      <el-table-column label="平台" width="120" align="center" prop="platformName" :formatter="changePlatform" />
      <el-table-column label="用户编号" align="center" width="168" prop="userId" />
      <el-table-column label="手机号" align="center" prop="mobile" width="110" />
      <el-table-column label="openId" align="center" prop="openId" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="信息授权" align="center" prop="reloadUser" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_user_reload_user" :value="scope.row.reloadUser" />
        </template>
      </el-table-column>
      <el-table-column label="权益会员" align="center" prop="vipUser" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_user_vip" :value="scope.row.vipUser" />
        </template>
      </el-table-column>
      <el-table-column label="关注状态" align="center" prop="followStatus" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_user_follow_status" :value="scope.row.followStatus" />
        </template>
      </el-table-column>
      <el-table-column label="上次登录时间" align="center" prop="lastLoginDate" width="160" />
      <el-table-column label="登录时间" align="center" prop="loginDate" width="160" />
      <el-table-column label="登录城市" align="center" prop="loginCityName" width="120" />
      <el-table-column label="登录区号" align="center" prop="loginCityCode" width="80" />
      <el-table-column label="首次访问城市" align="center" prop="registerCityName" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="80" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_user_stauts" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="150" fixed="right" />
      <el-table-column label="操作" align="center" width="80" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:user:edit']">修改</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:user:remove']">删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改用户信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!-- <el-form-item label="用户昵称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="头像" prop="userImg">
          <el-input v-model="form.userImg" placeholder="请输入头像" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" />
        </el-form-item> -->
        <el-form-item label="openId" prop="openId">
          <el-input v-model="form.openId" placeholder="请输入openId" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_user_stauts" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="信息授权" prop="reloadUser">
          <el-select v-model="form.reloadUser" placeholder="请选择信息授权">
            <el-option v-for="dict in dict.type.t_user_reload_user" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权益会员" prop="vipUser">
          <el-select v-model="form.vipUser" placeholder="请选择是否权益会员">
            <el-option v-for="dict in dict.type.t_user_vip" :key="dict.value" :label="dict.label" :value="dict.value">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="首次访问所在城市" prop="registerCityName">
          <el-input v-model="form.registerCityName" placeholder="请输入首次访问所在城市" />
        </el-form-item>
        <el-form-item label="首次访问所在城市行政区号" prop="registerCityCode">
          <el-input v-model="form.registerCityCode" placeholder="请输入首次访问所在城市行政区号" />
        </el-form-item> -->
        <el-form-item label="关注状态" prop="followStatus">
          <el-select v-model="form.followStatus" placeholder="请选择小程序关注状态">
            <el-option v-for="dict in dict.type.t_user_follow_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="最后登录IP" prop="loginIp">
          <el-input v-model="form.loginIp" placeholder="请输入最后登录IP" />
        </el-form-item>
        <el-form-item label="最后登录时间" prop="loginDate">
          <el-date-picker clearable v-model="form.loginDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择最后登录时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="最后登录所在城市" prop="loginCityName">
          <el-input v-model="form.loginCityName" placeholder="请输入最后登录所在城市" />
        </el-form-item>
        <el-form-item label="最后登录所在城市行政区号" prop="loginCityCode">
          <el-input v-model="form.loginCityCode" placeholder="请输入最后登录所在城市行政区号" />
        </el-form-item> -->
        <el-form-item label="平台标识" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台">
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 同步数据 -->
    <el-dialog :title="refreshTitle" :visible.sync="refreshOpen" width="600px" append-to-body>
      <el-form ref="refreshForm" :model="refreshForm" :rules="refreshRules" label-width="70px">
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="refreshForm.platformKey" placeholder="请选择平台" filterable clearable>
            <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="showStartDate" label-width="120">
          <el-date-picker clearable v-model="showStartDate" size="small" :picker-options="pickerOptions"
            value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
            end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="refreshSubmitForm">确 定</el-button>
        <el-button @click="refreshCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listUser,
    getUser,
    delUser,
    addUser,
    updateUser,
    selectAll
  } from "@/api/zlyyh/user";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "User",
    dicts: ['t_user_follow_status', 't_user_vip', 't_user_reload_user', 't_user_stauts'],
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
        // 用户信息表格数据
        userList: [],
        platformList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        refreshOpen: false,
        refreshTitle: '',
        refreshForm: {},
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          userName: undefined,
          userImg: undefined,
          mobile: undefined,
          openId: undefined,
          status: undefined,
          reloadUser: undefined,
          vipUser: undefined,
          registerCityName: undefined,
          registerCityCode: undefined,
          followStatus: undefined,
          loginIp: undefined,
          loginDate: undefined,
          loginCityName: undefined,
          loginCityCode: undefined,
          platformKey: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc'
        },
        // 表单参数
        form: {},
        showStartDate: [],
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        refreshRules: {
          platformKey: {
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }
        },
        // 表单校验
        rules: {
          userId: [{
            required: true,
            message: "用户ID不能为空",
            trigger: "blur"
          }],
          userName: [{
            required: true,
            message: "用户昵称不能为空",
            trigger: "blur"
          }],
          userImg: [{
            required: true,
            message: "头像不能为空",
            trigger: "blur"
          }],
          mobile: [{
            required: true,
            message: "手机号不能为空",
            trigger: "blur"
          }],
          openId: [{
            required: true,
            message: "openId，第三方平台联登唯一标识不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          reloadUser: [{
            required: true,
            message: "信息授权不能为空",
            trigger: "change"
          }],
          vipUser: [{
            required: true,
            message: "是否权益会员不能为空",
            trigger: "change"
          }],
          registerCityName: [{
            required: true,
            message: "首次访问所在城市不能为空",
            trigger: "blur"
          }],
          registerCityCode: [{
            required: true,
            message: "首次访问所在城市行政区号不能为空",
            trigger: "blur"
          }],
          followStatus: [{
            required: true,
            message: "小程序关注状态不能为空",
            trigger: "change"
          }],
          loginIp: [{
            required: true,
            message: "最后登录IP不能为空",
            trigger: "blur"
          }],
          loginDate: [{
            required: true,
            message: "最后登录时间不能为空",
            trigger: "blur"
          }],
          loginCityName: [{
            required: true,
            message: "最后登录所在城市不能为空",
            trigger: "blur"
          }],
          loginCityCode: [{
            required: true,
            message: "最后登录所在城市行政区号不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }]
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
      handleImport() {
        this.refreshOpen = true;
        this.refreshTitle = "同步用户数据";
        this.refreshReset();
      },
      refreshSubmitForm() {
        this.$refs["refreshForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (null != this.showStartDate && '' != this.showStartDate) {
              this.refreshForm.beginStartDate = this.showStartDate[0];
              this.refreshForm.endStartDate = this.showStartDate[1];
            }
            selectAll(this.refreshForm).then(response => {
              this.$modal.msgSuccess("操作成功");
              this.refreshOpen = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
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
      /** 查询用户信息列表 */
      getList() {
        this.loading = true;
        listUser(this.queryParams).then(response => {
          this.userList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      refreshCancel() {
        this.refreshOpen = false;
        this.refreshReset();
      },
      refreshReset() {
        this.refreshForm = {
          platformKey: undefined,
          beginStartDate: undefined,
          endStartDate: undefined
        }
        this.resetForm("refreshForm");
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          userId: undefined,
          userName: undefined,
          userImg: undefined,
          mobile: undefined,
          openId: undefined,
          status: undefined,
          reloadUser: undefined,
          vipUser: undefined,
          registerCityName: undefined,
          registerCityCode: undefined,
          followStatus: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined,
          loginIp: undefined,
          loginDate: undefined,
          loginCityName: undefined,
          loginCityCode: undefined,
          platformKey: undefined
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
        this.ids = selection.map(item => item.userId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加用户信息";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const userId = row.userId || this.ids
        getUser(userId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.form.mobile = undefined;
          this.open = true;
          this.title = "修改用户信息";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.userId != null) {
              updateUser(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addUser(this.form).then(response => {
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
        const userIds = row.userId || this.ids;
        this.$modal.confirm('是否确认删除用户信息编号为"' + userIds + '"的数据项？').then(() => {
          this.loading = true;
          return delUser(userIds);
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
        this.download('zlyyh-admin/user/export', {
          ...this.queryParams
        }, `user_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>