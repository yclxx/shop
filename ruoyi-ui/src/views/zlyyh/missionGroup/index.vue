<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="组名称" prop="missionGroupName">
        <el-input v-model="queryParams.missionGroupName" placeholder="请输入任务组名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
          <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
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
          v-hasPermi="['zlyyh:missionGroup:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:missionGroup:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:missionGroup:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:missionGroup:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionGroupList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" width="190" align="center" prop="missionGroupId" v-if="true" />
      <el-table-column label="组名称" align="center" prop="missionGroupName" />
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
      <el-table-column label="任务组编号" min-width="100" align="center" prop="missionGroupUpid" />
      <el-table-column label="积点兑商品" width="100" align="center" prop="jdConvertRedPacket">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jd_convert_red_packet" :value="scope.row.jdConvertRedPacket" />
        </template>
      </el-table-column>
      <el-table-column label="平台标识" min-width="100" align="center" prop="platformKey" :formatter="changePlatform" />
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
      <el-table-column label="规则图片" align="center" prop="missionImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.missionImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="背景图片" align="center" prop="missionBgImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.missionBgImg" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="状态" fixed="right" width="68" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:missionGroup:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:missionGroup:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务组对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务组名称" prop="missionGroupName">
              <el-input v-model="form.missionGroupName" placeholder="请输入任务组名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务组编号" prop="missionGroupUpid">
              <span slot="label">
                任务组编号
                <el-tooltip content="银联任务组编号" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.missionGroupUpid" placeholder="请输入银联任务组编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="活动城市" prop="showCity">
              <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
              <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
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
                default-time="23:59:59" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规则图片" prop="missionImg">
              <image-upload :limit="1" :isShowTip="false" v-model="form.missionImg" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="默认背景" prop="missionBgImg">
              <image-upload :limit="1" :isShowTip="false" v-model="form.missionBgImg" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="积点兑商品" prop="jdConvertRedPacket">
              <span slot="label">
                积点兑商品
                <el-tooltip content="活动获得的积点是否可以兑换商品" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-radio-group v-model="form.jdConvertRedPacket">
                <el-radio v-for="dict in dict.type.jd_convert_red_packet" :key="dict.value"
                  :label="dict.value">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="兑换金额" prop="convertValue">
              <el-input v-model="form.convertValue" placeholder="请输入兑换金额上限" />
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
    listMissionGroup,
    getMissionGroup,
    delMissionGroup,
    addMissionGroup,
    updateMissionGroup
  } from "@/api/zlyyh/missionGroup";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform"
  import {
    treeselect as cityTreeselect,
    productShowCityTreeSelect,
    selectCityList
  } from "@/api/zlyyh/area"

  export default {
    name: "MissionGroup",
    dicts: ['sys_normal_disable', 'jd_convert_red_packet'],
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
        // 任务组表格数据
        missionGroupList: [],
        platformList: [],
        cityNodeAll: false,
        cityList: [],
        //城市列表
        cityOptions: [],
        defaultProps: {
          children: "children",
          label: "label"
        },
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'mission_group_id',
          isAsc: 'desc',
          missionGroupName: undefined,
          status: undefined,
          platformKey: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          missionGroupId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          missionGroupName: [{
            required: true,
            message: "任务组名称不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
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
      this.getCityTreeselect();
    },
    methods: {
      getCityTreeselect() {
        cityTreeselect().then(response => {
          this.cityOptions = response.data;
        });
      },
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
      /** 查询任务组列表 */
      getList() {
        this.loading = true;
        listMissionGroup(this.queryParams).then(response => {
          this.missionGroupList = response.rows;
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
          missionGroupId: undefined,
          missionGroupName: undefined,
          status: '0',
          startDate: undefined,
          endDate: undefined,
          missionImg: undefined,
          missionBgImg: undefined,
          missionGroupUpid: undefined,
          jdConvertRedPacket: '0',
          platformKey: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined,
          showCity: undefined
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
        this.ids = selection.map(item => item.missionGroupId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加任务组";
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
        const missionGroupId = row.missionGroupId || this.ids
        getMissionGroup(missionGroupId).then(response => {
          this.loading = false;
          this.form = response.data;
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
          this.title = "修改任务组";
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
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.cityNodeAll) {
              this.form.showCity = "ALL";
            } else {
              this.form.showCity = this.getCityAllCheckedKeys().toString();
            }
            if (this.form.missionGroupId != null) {
              updateMissionGroup(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMissionGroup(this.form).then(response => {
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
        const missionGroupIds = row.missionGroupId || this.ids;
        this.$modal.confirm('是否确认删除任务组编号为"' + missionGroupIds + '"的数据项？').then(() => {
          this.loading = true;
          return delMissionGroup(missionGroupIds);
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
        this.download('zlyyh-admin/missionGroup/export', {
          ...this.queryParams
        }, `missionGroup_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
