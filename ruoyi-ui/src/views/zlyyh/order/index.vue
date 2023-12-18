<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="number">
        <el-input v-model="queryParams.number" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品ID或者商品名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID或者用户手机号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="领取方式" prop="pickupMethod">
        <el-select v-model="queryParams.pickupMethod" placeholder="请选择领取方式" clearable>
          <el-option v-for="dict in dict.type.t_order_pickup_method" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable>
          <el-option v-for="dict in dict.type.t_order_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发放状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发放状态" clearable>
          <el-option v-for="dict in dict.type.t_order_send_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="取码订单号" prop="pushNumber">
        <el-input v-model="queryParams.pushNumber" placeholder="请输入取码(充值)订单号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable v-model="createTime" size="small" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单类型" prop="orderType">
        <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable>
          <el-option v-for="dict in dict.type.t_product_type" :key="dict.value" :label="dict.label"
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
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:order:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column label="订单号" align="left" prop="number" width="270">
        <template slot-scope="scope">
          <span>订单号：{{ scope.row.number }}</span><br>
          <span v-if="scope.row.parentNumber">券包母订单：{{ scope.row.parentNumber }}</span><br
            v-if="scope.row.parentNumber">
          <span>大订单编号：{{ scope.row.collectiveNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" align="left" prop="productId" width="230">
        <template slot-scope="scope">
          <span>商品ID：{{ scope.row.productId }}</span><br>
          <span>商品名称：{{ scope.row.productName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户信息" align="left" prop="userId" width="230">
        <template slot-scope="scope">
          <span>用户ID：{{ scope.row.userId }}</span><br>
          <span>发放账号：{{ scope.row.account }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品图片" align="center" prop="productImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.productImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="领取方式" align="center" prop="pickupMethod" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_pickup_method" :value="scope.row.pickupMethod" />
        </template>
      </el-table-column>
      <el-table-column label="订单金额" align="left" prop="totalAmount" width="180">
        <template slot-scope="scope">
          <div v-if="scope.row.pickupMethod == 0 || scope.row.pickupMethod == 1">
            <span>总金额：{{ scope.row.totalAmount }} 元</span><br>
            <span>优惠金额：{{ scope.row.reducedPrice }} 元</span><br>
            <span>需付金额：{{ scope.row.wantAmount }} 元</span><br>
            <span>实付金额：{{ scope.row.outAmount }} 元</span><br>
          </div>
          <div v-if="scope.row.pickupMethod == 2">
            <span>总积点：{{ Math.trunc(scope.row.totalAmount) }} 积点</span><br>
            <span>优惠积点：{{ Math.trunc(scope.row.reducedPrice) }} 积点</span><br>
            <span>需付积点：{{ Math.trunc(scope.row.wantAmount) }} 积点</span><br>
            <span>实付积点：{{ Math.trunc(scope.row.outAmount) }} 积点</span><br>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="订单时间" align="left" prop="payTime" width="250">
        <template slot-scope="scope">
          <span>支付时间：{{ parseTime(scope.row.payTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span><br>
          <span>失效时间：{{ parseTime(scope.row.expireDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span><br>
          <span>发放时间：{{ parseTime(scope.row.sendTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span><br>
          <span>创建时间：{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span><br>
        </template>
      </el-table-column>
      <el-table-column label="订单城市" align="left" prop="orderCityName" width="180">
        <template slot-scope="scope">
          <span>下单城市：{{ scope.row.orderCityName }}</span><br>
          <span>城市行政区号：{{ scope.row.orderCityCode }}</span><br>
        </template>
      </el-table-column>
      <el-table-column label="购买数量" align="center" prop="count" />
      <el-table-column label="核销状态" align="center" prop="verificationStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_verification_status" :value="scope.row.verificationStatus" />
        </template>
      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformKey" width="130" :formatter="platformFormatter" />
      <el-table-column label="客户端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type" :value="scope.row.supportChannel" />
        </template>
      </el-table-column>
      <el-table-column label="订单类型" align="center" prop="orderType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_type" :value="scope.row.orderType" />
        </template>
      </el-table-column>
      <el-table-column label="发放状态" align="center" prop="sendStatus" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_send_status" :value="scope.row.sendStatus" />
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="status" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="外部产品ID" align="center" prop="externalProductId" width="150" />
      <el-table-column label="发放金额" align="center" prop="externalProductSendValue" width="100" />
      <el-table-column label="供应商订单号" align="center" prop="externalOrderNumber" width="150" />
      <el-table-column label="取码订单号" align="center" prop="pushNumber" width="150" />
      <el-table-column label="美食退款" align="center" prop="cancelStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_food_cancel_status" :value="scope.row.cancelStatus" />
        </template>
      </el-table-column>
      <el-table-column label="用户退款" align="center" prop="cusRefund">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_cus_refund" :value="scope.row.cusRefund" />
        </template>
      </el-table-column>
      <el-table-column label="支付商户" align="center" prop="merchantVo.merchantName" width="150" />
      <el-table-column label="备注" align="center" prop="failReason" width="130" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <!-- <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:order:edit']">修改
          </el-button> -->
          <el-button size="mini" type="text" icon="el-icon-edit-outline" @click="handlePushLog(scope.row)"
            v-hasPermi="['zlyyh:order:pushLog']">取码记录
          </el-button>
          <el-button v-if="scope.row.status == 2 && (scope.row.sendStatus == 3 || scope.row.sendStatus == 0)"
            size="mini" type="text" icon="el-icon-tickets" @click="handleReissue(scope.row)"
            v-hasPermi="['zlyyh:order:reissue']">补发
          </el-button>
          <el-button
            v-if="(scope.row.status == 2 && scope.row.sendStatus == 3 && scope.row.pickupMethod != 0) || (scope.row.orderType == '1' && scope.row.cancelStatus == '1')"
            size="mini" type="text" icon="el-icon-edit" @click="handleRefund(scope.row)"
            v-hasPermi="['zlyyh:order:refund']">退款
          </el-button>
          <el-button
            v-if="scope.row.status == 2  && scope.row.pickupMethod != 0 && (scope.row.orderType == '1' || scope.row.orderType == '5' || scope.row.orderType == '15')"
            size="mini" type="text" icon="el-icon-edit" @click="handlefoodCancel(scope.row)"
            v-hasPermi="['zlyyh:order:refund']">美食退款
          </el-button>
          <el-button v-if="scope.row.status == 2" size="mini" type="text" icon="el-icon-edit"
            @click="handleRefundDirect(scope.row)" v-hasPermi="['zlyyh:order:refund']">直接退款
          </el-button>
          <el-button v-if="scope.row.status == 2 && (scope.row.orderType == '11' || scope.row.orderType == '12')"
            size="mini" type="text" icon="el-icon-edit" @click="handleCouponRefund(scope.row)"
            v-hasPermi="['zlyyh:order:couponRefund']">退券
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改退款订单对话框 -->
    <el-dialog title="订单退款" :visible.sync="refundOpen" width="500px" append-to-body>
      <el-form ref="refundForm" :model="refundForm" :rules="refundRules" label-width="80px">
        <el-form-item label="退还积点" prop="refund" v-if="pickupMethod == 2">
          <el-input v-model="refundForm.refund" placeholder="请输入退还积点" />
        </el-form-item>
        <el-form-item label="退款金额" prop="refund" v-if="pickupMethod == 1">
          <el-input v-model="refundForm.refund" placeholder="请输入退款金额" />
        </el-form-item>
        <el-form-item label="退款原因">
          <el-input v-model="refundForm.refundReason" placeholder="请输入退款原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitRefundForm">确 定</el-button>
        <el-button @click="refundCancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="取码记录" :visible.sync="pushOpen" width="1400px" append-to-body>
      <el-row :gutter="10" class="mb8">
        <right-toolbar :showSearch.sync="orderPushInfo.showSearch" @queryTable="getOrderPushInfoList"></right-toolbar>
      </el-row>

      <el-table v-loading="orderPushInfo.loading" :data="orderPushInfo.orderPushInfoList"
        @selection-change="handleSelectionChange">
        <el-table-column label="订单号" width="170" align="center" prop="number" />
        <el-table-column label="取码(充值)订单号" width="170" align="center" prop="pushNumber" />
        <el-table-column label="供应商订单号" min-width="170" align="center" prop="externalOrderNumber" />
        <el-table-column label="供应商产品编号" min-width="170" align="center" prop="externalProductId" />
        <el-table-column label="发放金额" width="88" align="center" prop="externalProductSendValue" />
        <el-table-column label="订单状态" width="88" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_order_push_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="170" align="center" prop="remark" />
        <el-table-column label="创建时间" width="160" align="center" prop="createTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="orderPushInfo.total>0" :total="orderPushInfo.total"
        :page.sync="orderPushInfo.queryParams.pageNum" :limit.sync="orderPushInfo.queryParams.pageSize"
        @pagination="getOrderPushInfoList" />
    </el-dialog>

    <!-- 同步数据 -->
    <el-dialog :title="refreshTitle" :visible.sync="refreshOpen" width="600px" append-to-body>
      <el-form ref="refreshForm" :model="refreshForm" :rules="refreshRules" label-width="70px">
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="refreshForm.platformKey" placeholder="请选择平台" filterable clearable>
            <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间" prop="showStartDate" label-width="120">
          <el-date-picker clearable v-model="showStartDate" size="small" :picker-options="pickerOptions"
            value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
            end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="refreshSubmitForm">确 定</el-button>
        <el-button @click="refreshCancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品图片" prop="productImg">
          <el-input v-model="form.productImg" placeholder="请输入商品图片" />
        </el-form-item>
        <el-form-item label="领取方式" prop="pickupMethod">
          <el-select v-model="form.pickupMethod" placeholder="请选择领取方式">
            <el-option v-for="dict in dict.type.t_order_pickup_method" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单总金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入订单总金额" />
        </el-form-item>
        <el-form-item label="订单优惠金额" prop="reducedPrice">
          <el-input v-model="form.reducedPrice" placeholder="请输入订单优惠金额" />
        </el-form-item>
        <el-form-item label="需支付金额" prop="wantAmount">
          <el-input v-model="form.wantAmount" placeholder="请输入需支付金额" />
        </el-form-item>
        <el-form-item label="实际支付金额" prop="outAmount">
          <el-input v-model="form.outAmount" placeholder="请输入实际支付金额" />
        </el-form-item>
        <el-form-item label="支付完成时间" prop="payTime">
          <el-date-picker clearable v-model="form.payTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择支付完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="订单失效时间" prop="expireDate">
          <el-date-picker clearable v-model="form.expireDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择订单失效时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="购买数量" prop="count">
          <el-input v-model="form.count" placeholder="请输入购买数量" />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择订单状态">
            <el-option v-for="dict in dict.type.t_order_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放账号" prop="account">
          <el-input v-model="form.account" placeholder="请输入发放账号" />
        </el-form-item>
        <el-form-item label="发放状态" prop="sendStatus">
          <el-select v-model="form.sendStatus" placeholder="请选择发放状态">
            <el-option v-for="dict in dict.type.t_order_send_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发放时间" prop="sendTime">
          <el-date-picker clearable v-model="form.sendTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择发放时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="外部产品ID" prop="externalProductId">
          <el-input v-model="form.externalProductId" placeholder="请输入外部产品ID" />
        </el-form-item>
        <el-form-item label="供应商订单号" prop="externalOrderNumber">
          <el-input v-model="form.externalOrderNumber" placeholder="请输入供应商订单号" />
        </el-form-item>
        <el-form-item label="取码(充值)订单号" prop="pushNumber">
          <el-input v-model="form.pushNumber" placeholder="请输入取码(充值)订单号" />
        </el-form-item>
        <el-form-item label="失败原因" prop="failReason">
          <el-input v-model="form.failReason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="下单所在城市" prop="orderCityName">
          <el-input v-model="form.orderCityName" placeholder="请输入下单所在城市" />
        </el-form-item>
        <el-form-item label="下单所在城市行政区号" prop="orderCityCode">
          <el-input v-model="form.orderCityCode" placeholder="请输入下单所在城市行政区号" />
        </el-form-item>
        <el-form-item label="平台标识" prop="platformKey">
          <el-input v-model="form.platformKey" placeholder="请输入平台标识" />
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
    listOrder,
    getOrder,
    delOrder,
    addOrder,
    updateOrder,
    exportOrder,
    orderReissue,
    cancelFoodOrder,
    syncOrderData
  } from "@/api/zlyyh/order";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    listOrderPushInfo,
    getOrderPushInfo,
    delOrderPushInfo,
    addOrderPushInfo,
    updateOrderPushInfo
  } from "@/api/zlyyh/orderPushInfo";
  import {
    Row
  } from "element-ui";
  import {
    addOrderBackTrans,
    addOrderBackTransDirect,
    couponRefundOrder
  } from "@/api/zlyyh/orderBackTrans";

  export default {
    name: "Order",
    dicts: ['t_order_status', 't_order_pickup_method', 't_order_send_status', 't_order_push_status',
      't_product_type', 'channel_type', 't_order_verification_status'
    ],
    data() {
      return {
        refundWay: '0',
        pickupMethod: '',
        refundForm: {},
        refundOpen: false,
        //创建时间范围
        createTime: [],
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
        // 订单表格数据
        orderList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        refreshOpen: false,
        refreshTitle: "",
        refreshForm: {},
        showStartDate: [],
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
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'number',
          isAsc: 'desc',
          number: undefined,
          productId: undefined,
          userId: undefined,
          productName: undefined,
          productImg: undefined,
          pickupMethod: undefined,
          totalAmount: undefined,
          reducedPrice: undefined,
          wantAmount: undefined,
          outAmount: undefined,
          payTime: undefined,
          expireDate: undefined,
          count: undefined,
          status: undefined,
          account: undefined,
          sendStatus: undefined,
          sendTime: undefined,
          externalProductId: undefined,
          externalOrderNumber: undefined,
          pushNumber: undefined,
          failReason: undefined,
          orderCityName: undefined,
          orderCityCode: undefined,
          platformKey: undefined,
          orderType: undefined
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {},
        refundRules: {
          refund: [{
            required: true,
            message: "退款金额不能为空",
            trigger: "blur"
          }],
          refundIntegral: [{
            required: true,
            message: "退还积点不能为空",
            trigger: "blur"
          }],
        },
        refreshRules: {
          platformKey: [{
            required: true,
            message: "退款金额不能为空",
            trigger: "blur"
          }],
        },
        pushOpen: false,
        orderNumber: '',
        orderPushInfo: {
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
          // 订单取码记录表格数据
          orderPushInfoList: [],
          // 弹出层标题
          title: "",
          // 是否显示弹出层
          open: false,
          // 查询参数
          queryParams: {
            pageNum: 1,
            pageSize: 10,
            orderByColumn: 'id',
            isAsc: 'desc',
            number: undefined,
            pushNumber: undefined,
            externalOrderNumber: undefined,
            externalProductId: undefined,
            status: undefined,
          },
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
    },
    methods: {
      handleImport() {
        this.refreshOpen = true;
        this.refreshTitle = "同步订单数据";
        this.refreshReset();
      },
      refreshSubmitForm() {
        this.$refs["refreshForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (null != this.showStartDate && '' != this.showStartDate) {
              this.refreshForm.beginStartDate = this.showStartDate[0];
              this.refreshForm.endStartDate = this.showStartDate[1];
            }
            syncOrderData(this.refreshForm).then(response => {
              this.$modal.msgSuccess("操作成功");
              this.refreshOpen = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      refreshReset() {
        this.refreshForm = {
          platformKey: undefined,
          beginStartDate: undefined,
          endStartDate: undefined
        }
        this.resetForm("refreshForm");
      },
      refreshCancel() {
        this.refreshOpen = false;
        this.refreshReset();
      },
      /** 查询订单列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.createTime && '' != this.createTime) {
          this.queryParams.params["beginCreateTime"] = this.createTime[0];
          this.queryParams.params["endCreateTime"] = this.createTime[1];
        }
        listOrder(this.queryParams).then(response => {
          this.orderList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      //平台下拉列表
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
          number: undefined,
          productId: undefined,
          userId: undefined,
          productName: undefined,
          productImg: undefined,
          pickupMethod: undefined,
          totalAmount: undefined,
          reducedPrice: undefined,
          wantAmount: undefined,
          outAmount: undefined,
          payTime: undefined,
          expireDate: undefined,
          count: undefined,
          status: undefined,
          account: undefined,
          sendStatus: undefined,
          sendTime: undefined,
          externalProductId: undefined,
          externalOrderNumber: undefined,
          pushNumber: undefined,
          failReason: undefined,
          orderCityName: undefined,
          orderCityCode: undefined,
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
        this.createTime = null;
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.number)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加订单";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const number = row.number || this.ids
        getOrder(number).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改订单";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.number != null) {
              updateOrder(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addOrder(this.form).then(response => {
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
        const numbers = row.number || this.ids;
        this.$modal.confirm('是否确认删除订单编号为"' + numbers + '"的数据项？').then(() => {
          this.loading = true;
          return delOrder(numbers);
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
        this.$modal.confirm('确定要下载数据吗？').then(() => {
          exportOrder(this.queryParams).then(response => {
            if (response.data === 200) {
              this.download('zlyyh-admin/order/export', {
                ...this.queryParams
              }, `order_${new Date().getTime()}.xlsx`)
            } else if (response.data === 501) {
              this.$modal.msgSuccess("文件导出中，请稍后下载！");
              this.$router.push('/merchantGoods/orderDownloadLog');
            }
          });
        });
      },
      //取码记录按钮
      handlePushLog(row) {
        this.pushOpen = true;
        this.orderNumber = row.number;
        this.getOrderPushInfoList();
      },
      /** 查询订单取码记录列表 */
      getOrderPushInfoList() {
        this.orderPushInfo.loading = true;
        this.orderPushInfo.queryParams.number = this.orderNumber;
        listOrderPushInfo(this.orderPushInfo.queryParams).then(response => {
          this.orderPushInfo.orderPushInfoList = response.rows;
          this.orderPushInfo.total = response.total;
          this.orderPushInfo.loading = false;
        });
      },
      // 退款取消按钮
      refundCancel() {
        this.refundOpen = false;
        this.resetRefund();
      },
      resetRefund() {
        this.refundForm = {
          number: undefined,
          refund: undefined,
          refundReason: undefined,
        };
        this.resetForm("refundForm");
      },
      //退款按钮
      handleRefund(row) {
        this.resetRefund();
        this.refundWay = '0';
        this.refundOpen = true;
        this.refundForm.number = row.number;
        this.pickupMethod = row.pickupMethod;
        this.refundForm.pickupMethod = row.pickupMethod;
      },
      //退款按钮
      handleRefundDirect(row) {
        this.resetRefund();
        this.refundWay = '1';
        this.refundOpen = true;
        this.refundForm.number = row.number;
        this.pickupMethod = row.pickupMethod;
        this.refundForm.pickupMethod = row.pickupMethod;
      },
      // 退券按钮
      handleCouponRefund(row) {
        this.$modal.confirm('是否确认退款订单"' + row.number + '"？').then(() => {
          this.loading = true;
          return couponRefundOrder(row.number);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      /** 提交按钮 */
      submitRefundForm() {
        this.$refs["refundForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.refundWay == '0') {
              addOrderBackTrans(this.refundForm).then(response => {
                this.$modal.msgSuccess("操作成功");
                this.refundOpen = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else if (this.refundWay == '1') {
              addOrderBackTransDirect(this.refundForm).then(response => {
                this.$modal.msgSuccess("操作成功");
                this.refundOpen = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });

            }
          }
        });
      },
      //订单补发
      handleReissue(row) {
        this.$modal.confirm('是否确认补发订单"' + row.number + '"？').then(() => {
          this.loading = true;
          return orderReissue(row.number);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("补发成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },

      //订单退款
      handlefoodCancel(row) {
        this.$modal.confirm('是否确认退款美食订单"' + row.number + '"？').then(() => {
          this.loading = true;
          return cancelFoodOrder(row.number);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("退款成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      }

    }
  };
</script>
<style scoped>
  ::v-deep .el-form-item--small .el-form-item__label {
    white-space: nowrap;
    width: auto !important;
  }
</style>