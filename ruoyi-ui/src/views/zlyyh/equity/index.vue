<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="权益包名称" prop="equityName">
        <el-input v-model="queryParams.equityName" placeholder="请输入权益包名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
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
          v-hasPermi="['zlyyh:equity:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:equity:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:equity:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:equity:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="equityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="权益包ID" width="190" align="center" prop="equityId" v-if="true" />
      <el-table-column label="权益包名称" min-width="120" align="center" prop="equityName" />
      <el-table-column label="平台标识" min-width="100" align="center" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="售卖价格" align="center" prop="sellAmount" />
      <el-table-column label="展示开始时间" align="center" prop="showStartDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.showStartDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="展示结束时间" align="center" prop="showEndDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.showEndDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="售卖开始时间" align="center" prop="sellStartDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sellStartDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="售卖结束时间" align="center" prop="sellEndDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sellEndDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期" align="center" prop="expiryDate" />
      <el-table-column label="每日数量" align="center" prop="dayCount" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="规则图片" align="center" prop="equityImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.equityImg" :width="50" :height="50" />
        </template>
      </el-table-column>
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
      <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:equity:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:equity:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改权益包对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="权益包名称" prop="equityName">
              <el-input v-model="form.equityName" placeholder="请输入权益包名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售卖价格" prop="sellAmount">
              <el-input v-model="form.sellAmount" placeholder="请输入售卖价格" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展示开始时间" prop="showStartDate">
              <el-date-picker clearable style="width: 100%;" v-model="form.showStartDate" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展示结束时间" prop="showEndDate">
              <el-date-picker clearable style="width: 100%;" default-time="23:59:59" v-model="form.showEndDate"
                type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售卖开始时间" prop="sellStartDate">
              <el-date-picker clearable style="width: 100%;" v-model="form.sellStartDate" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择售卖开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售卖结束时间" prop="sellEndDate">
              <el-date-picker clearable style="width: 100%;" default-time="23:59:59" v-model="form.sellEndDate"
                type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择售卖结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购买后有效期" prop="expiryDate">
              <el-input v-model="form.expiryDate" placeholder="请输入购买后权益有效期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则图片" prop="equityImg">
              <image-upload :limit="1" v-model="form.equityImg" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每日售卖数量" prop="dayCount">
              <el-input v-model="form.dayCount" placeholder="请输入每日售卖数量,0为不限制" />
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
    listEquity,
    getEquity,
    delEquity,
    addEquity,
    updateEquity
  } from "@/api/zlyyh/equity";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "Equity",
    dicts: ['sys_normal_disable'],
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
        // 权益包表格数据
        equityList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 规则图片时间范围
        daterangeShowStartDate: [],
        // 规则图片时间范围
        daterangeShowEndDate: [],
        // 规则图片时间范围
        daterangeSellStartDate: [],
        // 规则图片时间范围
        daterangeSellEndDate: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          equityName: undefined,
          showStartDate: undefined,
          showEndDate: undefined,
          sellStartDate: undefined,
          sellEndDate: undefined,
          status: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          equityName: [{
            required: true,
            message: "权益包名称不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "权益包名称不能为空",
            trigger: "blur"
          }],
          sellAmount: [{
            required: true,
            message: "售卖价格不能为空",
            trigger: "blur"
          }],
          showStartDate: [{
            required: true,
            message: "展示开始时间不能为空",
            trigger: "blur"
          }],
          showEndDate: [{
            required: true,
            message: "展示结束时间不能为空",
            trigger: "blur"
          }],
          sellStartDate: [{
            required: true,
            message: "售卖开始时间不能为空",
            trigger: "blur"
          }],
          sellEndDate: [{
            required: true,
            message: "售卖结束时间不能为空",
            trigger: "blur"
          }],
          expiryDate: [{
            required: true,
            message: "购买后权益有效期不能为空",
            trigger: "blur"
          }],
        },
        platformList: [],
      };
    },
    created() {
      this.getList();
      selectListPlatform({}).then(res => {
        this.platformList = res.data;
      })
    },
    methods: {
      /** 查询权益包列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeShowStartDate && '' != this.daterangeShowStartDate) {
          this.queryParams.params["beginShowStartDate"] = this.daterangeShowStartDate[0];
          this.queryParams.params["endShowStartDate"] = this.daterangeShowStartDate[1];
        }
        if (null != this.daterangeShowEndDate && '' != this.daterangeShowEndDate) {
          this.queryParams.params["beginShowEndDate"] = this.daterangeShowEndDate[0];
          this.queryParams.params["endShowEndDate"] = this.daterangeShowEndDate[1];
        }
        if (null != this.daterangeSellStartDate && '' != this.daterangeSellStartDate) {
          this.queryParams.params["beginSellStartDate"] = this.daterangeSellStartDate[0];
          this.queryParams.params["endSellStartDate"] = this.daterangeSellStartDate[1];
        }
        if (null != this.daterangeSellEndDate && '' != this.daterangeSellEndDate) {
          this.queryParams.params["beginSellEndDate"] = this.daterangeSellEndDate[0];
          this.queryParams.params["endSellEndDate"] = this.daterangeSellEndDate[1];
        }
        listEquity(this.queryParams).then(response => {
          this.equityList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
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
      // 表单重置
      reset() {
        this.form = {
          equityId: undefined,
          equityName: undefined,
          sellAmount: undefined,
          showStartDate: undefined,
          showEndDate: undefined,
          platformKey: undefined,
          sellStartDate: undefined,
          sellEndDate: undefined,
          expiryDate: undefined,
          dayCount: undefined,
          status: '0',
          equityImg: undefined,
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
        this.daterangeShowStartDate = [];
        this.daterangeShowEndDate = [];
        this.daterangeSellStartDate = [];
        this.daterangeSellEndDate = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.equityId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加权益包";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const equityId = row.equityId || this.ids
        getEquity(equityId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改权益包";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.equityId != null) {
              updateEquity(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addEquity(this.form).then(response => {
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
        const equityIds = row.equityId || this.ids;
        this.$modal.confirm('是否确认删除权益包编号为"' + equityIds + '"的数据项？').then(() => {
          this.loading = true;
          return delEquity(equityIds);
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
        this.download('zlyyh-admin/equity/export', {
          ...this.queryParams
        }, `equity_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>