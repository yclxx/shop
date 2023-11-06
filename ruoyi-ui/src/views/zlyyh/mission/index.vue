<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable>
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务名称" prop="missionName">
        <el-input v-model="queryParams.missionName" placeholder="请输入任务名称" clearable @keyup.enter.native="handleQuery" />
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
          v-hasPermi="['zlyyh:mission:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:mission:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:mission:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:mission:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务ID" align="center" width="190" prop="missionId" v-if="true" />
      <el-table-column label="任务名称" align="center" min-width="100" prop="missionName" />
      <el-table-column label="平台" align="center" min-width="100" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="任务组" align="center" min-width="100" prop="missionGroupId"
        :formatter="formatterMissionGroup" />
      <el-table-column label="任务编号" min-width="100" align="center" prop="missionUpid" />
      <el-table-column label="开始时间" align="center" prop="startDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务周期" align="center" prop="periodType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.period_type" :value="scope.row.periodType" />
        </template>
      </el-table-column>
      <el-table-column label="任务归属" align="center" prop="missionAffiliation">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_mission_affiliation" :value="scope.row.missionAffiliation" />
        </template>
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="missionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_mission_type" :value="scope.row.missionType" />
        </template>
      </el-table-column>
      <el-table-column label="奖励类型" align="center" prop="missionAwardType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.mission_award_type" :value="scope.row.missionAwardType" />
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
      <el-table-column label="状态" fixed="right" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:mission:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:mission:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
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
            <el-form-item label="任务名称" prop="missionName">
              <el-input v-model="form.missionName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务编号" prop="missionUpid">
              <span slot="label">
                任务编号
                <el-tooltip content="银联任务编号" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.missionUpid" placeholder="请输入银联任务编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开始时间" prop="startDate">
              <el-date-picker style="width: 100%;" clearable v-model="form.startDate" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束时间" prop="endDate">
              <el-date-picker style="width: 100%;" clearable v-model="form.endDate" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" default-time="23:59:59" placeholder="请选择结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务时间" prop="missionTime">
                  <span slot="label">
                    任务时间
                    <el-tooltip content="限制购买时间,例如每天8点至9点可购买: 08:00:00-09:00:00" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </span>
              <el-time-picker is-range v-model="form.missionTime" range-separator="-" start-placeholder="开始时间"
                              end-placeholder="结束时间" placeholder="选择时间范围" style="width: 100%;" value-format="HH:mm:ss">
              </el-time-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="支持端" prop="supportChannel">
              <el-checkbox-group v-model="form.supportChannel">
                <el-checkbox v-for="item in dict.type.channel_type" :name="item.value" :key="item.value"
                  :label="item.value">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="规则图片" prop="missionImg">
              <image-upload :limit="1" :isShowTip="false" v-model="form.missionImg"></image-upload>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务归属" prop="missionAffiliation">
              <el-select style="width: 100%;" v-model="form.missionAffiliation" placeholder="请选择任务归属">
                <el-option v-for="dict in dict.type.t_mission_affiliation" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务类型" prop="missionType">
              <el-select style="width: 100%;" v-model="form.missionType" placeholder="请选择任务类型">
                <el-option v-for="dict in dict.type.t_mission_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖励类型" prop="missionAwardType">
              <span slot="label">
                奖励类型
                <el-tooltip placement="top">
                  <div slot="content">
                    1、抽奖机会：完成任务赠送抽奖机会，用户需点击抽奖抽取相关商品<br />
                    2、奖品：完成任务直接赠送奖品给客户
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-select style="width: 100%;" v-model="form.missionAwardType" placeholder="请选择任务奖励类型">
                <el-option v-for="dict in dict.type.mission_award_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="失效类型" prop="awardExpiryType">
              <span slot="label">
                失效类型
                <el-tooltip placement="top">
                  <div slot="content">
                    奖励类型为抽奖机会有效<br />
                    1、无失效时间：用户获取的抽奖机会随任务时间<br />
                    2、指定失效时间：用户获取的抽奖机会指定时间后未抽奖则失效<br />
                    3、增加指定天数：用户获取的抽奖机会自获取之日起往后多少天内有效<br />
                    4、次月指定时间失效：用户获取的抽奖机会在次月指定时间后未抽奖则失效
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-select style="width: 100%;" v-model="form.awardExpiryType" placeholder="请选择奖励失效时间类型">
                <el-option v-for="dict in dict.type.award_expiry_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.missionAwardType == '0' && form.awardExpiryType && '0' != form.awardExpiryType">
            <el-form-item label="失效时间" prop="awardExpiryDate">
              <el-date-picker v-if="'1' == form.awardExpiryType" style="width: 100%;" clearable
                v-model="form.awardExpiryDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择开始时间">
              </el-date-picker>
              <el-input-number style="width: 100%;" v-else-if="'2' == form.awardExpiryType"
                v-model="form.awardExpiryDate" :min="0" :max="999" label="请输入多少天后失效"
                controls-position="right"></el-input-number>
              <div v-else-if="'3' == form.awardExpiryType">
                <el-input v-model="form.awardExpiryDate" placeholder="请输入次月失效时间" />
                <div style="line-height: 20px;">
                  <span style="color: red;">例如：每月10号23点59分59秒</span><br />
                  <span style="color: red;">则填写：10 23:59:59</span>
                </div>
              </div>
              <el-input v-else v-model="form.awardExpiryDate" placeholder="请输入奖励失效时间" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="奖励总额度" prop="missionTotalQuota">
              <span slot="label">
                奖励总额度
                <el-tooltip placement="top">
                  <div slot="content">
                    奖励总额度（无单位限制，根据奖品设置的额度进行计算，例如总额度设置的100，奖品额度设置的1，则最多发放100个奖品），0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.missionTotalQuota" placeholder="请输入奖励总额度" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="刷新周期" prop="periodType">
              <span slot="label">
                刷新周期
                <el-tooltip placement="top">
                  <div slot="content">
                    任务刷新周期，例如周任务，则配置每周刷新
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-radio-group v-model="form.periodType">
                <el-radio v-for="dict in dict.type.period_type" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.periodType != '0'">
            <el-form-item label="周期额度" prop="missionPeriodQuota">
              <span slot="label">
                周期额度
                <el-tooltip placement="top">
                  <div slot="content">
                    周期额度（无单位限制，根据奖品设置的额度进行计算，例如周期是月，额度设置的100，奖品额度设置的1，则每月最多发放100个奖品），0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.missionPeriodQuota" placeholder="请输入任务周期额度" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入排序,从小到大排序" />
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
        </el-row>
        <el-row>
          <el-divider>用户可获奖励额度设置</el-divider>
          <el-col :span="8">
            <el-form-item label="累计可获" prop="userTotalQuota">
              <span slot="label">
                累计可获
                <el-tooltip placement="top">
                  <div slot="content">
                    用户在任务期间累计最多可获多少奖励，0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userTotalQuota" placeholder="请输入用户累计可获奖励额度" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="周期可获" prop="userPeriodQuota">
              <span slot="label">
                周期可获
                <el-tooltip placement="top">
                  <div slot="content">
                    用户在任务刷新周期最多可获多少奖励，0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userPeriodQuota" placeholder="请输入用户任务周期可获奖励" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-divider>用户可获得奖励次数设置</el-divider>
          <el-col :span="8">
            <el-form-item label="每日次数" prop="userCountDay">
              <span slot="label">
                每日次数
                <el-tooltip placement="top">
                  <div slot="content">
                    用户每日可获奖励次数（例如：每天可获得三次抽奖机会，注意不是额度，是次数），0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountDay" placeholder="请输入用户每日限参与次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每周次数" prop="userCountWeek">
              <span slot="label">
                每周次数
                <el-tooltip placement="top">
                  <div slot="content">
                    用户每周可获奖励次数（例如：每周可获得三次抽奖机会，注意不是额度，是次数），0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountWeek" placeholder="请输入用户每周限参与次数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每月次数" prop="userCountMonth">
              <span slot="label">
                每月次数
                <el-tooltip placement="top">
                  <div slot="content">
                    用户每月可获奖励次数（例如：每月可获得三次抽奖机会，注意不是额度，是次数），0为不限制上限
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.userCountMonth" placeholder="请输入用户每月限参与次数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-divider>展示设置</el-divider>
          <el-col :span="8">
            <el-form-item label="展示首页" prop="showIndex">
              <el-select v-model="form.showIndex" style="width: 100%;" placeholder="请选择展示首页">
                <el-option v-for="dict in dict.type.mission_show_index" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示开始时间" prop="showStartDate">
              <el-date-picker clearable v-model="form.showStartDate" type="datetime" style="width: 100%;"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示结束时间" prop="showEndDate">
              <el-date-picker clearable v-model="form.showEndDate" type="datetime" style="width: 100%;"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择展示结束时间" default-time="23:59:59">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示城市" prop="showCity">
              <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
              <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="指定周几" prop="assignDate">
              <el-select v-model="form.assignDate" placeholder="请选择指定周几" style="width: 100%;">
                <el-option v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.assignDate == '1'">
            <el-form-item label="周几能领" prop="weekDate">
              <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示内容类型" prop="showContentType">
              <el-select v-model="form.showContentType" style="width: 100%;" placeholder="请选择展示内容类型">
                <el-option v-for="dict in dict.type.show_content_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="展示内容" v-if="form.showContentType == '0'">
              <image-upload :limit="1" :isShowTip="false" v-model="form.showContent"></image-upload>
            </el-form-item>
            <el-form-item label="展示内容" v-else>
              <el-input v-model="form.showContent" placeholder="请输入展示内容" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="跳转类型" prop="toType">
              <el-select v-model="form.toType" placeholder="请选择跳转类型" style="width: 100%;">
                <el-option v-for="dict in dict.type.t_product_to_type" :key="dict.value" :label="dict.label"
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
          <el-col :span="8" v-if="form.toType == '4'">
            <el-form-item label="页面地址" prop="url">
              <image-upload :isShowTip="false" v-model="form.url" :limit="1" />
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
    listMission,
    getMission,
    delMission,
    addMission,
    updateMission
  } from "@/api/zlyyh/mission";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"
  import {
    listMissionGroupSelect
  } from "@/api/zlyyh/missionGroup"
  import {
    treeselect as cityTreeselect,
    productShowCityTreeSelect,
    selectCityList
  } from "@/api/zlyyh/area"

  export default {
    name: "Mission",
    dicts: ['award_expiry_type', 'mission_award_type', 'sys_normal_disable', 'period_type', 'mission_show_index',
      'show_content_type', 'sys_normal_disable', 't_product_to_type', 't_product_assign_date', 't_mission_type',
      't_grad_period_date_list', 't_mission_affiliation', 'channel_type'
    ],
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
        // 任务配置表格数据
        missionList: [],
        missionGroupList: [],
        cityNodeAll: false,
        cityList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'mission_id',
          isAsc: 'desc',
          missionGroupId: undefined,
          missionName: undefined,
          status: undefined,
          platformKey: undefined,
          missionTime: undefined
        },
        // 表单参数
        form: {},
        platformList: [],
        //城市列表
        cityOptions: [],
        defaultProps: {
          children: "children",
          label: "label"
        },
        // 表单校验
        rules: {
          missionId: [{
            required: true,
            message: "任务ID不能为空",
            trigger: "blur"
          }],
          missionGroupId: [{
            required: true,
            message: "任务组ID不能为空",
            trigger: "blur"
          }],
          missionName: [{
            required: true,
            message: "任务名称不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          startDate: [{
            required: true,
            message: "开始时间不能为空",
            trigger: "blur"
          }],
          endDate: [{
            required: true,
            message: "结束时间不能为空",
            trigger: "blur"
          }],
          periodType: [{
            required: true,
            message: "任务刷新周期不能为空",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          missionType: [{
            required: true,
            message: "任务类型不能为空",
            trigger: "blur"
          }],
          missionAffiliation: [{
            required: true,
            message: "任务归属不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      selectListPlatform({}).then(res => {
        this.platformList = res.data;
      })
      listMissionGroupSelect({}).then(res => {
        this.missionGroupList = res.data;
      })
      this.getCityTreeselect();
    },
    methods: {
      selectAll(val) {
        if (this.cityNodeAll) {
          // 	设置目前勾选的节点，使用此方法必须设置 node-key 属性
          this.$refs.city.setCheckedNodes([])
        } else {
          // 全部不选中
          this.$refs.city.setCheckedNodes([])
        }
      },
      handleNodeClick(data) {
        this.cityNodeAll = false;
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
      /** 查询任务配置列表 */
      getList() {
        this.loading = true;
        listMission(this.queryParams).then(response => {
          this.missionList = response.rows;
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
          missionId: undefined,
          missionGroupId: undefined,
          missionName: undefined,
          missionUpid: undefined,
          status: '0',
          startDate: undefined,
          endDate: undefined,
          periodType: '0',
          missionImg: undefined,
          missionAwardType: undefined,
          awardExpiryType: undefined,
          awardExpiryDate: undefined,
          missionTotalQuota: undefined,
          missionPeriodQuota: undefined,
          userTotalQuota: undefined,
          userPeriodQuota: undefined,
          userCountDay: undefined,
          userCountWeek: undefined,
          userCountMonth: undefined,
          platformKey: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined,
          showIndex: undefined,
          showCity: undefined,
          showStartDate: undefined,
          showEndDate: undefined,
          showContentType: undefined,
          showContent: undefined,
          assignDate: undefined,
          weekDate: undefined,
          toType: undefined,
          appId: undefined,
          url: undefined,
          sort: undefined,
          supportChannel: ['ALL'],
          missionTime: undefined
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
        this.ids = selection.map(item => item.missionId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      getCityTreeselect() {
        cityTreeselect().then(response => {
          this.cityOptions = response.data;
        });
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加任务配置";
        this.cityNodeAll = true;
        this.$nextTick(() => {
          this.cityOptions.forEach((v) => {
            this.$nextTick(() => {
              this.$refs.city.setChecked(v, false, true);
            })
          })
        })
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const missionId = row.missionId || this.ids
        getMission(missionId).then(response => {
          this.loading = false;
          this.form = response.data;
          if (this.form && this.form.weekDate) {
            this.form.weekDate = this.form.weekDate.split(",");
          }
          if (response.data.supportChannel) {
            this.form.supportChannel = response.data.supportChannel.split(",")
          } else {
            this.form.supportChannel = ['ALL']
          }
          if (this.form && this.form.missionTime) {
            this.form.missionTime = this.form.missionTime.split("-")
          }
          if (this.form.showCity == 'ALL') {
            this.cityNodeAll = true;
            this.$nextTick(() => {
              this.cityOptions.forEach((v) => {
                this.$nextTick(() => {
                  this.$refs.city.setChecked(v, false, true);
                })
              })
            })
          } else {
            this.$nextTick(() => {
              this.cityNodeAll = false;
              this.cityOptions.forEach((v) => {
                this.$nextTick(() => {
                  this.$refs.city.setChecked(v, false, true);
                })
              })
              let cityIds = this.form.showCity.split(',')
              cityIds.forEach((v) => {
                this.$nextTick(() => {
                  this.$refs.city.setChecked(v, true, false);
                })
              })
            })
          }
          this.open = true;
          this.title = "修改任务配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.weekDate) {
              this.form.weekDate = this.form.weekDate.toString();
            }
            if (this.form.missionTime) {
              this.form.missionTime = this.form.missionTime[0] + "-" + this.form.missionTime[1];
            }
            if (this.cityNodeAll) {
              this.form.showCity = "ALL";
            } else {
              this.form.showCity = this.getCityAllCheckedKeys().toString();
            }
            if (this.form.supportChannel && this.form.supportChannel.length > 0) {
              this.form.supportChannel = this.form.supportChannel.join(",")
            } else {
              this.form.supportChannel = 'ALL'
            }
            if (this.form.missionId != null) {
              updateMission(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMission(this.form).then(response => {
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
      //所有菜单节点数据
      getCityAllCheckedKeys() {
        //目前被选中的城市节点
        let checkedKeys = this.$refs.city.getCheckedKeys();
        //半选中的城市节点
        let halfCheckedKeys = this.$refs.city.getHalfCheckedKeys();
        checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
        return checkedKeys;
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const missionIds = row.missionId || this.ids;
        this.$modal.confirm('是否确认删除任务配置编号为"' + missionIds + '"的数据项？').then(() => {
          this.loading = true;
          return delMission(missionIds);
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
        this.download('zlyyh-admin/mission/export', {
          ...this.queryParams
        }, `mission_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>

