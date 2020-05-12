# 基于Spring Cloud的学生管理平台（Android学生端）

[![GitHub stars](https://img.shields.io/github/stars/itning/smp-android.svg?style=social&label=Stars)](https://github.com/itning/smp-android/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/itning/smp-android.svg?style=social&label=Fork)](https://github.com/itning/smp-android/network/members)
[![GitHub watchers](https://img.shields.io/github/watchers/itning/smp-android.svg?style=social&label=Watch)](https://github.com/itning/smp-android/watchers)
[![GitHub followers](https://img.shields.io/github/followers/itning.svg?style=social&label=Follow)](https://github.com/itning?tab=followers)

[![GitHub issues](https://img.shields.io/github/issues/itning/smp-android.svg)](https://github.com/itning/smp-android/issues)
[![GitHub license](https://img.shields.io/github/license/itning/smp-android.svg)](https://github.com/itning/smp-android/blob/master/LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/itning/smp-android.svg)](https://github.com/itning/smp-android/commits)
[![GitHub release](https://img.shields.io/github/release/itning/smp-android.svg)](https://github.com/itning/smp-android/releases)
[![GitHub repo size in bytes](https://img.shields.io/github/repo-size/itning/smp-android.svg)](https://github.com/itning/smp-android)
[![HitCount](http://hits.dwyl.io/itning/smp-android.svg)](http://hits.dwyl.io/itning/smp-android)
[![language](https://img.shields.io/badge/language-JAVA-green.svg)](https://github.com/itning/smp-android)

## 工程

1. 前端项目
   - [Vue.JS 实现](https://github.com/itning/smp-client)
   - [Angular 实现](https://github.com/itning/smp-client-angular)
2. Android移动端项目
   - [教师端](https://github.com/itning/smp-android-teacher)
   - [学生端](https://github.com/itning/smp-android)
3. 后端
   - [Spring Cloud](https://github.com/itning/smp-server)
4. 人脸识别模型库
   - [smp-ext-lib](https://gitee.com/itning/smp-ext-lib)
5. 统一配置中心数据存放仓库
   - [smp-server-config](https://gitee.com/itning/smp-server-config)

## 依赖

### 编译器

| 编译器            | 版本      |
| ----------------- | --------- |
| android studio    | 3.5.3+    |
| intellij idea     | 2019.3.1+ |
| intellij webstorm | 2019.3.1+ |

### 编译与运行

| 依赖                    | 版本                |
| ----------------------- | ------------------- |
| Java SE Development Kit | 8u231 (JDK 8<JDK11) |
| maven                   | 3.6.3+              |
| node.js                 | 12.14.0+            |
| yarn                    | 1.21.1+             |
| npm                     | 6.13.4+             |
| mysql                   | 8.0.18+             |

## 项目编译

**minSdkVersion 28**
**手机必须是android p (9 sdk28) 以上**

```bash
# 查看构建版本
./gradlew -v
# 清除build文件夹
./gradlew clean
# 检查依赖并编译打包
./gradlew build
# 编译并安装debug包
./gradlew installDebug
# 编译并打印日志
./gradlew build --info
# 译并输出性能报告，性能报告一般在 构建工程根目录 build/reports/profile
./gradlew build --profile
# 调试模式构建并打印堆栈日志
./gradlew build --info --debug --stacktrace
# 强制更新最新依赖，清除构建并构建
./gradlew clean build --refresh-dependencies
```

## 预览

![a](https://raw.githubusercontent.com/itning/smp-android/master/pic/a.jpg)

![b](https://raw.githubusercontent.com/itning/smp-android/master/pic/b.jpg)

![c](https://raw.githubusercontent.com/itning/smp-android/master/pic/c.jpg)

![d](https://raw.githubusercontent.com/itning/smp-android/master/pic/d.jpg)

![e](https://raw.githubusercontent.com/itning/smp-android/master/pic/e.jpg)

![f](https://raw.githubusercontent.com/itning/smp-android/master/pic/f.jpg)

![g](https://raw.githubusercontent.com/itning/smp-android/master/pic/g.jpg)

![h](https://raw.githubusercontent.com/itning/smp-android/master/pic/h.jpg)

![i](https://raw.githubusercontent.com/itning/smp-android/master/pic/i.jpg)

![j](https://raw.githubusercontent.com/itning/smp-android/master/pic/j.jpg)

## 版权声明

该项目仅用于学习，禁止用于商业用途。

项目是我个人毕业项目，**不建议作为您毕业项目来使用**。