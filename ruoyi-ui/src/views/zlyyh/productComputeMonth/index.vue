<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="统计月份" prop="month">
        <el-date-picker clearable
                        v-model="queryParams.month"
                        type="month"
                        value-format="yyyy-MM"
                        placeholder="请选择统计月份">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行政区号" prop="cityCode">
        <el-input
          v-model="queryParams.cityCode"
          placeholder="请输入行政区号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="城市" prop="cityName">
        <el-input
          v-model="queryParams.cityName"
          placeholder="请输入城市"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['zlyyh:productComputeMonth:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productComputeMonthList" @selection-change="handleSelectionChange">
      <!--<el-table-column type="selection" width="55" align="center"/>-->
      <el-table-column label="统计月份" align="center" prop="month"/>
      <el-table-column label="商品编号" align="center" prop="productId"/>
      <el-table-column label="行政区号" align="center" prop="cityCode"/>
      <el-table-column label="城市" align="center" prop="cityName"/>
      <el-table-column label="每个城市用户人数" align="center" prop="cityUserNumber"/>
      <el-table-column label="每个城市订单数量" align="center" prop="cityOrderNumber"/>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改订单数据统计（月份）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="" prop="id">
          <el-input v-model="form.id" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="统计月份" prop="month">
          <el-input v-model="form.month" placeholder="请输入统计月份"/>
        </el-form-item>
        <el-form-item label="商品编号" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品编号"/>
        </el-form-item>
        <el-form-item label="行政区号" prop="cityCode">
          <el-input v-model="form.cityCode" placeholder="请输入行政区号"/>
        </el-form-item>
        <el-form-item label="城市" prop="cityName">
          <el-input v-model="form.cityName" placeholder="请输入城市"/>
        </el-form-item>
        <el-form-item label="每个城市用户人数" prop="cityUserNumber">
          <el-input v-model="form.cityUserNumber" placeholder="请输入每个城市用户人数"/>
        </el-form-item>
        <el-form-item label="每个城市订单数量" prop="cityOrderNumber">
          <el-input v-model="form.cityOrderNumber" placeholder="请输入每个城市订单数量"/>
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
  listProductComputeMonth,
} from "@/api/zlyyh/productComputeMonth";

export default {
  name: "ProductComputeMonth",
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
      // 订单数据统计（月份）表格数据
      productComputeMonthList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        month: undefined,
        productId: undefined,
        cityCode: undefined,
        cityName: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          {required: true, message: "不能为空", trigger: "blur"}
        ],
        month: [
          {required: true, message: "统计月份不能为空", trigger: "blur"}
        ],
        productId: [
          {required: true, message: "商品编号不能为空", trigger: "blur"}
        ],
        cityCode: [
          {required: true, message: "行政区号不能为空", trigger: "blur"}
        ],
        cityName: [
          {required: true, message: "城市不能为空", trigger: "blur"}
        ],
        cityUserNumber: [
          {required: true, message: "每个城市用户人数不能为空", trigger: "blur"}
        ],
        cityOrderNumber: [
          {required: true, message: "每个城市订单数量不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询订单数据统计（月份）列表 */
    getList() {
      this.loading = true;
      listProductComputeMonth(this.queryParams).then(response => {
        this.productComputeMonthList = response.rows;
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
        month: undefined,
        productId: undefined,
        cityCode: undefined,
        cityName: undefined,
        cityUserNumber: undefined,
        cityOrderNumber: undefined,
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh-admin/productComputeMonth/export', {
        ...this.queryParams
      }, `productComputeMonth_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
