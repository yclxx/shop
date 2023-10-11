<template>
  <div v-if="ticket">
    <el-row>
      <el-col :span="8">
        <el-form-item label="票形式" :required="true">
          <el-select v-model="ticket.ticketForm" placeholder="请选择票形式" style="width: 100%;">
            <el-option v-for="item in ticketFormList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="身份信息" :required="true">
          <el-select v-model="ticket.ticketCard" placeholder="请选择身份信息" style="width: 100%;">
            <el-option v-for="item in ticketCardList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="不支持退" :required="true">
          <el-select v-model="ticket.ticketNonsupport" placeholder="请选择不支持退" style="width: 100%;">
            <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <el-form-item label="电子发票" :required="true">
          <el-select v-model="ticket.ticketInvoice" placeholder="请选择电子发票" style="width: 100%;">
            <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="过期退" :required="true">
          <el-select v-model="ticket.ticketExpired" placeholder="请选择过期退" style="width: 100%;">
            <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="随时退" :required="true">
          <el-select v-model="ticket.ticketAnyTime" placeholder="请选择随时退" style="width: 100%;">
            <el-option v-for="item in ticketStatusList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <el-form-item label="选座方式" :required="true">
          <el-select v-model="ticket.ticketChooseSeat" placeholder="请选择快递方式" style="width: 100%;">
            <el-option v-for="item in ticketChooseSeatList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="快递方式" :required="true">
          <el-select v-model="ticket.ticketPostWay" placeholder="请选择快递方式" style="width: 100%;">
            <el-option v-for="item in ticketPostWayList" :key="item.value" :label="item.label"
                       :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8" v-if="ticket.ticketPostWay === '2'">
        <el-form-item label="邮费">
          <el-input v-model="ticket.ticketPostage" placeholder="请输入邮费"/>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="16">
        <el-form-item label="须知" prop="ticketNotice">
          <editor v-model="ticket.ticketNotice" :min-height="192"/>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "productTicket",
  props: {
    ticket: {
      type: Object,
      default: undefined
    },
  },
  dicts: [],
  data() {
    return {
      ticketChooseSeatList: [
        {
          value: '1',
          label: '在线选座'
        },
        {
          value: '2',
          label: '先到先得'
        },
        {
          value: '3',
          label: '无座'
        },
        {
          value: '4',
          label: '实体票'
        }
      ],
      ticketFormList: [{
        value: '1',
        label: '电子票'
      },
        {
          value: '2',
          label: '实体票'
        }
      ],
      ticketCardList: [{
        value: '0',
        label: '必须填写'
      },
        {
          value: '1',
          label: '无需填写'
        }
      ],
      ticketStatusList: [{
        value: '0',
        label: '是'
      },
        {
          value: '1',
          label: '否'
        },
      ],
      ticketPostWayList: [{
        value: '0',
        label: '无需邮寄'
      },
        {
          value: '1',
          label: '包邮'
        },
        {
          value: '2',
          label: '另付邮费'
        },
      ],
    };
  },

  methods: {}
};
</script>
