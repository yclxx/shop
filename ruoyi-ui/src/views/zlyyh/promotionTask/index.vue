<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!--<el-form-item label="平台标识" prop="platformKey">-->
      <!--  <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>-->
      <!--    <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>-->
      <!--  </el-select>-->
      <!--</el-form-item>-->
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开始时间" prop="startDate">
        <el-date-picker clearable
                        v-model="queryParams.startDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate">
        <el-date-picker clearable
                        v-model="queryParams.endDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="活动性质" prop="activityNature">
        <el-select v-model="queryParams.activityNature" placeholder="请选择活动性质" clearable>
          <el-option
            v-for="dict in dict.type.activity_nature"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['zlyyh:promotionTask:add']"
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
          v-hasPermi="['zlyyh:promotionTask:edit']"
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
          v-hasPermi="['zlyyh:promotionTask:remove']"
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
          v-hasPermi="['zlyyh:promotionTask:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="promotionTaskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="唯一标识" align="center" prop="taskId"/>
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="任务名称" align="center" prop="taskName"/>
      <el-table-column label="开始时间" align="center" prop="startDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活动性质" align="center" prop="activityNature">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.activity_nature" :value="scope.row.activityNature"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="规则图片" align="center" prop="ruleImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.ruleImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="推广图片" align="center" prop="image" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.image" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="背景图片" align="center" prop="backgroundImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.backgroundImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="展示城市" align="center" prop="showCity"/>
      <el-table-column label="排序" align="center" prop="sort"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:promotionTask:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:promotionTask:remove']"
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

    <!-- 添加或修改推广任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台标识" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName">
              <el-input v-model="form.taskName" placeholder="请输入任务名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startDate">
              <el-date-picker clearable
                              v-model="form.startDate"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endDate">
              <el-date-picker clearable
                              v-model="form.endDate"
                              type="datetime"
                              value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="请选择结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="活动性质" prop="activityNature">
              <el-radio-group v-model="form.activityNature">
                <el-radio
                  v-for="dict in dict.type.activity_nature"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入排序"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则图片" prop="ruleImage">
              <image-upload v-model="form.ruleImage"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="推广图片" prop="image">
              <image-upload v-model="form.image"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="背景图片" prop="backgroundImage">
              <image-upload v-model="form.backgroundImage"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="展示城市" prop="showCity">
            <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
            <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                     ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
            <!--<el-input v-model="form.showCity" placeholder="请输入展示城市"/>-->
          </el-form-item>
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
  listPromotionTask,
  getPromotionTask,
  delPromotionTask,
  addPromotionTask,
  updatePromotionTask
} from "@/api/zlyyh/promotionTask";
import {selectListPlatform} from "@/api/zlyyh/platform";
import {treeselect as cityTreeselect} from "@/api/zlyyh/area";

export default {
  name: "PromotionTask",
  dicts: ['sys_normal_disable', 'activity_nature'],
  data() {
    return {
      cityNodeAll: false,
      //城市列表
      cityOptions: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
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
      // 推广任务表格数据
      promotionTaskList: [],
      platformList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformKey: undefined,
        taskName: undefined,
        startDate: undefined,
        endDate: undefined,
        activityNature: undefined,
        status: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        taskId: [
          {required: true, message: "id不能为空", trigger: "blur"}
        ],
        platformKey: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ],
        taskName: [
          {required: true, message: "任务名称不能为空", trigger: "blur"}
        ],
        startDate: [
          {required: true, message: "开始时间不能为空", trigger: "blur"}
        ],
        endDate: [
          {required: true, message: "结束时间不能为空", trigger: "blur"}
        ],
        activityNature: [
          {required: true, message: "活动性质不能为空", trigger: "change"}
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "change"}
        ],
        ruleImage: [
          {required: true, message: "规则图片不能为空", trigger: "blur"}
        ],
        image: [
          {required: true, message: "推广图片不能为空", trigger: "blur"}
        ],
        backgroundImage: [
          {required: true, message: "背景图片不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
    this.getCityTreeselect();
  },
  methods: {
    getCityTreeselect() {
      cityTreeselect().then(response => {
        this.cityOptions = response.data;
      });
    },
    handleNodeClick(data) {
      this.cityNodeAll = false;
    },
    // 全选/不全选
    selectAll(val) {
      if (this.cityNodeAll) {
        // 	设置目前勾选的节点，使用此方法必须设置 node-key 属性
        this.$refs.city.setCheckedNodes(this.cityOptions);
      } else {
        // 全部不选中
        this.$refs.city.setCheckedNodes([])
      }
    },
    //平台标识下拉列表
    getPlatformSelectList() {
      selectListPlatform({}).then(response => {
        this.platformList = response.data;
      });
    },
    platformFormatter(row) {
      let name = '';
      this.platformList.forEach(item => {
        if (item.id == row.platformKey) {
          name = item.label;
        }
      })
      if (name && name.length > 0) {
        row.platformName = name;
        return name;
      }
      return row.platformKey;
    },
    /** 查询推广任务列表 */
    getList() {
      this.loading = true;
      listPromotionTask(this.queryParams).then(response => {
        this.promotionTaskList = response.rows;
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
        taskId: undefined,
        platformKey: undefined,
        taskName: undefined,
        startDate: undefined,
        endDate: undefined,
        activityNature: undefined,
        status: '0',
        ruleImage: undefined,
        image: undefined,
        backgroundImage: undefined,
        showCity: undefined,
        sort: 99,
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.taskId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.cityNodeAll = true;
      this.$nextTick(() => {
        this.cityOptions.forEach((v) => {
          this.$nextTick(() => {
            this.$refs.city.setChecked(v, false, true);
          })
        })
      })
      this.title = "添加推广任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const taskId = row.taskId || this.ids
      getPromotionTask(taskId).then(response => {
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
        this.title = "修改推广任务";
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
          console.log(this.cityOptions)
          console.log(this.cityNodeAll)
          this.buttonLoading = true;
          if (this.cityNodeAll) {
            this.form.showCity = "ALL";
          } else {
            this.form.showCity = this.getCityAllCheckedKeys().toString();
          }
          if (this.form.taskId != null) {
            updatePromotionTask(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addPromotionTask(this.form).then(response => {
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
      const taskIds = row.taskId || this.ids;
      this.$modal.confirm('是否确认删除推广任务编号为"' + taskIds + '"的数据项？').then(() => {
        this.loading = true;
        return delPromotionTask(taskIds);
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
      this.download('zlyyh-admin/promotionTask/export', {
        ...this.queryParams
      }, `promotionTask_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
