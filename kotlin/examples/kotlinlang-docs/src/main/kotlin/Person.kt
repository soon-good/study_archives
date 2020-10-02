data class Person (var name: String, var age: Int = 0, var city: String = ""){
    fun moveTo(newCity: String) {city = newCity}
    fun incrementAge() {age++}
}