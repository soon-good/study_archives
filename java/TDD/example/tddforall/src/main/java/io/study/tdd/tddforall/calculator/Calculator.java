package io.study.tdd.tddforall.calculator;

import io.study.tdd.tddforall.util.timezone.CountryCode;
import io.study.tdd.tddforall.util.timezone.LocaleProcessor;

public class Calculator {

	private final LocaleProcessor localeProcessor;

	public Calculator (LocaleProcessor localeProcessor){
		this.localeProcessor = localeProcessor;
	}

	public int add (int left, int right){
		CountryCode serverCountryCode = localeProcessor.getServerCountryCode();
		String greeting = serverCountryCode.getGreeting();
		System.out.println(greeting);

		return left + right;
	}

	public int devide (int left, int right){

		return left/right;
	}
}
