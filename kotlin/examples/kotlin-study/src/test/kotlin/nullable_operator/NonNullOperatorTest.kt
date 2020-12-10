package nullable_operator

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NonNullOperatorTest {

    @Test
    @DisplayName("!! 연산자 테스트 >> 공백문자로 테스트해보기")
    fun testNonNullCase1() : Unit {
        val whitespace: String = ""
        val length1 = whitespace!!.length
        println(length1)
    }

    @Test
    @DisplayName("!! 연산자 테스트 >> nullable에 notnull 을 대입해보기")
    fun testNonNullCase2() : Unit {
        val label1 : String? = "테스트"
        val label2 : String = label1!!
        val label3 : String? = label1!!
        val label4 : String? = null
//        val label5 : String = label4!!  // NPE 발생

        println("label1 = ${label1}")
        println("label2 = ${label2}")
        println("label3 = ${label3}")
        println("label4 = ${label4}")
//        println("lable5 = ${label5}")
    }

    /**
     * Null Pointer Exception 이 발생한다.
     */
    @Test
    @DisplayName("!! 연산자 테스트 >> nullable 데이터 테스트")
    fun testNonNullCase3() : Unit {
        val nullableString: String? = null
        // null 데이터에 대해서 NPE (Null Pointer Exception 을 발생시킨다.)
        println("nullableString!!.length = ${nullableString!!.length}")
    }
}