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
