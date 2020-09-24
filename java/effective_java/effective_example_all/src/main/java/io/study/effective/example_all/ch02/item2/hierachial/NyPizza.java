package io.study.effective.example_all.ch02.item2.hierachial;

import java.util.Objects;

public class NyPizza extends Pizza {
	private final Size size;

	public NyPizza(Builder builder) {
		super(builder);
		size = builder.size;
	}

	public static class Builder extends Pizza.Builder<Builder> {

		private final Size size;

		public Builder(Size size){
			this.size = Objects.requireNonNull(size);
		}

		@Override
		Pizza build() {
			return new NyPizza(this);
		}

		@Override
		protected Builder self() {
			return this;
		}
	}

	@Override
	public String toString() {
		return String.format(
			"이 뉴욕피자는 토핑은 %s 가 들어갔고, 사이즈는 %s 입니다~",
			toppings, size
		);
	}
}
