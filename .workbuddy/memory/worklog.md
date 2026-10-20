# VIPCard 工作日志

## 2026-10-20

### 初始版本：会员卡管理 App
- 数据层：`MemberCard.kt`（id/商店名/手机号）+ `CardRepository.kt`（JSON 文件持久化，零额外依赖）
- 列表页：`MainActivity` + `CardAdapter`（RecyclerView）+ FAB 添加 + 删除确认弹窗
- 展示页：`DisplayCardActivity` 强制横屏、沉浸式全屏、深色背景、商店名小字（28sp）+ 手机号 auto-size 大字（24–200sp 自适应占满）、11 位手机号格式化为 `138 1234 5678`、屏幕常亮、点击返回
- compileSdk/targetSdk 升到 37（androidx.core 1.19.0 要求）
- 构建通过：assembleDebug BUILD SUCCESSFUL

### 优化 1：整卡点击跳转展示 + 编辑功能
- 列表卡片整体可点击 → 直接进入横屏展示页（移除单独展示按钮）
- 新增编辑：`AddCardDialogFragment` 重构为 `EditCardDialogFragment`（支持新增/编辑双模式，通过 arguments 传入卡片数据）
- `CardRepository.updateCard()` 按 id 更新
- item 卡片右侧改为：编辑（铅笔）+ 删除（垃圾桶）
- 构建通过：assembleDebug BUILD SUCCESSFUL

### 优化 2：横屏展示页可读性
- 背景改为纯黑 `#FF000000`（OLED 省电、对比度最高）
- 手机号改用 monospace 等宽字体：1 带衬线脚，与 7/l 明显区分；字符等宽，店员照抄不易错位
- 分组 3-4-4、组间三个空格（`138   1234   5678`）；10 位号码支持 3-4-3
- 移除发光阴影（避免笔画发虚），加粗纯白最大化清晰度
- 构建通过：assembleDebug BUILD SUCCESSFUL

### 优化 3：展示页字号/高亮（2026-10-20）
- 手机号 autoSize 上限 200sp→400sp，padding 32dp→12/8dp，商店名 28sp→20sp，让字号尽可能占满
- 中间四位用醒目黄色 `#FFD600`（ForegroundColorSpan），前后白色，方便店员核对区段
- 构建通过：assembleDebug BUILD SUCCESSFUL

### 优化 4：全 App 深色主题（简约高级风）
- 主题改为 `Theme.Material3.Dark.NoActionBar`，删除 values-night（恒为深色）
- 配色：背景 `#0B0B0D`、卡片 `#17171B`、描边 `#26FFFFFF`、香槟金主色 `#D4B36A`、正文 `#F2F2F5`、次要 `#8E8E96`
- 卡片：0 高度/16dp 圆角/1dp 细描边，编辑灰色、删除柔和红 `#E57373`
- FAB 金色+深色图标；工具栏标题加宽字距（0.1）不加阴影
- 新增 `VipCardApp` 强制 `MODE_NIGHT_YES`，保证亮色系统下状态栏图标也是浅色
- 已在真机安装验证：列表、横屏展示页、添加弹窗均正常
- 构建通过：assembleDebug BUILD SUCCESSFUL

### 优化 5：主色金色 → 红色
- 主色改为 `#EF4444`（亮红），其上内容（FAB 图标等）改白色
- 颜色角色重命名 `gold`/`on_gold` → `accent`/`on_accent`，主题与布局引用统一更新
- 展示页中间四位高亮仍为黄色（可读性优先，未随主题变动）
- 真机验证 FAB、弹窗按钮均为红色

### 优化 6：列表编辑/删除图标替换
- 弃用老式系统图标 `ic_menu_edit`/`ic_menu_delete`
- 新增矢量图标 `ic_edit.xml`（Material 铅笔）、`ic_delete.xml`（Material 垃圾桶轮廓）
- 保留灰色/柔和红着色，真机验证显示正常

### 优化 7：展示页中间四位改为主题红
- 取消硬编码黄色，改为运行时读取 `R.color.accent`（主题红），真机验证显示正常

### 优化 8：X 关闭按钮 + 默认卡自动打开
- 展示页取消点击空白关闭，右上角新增半透明圆形 X 按钮关闭（ic_close）
- 列表每张卡新增星标按钮：点按设为唯一默认（实心红星），再点取消；默认卡商店名旁显示"默认"徽章
- 默认卡 id 存 SharedPreferences（`vipcard_prefs/default_card_id`），删除默认卡时自动清除
- 冷启动（onCreate 且非重建）自动进入默认卡横屏展示页
- 真机验证：X 关闭、星标/徽章、杀掉进程重开自动进入展示页均正常

### 用户调整：展示页布局重构（2026-10-20）
- 用户自行将展示页改为 ConstraintLayout：手机号占满全屏居中，X 移至左上角，商店名顶部居中与 X 同高
- 代码无需改动（textStoreName/textPhoneNumber/btnClose id 均保留），构建与真机显示验证正常

### 优化 9：点击手机号系统播报（TTS）
- 展示页点击大号手机号 → TextToSpeech 中文数字逐位播报（一五六，六六一九，八一零二），语速 0.85
- TTS 未就绪时暂存点击，初始化完成后自动播报；onDestroy 停止并释放引擎
- 无 TTS 引擎的设备弹 Toast「系统未安装语音播报引擎，无法播报」
- 注意：一加测试机（ColorOS）未安装任何 TTS 引擎（`query-services TTS_SERVICE` 为空），仅能看到提示；装 Google 文字转语音等引擎后可正常播报

### 优化 10：语音播报改为独立按钮 + TTS 引擎可见性修复
- 展示页取消「点击手机号播报」；右上角新增独立圆形「语音播报」按钮（`ic_volume_up`，contentDescription=语音播报），与左上角 X 对称
- 按钮继续调用原 `speakPhoneNumber()`：中文数字逐位 + 逗号分组，语速 0.85，未就绪时暂存
- 修复 Android 11+ 包可见性导致 TTS 引擎不可见：Manifest 增加 `<queries>` 声明 `android.intent.action.TTS_SERVICE`
  - 修复前找不到引擎、误弹「系统未安装语音播报引擎」；修复后日志 `Sucessfully bound to com.xiaomi.mibrain.speech`
- 删除临时冒烟测试 `TtsSmokeTest.kt`
- 构建通过；真机截图确认布局，uiautomator 确认 `btnSpeak` clickable=true、边界与 X 对称
- 限制：该机系统禁止 adb 注入点击（INJECT_EVENTS），未能用 adb 实际点按验证声音输出

### 优化 11：播报数字读法适配
- 电话习惯：`1` 朗读为「幺」，其余仍按标准中文数字
- 3-4-4 分组处用「，」停顿，例：15666666666 → 「幺五六，六六六六，六六六六」
- 构建通过并安装

### 调整：播报去掉逐位停顿
- 按用户要求取消每位数字之间的「、」，仅在 3-4-4 分组处保留「，」
- 保持 `1` 读「幺」；构建并安装通过

### 优化 12：固定屏幕方向
- `MainActivity` 固定竖屏、`DisplayCardActivity` 固定横屏，均写入 Manifest `android:screenOrientation`
- 真机验证：主界面截图 1200×2608（竖屏），展示页仍 2608×1200（横屏）
- 为截取主界面曾临时移开默认卡偏好，测试后已原样恢复，确认「默认」徽章与自动打开正常

### 文档：新增 GitHub README
- 新增根目录 `README.md`：功能、使用方式、技术栈、环境要求、构建命令、项目结构、数据存储、TTS 说明
- 未附带 License，README 中已注明

### 隐私：手机号脱敏
- 将 README、worklog、展示页布局预览中的手机号示例统一替换为 15666666666
- 已全局检索确认源码与文档中不再包含原号码；构建通过

### 构建：Release 签名配置
- `app/build.gradle.kts` 读取根目录 `keystore.properties`，存在时自动创建并应用 release 签名；不存在时保持输出未签名包
- `.gitignore` 新增 `*.jks`、`*.keystore`、`keystore.properties`
- 用临时 keystore 验证 signed release 生成成功（apksigner V2 校验通过），随后删除临时文件并确认回退为 `app-release-unsigned.apk`
- 正式签名只需在根目录放入自己的 `.jks` 并配置 `keystore.properties`，再执行 `./gradlew assembleRelease`

### 构建：生成正式签名密钥与配置
- 已生成 `vipcard-release.jks`（alias `vipcard`，RSA 2048，有效期 10000 天）与 `keystore.properties`
- 密码为随机生成，仅写入 `keystore.properties`，未记录到日志
- 两者均已被 `.gitignore` 排除，不会上传 GitHub
- `assembleRelease` 已生成签名版 `app-release.apk`，apksigner V2 校验通过（CN=VIP Card）
- 注意：`.jks` 与 `keystore.properties` 必须妥善备份，丢失后将无法更新已发布应用

### 发布：GitHub Release v1.0.0
- 在 `OrangeBoston/VIPCard` 创建并 published Release：标签 `v1.0.0`（由 master `e8bbcd8` 新建），标题「VIP Card v1.0.0」，标记为 Latest
- 页面：https://github.com/OrangeBoston/VIPCard/releases/tag/v1.0.0
- 附件 `VIPCard-v1.0.0.apk`（签名版，5,689,725 字节，SHA-256 `87b8f81508043accba576d3ba2f73e33fc21e46c37280dfd711d3391c0ff9d87`）；下载链接已实测返回 200 且大小一致
- 说明正文仅包含功能、安装要求与本地存储说明，手机号示例使用 15666666666，无个人信息、无签名口令
- 用于上传的根目录临时副本已删除；`.gitignore` 新增 `*.apk` 避免误提交构建产物
