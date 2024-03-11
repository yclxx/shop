<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="巡检活动" prop="tourActivityId">
        <el-select v-model="queryParams.tourActivityId" placeholder="请选择巡检活动" clearable>
          <el-option v-for="item in tourActivityList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="门店名称" prop="shopName">
        <el-input v-model="queryParams.shopName" placeholder="请输入门店名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="巡检人员" prop="verifierId">
        <el-select v-model="queryParams.verifierId" placeholder="请选择巡检人员" clearable>
          <el-option v-for="item in verifierList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="奖励金额" prop="rewardAmount">
        <el-input v-model="queryParams.rewardAmount" placeholder="请输入奖励金额" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item label="是否被预约" prop="isReserve">
        <el-select v-model="queryParams.isReserve" placeholder="请选择是否被预约" clearable>
          <el-option v-for="dict in dict.type.t_tour_is_reserve" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="门店状态" prop="shopStatus">
        <el-select v-model="queryParams.shopStatus" placeholder="请选择门店状态" clearable>
          <el-option v-for="dict in dict.type.t_tour_shop_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_tour_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否参与活动" prop="isActivity">
        <el-select v-model="queryParams.isActivity" placeholder="请选择是否继续参与活动" clearable>
          <el-option v-for="dict in dict.type.t_tour_is_activity" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="门店是否关闭" prop="isClose">
        <el-select v-model="queryParams.isClose" placeholder="请选择门店是否关闭" clearable>
          <el-option v-for="dict in dict.type.t_tour_is_close" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:shopTour:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:shopTour:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:shopTour:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:shopTour:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain size="mini" @click="handleChangeShop"
          v-hasPermi="['zlyyh:shopTour:changeShop']">添加巡检商户</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopTourList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!-- <el-table-column label="id" align="center" prop="id" v-if="true" /> -->
      <!-- <el-table-column label="门店id" align="center" prop="shopId" /> -->
      <el-table-column label="巡检门店" align="center" width="130" prop="shopName" />
      <el-table-column label="巡检活动" align="center" width="130" prop="tourActivityId"
        :formatter="tourActivityFormatter" />
      <el-table-column label="巡检人员" align="center" width="106" prop="verifierId" :formatter="verifierIdFormatter" />
      <el-table-column label="奖励金额(元)" align="center" width="100" prop="rewardAmount" />
      <el-table-column label="预约信息" align="left" width="210" prop="isReserve">
        <template slot-scope="scope">
          <div style="display: flex;">是否预约:
            <dict-tag :options="dict.type.t_tour_is_reserve" :value="scope.row.isReserve" />
          </div>
          <div v-if="scope.row.isReserve == '1'">预约时间:{{scope.row.reserveDate}}</div>
          <div v-if="scope.row.isReserve == '1'">有效期至:{{scope.row.reserveValidity}}</div>
        </template>
      </el-table-column>
      <el-table-column label="门店状态" align="center" prop="shopStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_shop_status" :value="scope.row.shopStatus" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="巡检人员和门店合影" align="center" prop="verifierImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.verifierImage" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="物料照片" align="center" prop="goodsImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.goodsImage" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="门店照片" align="center" prop="shopImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.shopImage" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="巡检备注" align="center" prop="tourRemark" width="180" />
      <el-table-column label="商户号信息" align="left" width="180" prop="merchantNo">
        <template slot-scope="scope">
          <div v-if="scope.row.oldMerchantNo">原始商户号:{{scope.row.oldMerchantNo}}</div>
          <div v-if="scope.row.merchantType" style="display: flex;">商户类型:
            <dict-tag :options="dict.type.t_shop_merchant_type" :value="scope.row.merchantType" />
          </div>
          <div v-if="scope.row.merchantNo">变更商户号:{{scope.row.merchantNo}}</div>
        </template>
      </el-table-column>
      <el-table-column label="是否参加活动" align="center" width="100" prop="isActivity">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_is_activity" :value="scope.row.isActivity" />
          <!-- <div v-if="scope.row.isActivity == '0'">处理方式:{{scope.row.noActivityRemark}}</div> -->
        </template>
      </el-table-column>
      <el-table-column label="门店是否关闭" align="center" width="100" prop="isClose">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_is_close" :value="scope.row.isClose" />
          <!-- <div v-if="scope.row.isClose == '0'">处理方式:{{scope.row.closeRemark}}</div> -->
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="checkRemark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleCheckPass(scope.row)"
            v-show="scope.row.status == 2" v-hasPermi="['zlyyh:shopTour:checkPass']">审核通过</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleCheckRefuse(scope.row)"
            v-show="scope.row.status == 2" v-hasPermi="['zlyyh:shopTour:checkRefuse']">审核拒绝</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shopTour:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:shopTour:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改巡检商户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!-- <el-form-item label="门店id" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入门店id" />
        </el-form-item>
        <el-form-item label="巡检人员id" prop="verifierId">
          <el-input v-model="form.verifierId" placeholder="请输入巡检人员id" />
        </el-form-item> -->
        <el-form-item label="巡检活动" prop="tourActivityId">
          <el-select v-model="form.tourActivityId" placeholder="请选择巡检活动" clearable>
            <el-option v-for="item in tourActivityList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="奖励金额" prop="rewardAmount">
          <el-input v-model="form.rewardAmount" placeholder="请输入奖励金额">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <!-- <el-form-item label="是否被预约" prop="isReserve">
          <el-select v-model="form.isReserve" placeholder="请选择是否被预约">
            <el-option v-for="dict in dict.type.t_tour_is_reserve" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="门店状态" prop="shopStatus">
          <el-select v-model="form.shopStatus" placeholder="请选择门店状态">
            <el-option v-for="dict in dict.type.t_tour_shop_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_tour_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="审核意见" prop="checkRemark">
          <el-input v-model="form.checkRemark" placeholder="请输入审核意见" />
        </el-form-item>
        <el-form-item label="巡检人员和门店合影" prop="verifierImage">
          <image-upload v-model="form.verifierImage" />
        </el-form-item>
        <el-form-item label="物料照片" prop="goodsImage">
          <image-upload v-model="form.goodsImage" />
        </el-form-item>
        <el-form-item label="门店照片" prop="shopImage">
          <image-upload v-model="form.shopImage" />
        </el-form-item>
        <el-form-item label="巡检备注" prop="tourRemark">
          <el-input v-model="form.tourRemark" placeholder="请输入巡检备注" />
        </el-form-item>
        <el-form-item label="商户号" prop="merchantNo">
          <el-input v-model="form.merchantNo" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="是否继续参与活动" prop="isActivity">
          <el-select v-model="form.isActivity" placeholder="请选择是否继续参与活动">
            <el-option v-for="dict in dict.type.t_tour_is_activity" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="门店是否关闭" prop="isClose">
          <el-select v-model="form.isClose" placeholder="请选择门店是否关闭">
            <el-option v-for="dict in dict.type.t_tour_is_close" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 选择待巡检商户弹出框 -->
    <el-dialog title="添加巡检商户" :visible.sync="tourOpen" width="1200px" append-to-body>
      <el-form :model="tourShop.queryParams" ref="queryShopForm" size="small" :inline="true"
        v-show="tourShop.showSearch" label-width="68px">
        <el-form-item label="商圈" prop="businessDistrictId">
          <el-select v-model="tourShop.queryParams.businessDistrictId" placeholder="请选择商圈" clearable>
            <el-option v-for="item in businessDistrictList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商户" prop="commercialTenantId">
          <el-select v-model="tourShop.queryParams.commercialTenantId" placeholder="请选择商户" clearable>
            <el-option v-for="item in commercialTenantList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店信息" prop="shopName">
          <el-input v-model="tourShop.queryParams.shopName" placeholder="门店名称/门店地址" clearable
            @keyup.enter.native="handleShopQuery" />
        </el-form-item>
        <el-form-item label="省市县" prop="province">
          <el-input v-model="tourShop.queryParams.province" placeholder="请输入省/市/县" clearable
            @keyup.enter.native="handleShopQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="tourShop.queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="dict in dict.type.t_shop_status" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="商户审核" prop="examineVerifier">
          <el-select v-model="tourShop.queryParams.examineVerifier" placeholder="请选择商户审核状态" clearable>
            <el-option v-for="dict in dict.type.t_examine_verifier" :key="dict.value" :label="dict.label"
              :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleShopQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetShopQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="tourShop.loading" :data="tourShop.shopList"
        @selection-change="handleTourShopSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="商户" align="center" prop="commercialTenantId" :formatter="merFormatter" />
        <el-table-column label="门店名称" align="center" prop="shopName" />
        <el-table-column label="门店地址" width="150" align="center" prop="address" show-overflow-tooltip />
        <el-table-column label="省市区" align="left" prop="province" width="130">
          <template slot-scope="scope">
            <div>
              <span>省：{{scope.row.province}}</span><br>
              <span>市：{{scope.row.city}}</span><br>
              <span>县(区)：{{scope.row.district}}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="门店电话" align="center" prop="shopTel" width="109" />
        <el-table-column label="营业时间" width="115" align="center" prop="businessHours" />
        <el-table-column label="门店ID" align="center" prop="shopId" v-if="true" />
        <el-table-column label="状态" align="center" prop="status" fixed="right">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_shop_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="商户审核" align="center" prop="examineVerifier" fixed="right">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_examine_verifier" :value="scope.row.examineVerifier" />
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="tourShop.total>0" :total="tourShop.total" :page.sync="tourShop.queryParams.pageNum"
        :limit.sync="tourShop.queryParams.pageSize" @pagination="getShopList" />

      <div slot="footer" class="dialog-footer">
        <el-button :loading="tourShop.buttonLoading" type="primary" @click="submitShopForm">确 定</el-button>
        <el-button @click="tourOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审核失败对话框 -->
    <el-dialog title="审核拒绝" :visible.sync="refuseOpen" width="500px" append-to-body>
      <el-form ref="refuseForm" :model="refuseForm" :rules="refuseRules" label-width="80px">
        <el-form-item label="审核意见" prop="checkRemark">
          <el-input v-model="refuseForm.checkRemark" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="refuseSubmitForm">确 定</el-button>
        <el-button @click="refuseOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加巡检商户确认框 -->
    <el-dialog title="巡检奖励" :visible.sync="rewardOpen" width="500px" append-to-body>
      <el-form ref="rewardForm" :model="rewardForm" :rules="rewardRules" label-width="80px">
        <el-form-item label="巡检活动" prop="tourActivityId">
          <el-select v-model="rewardForm.tourActivityId" placeholder="请选择巡检活动" clearable>
            <el-option v-for="item in tourActivityList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检奖励" prop="rewardAmount">
          <el-input v-model="rewardForm.rewardAmount" placeholder="请输入奖励金额">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="rewardSubmitForm">确 定</el-button>
        <el-button @click="rewardOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listShopTour,
    getShopTour,
    delShopTour,
    addShopTour,
    updateShopTour,
    changeTourShop,
    tourCheckPass
  } from "@/api/zlyyh/shopTour";
  import {
    getPageList,
    selectShopList
  } from "@/api/zlyyh/shop";
  import {
    selectListMerchant
  } from "@/api/zlyyh/commercialTenant";
  import {
    selectListVerifier
  } from "@/api/zlyyh/verifier";
  import {
    selectListBusinessDistrict
  } from "@/api/zlyyh/businessDistrict";
  import {
    selectListTourActivity
  } from "@/api/zlyyh/shopTourActivity";


  export default {
    name: "ShopTour",
    dicts: ['t_tour_is_close', 't_tour_is_activity', 't_tour_shop_status', 't_tour_status', 't_tour_is_reserve',
      't_shop_status', 't_examine_verifier', 't_shop_merchant_type'
    ],
    data() {
      return {
        //商圈下拉列表
        businessDistrictList: [],
        // 门店下拉列表
        shopSelectList: [],
        //核销人员下拉列表
        verifierList: [],
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
        // 巡检商户表格数据
        shopTourList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          shopId: undefined,
          verifierId: undefined,
          rewardAmount: undefined,
          isReserve: undefined,
          shopStatus: undefined,
          status: undefined,
          checkRemark: undefined,
          verifierImage: undefined,
          goodsImage: undefined,
          shopImage: undefined,
          tourRemark: undefined,
          merchantNo: undefined,
          isActivity: undefined,
          isClose: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc',
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          // id: [{
          //   required: true,
          //   message: "id不能为空",
          //   trigger: "blur"
          // }],
          // shopId: [{
          //   required: true,
          //   message: "门店id不能为空",
          //   trigger: "blur"
          // }],
          // verifierId: [{
          //   required: true,
          //   message: "巡检人员id不能为空",
          //   trigger: "blur"
          // }],
          // rewardAmount: [{
          //   required: true,
          //   message: "奖励金额  单位：分不能为空",
          //   trigger: "blur"
          // }],
          // isReserve: [{
          //   required: true,
          //   message: "是否被预约  0-否  1-是不能为空",
          //   trigger: "change"
          // }],
          // shopStatus: [{
          //   required: true,
          //   message: "门店状态  0-正常  1-异常不能为空",
          //   trigger: "change"
          // }],
          // status: [{
          //   required: true,
          //   message: "状态  0-待审核  1-审核通过  2-审核失败不能为空",
          //   trigger: "change"
          // }],
          // checkRemark: [{
          //   required: true,
          //   message: "审核意见不能为空",
          //   trigger: "blur"
          // }],
          // verifierImage: [{
          //   required: true,
          //   message: "巡检人员和门店合影不能为空",
          //   trigger: "blur"
          // }],
          // goodsImage: [{
          //   required: true,
          //   message: "物料照片不能为空",
          //   trigger: "blur"
          // }],
          // shopImage: [{
          //   required: true,
          //   message: "门店照片不能为空",
          //   trigger: "blur"
          // }],
          // tourRemark: [{
          //   required: true,
          //   message: "巡检备注不能为空",
          //   trigger: "blur"
          // }],
          // merchantNo: [{
          //   required: true,
          //   message: "商户号不能为空",
          //   trigger: "blur"
          // }],
          // isActivity: [{
          //   required: true,
          //   message: "是否继续参与活动  0-不参与  1-参与不能为空",
          //   trigger: "change"
          // }],
          tourActivityId: [{
            required: true,
            message: "巡检活动不能为空",
            trigger: "change"
          }],
        },
        tourOpen: false,
        tourShop: {
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
          // 查询参数
          queryParams: {
            pageNum: 1,
            pageSize: 10,
          },
        },
        //商户下拉列表
        commercialTenantList: [],
        refuseOpen: false,
        refuseForm: {},
        refuseRules: {
          // checkRemark: [{
          //   required: true,
          //   message: "审核意见不能为空",
          //   trigger: "blur"
          // }],
        },
        rewardOpen: false,
        rewardForm: {},
        rewardRules: {
          tourActivityId: [{
            required: true,
            message: "巡检活动不能为空",
            trigger: "change"
          }],
          // rewardAmount: [{
          //   required: true,
          //   message: "巡检奖励不能为空",
          //   trigger: "blur"
          // }],
        },
        tourActivityList: [],
      };
    },
    created() {
      this.getList();
      this.getMerSelectList();
      this.getVerifiereSelectList();
      // this.getShopSelectList();
      this.getBusinessDistrictSelectList();
      this.getTourActivityList();
    },
    methods: {
      /** 查询巡检商户列表 */
      getList() {
        this.loading = true;
        listShopTour(this.queryParams).then(response => {
          this.shopTourList = response.rows;
          this.shopTourList.forEach(item => {
            item.rewardAmount = (Number(item.rewardAmount) / 100).toFixed(2)
          })
          this.total = response.total;
          this.loading = false;
        });
      },
      //巡检活动下拉列表查询
      getTourActivityList() {
        let param = {
          status: '0'
        }
        selectListTourActivity(param).then(res => {
          this.tourActivityList = res.data;
        })
      },
      //商圈下拉列表
      getBusinessDistrictSelectList() {
        let param = {
          status: '0'
        }
        selectListBusinessDistrict(param).then(response => {
          this.businessDistrictList = response.data;
        });
      },
      //门店下拉列表
      getShopSelectList() {
        // let param = {
        //   platformKey: '1718900262780600322'
        // }
        selectShopList({}).then(response => {
          this.shopSelectList = response.data;
        });
      },
      shopIdFormatter(row) {
        let name = '';
        this.shopSelectList.forEach(item => {
          if (item.id == row.shopId) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.shopName = name;
          return name;
        }
        return row.shopId;
      },
      //核销人员下拉列表
      getVerifiereSelectList() {
        selectListVerifier({}).then(response => {
          this.verifierList = response.data;
        });
      },
      verifierIdFormatter(row) {
        let name = '';
        this.verifierList.forEach(item => {
          if (item.id == row.verifierId) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.verifierMobile = name;
          return name;
        }
        return row.verifierId;
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
          shopId: undefined,
          verifierId: undefined,
          rewardAmount: undefined,
          isReserve: undefined,
          shopStatus: undefined,
          status: undefined,
          checkRemark: undefined,
          verifierImage: undefined,
          goodsImage: undefined,
          shopImage: undefined,
          tourRemark: undefined,
          merchantNo: undefined,
          isActivity: undefined,
          isClose: undefined,
          delFlag: undefined,
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
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加巡检商户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getShopTour(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.form.rewardAmount = (Number(this.form.rewardAmount) / 100).toFixed(2);
          this.open = true;
          this.title = "修改巡检商户";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.rewardAmount) {
              this.form.rewardAmount = (this.form.rewardAmount * 100).toFixed(0);
            }
            console.log(this.form)
            if (this.form.id != null) {
              updateShopTour(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addShopTour(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除巡检商户编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delShopTour(ids);
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
        this.download('zlyyh/shopTour/export', {
          ...this.queryParams
        }, `shopTour_${new Date().getTime()}.xlsx`)
      },
      //选择商户操作
      handleChangeShop() {
        this.resetForm("queryShopForm");
        this.tourOpen = true;
        this.getShopList();
      },
      /** 查询门店列表 */
      getShopList() {
        this.tourShop.loading = true;
        // this.tourShop.queryParams.platformKey = '1718900262780600322';
        getPageList(this.tourShop.queryParams).then(response => {
          this.tourShop.shopList = response.rows;
          this.tourShop.total = response.total;
          this.tourShop.loading = false;
        });
      },
      /** 搜索按钮操作 */
      handleShopQuery() {
        this.tourShop.queryParams.pageNum = 1;
        this.getShopList();
      },
      /** 重置按钮操作 */
      resetShopQuery() {
        this.resetForm("queryShopForm");
        this.handleShopQuery();
      },
      // 多选框选中数据
      handleTourShopSelectionChange(selection) {
        this.tourShop.ids = selection.map(item => item.shopId)
        this.tourShop.single = selection.length !== 1
        this.tourShop.multiple = !selection.length
      },
      //商户下拉列表
      getMerSelectList() {
        let param = {
          status: '0'
        }
        selectListMerchant(param).then(response => {
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
      tourActivityFormatter(row) {
        let name = '';
        this.tourActivityList.forEach(item => {
          if (item.id == row.tourActivityId) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.tourActivityName = name;
          return name;
        }
        return row.tourActivityId;
      },
      submitShopForm() {
        console.log(this.tourShop.ids)
        if (!this.tourShop.ids || this.tourShop.ids.length == 0) {
          this.$modal.msgWarning("请先选择需要巡检的商户门店");
          return;
        }
        this.resetForm("rewardForm");
        this.rewardOpen = true;
        this.rewardForm.shopIds = this.tourShop.ids;
      },
      rewardSubmitForm() {
        this.$refs["rewardForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.rewardForm.rewardAmount) {
              this.rewardForm.rewardAmount = Number((this.rewardForm.rewardAmount * 100).toFixed(0));
            }
            changeTourShop(this.rewardForm).then(response => {
              this.$modal.msgSuccess("操作成功");
              this.rewardOpen = false;
              this.tourOpen = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      /** 审核通过按钮操作 */
      handleCheckPass(row) {
        this.$modal.confirm('是否确认审核通过？').then(() => {
          this.loading = true;
          row.rewardAmount = Number(row.rewardAmount * 100);
          return tourCheckPass(row);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      //审核拒绝
      handleCheckRefuse(row) {
        this.resetForm("refuseForm");
        this.refuseOpen = true;
        this.refuseForm.id = row.id;
        this.refuseForm.status = '4';
      },
      //审核拒绝提交
      refuseSubmitForm() {
        this.$refs["refuseForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            updateShopTour(this.refuseForm).then(response => {
              this.$modal.msgSuccess("操作成功");
              this.refuseOpen = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
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