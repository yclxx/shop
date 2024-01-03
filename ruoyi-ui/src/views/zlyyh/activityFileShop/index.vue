<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商户名称" prop="activityShopName">
        <el-input
          v-model="queryParams.activityShopName"
          placeholder="请输入商户名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入商户地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码" prop="formattedAddress">
        <el-input
          v-model="queryParams.formattedAddress"
          placeholder="请输入结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省份名" prop="province">
        <el-input
          v-model="queryParams.province"
          placeholder="请输入省份名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="城市名" prop="city">
        <el-input
          v-model="queryParams.city"
          placeholder="请输入城市名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地址所在区" prop="district">
        <el-input
          v-model="queryParams.district"
          placeholder="请输入地址所在区"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省份编码" prop="procode">
        <el-input
          v-model="queryParams.procode"
          placeholder="请输入省份编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="城市编码" prop="citycode">
        <el-input
          v-model="queryParams.citycode"
          placeholder="请输入城市编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区域编码" prop="adcode">
        <el-input
          v-model="queryParams.adcode"
          placeholder="请输入区域编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文件名称" prop="fileName">
        <el-input
          v-model="queryParams.fileName"
          placeholder="请输入文件名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文件唯一标识" prop="fileId">
        <el-input
          v-model="queryParams.fileId"
          placeholder="请输入文件唯一标识"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="返回h5链接" prop="indexUrl">
        <el-input
          v-model="queryParams.indexUrl"
          placeholder="请输入返回h5链接"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="经度,基于高德地图" prop="longitude">
        <el-input
          v-model="queryParams.longitude"
          placeholder="请输入经度,基于高德地图"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="纬度,基于高德地图" prop="latitude">
        <el-input
          v-model="queryParams.latitude"
          placeholder="请输入纬度,基于高德地图"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序：从小到大" prop="sort">
        <el-input
          v-model="queryParams.sort"
          placeholder="请输入排序：从小到大"
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
          v-hasPermi="['zlyyh:activityFileShop:add']"
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
          v-hasPermi="['zlyyh:activityFileShop:edit']"
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
          v-hasPermi="['zlyyh:activityFileShop:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:activityFileShop:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activityFileShopList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="activityShopId" v-if="true"/>
      <el-table-column label="商户名称" align="center" prop="activityShopName" />
      <el-table-column label="商户地址" align="center" prop="address" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码" align="center" prop="formattedAddress" />
      <el-table-column label="省份名" align="center" prop="province" />
      <el-table-column label="城市名" align="center" prop="city" />
      <el-table-column label="地址所在区" align="center" prop="district" />
      <el-table-column label="省份编码" align="center" prop="procode" />
      <el-table-column label="城市编码" align="center" prop="citycode" />
      <el-table-column label="区域编码" align="center" prop="adcode" />
      <el-table-column label="文件名称" align="center" prop="fileName" />
      <el-table-column label="文件唯一标识" align="center" prop="fileId" />
      <el-table-column label="返回h5链接" align="center" prop="indexUrl" />
      <el-table-column label="经度,基于高德地图" align="center" prop="longitude" />
      <el-table-column label="纬度,基于高德地图" align="center" prop="latitude" />
      <el-table-column label="排序：从小到大" align="center" prop="sort" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:activityFileShop:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:activityFileShop:remove']"
          >删除</el-button>
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

    <!-- 添加或修改活动商户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商户名称" prop="activityShopName">
          <el-input v-model="form.activityShopName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="商户地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入商户地址" />
        </el-form-item>
        <el-form-item label="结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码" prop="formattedAddress">
          <el-input v-model="form.formattedAddress" placeholder="请输入结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码" />
        </el-form-item>
        <el-form-item label="省份名" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份名" />
        </el-form-item>
        <el-form-item label="城市名" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市名" />
        </el-form-item>
        <el-form-item label="地址所在区" prop="district">
          <el-input v-model="form.district" placeholder="请输入地址所在区" />
        </el-form-item>
        <el-form-item label="省份编码" prop="procode">
          <el-input v-model="form.procode" placeholder="请输入省份编码" />
        </el-form-item>
        <el-form-item label="城市编码" prop="citycode">
          <el-input v-model="form.citycode" placeholder="请输入城市编码" />
        </el-form-item>
        <el-form-item label="区域编码" prop="adcode">
          <el-input v-model="form.adcode" placeholder="请输入区域编码" />
        </el-form-item>
        <el-form-item label="文件名称" prop="fileName">
          <el-input v-model="form.fileName" placeholder="请输入文件名称" />
        </el-form-item>
        <el-form-item label="文件唯一标识" prop="fileId">
          <el-input v-model="form.fileId" placeholder="请输入文件唯一标识" />
        </el-form-item>
        <el-form-item label="返回h5链接" prop="indexUrl">
          <el-input v-model="form.indexUrl" placeholder="请输入返回h5链接" />
        </el-form-item>
        <el-form-item label="经度,基于高德地图" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度,基于高德地图" />
        </el-form-item>
        <el-form-item label="纬度,基于高德地图" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度,基于高德地图" />
        </el-form-item>
        <el-form-item label="排序：从小到大" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序：从小到大" />
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
import { listActivityFileShop, getActivityFileShop, delActivityFileShop, addActivityFileShop, updateActivityFileShop } from "@/api/zlyyh/activityFileShop";

export default {
  name: "ActivityFileShop",
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
      // 表单校验
      rules: {
        activityShopId: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
        activityShopName: [
          { required: true, message: "商户名称不能为空", trigger: "blur" }
        ],
        address: [
          { required: true, message: "商户地址不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
        formattedAddress: [
          { required: true, message: "结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码不能为空", trigger: "blur" }
        ],
        province: [
          { required: true, message: "省份名不能为空", trigger: "blur" }
        ],
        city: [
          { required: true, message: "城市名不能为空", trigger: "blur" }
        ],
        district: [
          { required: true, message: "地址所在区不能为空", trigger: "blur" }
        ],
        procode: [
          { required: true, message: "省份编码不能为空", trigger: "blur" }
        ],
        citycode: [
          { required: true, message: "城市编码不能为空", trigger: "blur" }
        ],
        adcode: [
          { required: true, message: "区域编码不能为空", trigger: "blur" }
        ],
        fileName: [
          { required: true, message: "文件名称不能为空", trigger: "blur" }
        ],
        fileId: [
          { required: true, message: "文件唯一标识不能为空", trigger: "blur" }
        ],
        indexUrl: [
          { required: true, message: "返回h5链接不能为空", trigger: "blur" }
        ],
        longitude: [
          { required: true, message: "经度,基于高德地图不能为空", trigger: "blur" }
        ],
        latitude: [
          { required: true, message: "纬度,基于高德地图不能为空", trigger: "blur" }
        ],
        sort: [
          { required: true, message: "排序：从小到大不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        sort: undefined
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
      this.single = selection.length!==1
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
      }).catch(() => {
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('zlyyh/activityFileShop/export', {
        ...this.queryParams
      }, `activityFileShop_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
