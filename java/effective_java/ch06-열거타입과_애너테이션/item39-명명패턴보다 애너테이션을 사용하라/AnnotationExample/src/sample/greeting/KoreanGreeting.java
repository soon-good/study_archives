package sample.greeting;

import sample.annotation.SampleAnnotation;

public class KoreanGreeting {

	@SampleAnnotation(value = "Korean Greeting")
	public void printMsg(){
		System.out.println("안뇽하세요");
	}
}
