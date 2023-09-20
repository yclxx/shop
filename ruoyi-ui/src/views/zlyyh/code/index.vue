<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品ID" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="场次ID" prop="productSessionId">
        <el-input
          v-model="queryParams.productSessionId"
          placeholder="请输入场次ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="规格ID" prop="productSkuId">
        <el-input
          v-model="queryParams.productSkuId"
          placeholder="请输入规格ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input
          v-model="queryParams.productName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="场次名称" prop="productSessionName">
        <el-input
          v-model="queryParams.productSessionName"
          placeholder="请输入场次名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="规格名称" prop="productSkuName">
        <el-input
          v-model="queryParams.productSkuName"
          placeholder="请输入规格名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="券号" prop="codeNo">
        <el-input
          v-model="queryParams.codeNo"
          placeholder="请输入券号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分配状态：0-未分配，1-已分配" prop="allocationState">
        <el-select v-model="queryParams.allocationState" placeholder="请选择分配状态：0-未分配，1-已分配" clearable>
          <el-option
            v-for="dict in dict.type.t_code_allocation_state"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属订单号" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入所属订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销状态：0-未核销，1-已核销，2-已失效，3-已作废" prop="usedStatus">
        <el-select v-model="queryParams.usedStatus" placeholder="请选择核销状态：0-未核销，1-已核销，2-已失效，3-已作废" clearable>
          <el-option
            v-for="dict in dict.type.t_code_used_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="券码类型：0-系统券码，1-外部券码" prop="codeType">
        <el-select v-model="queryParams.codeType" placeholder="请选择券码类型：0-系统券码，1-外部券码" clearable>
          <el-option
            v-for="dict in dict.type.t_code_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="核销或作废时间" prop="usedTime">
        <el-date-picker clearable
          v-model="queryParams.usedTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择核销或作废时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="核销店铺ID" prop="shopId">
        <el-input
          v-model="queryParams.shopId"
          placeholder="请输入核销店铺ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销店铺名称" prop="shopName">
        <el-input
          v-model="queryParams.shopName"
          placeholder="请输入核销店铺名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销人员ID" prop="verifierId">
        <el-input
          v-model="queryParams.verifierId"
          placeholder="请输入核销人员ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销人员手机号" prop="verifierMobile">
        <el-input
          v-model="queryParams.verifierMobile"
          placeholder="请输入核销人员手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="二维码图片URL" prop="qrcodeImgUrl">
        <el-input
          v-model="queryParams.qrcodeImgUrl"
          placeholder="请输入二维码图片URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约店铺ID" prop="appointmentShopId">
        <el-input
          v-model="queryParams.appointmentShopId"
          placeholder="请输入预约店铺ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约店铺名称" prop="appointmentShopName">
        <el-input
          v-model="queryParams.appointmentShopName"
          placeholder="请输入预约店铺名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约时间" prop="appointmentDate">
        <el-date-picker clearable
          v-model="queryParams.appointmentDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择预约时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="预约状态：0-未预约，1-已预约，2-已取消" prop="appointmentStatus">
        <el-select v-model="queryParams.appointmentStatus" placeholder="请选择预约状态：0-未预约，1-已预约，2-已取消" clearable>
          <el-option
            v-for="dict in dict.type.appointment_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预约时间ID" prop="appointmentId">
        <el-input
          v-model="queryParams.appointmentId"
          placeholder="请输入预约时间ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门id" prop="sysDeptId">
        <el-input
          v-model="queryParams.sysDeptId"
          placeholder="请输入部门id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户id" prop="sysUserId">
        <el-input
          v-model="queryParams.sysUserId"
          placeholder="请输入用户id"
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
          v-hasPermi="['zlyyh:code:add']"
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
          v-hasPermi="['zlyyh:code:edit']"
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
          v-hasPermi="['zlyyh:code:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:code:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="codeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" v-if="true"/>
      <el-table-column label="商品ID" align="center" prop="productId" />
      <el-table-column label="场次ID" align="center" prop="productSessionId" />
      <el-table-column label="规格ID" align="center" prop="productSkuId" />
      <el-table-column label="商品名称" align="center" prop="productName" />
      <el-table-column label="场次名称" align="center" prop="productSessionName" />
      <el-table-column label="规格名称" align="center" prop="productSkuName" />
      <el-table-column label="券号" align="center" prop="codeNo" />
      <el-table-column label="分配状态：0-未分配，1-已分配" align="center" prop="allocationState">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_code_allocation_state" :value="scope.row.allocationState"/>
        </template>
      </el-table-column>
      <el-table-column label="所属订单号" align="center" prop="number" />
      <el-table-column label="核销状态：0-未核销，1-已核销，2-已失效，3-已作废" align="center" prop="usedStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_code_used_status" :value="scope.row.usedStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="券码类型：0-系统券码，1-外部券码" align="center" prop="codeType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_code_type" :value="scope.row.codeType"/>
        </template>
      </el-table-column>
      <el-table-column label="核销或作废时间" align="center" prop="usedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.usedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="核销店铺ID" align="center" prop="shopId" />
      <el-table-column label="核销店铺名称" align="center" prop="shopName" />
      <el-table-column label="核销人员ID" align="center" prop="verifierId" />
      <el-table-column label="核销人员手机号" align="center" prop="verifierMobile" />
      <el-table-column label="二维码图片URL" align="center" prop="qrcodeImgUrl" />
      <el-table-column label="预约店铺ID" align="center" prop="appointmentShopId" />
      <el-table-column label="预约店铺名称" align="center" prop="appointmentShopName" />
      <el-table-column label="预约时间" align="center" prop="appointmentDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.appointmentDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预约状态：0-未预约，1-已预约，2-已取消" align="center" prop="appointmentStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.appointment_status" :value="scope.row.appointmentStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="预约时间ID" align="center" prop="appointmentId" />
      <el-table-column label="部门id" align="center" prop="sysDeptId" />
      <el-table-column label="用户id" align="center" prop="sysUserId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:code:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:code:remove']"
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

    <!-- 添加或修改商品券码对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="场次ID" prop="productSessionId">
          <el-input v-model="form.productSessionId" placeholder="请输入场次ID" />
        </el-form-item>
        <el-form-item label="规格ID" prop="productSkuId">
          <el-input v-model="form.productSkuId" placeholder="请输入规格ID" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="场次名称" prop="productSessionName">
          <el-input v-model="form.productSessionName" placeholder="请输入场次名称" />
        </el-form-item>
        <el-form-item label="规格名称" prop="productSkuName">
          <el-input v-model="form.productSkuName" placeholder="请输入规格名称" />
        </el-form-item>
        <el-form-item label="券号" prop="codeNo">
          <el-input v-model="form.codeNo" placeholder="请输入券号" />
        </el-form-item>
        <el-form-item label="分配状态：0-未分配，1-已分配" prop="allocationState">
          <el-select v-model="form.allocationState" placeholder="请选择分配状态：0-未分配，1-已分配">
            <el-option
              v-for="dict in dict.type.t_code_allocation_state"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属订单号" prop="number">
          <el-input v-model="form.number" placeholder="请输入所属订单号" />
        </el-form-item>
        <el-form-item label="核销状态：0-未核销，1-已核销，2-已失效，3-已作废" prop="usedStatus">
          <el-radio-group v-model="form.usedStatus">
            <el-radio
              v-for="dict in dict.type.t_code_used_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="券码类型：0-系统券码，1-外部券码" prop="codeType">
          <el-select v-model="form.codeType" placeholder="请选择券码类型：0-系统券码，1-外部券码">
            <el-option
              v-for="dict in dict.type.t_code_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="核销或作废时间" prop="usedTime">
          <el-date-picker clearable
            v-model="form.usedTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择核销或作废时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="核销店铺ID" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入核销店铺ID" />
        </el-form-item>
        <el-form-item label="核销店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入核销店铺名称" />
        </el-form-item>
        <el-form-item label="核销人员ID" prop="verifierId">
          <el-input v-model="form.verifierId" placeholder="请输入核销人员ID" />
        </el-form-item>
        <el-form-item label="核销人员手机号" prop="verifierMobile">
          <el-input v-model="form.verifierMobile" placeholder="请输入核销人员手机号" />
        </el-form-item>
        <el-form-item label="二维码图片URL" prop="qrcodeImgUrl">
          <el-input v-model="form.qrcodeImgUrl" placeholder="请输入二维码图片URL" />
        </el-form-item>
        <el-form-item label="预约店铺ID" prop="appointmentShopId">
          <el-input v-model="form.appointmentShopId" placeholder="请输入预约店铺ID" />
        </el-form-item>
        <el-form-item label="预约店铺名称" prop="appointmentShopName">
          <el-input v-model="form.appointmentShopName" placeholder="请输入预约店铺名称" />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentDate">
          <el-date-picker clearable
            v-model="form.appointmentDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择预约时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="预约状态：0-未预约，1-已预约，2-已取消" prop="appointmentStatus">
          <el-radio-group v-model="form.appointmentStatus">
            <el-radio
              v-for="dict in dict.type.appointment_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="预约时间ID" prop="appointmentId">
          <el-input v-model="form.appointmentId" placeholder="请输入预约时间ID" />
        </el-form-item>
        <el-form-item label="部门id" prop="sysDeptId">
          <el-input v-model="form.sysDeptId" placeholder="请输入部门id" />
        </el-form-item>
        <el-form-item label="用户id" prop="sysUserId">
          <el-input v-model="form.sysUserId" placeholder="请输入用户id" />
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
import { listCode, getCode, delCode, addCode, updateCode } from "@/api/zlyyh/code";

export default {
  name: "Code",
  dicts: ['appointment_status', 't_code_used_status', 't_code_type', 't_code_allocation_state'],
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
      // 商品券码表格数据
      codeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productId: undefined,
        productSessionId: undefined,
        productSkuId: undefined,
        productName: undefined,
        productSessionName: undefined,
        productSkuName: undefined,
        codeNo: undefined,
        allocationState: undefined,
        number: undefined,
        usedStatus: undefined,
        codeType: undefined,
        usedTime: undefined,
        shopId: undefined,
        shopName: undefined,
        verifierId: undefined,
        verifierMobile: undefined,
        qrcodeImgUrl: undefined,
        appointmentShopId: undefined,
        appointmentShopName: undefined,
        appointmentDate: undefined,
        appointmentStatus: undefined,
        appointmentId: undefined,
        sysDeptId: undefined,
        sysUserId: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询商品券码列表 */
    getList() {
      this.loading = true;
      listCode(this.queryParams).then(response => {
        this.codeList = response.rows;
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
        productId: undefined,
        productSessionId: undefined,
        productSkuId: undefined,
        productName: undefined,
        productSessionName: undefined,
        productSkuName: undefined,
        codeNo: undefined,
        allocationState: undefined,
        number: undefined,
        usedStatus: undefined,
        codeType: undefined,
        usedTime: undefined,
        shopId: undefined,
        shopName: undefined,
        verifierId: undefined,
        verifierMobile: undefined,
        qrcodeImgUrl: undefined,
        appointmentShopId: undefined,
        appointmentShopName: undefined,
        appointmentDate: undefined,
        appointmentStatus: undefined,
        appointmentId: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        delFlag: undefined,
        sysDeptId: undefined,
        sysUserId: undefined
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品券码";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getCode(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改商品券码";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateCode(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addCode(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除商品券码编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delCode(ids);
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
      this.download('zlyyh/code/export', {
        ...this.queryParams
      }, `code_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
