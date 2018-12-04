# XJar-Maven-Plugin [![](https://jitpack.io/v/core-lib/xjar-maven-plugin.svg)](https://jitpack.io/#core-lib/xjar-maven-plugin)
#### XJar-Maven-Plugin 是对 [XJar](https://github.com/core-lib/xjar) 的一个Maven Plugin封装，实现可通过Maven命令或绑定在Maven构建的生命周期之中执行，用以更加便捷的方式集成 [XJar](https://github.com/core-lib/xjar) 。

GitHub: https://github.com/core-lib/xjar-maven-plugin

## 什么是XJar
XJar 是基于对JAR包内资源的加密以及拓展ClassLoader来构建的一套程序加密启动，动态解密运行的方案，避免源码泄露或反编译，支持Spring Boot JAR 安全加密运行工具，同时支持的原生JAR。
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
                <version>LATEST_VERSION</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>spring-boot</goal>
                        </goals>
                        <phase>package</phase>
                        <configuration>
                            <password>io.xjar</password>
                            <!-- optional
                            <algorithm/>
                            <keySize/>
                            <ivSize/>
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
#### 也可以通过Maven命令执行
```text
mvn xjar:spring-boot -Dxjar.password=io.xjar
mvn xjar:jar -Dxjar.password=io.xjar -Dxjar.targetDir=/path/to/save/target.xjar 
```

## 命令目标
| 目标名称 | 目标说明 |
| :------- | :------ |
| spring-boot | Spring-Boot项目或模块使用 |
| jar | 普通jar项目或模块使用 |

## 参数说明
| 参数名称 | 命令参数名称 | 参数说明 | 参数类型 | 缺省值 | 示例值 |
| :------ | :----------- | :------ | :------ | :----- | :----- |
| password | -Dxjar.password | 密码字符串 | String | 必须 | 任意字符串，io.xjar |
| algorithm | -Dxjar.algorithm | 加密算法名称 | String | AES | JDK内置加密算法，如：AES / DES |
| keySize | -Dxjar.keySize | 密钥长度 | int | 128 | 根据加密算法而定，56，128，256 |
| ivSize | -Dxjar.ivSize | 密钥向量长度 | int | 128 | 根据加密算法而定，128 |
| sourceDir | -Dxjar.sourceDir | 源jar所在目录 | File | ${project.build.directory} | 文件目录 |
| sourceJar | -Dxjar.sourceJar | 源jar名称 | String | ${project.build.finalName}.jar | 文件名称 |
| targetDir | -Dxjar.targetDir | 目标jar存放目录 | File | ${project.build.directory} | 文件目录 |
| targetDir | -Dxjar.targetJar | 目标jar名称 | String | ${project.build.finalName}.xjar | 文件名称 |
| includes | -Dxjar.includes | 需要加密的包内资源 | String[] | 无 | BOOT-INF/classes/** , BOOT-INF/lib/xjar-*.jar , 支持Ant表达式 |
| excludes | -Dxjar.excludes | 无需加密的包内资源 | String[] | 无 | BOOT-INF/classes/** , BOOT-INF/lib/xjar-*.jar , 支持Ant表达式 |

#### 注意：
当 includes 和 excludes 同时使用是，excludes将会失效！

## 版本记录
* v1.0.4
    * 第一个正式版发布

## 协议声明
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

## 联系作者
QQ 646742615 不会钓鱼的兔子