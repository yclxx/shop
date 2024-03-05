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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:commercialTenant:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:commercialTenant:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:commercialTenant:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:commercialTenant:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commercialTenantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商户ID" align="center" prop="commercialTenantId" v-if="true" />
      <el-table-column label="品牌名称" align="center" prop="commercialTenantName">
        <template slot-scope="scope">
          <span v-if="scope.row.source == 0 && scope.row.brandId">
            {{scope.row.commercialTenantName}}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="商户名称" align="center" prop="commercialTenantTitle">
        <template slot-scope="scope">
          <span v-if="scope.row.source == 0 && scope.row.brandId">
            {{scope.row.commercialTenantTitle}}
          </span>
          <span v-else>
            {{scope.row.commercialTenantName}}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="logo" align="center" prop="commercialTenantImg">
        <template slot-scope="scope">
          <image-preview :src="scope.row.commercialTenantImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="标签" align="center" prop="tags" show-overflow-tooltip /> -->
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
      <el-table-column label="状态" align="center" prop="status" width="78">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_commercial_tenant_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:commercialTenant:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:commercialTenant:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="商户名称" prop="commercialTenantName">
              <el-input v-model="form.commercialTenantName" placeholder="请输入商户名称" />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.t_commercial_tenant_status" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="生效时间" prop="startTime">
              <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="失效时间" prop="endTime">
              <el-date-picker clearable v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择失效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入排序" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="商品" prop="productIds">
              <el-select v-model="form.productIds" placeholder="请选择商品" multiple clearable>
                <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
                  <span style="float: left">{{ item.label }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="栏目" prop="categoryIds">
              <el-select v-model="form.categoryIds" placeholder="请选择栏目" multiple clearable>
                <el-option v-for="item in categoryList" :key="item.id" :label="item.label" :value="item.id">
                  <span style="float: left">{{ item.label }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="首页显示" prop="indexShow" label-width="95px">
              <el-select v-model="form.indexShow" placeholder="请选择是否显示在首页">
                <el-option v-for="dict in dict.type.t_commercial_tenant_index_show" :key="dict.value"
                  :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="共享" prop="isShare" label-width="95px">
              <el-select v-model="form.isShare" placeholder="请选择是否共享">
                <el-option v-for="dict in dict.type.sys_yes_no" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商" prop="supplier">
              <el-select v-model="form.supplier" placeholder="请选择供应商" style="width: 100%;">
                <el-option v-for="dict in supplierList" :key="dict.id" :label="dict.label" :value="dict.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="收款账户" prop="account" label-width="95px">
              <el-input v-model="form.account" placeholder="请输入收款账户" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="性质" prop="nature" label-width="95px">
              <el-select v-model="form.nature" placeholder="请选择性质">
                <el-option v-for="dict in dict.type.nature_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="活动类型" prop="activity" label-width="95px">
              <el-select v-model="form.activity" placeholder="请选择活动类型">
                <el-option v-for="dict in dict.type.activity_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发票类型" prop="invoice" label-width="95px">
              <el-select v-model="form.invoice" placeholder="请选择发票类型">
                <el-option v-for="dict in dict.type.invoice_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标签" prop="tags">
              <el-select v-model="form.tags" multiple placeholder="请选择标签">
                <el-option v-for="item in tagsList" :key="item.tagsId" :label="item.tagsName" :value="item.tagsName">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10">
            <el-form-item label="logo" prop="commercialTenantImg">
              <image-upload :limit="1" v-model="form.commercialTenantImg" />
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="营业执照" prop="license">
              <image-upload :limit="1" v-model="form.license" />
            </el-form-item>
          </el-col>
        </el-row>
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
    listCommercialTenant,
    getCommercialTenant,
    delCommercialTenant,
    addCommercialTenant,
    updateCommercialTenant
  } from "@/api/zlyyh/commercialTenant";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    selectListProduct
  } from "@/api/zlyyh/product";
  import {
    selectListCategory
  } from "@/api/zlyyh/category";
  import {
    exportTags
  } from "@/api/zlyyh/tags";
  import {
    selectSupplier
  } from "@/api/zlyyh/supplier";

  export default {
    name: "CommercialTenant",
    dicts: ['t_commercial_tenant_index_show', 't_commercial_tenant_status',
      'nature_type', 'invoice_type', 'activity_type', 'sys_yes_no'
    ],
    data() {
      return {
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
        // 表单校验
        rules: {
          commercialTenantId: [{
            required: true,
            message: "商户ID不能为空",
            trigger: "blur"
          }],
          commercialTenantName: [{
            required: true,
            message: "商户名称不能为空",
            trigger: "blur"
          }],

        },
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
      };
    },
    created() {
      this.getList();
      this.getPlatformSelectList();
      //this.getProductSelectList();
      this.getCategorySelectList();
      this.getTagsList();
      this.selectSupplierList();
    },
    methods: {
      /** 查询商户列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.startTime && '' != this.startTime) {
          this.queryParams.params["beginStartTime"] = this.startTime[0];
          this.queryParams.params["endStartTime"] = this.startTime[1];
        }
        if (null != this.endTime && '' != this.endTime) {
          this.queryParams.params["beginEndTime"] = this.endTime[0];
          this.queryParams.params["endEndTime"] = this.endTime[1];
        }
        listCommercialTenant(this.queryParams).then(response => {
          this.commercialTenantList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      //栏目下拉列表
      getCategorySelectList() {
        selectListCategory(this.categoryForm).then(response => {
          this.categoryList = response.data;
        });
      },
      //商品下拉列表
      getProductSelectList() {
        selectListProduct({
          status: '0'
        }).then(response => {
          this.productList = response.data;
        });
      },
      //平台标识下拉列表
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
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          commercialTenantId: undefined,
          commercialTenantName: undefined,
          commercialTenantImg: undefined,
          tags: undefined,
          startTime: undefined,
          endTime: undefined,
          indexShow: undefined,
          status: '0',
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          platformKey: undefined,
          isShare: undefined,
          supplier: undefined,
          license: undefined,
          nature: undefined,
          invoice: undefined,
          account: undefined,
          activity: undefined
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
        this.startTime = null;
        this.endTime = null;
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.commercialTenantId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加商户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const commercialTenantId = row.commercialTenantId || this.ids
        getCommercialTenant(commercialTenantId).then(response => {
          this.loading = false;
          if (response.data.tags != undefined && response.data.tags != null && response.data.tags != '') {
            response.data.tags = response.data.tags.split(',');
          }
          this.form = response.data;
          this.open = true;
          this.title = "修改商户";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.tags != undefined && this.form.tags != null && this.form.tags != '') {
              this.form.tags = this.form.tags.join(',')
            } else {
              this.form.tags = ''
            }
            if (this.form.commercialTenantId != null) {
              updateCommercialTenant(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addCommercialTenant(this.form).then(response => {
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
        const commercialTenantIds = row.commercialTenantId || this.ids;
        this.$modal.confirm('是否确认删除商户编号为"' + commercialTenantIds + '"的数据项？').then(() => {
          this.loading = true;
          return delCommercialTenant(commercialTenantIds);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      /** 查询标签 */
      getTagsList() {
        const param = {
          'tagsType': '3'
        }
        exportTags(param).then(response => {
          this.tagsList = response.data;
        });
      },
      /** 查询供应商 */
      selectSupplierList() {
        selectSupplier(this.form).then(response => {
          this.supplierList = response.data;
        }).finally(() => {});
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('zlyyh-admin/commercialTenant/export', {
          ...this.queryParams
        }, `commercialTenant_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
