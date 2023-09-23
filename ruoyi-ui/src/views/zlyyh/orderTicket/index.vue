<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品id" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入产品id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="场次id" prop="sessionId">
        <el-input
          v-model="queryParams.sessionId"
          placeholder="请输入场次id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="票种id" prop="lineId">
        <el-input
          v-model="queryParams.lineId"
          placeholder="请输入票种id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="观影时间" prop="ticketTime">
        <el-date-picker clearable
                        v-model="queryParams.ticketTime"
                        type="datetime"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择观影时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="购买数量" prop="count">
        <el-input
          v-model="queryParams.count"
          placeholder="请输入购买数量"
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:orderTicket:export']"
        >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport"
                   v-hasPermi="['system:user:import']">导入
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderTicketList">
      <el-table-column label="订单号" align="left" prop="number" width="230">
        <template slot-scope="scope">
          <div>
            订单号：{{ scope.row.number }}
          </div>
          <div>
            产品id：{{ scope.row.productId }}
          </div>
          <div>
            场次id：{{ scope.row.sessionId }}
          </div>
          <div>
            票种id：{{ scope.row.lineId }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="票档信息" align="left" prop="count">
        <template slot-scope="scope">
          <div>
            购买金额：{{ scope.row.price }}
          </div>
          <div>
            结算金额：{{ scope.row.sellPrice }}
          </div>
          <div>
            购买数量：{{ scope.row.count }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="收货地址" align="left" prop="addressInfo">
        <template slot-scope="scope">
          <div>
            收货人：
            <span>{{ scope.row.name }}</span>
          </div>
          <div>
            联系电话：{{ scope.row.tel }}
          </div>
          <div>
            详细地址：{{ scope.row.addressInfo }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="物流信息" align="left" prop="logistics">
        <template slot-scope="scope">
          <div>
            物流公司：{{ scope.row.logisticsCom }}
          </div>
          <div>
            物流状态：
            <span v-if="scope.row.logisticsStatus === '0'">未发货</span>
            <span v-else-if="scope.row.logisticsStatus === '1'">已发货</span>
            <span v-else-if="scope.row.logisticsStatus === '2'">已签收</span>
            <span v-else-if="scope.row.logisticsStatus === '3'">已拒收</span>
          </div>
          <div>
            物流单号：{{ scope.row.logistics }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="观影信息" align="left" prop="ticketTime">
        <template slot-scope="scope">
          <div>观影时间:{{ parseTime(scope.row.ticketTime, '{y}-{m}-{d} {h}:{m}') }}</div>
          <div>
            观影地址：{{ scope.row.shopAddress }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="快递方式" align="left" prop="ticketPostWay">
        <template slot-scope="scope">
          <div>
            <span v-if="scope.row.ticketForm == 1">电子票</span>
            <span v-if="scope.row.ticketForm == 2">实体票</span>
          </div>
          <div>快递方式:
            <span v-if="scope.row.ticketPostWay == '0'">无需邮寄</span>
            <span v-if="scope.row.ticketPostWay == '1'">包邮</span>
            <span v-if="scope.row.ticketPostWay == '2'">另付邮费</span>
          </div>
          <div>
            邮费：{{ scope.row.ticketPostage }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <div>
            <el-button type="text" size="mini" @click="queryTicketLine(scope.row)">票种
            </el-button>
          </div>
          <div>
            <el-button type="text" size="mini" @click="queryOrderIdCard(scope.row)">观影人
            </el-button>
          </div>
          <div>
            <el-button type="text" size="mini" @click="listCode(scope.row)">核销</el-button>
          </div>
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

    <el-dialog title="票种详情" :visible.sync="lineOpen" width="30%">
      <div>
        票种名称:
        <span>{{ ticketLine.lineTitle }}</span>
      </div>
      <div>
        销售价格: {{ ticketLine.linePrice }}
      </div>
      <div>
        结算价格: {{ ticketLine.lineSettlePrice }}
      </div>
      <div>
        购买数量上限: {{ ticketLine.lineNumber }}
      </div>
      <div>
        单次购买上限: {{ ticketLine.lineUpperLimit }}
      </div>
      <div>
        状态:
        <el-select disabled v-model="ticketLine.lineStatus" placeholder="请选择状态">
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </div>
      <div>
        第三方编号: {{ ticketLine.otherId }}
      </div>
      <div>
        说明: {{ ticketLine.lineDescription }}
      </div>
      <div>
        <el-button type="primary" @click="lineOpen = false" style="margin-right: auto">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="观影人信息" :visible.sync="orderIdCardOpen" width="50%">
      <el-table :data="orderTicketIdCardList">
        <el-table-column prop="name" label="姓名" width="100"/>
        <el-table-column prop="cardType" label="证件类型" width="200">
          <template slot-scope="scope">
            <span v-if="scope.row.cardType === '0'">身份证</span>
            <span v-else-if="scope.row.cardType === '1'">护照</span>
            <span v-else-if="scope.row.cardType === '2'">港澳台居民居住证</span>
            <span v-else-if="scope.row.cardType === '3'">户口簿</span>
          </template>
        </el-table-column>
        <el-table-column prop="idCard" label="证件号"/>
      </el-table>
    </el-dialog>

    <!-- 核销操作 -->
    <el-dialog title="核销码信息" :visible.sync="open" width="55%" append-to-body>
      <el-table :data="codeList">
        <el-table-column prop="codeNo" label="券号"/>
        <el-table-column prop="usedStatus" label="核销状态">
          <template slot-scope="scope">
            <span v-if="scope.row.usedStatus === '0'">未核销</span>
            <span v-else-if="scope.row.usedStatus === '1'">已核销</span>
            <span v-else-if="scope.row.usedStatus === '2'">已失效</span>
            <span v-else-if="scope.row.usedStatus === '3'">已作废</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.usedStatus === '0'" type="text" size="mini" @click="writeOffCode(scope.row)">
              核销
            </el-button>
            <el-button v-if="scope.row.usedStatus === '1'" type="text" size="mini" @click="voidCode(scope.row)">
              票卷返还
            </el-button>
            <el-button v-if="scope.row.usedStatus === '0'" type="text" size="mini" @click="returnCode(scope.row)">作废
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="codeTotal>0"
        :total="codeTotal"
        :page.sync="codeParams.pageNum"
        :limit.sync="codeParams.pageSize"
        @pagination="listCode(null)"
      />
    </el-dialog>

    <!--物流数据导入-->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
                 :action="upload.url" :disabled="upload.isUploading"
                 :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
                   @click="importTemplate">下载模板
          </el-link>
          <div class="el-upload__tip" slot="tip">
            请注意：物流状态字段请根据提示填写对应的数字。
          </div>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {listOrderTicket, writeOffCode, voidCode, returnCode} from "@/api/zlyyh/orderTicket";
import {getProductTicketLine, getOrderIdCardList} from "@/api/zlyyh/productTicketLine";
import {listCode} from '@/api/zlyyh/code';
import {getToken} from "@/utils/auth";

export default {
  name: "OrderTicket",
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
      codeTotal: 0,
      // 演出票订单表格数据
      orderTicketList: [],
      orderIdCardOpen: false,
      orderTicketIdCardList: [],
      open: false,
      codeList: [],
      codeParams: {
        pageNum: 1,
        pageSize: 10,
        number: undefined,
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        number: undefined,
        productId: undefined,
        sessionId: undefined,
        lineId: undefined,
        ticketTime: undefined,
        count: undefined,
      },
      lineOpen: false,
      ticketLine: {
        lineTitle: undefined,
        linePrice: 0,
        lineSettlePrice: undefined,
        lineNumber: 0,
        lineUpperLimit: 0,
        lineStatus: 1,
        otherId: undefined,
        lineDescription: undefined,
      },
      // 物流数据导入
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: {
          Authorization: "Bearer " + getToken()
        },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/zlyyh-admin/orderTicket/importData"
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询演出票订单列表 */
    getList() {
      this.loading = true;
      listOrderTicket(this.queryParams).then(response => {
        this.orderTicketList = response.rows;
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
        number: undefined,
        productId: undefined,
        sessionId: undefined,
        lineId: undefined,
        ticketTime: undefined,
        price: undefined,
        sellPrice: undefined,
        count: undefined,
        userAddressId: undefined,
        name: undefined,
        tel: undefined,
        address: undefined,
        addressInfo: undefined,
        shopId: undefined,
        shopName: undefined,
        shopAddress: undefined,
        createTime: undefined,
        updateTime: undefined,
        createBy: undefined,
        updateBy: undefined,
        delFlag: undefined,
        ticketNonsupport: undefined,
        ticketInvoice: undefined,
        ticketExpired: undefined,
        ticketAnyTime: undefined,
        ticketChooseSeat: undefined,
        ticketPostWay: undefined,
        ticketPostage: undefined,
        logistics: undefined,
        logisticsStatus: undefined,
        logisticsCom: undefined,
        ticketForm: undefined
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
    // 票种查看
    queryTicketLine(row) {
      const lineId = row.lineId || this.ids;
      getProductTicketLine(lineId).then(response => {
        this.ticketLine = response.data;
        this.lineOpen = true;
      });
    },
    // 观影人信息
    queryOrderIdCard(row) {
      const number = row.number || this.ids;
      getOrderIdCardList(number).then(response => {
        this.orderTicketIdCardList = response.data;
        this.orderIdCardOpen = true;
      });
    },
    // 核销码信息
    listCode(row) {
      if (row != null) {
        const number = row.number || this.ids;
        this.codeParams.number = number;
        listCode(this.codeParams).then(response => {
          this.codeList = response.rows;
          this.codeTotal = response.total;
          this.open = true;
        });
      }
    },
    // 核销
    writeOffCode(row) {
      const codeNo = row.codeNo || this.ids;
      writeOffCode(codeNo).then(response => {
        if (response.data === true) {
          this.$message({
            type: 'success',
            message: '操作成功!'
          });
          this.listCode(row);
        } else {
          this.$message({
            type: 'error',
            message: '操作失败!'
          });
        }
      });
    },
    // 票券返还
    voidCode(row) {
      const codeNo = row.codeNo || this.ids;
      voidCode(codeNo).then(response => {
        if (response.data === true) {
          this.$message({
            type: 'success',
            message: '操作成功!'
          });
          this.listCode(row);
        } else {
          this.$message({
            type: 'error',
            message: '操作失败!'
          });
        }
      });
    },
    // 作废
    returnCode(row) {
      const codeNo = row.codeNo || this.ids;
      returnCode(codeNo).then(response => {
        if (response.data === true) {
          this.$message({
            type: 'success',
            message: '操作成功!'
          });
          this.listCode(row);
        } else {
          this.$message({
            type: 'info',
            message: '操作失败!'
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/zlyyh-admin/orderTicket/export', {
        ...this.queryParams
      }, `orderTicket_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "物流数据导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('/zlyyh-admin/orderTicket/importTemplate', {}, `logistics_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response
        .msg + "</div>", "导入结果", {
        dangerouslyUseHTMLString: true
      });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    }
  }
};
</script>
