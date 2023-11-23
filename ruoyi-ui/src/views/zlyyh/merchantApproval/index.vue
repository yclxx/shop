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
          <div>名称:{{ scope.row.brandName }}</div>
          <div>Logo:
            <image-preview :src="scope.row.brandLogo" :width="30" :height="30"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="门店信息" align="center" prop="shopName" width="250px">
        <template slot-scope="scope">
          <div>门店:{{ scope.row.shopName }}</div>
          <div>电话:{{ scope.row.shopMobile }}</div>
          <div>区域:{{ scope.row.shopAddress }}</div>
          <div>地址:{{ scope.row.shopAddressInfo }}</div>
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
      <el-table-column fixed="right" label="状态" align="center" prop="approvalStatus" width="150px">
        <template slot-scope="scope">
          <div>
            <span v-for="approval in approvalStatusList" v-if="approval.value === scope.row.approvalStatus">
            {{ approval.label }}
          </span>
          </div>
          <div v-if="scope.row.approvalStatus === '2'">拒绝原因：{{ scope.row.rejectReason }}</div>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text"
                     @click="getMerchantApproval(scope.row)"
          >详情
          </el-button>
          <!--<div v-if="scope.row.approvalStatus === '0'">-->
          <!--  <el-button size="mini" type="text" icon="el-icon-check"-->
          <!--             @click="handleApproval(scope.row,'1')"-->
          <!--             v-hasPermi="['zlyyh:merchantApproval:edit']"-->
          <!--  >通过-->
          <!--  </el-button>-->
          <!--  <el-button size="mini" type="text" icon="el-icon-close"-->
          <!--             @click="handleOpen(scope.row,'2')"-->
          <!--             v-hasPermi="['zlyyh:merchantApproval:edit']"-->
          <!--  >拒绝-->
          <!--  </el-button>-->
          <!--</div>-->
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
    <el-dialog :visible.sync="detailsOpen" width="80%">
      <el-descriptions v-if="details" class="margin-top" title="申请信息" border :column="3">
        <template slot="extra" v-if="details.approvalStatus === '0'">
          <el-button icon="el-icon-check" type="primary"
                     @click="handleApproval(details,'1')"
                     v-hasPermi="['zlyyh:merchantApproval:edit']"
          >通过
          </el-button>
          <el-button icon="el-icon-close" type="warning"
                     @click="handleOpen(details,'2')"
                     v-hasPermi="['zlyyh:merchantApproval:edit']"
          >拒绝
          </el-button>
        </template>
        <el-descriptions-item label="类型">
          <span v-show="details.type === '1'">商户申请</span>
          <span v-show="details.type === '2'">bd商户录入</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <span v-for="approval in approvalStatusList" v-if="approval.value === details.approvalStatus">
            {{ approval.label }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="管理员手机号">
          {{ details.mobile }}
        </el-descriptions-item>
        <el-descriptions-item label="品牌名称">
          {{ details.brandName }}
        </el-descriptions-item>
        <el-descriptions-item label="品牌Logo">
          <image-preview :src="details.brandLogo" :width="50" :height="50"/>
        </el-descriptions-item>
        <el-descriptions-item label="商户归属">
          <span v-for="invoice in dict.type.activity_nature" v-if="invoice.value === details.activityNature">
            {{ invoice.label }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="店长">
          {{ details.shopKeeper }}
        </el-descriptions-item>
        <el-descriptions-item label="店长电话">
          {{ details.shopKeeperMobile }}
        </el-descriptions-item>
        <el-descriptions-item label="门店名称">
          {{ details.shopName }}
        </el-descriptions-item>
        <el-descriptions-item label="门店电话">
          {{ details.shopMobile }}
        </el-descriptions-item>
        <el-descriptions-item label="门店类型">
          <span v-for="invoice in dict.type.shop_type" v-if="invoice.value === details.shopType">
            {{ invoice.label }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="门店区域">
          {{ details.shopAddress }}
        </el-descriptions-item>
        <el-descriptions-item label="门店详情地址">
          {{ details.shopAddressInfo }}
        </el-descriptions-item>
        <el-descriptions-item label="门店图片">
          <span v-for="(item,key) in details.shopImage.split(',')" :key="key">
          <image-preview :src="item" :width="50" :height="50"/>
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="营业时间">
          <span v-for="week in dict.type.t_grad_period_date_list" :key="week.value">
            <span v-for="business in details.businessWeek.split(',')" v-if="business === week.value">
              {{ week.label }},
            </span>
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="每天营业开始时间">
          {{ details.businessBegin }}
        </el-descriptions-item>
        <el-descriptions-item label="每天营业结束时间">
          {{ details.businessEnd }}
        </el-descriptions-item>
        <el-descriptions-item label="性质">
          <span v-for="na in dict.type.nature_type" v-if="na.value === details.nature">{{ na.label }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="扩展服务商">
          {{ details.extend }}
        </el-descriptions-item>
        <el-descriptions-item label="参与活动">
          <span v-for="ac in dict.type.activity_type" :key="ac.value">
            <span v-for="activity in details.activity.split(',')" v-if="activity === ac.value">
              {{ ac.label }},
            </span>
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="发票类型">
          <span v-for="invoice in dict.type.invoice_type" v-if="invoice.value === details.invoiceType">
            {{ invoice.label }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="收款账户">
          {{ details.account }}
        </el-descriptions-item>
        <el-descriptions-item label="商品类型">
          <span v-for="product in productTypeList" :key="product.value">
            <span v-for="type in details.productType.split(',')" v-if="type === product.value">
              {{ product.label }},
            </span>
          </span>
        </el-descriptions-item>
        <!--<el-descriptions-item label="商户所在平台">-->
        <!--  {{ details.merchantPlatformKey }}-->
        <!--</el-descriptions-item>-->
        <el-descriptions-item label="营业执照">
          <image-preview :src="details.license" :width="50" :height="50"/>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import {listMerchantApproval, getMerchantApproval, updateMerchantApproval,} from "@/api/zlyyh/merchantApproval";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "MerchantApproval",
  // 此页面大多数参数均未使用数据字典
  dicts: ['nature_type', 'invoice_type', 'activity_type', 't_grad_period_date_list',
    'shop_type', 'activity_nature'],
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
      },
      detailsOpen: false,
      details: undefined,
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
    getMerchantApproval(row) {
      getMerchantApproval(row.approvalId).then(response => {
        this.loading = false;
        this.details = response.data;
        this.detailsOpen = true;
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
      this.detailsOpen = false;
    },
    /** 通过 */
    handleApproval(row, status) {
      this.form.approvalId = row.approvalId
      this.form.approvalStatus = status;
      this.form.rejectReason = undefined;
      updateMerchantApproval(this.form).then(response => {
        this.detailsOpen = false;
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
