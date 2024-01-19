<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="商品组名称" prop="productGroupName">
        <el-input
          v-model="queryParams.productGroupName"
          placeholder="请输入商品组名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>


      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.t_banner_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['zlyyh:productGroup:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['zlyyh:productGroup:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zlyyh:productGroup:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:productGroup:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productGroupList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="productGroupId" v-if="true" width="180px"/>
      <el-table-column label="商品组名称" align="center" prop="productGroupName" />
      <el-table-column label="用户端提醒" align="center" prop="productGroupTip" />
      <el-table-column label="每日同一用户限领" align="center" prop="dayUserCount" />
      <el-table-column label="每周同一用户限领" align="center" prop="weekUserCount" />
      <el-table-column label="当月同一用户限领" align="center" prop="monthUserCount" />
      <el-table-column label="活动周期同一用户限领" align="center" prop="totalUserCount" width="200px" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
                     size="mini"
                     type="text"
                     icon="el-icon-edit"
                     @click="handleProductByGroup(scope.row)"
                     v-hasPermi="['zlyyh:productGroup:edit']"
          >商品维护
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:productGroup:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:productGroup:remove']"
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

    <!-- 添加或修改商品组规则配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="170px">
        <el-form-item label="商品组名称" prop="productGroupName">
          <el-input v-model="form.productGroupName" placeholder="请输入商品组名称" />
        </el-form-item>
        <el-form-item label="用户端提醒" prop="productGroupTip">
          <el-input v-model="form.productGroupTip" placeholder="请输入用户端提醒" />
        </el-form-item>
        <el-form-item label="每日同一用户限领" prop="dayUserCount">
          <el-input v-model="form.dayUserCount" placeholder="请输入每日同一用户限领数量，0为不限制" />
        </el-form-item>
        <el-form-item label="每周同一用户限领" prop="weekUserCount">
          <el-input v-model="form.weekUserCount" placeholder="请输入每周同一用户限领数量，0为不限制" />
        </el-form-item>
        <el-form-item label="当月同一用户限领" prop="monthUserCount">
          <el-input v-model="form.monthUserCount" placeholder="请输入当月同一用户限领数量，0为不限制" />
        </el-form-item>
        <el-form-item label="活动周期同一用户限领" prop="totalUserCount">
          <el-input v-model="form.totalUserCount" placeholder="请输入活动周期同一用户限领数量，0为不限制" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.t_banner_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="商品维护" :visible.sync="isProduct" width="90%">
      <groupProductConnect v-bind:productGroupId="productGroupId"></groupProductConnect>
    </el-dialog>
  </div>
</template>

<script>
import { listProductGroup, getProductGroup, delProductGroup, addProductGroup, updateProductGroup,updateGroupProduct } from "@/api/zlyyh/productGroup";
import groupProductConnect from "@/views/zlyyh/product/groupProductConnect.vue";

export default {
  name: "ProductGroup",
  dicts: ['t_banner_status'],
  components: {
    groupProductConnect
  },
  data() {
    return {
      // 商品信息
      productGroupId: undefined,
      isProduct: false,
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
      // 商品组规则配置表格数据
      productGroupList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productGroupName: undefined,
        productGroupTip: undefined,
        dayUserCount: undefined,
        weekUserCount: undefined,
        monthUserCount: undefined,
        totalUserCount: undefined,
        status: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productGroupId: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
        productGroupName: [
          { required: true, message: "商品组名称不能为空", trigger: "blur" }
        ],
        dayUserCount: [
          { required: true, message: "每日同一用户限领数量，0为不限制不能为空", trigger: "blur" }
        ],
        weekUserCount: [
          { required: true, message: "每周同一用户限领数量，0为不限制不能为空", trigger: "blur" }
        ],
        monthUserCount: [
          { required: true, message: "当月同一用户限领数量，0为不限制不能为空", trigger: "blur" }
        ],
        totalUserCount: [
          { required: true, message: "活动周期同一用户限领数量，0为不限制不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 商品维护
    handleProductByGroup(row) {
      this.productGroupId = row.productGroupId;
      this.isProduct = true;
    },
    /** 查询商品组规则配置列表 */
    getList() {
      this.loading = true;
      listProductGroup(this.queryParams).then(response => {
        this.productGroupList = response.rows;
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
        productGroupId: undefined,
        productGroupName: undefined,
        productGroupTip: undefined,
        dayUserCount: undefined,
        weekUserCount: undefined,
        monthUserCount: undefined,
        totalUserCount: undefined,
        status: undefined,
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
      this.ids = selection.map(item => item.productGroupId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品组规则配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const productGroupId = row.productGroupId || this.ids
      getProductGroup(productGroupId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改商品组规则配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.productGroupId != null) {
            updateProductGroup(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addProductGroup(this.form).then(response => {
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
      const productGroupIds = row.productGroupId || this.ids;
      this.$modal.confirm('是否确认删除商品组规则配置编号为"' + productGroupIds + '"的数据项？').then(() => {
        this.loading = true;
        return delProductGroup(productGroupIds);
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
      this.download('zlyyh/productGroup/export', {
        ...this.queryParams
      }, `productGroup_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
