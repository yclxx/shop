<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商圈名称" prop="businessDistrictName">
        <el-input
          v-model="queryParams.businessDistrictName"
          placeholder="请输入商圈名称"
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
      <el-form-item label="平台" prop="platformKey">
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
          v-hasPermi="['zlyyh:businessDistrict:add']"
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
          v-hasPermi="['zlyyh:businessDistrict:edit']"
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
          v-hasPermi="['zlyyh:businessDistrict:remove']"
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
          v-hasPermi="['zlyyh:businessDistrict:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="businessDistrictList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="businessDistrictId" v-if="true"/>
      <el-table-column label="商圈图片" align="left" prop="businessDistrictImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.businessDistrictImg" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="商圈信息" align="left" prop="businessMobile" width="150">
        <template slot-scope="scope">
          <span>商圈名称：{{ scope.row.businessDistrictName }}</span><br>
          <span>电话：{{ scope.row.businessMobile }}</span><br>
          <span>营业时间：{{ scope.row.businessHours }}</span>
        </template>
      </el-table-column>
      <el-table-column label="位置信息" align="left" prop="address" width="230">
        <template slot-scope="scope">
          <span>地址：{{ scope.row.address }}</span><br>
          <span>结构化地址：{{ scope.row.formattedAddress }}</span>
        </template>
      </el-table-column>
      <el-table-column label="城市信息" align="left" prop="address" width="180">
        <template slot-scope="scope">
          <span>省份：{{ scope.row.province }}({{ scope.row.procode }})</span><br>
          <span>城市：{{ scope.row.city }}({{ scope.row.citycode }})</span><br>
          <span>县市：{{ scope.row.district }}({{ scope.row.adcode }})</span>
        </template>
      </el-table-column>
      <el-table-column label="经纬度" align="left" prop="address" width="150">
        <template slot-scope="scope">
          <span>经度：{{ scope.row.longitude }}</span><br>
          <span>纬度：{{ scope.row.latitude }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" align="center" prop="status" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>

      <el-table-column label="自定义区域" align="center" prop="businessDistrictScope" width="150"/>
      <el-table-column label="平台" width="100" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleShop(scope.row)"
            v-hasPermi="['zlyyh:businessDistrict:shop']"
          >门店维护
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:businessDistrict:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:businessDistrict:remove']"
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

    <!-- 添加或修改商圈对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" @change="getPlatformPageSelectList"
                         clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="商圈名称" prop="businessDistrictName" style="width: 94%;">
              <el-input v-model="form.businessDistrictName" placeholder="请输入商圈名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电话" prop="businessMobile" style="width: 94%;">
              <el-input v-model="form.businessMobile" placeholder="请输入电话"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="营业时间" prop="businessHours" style="width: 94%;">
              <el-input v-model="form.businessHours" placeholder="请输入营业时间"/>
            </el-form-item>
          </el-col>

          <el-col :span="8">
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

        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="商圈图片" prop="businessDistrictImg">
              <image-upload :limit="1" v-model="form.businessDistrictImg"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址"/>
        </el-form-item>

        <el-form-item label="结构化地址" prop="formattedAddress" label-width="100px">
          <el-input v-model="form.formattedAddress"
                    placeholder="请输入结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码"/>
        </el-form-item>

        <el-row>
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input disabled v-model="form.longitude" placeholder="经度,点击下方地图获取"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input disabled v-model="form.latitude" placeholder="纬度,点击下方地图获取"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="自定义区" prop="businessDistrictScope" label-width="100px">
          <span slot="label">
            自定义区
            <el-tooltip
              content="基于高德地图,经纬度之间用,隔开 每组经纬度之间用;隔开 例如：经度,纬度;经度,纬度;经度,纬度;经度,纬度"
              placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="form.businessDistrictScope" type="textarea" placeholder="请输入内容"/>
        </el-form-item>

        <el-form-item label="poi地址">
          <span slot="label">
            poi地址
            <el-tooltip content="poi地址只做地址辅助选项,保存的是商户店址" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="poiAddress" style="width: 100%;" @change="poiChange"
                     placeholder="选择poi地址（poi地址只做地址辅助选项,保存的是商户店址）" clearable>
            <el-option v-for="(poi,index) in pois" :key="index" :label="poi.address" :value="index"/>
          </el-select>
        </el-form-item>
        <el-row>
          <div style="text-align: center;color:red;font-weight: bold">商户地址经纬度必须从地图上选择对应的位置获取</div>
          <el-amap-search-box class="search-box" :search-option="searchOption" :on-search-result="onSearchResult">
          </el-amap-search-box>
          <el-col :span="24">
            <el-amap vid="amapDemo" :center="center" :zoom="zoom" :plugin="plugin" :events="events"
                     style="width: 100%;height: 400px;">
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
    <el-dialog title="商圈门店" :visible.sync="ShopVisible" width="90%">
      <Shop v-bind:businessDistrictId=businessDistrictId></Shop>
    </el-dialog>
  </div>
</template>

<script>
import {
  listBusinessDistrict,
  getBusinessDistrict,
  delBusinessDistrict,
  addBusinessDistrict,
  updateBusinessDistrict
} from "@/api/zlyyh/businessDistrict";
import {selectListPlatform} from "@/api/zlyyh/platform";
import {deptTreeSelect} from "@/api/system/user";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import Treeselect from "@riophae/vue-treeselect";
import {selectListPage} from "@/api/zlyyh/page";
import Shop from "@/views/zlyyh/shop/businessShop.vue";

export default {
  name: "BusinessDistrict",
  dicts: ['sys_normal_disable'],
  components: {
    Shop,
    Treeselect
  },
  data() {
    let self = this
    return {
      // 按钮loading
      buttonLoading: false,
      //平台下拉列表
      platformList: [],
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
      // 商圈表格数据
      businessDistrictList: [],
      // 部门树选项
      deptOptions: undefined,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 门店相关数据
      ShopVisible: false,
      businessDistrictId: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        businessDistrictName: undefined,
        businessDistrictImg: undefined,
        businessMobile: undefined,
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
        businessDistrictScope: undefined,
        platformKey: undefined,
        sysDeptId: undefined,
        sysUserId: undefined
      },

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
      },],
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
          geocoder.getAddress([lng, lat], function (status, result) {
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
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        businessDistrictId: [
          {required: true, message: "ID不能为空", trigger: "blur"}
        ],
        businessDistrictName: [
          {required: true, message: "商圈名称不能为空", trigger: "blur"}
        ],
        businessDistrictImg: [
          {required: true, message: "商圈图片不能为空", trigger: "blur"}
        ],
        address: [
          {required: true, message: "地址不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        formattedAddress: [
          {required: true, message: "结构化地址不能为空", trigger: "blur"}
        ],

        longitude: [
          {required: true, message: "经度", trigger: "blur"}
        ],
        latitude: [
          {required: true, message: "纬度", trigger: "blur"}
        ],



      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
    this.getDeptTree();

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
      geocoder.getAddress([poi.location.lng, poi.location.lat], function (status, result) {
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
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    //page下拉列表
    getPlatformPageSelectList() {
      if (this.form && this.form.platformKey) {
        selectListPage({
          platformKey: this.form.platformKey
        }).then(response => {
          this.platformPageList = response.data;
        })
      }
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
    /** 查询商圈列表 */
    getList() {
      this.loading = true;
      listBusinessDistrict(this.queryParams).then(response => {
        this.businessDistrictList = response.rows;
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
        businessDistrictId: undefined,
        businessDistrictName: undefined,
        businessDistrictImg: undefined,
        businessMobile: undefined,
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
        businessDistrictScope: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        platformKey: undefined,
        sysDeptId: undefined,
        sysUserId: undefined
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
      this.ids = selection.map(item => item.businessDistrictId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商圈";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const businessDistrictId = row.businessDistrictId || this.ids
      getBusinessDistrict(businessDistrictId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改商圈";
      });
    },
    /** 门店维护 */
    handleShop(row) {
      this.ShopVisible = true;
      this.businessDistrictId = row.businessDistrictId || this.ids
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.businessDistrictId != null) {
            updateBusinessDistrict(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addBusinessDistrict(this.form).then(response => {
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
      const businessDistrictIds = row.businessDistrictId || this.ids;
      this.$modal.confirm('是否确认删除商圈编号为"' + businessDistrictIds + '"的数据项？').then(() => {
        this.loading = true;
        return delBusinessDistrict(businessDistrictIds);
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
      this.download('zlyyh/businessDistrict/export', {
        ...this.queryParams
      }, `businessDistrict_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
