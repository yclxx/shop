<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="bannerType =='10'" label="通知内容" prop="bannerName">
        <el-input v-model="queryParams.bannerName" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item v-else label="名称" prop="bannerName">
        <el-input v-model="queryParams.bannerName" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="跳转类型" prop="toType">
        <el-select v-model="queryParams.toType" placeholder="请选择跳转类型" clearable>
          <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="展示维度" prop="showDimension">
        <el-select v-model="queryParams.showDimension" placeholder="请选择展示维度" clearable>
          <el-option v-for="dict in dict.type.t_banner_show_dimension" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_banner_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="生效时间" prop="startTime">
        <el-date-picker clearable v-model="startTime" size="small" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间" prop="endTime">
        <el-date-picker clearable v-model="endTime" size="small" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="展示城市" prop="showCity">
        <el-select v-model="queryParams.showCity" placeholder="请选择展示城市" clearable>
          <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label" :value="item.rightLabel" />
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
          v-hasPermi="['zlyyh:banner:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:banner:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:banner:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bannerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="banner图" align="center" prop="bannerImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.bannerImage" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="名称" width="100" align="center" prop="bannerName" />
      <el-table-column v-if="bannerType == '1'" label="角标" align="center" prop="bannerMark" />
      <el-table-column label="排序" align="center" prop="bannerRank" />
      <el-table-column label="显示页面" width="100" align="center" prop="pagePath" :formatter="pageFormatter" />
      <el-table-column label="平台" width="100" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="跳转类型" width="100" align="center" prop="toType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_to_type" :value="scope.row.toType" />
        </template>
      </el-table-column>
      <el-table-column label="小程序ID" width="180" align="center" prop="appId" />
      <el-table-column label="页面地址" width="180" align="center" prop="url" show-overflow-tooltip />
      <el-table-column label="生效时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="展示维度" align="center" prop="showDimension">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_show_dimension" :value="scope.row.showDimension" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:banner:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:banner:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改广告管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" @change="getPlatformPageSelectList" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="bannerType == '1'">
            <el-form-item label="名称" prop="bannerName" style="width: 94%;">
              <el-input v-model="form.bannerName" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-else-if="bannerType=='10'">
            <el-form-item label="通知内容" style="width: 94%;">
              <el-input v-model="form.bannerName" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-else>
            <el-form-item label="名称" style="width: 94%;">
              <el-input v-model="form.bannerName" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="bannerType == '1'">
            <el-form-item label="角标" prop="bannerMark" style="width: 94%;">
              <el-input v-model="form.bannerMark" :maxlength="5" placeholder="请输入角标" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="显示页面" prop="pagePath">
              <el-select v-model="form.pagePath" placeholder="请选择显示页面" clearable>
                <el-option v-for="item in platformPageList" :key="item.rightLabel" :label="item.label"
                  :value="item.rightLabel" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示维度" prop="showDimension">
              <el-select v-model="form.showDimension" placeholder="请选择展示维度">
                <el-option v-for="dict in dict.type.t_banner_show_dimension" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="生效时间" prop="startTime">
              <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="失效时间" prop="endTime">
              <el-date-picker clearable v-model="form.endTime" default-time="23:59:59" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择失效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.t_banner_status" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="指定周几" prop="assignDate">
              <el-radio-group v-model="form.assignDate">
                <el-radio v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="周几显示" prop="weekDate" v-if="form.assignDate == '1'">
              <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="bannerRank" style="width: 94%;">
              <el-input v-model="form.bannerRank" placeholder="请输入排序" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="跳转类型" prop="toType">
              <el-select v-model="form.toType" placeholder="请选择跳转类型" clearable>
                <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.toType == 3">
            <el-form-item label="小程序ID" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入小程序ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.toType != '0' && form.toType != '4' && form.toType != '6'">
            <el-form-item label="页面地址" v-if="form.toType == '3'">
              <el-input v-model="form.url" placeholder="请输入内容" />
            </el-form-item>
            <el-form-item label="页面地址" v-else prop="url">
              <el-input v-model="form.url" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="bannerType == '10'">
          <el-col :span="12" v-if="form.toType == 4">
            <el-form-item label="图片页面" prop="url">
              <image-upload :limit="1" v-model="form.url" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="左侧图标" prop="bannerImage">
              <image-upload :limit="1" v-model="form.bannerImage" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="展示城市" prop="showCity">
              <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
              <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-else>
          <el-col :span="12" v-if="form.toType == 4">
            <el-form-item label="图片页面" prop="url">
              <image-upload :limit="1" v-model="form.url" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图片" prop="bannerImage">
              <image-upload :limit="1" v-model="form.bannerImage" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支持端" prop="supportChannel">
              <el-checkbox-group v-model="form.supportChannel">
                <el-checkbox
                  v-for="item in dict.type.channel_type" :key="item.value" :label="item.value">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="展示城市" prop="showCity">
              <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
              <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
            </el-form-item>
          </el-col>
        </el-row>
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
    listBanner,
    getBanner,
    delBanner,
    addBanner,
    updateBanner
  } from "@/api/zlyyh/banner";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    selectListPage
  } from "@/api/zlyyh/page";
  import {
    treeselect as cityTreeselect,
    bannerShowCityTreeSelect,
    selectCityList
  } from "@/api/zlyyh/area"

  export default {
    name: "common",
    dicts: ['t_banner_show_dimension', 't_banner_type', 't_banner_status', 't_banner_to_type', 't_product_assign_date',
      't_grad_period_date_list', 'channel_type'
    ],
    props: {
      bannerType: {
        type: String
      }
    },
    data() {
      return {
        //城市下拉列表
        cityList: [],
        cityForm: {},
        cityNodeAll: false,
        //城市列表
        cityOptions: [],
        defaultProps: {
          children: "children",
          label: "label"
        },
        //page下拉列表
        pageList: [],
        //平台下拉列表
        platformList: [],
        //生效时间范围
        startTime: [],
        //失效时间范围
        endTime: [],
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
        // 广告管理表格数据
        bannerList: [],
        platformPageList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'bannerId',
          isAsc: 'desc',
          bannerName: undefined,
          bannerMark: undefined,
          bannerImage: undefined,
          bannerRank: undefined,
          status: undefined,
          bannerType: this.bannerType,
          pagePath: undefined,
          toType: undefined,
          appId: undefined,
          url: undefined,
          startTime: undefined,
          endTime: undefined,
          showDimension: undefined,
          showCity: undefined,
          platformKey: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          bannerId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          bannerName: [{
            required: true,
            message: "名称不能为空",
            trigger: "blur"
          }],
          bannerImage: [{
            required: this.bannerType !== '10',
            message: "banner图不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          pagePath: [{
            required: true,
            message: "显示页面",
            trigger: "blur"
          }],
          appId: [{
            required: true,
            message: "小程序ID不能为空",
            trigger: "blur"
          }],
          url: [{
            required: true,
            message: "页面地址不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          weekDate: [{
            required: true,
            message: "请选择周几显示",
            trigger: "blur"
          }],
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
      this.getPageSelectList();
      this.getPlatformSelectList();
      this.getCitySelectList();
    },
    methods: {
      /** 查询广告管理列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.startTime && '' != this.startTime) {
          this.queryParams.params["beginStartTime"] = this.startTime[0];
          this.queryParams.params["endStartTime"] = this.startTime[1];
        }
        if (null != this.endTime && '' != this.endTime) {
          this.queryParams.params["beginEndTime"] = this.endTime[0];
          this.queryParams.params["endEndTime"] = this.endTime[1];
        }
        listBanner(this.queryParams).then(response => {
          this.bannerList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      //city下拉列表
      getCitySelectList() {
        this.cityForm.level = 'city';
        selectCityList(this.cityForm).then(response => {
          this.cityList = response.data;
        })
      },
      handleNodeClick(data) {
        this.cityNodeAll = false;
      },
      // 全选/不全选
      selectAll(val) {
        if (this.cityNodeAll) {
          // 	设置目前勾选的节点，使用此方法必须设置 node-key 属性
          this.$refs.city.setCheckedNodes(this.cityOptions);
        } else {
          // 全部不选中
          this.$refs.city.setCheckedNodes([])
        }
      },
      //page下拉列表
      getPageSelectList() {
        selectListPage({}).then(response => {
          this.pageList = response.data;
        })
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
      pageFormatter(row) {
        let name = '';
        this.pageList.forEach(item => {
          if (item.rightLabel == row.pagePath) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.pageName = name;
          return name;
        }
        return row.pagePath;
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
      //所有菜单节点数据
      getCityAllCheckedKeys() {
        //目前被选中的城市节点
        let checkedKeys = this.$refs.city.getCheckedKeys();
        //半选中的城市节点
        let halfCheckedKeys = this.$refs.city.getHalfCheckedKeys();
        checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
        return checkedKeys;
      },
      getCityTreeselect() {
        cityTreeselect().then(response => {
          this.cityOptions = response.data;
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
          bannerId: undefined,
          bannerName: undefined,
          bannerMark: undefined,
          bannerImage: undefined,
          bannerRank: undefined,
          status: '0',
          bannerType: this.bannerType,
          pagePath: undefined,
          toType: undefined,
          appId: undefined,
          url: undefined,
          startTime: undefined,
          endTime: undefined,
          showDimension: '0',
          assignDate: '0',
          weekDate: '',
          showCity: undefined,
          supportChannel: [],
          platformKey: undefined,
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
        this.startTime = null;
        this.endTime = null;
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.bannerId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加广告管理";
        this.cityNodeAll = true;
        this.getCityTreeselect();
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const bannerId = row.bannerId || this.ids
        const showCity = this.getShowCityTreeselect(bannerId);
        getBanner(bannerId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改广告管理";
          this.cityNodeAll = false;
          if (this.form && this.form.weekDate) {
            this.form.weekDate = this.form.weekDate.split(",");
          }
          if (response.data.supportChannel) {
            this.form.supportChannel = response.data.supportChannel.split(",")
          } else {
            this.form.supportChannel = []
          }
          this.getPlatformPageSelectList()
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
      getShowCityTreeselect(bannerId) {
        return bannerShowCityTreeSelect(bannerId).then(response => {
          this.cityOptions = response.data.citys;
          return response;
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.cityNodeAll) {
              this.form.showCity = "ALL";
            } else {
              this.form.showCity = this.getCityAllCheckedKeys().toString();
            }
            if (this.form.weekDate) {
              this.form.weekDate = this.form.weekDate.join(',');
            }
            if (this.form.supportChannel) {
              this.form.supportChannel = this.form.supportChannel.join(",")
            }
            if (this.form.bannerId != null) {
              updateBanner(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addBanner(this.form).then(response => {
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
        const bannerIds = row.bannerId || this.ids;
        this.$modal.confirm('是否确认删除广告管理编号为"' + bannerIds + '"的数据项？').then(() => {
          this.loading = true;
          return delBanner(bannerIds);
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
        this.download('zlyyh-admin/banner/export', {
          ...this.queryParams
        }, `banner_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
