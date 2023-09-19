<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品编号" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入商品编号" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="商品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择商品类型" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_type" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="商品归属" prop="productAffiliation">
        <el-select v-model="queryParams.productAffiliation" placeholder="请选择商品归属" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_affiliation" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="领取方式" prop="pickupMethod">
        <el-select v-model="queryParams.pickupMethod" placeholder="请选择领取方式" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_pickup_method" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_product_status" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="外部产品号" prop="externalProductId" label-width="90px">
        <el-input v-model="queryParams.externalProductId" placeholder="请输入外部产品号" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="展示开始时间" prop="showStartDate" label-width="120">
        <el-date-picker clearable v-model="showStartDate" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="展示结束时间" prop="showEndDate" label-width="120">
        <el-date-picker clearable v-model="showEndDate" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="展示城市" prop="showCity">
        <el-select v-model="queryParams.showCity" placeholder="请选择展示城市" clearable>
          <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label" :value="item.rightLabel"/>
        </el-select>
      </el-form-item>
      <el-form-item label="显示首页" prop="showIndex">
        <el-select v-model="queryParams.showIndex" placeholder="请选择是否显示首页" clearable style="width: 100%;">
          <el-option v-for="dict in dict.type.t_show_index" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
                   v-hasPermi="['zlyyh:product:add']">新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
                   v-hasPermi="['zlyyh:product:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['zlyyh:product:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['zlyyh:product:export']">导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList">
      <el-table-column label="商品编号" align="center" prop="productId" width="170" fixed="left"/>
      <el-table-column label="商品名称" align="center" prop="productName" width="150" :show-overflow-tooltip="true"
                       fixed="left"/>
      <el-table-column label="平台" align="center" prop="platformName" width="100" :formatter="changePlatform"/>
      <el-table-column label="商品归属" align="center" prop="productAffiliation" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_affiliation" :value="scope.row.productAffiliation"/>
        </template>
      </el-table-column>
      <el-table-column label="商品类型" align="center" prop="productType" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_type" :value="scope.row.productType"/>
        </template>
      </el-table-column>
      <el-table-column label="领取方式" align="center" prop="pickupMethod" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_pickup_method" :value="scope.row.pickupMethod"/>
        </template>
      </el-table-column>
      <el-table-column label="市场价格" align="center" prop="originalAmount" width="80"/>
      <el-table-column label="售卖价格" align="center" prop="sellAmount" width="80"/>
      <el-table-column label="62会员价格" align="center" prop="vipUpAmount" width="90"/>
      <el-table-column label="权益会员价格" align="center" prop="vipAmount" width="100"/>
      <el-table-column label="外部产品号" align="center" prop="externalProductId" width="90"/>
      <el-table-column label="状态" align="center" prop="status" fixed="right" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_product_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="展示开始时间" align="center" prop="showStartDate" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.showStartDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="展示结束时间" align="center" prop="showEndDate" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.showEndDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['zlyyh:product:edit']">修改
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['zlyyh:product:remove']">删除
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdateDayCount(scope.row)"
                     v-hasPermi="['zlyyh:product:edit']">抢购状态
          </el-button>
          <el-button v-if="scope.row.productType == '13'" size="mini" type="text" icon="el-icon-edit"
                     @click="handleTicketSessionLine(scope.row)" v-hasPermi="['zlyyh:product:ticket']">场次与票种
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-tabs v-model="activeName">
        <el-tab-pane label="基本信息" name="basicCoupon" key="basicCoupon" :style="{height: tableHeight}">
          <el-form ref=" form" :model="form" :rules="rules" label-width="110px">
            <el-row>
              <el-col :span="8">
                <el-form-item label="商品名称" prop="productName">
                  <el-input maxlength="64" v-model="form.productName" placeholder="请输入商品名称"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="简称" prop="productAbbreviation">
                  <el-input maxlength="20" v-model="form.productAbbreviation" placeholder="请输入简称"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="副标题" prop="productSubhead">
                  <el-input maxlength="64" v-model="form.productSubhead" placeholder="请输入副标题"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="商品归属" prop="productAffiliation">
                  <span slot="label">
                    商品归属
                    <dict-tooltip :options="dict.type.t_product_affiliation"></dict-tooltip>
                  </span>
                  <el-select v-model="form.productAffiliation" placeholder="请选择商品归属" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_affiliation" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="商品类型" prop="productType">
                  <span slot="label">
                    商品类型
                    <dict-tooltip :options="dict.type.t_product_type"></dict-tooltip>
                  </span>
                  <el-select v-model="form.productType" placeholder="请选择商品类型" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_type" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="账号类型" prop="sendAccountType">
                  <span slot="label">
                    账号类型
                    <dict-tooltip :options="dict.type.t_product_send_account_type"></dict-tooltip>
                  </span>
                  <el-select v-model="form.sendAccountType" placeholder="请选择发券账号类型" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_send_account_type" :key="dict.value"
                               :label="dict.label" :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="领取方式" prop="pickupMethod">
                  <el-select v-model="form.pickupMethod" placeholder="请选择领取方式" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_pickup_method" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.pickupMethod && form.pickupMethod != '0'">
                <el-form-item label="显示市场价格" prop="showOriginalAmount">
                  <el-select v-model="form.showOriginalAmount" placeholder="请选择是否显示市场价格"
                             style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_show_original_amount" :key="dict.value"
                               :label="dict.label" :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.pickupMethod && form.pickupMethod != '0' && form.showOriginalAmount == '1'">
                <el-form-item label="市场价格" prop="originalAmount">
                  <el-input type="number" :min="0" :max="100000" v-model="form.originalAmount"
                            placeholder="请输入市场价格">
                    <template v-if="form.pickupMethod == '1'" slot="append">元</template>
                    <template v-if="form.pickupMethod == '2'" slot="append">积点</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.pickupMethod && form.pickupMethod != '0' ">
                <el-form-item label="售卖价格" prop="sellAmount">
                  <el-input type="number" :min="0" :max="100000" v-model="form.sellAmount" placeholder="请输入售卖价格">
                    <template v-if="form.pickupMethod == '1'" slot="append">元</template>
                    <template v-if="form.pickupMethod == '2'" slot="append">积点</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.pickupMethod && form.pickupMethod != '0' ">
                <el-form-item label="62会员价格" prop="vipUpAmount">
                  <el-input type="number" :min="0" :max="100000" v-model="form.vipUpAmount"
                            placeholder="请输入62会员价格">
                    <template v-if="form.pickupMethod == '1'" slot="append">元</template>
                    <template v-if="form.pickupMethod == '2'" slot="append">积点</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.pickupMethod && form.pickupMethod != '0' ">
                <el-form-item label="权益会员价格" prop="vipAmount">
                  <el-input type="number" :min="0" :max="100000" v-model="form.vipAmount"
                            placeholder="请输入权益会员价格">
                    <template v-if="form.pickupMethod == '1'" slot="append">元</template>
                    <template v-if="form.pickupMethod == '2'" slot="append">积点</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="展示开始时间" prop="showStartDate">
                  <el-date-picker clearable v-model="form.showStartDate" type="datetime" style="width: 100%;"
                                  value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示开始时间">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="展示结束时间" prop="showEndDate">
                  <el-date-picker clearable v-model="form.showEndDate" type="datetime" style="width: 100%;"
                                  value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示结束时间"
                                  default-time="23:59:59">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="领取开始时间" prop="sellStartDate">
                  <el-date-picker clearable v-model="form.sellStartDate" type="datetime" style="width: 100%;"
                                  value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择领取开始时间">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="领取结束时间" prop="sellEndDate">
                  <el-date-picker clearable v-model="form.sellEndDate" type="datetime" style="width: 100%;"
                                  value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择领取结束时间"
                                  default-time="23:59:59">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="平台" prop="platformKey">
                  <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable
                             style="width: 100%;">
                    <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="支付商户号" prop="merchantId"
                              v-if="form.productAffiliation == '0' && form.pickupMethod == '1'">
                  <el-select v-model="form.merchantId" placeholder="请选择支付商户号" clearable filterable
                             style="width: 100%;">
                    <el-option v-for="item in merchantList" :key="item.id" :label="item.rightLabel" :value="item.id">
                      <span style="float: left">{{ item.rightLabel }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px"
                            v-if="item.label && item.label.length>0">
                        {{ item.label }}
                      </span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="外部产品号" prop="externalProductId">
                  <span slot="label">
                    外部产品号
                    <el-tooltip content="第三方平台提供产品的产品编号" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.externalProductId" placeholder="请输入外部产品号"/>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productType == '3'">
                <el-form-item label="发放金额" prop="externalProductSendValue">
                  <el-input v-model="form.externalProductSendValue" placeholder="请输入发放金额">
                    <template slot="append">元</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productType == '4'">
                <el-form-item label="发放数量" prop="externalProductSendValue">
                  <el-input v-model="form.externalProductSendValue" placeholder="请输入发放数量">
                    <template slot="append">积点</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_status" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="商品范围" prop="search">
                  <span slot="label">
                    商品范围
                    <dict-tooltip :options="dict.type.t_product_search"></dict-tooltip>
                  </span>
                  <el-select v-model="form.search" placeholder="请选择商品范围" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_search" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="是否搜索" prop="searchStatus">
                  <span slot="label">
                    是否搜索
                    <dict-tooltip :options="dict.type.t_search_status"></dict-tooltip>
                  </span>
                  <el-select v-model="form.searchStatus" placeholder="请选择是否搜索" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_search_status" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="用户退款" prop="cusRefund">
                  <span slot="label">
                    用户退款
                    <dict-tooltip :options="dict.type.t_cus_refund"></dict-tooltip>
                  </span>
                  <el-select v-model="form.cusRefund" placeholder="请选择是否支持用户侧退款" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_cus_refund" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="可购用户" prop="payUser">
                  <span slot="label">
                    可购用户
                    <dict-tooltip :options="dict.type.t_product_pay_user"></dict-tooltip>
                  </span>
                  <el-select v-model="form.payUser" placeholder="请选择可购用户群体" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_pay_user" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="银联分销" prop="unionPay">
                  <span slot="label">
                    银联分销
                    <dict-tooltip :options="dict.type.t_product_union_pay"></dict-tooltip>
                  </span>
                  <el-select v-model="form.unionPay" placeholder="请选择是否是涉及银联分销" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_union_pay" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8"
                      v-if="form.productAffiliation && form.productAffiliation == '0' && form.unionPay == '1'">
                <el-form-item label="内容方" prop="distributorId">
                  <el-select v-model="form.distributorId" placeholder="请选择内容方" style="width: 100%;">
                    <el-option v-for="dict in distributorList" :key="dict.id" :label="dict.label"
                               :value="dict.id"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="显示首页" prop="showIndex">
                  <el-radio-group v-model="form.showIndex">
                    <el-radio v-for="dict in dict.type.t_show_index" :key="dict.value" :label="dict.value">
                      {{ dict.label }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="排序" prop="sort">
                  <el-input v-model="form.sort" placeholder="请输入排序：从小到大"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="商品图片" prop="productImg">
                  <image-upload v-model="form.productImg" :limit="1"/>
                </el-form-item>
              </el-col>
              <el-col :span="16" v-if="form.productAffiliation && form.productAffiliation == '0'">
                <el-form-item label="商品详情">
                  <editor v-model="form.description" :min-height="192"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="限制城市" prop="showCity">
                  <span slot="label">
                    限制城市
                    <el-tooltip content="限制哪些城市的用户可以看到可以购买" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
                  <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox
                           default-expand-all ref="city" node-key="id" empty-text="加载中,请稍后"
                           :props="defaultProps"></el-tree>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="商品规则" name="couponCount" key="couponCount" :style="{height: tableHeight}">
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-row>
              <el-col :span="8">
                <el-form-item label="领取区间" prop="sellTime">
                  <span slot="label">
                    领取区间
                    <el-tooltip content="限制购买时间,例如每天8点至9点可购买: 08:00:00-09:00:00" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-time-picker is-range v-model="form.sellTime" range-separator="-" start-placeholder="开始时间"
                                  end-placeholder="结束时间" placeholder="选择时间范围" style="width: 100%;"
                                  value-format="HH:mm:ss">
                  </el-time-picker>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="指定周几" prop="assignDate">
                  <span slot="label">
                    指定周几
                    <dict-tooltip :options="dict.type.t_product_assign_date"></dict-tooltip>
                  </span>
                  <el-select v-model="form.assignDate" placeholder="请选择指定周几" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="周几能领" prop="weekDate" v-if="form.assignDate == '1'">
                  <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                    <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="总数量" prop="totalCount">
                  <span slot="label">
                    总数量
                    <el-tooltip content="总数量为-1代表不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.totalCount" placeholder="请输入总数量"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每月数量" prop="monthCount">
                  <span slot="label">
                    每月数量
                    <el-tooltip content="每月数量为-1代表不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.monthCount" placeholder="请输入每月数量"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每周数量" prop="weekCount">
                  <span slot="label">
                    每周数量
                    <el-tooltip content="每周数量为-1代表不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.weekCount" placeholder="请输入每周数量"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每日数量" prop="dayCount">
                  <span slot="label">
                    每日数量
                    <el-tooltip content="每日数量为-1代表不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.dayCount" placeholder="请输入每日数量"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每日用户限领" prop="dayUserCount">
                  <span slot="label">
                    每日用户限领
                    <el-tooltip content="每日同一用户限领数量，0为不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.dayUserCount" placeholder="请输入每日用户限领"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每周用户限领" prop="weekUserCount">
                  <span slot="label">
                    每周用户限领
                    <el-tooltip content="请输入每周同一用户限领数量，0为不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.weekUserCount" placeholder="请输入每周用户限领"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每月用户限领" prop="monthUserCount">
                  <span slot="label">
                    每月用户限领
                    <el-tooltip content="请输入当月同一用户限领数量，0为不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.monthUserCount" placeholder="请输入每月用户限领"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="活动用户限领" prop="totalUserCount">
                  <span slot="label">
                    活动用户限领
                    <el-tooltip content="请输入活动周期同一用户限领数量，0为不限制" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
                  <el-input v-model="form.totalUserCount" placeholder="请输入活动用户限领"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="扩展信息" name="expand" key="expand" :style="{height: tableHeight}">
          <el-form ref="form" :model="form" :rules="rules" label-width="110px">
            <el-row>
              <el-col :span="8">
                <el-form-item label="门店" prop="shopId">
                  <el-select v-model="form.shopId" placeholder="请输入门店名称搜索"
                             multiple
                             filterable
                             remote
                             reserve-keyword
                             :remote-method="selectShopLists"
                             clearable
                             style="width: 100%;">
                    <el-option v-for="item in shopList" :key="item.id" :value="item.id" :label="item.label">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="商户" prop="commercialTenantId">
                  <el-select v-model="form.commercialTenantId" placeholder="请选择商户" filterable clearable multiple
                             style="width: 100%;">
                    <el-option v-for="item in commercialTenantList" :key="item.id" :value="item.id" :label="item.label">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="栏目" prop="categoryId">
                  <el-select v-model="form.categoryId" placeholder="请选择栏目" filterable clearable multiple
                             style="width: 100%;">
                    <el-option v-for="item in categoryList" :key="item.id" :value="item.id" :label="item.label">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="跳转类型" prop="toType">
                  <span slot="label">
                    跳转类型
                    <dict-tooltip :options="dict.type.t_product_to_type"></dict-tooltip>
                  </span>
                  <el-select v-model="form.toType" placeholder="请选择跳转类型" style="width: 100%;">
                    <el-option v-for="dict in dict.type.t_product_to_type" :key="dict.value" :label="dict.label"
                               :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.toType == '3'">
                <el-form-item label="小程序ID" prop="appId">
                  <el-input v-model="form.appId" placeholder="请输入小程序ID"/>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.toType != '4' && form.toType != '0'">
                <el-form-item label="页面地址" prop="url">
                  <el-input v-model="form.url" placeholder="请输入页面地址"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="提供方名称" prop="providerName">
                  <el-input v-model="form.providerName" placeholder="请输入活动提供方名称"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="标签" prop="tags">
                  <el-input v-model="form.tags" placeholder="请输入内容"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="按钮名称" prop="btnText">
                  <el-input v-model="form.btnText" placeholder="请输入按钮名称"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="分享标题" prop="shareTitle">
                  <el-input v-model="form.shareTitle" placeholder="请输入分享标题"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="分享描述" prop="shareName">
                  <el-input v-model="form.shareName" type="textarea" placeholder="请输入内容"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="分享图片" prop="shareImage">
                  <image-upload v-model="form.shareImage" :limit="1"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="提供方logo" prop="providerLogo">
                  <image-upload v-model="form.providerLogo" :limit="1"/>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.toType == '4'">
                <el-form-item label="页面地址" prop="url">
                  <image-upload v-model="form.url" :limit="1"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="演出票信息" name="ticket" key="ticket"
                     v-if="form.ticket && (form.productType == '13' || isUpdate)">
          <el-form ref="form" :rules="rules" :model="form" label-width="110px">
            <el-row>
              <el-col :span="8">
                <el-form-item label="票形式" required="true">
                  <el-select v-model="form.ticket.ticketForm" placeholder="请选择票形式" style="width: 100%;">
                    <el-option v-for="item in ticketFormList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="身份信息" required="true">
                  <el-select v-model="form.ticket.ticketCard" placeholder="请选择身份信息" style="width: 100%;">
                    <el-option v-for="item in ticketCardList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="不支持退" required="true">
                  <el-select v-model="form.ticket.ticketNonsupport" placeholder="请选择不支持退"
                             style="width: 100%;">
                    <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="电子发票" required="true">
                  <el-select v-model="form.ticket.ticketInvoice" placeholder="请选择电子发票"
                             style="width: 100%;">
                    <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="过期退" required="true">
                  <el-select v-model="form.ticket.ticketExpired" placeholder="请选择过期退"
                             style="width: 100%;">
                    <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="随时退" required="true">
                  <el-select v-model="form.ticket.ticketAnyTime" placeholder="请选择随时退"
                             style="width: 100%;">
                    <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="选座方式" required="true">
                  <el-select v-model="form.ticket.ticketChooseSeat" placeholder="请选择快递方式" style="width: 100%;">
                    <el-option v-for="item in ticketChooseSeatList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="快递方式" required="true">
                  <el-select v-model="form.ticket.ticketPostWay" placeholder="请选择快递方式" style="width: 100%;">
                    <el-option v-for="item in ticketPostWayList" :key="item.value" :label="item.label"
                               :value="item.value"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="form.ticket.ticketPostWay === '2'">
                <el-form-item label="邮费">
                  <el-input v-model="form.ticket.ticketPostage" placeholder="请输入邮费"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="16">
                <el-form-item label="须知" prop="ticketNotice">
                  <editor v-model="form.ticket.ticketNotice" :min-height="192"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="场次与票种" name="session" key="session"
                     v-if="form.ticket && (form.productType == '13' || isUpdate)">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addSessionRow()">新增场次与票种
          </el-button>
          <el-table :data="form.ticketSession" :rules="rules" ref="table" style="width: 100%">
            <el-table-column type="expand">
              <template slot-scope="props">
                <el-table :data="props.row.ticketLine">
                  <el-table-column :render-header="renderHeader" label="票种名称" align="center" prop="lineTitle">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.lineTitle" placeholder="请输入票种名称"/>
                    </template>
                  </el-table-column>
                  <el-table-column :render-header="renderHeader" label="销售价格" align="center" prop="linePrice">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.linePrice" placeholder="请输入销售价格"/>
                    </template>
                  </el-table-column>
                  <el-table-column :render-header="renderHeader" label="结算价格" align="center" prop="lineSettlePrice">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.lineSettlePrice" placeholder="请输入结算价格"/>
                    </template>
                  </el-table-column>
                  <el-table-column :render-header="renderHeader" label="总数量" align="center" prop="lineNumber">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.lineNumber" placeholder="请输入总数量"/>
                    </template>
                  </el-table-column>
                  <el-table-column :render-header="renderHeader" label="单次购买上限" align="center"
                                   prop="lineUpperLimit">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.lineUpperLimit" placeholder="请输入单次购买上限"/>
                    </template>
                  </el-table-column>
                  <el-table-column :render-header="renderHeader" label="状态" align="center" prop="lineStatus">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.lineStatus" placeholder="请选择状态">
                        <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
                                   :value="dict.value"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                      <el-button size="mini" type="text" icon="el-icon-delete" @click="delLineRow(props.row,scope.row)"
                                 v-hasPermi="['zlyyh:productTicketSession:remove']">删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="场次名称" align="center" prop="lineTitle">
              <template slot-scope="scope">
                <el-input v-model="scope.row.session" placeholder="请输入场次名称"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="状态" align="center" prop="lineStatus">
              <template slot-scope="scope">
                <el-select v-model="scope.row.status" placeholder="请选择状态">
                  <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
                             :value="dict.value"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="日期" align="center" prop="sessionDate">
              <template slot-scope="scope">
                <el-date-picker clearable v-model="scope.row.date" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                                placeholder="请选择日期">
                </el-date-picker>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button type="primary" plain size="mini" @click="addLineRow(scope.row)">新增票种
                </el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="delSessionRow(scope.row)"
                           v-hasPermi="['zlyyh:productTicketSession:remove']">删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <el-form label-width="100px">
        <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
          <el-button v-if="activeName != tabNameList[0]" @click="lastTab()">上一步</el-button>
          <el-button v-if="form.productType!='13' && activeName != tabNameList[tabNameList.length -3]" type="primary"
                     @click="nextTab()">下一步
          </el-button>
          <el-button :loading="buttonLoading" style="float: right;"
                     v-if="form.productType!='13' && (activeName == tabNameList[tabNameList.length -3] || isUpdate)"
                     type="primary" @click="submitForm()">提交
          </el-button>
          <el-button v-if="form.productType=='13' && activeName != tabNameList[tabNameList.length -1]" type="primary"
                     @click="nextTab()">下一步
          </el-button>
          <el-button :loading="buttonLoading" style="float: right;"
                     v-if="form.productType=='13' && (activeName == tabNameList[tabNameList.length -1] || isUpdate)"
                     type="primary" @click="submitForm()">提交
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog title="设置商品抢购状态" :visible.sync="openDayCount" width="400px" append-to-body>
      <el-form>
        <el-form-item label="今日抢购状态">
          <el-radio v-model="dayCount" label="0" border>正常</el-radio>
          <el-radio v-model="dayCount" label="1" border>已抢完</el-radio>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="dayCountSubmitForm">确 定</el-button>
        <el-button @click="dayCountCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listProduct,
  getProduct,
  delProduct,
  addProduct,
  updateProduct,
  setProductDayCount
} from "@/api/zlyyh/product";
import {
  selectListPlatform
} from "@/api/zlyyh/platform"
import {
  listSelectMerchant
} from "@/api/zlyyh/merchant"
import {
  selectListMerchant
} from "@/api/zlyyh/commercialTenant"
import {
  selectListDistributor
} from "@/api/zlyyh/distributor"
import {
  selectListCategory
} from "@/api/zlyyh/category"
import {
  treeselect as cityTreeselect,
  productShowCityTreeSelect,
  selectCityList
} from "@/api/zlyyh/area"
import {selectShopList, selectShopListById} from "@/api/zlyyh/shop";
import item from "@/layout/components/Sidebar/Item.vue";

export default {
  name: "Product",
  computed: {
    item() {
      return item
    }
  },
  dicts: ['t_product_to_type', 't_product_status', 't_product_affiliation', 't_product_assign_date', 't_product_type',
    't_product_show_original_amount', 't_product_pickup_method', 't_grad_period_date_list', 't_product_search',
    't_search_status', 't_product_pay_user', 't_show_index', 't_product_send_account_type', 't_cus_refund', 'sys_normal_disable',
    't_product_union_pay'
  ],
  data() {
    return {
      tableHeight: document.documentElement.scrollHeight - 245 + "px",
      activeName: "basicCoupon",
      tabNameList: ["basicCoupon", "couponCount", "expand", "ticket", "session"],
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
      // 商品表格数据
      productList: [],
      platformList: [],
      merchantList: [],
      shopList: [],
      commercialTenantList: [],
      cityList: [],
      categoryList: [],
      cityNodeAll: false,
      defaultProps: {
        children: "children",
        label: "label"
      },
      showStartDate: [],
      showEndDate: [],
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
      //城市列表
      cityOptions: [],
      distributorList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      openDayCount: false,
      dayCount: '',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        externalProductId: undefined,
        productId: undefined,
        productName: undefined,
        productAbbreviation: undefined,
        productSubhead: undefined,
        productImg: undefined,
        productAffiliation: undefined,
        productType: undefined,
        pickupMethod: undefined,
        showOriginalAmount: undefined,
        originalAmount: undefined,
        sellAmount: undefined,
        vipUpAmount: undefined,
        vipAmount: undefined,
        toType: undefined,
        appId: undefined,
        url: undefined,
        showIndex: undefined,
        status: undefined,
        showStartDate: undefined,
        showEndDate: undefined,
        sellStartDate: undefined,
        sellEndDate: undefined,
        assignDate: undefined,
        weekDate: undefined,
        sellTime: undefined,
        totalCount: undefined,
        monthCount: undefined,
        weekCount: undefined,
        dayCount: undefined,
        dayUserCount: undefined,
        weekUserCount: undefined,
        monthUserCount: undefined,
        totalUserCount: undefined,
        description: undefined,
        providerLogo: undefined,
        providerName: undefined,
        tags: undefined,
        showCity: undefined,
        merchantId: undefined,
        shopGroupId: undefined,
        btnText: undefined,
        shareTitle: undefined,
        shareName: undefined,
        shareImage: undefined,
        platformKey: undefined,
        sort: undefined,
        orderByColumn: "product_id",
        isAsc: 'desc'
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productId: [{
          required: true,
          message: "ID不能为空",
          trigger: "blur"
        }],
        productName: [{
          required: true,
          message: "商品名称不能为空",
          trigger: "blur"
        }],
        productAffiliation: [{
          required: true,
          message: "商品归属不能为空",
          trigger: "change"
        }],
        productType: [{
          required: true,
          message: "商品类型不能为空",
          trigger: "change"
        }],
        sendAccountType: [{
          required: true,
          message: "发券账号类型不能为空",
          trigger: "change"
        }],
        pickupMethod: [{
          required: true,
          message: "领取方式不能为空",
          trigger: "change"
        }],
        showOriginalAmount: [{
          required: true,
          message: "是否显示市场价格不能为空",
          trigger: "change"
        }],
        originalAmount: [{
          required: true,
          message: "市场价格不能为空",
          trigger: "blur"
        }],
        sellAmount: [{
          required: true,
          message: "售卖价格不能为空",
          trigger: "blur"
        }],
        toType: [{
          required: true,
          message: "跳转类型不能为空",
          trigger: "change"
        }],
        appId: [{
          required: true,
          message: "小程序ID不能为空",
          trigger: "blur"
        }],
        status: [{
          required: true,
          message: "状态不能为空",
          trigger: "change"
        }],
        assignDate: [{
          required: true,
          message: "指定周几不能为空",
          trigger: "change"
        }],
        weekDate: [{
          required: true,
          message: "周几能领不能为空",
          trigger: "blur"
        }],
        platformKey: [{
          required: true,
          message: "平台不能为空",
          trigger: "blur"
        }],
        externalProductSendValue: [{
          required: true,
          message: "发放金额不能为空",
          trigger: "blur"
        }],
        search: [{
          required: true,
          message: "商品范围不能为空",
          trigger: "blur"
        }],
        searchStatus: [{
          required: true,
          message: "是否搜索不能为空",
          trigger: "blur"
        }],
        cusRefund: [{
          required: true,
          message: "是否支持用户退款不能为空",
          trigger: "blur"
        }],
        payUser: [{
          required: true,
          message: "商品范围不能为空",
          trigger: "blur"
        }],
        unionPay: [{
          required: true,
          message: "请选择是否是涉及银联分销",
          trigger: "blur"
        }],
      },
      isUpdate: false,
      ticketChooseSeatList: [
        {value: '1', label: '在线选座'},
        {value: '2', label: '先到先得'},
        {value: '3', label: '无座'},
        {value: '4', label: '实体票'}
      ],
      ticketFormList: [
        {value: '1', label: '电子票'},
        {value: '2', label: '实体票'}
      ],
      ticketCardList: [
        {value: '0', label: '必须填写'},
        {value: '1', label: '无需填写'}
      ],
      ticketStatusList: [
        {value: '0', label: '是'},
        {value: '1', label: '否'},],
      ticketPostWayList: [
        {value: '0', label: '无需邮寄'},
        {value: '1', label: '包邮'},
        {value: '2', label: '另付邮费'},
      ],
      shopParams: undefined
    };
  },
  created() {
    this.getList();
    this.getCitySelectList();
    selectListPlatform({}).then(res => {
      this.platformList = res.data;
    })
    listSelectMerchant({}).then(res => {
      this.merchantList = res.data;
    })
    this.selectShopLists();
    let merchantParams = {
      status: "0"
    }
    selectListMerchant(merchantParams).then(res => {
      this.commercialTenantList = res.data;
    })
    let categoryParams = {
      categoryListType: "0",
      status: "0"
    }
    selectListCategory(categoryParams).then(res => {
      this.categoryList = res.data;
    })
    selectListDistributor({
      status: "0"
    }).then(res => {
      this.distributorList = res.data;
    })
  },
  methods: {
    selectShopLists(query) {
      if (query !== undefined) {
        this.shopParams = {
          status: "0",
          shopName: query,
          pageSize: 100
        }
      } else {
        this.shopParams = {
          status: "0",
          pageSize: 100
        }
      }
      selectShopList(this.shopParams).then(res => {
        this.shopList = res.data;
      })
    },
    selectShop(query) {
      let params = {
        ids: query
      }
      selectShopListById(params).then(res => {
        this.shopList = res.data;
      })
    },
    selectAll(val) {
      if (this.cityNodeAll) {
        // 	设置目前勾选的节点，使用此方法必须设置 node-key 属性
        this.$refs.city.setCheckedNodes([])
      } else {
        // 全部不选中
        this.$refs.city.setCheckedNodes([])
      }
    },
    //city下拉列表
    getCitySelectList() {
      let cityForm = {
        level: "city"
      }
      selectCityList(cityForm).then(response => {
        this.cityList = response.data;
      })
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
    handleNodeClick(data) {
      this.cityNodeAll = false;
    },
    lastTab() {
      let index = this.tabNameList.indexOf(this.activeName);
      if (index > 0) {
        this.activeName = this.tabNameList[index - 1]
      }
    },
    nextTab() {
      let index = this.tabNameList.indexOf(this.activeName);
      if (index < this.tabNameList.length - 1) {
        this.activeName = this.tabNameList[index + 1]
      }
    },
    /** 查询商品列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.showStartDate && '' != this.showStartDate) {
        this.queryParams.params["beginStartDate"] = this.showStartDate[0];
        this.queryParams.params["endStartDate"] = this.showStartDate[1];
      }
      if (null != this.showEndDate && '' != this.showEndDate) {
        this.queryParams.params["beginEndDate"] = this.showEndDate[0];
        this.queryParams.params["endEndDate"] = this.showEndDate[1];
      }
      listProduct(this.queryParams).then(response => {
        this.productList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    dayCountCancel() {
      this.openDayCount = false;
      this.dayCount = '';
      this.reset();
    },
    dayCountSubmitForm() {
      this.buttonLoading = true;
      setProductDayCount(this.dayCount, this.form).then(response => {
        this.$modal.msgSuccess("操作成功");
        this.openDayCount = false;
      }).finally(() => {
        this.buttonLoading = false;
      });
    },
    // 表单重置
    reset() {
      this.form = {
        productId: undefined,
        externalProductId: undefined,
        productName: undefined,
        productAbbreviation: undefined,
        productSubhead: undefined,
        productImg: undefined,
        productAffiliation: undefined,
        productType: undefined,
        pickupMethod: undefined,
        showOriginalAmount: "0",
        originalAmount: undefined,
        sellAmount: undefined,
        vipUpAmount: undefined,
        vipAmount: undefined,
        toType: "0",
        showIndex: '0',
        appId: undefined,
        url: undefined,
        status: "0",
        search: undefined,
        showStartDate: undefined,
        showEndDate: undefined,
        sellStartDate: undefined,
        sellEndDate: undefined,
        assignDate: "0",
        unionPay: undefined,
        weekDate: undefined,
        sellTime: undefined,
        totalCount: undefined,
        monthCount: undefined,
        weekCount: undefined,
        dayCount: undefined,
        dayUserCount: undefined,
        weekUserCount: undefined,
        monthUserCount: undefined,
        totalUserCount: undefined,
        description: undefined,
        providerLogo: undefined,
        providerName: undefined,
        tags: undefined,
        showCity: undefined,
        merchantId: undefined,
        shopGroupId: undefined,
        shopId: undefined,
        commercialTenantId: undefined,
        categoryId: undefined,
        btnText: undefined,
        shareTitle: undefined,
        shareName: undefined,
        shareImage: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        delFlag: undefined,
        platformKey: undefined,
        sort: undefined,
        ticket: {
          ticketChooseSeat: undefined,
          ticketForm: undefined,
          ticketCard: undefined,
          ticketNonsupport: undefined,
          ticketInvoice: undefined,
          ticketExpired: undefined,
          ticketAnyTime: undefined,
          ticketPostWay: undefined,
          ticketPostage: undefined,
          ticketNotice: undefined
        },
        ticketSession: [{
          productId: undefined,
          sessionId: undefined,
          session: undefined,
          status: undefined,
          date: undefined,
          ticketLine: [{
            lineId: undefined,
            productId: undefined,
            sessionId: undefined,
            lineTitle: undefined,
            linePrice: undefined,
            lineSettlePrice: undefined,
            lineNumber: undefined,
            lineUpperLimit: undefined,
            lineStatus: undefined
          }],
        }]
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
      this.ids = selection.map(item => item.productId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品";
      this.activeName = this.tabNameList[0];
      this.cityNodeAll = true;
      this.isUpdate = false;
      this.getCityTreeselect();
    },
    getCityTreeselect() {
      cityTreeselect().then(response => {
        this.cityOptions = response.data;
      });
    },
    getShowCityTreeselect(productId) {
      return productShowCityTreeSelect(productId).then(response => {
        this.cityOptions = response.data.citys;
        return response;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      this.activeName = this.tabNameList[0];
      const productId = row.productId || this.ids
      const showCity = this.getShowCityTreeselect(productId);
      this.isUpdate = true;
      getProduct(productId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改商品";
        if (this.form && this.form.weekDate) {
          this.form.weekDate = this.form.weekDate.split(",");
        }
        if (this.form && this.form.sellTime) {
          this.form.sellTime = this.form.sellTime.split("-")
        }
        if (this.form && this.form.categoryId) {
          this.form.categoryId = this.form.categoryId.split(",")
        }
        if (this.form && this.form.commercialTenantId) {
          this.form.commercialTenantId = this.form.commercialTenantId.split(",")
        }
        debugger
        if (this.form && this.form.shopId) {
          this.selectShop(response.data.shopId);
          this.form.shopId = this.form.shopId.split(",")
        }
        this.cityNodeAll = false;
        this.$nextTick(() => {
          showCity.then(res => {
            let checkedKeys = res.data.checkedKeys;
            checkedKeys.forEach((v) => {
              if (v == 99) {
                this.cityNodeAll = true;
              } else {
                this.$nextTick(() => {
                  this.$refs.city.setChecked(v, true, false);
                })
              }
            })
          })
        })
      });
    },
    handleUpdateDayCount(row) {
      this.reset();
      this.dayCount = '';
      this.form.productId = row.productId
      this.openDayCount = true;
    },
    handleTicketSessionLine(row) {
      const productId = row.productId;
      const params = {pageNum: this.queryParams.pageNum};
      this.$tab.openPage("配置[" + row.productName + "]的场次与票种", '/zlyyh/productTicketSession/index/' + productId, params);
    },
    //所有菜单节点数据
    getCityAllCheckedKeys() {
      //目前被选中的城市节点
      let checkedKeys = this.$refs.city.getCheckedKeys();
      //半选中的城市节点
      let halfCheckedKeys = this.$refs.city.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.productType === '13') {
            let check = this.checkTicketSession(this.form.ticketSession);
            if (check === 0) {
              return;
            }
          }
          this.buttonLoading = true;
          if (this.cityNodeAll) {
            this.form.showCity = "ALL";
          } else {
            this.form.showCity = this.getCityAllCheckedKeys().toString();
          }
          if (this.form.weekDate) {
            this.form.weekDate = this.form.weekDate.toString();
          }
          if (this.form.sellTime) {
            this.form.sellTime = this.form.sellTime[0] + "-" + this.form.sellTime[1];
          }
          if (this.form.categoryId) {
            this.form.categoryId = this.form.categoryId.toString();
          }
          if (this.form.commercialTenantId) {
            this.form.commercialTenantId = this.form.commercialTenantId.toString();
          }
          if (this.form.shopId) {
            this.form.shopId = this.form.shopId.join(",");
          }
          if (this.form.productId != null) {
            updateProduct(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addProduct(this.form).then(response => {
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
      const productIds = row.productId || this.ids;
      this.$modal.confirm('是否确认删除商品编号为"' + productIds + '"的数据项？').then(() => {
        this.loading = true;
        return delProduct(productIds);
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
      this.download('zlyyh-admin/product/export', {
        ...this.queryParams
      }, `product_${new Date().getTime()}.xlsx`)
    },
    // 新增行
    addSessionRow() {
      if (this.form.ticketSession.length === 3) {
        this.$modal.msg("场次已达上限。");
        return;
      }
      const row = {
        productId: undefined,
        sessionId: undefined,
        session: undefined,
        status: undefined,
        date: undefined,
        ticketLine: [],
      };
      this.form.ticketSession.push(row)
      this.addLineRow(row);
    },
    // 删除行
    delSessionRow(row) {
      const index = this.form.ticketSession.indexOf(row)
      this.form.ticketSession.splice(index, 1);
    },
    // 新增行
    addLineRow(row) {
      if (row.ticketLine.length === 3) {
        this.$modal.msg("票种已达上限。");
        return;
      }
      const rows = {
        lineId: undefined,
        productId: undefined,
        sessionId: undefined,
        lineTitle: undefined,
        linePrice: undefined,
        lineSettlePrice: undefined,
        lineNumber: undefined,
        lineUpperLimit: undefined,
        lineStatus: undefined
      };
      row.ticketLine.push(rows);
    },    // 删除行
    delLineRow(row1, row2) {
      const index = row1.ticketLine.indexOf(row2)
      row1.ticketLine.splice(index, 1);
    },
    // 演出票数据校验
    checkTicketSession(ticketSession) {
      debugger
      if (this.form.ticket.ticketForm === undefined) {
        this.$modal.msgWarning("票形式不能为空！");
        return 0;
      }
      if (this.form.ticket.ticketChooseSeat === undefined) {
        this.$modal.msgWarning("选座方式不能为空！");
        return 0;
      }
      if (this.form.ticket.ticketCard === undefined) {
        this.$modal.msgWarning("身份信息不能为空！");
        return 0;
      }

      if (this.form.ticket.ticketPostWay === undefined) {
        this.$modal.msgWarning("快递方式不能为空！");
        return 0;
      }

      if (this.form.ticket.ticketNonsupport === undefined) {
        this.$modal.msgWarning("不支持退不能为空！");
        return 0;
      }
      if (this.form.ticket.ticketInvoice === undefined) {
        this.$modal.msgWarning("电子发票不能为空！");
        return 0;
      }
      if (this.form.ticket.ticketExpired === undefined) {
        this.$modal.msgWarning("过期退不能为空！");
        return 0;
      }
      if (this.form.ticket.ticketAnyTime === undefined) {
        this.$modal.msgWarning("随时退不能为空！");
        return 0;
      }
      if (ticketSession.length === 0) {
        this.$modal.msgWarning("场次信息不能为空！");
      } else {
        for (let i = 0; i < ticketSession.length; i++) {
          const session = ticketSession[i];
          if (session === undefined) {
            this.$modal.msgWarning("场次信息不能为空！");
            return 0;
          }
          if (session.session == null || session.session === '' || session.session === undefined) {
            this.$modal.msgWarning("场次名称不能为空！");
            return 0;
          }
          if (session.status == null || session.status === '' || session.status === undefined) {
            this.$modal.msgWarning("场次状态不能为空！");
            return 0;
          }
          if (session.date == null || session.date === '' || session.date === undefined) {
            this.$modal.msgWarning("场次日期不能为空！");
            return 0;
          }
          if (session.ticketLine.length === 0) {
            this.$modal.msgWarning("票种信息不能为空！");
            return 0;
          }
          for (let j = 0; j < session.ticketLine.length; j++) {
            const ticketLine = session.ticketLine[j];
            if (ticketLine === undefined) {
              this.$modal.msgWarning("票种信息不能为空！");
              return 0;
            }
            if (ticketLine.lineTitle == null || ticketLine.lineTitle === '' || ticketLine.lineTitle === undefined) {
              this.$modal.msgWarning("票种名称不能为空！");
              return 0;
            }
            if (ticketLine.linePrice == null || ticketLine.linePrice === '' || ticketLine.linePrice === undefined) {
              this.$modal.msgWarning("销售价格不能为空！");
              return 0;
            }
            if (ticketLine.lineSettlePrice == null || ticketLine.lineSettlePrice === '' || ticketLine.lineSettlePrice === undefined) {
              this.$modal.msgWarning("结算价格不能为空！");
              return 0;
            }
            if (ticketLine.lineNumber == null || ticketLine.lineNumber === '' || ticketLine.lineNumber === undefined) {
              this.$modal.msgWarning("总数量不能为空！");
              return 0;
            }
            if (ticketLine.lineUpperLimit == null || ticketLine.lineUpperLimit === '' || ticketLine.lineUpperLimit === undefined) {
              this.$modal.msgWarning("单次购买上限不能为空！");
              return 0;
            }
            if (ticketLine.lineStatus == null || ticketLine.lineStatus === '' || ticketLine.lineStatus === undefined) {
              this.$modal.msgWarning("状态不能为空！");
              return 0;
            }
          }
        }
      }
    },
    renderHeader(h, {column}) {
      let currentLabel = column.label;
      return h('span', {}, [
        h('span', {style: 'color:red'}, '* '),
        h('span', {}, currentLabel)
      ])
    },
  }
};
</script>
