import { createApp } from 'vue'
<<<<<<< HEAD
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
=======
import ElementPlus from 'element-plus' // 导入ElementPlus UI组件库
import 'element-plus/dist/index.css' // 导入ElementPlus样式
import './styles/global.css' // 导入我们自己写的全局样式
import App from './App.vue'
import router from './router' // 导入路由配置

const app = createApp(App)
app.use(ElementPlus) // 注册ElementPlus，所有组件都能使用它的UI组件
app.use(router) // 注册路由系统
app.mount('#app') // 将Vue应用挂载到HTML页面的#app元素上
>>>>>>> 09dc229a25f3a480d8922bf9e3b668917763e80e
