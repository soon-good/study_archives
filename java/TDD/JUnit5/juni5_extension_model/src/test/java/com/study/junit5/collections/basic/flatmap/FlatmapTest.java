package com.study.junit5.collections.basic.flatmap;


import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class FlatmapTest {

	@Test
	public void testFlatMap1(){
		String [] arr = {"Apple is Good", "Banana is Good", "Carrot is Good"};

		Stream<String> stream = Arrays.stream(arr);
		stream.flatMap(s->Stream.of(s.split(" "))).forEach(System.out::println);
	}
}
