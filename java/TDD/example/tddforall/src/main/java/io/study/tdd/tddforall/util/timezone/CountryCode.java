package io.study.tdd.tddforall.util.timezone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum CountryCode {
	KOREA (Locale.KOREA.getCountry(), Locale.KOREA){
		@Override
		public String getGreeting() {
			return "안녕하세용 ~!!";
		}
	},
	JAPAN (Locale.JAPAN.getCountry(), Locale.JAPAN){
		@Override
		public String getGreeting() {
			return "모시모시 ~ 스미마센";
		}
	},
	CHINA (Locale.CHINA.getCountry(), Locale.CHINA){
		@Override
		public String getGreeting() {
			return "니하오, 따쟈하오~ 짜이찌엔~!!";
		}
	};

	private String codeNm;
	private Locale locale;

	private static Map<Locale, CountryCode> codeMap = new HashMap<>();

	static {
		Arrays.stream(CountryCode.values()).forEach(
			countryCode -> codeMap.put(countryCode.locale, countryCode)
		);
		// 참고) Java 8 이전의 방식으로 풀어서 써보면...
//		for (CountryCode c : CountryCode.values()){
//			codeMap.put(c.locale, c);
//		}
	}

	CountryCode(String codeNm, Locale locale ){
		this.codeNm = codeNm;
		this.locale = locale;
	}

	public static CountryCode valueOf(Locale locale){
		return codeMap.get(locale);
	}

	public abstract String getGreeting();

	public String getFullCodeNm(){
		return this.locale.toString();
	}

	public String getLanguageCode(){
		return this.locale.getLanguage();
	}

	public String getDisplayLanguage(){
		return this.locale.getDisplayLanguage();
	}

	public String getCodeNm() {
		return codeNm;
	}

	public Locale getLocale() {
		return locale;
	}
}
