# flashbuy
> 技术栈:JDK17+SprintBoot3+MyBatisPlus+MySQL+liquibase+Redis+JQuery+Ajax+Boostrap

> 如何启动

1. 打开src/doc目录，导入flashbuy.sql到你的数据库中
2. 修改application.yml中的数据库和redis配置
3. 运行FlashbuyApplication启动类时，添加下面的启动参数
```
spring.profiles.active=dev;app.adminAccount=464480515@qq.com;jwt.secret=mySecretKeyForJwtTokenGenerationInSpringBoot3Application
```
4. 打开flashbuy.postman_collection.json 导入postman
5. 运行postman中的注册一个用户作为admin,然后测试登录等接口,后续需要登录获取jwt token,注册一个admin接口和上面的启动参数app.adminAccount一致
6. 之后你就能跑通所有postman接口，实现一个秒杀系统的最小实现

> 接口文档: http://localhost:8090/swagger-ui/index.html


> 前端访问：http://localhost:8090/login.html

也可以直接打开双击打开html，效果一样，前后端分离


# 功能介绍

## 实现功能

> 实现最精简版的前后端单体的秒杀系统服务

用户功能：
注册
登录
验证码
JWT鉴权
用户会话

商品：
基础增删改查
分页查询
活动秒杀

订单：
订单创建

## 数据库设计

遵循3NF(消除非主属性的传递依赖)，减少数据冗余，保证数据一致性

https://github.com/zljin/flashbuy/blob/master/src/doc/flashbuy.sql


## JWT+ThreadLocal+Redis+拦截器 如何设计用户状态数据获取工具

1、登录成功，通过账号和userId生成JWT
2、以USER_CACHE:userId-->userVo 存入redis，30分钟过期
3、当再次请求其他接口时，header传入JWT token携带的用户信息
4、拦截器拦截并解析JWTToken中的userId信息
5、通过USER_CACHE:userId查redis缓存中userVo的数据
6、将userVo的数据放入工具类UserHolder的ThreadLocal变量
7、这样每次会话请求就可以直接通过UserHolder工具拿到用户状态


## 秒杀活动

1、定义三个状态，秒杀前，秒杀中，秒杀后
2、创建活动表，定义秒杀活动开始时间和结束时间和活动价格
3、秒杀活动前，创建订单按钮置灰，显示原价
4、若当前时间在秒杀中，创建订单按钮恢复，前端展示秒杀价格，可提交订单，扣减库存
5、秒杀活动后，价格复原

保证数据库事务安全
1、由于用的是mysql8+,默认引擎为innodb,事务隔离级别为可重复读
2、@EnableTransactionManagement开启事务
3、在修改方法中添加注解 @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)


# update log

1. 添加liquibase做数据库状态管理