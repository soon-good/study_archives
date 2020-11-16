import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class ProductTest {

    @Test
    @DisplayName("코틀린 data 클래스 > equals, hashCode 의 객체 동일성 테스트하기")
    internal fun testDataProduct_equals_hashCode(){
        val p1 = Product("baseball", 10.0)
        val p2 = Product("baseball", 10.0, false)

        Assertions.assertThat(p1).isEqualTo(p2)
        Assertions.assertThat(p1.hashCode()).isEqualTo(p2.hashCode())
    }

    @Test
    @DisplayName("코틀린 data 클래스 > equals, hashCode 를 Set 자료구조로 테스트해보기")
    fun testDataProduct_equals_hashCode_bySet(){
        val p1 = Product("baseball", 10.0)
        val p2 = Product(price = 10.0, onSale = false, name = "baseball")

        val productSet = setOf(p1, p2)
        Assertions.assertThat(productSet.size).isEqualTo(1)
    }

    @Test
    @DisplayName("코틀린 data 클래스 > copy() 메서드")
    fun testDataProduct_copy(){
        val p1 = Product("baseball", 10.0)
        val p2 = p1.copy(price = 12.0)

        Assertions.assertThat("baseball").isEqualTo(p2.name)
        Assertions.assertThat(p2.onSale).isFalse()
    }

    @Test
    @DisplayName("코틀린 data 클래스 > copy() 메서드의 얕은 복사 검증")
    fun testDataProduct_copy_swallow_copy(){
        val item1 = OrderItem(Product("baseball", 10.0), 5)
        val item2 = item1.copy()

        Assertions.assertThat(item1).isEqualTo(item2)
        Assertions.assertThat(item1 === item2).isFalse()
        Assertions.assertThat(item1.product == item2.product).isTrue()
        Assertions.assertThat(item1.product === item2.product).isTrue()
    }

    @Test
    @DisplayName("코틀린 data 클래스 > Product 인스턴스 구조분해")
    fun testDataProduct_product_destructuring(){
        val p = Product("baseball", 10.0)

        val (name, price, saleFlag) = p

        Assertions.assertThat(name).isEqualTo(p.name)
        Assertions.assertThat(price).isEqualTo(p.price)
        Assertions.assertThat(saleFlag).isEqualTo(p.onSale)
    }
}
