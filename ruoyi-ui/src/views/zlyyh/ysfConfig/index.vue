<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="queryParams.platformId" clearable filterable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="参数名称" prop="configName">
        <el-input
          v-model="queryParams.configName"
          placeholder="请输入参数名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input
          v-model="queryParams.configKey"
          placeholder="请输入参数键名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数键值" prop="configValue">
        <el-input
          v-model="queryParams.configValue"
          placeholder="请输入参数键值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否缓存" prop="isCache">
        <el-select v-model="queryParams.isCache" placeholder="请选择是否缓存" clearable>
          <el-option
            v-for="dict in dict.type.redis_cache_config"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="缓存时间" prop="cacheTime">
        <el-input
          v-model="queryParams.cacheTime"
          placeholder="请输入缓存时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['zlyyh:ysfConfig:add']"
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
          v-hasPermi="['zlyyh:ysfConfig:edit']"
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
          v-hasPermi="['zlyyh:ysfConfig:remove']"
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
          v-hasPermi="['zlyyh:ysfConfig:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ysfConfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="平台" align="center" prop="platformId" :formatter="changePlatform"/>
      <el-table-column label="参数名称" align="center" prop="configName"/>
      <el-table-column label="参数键名" align="center" prop="configKey"/>
      <el-table-column label="参数键值" align="center" prop="configValue"/>
      <el-table-column label="是否缓存" align="center" prop="isCache">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.redis_cache_config" :value="scope.row.isCache"/>
        </template>
      </el-table-column>
      <el-table-column label="缓存时间" align="center" prop="cacheTime"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:ysfConfig:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:ysfConfig:remove']"
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

    <!-- 添加或修改云闪付参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!--<el-form-item label="平台id" prop="platformId">-->
        <!--  <el-input v-model="form.platformId" placeholder="请输入平台id"/>-->
        <!--</el-form-item>-->
        <el-form-item label="平台标识" prop="platformId">
          <el-select v-model="form.platformId">
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称"/>
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名"/>
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="是否缓存" prop="isCache">
          <el-radio-group v-model="form.isCache">
            <el-radio
              v-for="dict in dict.type.redis_cache_config"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="缓存时间" prop="cacheTime">
          <el-input v-model="form.cacheTime" placeholder="请输入参数键名"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"/>
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
import {listYsfConfig, getYsfConfig, delYsfConfig, addYsfConfig, updateYsfConfig} from "@/api/zlyyh/ysfConfig";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "YsfConfig",
  dicts: ['redis_cache_config'],
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
      // 云闪付参数配置表格数据`
      ysfConfigList: [],
      platformList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformId: undefined,
        configName: undefined,
        configKey: undefined,
        configValue: undefined,
        isCache: undefined,
        cacheTime: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        configId: [
          {required: true, message: "参数主键不能为空", trigger: "blur"}
        ],
        platformId: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ],
        configName: [
          {required: true, message: "参数名称不能为空", trigger: "blur"}
        ],
        configKey: [
          {required: true, message: "参数键名不能为空", trigger: "blur"}
        ],
        configValue: [
          {required: true, message: "参数键值不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
    selectListPlatform({}).then(response => {
      this.platformList = response.data;
    });
  },
  methods: {
    changePlatform(row) {
      let platformName = ''
      this.platformList.forEach(item => {
        if (row.platformId === item.id) {
          platformName = item.label;
        }
      })
      if (platformName && platformName.length > 0) {
        row.platformName = platformName;
        return platformName;
      }
      return row.platformId;
    },
    /** 查询云闪付参数配置列表 */
    getList() {
      this.loading = true;
      listYsfConfig(this.queryParams).then(response => {
        this.ysfConfigList = response.rows;
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
        configId: undefined,
        platformId: undefined,
        configName: undefined,
        configKey: undefined,
        configValue: undefined,
        isCache: undefined,
        cacheTime: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        remark: undefined
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
      this.ids = selection.map(item => item.configId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加云闪付参数配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const configId = row.configId || this.ids
      getYsfConfig(configId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改云闪付参数配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.configId != null) {
            updateYsfConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addYsfConfig(this.form).then(response => {
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
      const configIds = row.configId || this.ids;
      this.$modal.confirm('是否确认删除云闪付参数配置编号为"' + configIds + '"的数据项？').then(() => {
        this.loading = true;
        return delYsfConfig(configIds);
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
      this.download('zlyyh-admin/ysfConfig/export', {
        ...this.queryParams
      }, `ysfConfig_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
