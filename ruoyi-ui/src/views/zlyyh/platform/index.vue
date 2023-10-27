<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="平台名称" prop="platformName">
        <el-input v-model="queryParams.platformName" placeholder="请输入平台名称" clearable
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.t_platform_status" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
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
                   v-hasPermi="['zlyyh:platform:add']">新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
                   v-hasPermi="['zlyyh:platform:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['zlyyh:platform:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['zlyyh:platform:export']">导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="platformList">
      <el-table-column label="平台标识" width="190" align="center" prop="platformKey" v-if="true"/>
      <el-table-column label="平台名称" align="center" prop="platformName"/>
      <el-table-column label="状态" width="66" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.t_platform_status" :value="scope.row.status"/>
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
      <el-table-column label="操作" width="120" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['zlyyh:platform:edit']">修改
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['zlyyh:platform:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>
    <!-- 添加或修改平台信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="70%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="平台编号" prop="platformKey" v-if="insert">
              <el-input v-model="form.platformKey" placeholder="请输入平台编号"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="平台名称" prop="platformName">
              <el-input v-model="form.platformName" placeholder="请输入平台名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%;" placeholder="请选择状态">
                <el-option v-for="dict in dict.type.t_platform_status" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="会员权限" prop="unionPayVip">
              <el-select v-model="form.unionPayVip" style="width: 100%;" placeholder="请选择云闪付会员权限">
                <el-option v-for="dict in dict.type.t_platform_union_pay_vip" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supportSupplier">
              <el-select v-model="form.supportSupplier" multiple placeholder="请选择供应商" style="width: 100%">
                <el-option
                  v-for="item in supplierList"
                  :key="item.id"
                  :label="item.label"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="管理部门" prop="manangerDeptId">
              <treeselect v-model="form.manangerDeptId" :options="deptOptions" :show-count="true"
                          placeholder="请选择管理部门"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公共部门" prop="sysDeptId">
              <treeselect v-model="form.sysDeptId" :options="deptOptions" :show-count="true"
                          placeholder="请选择公共部门"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客服电话" prop="serviceTel">
              <el-input v-model="form.serviceTel" placeholder="请输入客服电话"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客服时间" prop="serviceTime">
              <el-input v-model="form.serviceTime" placeholder="请输入客服服务时间"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="支持端" prop="supportChannel">
              <el-checkbox-group v-model="form.supportChannel">
                <el-checkbox @change="checked=>changeChannel(checked,dict)"
                             v-for="item in dict.type.channel_type" :key="item.value" :label="item.value">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.platformChannel">
          <div v-for="(item,index) in form.platformChannel" :key="index">
            <el-card>
              <div slot="header">
                <span v-for="dict in dict.type.channel_type" v-if="dict.value === item.channel">
                  {{ dict.label }}配置
                </span>
              </div>
              <el-col :span="6">
                <el-form-item label="支付商户号"
                              :prop="'platformChannel.'+index+'.merchantId'"
                              :rules="{required: true, message: '请选择支付商户号', trigger: 'blur'}">
                  <el-select v-model="item.merchantId" style="width: 100%;" placeholder="请选择商户号">
                    <el-option v-for="items in merchantList" :key="items.id" :label="items.rightLabel"
                               :value="items.id">
                      <span style="float: left">{{ items.rightLabel }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px"
                            v-if="items.label && items.label.length>0">
                    {{ items.label }}
                  </span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="小程序标题" align="center"
                              :prop="'platformChannel.'+index+'.platformTitle'"
                              :rules="{required: true, message: '请输入小程序标题', trigger: 'blur'}">
                  <el-input v-model="item.platformTitle" placeholder="请输入小程序标题"/>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="appId" align="center"
                              :prop="'platformChannel.'+index+'.appId'"
                              :rules="{required: true, message: '请输入appId', trigger: 'blur'}">
                  <el-input v-model="item.appId" placeholder="请输入appId"/>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="小程序ID" align="center"
                              :prop="'platformChannel.'+index+'.encryptAppId'"
                              :rules="{required: true, message: '请输入小程序ID', trigger: 'blur'}">
                  <el-input v-model="item.encryptAppId" placeholder="请输入小程序ID"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密钥" align="center">
                  <el-input v-model="item.secret" placeholder="请输入密钥"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="对称密钥" align="center">
                  <el-input v-model="item.symmetricKey" placeholder="请输入对称密钥"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="rsa签名私钥">
                  <el-input v-model="item.rsaPrivateKey" type="textarea" placeholder="请输入rsa签名私钥"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="rsa签名公钥">
                  <el-input v-model="item.rsaPublicKey" type="textarea" placeholder="请输入rsa签名公钥"/>
                </el-form-item>
              </el-col>
            </el-card>
          </div>
        </el-row>
        <el-row>
          <el-col :span="24" style="margin-top: 25px">
            <el-form-item label="活动城市">
              <el-checkbox v-model="cityNodeAll" @change="selectAll">不限制城市</el-checkbox>
              <el-tree @check="handleNodeClick" class="tree-border" :data="cityOptions" show-checkbox default-expand-all
                       ref="city" node-key="id" empty-text="加载中,请稍后" :props="defaultProps"></el-tree>
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
  listPlatform,
  getPlatform,
  delPlatform,
  addPlatform,
  updatePlatform
} from "@/api/zlyyh/platform";
import {
  listSelectMerchant
} from "@/api/zlyyh/merchant"
import {
  treeselect as cityTreeselect,
  platformCityTreeselect
} from "@/api/zlyyh/area"
import {
  deptTreeSelect
} from "@/api/system/user";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {selectSupplier} from "@/api/zlyyh/supplier";

export default {
  name: "Platform",
  dicts: ['t_platform_status', 't_platform_union_pay_vip', 'channel_type'],
  components: {
    Treeselect
  },
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
      cityExpand: false,
      cityNodeAll: false,
      // 显示搜索条件
      showSearch: true,
      // 部门树选项
      deptOptions: undefined,
      // 总条数
      total: 0,
      // 平台信息表格数据
      platformList: [],
      merchantList: [],
      cityOptions: [],
      supplierList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformName: undefined,
        platformTitle: undefined,
        status: undefined,
        appId: undefined,
        encryptAppId: undefined,
        secret: undefined,
        symmetricKey: undefined,
        rsaPrivateKey: undefined,
        rsaPublicKey: undefined,
        serviceTel: undefined,
        serviceTime: undefined,
        platformCity: undefined,
        merchantId: undefined,
        orderByColumn: 'create_time',
        isAsc: 'desc'
      },
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        platformName: [{
          required: true,
          message: "平台名称不能为空",
          trigger: "blur"
        }],
        status: [{
          required: true,
          message: "状态不能为空",
          trigger: "change"
        }],
        unionPayVip: [{
          required: true,
          message: "云闪付会员权限不能为空",
          trigger: "blur"
        }],
        supportChannel: [
          {required: true, message: "支持端不能为空", trigger: "blur"}
        ]
      },
      insert: true,
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
    this.selectSupplierList();
  },
  methods: {
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    /** 查询平台信息列表 */
    getList() {
      this.loading = true;
      listPlatform(this.queryParams).then(response => {
        this.platformList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
      listSelectMerchant({}).then(response => {
        this.merchantList = response.data;
      })
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    handleNodeClick(data) {
      this.cityNodeAll = false;
    },
    // 表单重置
    reset() {
      this.form = {
        platformKey: undefined,
        platformName: undefined,
        platformTitle: undefined,
        status: undefined,
        unionPayVip: undefined,
        appId: undefined,
        encryptAppId: undefined,
        secret: undefined,
        symmetricKey: undefined,
        rsaPrivateKey: undefined,
        rsaPublicKey: undefined,
        serviceTel: undefined,
        serviceTime: undefined,
        platformCity: undefined,
        merchantId: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined,
        delFlag: undefined,
        sysDeptId: undefined,
        platformChannel: [{
          channel: '0',
          id: undefined,
          platformTitle: undefined,
          appId: undefined,
          encryptAppId: undefined,
          secret: undefined,
          symmetricKey: undefined,
          rsaPrivateKey: undefined,
          rsaPublicKey: undefined,
          merchantId: undefined
        }],
        manangerDeptId: undefined,
        supportChannel: ['ALL'],
        supportSupplier: [],
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
      this.ids = selection.map(item => item.platformKey)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 全选/不全选
    selectAll(val) {
      console.log(this.cityNodeAll)
      if (this.cityNodeAll) {
        // 	设置目前勾选的节点，使用此方法必须设置 node-key 属性
        this.$refs.city.setCheckedNodes([])
      } else {
        // 全部不选中
        this.$refs.city.setCheckedNodes([])
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加平台信息";
      this.insert = true;
      this.getCityTreeselect();
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
    getCityTreeselect() {
      cityTreeselect().then(response => {
        this.cityOptions = response.data;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.insert = false;
      this.reset();
      const platformKey = row.platformKey || this.ids
      const platformCity = this.getPlatformCityTreeselect(platformKey);
      getPlatform(platformKey).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改平台信息";
        this.cityNodeAll = false;
        if (response.data.supportSupplier && response.data.supportSupplier !== 'ALL') {
          this.form.supportSupplier = response.data.supportSupplier.split(',');
        } else {
          this.form.supportSupplier = [];
        }
        if (response.data.supportChannel) {
          this.form.supportChannel = response.data.supportChannel.split(',');
        }
        this.$nextTick(() => {
          platformCity.then(res => {
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
    getPlatformCityTreeselect(platformKey) {
      return platformCityTreeselect(platformKey).then(response => {
        this.cityOptions = response.data.citys;
        return response;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.cityNodeAll) {
            this.form.platformCity = "ALL";
          } else {
            this.form.platformCity = this.getCityAllCheckedKeys().toString();
          }
          if (this.form.supportSupplier && this.form.supportSupplier !== 'ALL') {
            this.form.supportSupplier = this.form.supportSupplier.join(',');
          }
          if (this.form.supportChannel.length > 0) {
            this.form.supportChannel = this.form.supportChannel.join(',');
          }
          if (!this.insert) {
            updatePlatform(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addPlatform(this.form).then(response => {
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
      const platformKeys = row.platformKey || this.ids;
      this.$modal.confirm('是否确认删除平台信息编号为"' + platformKeys + '"的数据项？').then(() => {
        this.loading = true;
        return delPlatform(platformKeys);
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
      this.download('zlyyh-admin/platform/export', {
        ...this.queryParams
      }, `platform_${new Date().getTime()}.xlsx`)
    },
    handleCheckedTreeExpand(value) {
      let treeList = this.cityOptions;
      for (let i = 0; i < treeList.length; i++) {
        this.$refs.city.store.nodesMap[treeList[i].id].expanded = value;
      }
    },
    handleCheckedTreeNodeAll(value) {
      this.$refs.city.setCheckedNodes(value ? this.cityOptions : []);
    },
    /** 查询供应商 */
    selectSupplierList() {
      selectSupplier(this.form).then(response => {
        this.supplierList = response.data;
      }).finally(() => {
      });
    },
    changeChannel(check, item) {
      if (check) {
        //增加支持端
        const platformChannel = {
          channel: item.value,
          id: undefined,
          platformTitle: undefined,
          appId: undefined,
          encryptAppId: undefined,
          secret: undefined,
          symmetricKey: undefined,
          rsaPrivateKey: undefined,
          rsaPublicKey: undefined,
          merchantId: undefined
        }
        this.form.platformChannel.push(platformChannel);
      } else {
        // 删除支持端
        for (let i = 0; i < this.form.platformChannel.length; i++) {
          if (item.value === this.form.platformChannel[i].channel) {
            this.form.platformChannel.splice(i, 1)
          }
        }
      }
    },
    renderHeader(h, {
      column
    }) {
      let currentLabel = column.label;
      return h('span', {}, [
        h('span', {
          style: 'color:red'
        }, '* '),
        h('span', {}, currentLabel)
      ])
    },
  }
};
</script>
