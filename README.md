# XJar-Maven-Plugin [![](https://jitpack.io/v/core-lib/xjar-maven-plugin.svg)](https://jitpack.io/#core-lib/xjar-maven-plugin)
#### XJar-Maven-Plugin 是对 [XJar](https://github.com/core-lib/xjar) 的一个Maven Plugin封装，实现可通过Maven命令或绑定在Maven构建的生命周期之中执行，用以更加便捷的方式集成 [XJar](https://github.com/core-lib/xjar) 。

GitHub: https://github.com/core-lib/xjar-maven-plugin

## 什么是XJar
XJar 是基于对JAR包内资源的加密以及拓展ClassLoader来构建的一套程序加密启动，动态解密运行的方案，避免源码泄露或反编译，支持Spring Boot JAR 安全加密运行，同时支持的原生JAR。
更多文档请点击：[XJar](https://github.com/core-lib/xjar)

## 环境依赖
JDK 1.7 +

## 集成步骤
```xml
<project>
    <!-- 设置 jitpack.io 插件仓库 -->
    <pluginRepositories>
        <pluginRepository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </pluginRepository>
    </pluginRepositories>
    <!-- 添加 XJar Maven 插件 -->
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.core-lib</groupId>
                <artifactId>xjar-maven-plugin</artifactId>
                <version>2.0.8</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                        </goals>
                        <phase>package</phase>
                        <configuration>
                            <password>io.xjar</password>
                            <!-- optional
                            <algorithm/>
                            <keySize/>
                            <ivSize/>
                            <mode/>
                            <includes>
                                <include/>
                            </includes>
                            <excludes>
                                <exclude/>
                            </excludes>
                            <sourceDir/>
                            <sourceJar/>
                            <targetDir/>
                            <targetJar/>
                            -->
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

```
#### 也可以通过Maven命令单独执行 XJar 插件
```text
mvn xjar:build -Dxjar.password=io.xjar
mvn xjar:build -Dxjar.password=io.xjar -Dxjar.targetDir=/directory/to/save/target.xjar
```

#### 但通常情况下是让XJar插件绑定到指定的phase中自动执行，这样就能在项目构建的时候自动构建出加密的包。
```text
mvn clean package -Dxjar.password=io.xjar
mvn clean install -Dxjar.password=io.xjar -Dxjar.targetDir=/directory/to/save/target.xjar
```

## 强烈建议
强烈建议不要在 pom.xml 的 xjar-maven-plugin 配置中写上密码，这样会导致打包出来的 xjar 包中的 pom.xml 文件保留着密码，极其容易暴露密码！强烈推荐通过 mvn 命令来指定加密密钥！

## 注意事项
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <!-- 需要将executable和embeddedLaunchScript参数删除，目前还不能支持对该模式Jar的加密！后面将会支持该方式的打包。 
    <configuration>
        <executable>true</executable>
        <embeddedLaunchScript>...</embeddedLaunchScript>
    </configuration>
    -->
</plugin>
```

## 参数说明
| 参数名称 | 命令参数名称 | 参数说明 | 参数类型 | 缺省值 | 示例值 |
| :------ | :----------- | :------ | :------ | :----- | :----- |
| password | -Dxjar.password | 密码字符串 | String | 必须 | 任意字符串，io.xjar |
| algorithm | -Dxjar.algorithm | 加密算法名称 | String | AES | JDK内置加密算法，如：AES / DES |
| keySize | -Dxjar.keySize | 密钥长度 | int | 128 | 根据加密算法而定，56，128，256 |
| ivSize | -Dxjar.ivSize | 密钥向量长度 | int | 128 | 根据加密算法而定，128 |
| mode | -Dxjar.mode | 加密模式 | int | 0 | 0：普通模式 1：危险模式（免密码启动）|
| sourceDir | -Dxjar.sourceDir | 源jar所在目录 | File | ${project.build.directory} | 文件目录 |
| sourceJar | -Dxjar.sourceJar | 源jar名称 | String | ${project.build.finalName}.jar | 文件名称 |
| targetDir | -Dxjar.targetDir | 目标jar存放目录 | File | ${project.build.directory} | 文件目录 |
| targetJar | -Dxjar.targetJar | 目标jar名称 | String | ${project.build.finalName}.xjar | 文件名称 |
| includes | -Dxjar.includes | 需要加密的资源路径表达式 | String[] | 无 | com/company/project/** , mapper/*Mapper.xml , 支持Ant表达式 |
| excludes | -Dxjar.excludes | 无需加密的资源路径表达式 | String[] | 无 | static/** , META-INF/resources/** , 支持Ant表达式 |
| deletes | -Dxjar.deletes | 加密后删除指定资源路径表达式 | String[] | 无 | target/*.jar, ../module/target/*.jar, 支持Ant表达式 |
#### 注意：
* 当 includes 和 excludes 同时使用时即加密在includes的范围内且排除了excludes的资源。
* mode 设置为 1 时表示危险加密模式，但同时也是免密码启动，请谨慎使用！

## 版本记录
* 2.0.8
    * 版本号去除"v"前缀
* v2.0.7
    * 支持加密后删除指定资源
* v2.0.6
    * 解决多jar包启动时无法找到准确的MANIFEST.MF导致无法正常启动的问题
* v2.0.5
    * 升级[LoadKit](https://github.com/core-lib/loadkit)依赖版本
    * 修复ANT表达式无法正确匹配**/*通配符的问题
* v2.0.4
    * 解决危险模式不支持ubuntu系统的问题
* v2.0.3
    * 过滤器泛型协变支持
    * 支持 includes 与 excludes 同时起效，当同时设置时即加密在includes范围内但又不在excludes范围内的资源
* v2.0.2
    * 原生jar增加密钥文件的启动方式，解决类似 nohup 和 javaw 的后台启动方式无法通过控制台输入密码的问题
* v2.0.1
    * 增加密钥文件的启动方式，解决类似 nohup 和 javaw 的后台启动方式无法通过控制台输入密码的问题
    * 修复解密后没有删除危险模式中在MANIFEST.MF中保留的密钥信息
* v2.0.0
    * 升级为v2.0.0 与 xjar版本保持一致
* v1.1.0
    * 支持 Spring-Boot 以ZIP方式打包，即依赖外部化方式启动。
    * 修复无加密资源时无法启动问题
* v1.0.9
    * 支持危险模式加密，实现免密码启动，但是请谨慎使用！
* v1.0.8
    * 避免过滤器使用不当造成无法启动
* v1.0.7
    * bug修复
* v1.0.6
    * 将对普通jar项目或模块和spring boot项目或模块的插件方式加密整合成一个智能分析goal，配置时无需关心当前项目或模块是哪一种。
    * 对于无法加密的Jar进行友好提示
    * 升级 XJar 版本为 v1.1.0
* v1.0.5
    * 引用新版本的XJar(v1.0.9)，修复不同版本的Spring-Boot加密无法运行问题
* v1.0.4
    * 第一个正式版发布

## 协议声明
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

## 加入群聊
QQ 950956093 [![](https://pub.idqqimg.com/wpa/images/group.png)](https://shang.qq.com/wpa/qunwpa?idkey=e567db1c32de4b02da480d895566757b3df73e3f8827ed6c9149e2859e4cdc93)