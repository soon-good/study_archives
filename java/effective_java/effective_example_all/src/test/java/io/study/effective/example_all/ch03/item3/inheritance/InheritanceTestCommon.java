package io.study.effective.example_all.ch03.item3.inheritance;

public class InheritanceTestCommon {

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

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	enum Color { RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET }

}
