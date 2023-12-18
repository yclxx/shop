<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台" prop="platformKey">
        <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
          <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务名称" prop="browseName">
        <el-input v-model="queryParams.browseName" placeholder="请输入任务名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="跳转类型" prop="toType">
        <el-select v-model="queryParams.toType" placeholder="请选择跳转类型" clearable>
          <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品ID" prop="productId">
        <el-input v-model="queryParams.productId" placeholder="请输入商品ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['zlyyh:browse:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['zlyyh:browse:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['zlyyh:browse:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['zlyyh:browse:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="browseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="browseId" v-if="false" />
      <el-table-column label="平台" width="100" align="center" prop="platformKey" :formatter="platformFormatter" />
      <el-table-column label="任务名称" align="center" prop="browseName" />
      <el-table-column label="奖励说明" align="center" prop="browseRemark" min-width="150" />
      <el-table-column label="失效时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="跳转类型" align="center" prop="toType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_banner_to_type" :value="scope.row.toType" />
        </template>
      </el-table-column>
      <el-table-column label="小程序ID" align="center" prop="appId" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="页面地址" align="center" prop="url" width="180" :show-overflow-tooltip="true" />

      <el-table-column label="支持端" align="center" prop="supportChannel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.channel_type"
            :value="scope.row.supportChannel ? scope.row.supportChannel.split(',') : []" />
        </template>
      </el-table-column>
      <el-table-column label="商品ID" align="center" prop="productId" width="180" />
      <el-table-column label="生效时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" fixed="right">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['zlyyh:browse:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['zlyyh:browse:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改浏览任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台" prop="platformKey">
              <el-select v-model="form.platformKey" placeholder="请选择平台" clearable style="width: 100%;">
                <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="browseName">
              <el-input v-model="form.browseName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="奖励说明" prop="browseRemark">
              <el-input v-model="form.browseRemark" placeholder="请输入奖励说明" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生效时间" prop="startTime">
              <el-date-picker clearable v-model="form.startTime" type="datetime" style="width: 100%;"
                value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效时间" prop="endTime">
              <el-date-picker clearable v-model="form.endTime" type="datetime" style="width: 100%;"
                value-format="yyyy-MM-dd HH:mm:ss" default-time="23:59:59" placeholder="请选择失效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="指定周几" prop="assignDate">
              <el-radio-group v-model="form.assignDate">
                <el-radio v-for="dict in dict.type.t_product_assign_date" :key="dict.value" :label="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="周几显示" prop="weekDate" v-if="form.assignDate == '1'">
              <el-select v-model="form.weekDate" placeholder="请选择星期" style="width: 100%;" multiple clearable>
                <el-option v-for="dict in dict.type.t_grad_period_date_list" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品ID" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入商品ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跳转类型" prop="toType">
              <el-select v-model="form.toType" placeholder="请选择跳转类型" clearable style="width: 100%;">
                <el-option v-for="dict in dict.type.t_banner_to_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.toType == 3">
            <el-form-item label="小程序ID" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入小程序ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.toType != '0' && form.toType != '4' && form.toType != '6'">
            <el-form-item label="页面地址" v-if="form.toType == '3'">
              <el-input v-model="form.url" placeholder="请输入内容" />
            </el-form-item>
            <el-form-item label="页面地址" v-else prop="url">
              <el-input v-model="form.url" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.toType == 4">
            <el-form-item label="图片页面" prop="url">
              <image-upload :limit="1" v-model="form.url" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入排序" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="支持端" prop="supportChannel">
          <el-checkbox-group v-model="form.supportChannel">
            <el-checkbox v-for="dict in dict.type.channel_type" :key="dict.value" :label="dict.value">
              {{dict.label}}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
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
  </div>
</template>

<script>
  import {
    listBrowse,
    getBrowse,
    delBrowse,
    addBrowse,
    updateBrowse
  } from "@/api/zlyyh/browse";
  import {
    selectListPlatform
  } from "@/api/zlyyh/platform";
  import {
    treeselect as cityTreeselect,
    browseShowCityTreeSelect,
    selectCityList
  } from "@/api/zlyyh/area"

  export default {
    name: "Browse",
    dicts: ['channel_type', 't_grad_period_date_list', 't_product_assign_date', 't_banner_to_type',
      'sys_normal_disable'
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
        // 浏览任务表格数据
        browseList: [],
        cityList: [],
        cityNodeAll: false,
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
        //平台下拉列表
        platformList: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderByColumn: 'browseId',
          isAsc: 'desc',
          browseName: undefined,
          toType: undefined,
          platformKey: undefined,
          productId: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          browseId: [{
            required: true,
            message: "ID不能为空",
            trigger: "blur"
          }],
          browseName: [{
            required: true,
            message: "任务名称不能为空",
            trigger: "blur"
          }],
          browseRemark: [{
            required: true,
            message: "奖励说明不能为空",
            trigger: "blur"
          }],
          toType: [{
            required: true,
            message: "跳转类型不能为空",
            trigger: "change"
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
          endTime: [{
            required: true,
            message: "失效时间不能为空",
            trigger: "blur"
          }],
          platformKey: [{
            required: true,
            message: "平台标识不能为空",
            trigger: "blur"
          }],
          supportChannel: [{
            required: true,
            message: "支持端不能为空",
            trigger: "blur"
          }],
          assignDate: [{
            required: true,
            message: "指定周几不能为空",
            trigger: "change"
          }],
          weekDate: [{
            required: true,
            message: "周几能领不能为空",
            trigger: "change"
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
      this.getPlatformSelectList()
      this.getCitySelectList();
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
      //city下拉列表
      getCitySelectList() {
        let cityForm = {
          level: 'city'
        };
        selectCityList(cityForm).then(response => {
          this.cityList = response.data;
        })
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
      /** 查询浏览任务列表 */
      getList() {
        this.loading = true;
        listBrowse(this.queryParams).then(response => {
          this.browseList = response.rows;
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
          browseId: undefined,
          browseName: undefined,
          browseRemark: undefined,
          toType: undefined,
          appId: undefined,
          url: undefined,
          startTime: undefined,
          endTime: undefined,
          showCity: undefined,
          platformKey: undefined,
          supportChannel: [],
          assignDate: '0',
          weekDate: undefined,
          productId: undefined,
          sort: undefined,
          sysDeptId: undefined,
          sysUserId: undefined,
          createBy: undefined,
          createTime: undefined,
          updateBy: undefined,
          updateTime: undefined,
          delFlag: undefined
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
        this.ids = selection.map(item => item.browseId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加浏览任务";
        this.cityNodeAll = true;
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.loading = true;
        this.reset();
        const browseId = row.browseId || this.ids
        const showCity = this.getShowCityTreeselect(browseId);
        getBrowse(browseId).then(response => {
          this.loading = false;
          this.form = response.data;
          if (this.form && this.form.supportChannel) {
            this.form.supportChannel = this.form.supportChannel.split(",");
          }
          this.open = true;
          this.title = "修改浏览任务";
          this.cityNodeAll = false;
          if (this.form && this.form.weekDate) {
            this.form.weekDate = this.form.weekDate.split(",");
          }
          this.$nextTick(() => {
            showCity.then(res => {
              let checkedKeys = res.data.checkedKeys;
              checkedKeys.forEach((v) => {
                if (v == 99) {
                  this.cityNodeAll = true;
                } else {
                  this.$nextTick(() => {
                    this.$refs.city.setChecked(v, true, false);
                  })
                }
              })
            })
          })
        });
      },
      getShowCityTreeselect(browseId) {
        return browseShowCityTreeSelect(browseId).then(response => {
          this.cityOptions = response.data.citys;
          return response;
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
            this.form.supportChannel = this.form.supportChannel.join(",");
            if (this.cityNodeAll) {
              this.form.showCity = "ALL";
            } else {
              this.form.showCity = this.getCityAllCheckedKeys().toString();
            }
            if (this.form.weekDate) {
              this.form.weekDate = this.form.weekDate.join(',');
            }
            if (this.form.browseId != null) {
              updateBrowse(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }).finally(() => {
                this.buttonLoading = false;
              });
            } else {
              addBrowse(this.form).then(response => {
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
        const browseIds = row.browseId || this.ids;
        this.$modal.confirm('是否确认删除浏览任务编号为"' + browseIds + '"的数据项？').then(() => {
          this.loading = true;
          return delBrowse(browseIds);
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
        this.download('zlyyh-admin/browse/export', {
          ...this.queryParams
        }, `browse_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>