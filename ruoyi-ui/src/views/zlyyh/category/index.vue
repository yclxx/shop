<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="栏目名称" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="请输入栏目名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="栏目类型" prop="categoryListType">
        <el-select v-model="queryParams.categoryListType" placeholder="请选择栏目内容类型" clearable>
          <el-option v-for="dict in dict.type.t_category_list_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_category_status" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
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
          v-hasPermi="['zlyyh:category:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-sort" size="mini" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="categoryList" row-key="categoryId"
      :default-expand-all="isExpandAll" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column label="栏目名称" prop="categoryName" />
      <el-table-column label="编号" width="200" align="center" prop="categoryId" />
      <el-table-column label="栏目类型" width="88" align="center" prop="categoryListType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_category_list_type" :value="scope.row.categoryListType" />
        </template>
      </el-table-column>
      <el-table-column label="支持端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <div v-for="channel in dict.type.channel_type">
            <div v-for="(supportChannel,index) in scope.row.supportChannel.split(',')" :key="index">
              <span v-if="channel.value === supportChannel">{{ channel.label }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="显示首页" width="88" align="center" prop="showIndex">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_show_index" :value="scope.row.showIndex" />
        </template>
      </el-table-column>
      <el-table-column label="周几显示" :show-overflow-tooltip="true" align="center" prop="weekDate">
        <template slot-scope="scope">
          <div v-if="scope.row.assignDate == '1'">
            <dict-tag :options="dict.type.t_grad_period_date_list" :value="scope.row.weekDate.split(',')" />
          </div>
          <div v-else>不限制</div>
        </template>
      </el-table-column>
      <el-table-column label="排序" width="50" align="center" prop="sort" />
      <el-table-column label="状态" width="68" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_category_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.categoryListType == '0'" size="mini" type="text" icon="el-icon-edit"
            @click="handleProductByCategory(scope.row)" v-hasPermi="['zlyyh:category:add']">商品维护</el-button>
          <el-button v-if="scope.row.categoryListType == '1'" size="mini" type="text" icon="el-icon-edit"
            @click="handleCommercialByCategory(scope.row)" v-hasPermi="['zlyyh:category:add']">门店维护</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:category:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
            v-hasPermi="['zlyyh:category:add']">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:category:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改栏目对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" clearable>
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="栏目名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入栏目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="栏目类型" prop="categoryListType">
              <el-select style="width: 100%;" v-model="form.categoryListType" placeholder="请选择栏目内容类型">
                <el-option v-for="dict in dict.type.t_category_list_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="支持端" prop="supportChannel">
              <el-checkbox-group v-model="form.supportChannel">
                <el-checkbox v-for="item in dict.type.channel_type" :key="item.value" :label="item.value">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <!--            <el-form-item label="商户" prop="commercialTenantIds" v-if="form.categoryListType == '1'">-->
            <!--              <el-select style="width: 100%;" v-model="form.commercialTenantIds" placeholder="请选择商户" multiple clearable>-->
            <!--                <el-option v-for="item in commercialTenantList" :key="item.id" :label="item.label" :value="item.id" />-->
            <!--              </el-select>-->
            <!--            </el-form-item>-->
            <!--            <el-form-item label="商品" prop="productIds" v-if="form.categoryListType == '0'">-->
            <!--              <el-select style="width: 100%;" v-model="form.productIds" placeholder="请选择商品" multiple clearable>-->
            <!--                <el-option v-for="item in productList" :key="item.id" :label="item.label" :value="item.id">-->
            <!--                  <span style="float: left">{{ item.label }}</span>-->
            <!--                </el-option>-->
            <!--              </el-select>-->
            <!--            </el-form-item>-->
          </el-col>
          <el-col :span="8">
            <el-form-item label="顶部图片" prop="topImg">
              <image-upload :isShowTip="false" v-model="form.topImg" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="类别logo" prop="logoImg">
              <image-upload :isShowTip="false" v-model="form.logoImg" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="logo未选" prop="unLogoImg">
              <image-upload :isShowTip="false" v-model="form.unLogoImg" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="归属" prop="parentId">
              <treeselect v-model="form.parentId" :options="categoryOptions" :normalizer="normalizer"
                placeholder="请选择归属" />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入排序" />
            </el-form-item>
            <el-form-item label="按钮颜色" prop="btnColor">
              <el-color-picker v-model="form.btnColor"></el-color-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.t_category_status" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="显示首页" prop="showIndex">
              <el-radio-group v-model="form.showIndex">
                <el-radio v-for="dict in dict.type.t_show_index" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="指定周几" prop="assignDate">
              <el-radio-group v-model="form.assignDate">
                <el-radio v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.value">
                  {{dict.label}}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="周几显示" prop="weekDate" v-if="form.assignDate == '1'">
              <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="展示城市" prop="showCity">
          <el-checkbox v-model="cityNodeAll" @change="selectAll">全部</el-checkbox>
          <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
            ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="栏目商品" :visible.sync="isProduct" width="90%">
      <Product v-bind:categoryId=categoryId></Product>
    </el-dialog>
    <el-dialog title="栏目门店" :visible.sync="isCommercial" width="90%">
      <Commercial v-bind:categoryId=categoryId></Commercial>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listCategory,
    getCategory,
    delCategory,
    addCategory,
    updateCategory
  } from "@/api/zlyyh/category";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";
  import {
    selectListMerchant
  } from "@/api/zlyyh/commercialTenant";
  import {
    selectListProduct
  } from "@/api/zlyyh/product";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    treeselect as cityTreeselect,
  } from "@/api/zlyyh/area"
  import Product from "@/views/zlyyh/product/categoryProductPoup.vue";
  import Commercial from "@/views/zlyyh/commercialTenant/categoryCommercialPoup.vue";
  export default {
    name: "Category",
    dicts: ['t_category_list_type', 't_category_status', 't_product_assign_date', 't_grad_period_date_list',
      't_show_index', 'channel_type'
    ],
    components: {
      Treeselect,
      Product,
      Commercial
    },
    data() {
      return {
        //商品下拉列表
        productList: [],
        //商户下拉列表
        commercialTenantList: [],
        //平台下拉列表
        platformList: [],
        // 按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        // 显示搜索条件
        showSearch: true,
        // 栏目表格数据
        categoryList: [],
        // 栏目树选项
        categoryOptions: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 是否展开，默认全部展开
        isExpandAll: true,
        // 重新渲染表格状态
        refreshTable: true,
        // 查询参数
        queryParams: {
          categoryName: undefined,
          categoryListType: undefined,
          status: undefined,
          parentId: undefined,
          sort: undefined,
          platformKey: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          categoryId: [{
            required: true,
            message: "栏目ID不能为空",
            trigger: "blur"
          }],
          categoryName: [{
            required: true,
            message: "栏目名称不能为空",
            trigger: "blur"
          }],
          categoryListType: [{
            required: true,
            message: "栏目内容类型不能为空",
            trigger: "change"
          }],
          supportChannel: [{
            required: true,
            message: "支持端不能为空",
            trigger: "change"
          }],
          status: [{
            required: true,
            message: "状态不能为空",
            trigger: "change"
          }],
          parentId: [{
            required: true,
            message: "父级id不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台不能为空",
            trigger: "blur"
          }],
          weekDate: [{
            required: true,
            message: "请选择周几显示",
            trigger: "blur"
          }],
        },
        cityNodeAll: false,
        defaultProps: {
          children: "children",
          label: "label"
        },
        cityOptions: [],
        categoryId: undefined,
        isProduct: false,
        isCommercial: false
      };
    },
    created() {
      this.getList();
      this.getPlatformSelectList();
      // this.getMerSelectList();
      //this.getProductSelectList();
      this.getCityTreeselect()
    },
    methods: {
      // 商品维护
      handleProductByCategory(row) {
        this.categoryId = row.categoryId;
        this.isProduct = true;
      },
      handleCommercialByCategory(row) {
        this.categoryId = row.categoryId;
        this.isCommercial = true;

      },
      getCityTreeselect() {
        cityTreeselect().then(response => {
          this.cityOptions = response.data;
        });
      },
      handleNodeClick(data) {
        this.cityNodeAll = false;
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

      /** 查询栏目列表 */
      getList() {
        this.loading = true;
        listCategory(this.queryParams).then(response => {
          this.categoryList = this.handleTree(response.data, "categoryId", "parentId");
          this.loading = false;
        });
      },
      //商品下拉列表
      // getProductSelectList() {
      //   selectListProduct({
      //     status: '0'
      //   }).then(response => {
      //     this.productList = response.data;
      //   });
      // },
      //商户下拉列表
      // getMerSelectList() {
      //   selectListMerchant({
      //     status: '0'
      //   }).then(response => {
      //     this.commercialTenantList = response.data;
      //   });
      // },
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
      /** 转换栏目数据结构 */
      normalizer(node) {
        if (node.children && !node.children.length) {
          delete node.children;
        }
        return {
          id: node.categoryId,
          label: node.categoryName,
          children: node.children
        };
      },
      /** 查询栏目下拉树结构 */
      getTreeselect() {
        listCategory().then(response => {
          this.categoryOptions = [];
          const data = {
            categoryId: 0,
            categoryName: '顶级节点',
            children: []
          };
          data.children = this.handleTree(response.data, "categoryId", "parentId");
          this.categoryOptions.push(data);
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
          categoryId: null,
          categoryName: null,
          categoryListType: null,
          status: '0',
          assignDate: '0',
          showIndex: '0',
          parentId: null,
          sort: null,
          platformKey: null,
          supportChannel: [],
          createBy: null,
          createTime: null,
          updateBy: null,
          updateTime: null,
          delFlag: null,
          // productIds: undefined,
          commercialTenantIds: undefined,
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd(row) {
        this.reset();
        this.getTreeselect();
        if (row != null && row.categoryId) {
          this.form.parentId = row.categoryId;
        } else {
          this.form.parentId = 0;
        }
        this.cityNodeAll = true;
        this.$nextTick(() => {
          this.cityOptions.forEach((v) => {
            this.$nextTick(() => {
              this.$refs.city.setChecked(v, false, true);
            })
          })
        })
        this.open = true;
        this.title = "添加栏目";
      },
      /** 展开/折叠操作 */
      toggleExpandAll() {
        this.refreshTable = false;
        this.isExpandAll = !this.isExpandAll;
        this.$nextTick(() => {
          this.refreshTable = true;
        });
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        this.getTreeselect();
        if (row != null) {
          this.form.parentId = row.categoryId;
        }
        getCategory(row.categoryId).then(response => {
          this.loading = false;
          this.form = response.data;
          if (this.form && this.form.weekDate) {
            this.form.weekDate = this.form.weekDate.split(",");
          }
          if (this.form && this.form.supportChannel) {
            this.form.supportChannel = this.form.supportChannel.split(",");
          } else {
            this.form.supportChannel = [];
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
          this.title = "修改栏目";
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
            if (this.form.weekDate) {
              this.form.weekDate = this.form.weekDate.toString();
            }
            if (this.cityNodeAll) {
              this.form.showCity = "ALL";
            } else {
              this.form.showCity = this.getCityAllCheckedKeys().toString();
            }
            if (this.form && this.form.supportChannel.length > 0) {
              this.form.supportChannel = this.form.supportChannel.join(',');
            }
            if (this.form.categoryId != null) {
              updateCategory(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addCategory(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除栏目编号为"' + row.categoryId + '"的数据项？').then(() => {
          this.loading = true;
          return delCategory(row.categoryId);
        }).then(() => {
          this.loading = false;
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {}).finally(() => {
          this.loading = false;
        });
      }
    }
  };
</script>