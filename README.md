# QuickLaunch

---


**QuickLaunch** 是一款基于 Android 的高效辅助启动工具，旨在解决手机应用过多、查找繁琐的痛点。它不仅仅是一个启动器，更是一个高度可定制的**应用索引系统**。

通过自定义别名、标签（Tags）以及强大的分类管理，配合全局悬浮窗，无论你在手机的哪个界面，都能在 1 秒内找到并启动你需要的 App。

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg) ![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-green.svg) ![Android](https://img.shields.io/badge/Android-15-orange.svg)

---

## ✨ 核心功能 (Features)

### 1. 🧠 智能搜索与自定义 (Smart Search & DIY)
*   **自定义别名**：给 App 起个只有你懂的名字（如把“Bilibili”改名为“学习资料”）。
*   **标签系统 (Tagging)**：给应用打上多个标签（如 `[游戏]`, `[摸鱼]`, `[办公]`），搜索标签即可找到相关应用。
*   **多维检索**：支持搜索 应用原名、包名、自定义别名、标签。

### 2. 📂 强大的分类管理 (Category Management)
*   **默认智能分类**：内置国内主流 App 知识库，安装即自动归类（社交、支付、视频等）。
*   **完全自定义**：支持新建、删除、重命名分类。
*   **自由排序**：分类顺序可上下调整，应用归属可自由勾选。
*   **便捷操作**：分类列表末尾的一键添加按钮，长按应用即可快速移除。

### 3. 👻 灵动悬浮窗 (Global Floating Window)
*   **全局呼出**：在任何界面悬浮，随时待命。
*   **智能隐藏**：不使用时自动吸附屏幕边缘并**隐藏 2/3**，且变为半透明，绝不遮挡视线。
*   **快速搜索**：点击展开为搜索框，启动应用后自动收起。
*   **快捷开关**：集成 Android 系统下拉通知栏快捷开关 (Quick Settings Tile)，一键开启/关闭。

### 4. 📊 智能推荐与统计 (Stats & Recommendation)
*   **常用推荐**：主页根据算法自动推荐使用频率最高的 Top 10 应用。
*   **使用统计**：详细记录应用的启动次数和最后启动时间，生成排行榜。
*   **我的收藏**：支持手动置顶常用 App 到主页网格。

---

## 🛠 技术栈 (Tech Stack)

本项目完全使用 **Kotlin** 编写，采用现代化的 Android 开发技术：

*   **UI 框架**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3 Design) - 声明式 UI，代码简洁美观。
*   **架构**: MVVM (Model-View-ViewModel) 思想。
*   **数据存储**: `SharedPreferences` + `Gson` - 轻量级本地对象存储。
*   **系统集成**:
    *   `Service` & `WindowManager`: 实现全局悬浮窗。
    *   `TileService`: 实现系统下拉栏快捷开关。
    *   `PackageManager`: 获取本机已安装应用信息。
*   **交互处理**:
    *   `combinedClickable`: 处理点击与长按事件。
    *   `detectDragGestures`: 实现悬浮窗的丝滑拖拽。

---

## 📸 截图展示 (Screenshots)

*(建议你运行 App 后截图，放在这里)*

| 主页 (Home) | 分类管理 (Category) | 悬浮窗 (Floating) | 编辑弹窗 (Edit) |
|:---:|:---:|:---:|:---:|
| ![Home](path/to/screenshot1.png) | ![Category](path/to/screenshot2.png) | ![Floating](path/to/screenshot3.png) | ![Edit](path/to/screenshot4.png) |

---

## 📥 

**注意权限**：
*   首次使用悬浮窗功能时，需授予 **“显示在其他应用上层”** 权限。
*   应用会自动申请 **“获取应用列表”** 权限。

---

## 📄 许可证 (License)

MIT License

Copyright (c) 2026 Sephuan
