<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="68px">
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
            <el-form-item label="平台标识" prop="platformKey">
                <el-select v-model="queryParams.platformKey" placeholder="请选择平台标识" clearable>
                    <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
                </el-select>
            </el-form-item>
            <el-form-item label="模板名称" prop="templateName">
                <el-input
                    v-model="queryParams.templateName"
                    placeholder="请输入模板名称"
                    clearable
                    @keyup.enter.native="handleQuery"
                />
            </el-form-item>
            <el-form-item label="支持端" prop="channel">
                <el-select v-model="queryParams.channel" placeholder="请选择状态" clearable>
                    <el-option
                        v-for="dict in dict.type.channel_type"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                    />
                </el-select>
                <!--<el-input-->
                <!--    v-model="queryParams.channel"-->
                <!--    placeholder="请输入支持端"-->
                <!--    clearable-->
                <!--    @keyup.enter.native="handleQuery"-->
                <!--/>-->
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
                    v-hasPermi="['zlyyh:messageTemplate:add']"
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
                    v-hasPermi="['zlyyh:messageTemplate:edit']"
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
                    v-hasPermi="['zlyyh:messageTemplate:remove']"
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
                    v-hasPermi="['zlyyh:messageTemplate:export']"
                >导出
                </el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="messageTemplateList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center"/>
            <el-table-column label="" align="center" prop="templateId" v-if="true"/>
            <el-table-column label="状态" align="center" prop="status">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
                </template>
            </el-table-column>
            <el-table-column label="平台标识" align="center" prop="platformKey" :formatter="platformFormatter"/>
            <el-table-column label="模板标识" align="center" prop="templateKey"/>
            <el-table-column label="模板名称" align="center" prop="templateName"/>
            <el-table-column label="支持端" align="center" prop="channel">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.channel_type" :value="scope.row.channel"/>
                </template>
            </el-table-column>
            <!--<el-table-column label="关键字" align="center" prop="keyword"/>-->
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button
                        size="mini"
                        type="text"
                        icon="el-icon-edit"
                        @click="handleMessages(scope.row)"
                        v-hasPermi="['zlyyh:messageTemplate:edit']"
                    >发送消息
                    </el-button>
                    <el-button
                        size="mini"
                        type="text"
                        icon="el-icon-edit"
                        @click="handleUpdate(scope.row)"
                        v-hasPermi="['zlyyh:messageTemplate:edit']"
                    >修改
                    </el-button>
                    <el-button
                        size="mini"
                        type="text"
                        icon="el-icon-delete"
                        @click="handleDelete(scope.row)"
                        v-hasPermi="['zlyyh:messageTemplate:remove']"
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

        <!-- 添加或修改消息模板对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
                <el-form-item label="平台标识" prop="platformKey">
                    <el-select v-model="form.platformKey" placeholder="请选择平台标识" @change="getPlatformSelectList"
                               clearable>
                        <el-option v-for="item in platformList" :key="item.id" :label="item.label" :value="item.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="支持端" prop="channel">
                    <el-select v-model="form.channel" placeholder="请选择状态" clearable>
                        <el-option
                            v-for="dict in dict.type.channel_type"
                            :key="dict.value"
                            :label="dict.label"
                            :value="dict.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="模板标识" prop="templateKey">
                    <el-input v-model="form.templateKey" placeholder="请输入模板标识"/>
                </el-form-item>
                <el-form-item label="模板名称" prop="templateName">
                    <el-input v-model="form.templateName" placeholder="请输入模板名称"/>
                </el-form-item>
                <el-form-item label="关键字" prop="keyword">
                    <div v-for="(item,index) in form.keyword" :key="index">
                        <el-row type="flex" :gutter="5">
                            <el-col :span="8">
                                <span>名称:</span>
                                <el-input v-model="item.name" type="text" placeholder="请输入名称"/>
                            </el-col>
                            <el-col :span="8">
                                <span>关键字</span>
                                <el-input v-model="item.key" type="text" placeholder="请输入Key"/>
                            </el-col>
                            <el-col v-if="index>0" :span="8">
                                <el-button @click.prevent="removeKeyword(item,index)">删除</el-button>
                            </el-col>
                        </el-row>
                    </div>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addKeyword">新增关键字</el-button>
                <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>

        <el-dialog :visible.sync="messagesOpen" width="800px" append-to-body>
            <el-row :gutter="20">
                <el-col :span="4">
                    支持端:
                    <span v-for="dict in dict.type.channel_type" v-if="dict.value === messagesForm.channel">
                            {{ dict.label }}
                        </span>
                </el-col>
                <el-col :span="8">
                    模板名称:{{ messagesForm.templateName }}
                </el-col>
                <el-col :span="8">
                    模板标识:{{ messagesForm.templateId }}
                </el-col>
            </el-row>
            <div style="margin-top: 20px" v-for="(item,index) in messagesForm.keyword" :key="index">
                <el-row :gutter="20">
                    <el-col :span="4">
                        <span>名称:</span>
                        <span>{{ item.name }}</span>
                    </el-col>
                    <el-col :span="4">
                        <span>关键字</span>
                        <span>{{ item.key }}</span>
                    </el-col>
                    <el-col :span="16">
                        <span>值</span>
                        <el-input v-model="item.value" type="text" placeholder="请输入消息"/>
                    </el-col>
                </el-row>
            </div>
            <div>
                <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
                           :action="upload.url" :disabled="upload.isUploading" :data="messagesParam"
                           :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess"
                           :auto-upload="false" drag>
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip text-center" slot="tip">
                        <span>仅允许导入xls、xlsx格式文件。</span>
                        <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
                                 @click="importTemplate">下载模板
                        </el-link>
                        <div class="el-upload__tip" slot="tip">
                            请注意：用户手机号根据模板平台和对应支持端发送消息，若没有用户数据自动过滤。
                        </div>
                    </div>
                </el-upload>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitFileForm">发送</el-button>
                <el-button @click="messagesOpen = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import {
    listMessageTemplate,
    getMessageTemplate,
    delMessageTemplate,
    addMessageTemplate,
    updateMessageTemplate
} from "@/api/zlyyh/messageTemplate";
import {selectListPlatform} from "@/api/zlyyh/platform";
import {getToken} from "@/utils/auth";

export default {
    name: "MessageTemplate",
    dicts: ['sys_normal_disable', 'channel_type'],
    data() {
        return {
            messagesOpen: false,
            messagesForm: {
                templateId: undefined,
                platformKey: undefined,
                templateName: undefined,
                channel: undefined,
                keyword: [{
                    name: undefined,
                    key: undefined,
                    value: ''
                }]
            },
            messagesParam: {
                templateId: undefined,
                platformKey: undefined,
                templateName: undefined,
                channel: undefined,
                keyword: undefined
            },
            // 物流数据导入
            upload: {
                // 是否显示弹出层（用户导入）
                open: false,
                // 弹出层标题（用户导入）
                title: "",
                // 是否禁用上传
                isUploading: false,
                // 设置上传的请求头部
                headers: {
                    Authorization: "Bearer " + getToken()
                },
                // 上传的地址
                url: process.env.VUE_APP_BASE_API + "/zlyyh-admin/messageTemplate/importData"
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
            // 消息模板表格数据
            messageTemplateList: [],
            platformList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                status: undefined,
                platformKey: undefined,
                templateKey: undefined,
                templateName: undefined,
                channel: undefined,
            },
            // 表单参数
            form: {},
            // 表单校验
            rules: {
                templateId: [
                    {required: true, message: "不能为空", trigger: "blur"}
                ],
                status: [
                    {required: true, message: "状态不能为空", trigger: "change"}
                ],
                platformKey: [
                    {required: true, message: "平台标识不能为空", trigger: "blur"}
                ],
                templateKey: [
                    {required: true, message: "模板标识不能为空", trigger: "blur"}
                ],
                templateName: [
                    {required: true, message: "模板名称不能为空", trigger: "blur"}
                ],
                channel: [
                    {required: true, message: "支持端不能为空", trigger: "blur"}
                ],
                keyword: [
                    {required: true, message: "关键字不能为空", trigger: "blur"}
                ]
            }
        };
    },
    created() {
        this.getList();
        this.getPlatformSelectList();
    },
    methods: {
        // 文件上传中处理
        handleFileUploadProgress(event, file, fileList) {
            this.upload.isUploading = true;
        },
        // 文件上传成功处理
        handleFileSuccess(response, file, fileList) {
            this.upload.open = false;
            this.upload.isUploading = false;
            this.$refs.upload.clearFiles();
            this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response
                .msg + "</div>", "导入结果", {
                dangerouslyUseHTMLString: true
            });
            this.getList();
        },
        // 新增关键字
        addKeyword() {
            const param = {
                name: undefined,
                key: undefined,
                value: ''
            }
            this.form.keyword.push(param);
        },
        removeKeyword(item, indexs) {
            console.log(indexs)
            if (indexs > 0) {
                this.form.keyword.splice(indexs, 1)
            }
        },
        /** 查询消息模板列表 */
        getList() {
            this.loading = true;
            listMessageTemplate(this.queryParams).then(response => {
                this.messageTemplateList = response.rows;
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
                templateId: undefined,
                createBy: undefined,
                createTime: undefined,
                updateBy: undefined,
                updateTime: undefined,
                status: undefined,
                platformKey: undefined,
                templateKey: undefined,
                templateName: undefined,
                channel: undefined,
                keyword: [{
                    name: undefined,
                    key: undefined,
                    value: ''
                }]
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
            this.ids = selection.map(item => item.templateId)
            this.single = selection.length !== 1
            this.multiple = !selection.length
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加消息模板";
        },
        /** 发送消息操作 */
        handleMessages(row) {
            const templateId = row.templateId || this.ids
            getMessageTemplate(templateId).then(response => {
                this.messagesForm.templateId = response.data.templateId;
                this.messagesForm.platformKey = response.data.platformKey;
                this.messagesForm.templateName = response.data.templateName;
                this.messagesForm.channel = response.data.channel;
                this.messagesForm.keyword = JSON.parse(response.data.keyword);
                this.messagesOpen = true;
            });
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.loading = true;
            this.reset();
            const templateId = row.templateId || this.ids
            getMessageTemplate(templateId).then(response => {
                this.loading = false;
                this.form = response.data;
                this.form.keyword = JSON.parse(response.data.keyword);
                this.open = true;
                this.title = "修改消息模板";
            });
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    for (let u = 0; u < this.form.keyword.length; u++) {
                        if (!this.form.keyword[u].name) {
                            this.$modal.msgError("必须填写名称");
                            return;
                        }
                        if (!this.form.keyword[u].key) {
                            this.$modal.msgError("必须填写关键字");
                            return;
                        }
                    }
                    this.buttonLoading = true;
                    this.form.keyword = JSON.stringify(this.form.keyword);
                    if (this.form.templateId != null) {
                        updateMessageTemplate(this.form).then(response => {
                            this.$modal.msgSuccess("修改成功");
                            this.open = false;
                            this.getList();
                        }).finally(() => {
                            this.buttonLoading = false;
                        });
                    } else {
                        addMessageTemplate(this.form).then(response => {
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
        /** 下载模板操作 */
        importTemplate() {
            this.download('/zlyyh-admin/messageTemplate/importTemplate', {}, `user_info_${new Date().getTime()}.xlsx`)
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const templateIds = row.templateId || this.ids;
            this.$modal.confirm('是否确认删除消息模板编号为"' + templateIds + '"的数据项？').then(() => {
                this.loading = true;
                return delMessageTemplate(templateIds);
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
            this.download('zlyyh/messageTemplate/export', {
                ...this.queryParams
            }, `messageTemplate_${new Date().getTime()}.xlsx`)
        },
        // 提交上传文件
        submitFileForm() {
            this.messagesParam.templateId = this.messagesForm.templateId;
            this.messagesParam.channel = this.messagesForm.channel;
            this.messagesParam.templateName = this.messagesForm.templateName;
            this.messagesParam.platformKey = this.messagesForm.platformKey;
            this.messagesParam.keyword = JSON.stringify(this.messagesForm.keyword);
            this.$refs.upload.submit();
        }
    }
};
</script>
