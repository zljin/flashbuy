# flashbuy
> 技术栈:JDK17+SprintBoot3+MyBatisPlus+MySQL+Redis+JQuery+Ajax+Boostrap

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
