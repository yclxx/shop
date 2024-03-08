<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="巡检ID" prop="tourId">
        <el-input v-model="queryParams.tourId" placeholder="请输入巡检ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="巡检人员" prop="verifierId">
        <el-select v-model="queryParams.verifierId" placeholder="请选择巡检人员" clearable>
          <el-option v-for="item in verifierList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作类型" prop="operType">
        <el-select v-model="queryParams.operType" placeholder="请选择操作类型" clearable>
          <el-option v-for="dict in dict.type.t_tour_oper_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="门店名称" prop="shopName">
        <el-input v-model="queryParams.shopName" placeholder="请输入门店名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <!-- <el-form-item label="商户类型" prop="merchantType">
        <el-select v-model="queryParams.merchantType" placeholder="请选择商户类型" clearable>
          <el-option v-for="dict in dict.type.t_shop_merchant_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item> -->

      <!-- <el-form-item label="是否继续参与活动" prop="isActivity">
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
      </el-form-item> -->
      <!-- <el-form-item label="审核失败原因" prop="checkFailReason">
        <el-input v-model="queryParams.checkFailReason" placeholder="请输入审核失败原因" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:shopTourLog:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:shopTourLog:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:shopTourLog:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:shopTourLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopTourLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="巡检记录ID" align="center" prop="tourLogId" v-if="true" /> -->
      <el-table-column label="巡检ID" align="center" prop="tourId" />
      <el-table-column label="巡检人员" align="center" prop="verifierId" :formatter="verifierIdFormatter" />
      <el-table-column label="操作类型" align="center" prop="operType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_oper_type" :value="scope.row.operType" />
        </template>
      </el-table-column>
      <el-table-column label="门店" align="center" prop="shopVo.shopName" width="130" />
      <el-table-column label="商户修改信息" align="left" prop="shopName" width="200">
        <template slot-scope="scope">
          <div v-if="scope.row.shopName">名称：{{scope.row.shopName}}</div>
          <div v-if="scope.row.address">地址：{{scope.row.address}}</div>
          <div v-if="scope.row.adminMobile">管理员电话：{{scope.row.adminMobile}}</div>
        </template>
      </el-table-column>
      <el-table-column label="门店状态" align="center" prop="shopStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_shop_status" :value="scope.row.shopStatus" />
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
      <el-table-column label="巡检备注" align="center" prop="tourRemark" />
      <el-table-column label="商户号信息" align="left" prop="merchantType" width="200">
        <template slot-scope="scope">
          <div v-if="scope.row.oldMerchantNo">原始商户号:{{scope.row.oldMerchantNo}}</div>
          <div v-if="scope.row.merchantType" style="display: flex;">商户类型:
            <dict-tag :options="dict.type.t_shop_merchant_type" :value="scope.row.merchantType" />
          </div>
          <div v-if="scope.row.merchantNo">变更商户号:{{scope.row.merchantNo}}</div>
        </template>
      </el-table-column>
      <el-table-column label="是否参加活动" align="center" prop="isActivity">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_is_activity" :value="scope.row.isActivity" />
        </template>
      </el-table-column>
      <el-table-column label="门店是否关闭" align="center" prop="isClose">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_tour_is_close" :value="scope.row.isClose" />
        </template>
      </el-table-column>
      <el-table-column label="审核失败原因" align="center" prop="checkFailReason" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shopTourLog:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:shopTourLog:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改巡检记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="巡检ID" prop="tourId">
          <el-input v-model="form.tourId" placeholder="请输入巡检ID" />
        </el-form-item>
        <el-form-item label="操作人员ID" prop="verifierId">
          <el-input v-model="form.verifierId" placeholder="请输入操作人员ID" />
        </el-form-item>
        <el-form-item label="操作类型  1-预约  2-提交巡检  3-取消预约  4-取消巡检  5-审核通过 6-审核拒绝  7-预约过期" prop="operType">
          <el-select v-model="form.operType" placeholder="请选择操作类型  1-预约  2-提交巡检  3-取消预约  4-取消巡检  5-审核通过 6-审核拒绝  7-预约过期">
            <el-option v-for="dict in dict.type.t_tour_oper_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="门店ID" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="门店名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入门店名称" />
        </el-form-item>
        <el-form-item label="门店地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入门店地址" />
        </el-form-item>
        <el-form-item label="商户管理员手机号" prop="adminMobile">
          <el-input v-model="form.adminMobile" placeholder="请输入商户管理员手机号" />
        </el-form-item>
        <el-form-item label="门店状态  0-正常  1-异常" prop="shopStatus">
          <el-select v-model="form.shopStatus" placeholder="请选择门店状态  0-正常  1-异常">
            <el-option v-for="dict in dict.type.t_tour_shop_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
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
        <el-form-item label="原始商户号" prop="oldMerchantNo">
          <el-input v-model="form.oldMerchantNo" placeholder="请输入原始商户号" />
        </el-form-item>
        <el-form-item label="商户类型  0-微信  1-云闪付  2-支付宝" prop="merchantType">
          <el-select v-model="form.merchantType" placeholder="请选择商户类型  0-微信  1-云闪付  2-支付宝">
            <el-option v-for="dict in dict.type.t_shop_merchant_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商户号" prop="merchantNo">
          <el-input v-model="form.merchantNo" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="是否继续参与活动  0-不参与  1-参与" prop="isActivity">
          <el-select v-model="form.isActivity" placeholder="请选择是否继续参与活动  0-不参与  1-参与">
            <el-option v-for="dict in dict.type.t_tour_is_activity" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="门店是否关闭  0-关闭  1-运营中" prop="isClose">
          <el-select v-model="form.isClose" placeholder="请选择门店是否关闭  0-关闭  1-运营中">
            <el-option v-for="dict in dict.type.t_tour_is_close" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核失败原因" prop="checkFailReason">
          <el-input v-model="form.checkFailReason" placeholder="请输入审核失败原因" />
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
    listShopTourLog,
    getShopTourLog,
    delShopTourLog,
    addShopTourLog,
    updateShopTourLog
  } from "@/api/zlyyh/shopTourLog";
  import {
    selectListVerifier
  } from "@/api/zlyyh/verifier";

  export default {
    name: "ShopTourLog",
    dicts: ['t_shop_merchant_type', 't_tour_oper_type', 't_tour_is_close', 't_tour_is_activity', 't_tour_shop_status'],
    data() {
      return {
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
        // 巡检记录表格数据
        shopTourLogList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          tourId: undefined,
          verifierId: undefined,
          operType: undefined,
          shopId: undefined,
          shopName: undefined,
          address: undefined,
          adminMobile: undefined,
          shopStatus: undefined,
          verifierImage: undefined,
          goodsImage: undefined,
          shopImage: undefined,
          tourRemark: undefined,
          oldMerchantNo: undefined,
          merchantType: undefined,
          merchantNo: undefined,
          isActivity: undefined,
          isClose: undefined,
          checkFailReason: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc',
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          tourLogId: [{
            required: true,
            message: "巡检记录ID不能为空",
            trigger: "blur"
          }],
          tourId: [{
            required: true,
            message: "巡检ID不能为空",
            trigger: "blur"
          }],
          verifierId: [{
            required: true,
            message: "操作人员ID不能为空",
            trigger: "blur"
          }],
          operType: [{
            required: true,
            message: "操作类型",
            trigger: "change"
          }],
          shopId: [{
            required: true,
            message: "门店ID不能为空",
            trigger: "blur"
          }],
          shopName: [{
            required: true,
            message: "门店名称不能为空",
            trigger: "blur"
          }],
          address: [{
            required: true,
            message: "门店地址不能为空",
            trigger: "blur"
          }],
          adminMobile: [{
            required: true,
            message: "商户管理员手机号不能为空",
            trigger: "blur"
          }],
          shopStatus: [{
            required: true,
            message: "门店状态不能为空",
            trigger: "change"
          }],
          verifierImage: [{
            required: true,
            message: "巡检人员和门店合影不能为空",
            trigger: "blur"
          }],
          goodsImage: [{
            required: true,
            message: "物料照片不能为空",
            trigger: "blur"
          }],
          shopImage: [{
            required: true,
            message: "门店照片不能为空",
            trigger: "blur"
          }],
          tourRemark: [{
            required: true,
            message: "巡检备注不能为空",
            trigger: "blur"
          }],
          oldMerchantNo: [{
            required: true,
            message: "原始商户号不能为空",
            trigger: "blur"
          }],
          merchantType: [{
            required: true,
            message: "商户类型不能为空",
            trigger: "change"
          }],
          merchantNo: [{
            required: true,
            message: "商户号不能为空",
            trigger: "blur"
          }],
          isActivity: [{
            required: true,
            message: "是否继续参与活动不能为空",
            trigger: "change"
          }],
          isClose: [{
            required: true,
            message: "门店是否关闭不能为空",
            trigger: "change"
          }],
          checkFailReason: [{
            required: true,
            message: "审核失败原因不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      this.getVerifiereSelectList();
    },
    methods: {
      /** 查询巡检记录列表 */
      getList() {
        this.loading = true;
        listShopTourLog(this.queryParams).then(response => {
          this.shopTourLogList = response.rows;
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
          tourLogId: undefined,
          tourId: undefined,
          verifierId: undefined,
          operType: undefined,
          shopId: undefined,
          shopName: undefined,
          address: undefined,
          adminMobile: undefined,
          shopStatus: undefined,
          verifierImage: undefined,
          goodsImage: undefined,
          shopImage: undefined,
          tourRemark: undefined,
          oldMerchantNo: undefined,
          merchantType: undefined,
          merchantNo: undefined,
          isActivity: undefined,
          isClose: undefined,
          checkFailReason: undefined,
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
        this.ids = selection.map(item => item.tourLogId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加巡检记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const tourLogId = row.tourLogId || this.ids
        getShopTourLog(tourLogId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改巡检记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.tourLogId != null) {
              updateShopTourLog(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addShopTourLog(this.form).then(response => {
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
        const tourLogIds = row.tourLogId || this.ids;
        this.$modal.confirm('是否确认删除巡检记录编号为"' + tourLogIds + '"的数据项？').then(() => {
          this.loading = true;
          return delShopTourLog(tourLogIds);
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
        this.download('zlyyh/shopTourLog/export', {
          ...this.queryParams
        }, `shopTourLog_${new Date().getTime()}.xlsx`)
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
    }
  };
</script>