package io.study.effective.example_all.ch02.item2.hierachial;

public class Calzone extends Pizza{

	private final boolean sauceInside;

	public Calzone(Builder builder) {
		super(builder);
		sauceInside = builder.sauceInside;
	}

	public static class Builder extends Pizza.Builder<Builder> {
		private boolean sauceInside = false;

		public Builder sauceInside(){
			sauceInside = true;
			return this;
		}

		@Override
		Pizza build() {
			return new Calzone (this);
		}

		@Override
		protected Builder self() {
			return this;
		}
	}

	@Override
	public String toString() {
		return String.format(
			"이 칼조네 파자의 소스는 %s 에 있고 토핑 으로 %s 가 들어갔습니당~",
			sauceInside ? "안" : "바깥", toppings
		);
	}
}
