<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="城市" prop="adcode">
        <el-select v-model="queryParams.adcode" placeholder="请选择展示城市" clearable>
          <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label" :value="item.rightLabel" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="跳转类型" prop="toType">
        <el-select v-model="queryParams.toType" placeholder="请选择跳转类型" clearable>
          <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:platformCityIndex:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:platformCityIndex:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="platformCityIndexList" @selection-change="handleSelectionChange">
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="城市" width="88" align="center" prop="adcode" :formatter="cityFormatter" />
      <el-table-column label="状态" width="60" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="跳转类型" width="120" align="center" prop="toType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_to_type" :value="scope.row.toType" />
        </template>
      </el-table-column>
      <el-table-column label="小程序ID" width="150" align="center" prop="appId" />
      <el-table-column label="页面地址" align="center" prop="url" :show-overflow-tooltip="true" />
      <el-table-column label="生效时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:platformCityIndex:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:platformCityIndex:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改自定义首页对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台" prop="platformKey">
              <el-select style="width: 100%;" v-model="form.platformKey" placeholder="请选择平台" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市" prop="adcode">
              <el-select v-model="form.adcode" style="width: 100%;" placeholder="请选择城市" clearable>
                <el-option v-for="item in cityList" :key="item.rightLabel" :label="item.label"
                  :value="item.rightLabel" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生效时间" prop="startTime">
              <el-date-picker clearable style="width: 100%;" v-model="form.startTime" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效时间" prop="endTime">
              <el-date-picker clearable style="width: 100%;" v-model="form.endTime" default-time="23:59:59"
                type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择失效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跳转类型" prop="toType">
              <el-select v-model="form.toType" style="width: 100%;" placeholder="请选择跳转类型" clearable>
                <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.toType == 3">
            <el-form-item label="小程序ID" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入小程序ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.toType != '0' && form.toType != '4' && form.toType != '6'">
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
          <el-col :span="12">
            <el-form-item label="指定周几" prop="assignDate">
              <el-radio-group v-model="form.assignDate">
                <el-radio v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="周几显示" prop="weekDate" v-if="form.assignDate == '1'">
              <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
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
    listPlatformCityIndex,
    getPlatformCityIndex,
    delPlatformCityIndex,
    addPlatformCityIndex,
    updatePlatformCityIndex
  } from "@/api/zlyyh/platformCityIndex";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    treeselect as cityTreeselect,
    bannerShowCityTreeSelect,
    selectCityList
  } from "@/api/zlyyh/area"

  export default {
    name: "PlatformCityIndex",
    dicts: ['t_grad_period_date_list', 't_product_assign_date', 'sys_normal_disable', 't_banner_to_type'],
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
        // 自定义首页表格数据
        platformCityIndexList: [],
        //平台下拉列表
        platformList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          adcode: undefined,
          status: undefined,
          toType: undefined,
          platformKey: undefined,
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
          adcode: [{
            required: true,
            message: "行政编码不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          toType: [{
            required: true,
            message: "跳转类型不能为空",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          assignDate: [{
            required: true,
            message: "指定周几不能为空",
            trigger: "change"
          }],
          weekDate: [{
            required: true,
            message: "周几能领不能为空",
            trigger: "change"
          }],
          appId: [{
            required: true,
            message: "小程序ID不能为空",
            trigger: "change"
          }],
        },
        defaultProps: {
          children: "children",
          label: "label"
        },
        //城市列表
        cityList: [],
      };
    },
    created() {
      this.getList();
      this.getPlatformSelectList();
      this.getCitySelectList();
    },
    methods: {
      cityFormatter(row) {
        let name = '';
        this.cityList.forEach(item => {
          if (item.rightLabel == row.adcode) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          return name;
        }
        return row.adcode;
      },
      //city下拉列表
      getCitySelectList() {
        selectCityList({
          level: 'city'
        }).then(response => {
          this.cityList = response.data;
        })
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
      /** 查询自定义首页列表 */
      getList() {
        this.loading = true;
        listPlatformCityIndex(this.queryParams).then(response => {
          this.platformCityIndexList = response.rows;
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
          adcode: undefined,
          status: "0",
          toType: undefined,
          appId: undefined,
          url: undefined,
          startTime: undefined,
          endTime: undefined,
          platformKey: undefined,
          assignDate: "0",
          weekDate: undefined,
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
        this.title = "添加自定义首页";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getPlatformCityIndex(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改自定义首页";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updatePlatformCityIndex(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addPlatformCityIndex(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除自定义首页编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delPlatformCityIndex(ids);
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
        this.download('zlyyh-admin/platformCityIndex/export', {
          ...this.queryParams
        }, `platformCityIndex_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>