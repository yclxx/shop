<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="用户人数" prop="userPeople">
        <el-input
          v-model="queryParams.userPeople"
          placeholder="请输入用户人数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="记录日期" prop="recordDate">
        <el-date-picker clearable
                        v-model="queryParams.recordDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择记录日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="来源" prop="source">
        <el-select v-model="queryParams.source" placeholder="请选择来源" clearable>
          <el-option
            v-for="dict in dict.type.source_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:recordLog:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="客户端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type" :value="scope.row.supportChannel"/>
        </template>
      </el-table-column>
      <el-table-column label="用户点击次数" align="center" prop="userNumber"/>
      <el-table-column label="用户人数" align="center" prop="userPeople"/>
      <el-table-column label="订单购买次数" align="center" prop="orderBuyNumber"/>
      <el-table-column label="用户购买人数" align="center" prop="orderBuyUser"/>
      <el-table-column label="记录日期" align="center" prop="recordDate"/>
      <el-table-column label="来源" align="center" prop="source">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.source_type" :value="scope.row.source"/>
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

    <!-- 添加或修改记录日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="平台标识" prop="platformKey">
          <el-input v-model="form.platformKey" placeholder="请输入平台标识"/>
        </el-form-item>
        <el-form-item label="用户点击次数" prop="userNumber">
          <el-input v-model="form.userNumber" placeholder="请输入用户点击次数"/>
        </el-form-item>
        <el-form-item label="用户人数" prop="userPeople">
          <el-input v-model="form.userPeople" placeholder="请输入用户人数"/>
        </el-form-item>
        <el-form-item label="订单购买次数" prop="orderBuyNumber">
          <el-input v-model="form.orderBuyNumber" placeholder="请输入订单购买次数"/>
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker clearable
                          v-model="form.recordDate"
                          type="datetime"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择记录日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-select v-model="form.source" placeholder="请选择来源">
            <el-option
              v-for="dict in dict.type.source_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
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
import {listRecordLog, getRecordLog, delRecordLog, addRecordLog, updateRecordLog} from "@/api/zlyyh/recordLog";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "RecordLog",
  dicts: ['source_type', 'channel_type'],
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
      // 记录日志表格数据
      recordLogList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 平台下拉列表
      platformList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        userNumber: undefined,
        userPeople: undefined,
        orderBuyNumber: undefined,
        recordDate: undefined,
        source: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        recordId: [
          {required: true, message: "不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
  },
  methods: {
    /** 查询记录日志列表 */
    getList() {
      this.loading = true;
      listRecordLog(this.queryParams).then(response => {
        this.recordLogList = response.rows;
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
        recordId: undefined,
        platformKey: undefined,
        userNumber: undefined,
        userPeople: undefined,
        orderBuyNumber: undefined,
        recordDate: undefined,
        source: undefined,
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
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加记录日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const recordId = row.recordId || this.ids
      getRecordLog(recordId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改记录日志";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.recordId != null) {
            updateRecordLog(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addRecordLog(this.form).then(response => {
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
      const recordIds = row.recordId || this.ids;
      this.$modal.confirm('是否确认删除记录日志编号为"' + recordIds + '"的数据项？').then(() => {
        this.loading = true;
        return delRecordLog(recordIds);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh-admin/recordLog/export', {
        ...this.queryParams
      }, `recordLog_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
