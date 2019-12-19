# item37 - ordinal 인덱싱 대신 EnumMap을 사용하라

## 핵심정리

**배열의 인덱스를 얻기위해 ordinal을 쓰는 것은 일반적으로 좋지 않으니, 대신 EnumMap을 사용하라.** 다차원 관계는 EnumMap\<..., EnumMap\<...\>\> 으로 표현하라. "애플리케이션 프로그래머는 Enum.ordinal을 (웬만헤서는) 사용하지 말아야 한다.(아이템 35)" 는 일반 원칙의 특수한 사례이다.



## 의도

EnumMap 타입의 인스턴스를 선언 후에 로직 내에서 아래와 같은 자료를 저장하는 경우를 예로 들고 있다. 알아보기 쉽도록 조금 단순화 했다. 

```java
Map<LifeCycle, Set<Plant>> plantsByLifeCycle = 
{
  ANNUAL 		: {APPLE, ROSE},
  BIENNIAL	: {Cherry Blossom},
  PERENNIAL : {BAMBOO, DAISY}
}
```

  

위와 같은 자료를 저장할 때 좋지 않은 방식으로 아래의 예를 들고 있다.  

```java
enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

Set<Plant> [] plantsByLifeCycle = 
{
  0 : {APPLE, ROSE},		// ANNUAL의 상수값에 해당
  1 : {Cherry Blossom},	// PERENNIAL의 상수값에 해당
  2 : {BAMBOO, DAISY}		// BIENNIAL의 상수값에 해당
}
```



생애주기(LifeCycle) 별로 정원내의 화초들을 순회하는 예제를 이 장에서 들고 있다. 클라이언트 내에서 Enum 상수의 종류에 따라 다른 Set을 가진 Map을 가지고 싶을때 EnumMap을 사용하라고 권고하는 챕터이다. HashMap이나 HashSet을 쓸수도 있다. 그런데도 굳이 EnumMap을 쓰라고 하는건 장점이 있어서다. 그 장점에 대해서도 뒤에서 정리해보자.



# Plant 의 LifeCycle 시나리오

화분의 생애 주기 유형을 enum으로 가지고 있는 클래스를 표현하고 있다.

ex)

```java
class Plant{
  enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }
  
  final String name;
  final LifeCycle lifeCycle;
  
  Plant(String name, LifeCycle lifeCycle){
    this.name = name;
    this.lifeCycle = lifeCycle;
  }
  
  @Override public String toString(){
    return name;
  }
}
```



# 나쁜 예 - ordinal을 사용해 상수접근을 하는예 

절대 따라하지 말라고 주의를 주는 예이다. 

```java
// 생애주기의 종류의 갯수만큼 Set<Plant>[] 생성,
// 즉, 배열 공간 생성
Set<Plant>[] plantsByLifeCycle = 
  (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];

// Set<Plant> []의 index마다 비어있는 HashSet<P> 공간 할당
for(int i=0; i<plantsByLifeCycle.length; i++){
  plantsByLifeCycle[i] = new HashSet<>();
}

// Plant의 LifeCycle에 대한 index에 Plant를 add
for(Plant p : garden)
  plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);

for(int i=0; i<plantsByLifeCycle.length; i++){
  System.out.printf("%s: %s%n". Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
}
```

배열 인덱스 하나에 enum Plant하나가 대응되고 있다. 이 경우 

- enum내의 대응되는 상수의 숫자를 기억하고 있어야 한다는 단점  
- 상수 추가시 배열내의 index위치도 달라지기 때문에 일일이 인덱스가 비게 되지 않도록 상수값을 잘 선택해줘야 한다는 단점

이 있다.



# 권장 - EnumMap 활용

정원에 화초가 2~3개 이상 있다고 가정해보자. 정원에 있는 화초들은 각각 자기 자신의 생애주기 정보(LifeCycle)를 가지고 있다. 아래는 정원내에 생애 주기별로 화초들의 목록을 EnumMap으로 정리하는 코드이다.

```java
// EnumMap 타입인 plantsByLifeCycle 선언
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = 
  new EnumMap<>(Plant.LifeCycle.class);

// Enum의 상수(ANNUAL, PERENNIAL, BIENNIAL)별로 순회하면서 
// plantsByLifeCycle에 <LifeCycle, Set<Plant>> 자료를 생성
// 즉, 각 상수(ANNUAL, PERENNIAL, BIENNIAL)별로 비어있는 HashSet할당
for(Plant.LifeCycle lc : Plant.LifeCycle.values())
  plantsByLifeCycle.put(lc, new HashSet<>());

// 정원 내의 화초를 순회
// 화초(i)가 가지고 있는 생애주기로 enumMap의 HashSet을 탐색하고,
// 해당 HashSet에 화초를 추가하고 있다.
// (생애주기 Enum에 대해 하나이상의 화초를 매핑하고 있다.)
for(Plant p : garden)
  plantsByLifeCycle.get(p.lifeCycle).add(p);

System.out.println(plantsByLifeCycle);
```



# EnumMap의 장점

EnumMap은 내부에서 배열을 사용한다. 이런 내부 구현방식을 안으로 숨겨서 

- Map의 안정성
- 배열의 성능(index를 직접 지정하는)

을 모두 얻어낸 것이다.  



EnumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로, 런타임 제네릭 정보를 제공한다. (ITEM 33)  



# 스트림 활용 (1)



TODO  

- 스트림 예제도 github내의 현재 디렉터리에 push할것.
- 어떻게 정리하지?? 이것도 서사시다....



