package sample;

import sample.context.MyContextContainer;
import sample.greeting.EnglishGreeting;
import sample.greeting.KoreanGreeting;

public class App1 {
	public static void main(String [] args) throws InstantiationException, IllegalAccessException{
		MyContextContainer con = new MyContextContainer();

		KoreanGreeting kor = con.get(KoreanGreeting.class);
		kor.printMsg();

//		KoreanGreeting k = new KoreanGreeting();
//		k.printMsg();	// 이 경우 "Korean Greeting" 은 출력되지 않는다.

		System.out.println();

		EnglishGreeting eng = con.get(EnglishGreeting.class);
		eng.printMsg();

//		EnglishGreeting e = new EnglishGreeting();
//		e.printMsg();	// 이 경우 "English Greeting" 은 출력되지 않는다.
	}
}
