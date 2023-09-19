<template>
  <el-tooltip placement="top">
    <div slot="content">
      <div v-for="(item, index) in options">
        <span>{{item.raw.dictLabel}}</span>
        <span>：</span>
        <span>{{item.raw.remark}}</span>
      </div>
    </div>
    <i class="el-icon-question"></i>
  </el-tooltip>
</template>

<script>
  export default {
    name: "DictTooltip",
    props: {
      options: {
        type: Array,
        default: null,
      },
    },
  };
</script>
<style scoped>
</style>