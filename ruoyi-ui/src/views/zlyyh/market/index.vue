<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="marketName">
        <el-input
          v-model="queryParams.marketName"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开始时间" prop="beginTime">
        <el-date-picker clearable
                        v-model="queryParams.beginTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker clearable
                        v-model="queryParams.endTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="指定时间" prop="dateSpecific">
        <el-date-picker clearable
                        v-model="queryParams.dateSpecific"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择指定时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="天数" prop="marketDay">
        <el-input
          v-model="queryParams.marketDay"
          placeholder="请输入天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="奖励类型" prop="rewardType">
        <el-select v-model="queryParams.rewardType" placeholder="请选择奖励类型" clearable>
          <el-option
            v-for="dict in dict.type.market_prize_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
          v-hasPermi="['zlyyh:market:add']"
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
          v-hasPermi="['zlyyh:market:edit']"
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
          v-hasPermi="['zlyyh:market:remove']"
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
          v-hasPermi="['zlyyh:market:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="marketList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="名称" align="center" prop="marketName"/>
      <el-table-column label="支持端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <div v-for="channel in dict.type.channel_type">
            <div v-for="(supportChannel,index) in scope.row.supportChannel.split(',')" :key="index">
              <span v-if="channel.value === supportChannel">{{ channel.label }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="beginTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.beginTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="指定时间" align="center" prop="dateSpecific" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dateSpecific, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="天数" align="center" prop="marketDay"/>
      <el-table-column label="奖励类型" align="center" prop="rewardType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.market_prize_type" :value="scope.row.rewardType"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handleProductOrCoupon(scope.row)"
            v-hasPermi="['zlyyh:market:product']"
          >奖励查看
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:market:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:market:remove']"
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

    <!-- 添加或修改新用户营销对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" height="50%">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台标识" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" @change="getPlatformSelectList"
                         clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="名称" prop="marketName">
              <el-input v-model="form.marketName" placeholder="请输入名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" clearable>
                <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
                           :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="beginTime">
              <el-date-picker clearable
                              v-model="form.beginTime"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker clearable
                              v-model="form.endTime"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="指定时间" prop="dateSpecific">
              <el-date-picker clearable
                              v-model="form.dateSpecific"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择指定时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="天数" prop="marketDay">
              <el-input v-model="form.marketDay" placeholder="请输入天数"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="奖励类型" prop="rewardType">
              <el-select v-model="form.rewardType" placeholder="请选择奖励类型">
                <el-option
                  v-for="dict in dict.type.market_prize_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="form.rewardType === '2'" :span="12">
            <el-form-item label="优惠券" prop="actionId">
              <el-select
                v-model="form.actionId"
                filterable
                remote
                reserve-keyword
                placeholder="请输入优惠券名称"
                :remote-method="getActionSelectList"
                :loading="loading">
                <el-option
                  v-for="item in actionList"
                  :key="item.label"
                  :label="item.id"
                  :value="item.label">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="form.rewardType === '3'" :span="12">
            <el-form-item label="商品" prop="productId">
              <el-select
                v-model="form.productId"
                filterable
                remote
                reserve-keyword
                placeholder="请输入商品名称"
                :remote-method="getProductSelectList "
                :loading="loading">
                <el-option
                  v-for="item in productList"
                  :key="item.id"
                  :label="item.label"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="支持端" prop="supportChannel">
              <el-checkbox-group v-model="form.supportChannel">
                <el-checkbox
                  v-for="item in dict.type.channel_type" :key="item.value" :label="item.value">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="优惠券图片" prop="marketImage">
          <image-upload v-model="form.marketImage"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="isPrize" width="50%">
      <div v-if="marketPrize.market.rewardType === '2' && marketPrize.action">
        <el-descriptions title="优惠券批次" :border="true" :column="2">
          <el-descriptions-item label="批次ID">{{ marketPrize.action.actionId }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ marketPrize.action.actionNo }}</el-descriptions-item>
          <el-descriptions-item label="优惠券名称">{{ marketPrize.action.couponName }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额">{{ marketPrize.action.couponAmount }}</el-descriptions-item>
          <el-descriptions-item label="最低消费金额">{{ marketPrize.action.minAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠券类型">
            <dict-tag :options="dict.type.t_coupon_type" :value="marketPrize.action.couponType"/>
          </el-descriptions-item>
          <el-descriptions-item label="优惠券状态">
            <dict-tag :options="dict.type.sys_normal_disable" :value="marketPrize.action.status"/>
          </el-descriptions-item>
          <el-descriptions-item label="可使用起始日期">{{ marketPrize.action.periodOfStart }}</el-descriptions-item>
          <el-descriptions-item label="使用有效截止日期">{{ marketPrize.action.periodOfValidity }}
          </el-descriptions-item>
          <el-descriptions-item label="优惠券描述">{{ marketPrize.action.couponDescription }}</el-descriptions-item>
        </el-descriptions>
        <div>
          <el-image :src="marketPrize.action.couponImage" style="width: 200px; height: 200px"/>
        </div>
      </div>
      <div v-else-if="marketPrize.market.rewardType === '3' && marketPrize.product">
        <el-descriptions title="商品" :border="true" :column="2">
          <el-descriptions-item label="商品ID">{{ marketPrize.product.productId }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ marketPrize.product.productName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="dict.type.sys_normal_disable" :value="marketPrize.product.status"/>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="marketPrize.product.productImg">
          <el-image :src="marketPrize.product.productImg" style="width: 200px; height: 200px"/>
        </div>
      </div>
      <div v-else>
        无奖励，请添加
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listMarket, getMarket, delMarket, addMarket, updateMarket, getMarketPrize} from "@/api/zlyyh/market";
import {selectListPlatform} from "@/api/zlyyh/platform";
import {selectListProduct} from "@/api/zlyyh/product";
import {selectListAction} from "@/api/zlyyh/action";

export default {
  name: "Market",
  dicts: ['market_prize_type', 'sys_normal_disable', 'channel_type', 't_coupon_type'],
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
      // 新用户营销表格数据
      marketList: [],
      // 平台下拉列表
      platformList: [],
      productList: [],
      actionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      isPrize: false,
      marketPrize: {
        market: {
          rewardType: undefined
        },
        action: undefined,
        product: undefined,
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        marketName: undefined,
        beginTime: undefined,
        endTime: undefined,
        dateSpecific: undefined,
        marketDay: undefined,
        rewardType: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        marketId: [
          {required: true, message: "不能为空", trigger: "blur"}
        ],
        platformKey: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ],
        marketName: [
          {required: true, message: "名称不能为空", trigger: "blur"}
        ],
        dateSpecific: [
          {required: true, message: "指定时间不能为空", trigger: "blur"}
        ],
        marketDay: [
          {required: true, message: "天数不能为空", trigger: "blur"}
        ],
        supportChannel: [
          {required: true, message: "支持端不能为空", trigger: "blur"}
        ],
        rewardType: [
          {required: true, message: "奖励类型不能为空", trigger: "change"}
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
  },
  methods: {
    //平台标识下拉列表
    getPlatformSelectList() {
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
    },
    //商品下拉列表
    getProductSelectList(query) {
      if (query) {
        const param = {
          productName: query
        }
        selectListProduct(param).then(response => {
          this.productList = response.data;
        });
      }
    },
    // 批次下拉列表
    getActionSelectList(query) {
      if (query) {
        const param = {
          couponName: query
        }
        selectListAction(param).then(response => {
          this.actionList = response.data;
        });
      }
    },
    platformFormatter(row) {
      let name = '';
      this.platformList.forEach(item => {
        if (item.id === row.platformKey) {
          name = item.label;
        }
      })
      if (name && name.length > 0) {
        row.platformName = name;
        return name;
      }
      return row.platformKey;
    },
    /** 查询新用户营销列表 */
    getList() {
      this.loading = true;
      listMarket(this.queryParams).then(response => {
        this.marketList = response.rows;
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
        marketId: undefined,
        platformKey: undefined,
        marketName: undefined,
        status: undefined,
        marketImage: undefined,
        beginTime: undefined,
        endTime: undefined,
        dateSpecific: undefined,
        marketDay: undefined,
        rewardType: undefined,
        supportChannel: [],
        productId: undefined,
        actionId: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined
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
      this.ids = selection.map(item => item.marketId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加新用户营销";
    },
    /** 修改按钮操作 */
    handleProductOrCoupon(row) {
      const marketId = row.marketId || this.ids
      getMarketPrize(marketId).then(response => {
        this.marketPrize = response.data;
        this.isPrize = true;
      });
    },
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const marketId = row.marketId || this.ids
      getMarket(marketId).then(response => {
        this.form = response.data;

        this.title = "修改新用户营销";
        if (response.data.rewardType === '2') {
          const param = {actionId: response.data.actionId}
          selectListAction(param).then(response => {
            this.actionList = response.data;
          });
        } else if (response.data.rewardType === '3') {
          const param = {productId: response.data.productId}
          selectListProduct(param).then(response => {
            this.productList = response.data;
          });
        }
        if (response.data && response.data.supportChannel) {
          this.form.supportChannel = response.data.supportChannel.split(",");
        } else {
          this.form.supportChannel = [];
        }
        this.loading = false;
        this.open = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.supportChannel.length > 0) {
            this.form.supportChannel = this.form.supportChannel.join(",");
          }
          this.buttonLoading = true;
          if (this.form.marketId != null) {
            updateMarket(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addMarket(this.form).then(response => {
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
      const marketIds = row.marketId || this.ids;
      this.$modal.confirm('是否确认删除新用户营销编号为"' + marketIds + '"的数据项？').then(() => {
        this.loading = true;
        return delMarket(marketIds);
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
      this.download('zlyyh-admin/market/export', {
        ...this.queryParams
      }, `market_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
