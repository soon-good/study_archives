package example.stream.third;

import java.util.EnumMap;
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

			Transition(Phase from, Phase to){
				this.from = from;
				this.to = to;
			}

			static{
				Stream.of(values())
					.collect(Collectors.groupingBy(
						t->t.from,
						()->new EnumMap<>(Phase.class),
						Collectors.toMap(
							t->t.to, t->t,
							(x,y)->y, ()->new EnumMap<>(Phase.class)
						)));
			}

		}


	}

}
