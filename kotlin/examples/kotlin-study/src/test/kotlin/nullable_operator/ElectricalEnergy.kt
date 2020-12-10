package nullable_operator

import java.time.LocalDate

data class ElectricalEnergy (
    var kwh : Double?,
    var voltage: Double?,
    val date: LocalDate
){
}