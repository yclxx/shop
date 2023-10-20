<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="用户id" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="领取时间" prop="receiveDate">
        <el-date-picker clearable
          v-model="queryParams.receiveDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择领取时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="奖励类型" prop="rewardType">
        <el-select v-model="queryParams.rewardType" placeholder="请选择奖励类型" clearable>
          <el-option
            v-for="dict in dict.type.mission_award_type"
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:marketLog:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="marketLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="" align="center" prop="logId" v-if="true"/>
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="客户端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type" :value="scope.row.supportChannel" />
        </template>
      </el-table-column>
      <el-table-column label="营销id" align="center" prop="marketId" />
      <el-table-column label="用户id" align="center" prop="userId" />
      <el-table-column label="领取时间" align="center" prop="receiveDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receiveDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="奖励类型" align="center" prop="rewardType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mission_award_type" :value="scope.row.rewardType"/>
        </template>
      </el-table-column>
      <el-table-column label="商品id" align="center" prop="productId" />
      <el-table-column label="优惠券id" align="center" prop="couponId" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listMarketLog, getMarketLog} from "@/api/zlyyh/marketLog";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "MarketLog",
  dicts: ['mission_award_type','channel_type'],
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
      // 奖励发放记录表格数据
      marketLogList: [],
      platformList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        marketId: undefined,
        userId: undefined,
        receiveDate: undefined,
        rewardType: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        logId: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
        platformKey: [
          { required: true, message: "平台标识不能为空", trigger: "blur" }
        ],
        marketId: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "用户id不能为空", trigger: "blur" }
        ],
        receiveDate: [
          { required: true, message: "领取时间不能为空", trigger: "blur" }
        ],
        rewardType: [
          { required: true, message: "奖励类型不能为空", trigger: "change" }
        ],
        productId: [
          { required: true, message: "商品id不能为空", trigger: "blur" }
        ],
        couponId: [
          { required: true, message: "优惠券id不能为空", trigger: "blur" }
        ],
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
    /** 查询奖励发放记录列表 */
    getList() {
      this.loading = true;
      listMarketLog(this.queryParams).then(response => {
        this.marketLogList = response.rows;
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
        logId: undefined,
        platformKey: undefined,
        marketId: undefined,
        userId: undefined,
        receiveDate: undefined,
        rewardType: undefined,
        productId: undefined,
        couponId: undefined,
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
      this.ids = selection.map(item => item.logId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    // handleAdd() {
    //   this.reset();
    //   this.open = true;
    //   this.title = "添加奖励发放记录";
    // },
    /** 修改按钮操作 */
    // handleUpdate(row) {
    //   this.loading = true;
    //   this.reset();
    //   const logId = row.logId || this.ids
    //   getMarketLog(logId).then(response => {
    //     this.loading = false;
    //     this.form = response.data;
    //     this.open = true;
    //     this.title = "修改奖励发放记录";
    //   });
    // },
    /** 提交按钮 */
    // submitForm() {
    //   this.$refs["form"].validate(valid => {
    //     if (valid) {
    //       this.buttonLoading = true;
    //       if (this.form.logId != null) {
    //         updateMarketLog(this.form).then(response => {
    //           this.$modal.msgSuccess("修改成功");
    //           this.open = false;
    //           this.getList();
    //         }).finally(() => {
    //           this.buttonLoading = false;
    //         });
    //       } else {
    //         addMarketLog(this.form).then(response => {
    //           this.$modal.msgSuccess("新增成功");
    //           this.open = false;
    //           this.getList();
    //         }).finally(() => {
    //           this.buttonLoading = false;
    //         });
    //       }
    //     }
    //   });
    // },
    /** 删除按钮操作 */
    // handleDelete(row) {
    //   const logIds = row.logId || this.ids;
    //   this.$modal.confirm('是否确认删除奖励发放记录编号为"' + logIds + '"的数据项？').then(() => {
    //     this.loading = true;
    //     return delMarketLog(logIds);
    //   }).then(() => {
    //     this.loading = false;
    //     this.getList();
    //     this.$modal.msgSuccess("删除成功");
    //   }).catch(() => {
    //   }).finally(() => {
    //     this.loading = false;
    //   });
    // },
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh-admin/marketLog/export', {
        ...this.queryParams
      }, `marketLog_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
