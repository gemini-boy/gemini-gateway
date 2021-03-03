# gemini-gateway

## 简介

API网关：作为反向代理，对外部系统提供统一的服务访问入口，并提供鉴权、限流等基础组件服务，最后将请求转发给后面的业务集群。

特性：基于事件响应、异步非阻塞

框架：springboot2、spring5 webflux

## 重点

基于扩展接口WebFilter实现子类，组成WebFilterChain链，优先级由小到大，顺序执行实现一系列组件功能
底层与外界（接收请求、转发请求、转发后异步等待返回数据）通信基于netty，需要借助异步特性+NIO，当有数据响应时通过IO线程触发Mono任务链


![image](https://user-images.githubusercontent.com/52782878/109771763-2a064980-7c38-11eb-9930-500195fd3130.png)

