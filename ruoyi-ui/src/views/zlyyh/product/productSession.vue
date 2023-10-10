<template>
  <div v-if="ticketSession">
    <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addSessionRow()">新增场次与票种
    </el-button>
    <el-table :data="ticketSession" ref="table" style="width: 100%" default-expand-all>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table :data="props.row.ticketLine">
            <el-table-column :render-header="renderHeader" label="票种名称" align="center" prop="lineTitle">
              <template slot-scope="scope">
                <el-input v-model="scope.row.lineTitle" placeholder="请输入票种名称"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="外部产品编号" align="center" prop="lineTitle">
              <template slot-scope="scope">
                <el-input v-model="scope.row.otherId" placeholder="请输入外部产品编号"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="市场价格" align="center" prop="linePrice">
              <template slot-scope="scope">
                <el-input v-model="scope.row.linePrice" placeholder="请输入市场价格"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="售价" align="center" prop="lineSettlePrice">
              <template slot-scope="scope">
                <el-input v-model="scope.row.lineSettlePrice" placeholder="请输入售价"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="总数量" align="center" prop="lineNumber">
              <template slot-scope="scope">
                <el-input v-model="scope.row.lineNumber" placeholder="请输入总数量"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="单次购买上限" align="center" prop="lineUpperLimit">
              <template slot-scope="scope">
                <el-input v-model="scope.row.lineUpperLimit" placeholder="请输入单次购买上限"/>
              </template>
            </el-table-column>
            <el-table-column :render-header="renderHeader" label="状态" align="center" prop="lineStatus">
              <template slot-scope="scope">
                <el-select v-model="scope.row.lineStatus" placeholder="请选择状态">
                  <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
                             :value="dict.value"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-delete"
                           @click="delLineRow(props.row,scope.row)" v-hasPermi="['zlyyh:productTicketSession:remove']">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column :render-header="renderHeader" label="场次名称" align="center" prop="lineTitle">
        <template slot-scope="scope">
          <el-input v-model="scope.row.session" placeholder="请输入场次名称"/>
        </template>
      </el-table-column>
      <el-table-column :render-header="renderHeader" label="状态" align="center" prop="lineStatus">
        <template slot-scope="scope">
          <el-select v-model="scope.row.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
                       :value="dict.value"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column :render-header="renderHeader" label="是否预约范围" align="center" prop="isRange">
        <template slot-scope="scope">
          <el-select v-model="scope.row.isRange" placeholder="请选择状态">
            <el-option v-for="dict in ticketStatusList" :key="dict.value" :label="dict.label"
                       :value="dict.value"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="观影时间" align="center" prop="date">
        <template slot-scope="scope">
          <el-date-picker clearable v-model="scope.row.date" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择日期">
          </el-date-picker>
        </template>
      </el-table-column>
      <el-table-column label="预约开始日期" align="left" prop="beginDate">
        <template slot-scope="scope">
          <el-date-picker v-model="scope.row.beginDate" type="date" value-format="yyyy-MM-dd"
                          placeholder="请选择预约开始日期">
          </el-date-picker>
        </template>
      </el-table-column>
      <el-table-column label="预约结束日期" align="left" prop="endDate">
        <template slot-scope="scope">
          <el-date-picker v-model="scope.row.endDate" type="date" value-format="yyyy-MM-dd"
                          placeholder="请选择预约结束日期">
          </el-date-picker>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" plain size="mini" @click="addLineRow(scope.row)">新增票种
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="delSessionRow(scope.row)"
                     v-hasPermi="['zlyyh:productTicketSession:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: "productSession",
  dicts: ['sys_normal_disable'],
  props: {
    ticketSession: {
      type: Array,
      default: undefined
    },
  },
  data() {
    return {
      ticketStatusList: [{
        value: '0',
        label: '是'
      },
        {
          value: '1',
          label: '否'
        },
      ],
    };
  },
  methods: {
    // 新增场次
    addSessionRow() {
      if (this.ticketSession.length === 3) {
        this.$modal.msg("场次已达上限。");
        return;
      }
      const row = {
        productId: undefined,
        sessionId: undefined,
        session: undefined,
        status: undefined,
        date: undefined,
        ticketLine: [],
      };
      this.ticketSession.push(row)
      this.addLineRow(row);
    },
    // 删除场次
    delSessionRow(row) {
      const index = this.ticketSession.indexOf(row)
      this.ticketSession.splice(index, 1);
    },
    // 新增票种
    addLineRow(row) {
      if (row.ticketLine.length === 3) {
        this.$modal.msg("票种已达上限。");
        return;
      }
      const rows = {
        lineId: undefined,
        productId: undefined,
        otherId: undefined,
        sessionId: undefined,
        lineTitle: undefined,
        linePrice: undefined,
        lineSettlePrice: undefined,
        lineNumber: undefined,
        lineUpperLimit: undefined,
        lineStatus: undefined
      };
      row.ticketLine.push(rows);
    }, // 删除票种
    delLineRow(row1, row2) {
      const index = row1.ticketLine.indexOf(row2)
      row1.ticketLine.splice(index, 1);
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
