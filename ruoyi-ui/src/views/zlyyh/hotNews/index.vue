<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="显示名称" prop="newsName">
        <el-input
          v-model="queryParams.newsName"
          placeholder="请输入显示名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.t_banner_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="生效时间" prop="startTime">
        <el-date-picker clearable
                        v-model="queryParams.startTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择生效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间" prop="endTime">
        <el-date-picker clearable
                        v-model="queryParams.endTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择失效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="展示城市" prop="showCity">
        <el-select v-model="queryParams.showCity" placeholder="请选择展示城市" clearable>
          <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label" :value="item.rightLabel"/>
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
          v-hasPermi="['zlyyh:hotNews:add']"
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
          v-hasPermi="['zlyyh:hotNews:edit']"
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
          v-hasPermi="['zlyyh:hotNews:remove']"
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
          v-hasPermi="['zlyyh:hotNews:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="hotNewsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="newsId" v-if="true"/>
      <el-table-column label="显示名称" align="center" prop="newsName"/>
      <el-table-column label="排序" align="center" prop="newsRank"/>
      <el-table-column label="支持端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <div v-for="channel in dict.type.channel_type">
            <div v-for="(supportChannel,index) in scope.row.supportChannel.split(',')" :key="index">
              <span v-if="channel.value === supportChannel">{{ channel.label }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="平台" width="100" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="生效时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:hotNews:edit']">修改
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['zlyyh:hotNews:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 添加或修改热门搜索配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="显示名称" prop="newsName">
          <el-input v-model="form.newsName" placeholder="请输入显示名称"/>
        </el-form-item>
        <el-form-item label="排序" prop="newsRank">
          <el-input v-model="form.newsRank" placeholder="请输入排序，从小到大"/>
        </el-form-item>
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台标识" clearable>
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_banner_status" :key="dict.value" :label="dict.label"
                       :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生效时间" prop="startTime">
          <el-date-picker clearable v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择生效时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="失效时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          default-time="23:59:59"
                          placeholder="请选择失效时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="指定周几" prop="assignDate">
          <el-radio-group v-model="form.assignDate">
            <el-radio v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="显示周几" prop="weekDate" v-if="form.assignDate == '1'">
          <el-select v-model="form.weekDate" placeholder="请选择" style="width: 100%;" multiple clearable>
            <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                       :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支持端" prop="supportChannel">
          <el-checkbox-group v-model="form.supportChannel">
            <el-checkbox
              v-for="item in dict.type.channel_type" :key="item.value" :label="item.value">
              {{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="展示城市" prop="showCity">
          <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
          <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                   ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
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
import {listHotNews, getHotNews, delHotNews, addHotNews, updateHotNews} from "@/api/zlyyh/hotNews";
import {
  bannerShowCityTreeSelect,
  hotNewsShowCityTreeSelect,
  selectCityList,
  treeselect as cityTreeselect
} from "@/api/zlyyh/area";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "HotNews",
  dicts: ['t_banner_show_dimension', 't_banner_type', 't_banner_status', 't_banner_to_type', 't_product_assign_date',
    't_grad_period_date_list', 'channel_type'
  ],
  data() {
    return {
      //平台下拉列表
      platformList: [],
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
      // 热门搜索配置表格数据
      hotNewsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        newsName: undefined,
        platformKey: undefined,
        status: undefined,
        startTime: undefined,
        endTime: undefined,
        showCity: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        newsId: [
          {required: true, message: "ID不能为空", trigger: "blur"}
        ],
        newsName: [
          {required: true, message: "显示名称不能为空", trigger: "blur"}
        ],
        newsRank: [
          {required: true, message: "排序不能为空", trigger: "blur"}
        ],
        platformKey: [
          {required: true, message: "平台不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        startTime: [
          {required: true, message: "生效时间不能为空", trigger: "blur"}
        ],
        endTime: [
          {required: true, message: "失效时间不能为空", trigger: "blur"}
        ],
        assignDate: [
          {required: true, message: "指定星期不能为空", trigger: "blur"}
        ],
        supportChannel: [
          {required: true, message: "支持端不能为空", trigger: "blur"}
        ],
        weekDate: [
          {required: true, message: "展示星期不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
    this.getCitySelectList();
  },
  methods: {
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
    handleNodeClick(data) {
      this.cityNodeAll = false;
    },
    /** 查询热门搜索配置列表 */
    getList() {
      this.loading = true;
      listHotNews(this.queryParams).then(response => {
        this.hotNewsList = response.rows;
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
        newsId: undefined,
        newsName: undefined,
        newsRank: undefined,
        platformKey: undefined,
        status: undefined,
        startTime: undefined,
        endTime: undefined,
        assignDate: undefined,
        showCity: undefined,
        weekDate: undefined,
        supportChannel: [],
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
      this.ids = selection.map(item => item.newsId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.cityNodeAll = true;
      this.title = "添加热门搜索";
      this.getCityTreeselect();
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const newsId = row.newsId || this.ids
      const showCity = this.getShowCityTreeselect(newsId);
      getHotNews(newsId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改热门搜索";
        this.cityNodeAll = false;
        if (this.form && this.form.weekDate) {
          this.form.weekDate = this.form.weekDate.split(",");
        }
        if (this.form && this.form.supportChannel) {
          this.form.supportChannel = this.form.supportChannel.split(",");
        } else {
          this.form.supportChannel = [];
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
    getShowCityTreeselect(bannerId) {
      return hotNewsShowCityTreeSelect(bannerId).then(response => {
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
          if (this.form.supportChannel.length > 0) {
            this.form.supportChannel = this.form.supportChannel.join(",");
          }
          if (this.form.newsId != null) {
            updateHotNews(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addHotNews(this.form).then(response => {
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
      const newsIds = row.newsId || this.ids;
      this.$modal.confirm('是否确认删除热门搜索配置编号为"' + newsIds + '"的数据项？').then(() => {
        this.loading = true;
        return delHotNews(newsIds);
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
      this.download('zlyyh-admin/hotNews/export', {
        ...this.queryParams
      }, `hotNews_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
