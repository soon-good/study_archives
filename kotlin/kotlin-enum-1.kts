import java.util.*
import kotlin.collections.HashMap

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

//        fun valueOf(locale : Locale) : CountryCode?{
//            println("Locale :: ${locale} ")
//            return CountryCode.codeMap[locale]
//        }

        fun valueOf(locale: Locale) : CountryCode?{
            return getCodeMap()[locale]
        }

        fun getCodeMap(): HashMap<Locale, CountryCode>{
            return codeMap
        }
    }

}

CountryCode.initCodeMap()

println("hello")
val krCode : CountryCode = CountryCode.KOREA
val valueOfCode : CountryCode = CountryCode.valueOf("KOREA")


println("krCode : $krCode")
println("valueOfCode : $valueOfCode")
println("Locale :: ${Locale.KOREA}")

println(CountryCode.getCodeMap()[Locale.KOREA])
println(CountryCode.valueOf(Locale.KOREA))

val valueOfLazy by lazy {
    CountryCode.valueOf(Locale.KOREA)
}

val valueOfLocale = CountryCode.valueOf(Locale.KOREA)
println("valueOfLazy : $valueOfLazy")
