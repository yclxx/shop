<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" @change="getPlatformSelectList"
                   clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="品牌名称" prop="brandName">
        <el-input
          v-model="queryParams.brandName"
          placeholder="请输入品牌名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="门店名称" prop="shopName">
        <el-input
          v-model="queryParams.shopName"
          placeholder="请输入门店名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="门店电话" prop="shopMobile">
        <el-input
          v-model="queryParams.shopMobile"
          placeholder="请输入门店电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性质" prop="nature">
        <el-input
          v-model="queryParams.nature"
          placeholder="请输入性质"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款账户" prop="account">
        <el-input
          v-model="queryParams.account"
          placeholder="请输入收款账户"
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:merchantApproval:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="merchantApprovalList">
      <el-table-column label="平台标识" align="center" prop="platformKey" width="90px" :formatter="platformFormatter"/>
      <el-table-column label="品牌信息" align="center" prop="brandName" width="200px">
        <template slot-scope="scope">
          <div>名称：{{ scope.row.brandName }}</div>
          <div>图片：
            <image-preview :src="scope.row.brandLogo" :width="25" :height="25"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="门店信息" align="center" prop="shopName" width="250px">
        <template slot-scope="scope">
          <div>门店：{{ scope.row.shopName }}</div>
          <div>电话：{{ scope.row.shopMobile }}</div>
          <div>所在区域：{{ scope.row.shopAddress }}</div>
          <div>详情地址：{{ scope.row.shopAddressInfo }}</div>
        </template>
      </el-table-column>
      <el-table-column label="收单商户号" align="center" prop="account" width="250px">
        <template slot-scope="scope">
          <div>收款账户：{{ scope.row.account }}</div>
          <div>云闪付商户号：{{ scope.row.ysfMerchant }}</div>
          <div>微信商户号：{{ scope.row.wxMerchant }}</div>
          <div>支付宝商户号：{{ scope.row.payMerchant }}</div>
        </template>
      </el-table-column>
      <el-table-column label="营业信息" align="center" prop="businessWeek" width="170px">
        <template slot-scope="scope">
          <span v-for="week in dict.type.t_grad_period_date_list" :key="week.value">
            <span v-for="business in scope.row.businessWeek.split(',')" v-if="business === week.value">
              {{ week.label }},
            </span>
          </span>
          <div>{{ scope.row.businessBegin }}-{{ scope.row.businessEnd }}</div>
        </template>
      </el-table-column>
      <el-table-column label="性质" align="center" prop="nature">
        <template slot-scope="scope">
          <span v-for="na in dict.type.nature_type" v-if="na.value === scope.row.nature">{{ na.label }}</span>
        </template>
      </el-table-column>
      <el-table-column label="参与活动" align="center" prop="activity" width="200px">
        <template slot-scope="scope">
          <span v-for="ac in dict.type.activity_type" :key="ac.value">
            <span v-for="activity in scope.row.activity.split(',')" v-if="activity === ac.value">
              {{ ac.label }},
            </span>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发票类型" align="center" prop="invoiceType">
        <template slot-scope="scope">
          <span v-for="invoice in dict.type.nature_type" v-if="invoice.value === scope.row.invoiceType">
            {{ invoice.label }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品类型" align="center" prop="productType">
        <template slot-scope="scope">
          <span v-for="product in productTypeList" :key="product.value">
            <span v-for="type in scope.row.productType.split(',')" v-if="type === product.value">
              {{ product.label }},
            </span>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="扩展服务商" align="center" prop="extend" width="200px"/>
      <el-table-column fixed="right" label="审批" align="center" prop="approvalStatus" width="150px">
        <template slot-scope="scope">
          <div> 状态：
            <span v-for="approval in approvalStatusList" v-if="approval.value === scope.row.approvalStatus">
            {{ approval.label }}
          </span>
          </div>
          <div v-if="scope.row.approvalStatus === '2'">拒绝原因：{{ scope.row.rejectReason }}</div>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.approvalStatus === '0'"
                     size="mini"
                     type="text"
                     icon="el-icon-check"
                     @click="handleApproval(scope.row,'1')"
                     v-hasPermi="['zlyyh:merchantApproval:edit']"
          >通过
          </el-button>
          <el-button v-if="scope.row.approvalStatus === '0'"
                     size="mini"
                     type="text"
                     icon="el-icon-close"
                     @click="handleOpen(scope.row,'2')"
                     v-hasPermi="['zlyyh:merchantApproval:edit']"
          >拒绝
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
    <el-dialog title="拒绝原因" :visible.sync="open" width="500px" append-to-body>
      <div>
        <el-input v-model="form.rejectReason" placeholder="请输入拒绝原因"></el-input>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">取 消</el-button>
        <el-button type="primary" @click="handlerReject()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMerchantApproval, updateMerchantApproval,
} from "@/api/zlyyh/merchantApproval";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "MerchantApproval",
  // 此页面大多数参数均未使用数据字典
  dicts: ['nature_type', 'invoice_type', 'activity_type', 't_grad_period_date_list'],
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      open: false,
      // 总条数
      total: 0,
      // 商户申请审批表格数据
      merchantApprovalList: [],
      platformList: [],
      approvalStatusList: [
        {label: '待审批', value: '0'},
        {label: '已通过', value: '1'},
        {label: '已拒绝', value: '2'}
      ],
      productTypeList: [
        {label: '包邮产品', value: '0'},
        {label: '一般团购', value: '1'},
        {label: '演出票', value: '2'},
        {label: '酒店门票', value: '3'}
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        brandName: undefined,
        shopName: undefined,
        shopMobile: undefined,
        nature: undefined,
        account: undefined,
      },
      form: {
        approvalId: undefined,
        approvalStatus: undefined,
        rejectReason: undefined
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
  },
  methods: {
    getPlatformSelectList() {
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
    },
    /** 查询商户申请审批列表 */
    getList() {
      this.loading = true;
      listMerchantApproval(this.queryParams).then(response => {
        this.merchantApprovalList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
    handleOpen(row, status) {
      this.form.approvalId = row.approvalId
      this.form.approvalStatus = status;
      this.form.rejectReason = row.rejectReason;
      this.open = true;
    },
    // 拒绝
    handlerReject() {
      updateMerchantApproval(this.form).then(response => {
        this.getList();
      });
      this.open = false;
    },
    /** 通过 */
    handleApproval(row, status) {
      this.form.approvalId = row.approvalId
      this.form.approvalStatus = status;
      this.form.rejectReason = undefined;
      updateMerchantApproval(this.form).then(response => {
        this.getList();
      });
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh-admin/merchantApproval/export', {
        ...this.queryParams
      }, `merchantApproval_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
