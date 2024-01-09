<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商户信息" prop="activityShopName">
        <el-input v-model="queryParams.activityShopName" placeholder="请输入商户名称/地址" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="省市县" prop="province">
        <el-input v-model="queryParams.province" placeholder="请输入省/市/县" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类别" prop="typeId">
        <el-select v-model="queryParams.typeId" placeholder="请选择类别" clearable>
          <el-option v-for="item in typeList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属文件" prop="fileId">
        <el-select v-model="queryParams.fileId" placeholder="请选择所属文件" clearable>
          <el-option v-for="item in fileList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
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
          v-hasPermi="['zlyyh:activityFileShop:add']">新增</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:activityFileShop:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:activityFileShop:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:activityFileShop:export']">导出</el-button>
      </el-col> -->
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleMerchantImport"
          v-hasPermi="['zlyyh:activityFileShop:merchantImport']">商户导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activityFileShopList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!-- <el-table-column label="ID" align="center" prop="activityShopId" v-if="true" /> -->
      <el-table-column label="商户名称" align="center" prop="activityShopName" />
      <el-table-column label="商户地址" align="center" prop="address" />
      <!-- <el-table-column label="结构化地址" align="center" prop="formattedAddress" /> -->
      <el-table-column label="省市区" align="left" prop="province">
        <template slot-scope="scope">
          <span>省：{{ scope.row.province}}</span><br>
          <span>市：{{scope.row.city }}</span><br>
          <span>县(区)：{{scope.row.district}}</span><br>
        </template>
      </el-table-column>
      <el-table-column label="编码" align="left" prop="procode" width="150">
        <template slot-scope="scope">
          <span>省份编码：{{ scope.row.procode}}</span><br>
          <span>城市编码：{{scope.row.citycode }}</span><br>
          <span>区域编码：{{scope.row.adcode}}</span><br>
        </template>
      </el-table-column>
      <!-- <el-table-column label="文件名称" align="center" prop="fileName" />
      <el-table-column label="文件唯一标识" align="center" prop="fileId" /> -->
      <el-table-column label="经纬度" align="left" prop="longitude" width="150">
        <template slot-scope="scope">
          <span>经度：{{ scope.row.longitude}}</span><br>
          <span>纬度：{{scope.row.latitude }}</span><br>
        </template>
      </el-table-column>
      <el-table-column label="类别" align="center" prop="typeId" :formatter="typeIdFormatter" />
      <el-table-column label="返回h5链接" align="center" prop="indexUrl" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:activityFileShop:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:activityFileShop:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改活动商户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商户名称" prop="activityShopName">
          <el-input v-model="form.activityShopName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="商户地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入商户地址" />
        </el-form-item>
        <el-form-item label="城市" prop="address">
          <span slot="label">
            城市
            <el-tooltip placement="top">
              <div slot="content">例：北京</div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="form.cityName" placeholder="请输入城市名称" />
        </el-form-item>
        <el-form-item label="类别" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择类别" clearable style="width: 100%;">
            <el-option v-for="item in typeList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属文件" prop="fileId">
          <el-select v-model="form.fileId" placeholder="请选择所属文件" clearable style="width: 100%;">
            <el-option v-for="item in fileList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="返回h5链接" prop="indexUrl">
          <el-input v-model="form.indexUrl" placeholder="请输入返回h5链接" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">
              {{dict.label}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序：从小到大" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 结算数据导入对话框 -->
    <el-dialog title="商户导入" :visible.sync="upload.open" width="500px" append-to-body>
      <el-form ref="importForm" :model="importForm" :rules="importRules" label-width="80px">
        <el-form-item label="页面标题" prop="pageTitle" style="width: 80%;">
          <el-input v-model="importForm.pageTitle" placeholder="请输入页面标题" clearable />
        </el-form-item>
      </el-form>
      <el-upload style="text-align: center;" ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url + '?pageTitle=' + this.importForm.pageTitle" :disabled="upload.isUploading"
        :on-progress="handleSettleFileUploadProgress" :on-success="handleSettleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
            @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitImportForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listActivityFileShop,
    getActivityFileShop,
    delActivityFileShop,
    addActivityFileShop,
    updateActivityFileShop
  } from "@/api/zlyyh/activityFileShop";
  import {
    selectFileList
  } from "@/api/zlyyh/fileImportLog";
  import {
    selectMerTypeList
  } from "@/api/zlyyh/merchantType";
  import {
    getToken
  } from "@/utils/auth";

  export default {
    name: "ActivityFileShop",
    dicts: ['sys_normal_disable'],
    data() {
      return {
        //类型下拉列表
        typeList: [],
        //导入文件下拉列表
        fileList: [],
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
        // 活动商户表格数据
        activityFileShopList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          activityShopName: undefined,
          address: undefined,
          status: undefined,
          formattedAddress: undefined,
          province: undefined,
          city: undefined,
          district: undefined,
          procode: undefined,
          citycode: undefined,
          adcode: undefined,
          fileName: undefined,
          fileId: undefined,
          indexUrl: undefined,
          longitude: undefined,
          latitude: undefined,
          sort: undefined
        },
        // 表单参数
        form: {},
        importForm: {},
        // 表单校验
        rules: {
          activityShopId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          activityShopName: [{
            required: true,
            message: "商户名称不能为空",
            trigger: "blur"
          }],
          address: [{
            required: true,
            message: "商户地址不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          formattedAddress: [{
            required: true,
            message: "结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码不能为空",
            trigger: "blur"
          }],
          province: [{
            required: true,
            message: "省份名不能为空",
            trigger: "blur"
          }],
          city: [{
            required: true,
            message: "城市名不能为空",
            trigger: "blur"
          }],
          district: [{
            required: true,
            message: "地址所在区不能为空",
            trigger: "blur"
          }],
          procode: [{
            required: true,
            message: "省份编码不能为空",
            trigger: "blur"
          }],
          citycode: [{
            required: true,
            message: "城市编码不能为空",
            trigger: "blur"
          }],
          adcode: [{
            required: true,
            message: "区域编码不能为空",
            trigger: "blur"
          }],
          fileName: [{
            required: true,
            message: "文件名称不能为空",
            trigger: "blur"
          }],
          fileId: [{
            required: true,
            message: "文件唯一标识不能为空",
            trigger: "blur"
          }],
          // indexUrl: [{
          //   required: true,
          //   message: "返回h5链接不能为空",
          //   trigger: "blur"
          // }],
          longitude: [{
            required: true,
            message: "经度,基于高德地图不能为空",
            trigger: "blur"
          }],
          latitude: [{
            required: true,
            message: "纬度,基于高德地图不能为空",
            trigger: "blur"
          }],
          // sort: [{
          //   required: true,
          //   message: "排序：从小到大不能为空",
          //   trigger: "blur"
          // }]
        },
        importRules: {
          pageTitle: [{
            required: true,
            message: "页面标题不能为空",
            trigger: "blur"
          }],
        },
        upload: {
          // 是否显示弹出层（激励金导入）
          open: false,
          // 弹出层标题（激励金导入）
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 是否更新已经存在的用户数据
          updateSupport: 0,
          // 设置上传的请求头部
          headers: {
            Authorization: "Bearer " + getToken()
          },
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/zlyyh-admin/activityFileShop/importData"
        },
      };
    },
    created() {
      this.getList();
      this.getTypeSelectList();
      this.getFileSelectList();
    },
    methods: {
      //类别下拉列表
      getTypeSelectList() {
        let param = {
          status: '0'
        }
        selectMerTypeList(param).then(response => {
          this.typeList = response.data;
        });
      },
      typeIdFormatter(row) {
        let name = '';
        this.typeList.forEach(item => {
          if (item.id == row.typeId) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          // row.typeName = name;
          return name;
        }
        return row.typeId;
      },
      //文件下拉列表
      getFileSelectList() {
        selectFileList({}).then(response => {
          this.fileList = response.data;
        });
      },
      fileIdFormatter(row) {
        let name = '';
        this.fileList.forEach(item => {
          if (item.id == row.fileId) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          // row.typeName = name;
          return name;
        }
        return row.fileId;
      },
      /** 查询活动商户列表 */
      getList() {
        this.loading = true;
        listActivityFileShop(this.queryParams).then(response => {
          this.activityFileShopList = response.rows;
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
          activityShopId: undefined,
          activityShopName: undefined,
          address: undefined,
          formattedAddress: undefined,
          province: undefined,
          city: undefined,
          district: undefined,
          procode: undefined,
          citycode: undefined,
          adcode: undefined,
          fileName: undefined,
          fileId: undefined,
          indexUrl: undefined,
          longitude: undefined,
          latitude: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          sort: 99,
          status: '0',
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
        this.ids = selection.map(item => item.activityShopId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加活动商户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const activityShopId = row.activityShopId || this.ids
        getActivityFileShop(activityShopId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.form.cityName = this.form.city.slice(0, -1);
          this.open = true;
          this.title = "修改活动商户";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.activityShopId != null) {
              updateActivityFileShop(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addActivityFileShop(this.form).then(response => {
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
        const activityShopIds = row.activityShopId || this.ids;
        this.$modal.confirm('是否确认删除活动商户编号为"' + activityShopIds + '"的数据项？').then(() => {
          this.loading = true;
          return delActivityFileShop(activityShopIds);
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
        this.download('zlyyh/activityFileShop/export', {
          ...this.queryParams
        }, `activityFileShop_${new Date().getTime()}.xlsx`)
      },
      /** 下载模板操作 */
      importTemplate() {
        this.download('zlyyh-admin/activityFileShop/importTemplate?', {},
          `merchant_template_${new Date().getTime()}.xlsx`)
      },
      //商户导入操作
      handleMerchantImport() {
        this.upload.open = true;
      },
      // 提交上传文件
      submitImportForm() {
        this.$refs["importForm"].validate(valid => {
          if (valid) {
            this.$refs.upload.submit();
          }
        });
      },
      // 文件上传中处理
      handleSettleFileUploadProgress(event, file, fileList) {
        this.upload.isUploading = true;
      },
      // 文件上传成功处理
      handleSettleFileSuccess(response, file, fileList) {
        if (response.code === 500) {
          this.$message.error(response.msg);
        }
        this.upload.open = false;
        this.upload.isUploading = false;
        this.$refs.upload.clearFiles();
        this.getList();
      },
    }
  };
</script>