<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务组" prop="missionGroupId">
        <el-select v-model="queryParams.missionGroupId" placeholder="请选择任务组" filterable clearable>
          <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select style="width: 100%;" v-model="queryParams.productId" placeholder="请选择商品" clearable>
          <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
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
          v-hasPermi="['zlyyh:missionGroupProduct:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:missionGroupProduct:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:missionGroupProduct:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:missionGroupProduct:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="missionGroupProductList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" width="190" prop="id" v-if="true" />
      <el-table-column label="任务组" align="center" min-width="100" prop="missionGroupId"
        :formatter="formatterMissionGroup" />
      <el-table-column label="任务" align="center" min-width="100" prop="missionId" :formatter="formatterMission" />
      <el-table-column label="商品" align="center" prop="productId" :formatter="formatterProduct" />
      <el-table-column label="排序" width="68" align="center" prop="sort" />
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
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:missionGroupProduct:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:missionGroupProduct:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改任务组可兑换商品配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务组" prop="missionGroupId">
          <el-select v-model="form.missionGroupId" placeholder="请选择任务组" filterable clearable style="width: 100%;">
            <el-option v-for="item in missionGroupList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务" prop="missionId">
          <el-select v-model="form.missionId" placeholder="请选择任务" filterable clearable style="width: 100%;">
            <el-option v-for="item in missionList" :key="item.id" :value="item.id" :label="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" />
          <!-- <el-select style="width: 100%;" v-model="form.productId" placeholder="请选择商品">
            <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">
              <span style="float: left">{{ item.label }}</span>
            </el-option>
          </el-select> -->
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序" />
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
    listMissionGroupProduct,
    getMissionGroupProduct,
    delMissionGroupProduct,
    addMissionGroupProduct,
    updateMissionGroupProduct
  } from "@/api/zlyyh/missionGroupProduct";
  import {
    listMissionGroupSelect
  } from "@/api/zlyyh/missionGroup"
  import {
    listMissionSelect
  } from "@/api/zlyyh/mission"
  import {
    selectListProduct
  } from "@/api/zlyyh/product";

  export default {
    name: "MissionGroupProduct",
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
        // 任务组可兑换商品配置表格数据
        missionGroupProductList: [],
        missionGroupList: [],
        missionList: [],
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
          orderByColumn: 'id',
          isAsc: 'desc',
          missionGroupId: undefined,
          productId: undefined,
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
          missionGroupId: [{
            required: true,
            message: "任务组ID不能为空",
            trigger: "blur"
          }],
          productId: [{
            required: true,
            message: "商品ID不能为空",
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
      // //商品下拉列表
      // selectListProduct({
      //   status: '0'
      // }).then(response => {
      //   this.productList = response.data;
      // });
    },
    methods: {
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
      formatterProduct(row) {
        let name = ''
        this.productList.forEach(item => {
          if (row.productId == item.id) {
            name = item.label;
          }
        })
        if (name && name.length > 0) {
          row.name = name;
          return name;
        }
        return row.productId;
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
      /** 查询任务组可兑换商品配置列表 */
      getList() {
        this.loading = true;
        listMissionGroupProduct(this.queryParams).then(response => {
          this.missionGroupProductList = response.rows;
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
          missionGroupId: undefined,
          missionId: undefined,
          productId: undefined,
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
        this.title = "添加任务组可兑换商品配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const id = row.id || this.ids
        getMissionGroupProduct(id).then(response => {
          this.loading = false;
          this.form = response.data;
          this.open = true;
          this.title = "修改任务组可兑换商品配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            if (this.form.id != null) {
              updateMissionGroupProduct(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addMissionGroupProduct(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除任务组可兑换商品配置编号为"' + ids + '"的数据项？').then(() => {
          this.loading = true;
          return delMissionGroupProduct(ids);
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
        this.download('zlyyh-admin/missionGroupProduct/export', {
          ...this.queryParams
        }, `missionGroupProduct_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>