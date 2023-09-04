<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="权益包ID" prop="equityId">
        <el-input v-model="queryParams.equityId" placeholder="请输入权益包ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="产品归属" prop="equityType">
        <el-select v-model="queryParams.equityType" placeholder="请选择产品归属" clearable>
          <el-option v-for="dict in dict.type.equity_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_equity_record_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="领取时间">
        <el-date-picker v-model="daterangeReceiveDate" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="订单号" prop="number">
        <el-input v-model="queryParams.number" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:equityRecord:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:equityRecord:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:equityRecord:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:equityRecord:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="equityRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" v-if="true" />
      <el-table-column label="权益包ID" align="center" prop="equityId" />
      <el-table-column label="商品ID" align="center" prop="productId" />
      <el-table-column label="商品名称" align="center" prop="productName" />
      <el-table-column label="商品图片" align="center" prop="productImg" />
      <el-table-column label="商品价值" align="center" prop="productAmount" />
      <el-table-column label="产品归属" align="center" prop="equityType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.equity_type" :value="scope.row.equityType" />
        </template>
      </el-table-column>
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_equity_record_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="领取开始时间" align="center" prop="receiveStartDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receiveStartDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="领取结束时间" align="center" prop="receiveEndDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receiveEndDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="领取时间" align="center" prop="receiveDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receiveDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单号" align="center" prop="number" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:equityRecord:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:equityRecord:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改领取记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="权益包ID" prop="equityId">
          <el-input v-model="form.equityId" placeholder="请输入权益包ID" />
        </el-form-item>
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品图片" prop="productImg">
          <el-input v-model="form.productImg" placeholder="请输入商品图片" />
        </el-form-item>
        <el-form-item label="商品价值" prop="productAmount">
          <el-input v-model="form.productAmount" placeholder="请输入商品价值" />
        </el-form-item>
        <el-form-item label="产品归属" prop="equityType">
          <el-select v-model="form.equityType" placeholder="请选择产品归属">
            <el-option v-for="dict in dict.type.equity_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.t_equity_record_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="领取开始时间" prop="receiveStartDate">
          <el-date-picker clearable v-model="form.receiveStartDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择领取开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="领取结束时间" prop="receiveEndDate">
          <el-date-picker clearable v-model="form.receiveEndDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择领取结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="领取时间" prop="receiveDate">
          <el-date-picker clearable v-model="form.receiveDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择领取时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="订单号" prop="number">
          <el-input v-model="form.number" placeholder="请输入订单号" />
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
    listEquityRecord,
    getEquityRecord,
    delEquityRecord,
    addEquityRecord,
    updateEquityRecord
  } from "@/api/zlyyh/equityRecord";

  export default {
    name: "EquityRecord",
    dicts: ['t_equity_record_status', 'equity_type'],
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
        // 领取记录表格数据
        equityRecordList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 订单号时间范围
        daterangeReceiveDate: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          equityId: undefined,
          productName: undefined,
          equityType: undefined,
          userId: undefined,
          status: undefined,
          receiveDate: undefined,
          number: undefined,
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
          equityId: [{
            required: true,
            message: "权益包ID不能为空",
            trigger: "blur"
          }],
          equityType: [{
            required: true,
            message: "产品归属不能为空",
            trigger: "change"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          receiveDate: [{
            required: true,
            message: "领取时间不能为空",
            trigger: "blur"
          }],
          number: [{
            required: true,
            message: "订单号不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询领取记录列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeReceiveDate && '' != this.daterangeReceiveDate) {
          this.queryParams.params["beginReceiveDate"] = this.daterangeReceiveDate[0];
          this.queryParams.params["endReceiveDate"] = this.daterangeReceiveDate[1];
        }
        listEquityRecord(this.queryParams).then(response => {
          this.equityRecordList = response.rows;
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
          equityId: undefined,
          productId: undefined,
          productName: undefined,
          productImg: undefined,
          productAmount: undefined,
          equityType: undefined,
          userId: undefined,
          status: undefined,
          receiveStartDate: undefined,
          receiveEndDate: undefined,
          receiveDate: undefined,
          number: undefined,
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
        this.daterangeReceiveDate = [];
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
        this.title = "添加领取记录";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getEquityRecord(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改领取记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateEquityRecord(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addEquityRecord(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除领取记录编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delEquityRecord(ids);
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
        this.download('zlyyh-admin/equityRecord/export', {
          ...this.queryParams
        }, `equityRecord_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>