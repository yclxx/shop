<template>
  <div>
    <div style="width: 500px;margin: auto;padding-top: 100px;">
      <div style="text-align: center;font-weight: bold;margin-bottom: 80px;">旧密码已失效，请重新设置密码</div>
      <div style="text-align: center;margin-bottom: 30px;">
        密码6-20位，必须包含大写字母，小写字母，数字及特殊字符
      </div>
      <el-form ref="form" :model="user" :rules="rules" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password />
        </el-form-item>
        <div style="width: 50%;margin: auto;">
          <el-button style="width: 100%;" type="primary" @click="submit">提交</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
  import {
    updateUserPwd
  } from "@/api/system/user";
  import {
    validPassword
  } from "@/utils/validate";
  import store from '@/store'
  import {
    isRelogin
  } from '@/utils/request'
  import router from '@/router'
  import {
    encryptByPublickKey
  } from '@/utils/jsencrypt'

  export default {
    data() {
      const equalToPassword = (rule, value, callback) => {
        if (this.user.newPassword !== value) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      };
      const validatePassword = (rule, value, callback) => {
        if (validPassword(value)) callback()
        else callback(new Error('密码6-20位，必须包含大写字母，小写字母，数字及特殊字符'))
      }
      return {
        user: {
          oldPassword: undefined,
          newPassword: undefined,
          confirmPassword: undefined
        },
        // 表单校验
        rules: {
          oldPassword: [{
            required: true,
            message: "旧密码不能为空",
            trigger: "blur"
          }],
          newPassword: [{
              required: true,
              message: "新密码不能为空",
              trigger: "blur"
            },
            {
              min: 6,
              max: 20,
              message: "长度在 6 到 20 个字符",
              trigger: "blur"
            },
            {
              required: true,
              trigger: "blur",
              validator: validatePassword
            },
          ],
          confirmPassword: [{
              required: true,
              message: "确认密码不能为空",
              trigger: "blur"
            },
            {
              required: true,
              validator: equalToPassword,
              trigger: "blur"
            }
          ]
        },
        redirect: undefined,
      };
    },
    watch: {
      $route: {
        handler: function(route) {
          this.redirect = route.query && route.query.redirect;
        },
        immediate: true
      }
    },
    methods: {
      submit() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            updateUserPwd(encryptByPublickKey(this.user.oldPassword), encryptByPublickKey(this.user.newPassword))
              .then(response => {
                this.$modal.msgSuccess("修改成功");
                store.dispatch('GetInfo').then(() => {
                  isRelogin.show = false
                  store.dispatch('GenerateRoutes').then(accessRoutes => {
                    // 根据roles权限生成可访问的路由表
                    router.addRoutes(accessRoutes) // 动态添加可访问路由表
                  })
                  this.$tab.closeOpenPage(this.redirect || "/");
                })
              });
          }
        });
      },
    }
  };
</script>