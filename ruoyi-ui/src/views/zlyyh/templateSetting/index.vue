<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="落地页" prop="templateId">
        <el-select style="width: 100%;" clearable v-model="queryParams.templateId" placeholder="请选择落地页">
          <el-option v-for="item in templatePageList" :key="item.id" :label="item.label" :value="item.id">
            <span style="float: left">{{ item.label }}</span>
          </el-option>
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
          v-hasPermi="['zlyyh:templateSetting:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:templateSetting:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:templateSetting:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:templateSetting:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateSettingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" width="180" align="center" prop="id" v-if="true" />
      <el-table-column label="落地页" align="center" prop="templateId" :formatter="formatterTemplatePage" />
      <el-table-column label="图片" align="center" prop="img" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.img" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="是否按钮" align="center" prop="btn">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_template_setting_btn" :value="scope.row.btn" />
        </template>
      </el-table-column>
      <el-table-column label="跳转类型" align="center" prop="toType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_template_setting_to_type" :value="scope.row.toType" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="图片宽度" align="center" prop="width" />
      <el-table-column label="父级id" align="center" prop="parentId" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:templateSetting:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:templateSetting:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改落地页配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="落地页" prop="templateId">
          <el-select style="width: 100%;" v-model="form.templateId" placeholder="请选择落地页">
            <el-option v-for="item in templatePageList" :key="item.id" :label="item.label" :value="item.id">
              <span style="float: left">{{ item.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图片" prop="img">
          <image-upload :limit="1" v-model="form.img" />
        </el-form-item>
        <el-form-item label="是否按钮" prop="btn">
          <el-radio-group v-model="form.btn">
            <el-radio v-for="dict in dict.type.t_template_setting_btn" :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <div v-if="form.btn == '1'">
          <el-form-item label="跳转类型" prop="toType">
            <el-select style="width: 100%;" v-model="form.toType" placeholder="请选择跳转类型">
              <el-option v-for="dict in dict.type.t_template_setting_to_type" :key="dict.value" :label="dict.label"
                :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
          <div v-if="form.toType == '3'">
            <el-form-item label="小程序ID" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入小程序ID" />
            </el-form-item>
          </div>
          <div v-if="form.toType != '4'">
            <el-form-item label="商品" v-if="form.toType == '0'">
              <el-select style="width: 100%;" v-model="form.url" placeholder="请选择商品">
                <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
                  <span style="float: left">{{ item.label }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="页面地址" v-else-if="form.toType == '3'">
              <el-input v-model="form.url" placeholder="请输入内容" />
            </el-form-item>
            <el-form-item label="页面地址" v-else prop="url">
              <el-input v-model="form.url" placeholder="请输入内容" />
            </el-form-item>
          </div>
          <div v-if="form.toType == '4'">
            <el-form-item label="图片页面" prop="url">
              <image-upload :limit="1" v-model="form.url" />
            </el-form-item>
          </div>
        </div>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
        </el-form-item>
        <el-form-item label="图片宽度" prop="width">
          <span slot="label">
            图片宽度
            <el-tooltip placement="top">
              <div slot="content">图片所占比例</div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="form.width" placeholder="请输入图片宽度">
            <template slot="append">%</template>
          </el-input>
        </el-form-item>
        <el-form-item label="父级id" prop="parentId">
          <el-input v-model="form.parentId" placeholder="请输入父级id" />
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
    listTemplateSetting,
    getTemplateSetting,
    delTemplateSetting,
    addTemplateSetting,
    updateTemplateSetting
  } from "@/api/zlyyh/templateSetting";
  import {
    selectListTemplatePage,
  } from "@/api/zlyyh/templatePage";
  import {
    selectListProduct
  } from "@/api/zlyyh/product";

  export default {
    name: "TemplateSetting",
    dicts: ['t_template_setting_btn', 't_template_setting_to_type'],
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
        // 落地页配置表格数据
        templateSettingList: [],
        templatePageList: [],
        //商品下拉列表
        productList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'template_id,sort',
          isAsc: 'desc,asc',
          templateId: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          id: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          templateId: [{
            required: true,
            message: "落地页不能为空",
            trigger: "blur"
          }],
          // img: [{
          //   required: true,
          //   message: "图片不能为空",
          //   trigger: "blur"
          // }],
          toType: [{
            required: true,
            message: "跳转类型不能为空",
            trigger: "blur"
          }],
          appId: [{
            required: true,
            message: "小程序ID不能为空",
            trigger: "blur"
          }],
          url: [{
            required: true,
            message: "页面地址不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      selectListTemplatePage({
        status: '0'
      }).then(response => {
        this.templatePageList = response.data;
      })
      //商品下拉列表
      selectListProduct({
        status: '0'
      }).then(response => {
        this.productList = response.data;
      });
    },
    methods: {
      formatterTemplatePage(row) {
        let name = ''
        this.templatePageList.forEach(item => {
          if (row.templateId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          return name;
        }
        return row.templateId;
      },
      /** 查询落地页配置列表 */
      getList() {
        this.loading = true;
        listTemplateSetting(this.queryParams).then(response => {
          this.templateSettingList = response.rows;
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
          templateId: undefined,
          img: undefined,
          btn: '0',
          toType: undefined,
          appId: undefined,
          url: undefined,
          sort: undefined,
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
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加落地页配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getTemplateSetting(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改落地页配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateTemplateSetting(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addTemplateSetting(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除落地页配置编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delTemplateSetting(ids);
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
        this.download('zlyyh-admin/templateSetting/export', {
          ...this.queryParams
        }, `templateSetting_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>