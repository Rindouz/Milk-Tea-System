"use strict";
const common_vendor = require("../../common/vendor.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "list",
  setup(__props) {
    const statusTabs = [
      { label: "全部", value: null },
      { label: "待支付", value: 0 },
      { label: "待取餐", value: 1 },
      { label: "制作中", value: 3 },
      { label: "已完成", value: 2 },
      { label: "已取消", value: 4 }
    ];
    const activeStatus = common_vendor.ref(null);
    const orderList = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const getStatusText = (status) => {
      const map = { 0: "待支付", 1: "待取餐", 2: "已完成", 3: "制作中", 4: "已取消" };
      return map[status] || "未知";
    };
    const getStatusColor = (status) => {
      const map = { 0: "#f39c12", 1: "#3498db", 2: "#2ecc71", 3: "#9b59b6", 4: "#95a5a6" };
      return map[status] || "#333";
    };
    const selectStatus = (value) => {
      activeStatus.value = value;
      loadOrders();
    };
    const loadOrders = async () => {
      loading.value = true;
      try {
        const userId = common_vendor.index.getStorageSync("userId") || 1;
        const res = await api_index.orderApi.list(userId, activeStatus.value);
        orderList.value = res.data || [];
      } catch (e) {
        orderList.value = [];
      } finally {
        loading.value = false;
      }
    };
    const onRefresh = async () => {
      await loadOrders();
    };
    const showDetail = async (order) => {
      try {
        const res = await api_index.orderApi.detail(order.orderNo);
        const detail = res.data;
        if (detail && detail.orderItems) {
          const itemsText = detail.orderItems.map((i) => `${i.productName} x${i.quantity} ¥${i.price}`).join("\n");
          common_vendor.index.showModal({
            title: "订单详情",
            content: `订单号：${detail.orderNo}
状态：${getStatusText(detail.orderStatus)}
总金额：¥${detail.totalAmount}

${itemsText}`,
            showCancel: false
          });
        }
      } catch (e) {
      }
    };
    const cancelOrder = async (orderNo) => {
      const res = await common_vendor.index.showModal({ title: "提示", content: "确定要取消该订单吗？" });
      if (res.confirm) {
        try {
          await api_index.orderApi.cancel(orderNo);
          common_vendor.index.showToast({ title: "已取消", icon: "success" });
          loadOrders();
        } catch (e) {
          common_vendor.index.showToast({ title: "取消失败", icon: "none" });
        }
      }
    };
    common_vendor.onMounted(() => {
      loadOrders();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(statusTabs, (tab, k0, i0) => {
          return {
            a: common_vendor.t(tab.label),
            b: tab.value,
            c: activeStatus.value === tab.value ? 1 : "",
            d: common_vendor.o(($event) => selectStatus(tab.value), tab.value)
          };
        }),
        b: orderList.value.length === 0 && !loading.value
      }, orderList.value.length === 0 && !loading.value ? {} : {}, {
        c: common_vendor.f(orderList.value, (order, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(order.orderNo),
            b: common_vendor.t(getStatusText(order.orderStatus)),
            c: getStatusColor(order.orderStatus),
            d: common_vendor.t(order.totalAmount),
            e: common_vendor.t(order.createTime),
            f: order.orderStatus === 0
          }, order.orderStatus === 0 ? {
            g: common_vendor.o(($event) => cancelOrder(order.orderNo), order.orderNo)
          } : {}, {
            h: order.orderNo,
            i: common_vendor.o(($event) => showDetail(order), order.orderNo)
          });
        }),
        d: common_vendor.o(onRefresh, "88")
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-456ecf67"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/list.js.map
