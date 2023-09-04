<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="活动编号" prop="id">
        <el-input v-model="queryParams.id" placeholder="请输入活动编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="活动名称" prop="grabPeriodName">
        <el-input v-model="queryParams.grabPeriodName" placeholder="请输入活动名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable filterable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_grab_period_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="周期" prop="dateType">
        <el-select v-model="queryParams.dateType" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_grab_period_date_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startDate">
        <el-date-picker clearable v-model="startDate" size="small" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate">
        <el-date-picker clearable v-model="endDate" size="small" :picker-options="pickerOptions"
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
          v-hasPermi="['zlyyh:grabPeriod:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:grabPeriod:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:grabPeriod:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:grabPeriod:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="grabPeriodList">
      <el-table-column label="活动编号" align="center" prop="id" :show-overflow-tooltip="true" />
      <el-table-column label="活动名称" align="center" prop="grabPeriodName" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="平台" align="center" prop="platformName" :formatter="changePlatform" width="90" />
      <el-table-column label="开始时间" align="center" prop="startDate" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endDate" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="起始时间" align="center" prop="sellStartTime" width="80" />
      <el-table-column label="停止时间" align="center" prop="sellEndTime" width="80" />
      <el-table-column label="周期" align="center" prop="dateType" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_grab_period_date_type" :value="scope.row.dateType" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="70">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_grab_period_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="150" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:grabPeriod:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:grabPeriod:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-picture-outline-round" @click="handleLook(scope.row)"
            v-hasPermi="['zlyyh:grabPeriod:look']">查看秒杀商品</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改秒杀配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="活动名称" prop="grabPeriodName">
              <el-input v-model="form.grabPeriodName" placeholder="请输入活动名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startDate">
              <el-date-picker clearable v-model="form.startDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择开始时间" style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endDate">
              <el-date-picker clearable v-model="form.endDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择结束时间" default-time="23:59:59" style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="领券开始时间" prop="sellStartTime">
              <span slot="label">
                起始时间
                <el-tooltip content="每天几点可以开始领券" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-time-picker :picker-options="{selectableRange: '00:00:00 - 23:59:59'}" v-model="form.sellStartTime"
                style="width: 100%;" value-format="HH:mm:ss" placeholder="请选择领取开始时间">
              </el-time-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="停止时间" prop="sellEndTime">
              <span slot="label">
                停止时间
                <el-tooltip content="每天几点停止领券" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-time-picker
                :picker-options="{selectableRange: form.sellStartTime ? (form.sellStartTime+ ' - 23:59:59') : '00:00:00 - 23:59:59'}"
                v-model="form.sellEndTime" value-format="HH:mm:ss" style="width: 100%;" placeholder="请选择领取结束时间">
              </el-time-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="周期" prop="dateType">
              <el-select v-model="form.dateType" placeholder="请选择周期" style="width: 100%;" @change="changeDateList">
                <el-option v-for="dict in dict.type.t_grab_period_date_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.dateType == 1" label="指定星期" prop="dateList">
              <el-select v-model="form.dateList" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.dateType == 2" label="指定日期" prop="dateList">
              <el-date-picker value-format="yyyy-MM-dd" type="dates" v-model="form.dateList" placeholder="请选择日期"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
                <el-option v-for="dict in dict.type.t_grab_period_status" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="背景图" prop="topBjImg">
              <image-upload :limit="1" v-model="form.topBjImg" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="活动规则">
              <editor v-model="form.description" :min-height="192" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>`
      <div slot="footer" class="dialog-footer" style="margin-top: 20px;">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="秒杀活动商品" :visible.sync="productOpen" width="1000px" append-to-body>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleProductAdd"
            v-hasPermi="['zlyyh:grabPeriodProduct:add']">新增</el-button>
        </el-col>
      </el-row>
      <el-table v-loading="productLoading" :data="grabPeriodProductList">
        <el-table-column label="商品编号" align="center" prop="productId" />
        <el-table-column label="商品名称" align="center" prop="productName" :formatter="changeProductName" />
        <el-table-column label="排序" align="center" prop="sort" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="150" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleProductUpdate(scope.row)"
              v-hasPermi="['zlyyh:grabPeriodProduct:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleProductDelete(scope.row)"
              v-hasPermi="['zlyyh:grabPeriodProduct:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="productTotal>0" :total="productTotal" :page.sync="queryProductParams.pageNum"
        :limit.sync="queryProductParams.pageSize" @pagination="getGrabPeriodProductList" />
    </el-dialog>

    <!-- 添加或修改秒杀商品配置对话框 -->
    <el-dialog :title="updateTitle" :visible.sync="updateOpen" width="500px" append-to-body>
      <el-form ref="updateForm" :model="updateForm" :rules="updateRules" label-width="80px">
        <el-form-item label="商品名称" prop="productId">
          <el-select v-model="updateForm.productId" placeholder="请选择商品" filterable clearable style="width: 100%;">
            <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="updateForm.sort" placeholder="请输入排序：从小到大" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitUpdateForm">确 定</el-button>
        <el-button @click="updateCancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改秒杀商品配置对话框 -->
    <el-dialog :title="addTitle" :visible.sync="addOpen" width="500px" append-to-body>
      <el-form ref="addForm" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="商品名称" prop="productIds">
          <el-select v-model="addForm.productIds" placeholder="请选择商品" filterable clearable multiple
            style="width: 100%;">
            <el-option v-for="item in productList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="addForm.sort" placeholder="请输入排序：从小到大" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitAddForm">确 定</el-button>
        <el-button @click="addCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listGrabPeriod,
    getGrabPeriod,
    delGrabPeriod,
    addGrabPeriod,
    updateGrabPeriod
  } from "@/api/zlyyh/grabPeriod";

  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"
  import {
    selectListProduct
  } from "@/api/zlyyh/product"

  import {
    listGrabPeriodProduct,
    getGrabPeriodProduct,
    delGrabPeriodProduct,
    addGrabPeriodProduct,
    updateGrabPeriodProduct
  } from "@/api/zlyyh/grabPeriodProduct";

  export default {
    name: "GrabPeriod",
    dicts: ['t_grab_period_date_type', 't_grab_period_status', 't_grad_period_date_list'],
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
        // 秒杀配置表格数据
        grabPeriodList: [],
        platformList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        queryProductParams: {
          pageNum: 1,
          pageSize: 10,
          grabPeriodId: undefined,
          productId: undefined,
          sort: undefined,
          orderByColumn: 'sort',
          isAsc: 'asc'
        },
        productList: [],
        grabPeriodProductList: [],
        productLoading: false,
        productTotal: 0,
        productOpen: false,
        grabPeriodId: undefined,
        updateForm: {},
        updateTitle: "",
        updateOpen: false,
        addOpen: false,
        addTitle: "",
        addForm: {},
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          grabPeriodName: undefined,
          status: undefined,
          startDate: undefined,
          endDate: undefined,
          topBjImg: undefined,
          sellStartTime: undefined,
          sellEndTime: undefined,
          dateType: undefined,
          dateList: undefined,
          description: undefined,
          platformKey: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc'
        },
        startDate: [],
        endDate: [],
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
        // 表单参数
        form: {},
        updateRules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          grabPeriodId: [{
            required: true,
            message: "秒杀活动不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "商品不能为空",
            trigger: "blur"
          }],
        },
        addRules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          grabPeriodId: [{
            required: true,
            message: "秒杀活动不能为空",
            trigger: "blur"
          }],
          productIds: [{
            required: true,
            message: "商品不能为空",
            trigger: "blur"
          }],
        },
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          grabPeriodName: [{
            required: true,
            message: "活动名称不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          startDate: [{
            required: true,
            message: "开始时间不能为空",
            trigger: "blur"
          }],
          endDate: [{
            required: true,
            message: "结束时间不能为空",
            trigger: "blur"
          }],
          dateType: [{
            required: true,
            trigger: "change"
          }],
          dateList: [{
            required: true,
            message: "指定时间不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
      selectListProduct({
        status: '0'
      }).then(res => {
        this.productList = res.data;
      })
    },
    methods: {
      handleLook(row) {
        this.grabPeriodId = row.id;
        this.getGrabPeriodProductList();
      },
      getGrabPeriodProductList() {
        this.productLoading = true;
        this.queryProductParams.grabPeriodId = this.grabPeriodId;
        listGrabPeriodProduct(this.queryProductParams).then(response => {
          this.grabPeriodProductList = response.rows;
          this.productTotal = response.total;
          this.productOpen = true;
          this.productLoading = false;
        });
      },
      changeProductName(row) {
        let productName = ''
        this.productList.forEach(item => {
          if (row.productId == item.id) {
            productName = item.label;
          }
        })
        if (productName && productName.length > 0) {
          row.productName = productName;
          return productName;
        }
        return row.productId;
      },
      handleProductUpdate(row) {
        this.productLoading = true;
        this.updateReset();
        const id = row.id || this.ids
        getGrabPeriodProduct(id).then(response => {
          this.productLoading = false;
          this.updateForm = response.data;
          this.updateOpen = true;
          this.updateTitle = "修改秒杀商品配置";
        });
      },
      handleProductAdd() {
        this.addReset();
        this.addOpen = true;
        this.addTitle = "添加秒杀商品配置";
      },
      addCancel() {
        this.addReset();
        this.addOpen = false;
      },
      submitAddForm() {
        this.$refs["addForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            this.addForm.productIds = this.addForm.productIds.toString();
            this.addForm.grabPeriodId = this.grabPeriodId;
            addGrabPeriodProduct(this.addForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.addOpen = false;
              this.getGrabPeriodProductList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      /** 提交按钮 */
      submitUpdateForm() {
        this.$refs["updateForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            updateGrabPeriodProduct(this.updateForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.updateOpen = false;
              this.getGrabPeriodProductList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      updateCancel() {
        this.updateOpen = false;
        this.updateReset();
      },
      updateReset() {
        this.updateForm = {
          id: undefined,
          grabPeriodId: undefined,
          productId: undefined,
          productIds: undefined,
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined
        };
        this.resetForm("updateForm");
      },
      addReset() {
        this.addForm = {
          id: undefined,
          grabPeriodId: undefined,
          productId: undefined,
          productIds: undefined,
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined
        };
        this.resetForm("addForm");
      },
      handleProductDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除秒杀商品配置编号为"' + ids + '"的数据项？').then(() => {
          this.productLoading = true;
          return delGrabPeriodProduct(ids);
        }).then(() => {
          this.productLoading = false;
          this.getGrabPeriodProductList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.productLoading = false;
        });
      },
      changeDateList() {
        this.form.dateList = undefined;
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
      /** 查询秒杀配置列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.startDate && '' != this.startDate) {
          this.queryParams.params["beginStartDate"] = this.startDate[0];
          this.queryParams.params["endStartDate"] = this.startDate[1];
        }
        if (null != this.endDate && '' != this.endDate) {
          this.queryParams.params["beginEndDate"] = this.endDate[0];
          this.queryParams.params["endEndDate"] = this.endDate[1];
        }
        listGrabPeriod(this.queryParams).then(response => {
          this.grabPeriodList = response.rows;
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
          grabPeriodName: undefined,
          status: "0",
          startDate: undefined,
          endDate: undefined,
          topBjImg: undefined,
          sellStartTime: undefined,
          sellEndTime: undefined,
          dateType: undefined,
          dateList: undefined,
          description: undefined,
          platformKey: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined
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
        this.title = "添加秒杀配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getGrabPeriod(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改秒杀配置";
          if (this.form && this.form.dateList) {
            this.form.dateList = this.form.dateList.split(",");
          }
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form && this.form.dateList) {
              this.form.dateList = this.form.dateList.toString();
            }
            if (this.form.id != null) {
              updateGrabPeriod(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addGrabPeriod(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除秒杀配置编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delGrabPeriod(ids);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('zlyyh-admin/grabPeriod/export', {
          ...this.queryParams
        }, `grabPeriod_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>