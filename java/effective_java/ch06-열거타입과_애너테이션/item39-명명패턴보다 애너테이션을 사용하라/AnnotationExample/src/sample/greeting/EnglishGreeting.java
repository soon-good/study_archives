package sample.greeting;

import sample.annotation.SampleAnnotation;

public class EnglishGreeting {
	@SampleAnnotation(value="English Greeting")
	public void printMsg(){
		System.out.println("Hi, There...");
	}
}
