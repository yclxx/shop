<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
             label-width="68px">
      <el-form-item label="商户号" prop="merchantNo">
        <el-input
          v-model="queryParams.merchantNo"
          placeholder="请输入商户号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户类型" prop="merchantType">
        <el-select v-model="queryParams.merchantType" placeholder="请选择商户类型" clearable>
          <el-option
            v-for="dict in dict.type.channel_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_show_hide"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="结算方式" prop="settlementWay">
        <el-input
          v-model="queryParams.settlementWay"
          placeholder="请输入结算方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结算比例" prop="settlement">
        <el-input
          v-model="queryParams.settlement"
          placeholder="请输入结算比例"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款方式" prop="paymentMethod">
        <el-input
          v-model="queryParams.paymentMethod"
          placeholder="请输入收款方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收单机构" prop="acquirer">
        <el-input
          v-model="queryParams.acquirer"
          placeholder="请输入收单机构"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="终端编号" prop="terminalNo">
        <el-input
          v-model="queryParams.terminalNo"
          placeholder="请输入终端编号"
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
          v-hasPermi="['zlyyh:activityMerchant:add']"
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
          v-hasPermi="['zlyyh:activityMerchant:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zlyyh:activityMerchant:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:activityMerchant:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activityMerchantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="id" v-if="true"/>
      <el-table-column label="核销人员" align="center" prop="verifierId"/>
      <el-table-column label="活动ID" align="center" prop="activityId"/>
      <el-table-column label="商户号" align="center" prop="merchantNo"/>
      <el-table-column label="商户类型" align="center" prop="merchantType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type" :value="scope.row.merchantType"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_show_hide" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="结算方式" align="center" prop="settlementWay"/>
      <el-table-column label="结算比例" align="center" prop="settlement"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="收款方式" align="center" prop="paymentMethod"/>
      <el-table-column label="收单机构" align="center" prop="acquirer"/>
      <el-table-column label="终端编号" align="center" prop="terminalNo"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:activityMerchant:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:activityMerchant:remove']"
          >删除
          </el-button>
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

    <!-- 添加或修改活动商户号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="核销人员" prop="verifierId">
          <el-input v-model="form.verifierId" placeholder="请输入核销人员"/>
        </el-form-item>
        <el-form-item label="活动ID" prop="activityId">
          <el-input v-model="form.activityId" placeholder="请输入活动ID"/>
        </el-form-item>
        <el-form-item label="商户号" prop="merchantNo">
          <el-input v-model="form.merchantNo" placeholder="请输入商户号"/>
        </el-form-item>
        <el-form-item label="商户类型" prop="merchantType">
          <el-select v-model="form.merchantType" placeholder="请选择商户类型">
            <el-option
              v-for="dict in dict.type.channel_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_show_hide"
              :key="dict.value"
              :label="dict.value"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="结算方式" prop="settlementWay">
          <el-input v-model="form.settlementWay" placeholder="请输入结算方式"/>
        </el-form-item>
        <el-form-item label="结算比例" prop="settlement">
          <el-input v-model="form.settlement" placeholder="请输入结算比例"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="收款方式" prop="paymentMethod">
          <el-input v-model="form.paymentMethod" placeholder="请输入收款方式"/>
        </el-form-item>
        <el-form-item label="收单机构" prop="acquirer">
          <el-input v-model="form.acquirer" placeholder="请输入收单机构"/>
        </el-form-item>
        <el-form-item label="终端编号" prop="terminalNo">
          <el-input v-model="form.terminalNo" placeholder="请输入终端编号"/>
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
  addActivityMerchant,
  delActivityMerchant,
  getActivityMerchant,
  listActivityMerchant,
  updateActivityMerchant
} from "@/api/zlyyh/activityMerchant";

export default {
  name: "ActivityMerchant",
  dicts: ['channel_type', 'sys_show_hide'],
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
      // 活动商户号表格数据
      activityMerchantList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        merchantNo: undefined,
        merchantType: undefined,
        status: undefined,
        settlementWay: undefined,
        settlement: undefined,
        paymentMethod: undefined,
        acquirer: undefined,
        terminalNo: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          {required: true, message: "ID不能为空", trigger: "blur"}
        ],
        verifierId: [
          {required: true, message: "核销人员不能为空", trigger: "blur"}
        ],
        activityId: [
          {required: true, message: "活动ID不能为空", trigger: "blur"}
        ],
        merchantNo: [
          {required: true, message: "商户号不能为空", trigger: "blur"}
        ],
        merchantType: [
          {required: true, message: "商户类型不能为空", trigger: "change"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        settlementWay: [
          {required: true, message: "结算方式不能为空", trigger: "blur"}
        ],
        settlement: [
          {required: true, message: "结算比例不能为空", trigger: "blur"}
        ],
        remark: [
          {required: true, message: "备注不能为空", trigger: "blur"}
        ],
        paymentMethod: [
          {required: true, message: "收款方式不能为空", trigger: "blur"}
        ],
        acquirer: [
          {required: true, message: "收单机构不能为空", trigger: "blur"}
        ],
        terminalNo: [
          {required: true, message: "终端编号不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询活动商户号列表 */
    getList() {
      this.loading = true;
      listActivityMerchant(this.queryParams).then(response => {
        this.activityMerchantList = response.rows;
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
        verifierId: undefined,
        activityId: undefined,
        merchantNo: undefined,
        merchantType: undefined,
        status: undefined,
        settlementWay: undefined,
        settlement: undefined,
        remark: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        delFlag: undefined,
        paymentMethod: undefined,
        acquirer: undefined,
        terminalNo: undefined
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
      this.title = "添加活动商户号";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getActivityMerchant(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改活动商户号";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateActivityMerchant(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addActivityMerchant(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除活动商户号编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delActivityMerchant(ids);
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
      this.download('zlyyh/activityMerchant/export', {
        ...this.queryParams
      }, `activityMerchant_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
