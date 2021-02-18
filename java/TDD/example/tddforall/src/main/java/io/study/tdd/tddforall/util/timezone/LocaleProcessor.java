package io.study.tdd.tddforall.util.timezone;

import java.util.Locale;

public class LocaleProcessor {

	public CountryCode getServerCountryCode(){
		Locale serverLocale = Locale.getDefault();
		return CountryCode.valueOf(serverLocale);
	}

}
