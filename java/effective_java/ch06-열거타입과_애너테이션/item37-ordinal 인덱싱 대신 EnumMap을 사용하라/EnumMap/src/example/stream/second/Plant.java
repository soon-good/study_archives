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
