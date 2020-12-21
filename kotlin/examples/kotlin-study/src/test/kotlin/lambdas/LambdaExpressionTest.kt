package lambdas

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LambdaExpressionTest {

    @Test
    @DisplayName("제곱 테스트")
    fun testDefineLambda (): Unit {
        val sqrt = { number: Int -> number * number }
        val result = sqrt(7)
        println(result)
    }

    @Test
    @DisplayName("타입 추론")
    fun testTypeInference (): Unit {
        val magnitudeStr = { input : Int ->
            val magnitude = input * 100
            magnitude.toString()
        }

        println(magnitudeStr(100))

        val magnitudeStr2 = { input : Int ->
            val magnitude: Int = input * 100
            magnitude.toString()
        }

        println(magnitudeStr2(100))
    }

    /**
     * 타입을 명확하게 선언해야 할 경우에는 아래와 같이 선언이 가능하다.
     */
    @Test
    @DisplayName("타입 선언")
    fun testTypeDeclaration (): Unit {
        val str : String = "kg"
        val amount : Int = 58

        val appendVal = "" + amount + str
        println("String + Int 테스트 :: ${appendVal}")

        val doubleScale : (Int) -> Int = { a:Int -> a*2 }
        println("두배 곱해보기 :: ${doubleScale(10)}")

        val toKilogram : (Int, String) -> String = { amount, kg ->  "" + amount + kg }
        println("kilogram 단위 문자열 붙여보기 :: ${toKilogram(10, " (kg)")}")

        val voidLambda : (Int) -> Unit = { input:Int -> println(input) }
    }

    @Test
    @DisplayName("계절 이름 출력해보기")
    fun testWhenCaseLambda1(): Unit {
        val inferSeason = { month : Int ->
            when (month) {
                in 3..5 -> "SPRING"
                in 6..8 -> "SUMMER"
                in 9..11 -> "FALL"
                12 -> "WINTER"
                in 1..2 -> "WINTER"
                else -> "No Such Season Exist Exception"
            }
        }

        println("12월 :: ${inferSeason(12)}")
        println("1월 :: ${inferSeason(1)}")
        println("2월 :: ${inferSeason(2)}")
        println("")

        println("3월 :: ${inferSeason(3)}")
        println("4월 :: ${inferSeason(4)}")
        println("5월 :: ${inferSeason(5)}")
        println("")

        println("6월 :: ${inferSeason(6)}")
        println("7월 :: ${inferSeason(7)}")
        println("8월 :: ${inferSeason(8)}")
        println("")

        println("9월 :: ${inferSeason(9)}")
        println("10월 :: ${inferSeason(10)}")
        println("11월 :: ${inferSeason(11)}")
    }

    @Test
    @DisplayName("Lambda 식의 축약형, it 키워드 사용해보기")
    fun test_shorthand_lambda_expression () : Unit {
        // 단순 정수 리스트를 기준으로 생각해보자.
        // 람다를 일반적인 경우의 긴 표현식으로 사용하면 아래와 같다.
        val intList = arrayOf(1,3,5,7,9)

        println("축약형이 아닌 일반적인 경우의 람다식을 사용한 결과 =======")
        intList.forEach { number -> println(number) }

        println("축약형 키워드 it 키워드를 이용해 람다식을 사용한 결과 =======")
        intList.forEach { it -> println(it) }
    }

    fun anyFunctionsToNumberThree (lambda : (Int) -> Int ) : Int {
        return lambda(3)
    }

    @Test
    @DisplayName("Implementing Lambdas >>> Overview")
    fun test_implementing_lambdas_overview(): Unit {
        val sqrtToThree : Int = anyFunctionsToNumberThree { number -> number * number }
        val threePlusThree : Int = anyFunctionsToNumberThree { number -> number + number }
        val threeMinusThree : Int = anyFunctionsToNumberThree { number -> number - number }
        val divideNumberThree = anyFunctionsToNumberThree { number -> number/number }
        val multiplyThree = anyFunctionsToNumberThree { number -> number*number }

        println("sqrtTo3 :: ${sqrtToThree}")
        println("threePlusThree :: ${threePlusThree}")
        println("threeMinusThree :: ${threeMinusThree}")
        println("divideNumberThree :: ${divideNumberThree}")
        println("multiplyThree :: ${multiplyThree}")
    }
}