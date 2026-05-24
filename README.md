# QuickLaunch

**QuickLaunch** 是一款高度可定制的 Android 应用快速启动器，聚合搜索、分类管理、悬浮窗、快捷磁贴于一体。

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue) ![Compose](https://img.shields.io/badge/Compose-Material3-purple) ![API](https://img.shields.io/badge/API-24%2B-brightgreen) ![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 功能

| 模块 | 说明 |
|------|------|
| 智能搜索 | 支持应用名、包名、自定义别名、标签多维检索，评分排序 |
| 分类管理 | 内置国内主流应用知识库，可新建/重命名/拖拽排序/管理应用归属 |
| 悬浮搜索 | 全局悬浮球，可拖拽、自动吸附边缘半透明隐藏，点击展开搜索卡片 |
| 快捷磁贴 | 最多 12 个自定义 QS 磁贴，直接快启指定应用，可选 14 种图标 |
| 使用统计 | 按启动频率排行榜，支持长按编辑别名/标签/置顶 |
| 设置中心 | 可折叠分组，语言切换（中/英），MD3 主题取色（莫奈/8 种自定义+自动轮换），沉浸模式 |
| 通知栏搜索 | 下拉通知栏展开悬浮窗通知，输入关键词直接搜索并启动 |

---

## 技术栈

- **Kotlin 2.0** + **Jetpack Compose Material3**
- Navigation Compose、LifecycleService、AppCompat
- SharedPreferences + Gson 本地存储
- WindowManager 悬浮窗、TileService 快捷磁贴、RemoteInput 通知栏搜索
- Coil 图标懒加载

---

## 下载

[最新 Release](https://github.com/Sephuan/QuickLaunch/releases/latest)

---

## 权限

- 悬浮窗权限（显示在其他应用上层）
- 获取应用列表（QUERY_ALL_PACKAGES）
- 前台服务（保持悬浮窗存活）

---

## License

MIT © 2026 Sephuan
