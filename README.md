# dubbo项目使用和验证
# 订单项目使用说明

### api项目(主要用于对外提供dubbo的api服务)
>1.该模块只提供dubbo对外的api接口,最少集成

>2.dubbo对外api需要的协议bean

>3.dubbo对外基础工具包

>4.对外统一状态

- **api**    `[dubbo对外的api接口统一存放在这里]`
- **constant**  `[基础的参数统一存放在这里]`
- **model**  `[dubbo对外的bean统一存放在这里]`
- **utils**  `[对外服务需要的工具类统一存放在这里]`

***

### common项目(主要为consumer和provider提供基础服务)
>1.该模块只为consumer和provider提供基础服务支撑

>2.该服务和api项目不互通

- **constant**  `[基础服务静态文件和枚举文件]`
- **exception**     `[异常文件]`
- **model**     `[业务数据交互使用到的bean]`
- **protocol**     `[接口数据交互使用到的bean]`
- **utils**     `[基础服务使用到的工具类]`

***

### api项目

### consumer项目
#### 1. mvc
### consumer项目(主要对前端和对外提供服务,大多数存放控制层和部分service层)
>1.该模块为consumer服务,用于和前端进行交互,以及对外暴露api接口

#### 2. dubbo  consumer
>2.该服务需要引用api和common服务

#### 3. 日志组件 
- **annotation**  `[存放自定义注解文件]`
- **aop**  `[存放自定义切面文件]`
- **config**  `[基础配置类文件,类似xxl和配置文件读取]`
- **constant**  `[consumer自己需要的基础静态文件和枚举文件]`
- **filter**     `[consumer自己需要的拦截器]`
- **generateInvoice**     `[生成pdf发票版式文件工具类,后期做成jar包,提供基础服务]`
- **handle**     `[定时器相关]`
- **model**     `[consumer自己需要的业务数据交互bean]`
- **modules**     `[consumer自己需要的controller和service]`
- **openApiInterface**     `[consumer对外提供的接口]`
- **protocol**     `[consumer自己需要的接口数据交互bean]`
- **utils**     `[consumer自己需要使用到的工具类]`

### service 项目
#### 1. mybatis 使用须知 
#### 2. redis 使用须知
#### 3. dubbo provider
***

#### 4. zk 配置
#### 5. 日志组件 
1. 支持行号显示
2. 支持输出日志文件行号显示
3. 需要无需修改，需要默认启用maven filter
4. 输出目录地址为 `/data/logs/${groupId}/${artifactId}/${version}`
5. 只输出 正常业务处理日志，和异常日志

### provider项目(主要为consumer提供服务,该项目中保留最原子的基础服务)
>1.该模块为provider服务,用于为consumer提供最原子的基础服务

>2.该服务需要引用api和common服务

- **config**  `[基础配置类文件,类似rabbitmq配置文件读取]`
- **constant**  `[provider自己需要的基础静态文件和枚举文件]`
- **dao**     `[数据库连接层]`
- **model**     `[provider自己需要的业务数据交互bean]`
- **service**     `[provider提供的基础服务]`
- **utils**     `[consumer自己需要使用到的工具类]`


=======
# sims-order-itg
