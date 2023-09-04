<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="文件地址" prop="fileUrl">
        <el-input
          v-model="queryParams.fileUrl"
          placeholder="请输入文件地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_order_download_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable v-model="createTime" size="small" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zlyyh:orderDownloadLog:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['zlyyh:orderDownloadLog:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zlyyh:orderDownloadLog:remove']"
        >删除</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:orderDownloadLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderDownloadLogList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="id" align="center" prop="torderDownloadId" v-if="true" />
      <el-table-column label="文件" align="center">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" :disabled="!(scope.row.fileUrl && scope.row.fileUrl.length>0)">
            <el-link :disabled="false" v-if="scope.row.fileUrl && scope.row.fileUrl.length>0" type="info"
              style="font-size:12px;color: #fff;" :href="scope.row.fileUrl">
              下载文件</el-link>
            <span v-else>未上传</span>
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_download_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="失败原因" align="center" prop="failReason" />
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:orderDownloadLog:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:orderDownloadLog:remove']"
          >删除</el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改订单下载记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="文件地址" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入文件地址" />
        </el-form-item>
        <el-form-item label="状态：0：未导出   1：导出中   2：导出成功   3：导出失败" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态：0：未导出   1：导出中   2：导出成功   3：导出失败">
            <el-option v-for="dict in dict.type.t_order_download_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
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
  import {
    listOrderDownloadLog,
    getOrderDownloadLog,
    delOrderDownloadLog,
    addOrderDownloadLog,
    updateOrderDownloadLog
  } from "@/api/zlyyh/orderDownloadLog";

  export default {
    name: "OrderDownloadLog",
    dicts: ['t_order_download_status'],
    data() {
      return {
        //创建时间范围
        createTime: [],
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
        // 订单下载记录表格数据
        orderDownloadLogList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 't_order_download_id',
          isAsc: 'desc',
          fileUrl: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          tOrderDownloadId: [{
            required: true,
            message: "id不能为空",
            trigger: "blur"
          }],
          fileUrl: [{
            required: true,
            message: "文件地址不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态：0：未导出   1：导出中   2：导出成功   3：导出失败不能为空",
            trigger: "change"
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
    },
    methods: {
      /** 查询订单下载记录列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.createTime && '' != this.createTime) {
          this.queryParams.params["beginCreateTime"] = this.createTime[0];
          this.queryParams.params["endCreateTime"] = this.createTime[1];
        }
        listOrderDownloadLog(this.queryParams).then(response => {
          this.orderDownloadLogList = response.rows;
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
          tOrderDownloadId: undefined,
          fileUrl: undefined,
          status: undefined,
          createTime: undefined,
          updateTime: undefined,
          createBy: undefined,
          updateBy: undefined
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
        this.createTime = null;
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.tOrderDownloadId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加订单下载记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const tOrderDownloadId = row.tOrderDownloadId || this.ids
        getOrderDownloadLog(tOrderDownloadId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改订单下载记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.tOrderDownloadId != null) {
              updateOrderDownloadLog(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addOrderDownloadLog(this.form).then(response => {
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
        const tOrderDownloadIds = row.tOrderDownloadId || this.ids;
        this.$modal.confirm('是否确认删除订单下载记录编号为"' + tOrderDownloadIds + '"的数据项？').then(() => {
          this.loading = true;
          return delOrderDownloadLog(tOrderDownloadIds);
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
        this.download('zlyyh-admin/orderDownloadLog/export', {
          ...this.queryParams
        }, `orderDownloadLog_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>