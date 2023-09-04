<template>
  <div>
    <div style="display: flex;justify-content: space-between;">
      <el-card style="width: 50%;">
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
          <el-form-item prop="str" label="需加密内容">
            <el-input v-model="form.str" type="textarea" placeholder="请输入需加密内容" />
          </el-form-item>
          <div style="width: 40%;margin: auto;text-align: center;">
            <el-button style="margin: auto;" :loading="buttonLoading" type="primary" @click="submitForm">加 密</el-button>
          </div>
        </el-form>
      </el-card>
      <el-card style="width: 50%;">
        <el-form label-width="120px">
          <el-form-item prop="str" label="加密后内容">
            <span>{{encryptStr}}</span>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>


<script>
  import {
    encrypt
  } from "@/api/zlyyh/hsm";

  export default {
    name: "Hsm",
    data() {
      return {
        // 按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        // 表单参数
        form: {},
        encryptStr: undefined,
        // 表单校验
        rules: {
          str: [{
            required: true,
            message: "加密内容不能为空",
            trigger: "blur"
          }],
        }
      };
    },
    created() {},
    methods: {
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            this.buttonLoading = true;
            encrypt(this.form).then(response => {
              this.encryptStr = response.data
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        });
      },
    }
  };
</script>


<style>
</style>