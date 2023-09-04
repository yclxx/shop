<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select v-model="queryParams.productId" clearable placeholder="请选择商品" style="width: 100%;">
          <el-option v-for="item in productList" :key="item.id" :value="item.id"
                     :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_banner_status" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="跳转类型" prop="toType">
        <el-select v-model="queryParams.toType" placeholder="请选择跳转类型" clearable>
          <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker clearable v-model="startTime" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker clearable v-model="endTime" size="small" :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
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
          v-hasPermi="['zlyyh:searchGroup:add']"
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
          v-hasPermi="['zlyyh:searchGroup:edit']"
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
          v-hasPermi="['zlyyh:searchGroup:remove']"
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
          v-hasPermi="['zlyyh:searchGroup:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="searchGroupList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="searchId" v-if="true"/>
      <el-table-column label="平台" width="100" align="center" prop="platformKey" :formatter="platformFormatter"/>

      <el-table-column label="搜索内容" align="center" prop="searchContent" width="180"/>
      <el-table-column label="商品" align="center" prop="productId" :formatter="changeProductName"/>
      <el-table-column label="跳转类型" width="100" align="center" prop="toType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_to_type" :value="scope.row.toType"/>
        </template>
      </el-table-column>
      <el-table-column label="小程序ID" align="center" prop="appId"/>
      <el-table-column label="页面地址" align="center" prop="url"/>
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" align="center" prop="status" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:searchGroup:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:searchGroup:remove']"
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

    <!-- 添加或修改搜索彩蛋配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="搜索内容">
          <el-input v-model="form.searchContent" placeholder="请输入搜索内容"/>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="商品" prop="productId" v-if="form.toType == 0">
              <el-select v-model="form.productId" placeholder="请选择商品" style="width: 100%;">
                <el-option v-for="item in productList" :key="item.id" :value="item.id"
                           :label="item.label"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker clearable v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择失效时间">
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
          <el-col :span="12" v-if="form.toType == 4">
            <el-form-item label="图片页面" prop="url">
              <image-upload :limit="1" v-model="form.url" />
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
  listSearchGroup,
  getSearchGroup,
  delSearchGroup,
  addSearchGroup,
  updateSearchGroup
} from "@/api/zlyyh/searchGroup";
import {
  selectListPlatform
} from "@/api/zlyyh/platform";
import {
  treeselect as cityTreeselect,
  searchShowCityTreeSelect,
  selectCityList
} from "@/api/zlyyh/area"
import {
  selectListProduct
} from "@/api/zlyyh/product"

export default {
  name: "SearchGroup",
  dicts: ['t_banner_status', 't_banner_to_type', 't_product_assign_date','t_grad_period_date_list'],
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
      cityForm: {},
      cityNodeAll: false,
      cityList: [],
      cityOptions: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 搜索彩蛋配置表格数据
      searchGroupList: [],
      platformList: [],
      productList: [],
      //生效时间范围
      startTime: [],
      //失效时间范围
      endTime: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        searchContent: undefined,
        productId: undefined,
        toType: undefined,
        appId: undefined,
        url: undefined,
        startTime: undefined,
        endTime: undefined,
        showCity: undefined,
        assignDate: undefined,
        weekDate: undefined,
        status: undefined,
        platformKey: undefined
      },
      // 表单参数
      form: {},
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
      // 表单校验
      rules: {
        searchId: [
          {required: true, message: "ID不能为空", trigger: "blur"}
        ],
        searchContent: [
          {required: true, message: "搜索内容不能为空", trigger: "blur"}
        ],

        toType: [
          {required: true, message: "跳转类型：0-无需跳转，1-内部页面，2-外部页面，3-小程序跳转，4-图片页面，5-RN跳转，6-页面指定位置不能为空", trigger: "change"}
        ],

        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        platformKey: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
    this.getCitySelectList();
    this.getProductList();

  },
  methods: {
    /** 查询搜索彩蛋配置列表 */
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
      listSearchGroup(this.queryParams).then(response => {
        this.searchGroupList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getProductList() {
      selectListProduct({
        status: '0',
        searchStatus: '1',
      }).then(res => {
        this.productList = res.data;
      })
    },
    changeProductName(row) {
      let productName = ''
      this.productList.forEach(item => {
        if (row.productId == item.id) {
          productName = item.label;
        }
      })
      if (productName && productName.length > 0) {
        row.productName = productName;
        return productName;
      }
      return row.productId;
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
    //city下拉列表
    getCitySelectList() {
      this.cityForm.level = 'city';
      selectCityList(this.cityForm).then(response => {
        this.cityList = response.data;
      })
    },
    getShowCityTreeselect(searchId) {
      return searchShowCityTreeSelect(searchId).then(response => {
        this.cityOptions = response.data.citys;
        return response;
      });
    },

    getCityTreeselect() {
      cityTreeselect().then(response => {
        this.cityOptions = response.data;
      });
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        searchId: undefined,
        searchContent: undefined,
        productId: undefined,
        toType: undefined,
        appId: undefined,
        url: undefined,
        startTime: undefined,
        endTime: undefined,
        showCity: undefined,
        assignDate: undefined,
        weekDate: undefined,
        status: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        platformKey: undefined
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
      this.ids = selection.map(item => item.searchId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加搜索彩蛋配置";
      this.cityNodeAll = true;
      this.getCityTreeselect();
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const searchId = row.searchId || this.ids
      const showCity = this.getShowCityTreeselect(searchId);
      getSearchGroup(searchId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改搜索彩蛋配置";
        this.cityNodeAll = false;
        if (this.form && this.form.weekDate) {
          this.form.weekDate = this.form.weekDate.split(",");
        }
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
          if (this.form.searchId != null) {
            updateSearchGroup(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addSearchGroup(this.form).then(response => {
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
    //所有菜单节点数据
    getCityAllCheckedKeys() {
      //目前被选中的城市节点
      let checkedKeys = this.$refs.city.getCheckedKeys();
      //半选中的城市节点
      let halfCheckedKeys = this.$refs.city.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const searchIds = row.searchId || this.ids;
      this.$modal.confirm('是否确认删除搜索彩蛋配置编号为"' + searchIds + '"的数据项？').then(() => {
        this.loading = true;
        return delSearchGroup(searchIds);
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
      this.download('zlyyh/searchGroup/export', {
        ...this.queryParams
      }, `searchGroup_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
