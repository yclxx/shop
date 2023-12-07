<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!--<el-form-item label="平台标识" prop="platformKey">-->
      <!--  <el-input-->
      <!--    v-model="queryParams.platformKey"-->
      <!--    placeholder="请输入平台标识"-->
      <!--    clearable-->
      <!--    @keyup.enter.native="handleQuery"-->
      <!--  />-->
      <!--</el-form-item>-->
      <el-form-item label="品牌名称" prop="approvalBrandName">
        <el-input
          v-model="queryParams.approvalBrandName"
          placeholder="请输入品牌名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销员手机号" prop="verifierMobile">
        <el-input
          v-model="queryParams.verifierMobile"
          placeholder="请输入核销员手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:promotionLog:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="promotionLogList" @selection-change="handleSelectionChange">
      <!--<el-table-column type="selection" width="55" align="center"/>-->
      <el-table-column label="ID" align="center" prop="id" v-if="true"/>
      <el-table-column label="平台标识" align="center" prop="platformKey"/>
      <!--<el-table-column label="商户申请Id" align="center" prop="approvalId"/>-->
      <el-table-column label="品牌名称" align="center" prop="approvalBrandName"/>
      <!--<el-table-column label="任务Id" align="center" prop="taskId"/>-->
      <el-table-column label="任务名称" align="center" prop="taskName"/>
      <!--<el-table-column label="核销员Id" align="center" prop="verifierId"/>-->
      <el-table-column label="核销员手机号" align="center" prop="verifierMobile"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="拒绝原因" align="center" prop="reason"/>
      <!--<el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
      <!--  <template slot-scope="scope">-->
      <!--    <el-button-->
      <!--      size="mini"-->
      <!--      type="text"-->
      <!--      icon="el-icon-edit"-->
      <!--      @click="handleUpdate(scope.row)"-->
      <!--      v-hasPermi="['zlyyh:promotionLog:edit']"-->
      <!--    >修改-->
      <!--    </el-button>-->
      <!--    <el-button-->
      <!--      size="mini"-->
      <!--      type="text"-->
      <!--      icon="el-icon-delete"-->
      <!--      @click="handleDelete(scope.row)"-->
      <!--      v-hasPermi="['zlyyh:promotionLog:remove']"-->
      <!--    >删除-->
      <!--    </el-button>-->
      <!--  </template>-->
      <!--</el-table-column>-->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改推广任务记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
  listPromotionLog,
  getPromotionLog,
  delPromotionLog,
  addPromotionLog,
  updatePromotionLog
} from "@/api/zlyyh/promotionLog";

export default {
  name: "PromotionLog",
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
      // 推广任务记录表格数据
      promotionLogList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        approvalBrandName: undefined,
        taskName: undefined,
        verifierMobile: undefined,
        status: undefined,
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
        approvalId: [
          {required: true, message: "商户申请表id不能为空", trigger: "blur"}
        ],
        approvalBrandName: [
          {required: true, message: "品牌名称不能为空", trigger: "blur"}
        ],
        taskId: [
          {required: true, message: "id不能为空", trigger: "blur"}
        ],
        taskName: [
          {required: true, message: "任务名称不能为空", trigger: "blur"}
        ],
        verifierId: [
          {required: true, message: "核销员id不能为空", trigger: "blur"}
        ],
        verifierMobile: [
          {required: true, message: "核销员手机号不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        reason: [
          {required: true, message: "拒绝原因不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询推广任务记录列表 */
    getList() {
      this.loading = true;
      listPromotionLog(this.queryParams).then(response => {
        this.promotionLogList = response.rows;
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
        approvalId: undefined,
        approvalBrandName: undefined,
        taskId: undefined,
        taskName: undefined,
        verifierId: undefined,
        verifierMobile: undefined,
        status: undefined,
        reason: undefined,
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加推广任务记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getPromotionLog(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改推广任务记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updatePromotionLog(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addPromotionLog(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除推广任务记录编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delPromotionLog(ids);
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
      this.download('zlyyh/promotionLog/export', {
        ...this.queryParams
      }, `promotionLog_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
