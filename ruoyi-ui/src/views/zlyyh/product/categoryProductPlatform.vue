<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入商品编号" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="商品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择商品类型" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_type" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="商品归属" prop="productAffiliation">
        <el-select v-model="queryParams.productAffiliation" placeholder="请选择商品归属" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_affiliation" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="领取方式" prop="pickupMethod">
        <el-select v-model="queryParams.pickupMethod" placeholder="请选择领取方式" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_pickup_method" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_status" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="外部产品号" prop="externalProductId" label-width="90px">
        <el-input v-model="queryParams.externalProductId" placeholder="请输入外部产品号" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="展示开始时间" prop="showStartDate" label-width="120">
        <el-date-picker clearable v-model="showStartDate" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="展示结束时间" prop="showEndDate" label-width="120">
        <el-date-picker clearable v-model="showEndDate" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="展示城市" prop="showCity">
        <el-select v-model="queryParams.showCity" placeholder="请选择展示城市" clearable>
          <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label" :value="item.rightLabel"/>
        </el-select>
      </el-form-item>
      <el-form-item label="显示首页" prop="showIndex">
        <el-select v-model="queryParams.showIndex" placeholder="请选择是否显示首页" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_show_index" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-show="isProduct === true" plain size="mini" @click="handleProduct(0)">选择商品
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isProduct === true" :loading="buttonLoading" type="danger" plain
                   icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">解除关联
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isProduct === false" plain size="mini" @click="handleProduct(1)">查看商品
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isProduct === false" :loading="buttonLoading" type="primary" plain
                   icon="el-icon-check" size="mini" :disabled="multiple" @click="handleAddProduct">绑定关系
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table :title="title" v-loading="loading" :data="productList"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="商品编号" align="center" prop="productId" width="170" fixed="left"/>
      <el-table-column label="商品名称" align="center" prop="productName" width="150" :show-overflow-tooltip="true"
                       fixed="left"/>
      <el-table-column label="平台" align="center" prop="platformName" width="100" :formatter="changePlatform"/>
      <el-table-column label="商品归属" align="center" prop="productAffiliation" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_affiliation" :value="scope.row.productAffiliation"/>
        </template>
      </el-table-column>
      <el-table-column label="商品类型" align="center" prop="productType" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_type" :value="scope.row.productType"/>
        </template>
      </el-table-column>
      <el-table-column label="领取方式" align="center" prop="pickupMethod" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_pickup_method" :value="scope.row.pickupMethod"/>
        </template>
      </el-table-column>
      <el-table-column label="市场价格" align="center" prop="originalAmount" width="80"/>
      <el-table-column label="售卖价格" align="center" prop="sellAmount" width="80"/>
      <el-table-column label="62会员价格" align="center" prop="vipUpAmount" width="90"/>
      <el-table-column label="权益会员价格" align="center" prop="vipAmount" width="100"/>
      <el-table-column label="外部产品号" align="center" prop="externalProductId" width="90"/>
      <el-table-column label="状态" align="center" prop="status" fixed="right" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="展示开始时间" align="center" prop="showStartDate" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.showStartDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="展示结束时间" align="center" prop="showEndDate" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.showEndDate) }}</span>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>
  </div>
</template>

<script>
import {
  categoryPlatformProductList,

} from "@/api/zlyyh/product";
import {
  selectCityList
} from "@/api/zlyyh/area"
import {selectListPlatform} from "@/api/zlyyh/platform"
import {listSelectMerchant} from "@/api/zlyyh/merchant"
import {selectListMerchant} from "@/api/zlyyh/commercialTenant"
import {selectListDistributor} from "@/api/zlyyh/distributor"
import {selectListCategory} from "@/api/zlyyh/category"
import item from "@/layout/components/Sidebar/Item.vue";
import {
  addProductByCategory,
  delProductByCategory
} from "@/api/zlyyh/categoryPlatformProduct";
export default {
  name: "categoryProductPlatform",
  components: {},
  computed: {
    item() {
      return item
    }
  },
  props: {
    categoryPlatformId: {
      type: String,
      default: undefined
    }
  },
  watch: {
    categoryPlatformId: {
      deep: true,
      handler() {
        this.isProduct = true
        this.getList()
      }
    }
  },
  dicts: ['t_product_to_type', 't_product_status', 't_product_affiliation', 't_product_assign_date', 't_product_type',
    't_product_show_original_amount', 't_product_pickup_method', 't_grad_period_date_list', 't_product_search',
    't_search_status', 't_product_pay_user', 't_show_index', 't_product_send_account_type', 't_cus_refund',
    'sys_normal_disable', 't_product_check_pay_city','t_product_union_pay', 'sys_yes_no', 'channel_type'
  ],
  data() {
    return {
      tableHeight: document.documentElement.scrollHeight - 245 + "px",
      activeName: "basicCoupon",
      tabNameList: ["basicCoupon", "couponCount", "expand", "ticket", "session"],
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      isProduct: true,
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
      // 商品表格数据
      productList: [],
      // 标签列表
      tagsList: [],
      platformList: [],
      merchantList: [],
      commercialTenantList: [],
      cityList: [],
      categoryList: [],
      cityNodeAll: false,
      defaultProps: {
        children: "children",
        label: "label"
      },
      showStartDate: [],
      showEndDate: [],
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
      //城市列表
      cityOptions: [],
      distributorList: [],
      // 供应商
      supplierList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,

      dayCount: '',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        externalProductId: undefined,
        productId: undefined,
        productName: undefined,
        productAbbreviation: undefined,
        productSubhead: undefined,
        productImg: undefined,
        productAffiliation: undefined,
        productType: undefined,
        pickupMethod: undefined,
        showOriginalAmount: undefined,
        originalAmount: undefined,
        sellAmount: undefined,
        vipUpAmount: undefined,
        vipAmount: undefined,
        toType: undefined,
        showIndex: undefined,
        status: undefined,
        showStartDate: undefined,
        showEndDate: undefined,
        sellStartDate: undefined,
        sellEndDate: undefined,
        assignDate: undefined,
        weekDate: undefined,
        sellTime: undefined,
        totalCount: undefined,
        monthCount: undefined,
        weekCount: undefined,
        dayCount: undefined,
        dayUserCount: undefined,
        weekUserCount: undefined,
        monthUserCount: undefined,
        totalUserCount: undefined,
        description: undefined,
        providerLogo: undefined,
        providerName: undefined,
        showCity: undefined,
        merchantId: undefined,
        shopGroupId: undefined,
        shopId: undefined,
        btnText: undefined,
        shareTitle: undefined,
        shareName: undefined,
        shareImage: undefined,
        platformKey: undefined,
        sort: undefined,
        orderByColumn: "product_id",
        isAsc: 'desc',
        categoryPlatformId: undefined,
      },
      // 表单参数
      form: {},

      isUpdate: false
    };
  },
  created() {
    this.getList();

    selectListPlatform({}).then(res => {
      this.platformList = res.data;
    })
    listSelectMerchant({}).then(res => {
      this.merchantList = res.data;
    })
    let merchantParams = {
      status: "0"
    }
    selectListMerchant(merchantParams).then(res => {
      this.commercialTenantList = res.data;
    })
    let categoryParams = {
      categoryListType: "0",
      status: "0"
    }
    selectListCategory(categoryParams).then(res => {
      this.categoryList = res.data;
    })
    selectListDistributor({
      status: "0"
    }).then(res => {
      this.distributorList = res.data;
    })
    // this.getTagsList();
    // this.selectSupplierList();
  },
  methods: {
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.productId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    selectAll(val) {
      if (this.cityNodeAll) {
        // 	设置目前勾选的节点，使用此方法必须设置 node-key 属性
        this.$refs.city.setCheckedNodes([])
      } else {
        // 全部不选中
        this.$refs.city.setCheckedNodes([])
      }
    },
    //city下拉列表
    getCitySelectList() {
      let cityForm = {
        level: "city"
      }
      selectCityList(cityForm).then(response => {
        this.cityList = response.data;
      })
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


    /** 查询商品列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.showStartDate && '' != this.showStartDate) {
        this.queryParams.params["beginStartDate"] = this.showStartDate[0];
        this.queryParams.params["endStartDate"] = this.showStartDate[1];
      }
      if (null != this.showEndDate && '' != this.showEndDate) {
        this.queryParams.params["beginEndDate"] = this.showEndDate[0];
        this.queryParams.params["endEndDate"] = this.showEndDate[1];
      }
      this.queryParams.categoryPlatformId = this.categoryPlatformId;
      if (this.isProduct) {
        this.queryParams.sort = 0;
      } else {
        this.queryParams.sort = 1;
      }
      categoryPlatformProductList(this.queryParams).then(response => {
        this.productList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
    },
    handleProduct(index) {
      this.isProduct = index !== 0;
      this.handleQuery();
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

    /** 删除按钮操作 */
    handleDelete(row) {
      const productIds = row.productId || this.ids;
      this.$modal.confirm('解除门店编号为"' + productIds + '"与商品的关系？').then(() => {
        this.loading = true;
        const param = {
          'productIds': productIds,
          'categoryPlatformId': this.categoryPlatformId
        }
        delProductByCategory(param).then(response => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("解除成功");
          this.open = false;
        }).finally(() => {
          this.loading = false;
        });
      }).then(() => {
      }).catch(() => {
      }).finally(() => {
      });
    },
    /**
     * 确定选择门店
     */
    handleAddProduct() {
      const productIds = this.ids;
      this.$modal.confirm('绑定商品编号为"' + productIds + '"与栏目的关系？').then(() => {
        this.loading = true;
        const param = {
          'productIds': productIds,
          'categoryPlatformId': this.categoryPlatformId
        }
        addProductByCategory(param).then(response => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("绑定成功");
          this.open = false;
        }).finally(() => {
          this.loading = false;
        });
      }).then(() => {
      }).catch(() => {
      }).finally(() => {
      });
    },
  }


}
</script>

<style scoped>

</style>
