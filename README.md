# 简介

一体化架构

- Spring Boot
- Spring Authorization Server
- Cache

# Document

[Swagger](http://localhost:8080/swagger-ui/index.html)
[Knife4j](http://localhost:8080/doc.html)

# tmp

- 抛弃 po vo dto概念，直接使用entity。 （如此一来，文档就不规范了。vo，参数和响应实体都以Vo作为后缀命名，如：XxxVo）
- 每个功能模块新建一个包名，如：sys、log、order等等

响应式编程？

Add, Del, Upd, Get

# 授权方式

## 授权码方式

- 访问授权接口

```js
encodeURIComponent('http://127.0.0.1:8080/authorized')
'http%3A%2F%2F127.0.0.1%3A8080%2Fauthorized'
```

```text
http://127.0.0.1:8080/oauth2/authorize?client_id=oidc-client&response_type=code&scope=read&redirect_uri=http%3A%2F%2F127.0.0.1%3A8080%2Fauthorized
```

授权接口检测到未登录，重定向至登陆页面

![login](doc/image/login.png)

填写用户账号密码

- 授权确认页面

登陆成功后，授权接口检测到用户未确认授权，跳转至授权确认页面

![consent-1](doc/image/consent1.png)

选择对应的scope并提交确认权限

![consent-2](doc/image/consent2.png)

- 重定向至授权接口

用户确认授权后携带code跳转至redirectUri

```text
http://127.0.0.1:8080/authorized?code=imnUBq0dxLGEOmGIqNhDvCGip1BgJWj8X96JLfO1lV-uDhNppnG6Bq0fGMChgnbqWNDZgnAXroJGwXOiSpro4sM8B-Z0-CDqGaMz4GNvF3ouTMWbWDaSx41C70lrNMX2
```

![authorized](doc/image/authorized.png)

- 获取token

CLIENT_SECRET_BASIC（基于请求头认证）

![Postman Authorization Code](doc/image/postman_authorization_code.png)

```java
import java.util.Base64;

public class DateTest {
    public static void main(String[] args) {
        String username = "oidc-client";
        String password = "{noop}secret";
        String authentication = String.format("%s:%s", username, password);
        System.out.format("Authorization: Basic %s", Base64.getEncoder().encodeToString(authentication.getBytes())).println();
    }
}
```

```shell
$ curl --location --request POST 'http://127.0.0.1:8080/oauth2/token' \
--header 'Authorization: Basic b2lkYy1jbGllbnQ6e25vb3B9c2VjcmV0' \
--form 'grant_type="authorization_code"' \
--form 'redirect_uri="http://127.0.0.1:8080/authorized"' \
--form 'code="imnUBq0dxLGEOmGIqNhDvCGip1BgJWj8X96JLfO1lV-uDhNppnG6Bq0fGMChgnbqWNDZgnAXroJGwXOiSpro4sM8B-Z0-CDqGaMz4GNvF3ouTMWbWDaSx41C70lrNMX2"'
```

```json
{
  "access_token": "eyJraWQiOiJjOTc2YzkyZi0wMmYzLTQ1YmYtOTQ1NS0yNDE0Zjg0ZWQxZjIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXVkIjoib2lkYy1jbGllbnQiLCJuYmYiOjE2ODk5MDU3MzUsInNjb3BlIjpbInJlYWQiXSwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo4MDgwIiwiZXhwIjoxNjg5OTA2MDM1LCJpYXQiOjE2ODk5MDU3MzV9.rfypj4alAXQ4JfhBo2iLC27ahld2AbkQIt2FN7bX7ChVi2gSbjvuoofMvC9pUeZ4IdLg7QSgX18DNWPP0EVAxtUj21z-nV4M43E-9VSqbddXAJCnx5NpJamcq2IFoSXt2c_xLSTUN5WdYf06-GUyjZwvH4_-PTTKDp_qa6JKiDFq9qM7k41SG0wdT71_jYuLquz0A0eSmDYRXdQNUaMj5ayLcnZmRPQ2AO-90LSmfTOd6_1jqBVRo6iu5N1ZvBWOmi-WMSx0E-m_5bcElzrVxNhmX3UpY7Y_0Fc1589jjMNOWWEzrnfNvgUHV-X0hPRIq2LsSp7Epzpji98ZSslktw",
  "refresh_token": "HlIw1eyGhDaoGKC4WrcY3bJvhtkkidFkTelvmHuip0RrY1FCdcKezcop_7hmErDOlVtg3fA1SKF7NHTxSBv51zAe16SIKAmBY02WpG1DC14j1F2MrI3DwjYdZCG39vXd",
  "scope": "read",
  "token_type": "Bearer",
  "expires_in": 299
}
```

## 客户端凭证方式

客户端凭证，是针对第三方应用的，而不是针对用户的，即有可能多个用户共享同一个令牌。

CLIENT_SECRET_BASIC（基于请求头认证）

![Postman Client Credentials](doc/image/postman_client_credentials.png)

```shell
$ curl --location --request POST 'http://127.0.0.1:8080/oauth2/token' \
--header 'Authorization: Basic b2lkYy1jbGllbnQ6e25vb3B9c2VjcmV0' \
--header 'Cookie: JSESSIONID=0B991FBEF279EE4BE162C3943FBD8485' \
--form 'grant_type="client_credentials"'
```

```json
{
  "access_token": "eyJraWQiOiIyNDMwNjdjNC00YTkyLTQ5ZWQtODk3NC1mMjk0ZWRjYWNlY2IiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJvaWRjLWNsaWVudCIsImF1ZCI6Im9pZGMtY2xpZW50IiwibmJmIjoxNjg5OTEwMDMyLCJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjgwODAiLCJleHAiOjE2ODk5MTAzMzIsImlhdCI6MTY4OTkxMDAzMn0.Q-CNmEYvGL-4YNq6xre2hHNPljhs9ZyLr50OouU-nLqh0pd4BAmrPGFTzjzVOEl_9j22kMnJJFgqjHokobvJjk6_dbvNEZk69ELB4ynRJcqjpFzzn2ZISc8hOGQTBz_p25ClcqniP45t_CBduQXIO7DGRkUsDPkOWOfnU6ErmoNAnX1dbg0eD5KErQPDKBY3uDR0L4JSCZGtzfOoIjVm7XAAJ863mBcWIIp66P5IiW3KnzBQ0fLT2uK1609OQtxx93EKND5MW7M3_zYc9T3Ty-cJEn4vsvdLWYFTrDVQTa5VWKCEr1OnG0L2J4avOR88Kggwf9zNYXzjNjI7xGFGnw",
  "token_type": "Bearer",
  "expires_in": 300
}
```

## 密码方式

CLIENT_SECRET_BASIC（基于请求头认证）

![Postman Password](doc/image/postman_password.png)

```shell
$ curl --location --request POST 'http://127.0.0.1:8080/oauth2/token' \
--header 'Authorization: Basic b2lkYy1jbGllbnQ6e25vb3B9c2VjcmV0' \
--header 'Cookie: JSESSIONID=1904D0835643AF905BF84182BD47CA71' \
--form 'grant_type="password"' \
--form 'username="user"' \
--form 'password="password"'
```

```json
{
  "access_token": "eyJraWQiOiI0M2JmNDNlZC1kZWE0LTRlOGMtOTk0Zi05YmQ3YmViOTg4Y2QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXVkIjoib2lkYy1jbGllbnQiLCJuYmYiOjE2ODk5MzI0NDMsInNjb3BlIjpbInJlYWQiLCJvcGVuaWQiLCJwcm9maWxlIiwid3JpdGUiXSwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo4MDgwIiwiZXhwIjoxNjg5OTMyNzQzLCJpYXQiOjE2ODk5MzI0NDN9.X9524Txx-HsftZPlxQ_fIlLlKT-xkNOpu1E-IM5vdEK-AH8DPkflJHohi2PNQisSBcLFPF53n7dUQSPkdaIXLmmRI0JXGkSFgR7pjPnUW13vtUgVdD42E3YYNozGWssVQ78T9EeIEpOe_gZ2sHtCN3U__DiJCyHmD9yAz7X-cBEGiRkpbcu6nYXCeu9PVSUv-xpru2l7FD5WnToe0JYeDODxv518Zprvi8LhVvC2EoknYdMG6Zl3NzOq_fce4fuHG_l3qOJC-YpZRLUYvofpe_nsoHJKronCHFivKTc6B8IB3m-1sL0P1xXf1gJAEF_VKbOpayAflCJsEK5CdWWhZQ",
  "refresh_token": "J8VRNQQif7jdU94Y8sDARmaC2f30rp8wRIRcAP4s8gXXoCP0gMfk0_UYS-ye4u8abJ2Vmky-SjCoBu38wFVi5nG40QXQIbGCA9wcdB5vYC2g-U0Nrg4Cut3ivLHYmD7K",
  "scope": "read openid profile write",
  "token_type": "Bearer",
  "expires_in": 299
}
```

## 校验令牌

GET http://127.0.0.1:8080/oauth/check_token?token=4de1bfdc-b960-46ff-8e51-272e95a051c1

## 刷新令牌

CLIENT_SECRET_BASIC（基于请求头认证）

![Postman Refresh Token](doc/image/postman_refresh_token.png)

```shell
$ curl --location --request POST 'http://127.0.0.1:8080/oauth2/token' \
--header 'Authorization: Basic b2lkYy1jbGllbnQ6e25vb3B9c2VjcmV0' \
--header 'Cookie: JSESSIONID=0B991FBEF279EE4BE162C3943FBD8485' \
--form 'grant_type="refresh_token"' \
--form 'refresh_token="JS0ypeJZaD3I6meNnix8DXo4c7B28x5vkFRrrAGC4cgk9tkgJWxQvNwbWIFwOgHeSgxQmzv_lwXtVLtIF2ErqIWL3Nmf9tghkRlEu-7aFgACs2d1XbhAdwLNxJni77gY"'
```

## 撤销令牌

通过 access_token

```shell
curl --location --request POST 'http://localhost:3000/oauth2/revoke' \
--header 'Authorization: Basic cGlnOnBpZw==' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'token={access_token}' \
--data-urlencode 'token_type_hint=access_token'
```

通过 refresh_token

```shell
curl --location --request POST 'http://localhost:3000/oauth2/revoke' \
--header 'Authorization: Basic cGlnOnBpZw==' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'token={refresh_token}' \
--data-urlencode 'token_type_hint=refresh_token'
```

