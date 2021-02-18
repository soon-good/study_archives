package io.study.effective.example_all.ch03.item3.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InheritanceTest10_1 {

	class CaseInsensitiveString {
		private final String s;

		public CaseInsensitiveString(String s){
			this.s = Objects.requireNonNull(s);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof CaseInsensitiveString)
				return s.equalsIgnoreCase(((CaseInsensitiveString)o).s);
			if (o instanceof String) // 한 방향으로만 작동한다!!
				return s.equalsIgnoreCase((String) o);
			return false;
		}

//		@Override
//		public boolean equals(Object o) {
//			return o instanceof CaseInsensitiveString &&
//				((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
//		}
	}

	@Test
	@DisplayName("ch02::10-1, 55p ")
	void testSymetric(){
		CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
		String s = "polish";

		assertThat(cis.equals(s)).isTrue();
		assertThat(s.equals(cis)).isFalse();

		List<CaseInsensitiveString> list = new ArrayList<>();
		list.add(cis);

		// false 반환하여 대칭성을 위배하게 된다.
		assertThat(list.contains(s)).isFalse();
		// 하위 JDK 버전, 또는 다른 JDK 버전 에서는 true 를 반환하거나 런타임 예외를 던지게 되기도 한다.
	}
}
