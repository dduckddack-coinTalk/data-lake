# Drawing-Image Server
특정 시점의 차트 이미지를 불러오기 위해 빗썸의 코인 캔들차트 데이터를 저장/조회하는 기능을 제공하는 서버 

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



