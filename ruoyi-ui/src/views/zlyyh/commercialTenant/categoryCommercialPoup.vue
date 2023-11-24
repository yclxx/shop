<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商户名称" prop="commercialTenantName">
        <el-input v-model="queryParams.commercialTenantName" placeholder="请输入商户名称" clearable
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_commercial_tenant_status" :key="dict.value" :label="dict.label"
                     :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="首页显示" prop="indexShow">
        <el-select v-model="queryParams.indexShow" placeholder="请选择是否显示在首页" clearable>
          <el-option v-for="dict in dict.type.t_commercial_tenant_index_show" :key="dict.value" :label="dict.label"
                     :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="生效时间" prop="startTime">
        <el-date-picker clearable v-model="startTime" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间" prop="endTime">
        <el-date-picker clearable v-model="endTime" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-show="isCommercial === true" plain size="mini" @click="handleCommercial(0)">选择商户
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isCommercial === true" :loading="buttonLoading" type="danger" plain
                   icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">解除关联
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isCommercial === false" plain size="mini" @click="handleCommercial(1)">查看商户
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-show="isCommercial === false" :loading="buttonLoading" type="primary" plain
                   icon="el-icon-check" size="mini" :disabled="multiple" @click="handleAddCommercial">绑定关系
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commercialTenantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商户ID" align="center" prop="commercialTenantId" v-if="true" />
      <el-table-column label="商户名称" align="center" prop="commercialTenantName" />
      <el-table-column label="logo" align="center" prop="commercialTenantImg">
        <template slot-scope="scope">
          <image-preview :src="scope.row.commercialTenantImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="标签" align="center" prop="tags" show-overflow-tooltip />
      <el-table-column label="生效时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="首页显示" align="center" prop="indexShow">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_commercial_tenant_index_show" :value="scope.row.indexShow" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_commercial_tenant_status" :value="scope.row.status" />
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList" />

  </div>
</template>

<script>
import {
  categoryCommercialList,

} from "@/api/zlyyh/commercialTenant";
import {selectListPlatform} from "@/api/zlyyh/platform"
import item from "@/layout/components/Sidebar/Item.vue";
import {
  addProductByCategory,
  delProductByCategory
} from "@/api/zlyyh/categoryProduct";

export default {
  name: "categoryCommercialPoup",
  computed: {
    item() {
      return item
    }
  },
  props: {
    categoryId: {
      type: Object,
      default: undefined
    }
  },
  watch: {
    categoryId: {
      deep: true,
      handler() {
        this.isCommercial = true
        this.getList()
      }
    }
  },
  dicts: ['t_commercial_tenant_index_show', 't_commercial_tenant_status',
    'nature_type', 'invoice_type', 'activity_type', 'sys_yes_no'],

  data() {
    return {
      isCommercial: true,
      //栏目下拉列表
      categoryList: [],
      categoryForm: {
        categoryListType: 1,
      },
      //商品下拉列表
      productList: [],
      // 供应商
      supplierList: [],
      //生效时间范围
      startTime: [],
      //失效时间范围
      endTime: [],
      //平台下拉列表
      platformList: [],
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
      // 商户表格数据
      commercialTenantList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 标签
      tagsList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderByColumn: "commercial_tenant_id",
        isAsc: 'desc',
        commercialTenantName: undefined,
        commercialTenantImg: undefined,
        tags: undefined,
        startTime: undefined,
        endTime: undefined,
        indexShow: undefined,
        status: undefined,
        sort: undefined,
        platformKey: undefined
      },
      // 表单参数
      form: {},
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
    }

  },

  created() {
    this.getList();

    selectListPlatform({}).then(res => {
      this.platformList = res.data;
    })


  },
  methods: {
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.commercialTenantId)
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

      this.queryParams.categoryId = this.categoryId;
      if (this.isCommercial) {
        this.queryParams.sort = 0;
      } else {
        this.queryParams.sort = 1;
      }
      //categoryCommercialList
      categoryCommercialList(this.queryParams).then(response => {
        this.commercialTenantList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleCommercial(index) {
      this.isCommercial = index !== 0;
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
      const commercialTenantIds = row.commercialTenantId || this.ids;
      this.$modal.confirm('解除门店编号为"' + commercialTenantIds + '"与商品的关系？').then(() => {
        this.loading = true;
        const param = {
          'productIds': commercialTenantIds,
          'categoryId': this.categoryId
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
    handleAddCommercial() {
      const commercialTenantIds = this.ids;
      this.$modal.confirm('绑定商品编号为"' + commercialTenantIds + '"与栏目的关系？').then(() => {
        this.loading = true;
        const param = {
          'productIds': commercialTenantIds,
          'categoryId': this.categoryId
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


