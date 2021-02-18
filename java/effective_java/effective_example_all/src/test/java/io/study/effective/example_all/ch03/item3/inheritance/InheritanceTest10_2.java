package io.study.effective.example_all.ch03.item3.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InheritanceTest10_2 extends InheritanceTestCommon {

	class ColorPoint extends Point {

		private final Color color;

		public ColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}

		// 코드 10-2 잘못된 코드 - 대칭성 위배! (57쪽)
		// 위치 정보가 같은지 검사하는 Point 의 equals() 와
		// 색상을 비교하는 AreaPoint 의 equals() 를 통해
		// 위치와 색상이 같을 때만 true 를 반환하는 equals 를 ColorPint 내에 구현했다.

		// 결과)
		// Point 의 equals 는 색상을 무시하고,
		// AreaPoint 의 equals는 입력매개변수의 클래스 종류가 다르다고 매번 false만 반환한다.
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ColorPoint))
				return false;
			return super.equals(o) && ((ColorPoint) o).color == color;
		}
	}

	@Test
	@DisplayName("ch02::10-2, 57p > 잘못된 코드, 대칭성 위배")
	void testSymmetry(){
		Point p = new Point(1,2);
		ColorPoint cp = new ColorPoint(1,2, Color.RED);

		assertThat(p.equals(cp)).isTrue();
		assertThat(cp.equals(p)).isFalse();
	}
}
