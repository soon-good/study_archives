package nullable_operator

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TheOperatorAsTest {

    @Test
    @DisplayName("unsafe operator 1")
    fun testOperatorAsCase1() : Unit {
        val y : Any = "hello"
        val x : String = y as String
        println("x is ... ${x}")
    }

    @Test
    @DisplayName("unsafe operator 2")
    fun testOperatorAsCase2() : Unit {
        val y : Any? = null
        val x : String? = y as? String?
        println("x is ... ${x}")
    }

    @Test
    @DisplayName("unsafe operator 3")
    fun testOperatorAsCase3() : Unit {
        val y : Any? = null
        val x : String? = y as String   // Type cast Exception ( Any? -> String )
        println("x is ... ${x}")
    }

}