<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="巡检ID" prop="tourId">
        <el-input
          v-model="queryParams.tourId"
          placeholder="请输入巡检ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="巡检记录ID" prop="tourLogId">
        <el-input
          v-model="queryParams.tourLogId"
          placeholder="请输入巡检记录ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="巡检人员ID" prop="verifierId">
        <el-input
          v-model="queryParams.verifierId"
          placeholder="请输入巡检人员ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="门店ID" prop="shopId">
        <el-input
          v-model="queryParams.shopId"
          placeholder="请输入门店ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户号" prop="merchantNo">
        <el-input
          v-model="queryParams.merchantNo"
          placeholder="请输入商户号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户号类型  0-微信  1-云闪付  2-支付宝" prop="merchantType">
        <el-select v-model="queryParams.merchantType" placeholder="请选择商户号类型  0-微信  1-云闪付  2-支付宝" clearable>
          <el-option
            v-for="dict in dict.type.t_merchant_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="收款方式  1-住扫  2被扫" prop="paymentMethod">
        <el-input
          v-model="queryParams.paymentMethod"
          placeholder="请输入收款方式  1-住扫  2被扫"
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
      <el-form-item label="商编截图" prop="merchantImg">
        <el-input
          v-model="queryParams.merchantImg"
          placeholder="请输入商编截图"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否邮储商编  0-是  1-否" prop="ycMerchant">
        <el-select v-model="queryParams.ycMerchant" placeholder="请选择是否邮储商编  0-是  1-否" clearable>
          <el-option
            v-for="dict in dict.type.t_is_yc_merchant"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否修改  0-是  1-不是" prop="isUpdate">
        <el-input
          v-model="queryParams.isUpdate"
          placeholder="请输入是否修改  0-是  1-不是"
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
          v-hasPermi="['zlyyh:shopTourLsMerchant:add']"
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
          v-hasPermi="['zlyyh:shopTourLsMerchant:edit']"
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
          v-hasPermi="['zlyyh:shopTourLsMerchant:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:shopTourLsMerchant:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopTourLsMerchantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="巡检商户号临时表ID" align="center" prop="tourMerchantLsId" v-if="true"/>
      <el-table-column label="巡检ID" align="center" prop="tourId" />
      <el-table-column label="巡检记录ID" align="center" prop="tourLogId" />
      <el-table-column label="巡检人员ID" align="center" prop="verifierId" />
      <el-table-column label="门店ID" align="center" prop="shopId" />
      <el-table-column label="商户号" align="center" prop="merchantNo" />
      <el-table-column label="商户号类型  0-微信  1-云闪付  2-支付宝" align="center" prop="merchantType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_merchant_type" :value="scope.row.merchantType"/>
        </template>
      </el-table-column>
      <el-table-column label="收款方式  1-住扫  2被扫" align="center" prop="paymentMethod" />
      <el-table-column label="收单机构" align="center" prop="acquirer" />
      <el-table-column label="终端编号" align="center" prop="terminalNo" />
      <el-table-column label="商编截图" align="center" prop="merchantImg" />
      <el-table-column label="是否邮储商编  0-是  1-否" align="center" prop="ycMerchant">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_is_yc_merchant" :value="scope.row.ycMerchant"/>
        </template>
      </el-table-column>
      <el-table-column label="是否修改  0-是  1-不是" align="center" prop="isUpdate">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_is_update" :value="scope.row.isUpdate"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shopTourLsMerchant:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:shopTourLsMerchant:remove']"
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

    <!-- 添加或修改巡检商户号临时对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="巡检ID" prop="tourId">
          <el-input v-model="form.tourId" placeholder="请输入巡检ID" />
        </el-form-item>
        <el-form-item label="巡检记录ID" prop="tourLogId">
          <el-input v-model="form.tourLogId" placeholder="请输入巡检记录ID" />
        </el-form-item>
        <el-form-item label="巡检人员ID" prop="verifierId">
          <el-input v-model="form.verifierId" placeholder="请输入巡检人员ID" />
        </el-form-item>
        <el-form-item label="门店ID" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="商户号" prop="merchantNo">
          <el-input v-model="form.merchantNo" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="商户号类型  0-微信  1-云闪付  2-支付宝" prop="merchantType">
          <el-select v-model="form.merchantType" placeholder="请选择商户号类型  0-微信  1-云闪付  2-支付宝">
            <el-option
              v-for="dict in dict.type.t_merchant_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="收款方式  1-住扫  2被扫" prop="paymentMethod">
          <el-input v-model="form.paymentMethod" placeholder="请输入收款方式  1-住扫  2被扫" />
        </el-form-item>
        <el-form-item label="收单机构" prop="acquirer">
          <el-input v-model="form.acquirer" placeholder="请输入收单机构" />
        </el-form-item>
        <el-form-item label="终端编号" prop="terminalNo">
          <el-input v-model="form.terminalNo" placeholder="请输入终端编号" />
        </el-form-item>
        <el-form-item label="商编截图" prop="merchantImg">
          <el-input v-model="form.merchantImg" placeholder="请输入商编截图" />
        </el-form-item>
        <el-form-item label="是否邮储商编  0-是  1-否" prop="ycMerchant">
          <el-select v-model="form.ycMerchant" placeholder="请选择是否邮储商编  0-是  1-否">
            <el-option
              v-for="dict in dict.type.t_is_yc_merchant"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否修改  0-是  1-不是" prop="isUpdate">
          <el-input v-model="form.isUpdate" placeholder="请输入是否修改  0-是  1-不是" />
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
import { listShopTourLsMerchant, getShopTourLsMerchant, delShopTourLsMerchant, addShopTourLsMerchant, updateShopTourLsMerchant } from "@/api/zlyyh/shopTourLsMerchant";

export default {
  name: "ShopTourLsMerchant",
  dicts: ['t_merchant_type', 't_is_yc_merchant'],
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
      // 巡检商户号临时表格数据
      shopTourLsMerchantList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tourId: undefined,
        tourLogId: undefined,
        verifierId: undefined,
        shopId: undefined,
        merchantNo: undefined,
        merchantType: undefined,
        paymentMethod: undefined,
        acquirer: undefined,
        terminalNo: undefined,
        merchantImg: undefined,
        ycMerchant: undefined,
        isUpdate: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        tourMerchantLsId: [
          { required: true, message: "巡检商户号临时表ID不能为空", trigger: "blur" }
        ],
        tourId: [
          { required: true, message: "巡检ID不能为空", trigger: "blur" }
        ],
        tourLogId: [
          { required: true, message: "巡检记录ID不能为空", trigger: "blur" }
        ],
        verifierId: [
          { required: true, message: "巡检人员ID不能为空", trigger: "blur" }
        ],
        shopId: [
          { required: true, message: "门店ID不能为空", trigger: "blur" }
        ],
        merchantNo: [
          { required: true, message: "商户号不能为空", trigger: "blur" }
        ],
        merchantType: [
          { required: true, message: "商户号类型  0-微信  1-云闪付  2-支付宝不能为空", trigger: "change" }
        ],
        paymentMethod: [
          { required: true, message: "收款方式  1-住扫  2被扫不能为空", trigger: "blur" }
        ],
        acquirer: [
          { required: true, message: "收单机构不能为空", trigger: "blur" }
        ],
        terminalNo: [
          { required: true, message: "终端编号不能为空", trigger: "blur" }
        ],
        merchantImg: [
          { required: true, message: "商编截图不能为空", trigger: "blur" }
        ],
        ycMerchant: [
          { required: true, message: "是否邮储商编  0-是  1-否不能为空", trigger: "change" }
        ],
        isUpdate: [
          { required: true, message: "是否修改  0-是  1-不是不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询巡检商户号临时列表 */
    getList() {
      this.loading = true;
      listShopTourLsMerchant(this.queryParams).then(response => {
        this.shopTourLsMerchantList = response.rows;
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
        tourMerchantLsId: undefined,
        tourId: undefined,
        tourLogId: undefined,
        verifierId: undefined,
        shopId: undefined,
        merchantNo: undefined,
        merchantType: undefined,
        paymentMethod: undefined,
        acquirer: undefined,
        terminalNo: undefined,
        merchantImg: undefined,
        ycMerchant: undefined,
        isUpdate: undefined,
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
      this.ids = selection.map(item => item.tourMerchantLsId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加巡检商户号临时";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const tourMerchantLsId = row.tourMerchantLsId || this.ids
      getShopTourLsMerchant(tourMerchantLsId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改巡检商户号临时";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.tourMerchantLsId != null) {
            updateShopTourLsMerchant(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addShopTourLsMerchant(this.form).then(response => {
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
      const tourMerchantLsIds = row.tourMerchantLsId || this.ids;
      this.$modal.confirm('是否确认删除巡检商户号临时编号为"' + tourMerchantLsIds + '"的数据项？').then(() => {
        this.loading = true;
        return delShopTourLsMerchant(tourMerchantLsIds);
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
      this.download('zlyyh/shopTourLsMerchant/export', {
        ...this.queryParams
      }, `shopTourLsMerchant_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
