<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
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
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-plus"-->
<!--          size="mini"-->
<!--          @click="handleAdd"-->
<!--          v-hasPermi="['zlyyh:userAddress:add']"-->
<!--        >新增</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['zlyyh:userAddress:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-delete"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--          v-hasPermi="['zlyyh:userAddress:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:userAddress:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userAddressList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="userAddressId" v-if="false"/>
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="联系人" align="center" prop="name" />
      <el-table-column label="联系电话" align="center" prop="tel" />
      <el-table-column label="地址" align="center" prop="address" />
      <el-table-column label="详细地址" align="center" prop="addressInfo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:userAddress:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:userAddress:remove']"
          >删除</el-button>
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

    <!-- 添加或修改用户地址对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="联系人" prop="name">
          <el-input v-model="form.name" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="tel">
          <el-input v-model="form.tel" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="地址中文，省市县等，用空格隔开" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址中文，省市县等，用空格隔开" />
        </el-form-item>
        <el-form-item label="地址四级联动ID，对应t_area表，多个之间用空格隔开" prop="areaId">
          <el-input v-model="form.areaId" placeholder="请输入地址四级联动ID，对应t_area表，多个之间用空格隔开" />
        </el-form-item>
        <el-form-item label="详细地址" prop="addressInfo">
          <el-input v-model="form.addressInfo" placeholder="请输入详细地址" />
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
import { listUserAddress, getUserAddress, delUserAddress, addUserAddress, updateUserAddress } from "@/api/zlyyh/userAddress";

export default {
  name: "UserAddress",
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
      // 用户地址表格数据
      userAddressList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: undefined,
        name: undefined,
        tel: undefined,
        isDefault: undefined,
        address: undefined,
        areaId: undefined,
        addressInfo: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userAddressId: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "联系人不能为空", trigger: "blur" }
        ],
        tel: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        isDefault: [
          { required: true, message: "是否默认 0-默认 1-不默认不能为空", trigger: "blur" }
        ],
        address: [
          { required: true, message: "地址中文，省市县等，用空格隔开不能为空", trigger: "blur" }
        ],
        areaId: [
          { required: true, message: "地址四级联动ID，对应t_area表，多个之间用空格隔开不能为空", trigger: "blur" }
        ],
        addressInfo: [
          { required: true, message: "详细地址不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户地址列表 */
    getList() {
      this.loading = true;
      listUserAddress(this.queryParams).then(response => {
        this.userAddressList = response.rows;
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
        userAddressId: undefined,
        userId: undefined,
        name: undefined,
        tel: undefined,
        isDefault: undefined,
        address: undefined,
        areaId: undefined,
        addressInfo: undefined,
        createTime: undefined,
        updateTime: undefined,
        createBy: undefined,
        updateBy: undefined,
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
      this.ids = selection.map(item => item.userAddressId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加用户地址";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const userAddressId = row.userAddressId || this.ids
      getUserAddress(userAddressId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改用户地址";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.userAddressId != null) {
            updateUserAddress(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addUserAddress(this.form).then(response => {
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
      const userAddressIds = row.userAddressId || this.ids;
      this.$modal.confirm('是否确认删除用户地址编号为"' + userAddressIds + '"的数据项？').then(() => {
        this.loading = true;
        return delUserAddress(userAddressIds);
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
      this.download('zlyyh/userAddress/export', {
        ...this.queryParams
      }, `userAddress_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
