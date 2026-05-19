"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
const store_auth = require("./store/auth.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/menu/index.js";
  "./pages/product/detail.js";
  "./pages/cart/index.js";
  "./pages/order/create.js";
  "./pages/order/list.js";
  "./pages/user/index.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:6", "App Launch");
    const authStore = store_auth.useAuthStore();
    if (authStore.token) {
      authStore.isLogin = true;
    }
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:13", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:16", "App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  const pinia = common_vendor.createPinia();
  app.use(pinia);
  return { app };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
