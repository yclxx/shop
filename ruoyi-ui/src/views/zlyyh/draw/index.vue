<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable>
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="奖品名称" prop="drawName">
        <el-input v-model="queryParams.drawName" placeholder="请输入奖品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="奖品类型" prop="drawType">
        <el-select v-model="queryParams.drawType" placeholder="请选择奖品类型" clearable>
          <el-option v-for="dict in dict.type.draw_type" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['zlyyh:draw:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:draw:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:draw:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:draw:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="drawList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="奖品ID" width="170" align="center" prop="drawId" v-if="true" />
      <el-table-column label="奖品名称" min-width="150" align="center" prop="drawName" />
      <el-table-column label="奖品简称" min-width="120" align="center" prop="drawSimpleName" />
      <el-table-column label="任务组" align="center" min-width="100" prop="missionGroupId"
        :formatter="formatterMissionGroup" />
      <el-table-column label="平台" align="center" min-width="100" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="奖品图片" align="center" prop="drawImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.drawImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="中奖概率" align="center" prop="drawProbability" />
      <el-table-column label="状态" fixed="right" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="奖品类型" width="100" align="center" prop="drawType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.draw_type" :value="scope.row.drawType" />
        </template>
      </el-table-column>
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
      <el-table-column label="领取开始时间" align="center" prop="sellStartDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sellStartDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="领取结束时间" align="center" prop="sellEndDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sellEndDate) }}</span>
        </template>
      </el-table-column>
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
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:draw:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:draw:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改奖品管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务组" prop="missionGroupId">
              <el-select v-model="form.missionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
                <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id"
                  :label="item.label"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖品名称" prop="drawName">
              <el-input v-model="form.drawName" placeholder="请输入奖品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖品简称" prop="drawSimpleName">
              <span slot="label">
                奖品简称
                <el-tooltip content="显示在转盘上的或其他需要显示简短名称的地方" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.drawSimpleName" :maxlength="20" placeholder="请输入奖品简称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中奖概率" prop="drawProbability">
              <span slot="label">
                中奖概率
                <el-tooltip content="任务组中所有奖品根据数值比例进行抽奖" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input-number v-model="form.drawProbability" controls-position="right" :precision="6" :min="0.000001"
                :step="0.000001" :max="99999999">
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖品图片" prop="drawImg">
              <image-upload :limit="1" :isShowTip="false" isShow v-model="form.drawImg" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖品类型" prop="drawType">
              <el-select style="width: 100%;" v-model="form.drawType" placeholder="请选择奖品类型">
                <el-option v-for="dict in dict.type.draw_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="能否抽中" prop="drawWinning">
              <el-radio-group v-model="form.drawWinning">
                <el-radio v-for="dict in dict.type.t_draw_winner" :key="dict.value"
                          :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.drawType == '1'">
            <el-form-item label="发放金额" prop="sendValue">
              <el-input v-model="form.sendValue" placeholder="请输入发放金额">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.drawType == '2'">
            <el-form-item label="发放数量" prop="sendValue">
              <el-input v-model="form.sendValue" placeholder="请输入发放数量">
                <template slot="append">积点</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖品额度" prop="drawQuota">
              <span slot="label">
                奖品额度
                <el-tooltip content="奖品额度不等同于金额,单个奖品占用总额度的多少,主要统一单位好进行相关限制条件限制规则" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.drawQuota" placeholder="请输入奖品额度" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="取码编号" prop="drawNo">
              <span slot="label">
                取码编号
                <el-tooltip content="活动编号或发放票券编号或积点活动编号" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.drawNo" placeholder="请输入取码编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="跳转类型" prop="toType">
              <el-select v-model="form.toType" placeholder="请选择跳转类型" style="width: 100%;">
                <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.toType == '3'">
            <el-form-item label="小程序ID" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入小程序ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.toType != '4' && form.toType != '0'">
            <el-form-item label="页面地址" prop="url">
              <el-input v-model="form.url" placeholder="请输入页面地址" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示开始时间" prop="showStartDate">
              <el-date-picker style="width: 100%;" clearable v-model="form.showStartDate" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示结束时间" prop="showEndDate">
              <el-date-picker style="width: 100%;" clearable v-model="form.showEndDate" type="datetime"
                default-time="23:59:59" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="领取开始时间" prop="sellStartDate">
              <el-date-picker style="width: 100%;" clearable v-model="form.sellStartDate" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择领取开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="领取结束时间" prop="sellEndDate">
              <el-date-picker style="width: 100%;" clearable v-model="form.sellEndDate" type="datetime"
                default-time="23:59:59" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择领取结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总次数" prop="totalCount">
              <span slot="label">
                总次数
                <el-tooltip content="该奖品累计可被抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.totalCount" placeholder="请输入总次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每月次数" prop="monthCount">
              <span slot="label">
                每月次数
                <el-tooltip content="该奖品每月可被抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.monthCount" placeholder="请输入每月次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每周次数" prop="weekCount">
              <span slot="label">
                每周次数
                <el-tooltip content="该奖品每周可被抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.weekCount" placeholder="请输入每周次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每日次数" prop="dayCount">
              <span slot="label">
                每日次数
                <el-tooltip content="该奖品每日可被抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.dayCount" placeholder="请输入每日次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入排序" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-divider>用户可获该奖品次数设置</el-divider>
          <el-col :span="8">
            <el-form-item label="每日次数" prop="dayUserCount">
              <span slot="label">
                每日次数
                <el-tooltip content="该奖品每日可被同一用户抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.dayUserCount" placeholder="请输入用户每日限领次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每周次数" prop="weekUserCount">
              <span slot="label">
                每周次数
                <el-tooltip content="该奖品每周可被同一用户抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.weekUserCount" placeholder="请输入用户每周限领次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每月次数" prop="monthUserCount">
              <span slot="label">
                每月次数
                <el-tooltip content="该奖品每月可被同一用户抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.monthUserCount" placeholder="请输入用户每月限领次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="累计次数" prop="totalUserCount">
              <span slot="label">
                累计次数
                <el-tooltip content="该奖品累计可被同一用户抽中多少次,或发放多少次，0为不限制" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.totalUserCount" placeholder="请输入用户累计限领次数" />
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
    listDraw,
    getDraw,
    delDraw,
    addDraw,
    updateDraw
  } from "@/api/zlyyh/draw";
  import {
    listMissionGroupSelect
  } from "@/api/zlyyh/missionGroup"
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"

  export default {
    name: "Draw",
    dicts: ['draw_type', 'sys_normal_disable', 't_banner_to_type','t_draw_winner'],
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
        // 奖品管理表格数据
        drawList: [],
        missionGroupList: [],
        platformList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'draw_id',
          isAsc: 'desc',
          drawName: undefined,
          drawSimpleName: undefined,
          status: undefined,
          drawType: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          missionGroupId: [{
            required: true,
            message: "任务组不能为空",
            trigger: "blur"
          }],
          drawId: [{
            required: true,
            message: "奖品ID不能为空",
            trigger: "blur"
          }],
          drawName: [{
            required: true,
            message: "奖品名称不能为空",
            trigger: "blur"
          }],
          drawSimpleName: [{
            required: true,
            message: "奖品简称不能为空",
            trigger: "blur"
          }],
          drawProbability: [{
            required: true,
            message: "中奖概率不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          drawType: [{
            required: true,
            message: "奖品类型不能为空",
            trigger: "change"
          }],
          drawQuota: [{
            required: true,
            message: "奖品额度不能为空",
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
      selectListPlatform({}).then(res => {
        this.platformList = res.data;
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
      /** 查询奖品管理列表 */
      getList() {
        this.loading = true;
        listDraw(this.queryParams).then(response => {
          this.drawList = response.rows;
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
          drawId: undefined,
          drawName: undefined,
          drawSimpleName: undefined,
          drawImg: undefined,
          drawProbability: undefined,
          status: '0',
          drawType: undefined,
          drawNo: undefined,
          sendValue: undefined,
          drawQuota: undefined,
          toType: undefined,
          appId: undefined,
          url: undefined,
          showStartDate: undefined,
          showEndDate: undefined,
          sellStartDate: undefined,
          sellEndDate: undefined,
          totalCount: undefined,
          monthCount: undefined,
          weekCount: undefined,
          dayCount: undefined,
          dayUserCount: undefined,
          weekUserCount: undefined,
          monthUserCount: undefined,
          totalUserCount: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined,
          platformKey: undefined,
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
        this.ids = selection.map(item => item.drawId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加奖品管理";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const drawId = row.drawId || this.ids
        getDraw(drawId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改奖品管理";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.drawId != null) {
              updateDraw(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addDraw(this.form).then(response => {
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
        const drawIds = row.drawId || this.ids;
        this.$modal.confirm('是否确认删除奖品管理编号为"' + drawIds + '"的数据项？').then(() => {
          this.loading = true;
          return delDraw(drawIds);
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
        this.download('zlyyh-admin/draw/export', {
          ...this.queryParams
        }, `draw_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
