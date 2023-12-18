<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
             label-width="68px">
      <el-form-item label="平台标识" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支持端" prop="channel">
        <el-select v-model="queryParams.channel" placeholder="请选择支持端" clearable>
          <el-option
            v-for="dict in dict.type.channel_type"
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
          v-hasPermi="['zlyyh:marketActivity:add']"
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
          v-hasPermi="['zlyyh:marketActivity:edit']"
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
          v-hasPermi="['zlyyh:marketActivity:remove']"
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
          v-hasPermi="['zlyyh:marketActivity:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="marketActivityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="id" align="center" prop="activityId" width="100"/>
      <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
      <el-table-column label="名称" align="center" prop="name"/>
      <el-table-column label="图片" align="center" prop="image" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.image" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="规则图片" align="center" prop="ruleImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.ruleImg" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="详情图片" align="center" prop="detailsImg" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.detailsImg" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="支持端" align="center" prop="channel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type" :value="scope.row.channel"/>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:marketActivity:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:marketActivity:remove']"
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

    <!-- 添加或修改营销活动对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台标识" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台标识"
                         @change="getPlatformSelectList"
                         clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label"
                           :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="支持端" prop="channel">
              <el-select v-model="form.channel" placeholder="请选择支持端">
                <el-option
                  v-for="dict in dict.type.channel_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="图片" prop="image">
              <image-upload v-model="form.image"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则图片" prop="ruleImg">
              <image-upload v-model="form.ruleImg"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="详情图片" prop="detailsImg">
              <image-upload v-model="form.detailsImg"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入名称"/>
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
  addMarketActivity,
  delMarketActivity,
  getMarketActivity,
  listMarketActivity,
  updateMarketActivity
} from "@/api/zlyyh/marketActivity";
import {selectListPlatform} from "@/api/zlyyh/platform";

export default {
  name: "MarketActivity",
  dicts: ['channel_type'],
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
      // 营销活动表格数据
      marketActivityList: [],
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
        name: undefined,
        image: undefined,
        ruleImg: undefined,
        detailsImg: undefined,
        channel: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        activityId: [
          {required: true, message: "不能为空", trigger: "blur"}
        ],
        platformKey: [
          {required: true, message: "平台标识不能为空", trigger: "blur"}
        ],
        name: [
          {required: true, message: "名称不能为空", trigger: "blur"}
        ],
        channel: [
          {required: true, message: "支持端不能为空", trigger: "change"}
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getPlatformSelectList();
  },
  methods: {
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
    /** 查询营销活动列表 */
    getList() {
      this.loading = true;
      listMarketActivity(this.queryParams).then(response => {
        this.marketActivityList = response.rows;
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
        activityId: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        platformKey: undefined,
        name: undefined,
        image: undefined,
        ruleImg: undefined,
        detailsImg: undefined,
        channel: undefined,
        sort: 99,
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
      this.ids = selection.map(item => item.activityId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加营销活动";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const activityId = row.activityId || this.ids
      getMarketActivity(activityId).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改营销活动";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.activityId != null) {
            updateMarketActivity(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addMarketActivity(this.form).then(response => {
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
      const activityIds = row.activityId || this.ids;
      this.$modal.confirm('是否确认删除营销活动编号为"' + activityIds + '"的数据项？').then(() => {
        this.loading = true;
        return delMarketActivity(activityIds);
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
      this.download('zlyyh/marketActivity/export', {
        ...this.queryParams
      }, `marketActivity_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
