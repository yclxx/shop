<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable filterable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="行政区名称" prop="adcode" label-width="120px">
        <el-select v-model="queryParams.adcode" placeholder="请选择行政区名称" clearable filterable>
          <el-option v-for="item in areaList" :key="item.label" :label="item.rightLabel" :value="item.label">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="支付商户" prop="merchantId">
        <el-select v-model="queryParams.merchantId" clearable filterable placeholder="请选择支付商户">
          <el-option v-for="item in merchantList" :key="item.id" :label="item.rightLabel" :value="item.id">
          </el-option>
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
          v-hasPermi="['zlyyh:cityMerchant:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:cityMerchant:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:cityMerchant:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:cityMerchant:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cityMerchantList">
      <el-table-column label="平台" align="center" prop="platformName" :formatter="changePlatform" />
      <el-table-column label="行政编码" align="center" prop="adcode" />
      <el-table-column label="行政区名称" align="center" prop="areaName" />
      <el-table-column label="商户名称" align="center" prop="merchantName" :formatter="changeMerchant" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:cityMerchant:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:cityMerchant:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加城市商户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台" clearable filterable>
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支付商户号" prop="merchantId">
          <el-select v-model="form.merchantId" placeholder="请选择支付商户号" clearable filterable>
            <el-option v-for="item in merchantList" :key="item.id" :label="item.rightLabel" :value="item.id">
              <span style="float: left">{{ item.rightLabel }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px" v-if="item.label && item.label.length>0">
                {{ item.label }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="行政区名称" prop="adcode">
          <el-select v-model="form.adcode" placeholder="请选择行政区名称" clearable filterable multiple>
            <el-option v-for="item in areaList" :key="item.label" :label="item.rightLabel" :value="item.label">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 修改城市商户对话框 -->
    <el-dialog :title="updateTitle" :visible.sync="updateOpen" width="500px" append-to-body>
      <el-form ref="updateForm" :model="updateForm" :rules="updateRules" label-width="110px">
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="updateForm.platformKey" placeholder="请选择平台" clearable filterable>
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支付商户号" prop="merchantId">
          <el-select v-model="updateForm.merchantId" placeholder="请选择支付商户号" clearable filterable>
            <el-option v-for="item in merchantList" :key="item.id" :label="item.rightLabel" :value="item.id">
              <span style="float: left">{{ item.rightLabel }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px" v-if="item.label && item.label.length>0">
                {{ item.label }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="行政区名称" prop="adcode">
          <el-select v-model="updateForm.adcode" placeholder="请选择行政区名称" clearable filterable>
            <el-option v-for="item in areaList" :key="item.label" :label="item.rightLabel" :value="item.label">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitUpdateForm">确 定</el-button>
        <el-button @click="updateCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listCityMerchant,
    getCityMerchant,
    delCityMerchant,
    addCityMerchant,
    updateCityMerchant
  } from "@/api/zlyyh/cityMerchant";

  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  import {
    listSelectMerchant
  } from "@/api/zlyyh/merchant"

  import {
    listSelectArea
  } from "@/api/zlyyh/area"

  export default {
    name: "CityMerchant",
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
        // 城市商户表格数据
        cityMerchantList: [],
        platformList: [],
        merchantList: [],
        areaList: [],
        // 弹出层标题
        title: "",
        updateTitle: "",
        // 是否显示弹出层
        open: false,
        updateOpen: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          platformKey: undefined,
          adcode: undefined,
          areaName: undefined,
          merchantId: undefined,
          orderByColumn: 'id',
          isAsc: 'desc'
        },
        // 表单参数
        form: {},
        updateForm: {},
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          adcode: [{
            required: true,
            message: "行政编码不能为空",
            trigger: "blur"
          }],
          merchantId: [{
            required: true,
            message: "商户ID不能为空",
            trigger: "blur"
          }]
        },
        updateRules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          adcode: [{
            required: true,
            message: "行政编码不能为空",
            trigger: "blur"
          }],
          merchantId: [{
            required: true,
            message: "商户ID不能为空",
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
      listSelectMerchant({}).then(response => {
        this.merchantList = response.data;
      })
      let areaParams = {
        level: "city"
      }
      listSelectArea(areaParams).then(response => {
        this.areaList = response.data;
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
      changeMerchant(row) {
        let merchantName = ''
        this.merchantList.forEach(item => {
          if (row.merchantId == item.id) {
            merchantName = item.rightLabel;
          }
        })
        if (merchantName && merchantName.length > 0) {
          row.merchantName = merchantName;
          return merchantName;
        }
        return row.merchantId;
      },
      /** 查询城市商户列表 */
      getList() {
        this.loading = true;
        listCityMerchant(this.queryParams).then(response => {
          this.cityMerchantList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      updateCancel() {
        this.updateOpen = false;
        this.updateReset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: undefined,
          platformKey: undefined,
          adcode: undefined,
          areaName: undefined,
          merchantId: undefined
        };
        this.resetForm("form");
      },
      updateReset() {
        this.updateForm = {
          id: undefined,
          platformKey: undefined,
          adcode: undefined,
          areaName: undefined,
          merchantId: undefined
        };
        this.resetForm("updateForm");
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
        this.title = "添加城市商户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.updateReset();
        const id = row.id || this.ids
        getCityMerchant(id).then(response => {
          this.loading = false;
          this.updateForm = response.data;
          this.updateOpen = true;
          this.updateTitle = "修改城市商户";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            this.form.adcode = this.form.adcode.toString();
            if (this.form.id != null) {
              updateCityMerchant(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addCityMerchant(this.form).then(response => {
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
      submitUpdateForm() {
        this.$refs["updateForm"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            updateCityMerchant(this.updateForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.updateOpen = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除城市商户编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delCityMerchant(ids);
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
        this.download('zlyyh-admin/cityMerchant/export', {
          ...this.queryParams
        }, `cityMerchant_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
