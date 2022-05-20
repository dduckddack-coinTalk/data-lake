# Drawing-Image Server

빗썸의 코인 캔들차트 데이터를 저장/조회하는 기능을 제공하는 서버입니다.

### 배경(Background)

캔들차트 조회 API가 조회시점기준으로 최대 1500까지 데이터를 제공하기때문에 과거 데이터를 볼 수 없습니다. 그래서 사용자가 차트에 추세선등 그림을 그리고 저장 후 불러올 때, 과거 시간의 코인 데이터도 같이 불러와서 비교할 수 있도록 데이터를 미리 저장해뒀다가 조회가능한 기능을 제공합니다.


### 목표(Goals)

1. 빗썸 캔들차트 데이터 저장
2. 사용자가 요청한 코인, 요청한 시점의 캔들차트 데이터 조회


### 계획 (Plan)
* 빗썸 분봉데이터 받아와서 influxDB에 저장 (1분, 10분, 30분, 1시간)
* 사용자가 요청한 시점의 특정 코인 데이터 최대 1500까지 제공


### HowTo Setting

#### 1. influxDB 설치 및 설정 등록
1. influxDB 설치
2. 데이터베이스, 로그인 계정 생성 후 아래 설정에 등록
3. 외부에서 접속 가능하게 설정 변경


#### aplication.yml 설정

```yml
server:
  port: 9092

spring:
  influxdb:
    url: influxDB 서버 입력
    database: 데이터 베이스명 입력
    username: 접속 아이디 입력
    retention-policy: autogen

```



# REST API

### URI : `POST` / drawing / uploadImage
특정 시점의 코인 차트캔들 데이터 조회 

Parameters
| name | type | Description | Default |
|:---|:---:|:---:|:---:|
| `coin` |String|조회할 코인 종류||
| `time` |String|조회할 차트의 시간값||
| `chartIntervals` |String|조회할 차트의 시간 간격|1m|

Responses
<pre>
{
    "status": "0000",
    "message": {
        "t": [
            1652581800,
           
        ],
        "o": [
            "39596000"
 
        ],
        "h": [
            "39596000"
        ],
        "l": [
            "39506000"
        ],
        "c": [
            "39596000"
        ], 
        "v": [
            "6.53149475"
        ]
    }
}
</pre>



