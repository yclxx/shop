<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" clearable filterable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="页面标识" prop="pagePath">
        <el-input v-model="queryParams.pagePath" placeholder="请输入页面标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="页面标题" prop="pageName">
        <el-input v-model="queryParams.pageName" placeholder="请输入页面标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_page_status" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:page:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:page:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:page:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:page:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pageList">
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="页面标识" align="center" prop="pagePath" />
      <el-table-column label="所属页面" align="center" prop="pageRemake" />
      <el-table-column label="页面标题" align="center" prop="pageName" />
      <el-table-column label="导航栏背景色及透明度" align="center" prop="navBackgroundColor" />
      <el-table-column label="导航栏主题颜色" align="center" prop="appletStyle">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_page_applet_style" :value="scope.row.appletStyle" />
        </template>
      </el-table-column>
      <el-table-column label="标题栏是否显示。" align="center" prop="appletTitleBarVisible">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_page_applet_title_bar_visible" :value="scope.row.appletTitleBarVisible" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_page_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-picture-outline-round" @click="handleLook(scope.row)"
            v-hasPermi="['zlyyh:page:look']">查看页面配置</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:page:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:page:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改页面对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="所属平台" prop="platformKey">
          <el-select v-model="form.platformKey" style="width: 100%;">
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="页面标识" prop="pagePath">
          <el-input v-model="form.pagePath" placeholder="请输入页面标识" />
        </el-form-item>
        <el-form-item label="所属页面" prop="pageRemake">
          <el-input v-model="form.pageRemake" placeholder="请输入所属页面" />
        </el-form-item>
        <el-form-item label="页面标题" prop="pageName">
          <el-input v-model="form.pageName" placeholder="请输入页面标题" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_page_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题栏是否显示" prop="appletTitleBarVisible">
          <el-select v-model="form.appletTitleBarVisible" placeholder="请选择标题栏是否显示">
            <el-option v-for="dict in dict.type.t_page_applet_title_bar_visible" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="导航栏背景色" prop="navBackgroundColor">
          <el-color-picker v-model="form.navBackgroundColor"></el-color-picker>
        </el-form-item>
        <el-form-item label="导航栏主题颜色" prop="appletStyle">
          <el-select v-model="form.appletStyle" placeholder="请选择导航栏主题颜色">
            <el-option v-for="dict in dict.type.t_page_applet_style" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改页面配置对话框 -->
    <el-dialog :title="settingTitle" :visible.sync="settingOpen" width="1000px" append-to-body>
      <el-form ref="settingForm" :model="settingForm" :rules="settingRules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台标识" prop="platformKey">
              <el-select v-model="settingForm.platformKey" style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支持功能" prop="bannerType">
              <el-select v-model="settingForm.bannerType" placeholder="请选择支持功能" style="width: 100%;">
                <el-option v-for="dict in dict.type.t_page_setting_banner_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="settingForm.bannerType == '99'" label="模板名称" prop="blockId">
              <el-select v-model="settingForm.blockId" @change="changeColumn" style="width: 100%;">
                <el-option v-for="item in pageBlockList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-for="item in pageBlockColumnList" :span="12">
            <el-form-item :label="item.columnName" :prop="item.javaField" :rules="getRules(item)">
              <image-upload v-if="item.htmlType == '7'" v-model="item.value" :limit="1" />
              <el-select v-else-if="item.htmlType == '3'" v-model="item.value" :placeholder='"请选择"+item.columnName'>
                <el-option v-for="dict in dictSys[item.dictType]" :key="dict.dictValue" :label="dict.dictLabel"
                  :value="dict.dictValue"></el-option>
              </el-select>
              <el-radio-group v-else-if="item.htmlType == '5'" v-model="item.value">
                <el-radio v-for="dict in dictSys[item.dictType]" :key="dict.dictValue" :label="dict.dictValue">
                  {{dict.dictLabel}}
                </el-radio>
              </el-radio-group>
              <el-input v-else v-model="item.value" :placeholder='"请输入"+item.columnName'>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="settingForm.status" placeholder="请选择状态">
                <el-option v-for="dict in dict.type.t_page_setting_status" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="settingForm.sort" placeholder="请输入排序，从小到大" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitSettingForm">确 定</el-button>
        <el-button @click="settingCancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 页面配置 -->
    <el-dialog :title="settingDateTitle" :visible.sync="settingDataOpen" width="1000px" append-to-body>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleSettingAdd">新增</el-button>
        </el-col>
      </el-row>
      <el-table v-loading="settingLoading" :data="pageSettingList">
        <el-table-column label="平台" align="center" prop="platformName" :formatter="changePlatform" />
        <el-table-column label="支持功能" align="center" prop="bannerType">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_page_setting_banner_type" :value="scope.row.bannerType" />
          </template>
        </el-table-column>
        <el-table-column label="模板名称" align="center" prop="blockName" :formatter="changeBlock" />
        <el-table-column label="排序" align="center" prop="sort" />
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.t_page_setting_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleSettingUpdate(scope.row)"
              v-hasPermi="['zlyyh:pageSetting:edit']">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleSettingDelete(scope.row)"
              v-hasPermi="['zlyyh:pageSetting:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="settingTotal>0" :total="settingTotal" :page.sync="settingQueryParams.pageNum"
        :limit.sync="settingQueryParams.pageSize" @pagination="getSettingList" />
    </el-dialog>
  </div>
</template>

<script>
  import {
    listPage,
    getPage,
    delPage,
    addPage,
    updatePage
  } from "@/api/zlyyh/page";
  import {
    listPageSetting,
    getPageSetting,
    delPageSetting,
    addPageSetting,
    updatePageSetting
  } from "@/api/zlyyh/pageSetting";

  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  import {
    deepClone
  } from "@/utils/index"

  import {
    selectListPageBlock
  } from "@/api/zlyyh/pageBlock"

  import {
    getPageBlockColumnByBlockId
  } from "@/api/zlyyh/pageBlockColumn"

  import {
    getDicts
  } from "@/api/system/dict/data"

  export default {
    name: "Page",
    dicts: ['t_page_status', 't_page_applet_title_bar_visible', 't_page_applet_style', 't_page_setting_banner_type',
      't_page_setting_status'
    ],
    data() {
      return {
        dictSys: {},
        pageSettingList: [],
        settingLoading: false,
        settingTotal: 0,
        settingQueryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'sort',
          isAsc: 'asc'
        },
        settingDataOpen: false,
        settingDateTitle: "",
        settingOpen: false,
        settingTitle: "",
        settingForm: {},
        pagePath: '',
        platformKey: undefined,
        updateIndex: -1,
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
        // 页面表格数据
        pageList: [],
        platformList: [],
        pageBlockList: [],
        pageBlockColumnList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          pagePath: undefined,
          pageName: undefined,
          pageRemake: undefined,
          navBackgroundColor: undefined,
          appletStyle: undefined,
          appletTitleBarVisible: undefined,
          status: undefined,
          platformKey: undefined,
          orderByColumn: 'create_time',
          isAsc: 'desc'
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
          pagePath: [{
            required: true,
            message: "页面标识不能为空",
            trigger: "blur"
          }],
          pageRemake: [{
            required: true,
            message: "页面标题不能为空",
            trigger: "blur"
          }],
          appletStyle: [{
            required: true,
            message: "导航栏主题颜色不能为空",
            trigger: "change"
          }],
          appletTitleBarVisible: [{
            required: true,
            message: "标题栏是否显示不能为空",
            trigger: "change"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }],
        },
        settingRules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          bannerType: [{
            required: true,
            message: "支持功能不能为空",
            trigger: "change"
          }],
          blockId: [{
            required: true,
            message: "模版名称不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }]
        }
      };
    },
    created() {
      this.getList();
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
      selectListPageBlock({}).then(response => {
        this.pageBlockList = response.data;
      })
    },
    methods: {
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
      changeBlock(row) {
        let blockName = ''
        this.pageBlockList.forEach(item => {
          if (row.blockId == item.id) {
            blockName = item.label;
          }
        })
        if (blockName && blockName.length > 0) {
          row.blockName = blockName;
          return blockName;
        }
        return row.blockId;
      },
      getRules(item) {
        if (item.isRequired && item.isRequired == '1') {
          this.$set(this.settingForm, item.javaField, item.value)
          return [{
            required: true,
            message: '请输入' + item.columnName,
            trigger: "blur"
          }]
        }
      },
      changeColumn() {
        this.blockId = this.settingForm.blockId;
        this.getPageBlockColumnByBlockId();
      },
      handleLook(row) {
        this.pagePath = row.pagePath;
        this.platformKey = row.platformKey;
        this.getSettingList()
      },
      getSettingList() {
        this.settingLoading = true;
        this.settingQueryParams.pagePath = this.pagePath;
        this.settingQueryParams.platformKey = this.platformKey;
        listPageSetting(this.settingQueryParams).then(response => {
          this.pageSettingList = response.rows;
          this.settingTotal = response.total;
          this.settingLoading = false;
          this.settingDataOpen = true;
          this.settingDateTitle = "查看页面配置";
        });
      },
      handleSettingAdd() {
        this.settingReset();
        this.settingOpen = true;
        this.settingForm.pagePath = this.pagePath;
        this.settingTitle = "新增页面配置";
        this.pageBlockColumnList = [];
      },
      handleSettingUpdate(row) {
        this.settingReset();
        this.pageBlockColumnList = [];
        const id = row.id || this.ids
        getPageSetting(id).then(response => {
          this.settingForm = response.data;
          this.settingOpen = true;
          this.settingTitle = "修改页面配置";
          if (this.settingForm && this.settingForm.blockId) {
            this.blockId = this.settingForm.blockId;
            this.getPageBlockColumnByBlockId();
          }
        });
      },
      getPageBlockColumnByBlockId() {
        getPageBlockColumnByBlockId(this.blockId).then(res => {
          this.pageBlockColumnList = res.data;
          this.pageBlockColumnList.forEach(item => {
            if (item.dictType) {
              //查询传过来的字典字段 请求字段查询接口
              getDicts(item.dictType).then(res => {
                this.$set(this.dictSys, item.dictType, res.data)
                //console.log(this.dictSys)
              })

            }
            if (this.settingForm && this.settingForm.blockColumnValue) {
              var json = JSON.parse(this.settingForm.blockColumnValue);
              for (var key in json) {
                if (item.javaField == key) {
                  this.$set(item, 'value', json[key])
                }
              }
            } else {
              this.$set(item, 'value', '')
            }
          })
        })
      },
      /** 删除按钮操作 */
      handleSettingDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除页面配置编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delPageSetting(ids);
        }).then(() => {
          this.loading = false;
          this.getSettingList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      settingReset() {
        this.settingForm = {
          id: undefined,
          pagePath: undefined,
          bannerType: undefined,
          blockId: undefined,
          blockColumnValue: undefined,
          status: "0",
          sort: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          platformKey: this.platformKey,
          pageId: undefined
        };
        this.resetForm("settingForm");
      },
      settingCancel() {
        this.settingOpen = false;
        this.settingReset();
      },
      submitSettingForm() {
        var blockColumnValue = "{";
        this.pageBlockColumnList.forEach(item => {
          blockColumnValue += "\"" + item.javaField + "\":\"" + item.value + "\",";
        })
        blockColumnValue = blockColumnValue.substring(0, blockColumnValue.length - 1) + "}";
        this.settingForm.blockColumnValue = blockColumnValue;
        this.$refs["settingForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.settingForm.id != null) {
              updatePageSetting(this.settingForm).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.settingOpen = false;
                this.getSettingList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addPageSetting(this.settingForm).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.settingOpen = false;
                this.getSettingList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            }
          }
        });
      },
      /** 查询页面列表 */
      getList() {
        this.loading = true;
        listPage(this.queryParams).then(response => {
          this.pageList = response.rows;
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
          pagePath: undefined,
          pageName: undefined,
          navBackgroundColor: undefined,
          appletStyle: "black",
          appletTitleBarVisible: "1",
          status: "0",
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
        this.title = "添加页面";
        this.pageSettingList = [];
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getPage(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改页面";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updatePage(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addPage(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除页面编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delPage(ids);
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
        this.download('zlyyh-admin/page/export', {
          ...this.queryParams
        }, `page_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>