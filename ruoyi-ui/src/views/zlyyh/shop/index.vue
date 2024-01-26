<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商户" prop="commercialTenantId">
        <el-select v-model="queryParams.commercialTenantId" placeholder="请选择商户" clearable>
          <el-option v-for="item in commercialTenantList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="门店信息" prop="shopName">
        <el-input v-model="queryParams.shopName" placeholder="门店名称/门店地址" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="省市县" prop="province">
        <el-input v-model="queryParams.province" placeholder="请输入省/市/县" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_shop_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="商户审核" prop="examineVerifier">
        <el-select v-model="queryParams.examineVerifier" placeholder="请选择商户审核状态" clearable>
          <el-option v-for="dict in dict.type.t_examine_verifier" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:shop:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:shop:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:shop:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport"
          v-hasPermi="['zlyyh:shop:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:shop:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商户" align="center" prop="commercialTenantId" :formatter="merFormatter" />
      <el-table-column label="门店名称" align="center" prop="shopName" />
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter" />
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
      <el-table-column label="省市区编码" align="left" prop="procode" width="180">
        <template slot-scope="scope">
          <div>
            <span>省行政编码：{{scope.row.procode}}</span><br>
            <span>市行政编码：{{scope.row.citycode}}</span><br>
            <span>县(区)行政编码：{{scope.row.adcode}}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="经纬度" align="left" prop="longitude" width="150">
        <template slot-scope="scope">
          <div>
            <span>经度：{{scope.row.longitude}}</span><br>
            <span>纬度：{{scope.row.latitude}}</span>
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleProductByShop(scope.row)"
            v-hasPermi="['zlyyh:shop:merNoEdit']">商品维护</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleMerNoEdit(scope.row)"
            v-hasPermi="['zlyyh:shop:merNoEdit']">商户号配置</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shop:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:shop:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改门店对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" clearable style="width: 90%;">
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商户" prop="commercialTenantId">
              <el-select v-model="form.commercialTenantId" placeholder="请选择商户" clearable>
                <el-option v-for="item in commercialTenantList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店名称" prop="shopName" style="width: 92%;">
              <el-input v-model="form.shopName" placeholder="请输入门店名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店图片" prop="shopImgs">
              <image-upload v-model="form.shopImgs" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店logo" prop="shopLogo">
              <image-upload v-model="form.shopLogo" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店电话" prop="shopTel" style="width: 92%;">
              <el-input v-model="form.shopTel" placeholder="请输入门店电话" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="营业时间" prop="businessHours" style="width: 92%;">
              <el-input v-model="form.businessHours" placeholder="请输入营业时间" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展示开始时间" prop="showStartDate">
              <el-date-picker clearable v-model="form.showStartDate" type="datetime" style="width: 90%;"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展示结束时间" prop="showEndDate">
              <el-date-picker clearable v-model="form.showEndDate" type="datetime" style="width: 90%;"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示结束时间" default-time="23:59:59">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展示区间" prop="sellTime">
              <el-time-picker is-range v-model="form.sellTime" range-separator="-" start-placeholder="开始时间"
                end-placeholder="结束时间" placeholder="选择时间范围" style="width: 90%;" value-format="HH:mm:ss">
              </el-time-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.t_shop_status" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商户审核" prop="examineVerifier">
              <el-radio-group v-model="form.examineVerifier">
                <el-radio v-for="dict in dict.type.t_examine_verifier" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="指定周几" prop="assignDate">
              <el-select v-model="form.assignDate" placeholder="请选择指定周几" style="width: 90%;">
                <el-option v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- v-if="form.assignDate == '1'" -->
            <el-form-item label="周几能领" prop="weekDate" v-if="form.assignDate == '1'">
              <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 90%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商圈" prop="businessDistrictId">
              <el-select v-model="form.businessDistrictId" placeholder="请选择商圈" filterable clearable multiple
                style="width: 90%;">
                <el-option v-for="item in businessDistrictList" :key="item.id" :value="item.id" :label="item.label">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="共享" prop="isShare">
              <el-select v-model="form.isShare" placeholder="请选择是否共享" style="width: 90%;">
                <el-option v-for="dict in dict.type.sys_yes_no" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier" style="width: 92%;">
              <el-select v-model="form.supplier" placeholder="请选择供应商" style="width: 100%;">
                <el-option v-for="dict in supplierList" :key="dict.id" :label="dict.label"
                           :value="dict.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收款账户" prop="account" style="width: 92%;">
              <el-input v-model="form.account" placeholder="请输入收款账户" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性质" prop="nature">
              <el-select v-model="form.nature" placeholder="请选择性质" style="width: 90%;">
                <el-option v-for="dict in dict.type.nature_type" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动类型" prop="activity">
              <el-select v-model="form.activity" multiple placeholder="请选择活动类型" style="width: 90%;">
                <el-option v-for="dict in dict.type.activity_type" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票类型" prop="invoice">
              <el-select v-model="form.invoice" placeholder="请选择发票类型" style="width: 90%;">
                <el-option v-for="dict in dict.type.invoice_type" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签" prop="tags">
              <el-select v-model="form.tagsList" multiple placeholder="请选择标签" style="width: 90%;">
                <el-option v-for="item in tagsList" :key="item.tagsId" :label="item.tagsName" :value="item.tagsId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
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
          <el-col :span="10">
            <el-form-item label="营业执照" prop="license">
              <image-upload :limit="1" v-model="form.license" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="门店地址" prop="address">
              <el-input v-model="form.address" type="textarea" placeholder="请输入门店地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input disabled v-model="form.longitude" placeholder="经度,点击下方地图获取" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input disabled v-model="form.latitude" placeholder="纬度,点击下方地图获取" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="poi地址">
          <span slot="label">
            poi地址
            <el-tooltip content="poi地址只做地址辅助选项,保存的是门店址" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="poiAddress" style="width: 90%;" @change="poiChange"
            placeholder="选择poi地址（poi地址只做地址辅助选项,保存的是商户店址）" clearable>
            <el-option v-for="(poi,index) in pois" :key="index" :label="poi.address" :value="index" />
          </el-select>
        </el-form-item>
        <el-row>
          <div style="text-align: center;color:red;font-weight: bold">商户地址经纬度必须从地图上选择对应的位置获取</div>
          <el-amap-search-box class="search-box" :search-option="searchOption" :on-search-result="onSearchResult">
          </el-amap-search-box>
          <el-col :span="24">
            <el-amap vid="amapDemo" :center="center" :zoom="zoom" :plugin="plugin" :events="events"
              style="width: 90%;height: 400px;">
              <el-amap-marker v-for="(marker, index) in markers" :position="marker" :key="'marker' + index"
                :events="events"></el-amap-marker>
            </el-amap>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 门店导入对话框 -->
    <el-dialog title="门店导入" :visible.sync="importOpen" width="500px" append-to-body>
      <el-form ref="importForm" :model="importForm" :rules="rules" label-width="80px">
        <el-form-item label="平台" prop="platformKey">
          <el-select style="width: 90%;" v-model="importForm.platformKey" placeholder="请选择平台标识" clearable>
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="productIds">
          <el-select style="width: 90%;" v-model="importForm.productId" placeholder="请选择商品" clearable>
            <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
              <span style="float: left">{{ item.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-upload style="text-align: center;" ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url" :data="importForm" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
            @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="importOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 商户号配置 -->
    <el-dialog title="商户号配置" :visible.sync="shopMerchatOpen" width="1200px" append-to-body>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleShopMerchantAdd"
            v-hasPermi="['zlyyh:shopMerchant:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple"
            @click="handleShopMerchantDelete" v-hasPermi="['zlyyh:shopMerchant:remove']">删除</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="shopMerchant.showSearch" @queryTable="getShopMerchantList"></right-toolbar>
      </el-row>

      <el-table v-loading="shopMerchant.loading" :data="shopMerchant.shopMerchantList">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="商户号" align="center" prop="merchantNo" />
        <el-table-column label="商户类型" align="center" prop="merchantType">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_shop_merchant_type" :value="scope.row.merchantType" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_shop_merchant_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleShopMerchantUpdate(scope.row)"
              v-hasPermi="['zlyyh:shopMerchant:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleShopMerchantDelete(scope.row)"
              v-hasPermi="['zlyyh:shopMerchant:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="shopMerchant.total>0" :total="shopMerchant.total"
        :page.sync="shopMerchant.queryParams.pageNum" :limit.sync="shopMerchant.queryParams.pageSize"
        @pagination="getShopMerchantList" />

      <!-- 添加或修改门店商户号对话框 -->
      <el-dialog :title="shopMerchant.title" :visible.sync="shopMerchant.open" width="500px" append-to-body>
        <el-form ref="shopMerchantForm" :model="shopMerchant.form" :rules="shopMerchant.rules" label-width="80px">
          <el-form-item label="商户号" prop="merchantNo">
            <el-input v-model="shopMerchant.form.merchantNo" placeholder="请输入商户号" />
          </el-form-item>
          <el-form-item label="商户类型" prop="merchantType">
            <el-select v-model="shopMerchant.form.merchantType" placeholder="请选择商户类型">
              <el-option v-for="dict in dict.type.t_shop_merchant_type" :key="dict.value" :label="dict.label"
                :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="shopMerchant.form.status">
              <el-radio v-for="dict in dict.type.t_shop_merchant_status" :key="dict.value" :label="dict.value">
                {{dict.label}}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="shopMerchant.form.remark" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="shopMerchantSubmitForm">确 定</el-button>
          <el-button @click="shopMerchantCancel">取 消</el-button>
        </div>
      </el-dialog>
    </el-dialog>
    <el-dialog title="门店商品" :visible.sync="isProduct" width="90%">
      <Product v-bind:shopId=shopId></Product>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listShop,
    getShop,
    delShop,
    addShop,
    updateShop
  } from "@/api/zlyyh/shop";
  import {
    listShopMerchant,
    getShopMerchant,
    delShopMerchant,
    addShopMerchant,
    updateShopMerchant
  } from "@/api/zlyyh/shopMerchant";
  import { selectListMerchant } from "@/api/zlyyh/commercialTenant";
  import { selectListBusinessDistrict } from "@/api/zlyyh/businessDistrict";
  import { selectListPlatform } from "@/api/zlyyh/platform";
  import { selectListProduct } from "@/api/zlyyh/product";
  import { getToken } from "@/utils/auth";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";
  import Treeselect from "@riophae/vue-treeselect";
  import { exportTags } from "@/api/zlyyh/tags";
  import Product from "@/views/zlyyh/product/info.vue";
  import {selectSupplier} from "@/api/zlyyh/supplier";

  export default {
    name: "Shop",
    dicts: ['t_shop_status', 't_shop_merchant_type', 't_shop_merchant_status', 't_product_assign_date',
      't_grad_period_date_list', 'nature_type', 'invoice_type', 'activity_type', 'sys_yes_no','channel_type','t_examine_verifier'
    ],
    components: {
      Treeselect,
      Product
    },
    data() {
      let self = this
      return {
        shopId: undefined,
        // 部门树选项
        deptOptions: undefined,
        shopMerchatOpen: false,
        //平台下拉列表
        platformList: [],
        //商户下拉列表
        commercialTenantList: [],
        //商品下拉列表
        productList: [],
        // 商品展示页面
        isProduct: false,
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
        // 供应商
        supplierList: [],
        //商圈列表 选择商圈
        businessDistrictList: [],
        // 标签列表 选择标签
        tagsList: [],
        pois: [],
        poiAddress: '',
        address: {},
        searchOption: {
          city: '杭州',
          citylimit: false
        },
        markers: [],
        center: [121.59996, 31.197646],
        zoom: 12,
        plugin: [{
          pName: 'Geolocation',
          events: {
            init(o) {
              // o 是高德地图定位插件实例
              o.getCurrentPosition((status, result) => {
                if (result && result.position) {
                  self.center = [result.position.lng, result.position.lat]
                  self.$nextTick()
                }
              })
            }
          }
        }, ],
        events: {
          click(e) {
            let {
              lng,
              lat
            } = e.lnglat
            self.form.longitude = lng
            self.form.latitude = lat

            // 这里通过高德 SDK 完成。
            var geocoder = new AMap.Geocoder({
              radius: 1000,
              extensions: 'all'
            })
            geocoder.getAddress([lng, lat], function(status, result) {
              if (status === 'complete' && result.info === 'OK') {
                if (result && result.regeocode) {
                  self.form.address = result.regeocode.formattedAddress
                  self.pois = result.regeocode.pois;
                  self.poiAddress = '';
                  // 结构化地址信息
                  let ad = result.regeocode.addressComponent;
                  self.form.formattedAddress = result.regeocode.formattedAddress;
                  //再加入省市区名称
                  self.form.province = ad.province
                  self.form.city = ad.city
                  self.form.district = ad.district
                  //省行政编码最后面4个0
                  self.form.procode = ad.adcode.substring(0, 2) + '0000'
                  //市编码最后2个0
                  self.form.citycode = ad.adcode.substring(0, 4) + '00';
                  //区县编码
                  self.form.adcode = ad.adcode;
                  self.$nextTick()
                }
              }
            })
          }
        },

        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        importOpen: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: "shop_id",
          isAsc: 'desc',
          commercialTenantId: undefined,
          shopName: undefined,
          shopTel: undefined,
          businessHours: undefined,
          address: undefined,
          status: undefined,
          formattedAddress: undefined,
          province: undefined,
          city: undefined,
          district: undefined,
          procode: undefined,
          citycode: undefined,
          adcode: undefined,
          longitude: undefined,
          latitude: undefined,
          platformKey: undefined,
          examineVerifier: undefined
        },
        // 表单参数
        form: {},
        importForm: {},
        uploadSubmitUrl: '',
        upload: {
          // 是否显示弹出层（用户导入）
          open: false,
          // 弹出层标题（用户导入）
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 是否更新已经存在的用户数据
          updateSupport: 0,
          // 设置上传的请求头部
          headers: {
            Authorization: "Bearer " + getToken()
          },
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/zlyyh-admin/shop/importData",
        },
        // 表单校验
        rules: {
          shopId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          shopName: [{
            required: true,
            message: "门店名称不能为空",
            trigger: "blur"
          }]
        },
        //门店商户号
        shopMerchant: {
          shopId: '',
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
          // 门店商户号表格数据
          shopMerchantList: [],
          // 弹出层标题
          title: "",
          // 是否显示弹出层
          open: false,
          // 查询参数
          queryParams: {
            pageNum: 1,
            pageSize: 10,
            shopId: undefined,
            merchantNo: undefined,
            merchantType: undefined,
            status: undefined,
          },
          // 表单参数
          form: {},
          // 表单校验
          rules: {
            id: [{
              required: true,
              message: "ID不能为空",
              trigger: "blur"
            }],
            shopId: [{
              required: true,
              message: "门店ID不能为空",
              trigger: "blur"
            }],
            merchantNo: [{
              required: true,
              message: "商户号不能为空",
              trigger: "blur"
            }],
            merchantType: [{
              required: true,
              message: "商户类型不能为空",
              trigger: "change"
            }],
            status: [{
              required: true,
              message: "状态不能为空",
              trigger: "change"
            }],
          }
        },
      };
    },
    created() {
      this.getList();
      this.getPlatformSelectList();
      this.getMerSelectList();
      this.getBusinessDistrictList();
      this.getProductSelectList();
      this.getTagsList();
      this.selectSupplierList();
    },
    methods: {
      poiChange(index) {
        let poi = this.pois[index];
        let self = this;
        self.form.longitude = poi.location.lng
        self.form.latitude = poi.location.lat
        self.form.address = poi.address
        // 这里通过高德 SDK 完成。
        var geocoder = new AMap.Geocoder({
          radius: 1000,
          extensions: 'all'
        })
        geocoder.getAddress([poi.location.lng, poi.location.lat], function(status, result) {
          if (status === 'complete' && result.info === 'OK') {
            if (result && result.regeocode) {
              // 结构化地址信息
              let ad = result.regeocode.addressComponent;
              self.form.formattedAddress = result.regeocode.formattedAddress;
              //再加入省市区名称
              self.form.province = ad.province
              self.form.city = ad.city
              self.form.district = ad.district
              //省行政编码最后面4个0
              self.form.procode = ad.adcode.substring(0, 2) + '0000'
              //市编码最后2个0
              self.form.citycode = ad.adcode.substring(0, 4) + '00';
              //区县编码
              self.form.adcode = ad.adcode;
              self.$nextTick()
            }
          }
        })
      },
      onSearchResult(pois) {
        let latSum = 0
        let lngSum = 0
        if (pois.length > 0) {
          pois.forEach(poi => {
            let {
              lng,
              lat
            } = poi
            lngSum += lng
            latSum += lat
            this.markers.push([poi.lng, poi.lat])
          })
          let mapcenter = {
            lng: lngSum / pois.length,
            lat: latSum / pois.length
          }
          this.center = [mapcenter.lng, mapcenter.lat]
        }
      },
      //商品下拉列表
      getProductSelectList() {
        selectListProduct({
          status: '0'
        }).then(response => {
          this.productList = response.data;
        });
      },
      /** 查询门店列表 */
      getList() {
        this.loading = true;
        listShop(this.queryParams).then(response => {
          this.shopList = response.rows;
          this.total = response.total;
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
      //商户下拉列表
      getMerSelectList() {
        selectListMerchant({}).then(response => {
          this.commercialTenantList = response.data;
        });
      },
      getBusinessDistrictList() {
        selectListBusinessDistrict({}).then(response => {
          this.businessDistrictList = response.data;
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
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          shopId: undefined,
          commercialTenantId: undefined,
          shopName: undefined,
          shopTel: undefined,
          businessHours: undefined,
          address: undefined,
          status: '0',
          formattedAddress: undefined,
          province: undefined,
          city: undefined,
          district: undefined,
          procode: undefined,
          citycode: undefined,
          adcode: undefined,
          longitude: undefined,
          latitude: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          platformKey: undefined,
          businessDistrict: undefined,
          isShare: undefined,
          supplier: undefined,
          license: undefined,
          nature: undefined,
          invoice: undefined,
          account: undefined,
          activity: undefined,
          tagsList: undefined,
          supportChannel: []
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
        this.ids = selection.map(item => item.shopId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加门店";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const shopId = row.shopId || this.ids
        getShop(shopId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改门店";
          if (this.form && this.form.businessDistrictId) {
            this.form.businessDistrictId = this.form.businessDistrictId.split(",")
          }
          if (response.data.supportChannel) {
            this.form.supportChannel = response.data.supportChannel.split(",")
          } else {
            this.form.supportChannel = []
          }
          if (response.data.activity) {
            this.form.activity = response.data.activity.split(",")
          }
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.businessDistrictId) {
              this.form.businessDistrictId = this.form.businessDistrictId.toString();
            }
            if (this.form.supportChannel) {
              this.form.supportChannel = this.form.supportChannel.join(",")
            }
            if (this.form.activity) {
              this.form.activity = this.form.activity.join(",")
            }
            if (this.form.shopId != null) {
              updateShop(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addShop(this.form).then(response => {
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
        const shopIds = row.shopId || this.ids;
        this.$modal.confirm('是否确认删除门店编号为"' + shopIds + '"的数据项？').then(() => {
          this.loading = true;
          return delShop(shopIds);
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
        this.download('zlyyh-admin/shop/export', {
          ...this.queryParams
        }, `shop_${new Date().getTime()}.xlsx`)
      },
      /** 下载模板操作 */
      importTemplate() {
        this.download('zlyyh-admin/shop/importTemplate', {}, `shop_template_${new Date().getTime()}.xlsx`)
      },
      // 文件上传中处理
      handleFileUploadProgress(event, file, fileList) {
        this.upload.isUploading = true;
      },
      // 文件上传成功处理
      handleFileSuccess(response, file, fileList) {
        this.importOpen = false;
        this.upload.isUploading = false;
        this.$refs.upload.clearFiles();
        this.getList();
      },
      /** 导入按钮操作 */
      handleImport() {
        this.importForm = {
          platformKey: undefined,
          productId: '',
        };
        this.resetForm("importForm");
        this.importOpen = true;
      },
      /** 导入提交按钮 */
      submitFileForm() {
        this.$refs["importForm"].validate(valid => {
          if (valid) {
            this.$refs.upload.submit();
          }
        });
      },
      // 商品维护
      handleProductByShop(row) {
        this.shopId =  row.shopId;
        this.isProduct = true;
      },
      //商户号配置按钮
      handleMerNoEdit(row) {
        this.shopMerchatOpen = true;
        this.shopMerchant.shopId = row.shopId;
        this.getShopMerchantList();
      },
      /** 查询门店商户号列表 */
      getShopMerchantList() {
        this.shopMerchant.loading = true;
        this.shopMerchant.queryParams.shopId = this.shopMerchant.shopId;
        listShopMerchant(this.shopMerchant.queryParams).then(response => {
          this.shopMerchant.shopMerchantList = response.rows;
          this.shopMerchant.total = response.total;
          this.shopMerchant.loading = false;
        });
      },
      // 取消按钮
      shopMerchantCancel() {
        this.shopMerchant.open = false;
        this.shopMerchantReset();
      },
      // 表单重置
      shopMerchantReset() {
        this.shopMerchant.form = {
          id: undefined,
          shopId: undefined,
          merchantNo: undefined,
          merchantType: undefined,
          status: '0',
          remark: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined
        };
        this.resetForm("shopMerchantForm");
      },
      /** 新增按钮操作 */
      handleShopMerchantAdd() {
        this.shopMerchantReset();
        this.shopMerchant.form.shopId = this.shopMerchant.shopId;
        this.shopMerchant.open = true;
        this.shopMerchant.title = "添加门店商户号";
      },
      /** 修改按钮操作 */
      handleShopMerchantUpdate(row) {
        this.shopMerchant.loading = true;
        this.shopMerchantReset();
        const id = row.id || this.shopMerchant.ids
        getShopMerchant(id).then(response => {
          this.shopMerchant.loading = false;
          this.shopMerchant.form = response.data;
          this.shopMerchant.open = true;
          this.shopMerchant.title = "修改门店商户号";
        });
      },
      /** 提交按钮 */
      shopMerchantSubmitForm() {
        this.$refs["shopMerchantForm"].validate(valid => {
          if (valid) {
            this.shopMerchant.buttonLoading = true;
            if (this.shopMerchant.form.id != null) {
              updateShopMerchant(this.shopMerchant.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.shopMerchant.open = false;
                this.getShopMerchantList();
              }).finally(() => {
                this.shopMerchant.buttonLoading = false;
              });
            } else {
              addShopMerchant(this.shopMerchant.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.shopMerchant.open = false;
                this.getShopMerchantList();
              }).finally(() => {
                this.shopMerchant.buttonLoading = false;
              });
            }
          }
        });
      },
      /** 查询标签 */
      getTagsList() {
        const param = {
          'tagsType': '2'
        }
        exportTags(param).then(response => {
          this.tagsList = response.data;
        });
      },
      /** 查询供应商 */
      selectSupplierList() {
        selectSupplier(this.form).then(response => {
          this.supplierList = response.data;
        }).finally(() => {
        });
      },
      /** 删除按钮操作 */
      handleShopMerchantDelete(row) {
        const ids = row.id || this.shopMerchant.ids;
        this.$modal.confirm('是否确认删除门店商户号编号为"' + ids + '"的数据项？').then(() => {
          this.shopMerchant.loading = true;
          return delShopMerchant(ids);
        }).then(() => {
          this.shopMerchant.loading = false;
          this.getShopMerchantList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.shopMerchant.loading = false;
        });
      },
    }
  };
</script>
