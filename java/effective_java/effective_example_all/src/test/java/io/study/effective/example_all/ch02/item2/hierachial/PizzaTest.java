package io.study.effective.example_all.ch02.item2.hierachial;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PizzaTest {

	@Test
	@DisplayName("pizza test")
	void testPizza(){
		Pizza nyPizza = new NyPizza.Builder(Size.SMALL)
			.addTopping(Topping.HAM)
			.addTopping(Topping.ONION)
			.build();

		assertThat(nyPizza.toppings).contains(Topping.HAM, Topping.ONION);

		Pizza calzone = new Calzone.Builder()
			.addTopping(Topping.HAM)
			.addTopping(Topping.MUSHROOM)
			.sauceInside()
			.build();

		System.out.println("=====================");
		System.out.println(nyPizza);
		System.out.println("");
		System.out.println("");
		System.out.println("=====================");
		System.out.println(calzone);
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
}
