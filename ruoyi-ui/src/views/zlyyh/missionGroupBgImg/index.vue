<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable>
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.t_shop_status"
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
          v-hasPermi="['zlyyh:missionGroupBgImg:add']"
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
          v-hasPermi="['zlyyh:missionGroupBgImg:edit']"
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
          v-hasPermi="['zlyyh:missionGroupBgImg:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zlyyh:missionGroupBgImg:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionGroupBgImgList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="missionBgImgId" />
      <el-table-column label="任务组" align="center" min-width="100" prop="missionGroupId"
                       :formatter="formatterMissionGroup" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_shop_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务图片" align="center" prop="missionBgImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.missionBgImg" :width="50" :height="50" />
        </template>
      </el-table-column>
<!--      <el-table-column label="任务图片" align="center" prop="missionBgImg" />-->
      <el-table-column label="平台" align="center" min-width="100" prop="platformKey" :formatter="changePlatform" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:missionGroupBgImg:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:missionGroupBgImg:remove']"
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

    <!-- 添加或修改任务组背景图片配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务组" prop="missionGroupId">
          <el-select v-model="form.missionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
            <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id"
                       :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.t_shop_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始时间" prop="startDate">
          <el-date-picker clearable
            v-model="form.startDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endDate">
          <el-date-picker clearable
            v-model="form.endDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="任务图片" prop="missionBgImg">
          <image-upload :limit="1" :isShowTip="false" v-model="form.missionBgImg"></image-upload>
        </el-form-item>

        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台" filterable clearable style="width: 100%;">
            <el-option v-for="item in platformList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
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
import { listMissionGroupBgImg, getMissionGroupBgImg, delMissionGroupBgImg, addMissionGroupBgImg, updateMissionGroupBgImg } from "@/api/zlyyh/missionGroupBgImg";
import {
  selectListPlatform
} from "@/api/zlyyh/platform"
import {
  listMissionGroupSelect
} from "@/api/zlyyh/missionGroup"
export default {
  name: "MissionGroupBgImg",
  dicts: ['t_shop_status'],
  data() {
    return {
      missionGroupList: [],
      platformList: [],
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
      // 任务组背景图片配置表格数据
      missionGroupBgImgList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        missionBgImgId: undefined,
        status: undefined,
        startDate: undefined,
        endDate: undefined,
        missionBgImg: undefined,
        platformKey: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        missionGroupId: [
          { required: true, message: "ID不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
        startDate: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        endDate: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
        missionBgImg: [
          { required: true, message: "任务图片不能为空", trigger: "blur" }
        ],
        platformKey: [
          { required: true, message: "平台标识不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    selectListPlatform({}).then(res => {
      this.platformList = res.data;
    })
    listMissionGroupSelect({}).then(res => {
      this.missionGroupList = res.data;
    })
    this.getList();
  },
  methods: {
    /** 查询任务组背景图片配置列表 */
    getList() {
      this.loading = true;
      listMissionGroupBgImg(this.queryParams).then(response => {
        this.missionGroupBgImgList = response.rows;
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
        missionBgImgId: undefined,
        missionGroupId: undefined,
        status: undefined,
        startDate: undefined,
        endDate: undefined,
        missionBgImg: undefined,
        platformKey: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        delFlag: undefined
      };
      this.resetForm("form");
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加任务组背景图片";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const missionBgImgId = row.missionBgImgId || this.ids
      getMissionGroupBgImg(missionBgImgId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改任务组背景图片";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.missionBgImgId != null) {
            updateMissionGroupBgImg(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addMissionGroupBgImg(this.form).then(response => {
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
      const missionBgImgIds = row.missionBgImgId || this.ids;
      this.$modal.confirm('是否确认删除任务组背景图片编号为"' + missionBgImgIds + '"的数据项？').then(() => {
        this.loading = true;
        return delMissionGroupBgImg(missionBgImgIds);
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
      this.download('zlyyh/missionGroupBgImg/export', {
        ...this.queryParams
      }, `missionGroupBgImg_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
