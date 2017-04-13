## DuplicateToken.jar功能介绍
参考[BatchHacker.jar](https://github.com/1102568869/BatchHacker)使用AOP代替拦截器实现的防重复提交插件,较拦截器实现的优点是配置更灵活,
### Maven配置
1.在Maven配置文件中profiles节点新增
```
        <profile>
            <id>washmore</id>
            <repositories>
                <repository>
                    <id>public</id>
                    <url>http://maven.washmore.tech/nexus/content/repositories/public</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>central</id>
                    <url>http://maven.washmore.tech/nexus/content/repositories/public</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
```
2.在项目中引入最新版本的Maven依赖
```
    <dependency>
      <groupId>tech.washmore</groupId>
      <artifactId>util.duplicate.token</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
```

### 使用方法
1.首先将AOP增强拦截器托管给spring  
使用xml声明式:  
在spring配置文件中增加一行代码(需要在扫描Controller类的文件中，一般默认为spring-mvc.xml)
```
    <import resource="classpath*:duplicatetoken/spring-aop.xml"/>
```
或者使用注解式:  
新建类DuplicateTokenConfig类(确保此类能被自动扫描到):  
```
@Configuration
@ImportResource("classpath*:duplicatetoken/spring-aop.xml")
public class DuplicateTokenConfig {
}
```

2.然后在需要执行方重复提交逻辑的方法上增加@DuplicateToken注解即可;  
注:此注解只适用于SpringMVC环境中具有Controller注解的类(或者源自Controller，比如RestController，详见源码中AvoidDuplicateTokenInterceptor类的existControllerAnn方法)
