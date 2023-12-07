<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
             label-width="68px">
      <!--<el-form-item label="平台标识" prop="platformKey">-->
      <!--  <el-input-->
      <!--    v-model="queryParams.platformKey"-->
      <!--    placeholder="请输入平台标识"-->
      <!--    clearable-->
      <!--    @keyup.enter.native="handleQuery"-->
      <!--  />-->
      <!--</el-form-item>-->
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_common_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="openId" prop="openId">
        <el-input
          v-model="queryParams.openId"
          placeholder="请输入openId"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zlyyh:verifier:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['zlyyh:verifier:edit']"
        >修改
        </el-button>
      </el-col>
      <!--<el-col :span="1.5">-->
      <!--  <el-button-->
      <!--    type="danger"-->
      <!--    plain-->
      <!--    icon="el-icon-delete"-->
      <!--    size="mini"-->
      <!--    :disabled="multiple"-->
      <!--    @click="handleDelete"-->
      <!--    v-hasPermi="['zlyyh:verifier:remove']"-->
      <!--  >删除</el-button>-->
      <!--</el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:verifier:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="verifierList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="id" v-if="true"/>
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="手机号" align="center" prop="mobile"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="登录信息" align="center" prop="loginDate" width="200">
        <template slot-scope="scope">
          <div>上次登录时间:{{ parseTime(scope.row.lastLoginDate, '{y}-{m}-{d}') }}</div>
          <div>最后登录时间:{{ parseTime(scope.row.loginDate, '{y}-{m}-{d}') }}</div>
          <div>最后登录IP:{{ scope.row.loginIp }}</div>
        </template>
      </el-table-column>
      <el-table-column label="账号权限" align="center" prop="lastLoginDate" width="180">
        <template slot-scope="scope">
          <div>bd:{{ scope.row.isBd ? '是' : '否' }}</div>
          <div>管理员:{{ scope.row.isAdmin ? '是' : '否' }}</div>
          <div>核销人员:{{ scope.row.isVerifier ? '是' : '否' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="账号权限" align="center" prop="lastLoginDate" width="180">
        <template slot-scope="scope">
          <div>所在地区:{{ scope.row.cityCode }}</div>
          <div>归属公司:{{ scope.row.org }}</div>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改核销人员管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="70%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台标识" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="名称" prop="username">
              <el-input v-model="form.username" placeholder="请输入名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号" prop="mobile">
              <el-input v-model="form.mobile" placeholder="请输入手机号"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="openId" prop="openId">
              <el-input v-model="form.openId" placeholder="请输入openId"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="是否bd" prop="isBd">
              <el-radio-group v-model="form.isBd">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否管理员" prop="isAdmin">
              <el-radio-group v-model="form.isAdmin">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否核销人员" prop="isVerifier">
              <el-radio-group v-model="form.isVerifier">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="所处地区" prop="cityCode">
              <el-input v-model="form.cityCode" placeholder="请输入所处地区"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="归属公司" prop="org">
              <el-input v-model="form.org" placeholder="请输入归属公司"/>
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
import {listVerifier, getVerifier, delVerifier, addVerifier, updateVerifier} from "@/api/zlyyh/verifier";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "Verifier",
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
      // 核销人员管理表格数据
      verifierList: [],
      //平台信息
      platformList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        username: undefined,
        mobile: undefined,
        status: undefined,
        openId: undefined,
        extensionServiceProviderId: undefined,
        isBd: undefined,
        isAdmin: undefined,
        isVerifier: undefined,
        cityCode: undefined,
        org: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          {required: true, message: "不能为空", trigger: "blur"}
        ],
        platformKey: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ],
        mobile: [
          {required: true, message: "手机号不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        loginIp: [
          {required: true, message: "最后登录IP不能为空", trigger: "blur"}
        ],
        loginDate: [
          {required: true, message: "最后登录时间不能为空", trigger: "blur"}
        ],
        lastLoginDate: [
          {required: true, message: "上次登录时间不能为空", trigger: "blur"}
        ],
        isBd: [
          {required: true, message: "bd不能为空", trigger: "blur"}
        ],
        isAdmin: [
          {required: true, message: "管理员不能为空", trigger: "blur"}
        ],
        isVerifier: [
          {required: true, message: "核销人员不能为空", trigger: "blur"}
        ],
        cityCode: [
          {required: true, message: "所处地区不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
  },
  methods: {
    //平台标识下拉列表
    getPlatformSelectList() {
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
    },
    /** 查询核销人员管理列表 */
    getList() {
      this.loading = true;
      listVerifier(this.queryParams).then(response => {
        this.verifierList = response.rows;
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
        platformKey: undefined,
        mobile: undefined,
        status: undefined,
        openId: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        loginIp: undefined,
        loginDate: undefined,
        lastLoginDate: undefined,
        sysDeptId: undefined,
        sysUserId: undefined
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加核销人员管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getVerifier(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改核销人员管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateVerifier(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addVerifier(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除核销人员管理编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delVerifier(ids);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh-admin/verifier/export', {
        ...this.queryParams
      }, `verifier_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
