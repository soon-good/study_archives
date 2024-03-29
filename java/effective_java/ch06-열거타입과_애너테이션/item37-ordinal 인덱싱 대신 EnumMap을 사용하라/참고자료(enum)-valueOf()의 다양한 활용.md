# 참고자료(enum)-valueOf()의 다양한 활용



enum을 처음 배울때, 또는 처음 사용해볼때 원하는 enum을 얻어오기 위해 valueOf를 사용할 때, 상수로 접근해 valueOf로, 즉, valueOf(int)로 Enum을 얻어오려 할때 컴파일 에러를 내서 당황할 때가 있다.  

예를 들어보자면 아래와 같은 경우다.

```java
public enum DeviceType{
  PCS(1, "PCS"){

  },
  BMS(2, "BMS"){

  };

  private int deviceTypeCd;
  private String deviceTypeNm;

  DeviceType(int deviceTypeCd, String deviceTypeNm){
    this.deviceTypeCd = deviceTypeCd;
    this.deviceTypeNm = deviceTypeNm;
  }
  
  public static void main(String [] args){
		DeviceType d1 = DeviceType.valueOf("PCS");	// 정상 동작
		DeviceType d2 = DeviceType.valueOf(1);			// 컴파일 에러
	}
}
```

item37에서는 이러한 경우에 대해서 다룬다.   

위의 경우에 대한 해결책은 세가지다. 

1. EnumMap을 사용하는 방식  
   이책에서 언급하는 권장하는 방식이다.
2. static 스코프 내에서 enum 내에 Map 자료구조 인스턴스를 생성해 int 변수 하나를 키로 하여 enum하나를 저장하는 방식   
   나도 이 책을 읽기 전까지는 이 방식을 자주 사용했었다. [해외 자료](https://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum)를 참고했었다. 하지만 EnumMap을 사용하는 방식이 조금 더 일관성 있고, 유연하다.  
3. ordinal을 사용하는 방식  
   이 책에서 언급하는 주제이다. 사용하면 안되는 좋지 않은 경우에 대한 예를 들면서 절대 사용하지 말라고 한다.