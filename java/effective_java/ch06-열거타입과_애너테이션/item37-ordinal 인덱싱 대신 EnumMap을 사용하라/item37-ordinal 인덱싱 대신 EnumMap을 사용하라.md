# item37 - ordinal() 인덱싱 대신 EnumMap을 사용하라
> 생애주기(LifeCycle) 별로 정원내의 화초들을 순회하는 예제를 이 장에서 들고 있다. 클라이언트 내에서 Enum 상수의 종류에 따라 다른 Set을 가진 Map을 가지고 싶을때 EnumMap을 사용하라고 권고하는 챕터이다. HashMap이나 HashSet을 쓸수도 있다. 그런데도 굳이 EnumMap을 쓰라고 하는건 장점이 있어서다. 그 장점에 대해서도 뒤에서 정리해보자.

  

# 핵심정리

- **배열의 인덱스를 얻기위해 ordinal()을 쓰는 것은 일반적으로 좋지 않으니, 대신 EnumMap을 사용하라.** 
- 다차원 관계는 EnumMap\<..., EnumMap\<...\>\> 으로 표현하라. 
- "애플리케이션 프로그래머는 Enum.ordinal을 (웬만헤서는) 사용하지 말아야 한다.(아이템 35)" 는 일반 원칙의 특수한 사례이다.
  
    
  
  
# EnumMap의 장점
EnumMap은 내부에서 배열을 사용한다. EnumMap은 이런 내부 구현방식을 안으로 숨겨서  

- Map의 안정성
- 배열의 성능(index를 직접 지정하는)

을 충족시켰다.

또한 EnumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로, 런타임 제네릭 정보를 제공한다. (ITEM 33 - 타입 안전 이종 컨테이너를 고려하라)  


​    
# 중첩구조 열거타입 선언 예
책에서는 EnumMap 타입의 인스턴스를 선언 후에 로직 내에서 아래와 같은 자료를 저장하는 경우를 예로 들고 있다. java 문법에는 맞지 않지만 데이터를 알아보기 쉽도록 조금 단순화 해서 적어봤다.  
```java
Map<LifeCycle, Set<Plant>> plantsByLifeCycle = 
{
  ANNUAL 		: {APPLE, ROSE},
  BIENNIAL		: {Cherry Blossom},
  PERENNIAL 		: {BAMBOO, DAISY}
}
```

위와 같은 자료를 저장할 때 좋지 않은 방식으로 아래의 예를 들고 있다.  

  

## 나쁜 예
```java
enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

Set<Plant> [] plantsByLifeCycle = 
{
  0 : {APPLE, ROSE},		// ANNUAL의 상수값에 해당
  1 : {Cherry Blossom},	    // PERENNIAL의 상수값에 해당
  2 : {BAMBOO, DAISY}		// BIENNIAL의 상수값에 해당
}
```
plantsByLifeCycle\[i\] 에 Set\<Plant\> 를 저장하는 방식이다. 그리고 각각의 인덱스 번호에 부여된 의미는 아래와 같다.
- 0 : ANNUAL
- 1 : PERENNIAL
- 2 : BIENNIAL
배열 요소를 인덱싱해서 상수를 처리하고 있다. 확실히 나쁜예가 맞긴하다.  
  
  
  
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

  

## 나쁜 예 - ordinal을 사용해 상수접근을 하는예 
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

배열 인덱스 하나에 enum Plant하나가 대응되고 있다. 이 경우 아래의 단점들이 있다.

- enum내의 대응되는 상수의 숫자를 기억하고 있어야 한다는 단점  
- 상수 추가시 배열내의 index위치도 달라지기 때문에 일일이 인덱스가 비게 되지 않도록 상수값을 잘 선택해줘야 한다는 단점  
  
  
  
  
## 권장 - EnumMap 활용
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

  

# 스트림 활용
## 스트림 활용 (1)
참고로, 이 예제는 EnumMap을 사용하지 않는다. List\<Plant\> 형태인 garden에서 LifeCycle의 상수의 종류별로 그루핑해 리스트를 도출하고 있다.  

예제1) LifeCycle의 종류별로 그루핑하여 Map\<LifeCycle, List\<Plant\>\> 형태로 도출
```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Plant {
	public enum LifeCycle {
		ANNUAL(1, "ANNUAL"){

		},
		BIENNIAL(2, "BIENNIAL"){

		},
		PERENIAL(3, "PERENIAL"){

		};

		private int lifeCycleCd;
		private String lifeCycleNm;

		LifeCycle(int lifeCycleCd, String lifeCycleNm){
			this.lifeCycleCd = lifeCycleCd;
			this.lifeCycleNm = lifeCycleNm;
		}
	}

	private String name;
	private LifeCycle lifeCycle;

	public Plant(LifeCycle lifeCycle, String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public LifeCycle lifeCycle(){
		return this.lifeCycle;
	}

	public static void main(String [] args){

		List<Plant> garden = Arrays.asList(
			new Plant(LifeCycle.ANNUAL,"Rose"),
			new Plant(LifeCycle.BIENNIAL, "Daisy")
		);

    // List<Plant>를 Enum 상수별로 그룹핑
    // 그 결과로 LifeCycle 별로 Plant의 리스트(List<Plant>)가 도출된다.
		Map<LifeCycle, List<Plant>> d = garden.stream()
			.collect(Collectors.groupingBy(p -> p.lifeCycle()));
	}
}
```

  

## 스트림 활용 (2)
EnumMap을 사용하는 경우의 예제이다.  
- 매개변수 3개 짜리 Collectors.groupingBy 메서드를 사용했다.  
- 매개변수가 3개인 Collectors.groupingBy 메서드는 mapFactory 매개변수에 원하는 맵 구현체(여기서는 EnumMap)를 명시해 호출할 수 있다.  
  
```java
package example.stream.second;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Plant {
	public enum LifeCycle {
		ANNUAL(1, "ANNUAL"){

		},
		BIENNIAL(2, "BIENNIAL"){

		},
		PERENIAL(3, "PERENIAL"){

		};

		private int lifeCycleCd;
		private String lifeCycleNm;

		LifeCycle(int lifeCycleCd, String lifeCycleNm){
			this.lifeCycleCd = lifeCycleCd;
			this.lifeCycleNm = lifeCycleNm;
		}
	}

	private String name;
	private LifeCycle lifeCycle;

	public Plant(LifeCycle lifeCycle, String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public LifeCycle lifeCycle(){
		return this.lifeCycle;
	}

	public static void main(String [] args){

		List<Plant> garden = Arrays.asList(
			new Plant(LifeCycle.ANNUAL,"Rose"),
			new Plant(LifeCycle.BIENNIAL, "Daisy")
		);

		EnumMap<LifeCycle, Set<Plant>> d = garden.stream().collect(Collectors.groupingBy(
			p -> p.lifeCycle,
			() -> new EnumMap<>(LifeCycle.class),
			Collectors.toSet()
		));
	}
}

```

스트림을 사용하면 단순히 EnumMap만 사용했을 때와는 조금은 다르게 동작한다.  
EnumMap을 사용하면  
- 식물의 LifeCycle의 종류별 하나씩의 중첩 맵을 만들지만  
  

스트림 버전에서는  
- 해당 생애주기에 속하는 식물이 있을 때만 만든다.  

예를 들어, 정원에 한해살이와 두해 살이 식물만 있다면,  
- EnumMap버전은 맵을 3개 만들고  
  

스트림 버전에서는
- 2개만 만든다.
  
  
# 중첩 EnumMap 예제 

## ex) 상태 변화 맵 예제

```java
package example.stream.third;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestedEnumMap {
	public enum Phase{
		SOLID, LIQUID, GAS;

		public enum Transition {
			MELT(SOLID, LIQUID),	FREEZE(LIQUID, SOLID),
			BOIL(LIQUID, GAS),		CONDENSE(GAS, LIQUID),
			SUBLIME(SOLID, GAS),	DEPOSIT(GAS, SOLID);

			private final Phase from;
			private final Phase to;
      // 현재 상태에 대한 다음 상태를 얻기 위한 Map
			private static Map<Phase, EnumMap<Phase, Transition>> map;

			Transition(Phase from, Phase to){
				this.from = from;
				this.to = to;
			}

			// 책의 예제처럼 멤버변수 선언과 동시에 초기화할 수도 있지만, 선언과 할당을 따로 함
			static{
				map = Stream.of(values())
					.collect(Collectors.groupingBy(
						t -> t.from,
						() -> new EnumMap<>(Phase.class),
						Collectors.toMap(
							t -> t.to, t -> t,
							(x, y) -> y, () -> new EnumMap<>(Phase.class)
						)));
			}

			// from(현재상태) 에서 to(다음상태)를 얻어낸다.
			public static Transition from (Phase from, Phase to){
				return map.get(from).get(to);
			}

		}


	}

}

```

  

Map\<Phase, EnumMap\<Phase, Transition\>\> map 은 현재 상태(Phase from)에 대해 다음 상태(Phase to)를 얻어내기 위한 Map이다.  

위에서는 이 map을 초기화하기 위해 Collector를 두번 사용했다.

1. Collectors.groupingBy
   from 을 기준으로 grouping  

2. Collectors.toMap
   from에 대한 다음 상태를 map으로 1:1 대응시킴. 
   - (x,y)->y 는 선언만 하고 실제로 쓰이지 않는다. EnumMap을 얻기위해 단지 맵 팩터리가 필요한 것 뿐이고, Collector들은 점층적 팩터리(telescoping factory)를 제공하기 때문이다.  
   - 책의 2판에서는 명시적인 반복문을 사용했다. 다소 장황한 편이지만 이해하기 더 수월한 편이다.  



## ex) 위의 예제에서 새로운 상태인 플라스마(PLASMA)를 추가할 경우

플라스마는 아래 두 가지의 상태전이를 갖는다.  

- 이온화 (IONIZE)
  기체 -> 플라스마

- 탈이온화 (DEONIZE)
  플라스마 -> 기체
  
```java
public enum Phase{
	SOLID, LIQUID, GAS, 
  PLASMA; // PLASMA : 새로 추가된 상태
  
  public enum Transition{
		MELT(SOLID, LIQUID), 	FREEZE(LIQUID, SOLID),
    BOIL(LIQUID, GAS),		CONDENSE(GAS, LIQUID),
    SUBLIME(SOLID, GAS),	DEPOSIT(GAS, SOLID),
    // 새로 추가된 상태 전이
    IONIZE(GAS, PLASMA),	DEIONIZE(PLASMA, GAS); 
  }
}
```

배열, ordinal을 사용할 경우와는 다르게 원소를 추가하고 순서를 잘못나열하지 않아도 되는 점이 편하다. EnumMap 버전에서는 단순히

- Enum Phase내에 PLASMA를 추가하고  
- Enum Transition내에 IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS)를 추가한다.  

배열,ordinal() 버전처럼 따로 로직내에서 for문을 수정해야 한다던가, 인덱스를 수정할 일이 극히 적어지고, 단순히 상수만 추가해주고 있다. 새로 추가하는 상수에 대해 부가적인 동작이 필요하므로 유연한 방식이라고 할 수 있다.  

또한 EnumMap의 내부에서는 실제 동작이 Map들의 Map이 배열로 구현되므로 낭비되는 공간, 시간도 거의 없이 명확하며 유지보수하기 좋다.  