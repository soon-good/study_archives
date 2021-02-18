package com.study.junit5.custom;

import com.study.junit5.custom.annotation.CustomAnnotation;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomAnnotationTest {
	@CustomAnnotation
	@ValueSource(strings = {"A", "B", "C"})
	void testCustomAnnotation(){
		System.out.println("======= testCustomAnnotation =======");
	}
}
