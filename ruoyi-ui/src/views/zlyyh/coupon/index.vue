<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="优惠券名称" prop="couponName">
        <el-input
          v-model="queryParams.couponName"
          placeholder="请输入优惠券名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠券兑换码" prop="redeemCode">
        <el-input
          v-model="queryParams.redeemCode"
          placeholder="请输入优惠券兑换码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠金额" prop="couponAmount">
        <el-input
          v-model="queryParams.couponAmount"
          placeholder="请输入优惠金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最低消费金额" prop="minAmount">
        <el-input
          v-model="queryParams.minAmount"
          placeholder="请输入最低消费金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用日期" prop="useTime">
        <el-date-picker clearable
                        v-model="queryParams.useTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择使用日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="订单号" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户获得时间" prop="userAddTime">
        <el-date-picker clearable
                        v-model="queryParams.userAddTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择用户获得时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="批次号" prop="actionNo">
        <el-input
          v-model="queryParams.actionNo"
          placeholder="请输入批次号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
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
          icon="el-icon-edit"
          size="mini"
          @click="handleUpdate"
          v-hasPermi="['zlyyh:coupon:edit']"
        >批次号作废
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-remove"
          size="mini"
          :disabled="multiple"
          @click="handleRemove"
          v-hasPermi="['zlyyh:coupon:remove']"
        >批量作废
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:coupon:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="couponList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" align="center"/>
      <el-table-column fixed="left" label="优惠券兑换码" align="center" prop="redeemCode"/>
      <el-table-column label="优惠券信息" align="center" prop="couponName" width="180">
        <template slot-scope="scope">
          <div v-show="scope.row.couponName">
            <span>优惠券名称：{{ scope.row.couponName }}</span>
          </div>
          <div v-show="scope.row.couponAmount">
            <span>优惠金额：{{ scope.row.couponAmount }}</span>
          </div>
          <div v-show="scope.row.minAmount">
            <span>最低消费金额：{{ scope.row.minAmount }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="优惠券类型" align="center" prop="couponType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_coupon_type" :value="scope.row.couponType"/>
        </template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="actionNo"/>
      <el-table-column label="使用起始日期" align="center" prop="periodOfStart" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.periodOfStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用有效截止日期" align="center" prop="periodOfValidity" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.periodOfValidity, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用日期" align="center" prop="useTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.useTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单号" align="center" prop="number"/>
      <el-table-column label="用户获得时间" align="center" prop="userAddTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.userAddTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠券图片" align="center" prop="couponImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.couponImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="兑换起始日期" align="center" prop="conversionStartDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.conversionStartDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="兑换截止日期" align="center" prop="conversionEndDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.conversionEndDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="changePlatform"/>
      <el-table-column label="创建时间" align="center" prop="genTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.genTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="使用状态" align="center" prop="useStatus" :formatter="changeCouponStatus"/>
      <el-table-column fixed="right" label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleProductByCoupon(scope.row)">商品查看
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

    <!-- 添加或修改优惠券对话框 -->
    <el-dialog title="作废优惠券" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="批次号" prop="actionNo">
          <el-input v-model="form.actionNo" placeholder="请输入批次号"/>
        </el-form-item>
      </el-form>
      <div>请注意，此操作仅作废使用状态为"未发送，未使用"的优惠券</div>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="优惠券商品" :visible.sync="productOpen" width="30%" append-to-body>
      <el-table :data="productList">
        <el-table-column label="商品编号" align="center" prop="productId" fixed="left"/>
        <el-table-column label="商品名称" align="center" prop="productName"  :show-overflow-tooltip="true"
                         fixed="left"/>
      </el-table>
      <pagination v-show="productTotal>0" :total="productTotal" :page.sync="queryProductParams.pageNum" :limit.sync="queryProductParams.pageSize"
                  @pagination="handleProductByCoupon()"/>
    </el-dialog>
  </div>
</template>

<script>
import {delCoupon, listCoupon, updateCoupon, productList} from "@/api/zlyyh/coupon";
import Treeselect from "@riophae/vue-treeselect";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "Coupon",
  dicts: ['coupon_status', 't_coupon_type'],
  components: {
    Treeselect
  },
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
      // 优惠券表格数据
      couponList: [],
      platformList: [],
      // 商品列表信息
      queryProductParams: {
        pageNum: 1,
        pageSize: 10,
        couponId: undefined,
      },
      productOpen: false,
      productTotal: 0,
      productList: [],
      // 弹出层标题
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        couponName: undefined,
        redeemCode: undefined,
        couponAmount: undefined,
        minAmount: undefined,
        couponType: undefined,
        useStatus: undefined,
        useTime: undefined,
        number: undefined,
        userAddTime: undefined,
        actionNo: undefined,
        couponImage: undefined,
        userId: undefined,
        platformKey: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        couponId: [
          {required: true, message: "优惠券ID不能为空", trigger: "blur"}
        ],
        couponName: [
          {required: true, message: "优惠券名称不能为空", trigger: "blur"}
        ],
        couponAmount: [
          {required: true, message: "优惠金额不能为空", trigger: "blur"}
        ],
        couponType: [
          {required: true, message: "优惠券类型不能为空", trigger: "change"}
        ]
      }
    };
  },
  created() {
    this.getList();
    selectListPlatform({}).then(res => {
      this.platformList = res.data;
    })
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
    handleProductByCoupon(row) {
      if (row) {
        this.queryProductParams.couponId = row.couponId;
      }
      productList(this.queryProductParams).then(response => {
        this.productList = response.rows;
        this.productTotal = response.total;
        this.productOpen = true;
      });
    },
    changeCouponStatus(row) {
      let couponStatusName = ''
      this.dict.type.coupon_status.forEach(item => {
        if (row.useStatus === item.value) {
          couponStatusName = item.label;
        }
      })
      if (couponStatusName && couponStatusName.length > 0) {
        row.useStatus = couponStatusName;
        return couponStatusName;
      }
      return row.useStatus;
    },
    /** 查询优惠券列表 */
    getList() {
      this.loading = true;
      listCoupon(this.queryParams).then(response => {
        this.couponList = response.rows;
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
        actionNo: undefined,
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
      this.ids = selection.map(item => item.couponId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate() {
      this.reset();
      this.open = true;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        this.buttonLoading = true;
        updateCoupon(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.getList();
        }).finally(() => {
          this.buttonLoading = false;
        });
      });
    },
    /** 批量作废按钮操作 */
    handleRemove(row) {
      const couponIds = row.couponId || this.ids;
      this.$modal.confirm('请确认是否作废选中的的优惠券，仅作废未使用的优惠券').then(() => {
        this.loading = true;
        return delCoupon(couponIds);
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
      this.download('zlyyh-admin/coupon/export', {
        ...this.queryParams
      }, `coupon_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
