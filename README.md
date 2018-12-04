# XJar-Maven-Plugin
#### XJar-Maven-Plugin 是对[XJar](https://github.com/core-lib/xjar)的一个Maven Plugin封装，实现可通过Maven命令或绑定在Maven构建的生命周期之中执行，用以更加便捷的方式集成[XJar](https://github.com/core-lib/xjar)。

GitHub: https://github.com/core-lib/xjar-maven-plugin

## **环境依赖**
JDK 1.7 +

## **集成步骤**
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
                <version>v1.0.4</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>spring-boot</goal>
                        </goals>
                        <phase>package</phase>
                        <configuration>
                            <password>io.xjar</password>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## **参数说明**
| 参数名称 | 命令参数名称 | 参数说明 | 参数类型 | 缺省值 | 可选值 |
| :------ | :----------- | :------ | :------ | :----- | :----- |
| algorithm | -Dxjar.algorithm | 加密算法名称 | String | AES | JDK内置加密算法，如：AES / DES |
| keySize | -Dxjar.algorithm | 加密算法名称 | int | 128 | JDK内置加密算法，如：AES / DES |
| ivSize | -Dxjar.algorithm | 加密算法名称 | int | 128 | JDK内置加密算法，如：AES / DES |
| password | -Dxjar.algorithm | 加密算法名称 | String | 必须 | JDK内置加密算法，如：AES / DES |