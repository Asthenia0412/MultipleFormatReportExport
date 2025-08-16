# 故障排除指南

## 常见问题及解决方案

### 1. MyBatis类型处理器错误

**错误信息：**
```
Type handler was null on parameter mapping for property 'features'. 
It was either not specified and/or could not be found for the javaType ([Ljava.lang.String;) : jdbcType (null) combination.
```

**原因：**
- `JsonTypeHandler`没有被正确注册到MyBatis中
- XML映射文件中缺少类型处理器配置
- 缺少必要的依赖（如Jackson）

**解决方案：**

#### 1.1 确保依赖完整
在`pom.xml`中添加以下依赖：
```xml
<!-- Jackson依赖 -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.13.4</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.4</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.13.4</version>
</dependency>
```

#### 1.2 注册类型处理器
在`MybatisConfig.java`中注册类型处理器：
```java
@Bean
public SqlSessionFactory sqlSessionFactory(@Autowired DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    
    // 设置mapper位置和别名包
    sessionFactory.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
    );
    sessionFactory.setTypeAliasesPackage("io.github.asthenia0412.multipleformatreportexport.entity");
    
    // 创建SqlSessionFactory
    SqlSessionFactory factory = sessionFactory.getObject();
    
    // 注册类型处理器
    TypeHandlerRegistry typeHandlerRegistry = factory.getConfiguration().getTypeHandlerRegistry();
    typeHandlerRegistry.register(String[].class, JsonTypeHandler.class);
    
    return factory;
}
```

#### 1.3 配置XML映射
在`FormatInfoMapper.xml`中正确配置类型处理器：
```xml
<!-- 结果映射 -->
<result column="features" property="features" jdbcType="VARCHAR" 
        typeHandler="io.github.asthenia0412.multipleformatreportexport.util.JsonTypeHandler"/>

<!-- 插入和更新时也要指定类型处理器 -->
#{features,typeHandler=io.github.asthenia0412.multipleformatreportexport.util.JsonTypeHandler}
```

### 2. 数据库连接问题

**错误信息：**
```
Failed to configure a DataSource
```

**解决方案：**

#### 2.1 使用H2内存数据库进行测试
创建`application-dev.properties`：
```properties
spring.profiles.active=dev
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

#### 2.2 添加H2依赖
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 3. 启动类配置错误

**错误信息：**
```
Could not find or load main class
```

**解决方案：**
检查`pom.xml`中的主类配置：
```xml
<configuration>
    <mainClass>io.github.asthenia0412.multipleformatreportexport.MultipleFormatReportExportApplication</mainClass>
</configuration>
```

### 4. 构建和运行步骤

#### 4.1 清理并重新构建
```bash
mvn clean package
```

#### 4.2 运行应用
```bash
# 使用开发环境配置
java -jar target/report-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 或者直接运行
mvn spring-boot:run -Dspring.profiles.active=dev
```

#### 4.3 访问H2控制台
```
http://localhost:8080/h2-console
```

### 5. 验证步骤

1. 应用启动成功后，访问以下接口验证：
   - `GET /api/report/formats` - 获取支持的格式
   - `GET /api/report/count` - 获取记录数
   - `GET /api/report/data` - 获取分页数据

2. 检查日志输出，确保没有错误信息

3. 在H2控制台中验证数据是否正确插入

### 6. 常见配置问题

#### 6.1 端口冲突
如果8080端口被占用，修改`application.properties`：
```properties
server.port=8081
```

#### 6.2 日志级别
调整日志级别以获取更多调试信息：
```properties
logging.level.io.github.asthenia0412.multipleformatreportexport=DEBUG
logging.level.org.apache.ibatis=DEBUG
```

### 7. 联系支持

如果问题仍然存在，请：
1. 检查完整的错误堆栈信息
2. 确认所有依赖都已正确安装
3. 验证配置文件语法
4. 检查数据库连接和权限
