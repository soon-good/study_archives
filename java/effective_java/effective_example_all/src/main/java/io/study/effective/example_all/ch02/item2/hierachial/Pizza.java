package io.study.effective.example_all.ch02.item2.hierachial;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class Pizza {
	final Set<Topping> toppings;

	abstract static class Builder<T extends Builder<T>>{			// 재귀적인 타입 매개변수 (자기 자신의 하위 타입을 타입 매개변수로 받는 빌더)
		EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

		public T addTopping (Topping topping){
			toppings.add(Objects.requireNonNull(topping));
			return self();
		}

		abstract Pizza build();

		// 하위클래스 에서는  이 메서드를 재정의 해서 this (ex. NYPizza, Calzone) 를 반환하도록 구현
		protected abstract T self();
	}

	Pizza (Builder<?> builder){
		toppings = builder.toppings.clone();	// !TODO 아이템 50 :: '빌더에서 매개변수를 객체로 복사해온 다음에 확인하고'
	}
}