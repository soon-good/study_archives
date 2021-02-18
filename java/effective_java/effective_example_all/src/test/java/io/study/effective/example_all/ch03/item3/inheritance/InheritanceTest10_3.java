package io.study.effective.example_all.ch03.item3.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InheritanceTest10_3 extends InheritanceTestCommon {

	class ColorPoint extends Point {

		private final Color color;

		public ColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}

		// 코드 10-3 잘못된 코드 - 추이성 위배! (57쪽)
		@Override public boolean equals(Object o) {
			if (!(o instanceof Point))
				return false;

			// o가 일반 Point면 색상을 무시하고 비교한다.
			if (!(o instanceof ColorPoint))
				return o.equals(this);

			// o가 ColorPoint면 색상까지 비교한다.
			return super.equals(o) && ((ColorPoint) o).color == color;
		}
	}

	// 코드 10-3 잘못된 코드 - 추이성 위배! (57쪽)
	// ColorPoint의 equals가 Point와 비교할 때에는 색상을 무시하도록 수정했다.
	// (cp.equals(p) 할때 색상을 무시하도록)

	// p1과 p2, p2와 p3 비교에서는 색상을 무시했지만 p1과 p3 비교에서는 색상까지 고려했기 때문에 추이성이 깨졌다.
	// 이 방식은 무한 재귀에 빠질 위험 역시 존재한다.
	@Test
	@DisplayName("ch02::10-3, 57p > 잘못된 코드, 추이성 위배")
	void testSymmetry(){
		ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
		Point p2 = new Point(1,2);
		ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

		assertThat(p1.equals(p2)).isTrue();
		assertThat(p2.equals(p3)).isTrue();
		assertThat(p1.equals(p3)).isFalse();
	}

}
