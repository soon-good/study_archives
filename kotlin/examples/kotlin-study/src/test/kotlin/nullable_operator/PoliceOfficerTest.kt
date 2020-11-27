package nullable_operator

import PoliceOfficer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PoliceOfficerTest {

    @Test
    @DisplayName("Nullable Test")
    fun testNullableAssignment(): Unit {
        val policeOfficer1 : PoliceOfficer = PoliceOfficer(
            name = "경찰관#1",
            address = null,
            id = 1
        )

        val policeOfficer2 : PoliceOfficer = PoliceOfficer(
            name = "경찰관#2",
            address = "South Korea",
            id = 2
        )

        val policeOfficers : MutableList<PoliceOfficer> = mutableListOf<PoliceOfficer>()
        policeOfficers.add(policeOfficer1)
        policeOfficers.add(policeOfficer2)

        policeOfficers.forEach{
            println("${it.name} :: ${it.address?.toUpperCase()}")
        }
    }
}