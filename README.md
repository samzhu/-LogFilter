# LogFilter

Cxf其實有個算方便的功能是幫你把所有Request跟Response記錄下來

但有時候你不是用Cxf，但卻又需要加上所有Request的紀錄，就不要再一支一支程式加log了

原始碼
https://github.com/samzhu/LogFilter

其實是從logback的TeeFilter抄過來的

但是他是配置在Tomcat下會記錄所有WebAp的Request，但其實我並不需要記錄到所有WebAP，
所以就拿過來修修改改這樣

如果你Tomcat支援 @WebFilter 那只要放到你的AP底下lib就可以記錄像這樣的資訊

```
2015-06-24 14:25:24 INFO  - 
Request GET /lottery/rest/qrcode?aaa=123&bbb=456 HTTP/1.1 

Response 403 usedtime 626 
 {"message":"裝置未授權，禁止存取"}
沒有支援的話就自己配置吧
```

```
<filter>
    <filter-name>MFilter</filter-name>
    <filter-class>com.common.log.LogFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>MFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```
