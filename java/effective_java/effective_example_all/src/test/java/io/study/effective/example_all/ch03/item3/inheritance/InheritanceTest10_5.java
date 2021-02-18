package io.study.effective.example_all.ch03.item3.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.effective.example_all.ch03.item3.inheritance.InheritanceTest10_4.AreaPoint;
import io.study.effective.example_all.ch03.item3.inheritance.InheritanceTest10_4.ColorPoint;
import io.study.effective.example_all.ch03.item3.inheritance.InheritanceTest10_4.Point;
import io.study.effective.example_all.ch03.item3.inheritance.InheritanceTestCommon.Color;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InheritanceTest10_5 {

	class Point{
		private final int x;
		private final int y;

		public Point(int x, int y){
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Point)) return false;

			Point p = (Point) o;
			return p.x == x && p.y == y;
		}
	}

	public class ColorPoint {
		private final Point point;
		private final Color color;

		public ColorPoint(int x, int y, Color color){
			point = new Point(x, y);
			this.color = Objects.requireNonNull(color);
		}

		/**
		 * 이 ColorPoint의 Point 뷰를 반환한다.
		 */
		public Point asPoint(){
			return point;
		}

		@Override
		public boolean equals(Object o) {
			if(!(o instanceof ColorPoint)) return false;
			ColorPoint cp = (ColorPoint) o;
			return cp.point.equals(point) && cp.color.equals(color);
		}

		@Override
		public int hashCode() {
			return Objects.hash(point, color);
		}
	}

	@Test
	@DisplayName("asdfasdfasdfa")
	void testTestTest(){
		ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
		Point p2 = new Point(1,2);
		ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

		assertThat(p1.equals(p2)).isFalse();
		assertThat(p2.equals(p3)).isFalse();
		assertThat(p1.equals(p3)).isFalse();
	}
}
