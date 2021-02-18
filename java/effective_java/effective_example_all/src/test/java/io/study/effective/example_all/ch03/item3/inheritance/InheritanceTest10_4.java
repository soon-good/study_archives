package io.study.effective.example_all.ch03.item3.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InheritanceTest10_4 extends InheritanceTestCommon {

	class Point {
		private final int x;
		private final int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

//		@Override public boolean equals(Object o) {
//			if (!(o instanceof Point))
//				return false;
//			Point p = (Point)o;
//			return p.x == x && p.y == y;
//		}

//		instanceof 검사를 getClass 검사로 바꾸면 규악도 지키고 값도 추가하면서 구체 클래스를 상속할 수 있다는 뜻처럼 들리기도 한다.
//     	잘못된 코드 - 리스코프 치환 원칙 위배! (59쪽)
		@Override public boolean equals(Object o) {
			if (o == null || o.getClass() != getClass())
				return false;
			Point p = (Point) o;
			return p.x == x && p.y == y;
		}

		// 아이템 11 참조
		@Override public int hashCode()  {
			return 31 * x + y;
		}
	}

	class ColorPoint extends Point {

		private final Color color;

		public ColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
	}

	class AreaPoint extends Point{
		public AreaPoint(int x, int y) {
			super(x, y);
		}
	}

	@Test
	@DisplayName("ch02::10-4 리스코프 치환 원칙 위배")
	void testLiscovSubstitution(){
		ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
		Point p2 = new Point(1,2);
		ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);
		AreaPoint p4 = new AreaPoint(1, 2);

		assertThat(p1.equals(p2)).isFalse();
		assertThat(p2.equals(p3)).isFalse();
		assertThat(p1.equals(p3)).isTrue();
		assertThat(p3.equals(p4)).isFalse();
	}
}
