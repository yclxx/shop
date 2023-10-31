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
      <!--<el-form-item label="展示城市" prop="showCity">-->
      <!--  <el-select v-model="queryParams.showCity" placeholder="请选择展示城市" clearable>-->
      <!--    <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label" :value="item.rightLabel"/>-->
      <!--  </el-select>-->
      <!--</el-form-item>-->
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
        <el-button v-show="isAction === true" plain size="mini" @click="handleAction(1)">选择商品
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isAction === true" :loading="buttonLoading" type="danger" plain
                   icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleProductAction(0)">解除关联
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isAction === false" plain size="mini" @click="handleAction(0)">查看商品
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isAction === false" :loading="buttonLoading" type="primary" plain
                   icon="el-icon-check" size="mini" :disabled="multiple" @click="handleProductAction(1)">绑定关联
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
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
import {joinListProduct} from "@/api/zlyyh/product";
import {selectListPlatform} from "@/api/zlyyh/platform"
import {addAction, updateActionProduct} from "@/api/zlyyh/action"
import item from "@/layout/components/Sidebar/Item.vue";

export default {
  name: "Product",
  components: {},
  computed: {
    item() {
      return item
    }
  },
  props: {
    actionId: {
      default: undefined
    }
  },
  watch: {
    actionId: {
      deep: true,
      handler() {
        this.getList()
      }
    }
  },
  dicts: ['t_product_status', 't_product_affiliation', 't_product_type', 't_product_pickup_method', 't_show_index',],
  data() {
    return {
      isAction: true,
      productId: undefined,
      // 按钮loading
      buttonLoading: false,
      // 商品门店
      ShopVisible: false,
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
      // 商品表格数据
      productList: [],
      platformList: [],
      cityList: [],
      cityNodeAll: false,
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
      // 供应商
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        externalProductId: undefined,
        productId: undefined,
        productName: undefined,
        productAbbreviation: undefined,
        productAffiliation: undefined,
        productType: undefined,
        pickupMethod: undefined,
        originalAmount: undefined,
        sellAmount: undefined,
        vipUpAmount: undefined,
        vipAmount: undefined,
        showIndex: undefined,
        status: undefined,
        showStartDate: undefined,
        showEndDate: undefined,
        platformKey: undefined,
        actionId: undefined,
        sort: undefined,
        orderByColumn: "product_id",
        isAsc: 'desc'
      },
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
        if (row.platformKey === item.id) {
          platformName = item.label;
        }
      })
      if (platformName && platformName.length > 0) {
        row.platformName = platformName;
        return platformName;
      }
      return row.platformKey;
    },
    handleAction(index) {
      this.isAction = (index === 0);
      this.handleQuery();
    },
    /** 查询商品列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.showStartDate && '' !== this.showStartDate) {
        this.queryParams.params["beginStartDate"] = this.showStartDate[0];
        this.queryParams.params["endStartDate"] = this.showStartDate[1];
      }
      if (null != this.showEndDate && '' !== this.showEndDate) {
        this.queryParams.params["beginEndDate"] = this.showEndDate[0];
        this.queryParams.params["endEndDate"] = this.showEndDate[1];
      }
      if (this.actionId && this.actionId !== '') {
        this.queryParams.actionId = this.actionId;
        if (this.isAction) {
          this.queryParams.sort = 1;
        } else {
          this.queryParams.sort = 0;
        }
      }
      joinListProduct(this.queryParams).then(response => {
        this.productList = response.rows;
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.productId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleProductAction(type) {
      const param = {
        'productIds': this.ids,
        'actionId': this.actionId,
        'type': type
      }
      updateActionProduct(param).then(response => {
        if (type === 1) {
          this.isAction = true;
          this.$modal.msgSuccess("绑定成功");
        } else if (type === 0) {
          this.isAction = false;
          this.$modal.msgSuccess("解绑成功");
        }
        this.open = false;
        this.getList();
      }).finally(() => {
        this.buttonLoading = false;
      });
    }
  }
};
</script>
