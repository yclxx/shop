<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="场次名称" prop="session">
        <el-input
          v-model="queryParams.session"
          placeholder="请输入场次名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker clearable
                        v-model="queryParams.date"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择日期">
        </el-date-picker>
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
          v-hasPermi="['zlyyh:productTicketSession:add']"
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
          v-hasPermi="['zlyyh:productTicketSession:edit']"
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
          v-hasPermi="['zlyyh:productTicketSession:remove']"
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
          v-hasPermi="['zlyyh:productTicketSession:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productTicketSessionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="场次id" align="center" prop="sessionId" v-if="true"/>
      <el-table-column label="商品id" align="center" prop="productId"/>
      <el-table-column label="场次名称" align="center" prop="session"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="日期" align="center" prop="date" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.date, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="说明" align="center" prop="description"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handleLine(scope.row)"
            v-hasPermi="['zlyyh:productTicketSession:edit']"
          >票种
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:productTicketSession:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:productTicketSession:remove']"
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
    <!-- 添加或修改演出场次对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="80%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="6">
            <el-form-item label="名称" prop="session">
              <el-input v-model="form.session" placeholder="请输入场次名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="日期" prop="date">
              <el-date-picker clearable
                              v-model="form.date"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="说明" prop="description">
              <el-input v-model="form.description" placeholder="请输入说明"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addRow()">新增票种
        </el-button>
        <el-table :data="ticketLineData" ref="table" style="width: 100%">
          <el-table-column :render-header="renderHeader" class="required" label="票种名称" align="center" prop="lineTitle">
            <template slot-scope="scope">
              <el-input v-model="scope.row.lineTitle" placeholder="请输入票种名称"/>
            </template>
          </el-table-column>
          <el-table-column :render-header="renderHeader" label="销售价格" align="center" prop="linePrice">
            <template slot-scope="scope">
              <el-input v-model="scope.row.linePrice" placeholder="请输入销售价格"/>
            </template>
          </el-table-column>
          <el-table-column :render-header="renderHeader" label="结算价格" align="center" prop="lineSettlePrice">
            <template slot-scope="scope">
              <el-input v-model="scope.row.lineSettlePrice" placeholder="请输入结算价格"/>
            </template>
          </el-table-column>
          <el-table-column :render-header="renderHeader" label="总数量" align="center" prop="lineNumber">
            <template slot-scope="scope">
              <el-input v-model="scope.row.lineNumber" placeholder="请输入总数量"/>
            </template>
          </el-table-column>
          <el-table-column :render-header="renderHeader" label="单次购买上限" align="center" prop="lineUpperLimit">
            <template slot-scope="scope">
              <el-input v-model="scope.row.lineUpperLimit" placeholder="请输入单次购买上限"/>
            </template>
          </el-table-column>
          <el-table-column :render-header="renderHeader" label="状态" align="center" prop="lineStatus">
            <template slot-scope="scope">
              <el-select v-model="scope.row.lineStatus" placeholder="请选择状态">
                <el-option
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="第三方编号" align="center" prop="otherId">
            <template slot-scope="scope">
              <el-input v-model="scope.row.otherId" placeholder="请输入第三方编号"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="delRow(scope.row)"
                v-hasPermi="['zlyyh:productTicketSession:remove']"
              >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="票种信息" :visible.sync="line" width="80%" append-to-body>
      <el-form :model="queryLineParams" ref="queryLineForm" size="small" :inline="true" v-show="lineShowSearch"
               label-width="68px">
        <el-form-item label="票种名称" prop="lineTitle">
          <el-input
            v-model="queryLineParams.lineTitle"
            placeholder="请输入票种名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="lineStatus">
          <el-select v-model="queryLineParams.lineStatus" placeholder="请选择状态" clearable>
            <el-option
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleLineQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetLineQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleLineAdd"
            v-hasPermi="['zlyyh:productTicketLine:add']"
          >新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleLineExport"
            v-hasPermi="['zlyyh:productTicketLine:export']"
          >导出
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="lineShowSearch" @queryTable="getLineList"></right-toolbar>
      </el-row>
      <el-table v-loading="lineLoading" :data="productTicketLineList">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="商品id" align="center" prop="productId"/>
        <el-table-column label="场次id" align="center" prop="sessionId"/>
        <el-table-column label="第三方编号" align="center" prop="otherId"/>
        <el-table-column label="票种名称" align="center" prop="lineTitle"/>
        <el-table-column label="销售价格" align="center" prop="linePrice"/>
        <el-table-column label="结算价格" align="center" prop="lineSettlePrice"/>
        <el-table-column label="购买数量上限" align="center" prop="lineNumber"/>
        <el-table-column label="单次购买上限" align="center" prop="lineUpperLimit"/>
        <el-table-column label="状态" align="center" prop="lineStatus">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.lineStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="说明" align="center" prop="lineDescription"/>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleLineUpdate(scope.row)"
              v-hasPermi="['zlyyh:productTicketLine:edit']"
            >修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleLineDelete(scope.row)"
              v-hasPermi="['zlyyh:productTicketLine:remove']"
            >删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加或修改演出票种对话框 -->
    <el-dialog :title="title" :visible.sync="lineOpen" width="500px" append-to-body>
      <el-form ref="form" :model="lineForm" :rules="lineRules" label-width="80px">
        <el-form-item label="票种名称" prop="lineTitle">
          <el-input v-model="lineForm.lineTitle" placeholder="请输入票种名称"/>
        </el-form-item>
        <el-form-item label="销售价格" prop="linePrice">
          <el-input v-model="lineForm.linePrice" placeholder="请输入销售价格"/>
        </el-form-item>
        <el-form-item label="结算价格" prop="linePrice">
          <el-input v-model="lineForm.lineSettlePrice" placeholder="请输入结算价格"/>
        </el-form-item>
        <el-form-item label="购买数量上限" prop="linePrice">
          <el-input v-model="lineForm.lineNumber" placeholder="请输入价格"/>
        </el-form-item>
        <el-form-item label="单次购买上限" prop="linePrice">
          <el-input v-model="lineForm.lineUpperLimit" placeholder="请输入价格"/>
        </el-form-item>
        <el-form-item label="状态" prop="lineStatus">
          <el-select v-model="lineForm.lineStatus" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="第三方编号" prop="otherId">
          <el-input v-model="lineForm.otherId" placeholder="请输入第三方编号"/>
        </el-form-item>
        <el-form-item label="说明" prop="lineDescription">
          <el-input v-model="lineForm.lineDescription" placeholder="请输入说明"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLineLoading" type="primary" @click="submitLineForm">确 定</el-button>
        <el-button @click="cancelLine">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listProductTicketSession,
  getProductTicketSession,
  delProductTicketSession,
  addProductTicketSession,
  updateProductTicketSession
} from "@/api/zlyyh/productTicketSession";
import {
  addProductTicketLine, delProductTicketLine,
  getProductTicketLine,
  listProductTicketLine,
  updateProductTicketLine
} from "@/api/zlyyh/productTicketLine";

export default {
  name: "ProductTicketSession",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 商品id
      productId: undefined,
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
      // 演出(场次)日期表格数据
      productTicketSessionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productId: undefined,
        session: undefined,
        status: undefined,
        date: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        session: [
          {required: true, message: "场次名称不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        date: [
          {required: true, message: "日期不能为空", trigger: "blur"}
        ]
      },
      ticketLineData: [{
        lineId: undefined,
        productId: undefined,
        sessionId: undefined,
        otherId: undefined,
        lineTitle: undefined,
        linePrice: undefined,
        lineSettlePrice: undefined,
        lineNumber: undefined,
        lineUpperLimit: undefined,
        lineStatus: undefined,
      }],
      /** 演出票种数据 */
      productTicketLineList: [],
      lineLoading: true,
      line: false,
      lineOpen: false,
      buttonLineLoading: false,
      lineShowSearch: true,
      queryLineParams: {
        pageNum: 1,
        pageSize: 10,
        productId: undefined,
        sessionId: undefined,
        lineTitle: undefined,
        lineStatus: undefined,
        lineDescription: undefined,
      },
      // 表单参数
      lineForm: {},
      // 表单校验
      lineRules: {
        lineId: [
          {required: true, message: "票种id不能为空", trigger: "blur"}
        ],
        productId: [
          {required: true, message: "商品id不能为空", trigger: "blur"}
        ],
        sessionId: [
          {required: true, message: "场次id不能为空", trigger: "blur"}
        ],
        lineTitle: [
          {required: true, message: "票种名称不能为空", trigger: "blur"}
        ],
        linePrice: [
          {required: true, message: "价格不能为空", trigger: "blur"}
        ],
        lineStatus: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ]
      }
    };
  },
  created() {
    this.productId = this.$route.params && this.$route.params.productId
    this.queryParams.productId = this.productId;
    this.getList();
  },
  methods: {
    /** 查询演出(场次)日期列表 */
    getList() {
      this.loading = true;
      listProductTicketSession(this.queryParams).then(response => {
        this.productTicketSessionList = response.rows;
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
        sessionId: undefined,
        productId: undefined,
        session: undefined,
        status: undefined,
        date: undefined,
        description: undefined,
        ticketLine: []
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
      this.ids = selection.map(item => item.sessionId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.ticketLineData = [{
        lineId: undefined,
        productId: undefined,
        sessionId: undefined,
        otherId: undefined,
        lineTitle: undefined,
        linePrice: undefined,
        lineSettlePrice: undefined,
        lineNumber: undefined,
        lineUpperLimit: undefined,
        lineStatus: undefined,
      }];
      this.form.productId = this.productId;
      this.open = true;
      this.title = "添加演出场次与票种";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      this.form.productId = this.productId;
      const sessionId = row.sessionId || this.ids
      getProductTicketSession(sessionId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.ticketLineData = response.data.ticketLine;
        this.open = true;
        this.title = "修改演出场次与票种";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 循环判断票种信息
          let check = this.checkTicketLine();
          if (check === 0) {
            return
          }
          this.form.ticketLine = this.ticketLineData;
          this.buttonLoading = true;
          if (this.form.sessionId != null) {
            updateProductTicketSession(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addProductTicketSession(this.form).then(response => {
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
      const sessionIds = row.sessionId || this.ids;
      this.$modal.confirm('是否确认删除演出(场次)日期编号为"' + sessionIds + '"的数据项？').then(() => {
        this.loading = true;
        return delProductTicketSession(sessionIds);
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
      this.download('zlyyh-admin/productTicketSession/export', {
        ...this.queryParams
      }, `productTicketSession_${new Date().getTime()}.xlsx`)
    },
    /** 票种数据 */
    handleLine(row) {
      this.queryLineParams.sessionId = row.sessionId;
      this.queryLineParams.productId = row.productId;
      this.line = true;
      this.getLineList();
    },
    /** 查询票种列表 */
    /** 搜索按钮操作 */
    handleLineQuery() {
      this.queryParams.pageNum = 1;
      this.getLineList();
    },
    /** 重置按钮操作 */
    resetLineQuery() {
      this.resetForm("queryLineForm");
      this.handleLineQuery();
    },
    getLineList() {
      this.lineLoading = true;
      listProductTicketLine(this.queryLineParams).then(response => {
        this.productTicketLineList = response.rows;
        this.total = response.total;
        this.lineLoading = false;
      });
    },
    /** 新增按钮操作 */
    handleLineAdd() {
      this.resetLine();
      this.lineForm.productId = this.queryLineParams.productId;
      this.lineForm.sessionId = this.queryLineParams.sessionId;
      this.lineOpen = true;
      this.title = "添加演出票种";
    },
    /** 修改按钮操作 */
    handleLineUpdate(row) {
      this.lineLoading = true;
      this.resetLine();
      const lineId = row.lineId || this.ids
      getProductTicketLine(lineId).then(response => {
        this.lineLoading = false;
        this.lineForm = response.data;
        this.lineOpen = true;
        this.title = "修改演出票种";
      });
    },
    /** 提交按钮 */
    submitLineForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLineLoading = true;
          if (this.lineForm.lineId != null) {
            updateProductTicketLine(this.lineForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.lineOpen = false;
              this.getLineList();
            }).finally(() => {
              this.buttonLineLoading = false;
            });
          } else {
            addProductTicketLine(this.lineForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.lineOpen = false;
              this.getLineList();
            }).finally(() => {
              this.buttonLineLoading = false;
            });
          }
        }
      });
    },
    // 取消按钮
    cancelLine() {
      this.lineOpen = false;
      this.resetLine();
    },
    // 表单重置
    resetLine() {
      this.lineForm = {
        lineId: undefined,
        productId: undefined,
        sessionId: undefined,
        otherId: undefined,
        lineTitle: undefined,
        linePrice: undefined,
        lineSettlePrice: undefined,
        lineNumber: undefined,
        lineUpperLimit: undefined,
        lineStatus: undefined,
        lineDescription: undefined,
      };
      this.resetForm("lineForm");
    },
    /** 删除按钮操作 */
    handleLineDelete(row) {
      const lineIds = row.lineId || this.ids;
      this.$modal.confirm('是否确认删除演出票种编号为"' + lineIds + '"的数据项？').then(() => {
        this.lineLoading = true;
        return delProductTicketLine(lineIds);
      }).then(() => {
        this.lineLoading = false;
        this.getLineList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      }).finally(() => {
        this.lineLoading = false;
      });
    },
    /** 导出按钮操作 */
    handleLineExport() {
      this.download('zlyyh-admin/productTicketLine/export', {
        ...this.queryParams
      }, `productTicketLine_${new Date().getTime()}.xlsx`)
    },
    // 新增行
    addRow() {
      const row = {
        lineId: undefined,
        productId: undefined,
        sessionId: undefined,
        otherId: undefined,
        lineTitle: undefined,
        linePrice: undefined,
        lineSettlePrice: undefined,
        lineNumber: undefined,
        lineUpperLimit: undefined,
        lineStatus: undefined,
      };
      this.ticketLineData.push(row)
    },
    // 删除行
    delRow(row) {
      const index = this.ticketLineData.indexOf(row)
      this.ticketLineData.splice(index, 1);
    },
    // 判断票种信息
    checkTicketLine() {
      if (this.ticketLineData.length === 0) {
        this.$modal.msgWarning("票种信息不能为空！");
      } else {
        for (let i = 0; i < this.ticketLineData.length; i++) {
          const ticketLine = this.ticketLineData[i];
          if (ticketLine === undefined) {
            this.$modal.msgWarning("票种信息不能为空！");
            return 0;
          }
          if (ticketLine.lineTitle == null || ticketLine.lineTitle === '' || ticketLine.lineTitle === undefined) {
            this.$modal.msgWarning("票种名称不能为空！");
            return 0;
          }
          if (ticketLine.linePrice == null || ticketLine.linePrice === '' || ticketLine.linePrice === undefined) {
            this.$modal.msgWarning("销售价格不能为空！");
            return 0;
          }
          if (ticketLine.lineSettlePrice == null || ticketLine.lineSettlePrice === '' || ticketLine.lineSettlePrice === undefined) {
            this.$modal.msgWarning("结算价格不能为空！");
            return 0;
          }
          if (ticketLine.lineNumber == null || ticketLine.lineNumber === '' || ticketLine.lineNumber === undefined) {
            this.$modal.msgWarning("总数量不能为空！");
            return 0;
          }
          if (ticketLine.lineUpperLimit == null || ticketLine.lineUpperLimit === '' || ticketLine.lineUpperLimit === undefined) {
            this.$modal.msgWarning("单次购买上限不能为空！");
            return 0;
          }
          if (ticketLine.lineStatus == null || ticketLine.lineStatus === '' || ticketLine.lineStatus === undefined) {
            this.$modal.msgWarning("状态不能为空！");
            return 0;
          }
        }
      }
    },
    renderHeader(h,{column}) {
      let currentLabel = column.label;
      return h('span',{},[
        h('span',{style: 'color:red'},'* '),
        h('span',{},currentLabel)
      ])
    },
  }
};
</script>
