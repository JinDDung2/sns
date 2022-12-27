# MutsaSNS

url: http://ec2-43-200-70-107.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

### 관리자 아이디
```json
{
  "userName":"jin",
  "password":"jin"
} 
```
<br>
<br>

## 📔  Architecture(아키텍처) - Layered Architecture 구조
<img src="assets/img/architecture.png">

## DB설계

### users
<img src="assets/img/userDB.png">

### posts
<img src="assets/img/postDB.png">

## ERD
<img src="assets/img/erd.png">

## EndPoint
### 회원가입
`POST /api/v1/users/join`

**request**
```json
{
  "password": "string",
  "userName": "string"
}
```

**response**
```json
{
"resultCode": "SUCCESS",
"result":{
    "userId": int,
    "userName": "string"
    }
}
```

### 로그인
`POST /api/v1/users/login`

**request**
```json
{
  "userName": "string",
  "password": "string"
}
```

**response**
```json
{
  "resultCode": "SUCCESS",
  "result":{
    "jwt": "string"
  }
}
```

### 역할변경
`POST /api/v1/users/{userId}/role/change`

**response**
```json
{
"resultCode": "SUCCESS",
    "result":{
        "role": "string"
    }
}
```

### 포스트 등록
`POST /api/v1/posts`

**request**
```json
{
  "title": "string",
  "body": "string"
}
```

**response**
```json
"resultCode":"SUCCESS",
  "result":{
    "postId": int
    "message": "포스트 등록 완료",
    }
}
```


### 포스트 단건 조회
`GET /api/v1/posts/{postId}`

**response**
```json
{
  "id": int,
  "title": "string",
  "body": "string",
  "userName": "string",
  "createdAt": "yyyy-mm-dd hh:mm:ss",
  "lastModifiedAt": "yyyy-mm-dd hh:mm:ss"
}
```


### 포스트 리스트
`GET /api/v1/posts`

**response**
```json
{
    "resultCode": "SUCCESS",
    "result": {
        "content": [],
        "pageable": pageable,
        "last": boolean,
        "totalPages": int,
        "totalElements": int,
        "size": int,
        "number": int,
        "sort": {
            "empty": boolean,
            "sorted": boolean,
            "unsorted": boolean
        },
        "first": boolean,
        "numberOfElements": int,
        "empty": boolean
    }
}
```


### 포스트 수정
`PUT /api/v1/posts/{postId}`

**request**
```json
{
  "title":"string",
  "body":"string"
}
```

**response**
```json
"resultCode":"SUCCESS",
  "result":{
    "postId": int
    "message": "포스트 수정 완료",
    }
}
```


### 포스트 삭제
`DELETE /api/v1/posts/{postId}`

**response**
```json
"resultCode":"SUCCESS",
  "result":{
    "message": "포스트 삭제 완료",
    "postId": int
    }
}
```


