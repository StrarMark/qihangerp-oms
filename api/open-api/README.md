# README
## API请求说明
调用任何一个API都必须传入的参数，目前支持的公共参数有：

| 参数名称	      | 参数类型	| 是否必须	| 示例值	                                  | 参数描述                          |
|------------| --- |  --- |---------------------------------------|-------------------------------|
| app_key	   | string	| 是	| 3409409348479354011	                  | 配置三方系统回传时配置                   | 
| app_secret	 | string	| 是	| c6f957da-1239-4343-84a1-c84e68915ff7  | 	配置三方系统回传时配置                  | 
| param_json	 | string	| 是	| {"cid":"12","page":"1"}	| 没有参数传{}                       | 
| timestamp	 | string	| 是	| 1667899926	| Unix时间戳 和开放平台服务器时间相差超过10分钟会报错 |
| sign       | 	string	| 是	| 签名算法参照下面的介绍	| 签名方法DigestUtils.sha256Hex                          | 


param_json字段应当放在POST body中传输，形式为「Content-Type: application/json」。里面是业务参数，按照Key的字典序排序，嵌套JSON也需要按Key排序。
剩余字段（包括app_key、app_secret、timestamp、sign）依旧采用url query方式传递。

## 签名算法
+ STEP1：序列化参数
将paramJson参数序列化成JSON格式

+ STEP2：计算签名

拼接请求参数 以app_key、param_json、timestamp这个顺序，把以上参数的键值对依次拼接在一起

