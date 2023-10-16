<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商户" prop="commercialTenantId">
        <el-select v-model="queryParams.commercialTenantId" placeholder="请选择商户" clearable>
          <el-option v-for="item in commercialTenantList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="门店信息" prop="shopName">
        <el-input v-model="queryParams.shopName" placeholder="门店名称/门店地址" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="省市县" prop="province">
        <el-input v-model="queryParams.province" placeholder="请输入省/市/县" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_shop_status" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-show="isProduct === true" plain size="mini" @click="handleShop(0)">选择门店
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isProduct === true" :loading="buttonLoading" type="danger" plain
                   icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">解除关联
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isProduct === false" plain size="mini" @click="handleShop(1)">查看门店
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isProduct === false" :loading="buttonLoading" type="primary" plain
                   icon="el-icon-check" size="mini" :disabled="multiple" @click="handleAddShop">绑定关系
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table :title="title" v-loading="loading" :data="shopList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="商户" align="center" prop="commercialTenantId" :formatter="merFormatter"/>
      <el-table-column label="门店名称" align="center" prop="shopName"/>
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="门店地址" width="150" align="center" prop="address" show-overflow-tooltip/>
      <el-table-column label="省市区" align="left" prop="province" width="130">
        <template slot-scope="scope">
          <div>
            <span>省：{{ scope.row.province }}</span><br>
            <span>市：{{ scope.row.city }}</span><br>
            <span>县(区)：{{ scope.row.district }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="省市区编码" align="left" prop="procode" width="180">
        <template slot-scope="scope">
          <div>
            <span>省行政编码：{{ scope.row.procode }}</span><br>
            <span>市行政编码：{{ scope.row.citycode }}</span><br>
            <span>县(区)行政编码：{{ scope.row.adcode }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="经纬度" align="left" prop="longitude" width="150">
        <template slot-scope="scope">
          <div>
            <span>经度：{{ scope.row.longitude }}</span><br>
            <span>纬度：{{ scope.row.latitude }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="门店电话" align="center" prop="shopTel" width="109"/>
      <el-table-column label="营业时间" width="115" align="center" prop="businessHours"/>
      <el-table-column label="门店ID" align="center" prop="shopId" v-if="true"/>
      <el-table-column label="状态" align="center" prop="status" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_shop_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

  </div>
</template>

<script>
import {listShops} from "@/api/zlyyh/shop";
import {
  addShopByProduct,
  delByShopProduct
} from "@/api/zlyyh/shopProduct";
import {selectListMerchant} from "@/api/zlyyh/commercialTenant";
import {selectListPlatform} from "@/api/zlyyh/platform";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import Treeselect from "@riophae/vue-treeselect";

export default {
  name: "Shop",
  dicts: ['t_shop_status'],
  components: {
    Treeselect
  },
  props: {
    productId: {
      type: String,
      default: undefined
    }
  },
  watch: {
    productId: {
      deep: true,
      handler() {
        this.isProduct = true;
        this.getList()
      }
    }
  },
  data() {
    return {
      // 部门树选项
      deptOptions: undefined,
      shopMerchatOpen: false,
      // 产品数据展示
      isProduct: true,
      //平台下拉列表
      platformList: [],
      //商户下拉列表
      commercialTenantList: [],
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
      // 门店表格数据
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderByColumn: "shop_id",
        isAsc: 'desc',
        commercialTenantId: undefined,
        sort: 0,
        productId: undefined,
        shopName: undefined,
        shopTel: undefined,
        status: undefined,
        province: undefined,
        district: undefined,
        platformKey: undefined
      },
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
    this.getMerSelectList();
  },
  methods: {
    /** 查询门店列表 */
    getList() {
      this.queryParams.productId = this.productId;
      if (this.isProduct) {
        this.queryParams.sort = 0;
      } else {
        this.queryParams.sort = 1;
      }
      this.loading = true;
      listShops(this.queryParams).then(response => {
        this.shopList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 平台标识下拉列表
    getPlatformSelectList() {
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
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
    //商户下拉列表
    getMerSelectList() {
      selectListMerchant({}).then(response => {
        this.commercialTenantList = response.data;
      });
    },
    merFormatter(row) {
      let name = '';
      this.commercialTenantList.forEach(item => {
        if (item.id == row.commercialTenantId) {
          name = item.label;
        }
      })
      if (name && name.length > 0) {
        row.commercialTenantName = name;
        return name;
      }
      return row.commercialTenantId;
    },
    handleShop(index) {
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.shopId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const shopIds = row.shopId || this.ids;
      this.$modal.confirm('解除门店编号为"' + shopIds + '"与商品的关系？').then(() => {
        this.loading = true;
        const param = {
          'shopIds': shopIds,
          'productId': this.productId
        }
        delByShopProduct(param).then(response => {
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
    handleAddShop() {
      const shopIds = this.ids;
      this.$modal.confirm('解除门店编号为"' + shopIds + '"与商品的关系？').then(() => {
        this.loading = true;
        const param = {
          'shopIds': shopIds,
          'productId': this.productId
        }
        addShopByProduct(param).then(response => {
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
};
</script>
