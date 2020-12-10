package nullable_operator

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ElectricTest {

    @Test
    @DisplayName("?: 연산자 테스트 (Elvis Operator) #1 ")
    fun testElvisOpertor1 () : Unit {
//        var b = "abcdefgh"
        var b : String? = null
        val l: Int = if (b != null) b.length else -1
        val m: Int = b?.length ?: -1

        println("l : ${l}")
        println("m : ${m}")
    }

    @Test
    @DisplayName("?: 연산자 테스트 (Elvis Operator) #2 ")
    fun testElvisOperator2 () : Unit {
        val strDay1 = "20201203"
        val strDay2 = "20201204"

        val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
        val startDate = LocalDate.parse(strDay1, ofPattern)
        val endDate = LocalDate.parse(strDay2, ofPattern)

        val e1 = ElectricalEnergy(kwh = 2000.0, voltage = null, date = startDate)
        val e2 = ElectricalEnergy(kwh = 1500.0, voltage = null, date = endDate)

        val elecList = listOf<ElectricalEnergy>(e1, e2)
        elecList.forEach {
            val nullProcessed : Any = it.voltage?.dec() ?: 0
            println("voltage :: ${nullProcessed}")
        }
    }
}