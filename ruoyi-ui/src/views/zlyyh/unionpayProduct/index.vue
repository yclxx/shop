<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="活动编号" prop="activityNo">
        <el-input v-model="queryParams.activityNo" placeholder="请输入活动编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="活动类型" prop="activityTp">
        <el-select v-model="queryParams.activityTp" placeholder="请选择活动类型" clearable>
          <el-option v-for="dict in dict.type.t_unionpay_product_activity_tp" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间">
        <el-date-picker v-model="daterangeBeginTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间">
        <el-date-picker v-model="daterangeEndTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="限制类型" prop="limitTp">
        <el-select v-model="queryParams.limitTp" placeholder="请选择限制类型" clearable>
          <el-option v-for="dict in dict.type.t_unionpay_product_limit_tp" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动标识" prop="activityMark">
        <el-select v-model="queryParams.activityMark" placeholder="请选择活动标识" clearable>
          <el-option v-for="dict in dict.type.t_unionpay_product_activity_mark" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:unionpayProduct:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:unionpayProduct:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:unionpayProduct:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unionpayProductList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="活动编号" align="center" prop="activityNo" v-if="true" width="160" />
      <el-table-column label="活动名称" align="center" prop="activityNm" v-if="true" width="200" />
      <el-table-column label="总次数" align="center" prop="allCount" />
      <el-table-column label="总剩余次数" align="center" prop="allRemainCount" width="100" />
      <el-table-column label="当天总次数" align="center" prop="dayCount" width="100" />
      <el-table-column label="当天剩余次数" align="center" prop="dayRemainCount" width="120" />
      <el-table-column label="开始时间" align="center" prop="beginTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.beginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活动类型" align="center" prop="activityTp">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_unionpay_product_activity_tp" :value="scope.row.activityTp" />
        </template>
      </el-table-column>
      <el-table-column label="限制类型" align="center" prop="limitTp">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_unionpay_product_limit_tp" :value="scope.row.limitTp" />
        </template>
      </el-table-column>
      <el-table-column label="活动标识" align="center" prop="activityMark" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_unionpay_product_activity_mark" :value="scope.row.activityMark" />
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" fixed="right" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-refresh" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:unionpayProduct:edit']">刷新</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改银联活动对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="活动编号" prop="activityNo">
          <el-input v-model="form.activityNo" placeholder="请输入活动编号" />
        </el-form-item>
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
    listUnionpayProduct,
    getUnionpayProduct,
    delUnionpayProduct,
    addUnionpayProduct,
    updateUnionpayProduct
  } from "@/api/zlyyh/unionpayProduct";

  export default {
    name: "UnionpayProduct",
    dicts: ['t_unionpay_product_limit_tp', 't_unionpay_product_activity_mark', 't_unionpay_product_activity_tp'],
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
        // 银联活动表格数据
        unionpayProductList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 当天剩余次数时间范围
        daterangeBeginTime: [],
        // 当天剩余次数时间范围
        daterangeEndTime: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'createTime',
          isAsc: 'desc',
          activityNo: undefined,
          activityTp: undefined,
          beginTime: undefined,
          endTime: undefined,
          limitTp: undefined,
          activityMark: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          activityNo: [{
            required: true,
            message: "活动编号不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询银联活动列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeBeginTime && '' != this.daterangeBeginTime) {
          this.queryParams.params["beginBeginTime"] = this.daterangeBeginTime[0];
          this.queryParams.params["endBeginTime"] = this.daterangeBeginTime[1];
        }
        if (null != this.daterangeEndTime && '' != this.daterangeEndTime) {
          this.queryParams.params["beginEndTime"] = this.daterangeEndTime[0];
          this.queryParams.params["endEndTime"] = this.daterangeEndTime[1];
        }
        listUnionpayProduct(this.queryParams).then(response => {
          this.unionpayProductList = response.rows;
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
          activityNo: undefined,
          activityTp: undefined,
          beginTime: undefined,
          endTime: undefined,
          limitTp: undefined,
          activityMark: undefined,
          allCount: undefined,
          allRemainCount: undefined,
          dayCount: undefined,
          dayRemainCount: undefined,
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
        this.daterangeBeginTime = [];
        this.daterangeEndTime = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.activityNo)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加银联活动";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        let from = {
          activityNo: row.activityNo
        }
        updateUnionpayProduct(from).then(response => {
          this.getList();
        }).finally(() => {
          this.loading = false;
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            addUnionpayProduct(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const activityNos = row.activityNo || this.ids;
        this.$modal.confirm('是否确认删除银联活动编号为"' + activityNos + '"的数据项？').then(() => {
          this.loading = true;
          return delUnionpayProduct(activityNos);
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
        this.download('zlyyh-admin/unionpayProduct/export', {
          ...this.queryParams
        }, `unionpayProduct_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>