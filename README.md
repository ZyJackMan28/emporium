# **Emporium Mall(Project)** 

## **How to play**

1. git clone `https://github.com/codingXiaxw/seckill.git`
2. open IDEA -->  File  -->  New  --> Open 
3. choose the tpom.xml under the project，open it
4. you must update your all .yml files (jdbc,redis.mongoDB,ElasticSearch)
5. import emp.sql (you can use local or remote database)
6. configure your nginx.conf (or decide without reversal proxy on your own)
7. enjoy it 

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

#### a.后台管理主要涉及商品的CRUD，所以emporium-item商品后台管理的核心模块，并为其他模块提供基础的接口





