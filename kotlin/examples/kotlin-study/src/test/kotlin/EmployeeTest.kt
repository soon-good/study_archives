import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class EmployeeTest {

    @Test
    @DisplayName("equals() 테스트")
    fun testEmployeeEquals() : Unit{
        val fireFighter1 = Employee("소방관", 1L, 2000.0)
        val policeMan = Employee("경찰관", 1L, 2000.0)
        val copyFireFighter1 = fireFighter1.copy()
        val copyFireFighter2 = fireFighter1.copy("소방관2", 2L, 2500.0)
        val fireFighter2 = Employee("소방관", 1L, 2000.0)

        // 서로 다른 인스턴스이면서 각각의 프로퍼티 값 역시 다른 존재들을 비교해보자 => false
        println(fireFighter1.equals(policeMan))

        // 자기자신의 인스턴스 비교시 같은지 비교해보자. => 당연히 true
        println(fireFighter1.equals(fireFighter1))

        // copy() 한 인스턴스와 원본 객체 비교 => true
        // 명백히 서로 다른 인스턴스임에도 true가 나온 것을 보면 단지 값 비교 만을 수행하는 것임을 알 수 있따.
        println(copyFireFighter1.equals(fireFighter1))

        // 가지고 있는 값만 같은 완전히 다른 객체에 대한 equals 비교 => true
        // 이 역시 인스턴스 비교가 아닌 값 비교만을 수행하는 것임을 확인 가능하다.
        println(fireFighter1.equals(fireFighter2))

        // 이번에는 copy() 시에 프로퍼티의 값을 변경해서 비교해보자. => false
        // 역시 값 비교만을 수행하는 것임을 알 수 있다.
        println(fireFighter1.equals(copyFireFighter2))
    }

    @Test
    fun testToString() : Unit {
        val fireFighter1 = Employee("소방관", 1L, 2000.0)
        println(fireFighter1.toString())
    }

    @Test
    fun testCopy() : Unit {
        val fireFighter1 = Employee("소방관", 1L, 2000.0)
        val copyFireFighter1 = fireFighter1.copy()
        val copyFireFighter2 = fireFighter1.copy("소방관2", 2L, 2500.0)

        // 자기자신의 인스턴스 비교시 같은지 비교해보자. => 당연히 true
        println(fireFighter1.equals(fireFighter1))

        // copy() 한 인스턴스와 원본 객체 비교 => true
        // 명백히 서로 다른 인스턴스임에도 true가 나온 것을 보면 단지 값 비교 만을 수행하는 것임을 알 수 있따.
        println(copyFireFighter1.equals(fireFighter1))

        // 이번에는 copy() 시에 프로퍼티의 값을 변경해서 비교해보자. => false
        // 역시 값 비교만을 수행하는 것임을 알 수 있다.
        println(fireFighter1.equals(copyFireFighter2))
    }

    @Test
    fun testHashCode() : Unit {
        val fireFighter1 = Employee("소방관", 1L, 2000.0)
        val copyFireFighter1 = fireFighter1.copy()
        val copyFireFighter2 = fireFighter1.copy("소방관2", 2L, 2500.0)

        // 원본 객체의 hashCode를 출력해보자
        println("fireFighter's hashCode :: ${fireFighter1.hashCode()}")

        // 원본 객체가 가진 프로퍼티의 값을 같게 하여 copy 한 인스턴스의 hashCode() 를 출력해보자.
        // 원본 객체의 hashCode() 와 같은 값을 가지는 것을 확인 가능하다.
        println("copyFireFighter1's hashCode :: ${copyFireFighter1.hashCode()}")

        // 원본 객체에서 값을 다르게 해서 copy 한 인스턴스의 hashCode() 를 출력해보자.
        // 원본 객체의 hashCode() 와 다른 값을 가지는 것을 확인 가능하다.
        println("copyFireFighter2's hashCode :: ${copyFireFighter2.hashCode()}")
    }
}