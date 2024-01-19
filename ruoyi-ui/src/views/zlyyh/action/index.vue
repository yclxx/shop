<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="批次号" prop="actionNo">
        <el-input
          v-model="queryParams.actionNo"
          placeholder="请输入批次号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="couponName">
        <el-input
          v-model="queryParams.couponName"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="自动下单" prop="autoPay">
        <el-select v-model="queryParams.autoPay" placeholder="请选择是否自动支付" clearable>
          <el-option
            v-for="dict in dict.type.t_right_not"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
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
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zlyyh:action:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['zlyyh:action:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zlyyh:action:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:action:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="actionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column fixed="left" label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="批次号" align="center" prop="actionNo"/>
      <el-table-column label="名称" align="center" prop="couponName" width="100px"/>
      <el-table-column label="优惠金额" align="center" prop="couponAmount"/>
      <el-table-column label="最低消费金额" align="center" prop="minAmount" width="100px"/>
      <el-table-column label="类型" align="center" prop="couponType" width="100px">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_coupon_type" :value="scope.row.couponType"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="自动下单" align="center" prop="autoPay">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_right_not" :value="scope.row.autoPay"/>
        </template>
      </el-table-column>
      <el-table-column label="优惠券数量" align="center" prop="couponCount" width="100"/>
      <el-table-column label="使用起始日期" align="center" prop="periodOfStart" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.periodOfStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用有效截止日期" align="center" prop="periodOfValidity" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.periodOfValidity, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="兑换起始日期" align="center" prop="conversionStartDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.conversionStartDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="兑换截止日期" align="center" prop="conversionEndDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.conversionEndDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button v-if="scope.row.couponType !== '2'"
                     size="mini"
                     type="text"
                     icon="el-icon-edit"
                     @click="handleProductByAction(scope.row)"
                     v-hasPermi="['zlyyh:action:create']"
          >商品维护
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleCreate(scope.row)"
            v-hasPermi="['zlyyh:action:create']"
          >生成优惠券
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:action:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:action:remove']"
          >删除
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

    <!-- 添加或修改优惠券批次对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="批次号" prop="actionNo">
              <el-input v-model="form.actionNo" placeholder="请输入批次号" :disabled="isUpdate"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" @change="getPlatformSelectList"
                         clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠券名称" prop="couponName">
              <el-input v-model="form.couponName" placeholder="请输入优惠优惠券名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.couponType !== '1'">
            <el-form-item label="优惠金额" prop="couponAmount">
              <el-input v-model="form.couponAmount" placeholder="请输入优惠金额"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠券类型" prop="couponType">
              <el-select v-model="form.couponType" placeholder="请选择优惠券类型">
                <el-option
                  v-for="item in dict.type.t_coupon_type"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="自动下单" prop="autoPay">
              <el-radio-group v-model="form.autoPay">
                <el-radio
                  v-for="dict in dict.type.t_right_not"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠券数量" prop="couponCount">
              <el-input v-model="form.couponCount" placeholder="请输入优惠券数量"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.couponType !== '1'">
            <el-form-item label="最低消费金额" prop="minAmount">
              <el-input v-model="form.minAmount" placeholder="请输入最低消费金额"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="使用起始日期" prop="periodOfStart">
              <el-date-picker clearable
                              v-model="form.periodOfStart"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择可使用起始日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用截止日期" prop="periodOfValidity">
              <el-date-picker clearable
                              v-model="form.periodOfValidity"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择使用有效截止日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="兑换起始日期" prop="conversionStartDate">
              <el-date-picker clearable
                              v-model="form.conversionStartDate"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择兑换起始日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="兑换截止日期" prop="conversionEndDate">
              <el-date-picker clearable
                              v-model="form.conversionEndDate"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择兑换截止日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠券描述" prop="couponDescription">
              <el-input v-model="form.couponDescription" type="textarea" placeholder="请输入内容"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次描述" prop="actionNote">
              <el-input v-model="form.actionNote" type="textarea" placeholder="请输入内容"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠券图片" prop="couponImage">
              <image-upload v-model="form.couponImage"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog :title="title" :visible.sync="numberOpen" width="20%" append-to-body>
      <div>
        <el-form>
          <el-form-item label="优惠券数量">
            <el-input v-model="number" type="number" placeholder="请输入数量"/>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="loading" type="primary" @click="createCoupon">确 定</el-button>
        <el-button @click="numberOpen = false">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="商品维护" :visible.sync="isProduct" width="90%">
      <ProductAction v-bind:actionId="actionId"></ProductAction>
    </el-dialog>
  </div>
</template>

<script>
import {listAction, getAction, delAction, addAction, updateAction, createCoupon} from "@/api/zlyyh/action";
import {selectListPlatform} from "@/api/zlyyh/platform";
import ProductAction from "@/views/zlyyh/product/ProductAction.vue";

export default {
  name: "Action",
  dicts: ['sys_normal_disable', 't_coupon_type','t_right_not'],
  components: {
    ProductAction
  },
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      isUpdate: false,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 优惠券批次表格数据
      actionList: [],
      // 平台下拉列表
      platformList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      numberOpen: false,
      number: undefined,
      numberRow: undefined,
      // 商品信息
      actionId: undefined,
      isProduct: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        actionNo: undefined,
        couponName: undefined,
        couponType: undefined,
        periodOfStart: undefined,
        periodOfValidity: undefined,
        status: undefined,
        conversionStartDate: undefined,
        conversionEndDate: undefined,
        platformKey: undefined,
        autoPay: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        actionId: [
          {required: true, message: "批次ID不能为空", trigger: "blur"}
        ],
        actionNo: [
          {required: true, message: "批次号不能为空", trigger: "blur"}
        ],
        platformKey: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ],
        periodOfValidity: [
          {required: true, message: "使用截至日期不能为空", trigger: "blur"}
        ],
        couponName: [
          {required: true, message: "优惠券名称不能为空", trigger: "blur"}
        ],
        couponAmount: [
          {required: true, message: "优惠金额不能为空", trigger: "blur"}
        ],
        couponType: [
          {required: true, message: "优惠券类型不能为空", trigger: "change"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        couponCount: [
          {required: true, message: "优惠券数量不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
  },
  methods: {
    // 商品维护
    handleProductByAction(row) {
      this.actionId = row.actionId;
      this.isProduct = true;
    },
    /** 查询优惠券批次列表 */
    getList() {
      this.loading = true;
      listAction(this.queryParams).then(response => {
        this.actionList = response.rows;
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
        actionId: undefined,
        actionNo: undefined,
        actionNote: undefined,
        couponName: undefined,
        couponAmount: undefined,
        minAmount: undefined,
        couponType: undefined,
        periodOfStart: undefined,
        periodOfValidity: undefined,
        status: undefined,
        couponCount: undefined,
        couponDescription: undefined,
        couponImage: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        conversionStartDate: undefined,
        conversionEndDate: undefined,
        platformKey: undefined,
        autoPay: '0'
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
      this.ids = selection.map(item => item.actionId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.isUpdate = false;
      this.title = "添加优惠券批次";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const actionId = row.actionId || this.ids
      getAction(actionId).then(response => {
        this.loading = false;
        this.isUpdate = true;
        this.form = response.data;
        this.open = true;
        this.title = "修改优惠券批次";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.actionId != null) {
            updateAction(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addAction(this.form).then(response => {
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
    handleCreate(row) {
      this.numberOpen = true;
      this.numberRow = row;
    },
    createCoupon() {
      const params = {
        'actionId': this.numberRow.actionId,
        'couponCount': this.number
      }
      this.loading = true;
      this.$modal.confirm('请确认是否生成批次号为"' + this.numberRow.actionNo + '"，共计"' + this.number + '"张优惠券的优惠券信息').then(() => {
        return createCoupon(params);
      }).then(() => {
        this.numberOpen = false;
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("生成优惠券成功！");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
        this.numberOpen = false;
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const actionIds = row.actionId || this.ids;
      this.$modal.confirm('是否确认删除优惠券批次编号为"' + actionIds + '"的数据项？').then(() => {
        this.loading = true;
        return delAction(actionIds);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh-admin/action/export', {
        ...this.queryParams
      }, `action_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
