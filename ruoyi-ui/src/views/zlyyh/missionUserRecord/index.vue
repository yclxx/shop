<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户信息" prop="missionUserId">
        <el-input type="number" v-model="queryParams.missionUserId" placeholder="任务用户ID/用户ID/手机号" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable>
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务" prop="missionId">
        <el-select v-model="queryParams.missionId" placeholder="请选择任务" filterable clearable>
          <el-option v-for="item in missionList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="抽奖状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择抽奖状态" clearable>
          <el-option v-for="dict in dict.type.mission_user_record_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="奖品类型" prop="drawType">
        <el-select v-model="queryParams.drawType" placeholder="请选择奖品类型" clearable>
          <el-option v-for="dict in dict.type.draw_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="奖品名称" prop="drawName">
        <el-input v-model="queryParams.drawName" placeholder="请输入奖品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发放状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发放状态" clearable>
          <el-option v-for="dict in dict.type.t_order_send_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="领取时间">
        <el-date-picker clearable v-model="daterangeDrawTime" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间">
        <el-date-picker clearable v-model="daterangeExpiryTime" :picker-options="pickerOptions"
          value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" range-separator="-" start-placeholder="开始日期"
          end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:missionUserRecord:add']">新增抽奖机会</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:missionUserRecord:edit']">作废</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:missionUserRecord:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionUserRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" width="170" align="center" prop="missionUserRecordId" v-if="true" />
      <el-table-column label="任务用户ID" width="190" align="center" prop="missionUserId" />
      <el-table-column label="发放账号" align="center" width="110" prop="sendAccount" />
      <el-table-column label="任务组" align="center" min-width="100" prop="missionGroupId"
        :formatter="formatterMissionGroup" />
      <el-table-column label="任务" align="center" prop="missionId" min-width="100" :formatter="formatterMission" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mission_user_record_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="发放状态" align="center" prop="sendStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_order_send_status" :value="scope.row.sendStatus" />
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="expiryTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expiryTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="奖品类型" align="center" prop="drawType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.draw_type" :value="scope.row.drawType" />
        </template>
      </el-table-column>
      <el-table-column label="奖品名称" align="center" prop="drawName" />
      <el-table-column label="奖品图片" align="center" prop="drawImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.drawImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="领取时间" align="center" prop="drawTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.drawTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="取码单号" align="center" prop="pushNumber" />
      <el-table-column label="取码编号" align="center" prop="drawNo" />
      <el-table-column label="发放金额" align="center" prop="sendValue" />
      <el-table-column label="奖品额度" align="center" prop="drawQuota" />
      <el-table-column label="跳转类型" align="center" prop="toType" />
      <el-table-column label="小程序ID" align="center" prop="appId" />
      <el-table-column label="页面地址" align="center" prop="url" />
      <el-table-column label="发放时间" align="center" prop="sendOkTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendOkTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失败原因" align="center" prop="failReason" />
      <el-table-column label="下单城市" align="center" prop="orderCityName" />
      <el-table-column label="行政区号" align="center" prop="orderCityCode" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status == '1' && (scope.row.sendStatus == '3' || scope.row.sendStatus == '0')"
            size="mini" type="text" icon="el-icon-tickets" @click="handleReissue(scope.row)"
            v-hasPermi="['zlyyh:missionUserRecord:reissue']">补发</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务" prop="missionId">
          <el-select v-model="form.missionId" placeholder="请选择任务" filterable clearable style="width: 100%;">
            <el-option v-for="item in missionList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户信息" prop="missionUserId">
          <el-input v-model="form.missionUserId" placeholder="任务用户ID/用户ID/手机号" clearable />
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
    listMissionUserRecord,
    getMissionUserRecord,
    delMissionUserRecord,
    addMissionUserRecord,
    updateMissionUserRecord,
    expiryMissionUserRecord,
    reissue
  } from "@/api/zlyyh/missionUserRecord";
  import {
    listMissionGroupSelect
  } from "@/api/zlyyh/missionGroup"
  import {
    listMissionSelect
  } from "@/api/zlyyh/mission"

  export default {
    name: "MissionUserRecord",
    dicts: ['draw_type', 'mission_user_record_status', 't_order_send_status'],
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
        // 活动记录表格数据
        missionUserRecordList: [],
        missionGroupList: [],
        missionList: [],
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
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 下单所在城市行政区号时间范围
        daterangeExpiryTime: [],
        // 下单所在城市行政区号时间范围
        daterangeDrawTime: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'mission_user_record_id',
          isAsc: 'desc',
          missionUserId: undefined,
          missionGroupId: undefined,
          missionId: undefined,
          status: undefined,
          expiryTime: undefined,
          drawId: undefined,
          drawType: undefined,
          drawName: undefined,
          sendStatus: undefined,
          sendAccount: undefined,
          pushNumber: undefined,
          drawTime: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          missionId: [{
            required: true,
            message: "任务不能为空",
            trigger: "blur"
          }],
          missionUserId: [{
            required: true,
            message: "用户信息不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      listMissionGroupSelect({}).then(res => {
        this.missionGroupList = res.data;
      })
      listMissionSelect({}).then(res => {
        this.missionList = res.data;
      })
    },
    methods: {
      //订单补发
      handleReissue(row) {
        this.$modal.confirm('是否确认补发订单"' + row.missionUserRecordId + '"？').then(() => {
          this.loading = true;
          return reissue(row.missionUserRecordId);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      formatterMissionGroup(row) {
        let name = ''
        this.missionGroupList.forEach(item => {
          if (row.missionGroupId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.name = name;
          return name;
        }
        return row.missionGroupId;
      },
      formatterMission(row) {
        let name = ''
        this.missionList.forEach(item => {
          if (row.missionId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.name = name;
          return name;
        }
        return row.missionId;
      },
      /** 查询活动记录列表 */
      getList() {
        this.loading = true;
        this.queryParams.params = {};
        if (null != this.daterangeExpiryTime && '' != this.daterangeExpiryTime) {
          this.queryParams.params["beginExpiryTime"] = this.daterangeExpiryTime[0];
          this.queryParams.params["endExpiryTime"] = this.daterangeExpiryTime[1];
        }
        if (null != this.daterangeDrawTime && '' != this.daterangeDrawTime) {
          this.queryParams.params["beginDrawTime"] = this.daterangeDrawTime[0];
          this.queryParams.params["endDrawTime"] = this.daterangeDrawTime[1];
        }
        listMissionUserRecord(this.queryParams).then(response => {
          this.missionUserRecordList = response.rows;
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
          missionUserRecordId: undefined,
          missionUserId: undefined,
          missionGroupId: undefined,
          missionId: undefined,
          status: undefined,
          expiryTime: undefined,
          drawId: undefined,
          drawType: undefined,
          drawName: undefined,
          drawImg: undefined,
          sendStatus: undefined,
          sendAccount: undefined,
          pushNumber: undefined,
          drawTime: undefined,
          drawNo: undefined,
          sendValue: undefined,
          drawQuota: undefined,
          toType: undefined,
          appId: undefined,
          url: undefined,
          sendOkTime: undefined,
          failReason: undefined,
          orderCityName: undefined,
          orderCityCode: undefined,
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
        this.daterangeExpiryTime = [];
        this.daterangeDrawTime = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.missionUserRecordId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加抽奖机会";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const missionUserRecordId = row.missionUserRecordId || this.ids
        getMissionUserRecord(missionUserRecordId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改活动记录";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.missionUserRecordId != null) {
              updateMissionUserRecord(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMissionUserRecord(this.form).then(response => {
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
        const missionUserRecordIds = row.missionUserRecordId || this.ids;
        this.$modal.confirm('是否确认作废活动记录编号为"' + missionUserRecordIds + '"的数据项？').then(() => {
          this.loading = true;
          return expiryMissionUserRecord(missionUserRecordIds);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("操作成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('zlyyh-admin/missionUserRecord/export', {
          ...this.queryParams
        }, `missionUserRecord_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
<style>
  input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none !important;
  }
</style>