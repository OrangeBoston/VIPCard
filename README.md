# 我的会员卡 · VIP Card

一个离线、无广告的本地会员卡管理应用，面向门店/柜台场景：把店铺名称和会员手机号存在手机里，需要时一键横屏放大展示，方便店员快速报号或录入。

## 功能

- **本地管理会员卡**：列表展示、添加、编辑、删除，数据只保存在本机。
- **横屏大字展示**：点击卡片进入纯黑横屏页面，手机号自动放大至铺满屏幕，中间四位用主题红高亮，分组清晰，远处也能一眼看清。
- **语音播报**：展示页右上角一键播报手机号；中文逐位朗读，避免被当成一个数字连读（如 `15666666666` → 「幺五六，六六六六，六六六六」）。
- **默认卡直显**：在列表中点亮星标，将某张卡设为唯一「默认」；每次冷启动自动打开默认卡的展示页。
- **深色简约界面**：全局深色主题，以红色作为强调色，适合门店低光环境。
- **固定屏幕方向**：列表页固定竖屏，展示页固定横屏。
- **无网络权限**：不联网、不收集数据，适合存放会员信息。

## 使用方式

1. 打开应用，点击右下角「+」添加店铺名称与手机号。
2. 点击卡片即可进入展示页，横屏显示超大手机号。
3. 点击展示页右上角的喇叭按钮，可语音播报号码。
4. 在列表中点击卡片右侧的星标，将其设为默认卡；再次点击可取消。设置默认卡后，下次启动应用会直接进入该卡的展示页。
5. 点击展示页左上角的「×」或按返回键退出展示。

## 技术栈

- Kotlin
- Android Views / Material 3（深色主题）
- RecyclerView + ListAdapter / DiffUtil
- ConstraintLayout
- `TextToSpeech`（系统语音引擎）
- 本地持久化：JSON 文件 + SharedPreferences，无数据库、无网络依赖

## 环境要求

| 项目 | 版本 |
| --- | --- |
| 最低系统 | Android 7.0（API 24） |
| 编译/目标 SDK | API 37 |
| Gradle | 9.4.1（使用 Wrapper） |
| Android Gradle Plugin | 9.2.1 |
| JDK | 17 及以上 |
| 开发工具 | Android Studio（支持 AGP 9.2.1） |

> 首次构建前请确认已安装 Android SDK Platform 37，并在 `local.properties` 中配置 SDK 路径（该文件已被 `.gitignore` 忽略）。

## 构建

```bash
git clone <your-repo-url>
cd VIPCard

# 构建 Debug 包
./gradlew assembleDebug

# 安装到已连接的设备
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

构建产物位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 项目结构

```text
app/src/main/
├── java/com/orangeboston/vipcard/
│   ├── MainActivity.kt              # 卡片列表、默认卡与增删改入口
│   ├── DisplayCardActivity.kt       # 横屏展示与语音播报
│   ├── EditCardDialogFragment.kt    # 添加 / 编辑会员卡弹窗
│   ├── CardAdapter.kt               # 列表适配器（星标、徽章、编辑、删除）
│   ├── CardRepository.kt            # 卡片存储与默认卡偏好
│   ├── MemberCard.kt                # 会员卡数据模型
│   └── VipCardApp.kt                # 应用入口，强制深色模式
├── res/
│   ├── layout/                      # 列表、展示页、列表项等布局
│   ├── drawable/                    # 图标与背景
│   └── values/                      # 深色主题、颜色、文案
└── AndroidManifest.xml
```

## 数据存储

- **会员卡数据**：`files/cards.json`（应用内部存储，JSON 格式）。
- **默认卡 ID**：`SharedPreferences`（`vipcard_prefs/default_card_id`）。
- 应用不申请网络权限，数据不会离开设备；卸载应用后数据随之清除，请注意自行备份。

## 语音播报说明

- 播报依赖系统 TTS 引擎；若设备未安装中文语音引擎，点击播报按钮会提示「系统未安装语音播报引擎，无法播报」。
- 数字采用电话习惯读法：`1` 读作「幺」，并在分段处加入停顿（11 位按 3-4-4，10 位按 3-4-3），尽量避免引擎把手机号读成一个大数字。
- 已在小米设备（`com.xiaomi.mibrain.speech`）上验证引擎绑定与播报流程。

## License

本项目未附带开源许可证。如需公开分发或商用，请先补充合适的 License 文件。
