# **Emporium Mall(Project)** 

## **How to play**

1. git clone `https://github.com/ZyJackMan28/emporium.git`
2. open IDEA -->  File  -->  New  --> Open 
3. choose the tpom.xml under the project，open it
4. you must update your all .yml files (jdbc,redis.mongoDB,ElasticSearch)
5. import emp.sql (you can use local or remote database)
6. configure your nginx.conf (or decide without reversal proxy on your own)
7. you can just run the project only with Insomnia  or Postman RESTful tools or you need to run emporium-management-web and emporium-portal recommended.
8. enjoy it

## Develop Environment

IDEA2020.2 + MAVEN + Spring Cloud (Distributed Base) 

## 1.项目涉及的技术介绍

**项目后端共13个模块（不包含emporium-poi)**

**MySQL：**本项目涉及到的建表SQL已经添加到项目文件夹文件名 emp.sql，主要针对查询语句

**MyBatis:** DOA层的开发，MyBatis配合Mapper动态代理的方式访问数据库

**SpringBoot:** 主要还是Spring IOC和AOP的核心，自动装配省去我们以前Spring配置的繁琐

**SpringMVC:** RESTful接口设计和使用，应用于Web Controller接口的开发

**前端:**  1交互设计 2.Vuejs,3.JQuery

## 2. 模块设计

**a. 通用工具类和VO，异常捕获等统一放在emporium-common模块下**

**b. 所有微服务都统一注册到 emporium-registry模块中**

**c. emporium-gateway作为微服务的网关**

**d.商品管理的基本模块以及接口设计emporium-item,并为其他服务提供设计接口**

**e.文件上传模块 emporium-upload**

**f.前台页面搜索模块 emporium-search**

**g.用户鉴权 emporium-auth**

**h. 购物车模块 emporium-cart**

**i. 用户注册与登陆模块 emporium-crew**

**j. 用户下单支付模块 emporium-order**

**k.短信发送与验证模块 emporium-sms**

## Note

本项目的后端运行环境在CentOS7.2下完成的，所以项目中的MySQL运行在Docker容器里, Nginx，Redis, RabbitMQ等所有配置IP和端口号需要自行配置，或采用本项目中CentOS7默认环境，但.yml文件中的配置端口等个别配置需要根据你的环境进行配置



