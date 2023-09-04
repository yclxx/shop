<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="门店组" prop="shopGroupName">
        <el-input v-model="queryParams.shopGroupName" placeholder="请输入门店组名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_shop_group_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
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
          v-hasPermi="['zlyyh:shopGroup:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:shopGroup:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:shopGroup:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:shopGroup:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopGroupList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="shopGroupId" v-if="true" />
      <el-table-column label="门店组名称" align="center" prop="shopGroupName" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_shop_group_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:shopGroup:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:shopGroup:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改门店组配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="门店组" prop="shopGroupName">
          <el-input v-model="form.shopGroupName" placeholder="请输入门店组名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.t_shop_group_status" :key="dict.value" :label="dict.value">
              {{dict.label}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="平台" prop="platformKey">
          <el-select v-model="form.platformKey" placeholder="请选择平台标识" clearable filterable>
            <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店" prop="shopIds">
          <el-select v-model="form.shopIds" placeholder="请选择门店" clearable multiple filterable>
            <el-option v-for="item in shopList" :key="item.id" :label="item.label" :value="item.id" />
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
  import {
    listShopGroup,
    getShopGroup,
    delShopGroup,
    addShopGroup,
    updateShopGroup
  } from "@/api/zlyyh/shopGroup";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    selectShopList
  } from "@/api/zlyyh/shop";

  export default {
    name: "ShopGroup",
    dicts: ['t_shop_group_status'],
    data() {
      return {
        //平台下拉列表
        platformList: [],
        //门店下拉列表
        shopList: [],
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
        // 门店组配置表格数据
        shopGroupList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          shopGroupName: undefined,
          status: undefined,
          platformKey: undefined
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          shopGroupId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          shopGroupName: [{
            required: true,
            message: "门店组名称不能为空",
            trigger: "blur"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          platformKey: [{
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }],
          shopId: [{
            required: true,
            message: "门店不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {
      this.getList();
      this.getPlatformSelectList();
      this.getShopSelectList();
    },
    methods: {
      /** 查询门店组配置列表 */
      getList() {
        this.loading = true;
        listShopGroup(this.queryParams).then(response => {
          this.shopGroupList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      //门店下拉列表
      getShopSelectList() {
        selectShopList({}).then(response => {
          this.shopList = response.data;
        });
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
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          shopGroupId: undefined,
          shopGroupName: undefined,
          status: '0',
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          platformKey: undefined,
          shopIds: undefined,
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
        this.ids = selection.map(item => item.shopGroupId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加门店组配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const shopGroupId = row.shopGroupId || this.ids
        getShopGroup(shopGroupId).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改门店组配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.shopGroupId != null) {
              updateShopGroup(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addShopGroup(this.form).then(response => {
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
        const shopGroupIds = row.shopGroupId || this.ids;
        this.$modal.confirm('是否确认删除门店组配置编号为"' + shopGroupIds + '"的数据项？').then(() => {
          this.loading = true;
          return delShopGroup(shopGroupIds);
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
        this.download('zlyyh-admin/shopGroup/export', {
          ...this.queryParams
        }, `shopGroup_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>