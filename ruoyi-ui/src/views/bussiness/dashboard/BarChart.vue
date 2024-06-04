<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({ dayData, orderAmtData, orderCntData } = {}) {
      this.chart.clear()
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: dayData,
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            interval: 0,
            rotate: 40
          }
        }],
        yAxis: [{
          type: 'value',
          name: '金额',
          axisLabel: {
            formatter: '{value} 元'
          },
          axisTick: {
            show: false
          }
        }, {
          type: 'value',
          name: '订单数',
          axisLabel: {
            formatter: '{value} 笔'
          },
          axisTick: {
            show: false
          }
        }],
        series: [{
          name: '订单总额',
          type: 'bar',
          stack: 'vistors',
          data: orderAmtData,
          animationDuration: 1500,
          animationEasing: 'cubicInOut'
        }, {
          name: '订单笔数',
          type: 'line',
          yAxisIndex: 1,
          data: orderCntData,
          itemStyle: {
            normal: {
              color: '#CC5A5A',
              lineStyle: {
                color: '#CC5A5A',
                width: 2
              }
            }
          },
          animationDuration: 2000,
          animationEasing: 'cubicInOut'
        }]
      })
    }
  }
}
</script>
