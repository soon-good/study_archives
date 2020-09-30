package sample

import java.util.*

//enum class CountryCode(private val codeNm: String, private val locale: Locale) { // primary constructor
enum class CountryCode ( private var codeNm: String, private var locale: Locale ) {
    KOREA ("KOREA", Locale.KOREA){},
    JAPAN ("JAPAN", Locale.JAPAN){};

    companion object {
        private val codeMap: HashMap<Locale, CountryCode> = hashMapOf()

        fun initCodeMap(){
            CountryCode.values().forEach {
                codeMap[it.locale] = it
                println("it.locale :: ${it.locale}")
//                    countryCode -> codeMap2[countryCode.locale] = countryCode
            }
        }

        fun valueOf(locale: Locale) : CountryCode?{
            return getCodeMap()[locale]
        }

        fun getCodeMap(): HashMap<Locale, CountryCode>{
            return codeMap
        }
    }

}