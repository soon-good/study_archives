package more_language_constructs

import Person
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ScopeFunctionsTest {

    /**
     * https://kotlinlang.org/docs/reference/scope-functions.html
     * 예제가 길지 않다. 개념을 읽어보는게 조금 힘들뿐 ...
     */

    /*
    코틀린 표준 라이브러리는 오직 오브젝트의 컨텍스트 내에서 코드 블록을 실행하기 위한 목적을 가진 몇몇 함수를 가지고 있다.
    이런 함수들을 특정 객체 A 에 대해 lambda 표현식과 함께 호출하면, 이때 임시적인 스코프가 형성된다.
    이 임시적인 스코프 내에서 스코프 내에서 여러분은 특정 객체 A 의 이름 없이 객체 A에 접근할 수 있습니다.
    이런 함수들을 scope functions 라고 부릅니다.
    이 scope functions 에는 let, run, with, apply, also 가 있습니다.
    * */

    @Test
    @DisplayName("context object > 단순예제 (let) ")
    fun testContextObejct_simpleExample(){
        Person("Alice", 20, "Amsterdam")
            .let {
                println(it)
                it.moveTo("London")
                it.incrementAge()
                println(it)
            }
    }

    /*
    위의 코드를 let 없이 사용하면 아래와 같은 코드가 된다.
    * */
    @Test
    @DisplayName("context object > 단순예제 (without let) ")
    fun testContextObject_withoutLetExample(){
        val person = Person("Alice", 20, "Amsterdam")
        println(person)
        person.moveTo("London")
        person.incrementAge()
        println(person)
    }

    /*
    위의 두 예를 비교해보면 let을 사용했을 때가 코드가 더 간결해지고, 호흡이 좋은 것을 느낄 수 있다.
    물론, let의 사용법만 안다는 가정하에 그렇다.
    scope functions 는 새로운 기술적인 능력을 소개하는 것은 아니다. 단지 코드를 더욱 명확하고 가독성있게 만들어줄 뿐이다.
    * */

    /* Distinctions
    https://kotlinlang.org/docs/reference/scope-functions.html#distinctions
    scope functions 들은 모두 각각 본질은 같다. 하지만 각각의 scope function 들간에 다른 점들이 있는데
    이것들을 이해하는 것이 중요하다.
    - The way to refer to the context object
      - context object 를 가리키는(참조하는) 방식
    - The return value
      - scope functions 들의 반환 값 (return value)
    **/

    /*
    Context object : this or it
    https://kotlinlang.org/docs/reference/scope-functions.html#context-object-this-or-it
    scope function 의 람다 내부에는 context object의 실제 이름 대신 짧게 줄여쓴 참조변수를 통해 context object 에 접근가능하다.
    각각의 scope function 은 context object에 접근하기 위해 두 가지 중 한가지 방식을 사용한다.
    (this : lambda receiver, it : lambda argument )
    this, it 모두 같은 기능(capabilities)을 제공한다.

    this
        - run, with, apply
    it
        - let, also

    apply, also
        - return value 가 context object 이다.
    let, run, with
        - return value 가 lambda result 이다.

    **/
    @Test
    @DisplayName("context object > this or it")
    fun testThisOrIt(){
        val str = "Hello"

        str.run {
            println("The receiver string length : $length")             // (1)
            println("The receiver string this.length : ${this.length}") // (2)
            // (1) 과 (2) 의 length 와 this.length 는 서로 같은 구문이다.
            // .run{...} 내부에서는 this를 이용한 구문과 this를 생략한 구문은 같은 역할이다. (설명하니 더 이상해졌다.)
        }

        str.let {
            println("The receiver string's length is ${it.length}")
        }
    }

    /*
    run, with, apply 함수는 this 키워드를 이용해 context object 를 lambda receiver 로서 가리킨다.
    따라서 run, with, apply 함수의 람다 안에서 context object 는 일반적인 클래스 function 들 처럼 사용할 수 있다.
    receiver 객체의 멤버들에 접근할 때, 코드를 짧게 하기 위해 거의 대부분의 상황에서 this 키워드를 생략할 수 있다.

    한편으로는, this 키워드를 생략하면, 리시버 멤버들(receiver members)과 외부 객체들 및 외부 함수들을 구별하기 어려워진다.
    따라서 context object를 reciever(this)로 가지고 잇는 것은 해당객체에 대해 메인으로 연산을 수행하는 람다들에만 궍장된다.
    (예를 들면 해당되는 객체애 대한 함수호출 또는 프로퍼티 초기화 구문에만 사용)
    * */
    @Test
    @DisplayName("context object > this > run, with, apply")
    fun testThis(){
        val adam = Person("Adam").apply {
            age = 20
            city = "London"
        }
        println(adam)
    }

    /*
    let, also 함수는 context object 를 this 키워드를 통해 lambda receiver 로서 가리킨다.
    let, also 는 context object 를 lambda argument로 가지고 있는다. lambda 의 argument 이름을 정해주지 않으면
    object는 it라는 이름으로 접근 가능하다. it 는 this 보다 짧고 표현식도 읽기에 편하다는 주관적인 의견이 있다.

    하지만 객체의 함수호출 또는 properties를 접근할 때 this 처럼 lambda를 호출한 object를 가지고 있지 않다.
    따라서 context object를 it로 가지고 있는 것은 거의 대부분 context object가 함수 호출안에서 argument로 사용되었을 때이다.
    it 는 코드 블록 내에서 여러개의 변수들을 사용하고 있을 때에도 사용하기 좋다.
    * */
    @Test
    @DisplayName("context object > it (1) > let, also")
    fun testIt_1_LetAlso(){
        var randomN = Random.nextInt(100).also {
            println("Random.nextInt(100) generated value ${it}")
        }
        println("randomN :: ${randomN}")
    }

    /*
        it 대신 다른 이름을 사용하고 싶다면, 아래와 같이 scope 내에서 사용하고 싶은 context object 에 대한 이름을 직접 전달해주면 된다.
    * */
    @Test
    @DisplayName("context object > it (2) > let, also")
    fun testIt_2_LetAlso(){
        var randomN = Random.nextInt(100).also { value ->
            println("Random.nextInt(100) generated value ${value}")
        }
        println("randomN :: ${randomN}")
    }

    /*
    Context object
    apply, also 의 리턴 값은 context object 자체이다.
    따라서 call chain 안에 context object 가 포함되는 곳이 가능하다
    예를 들면 여러분은 같은 객체에 대해 연산이 끝난 뒤에도 function chaing을 계속 지속할 수 있다.
     */
    @Test
    @DisplayName("context object > apply, also 메서드")
    fun testApplyAndAlso(){
        val numberList = mutableListOf<Double>()

        numberList.also { println("Populating the list") }
            .apply {
                add(2.71)
                add(3.14)
                add(1.0)
            }
            .also { println("list :: ${numberList.toString()}") }
            .also { println("Sorting the list") }
            .sort()
            .also { println("Sorted List :: ${numberList.toString()}") }
    }

    /*
    <Lambda result>
    let, run, with 는 lambda result를 리턴한다.
        - 결과를 변수에 저장할때 사용하거나
        - 반환할 result 에 chaining operations 들을 적용하는 것 또한 가능하다.
        ( 결과값이 람다이므로 chaining operations 들을 람다에 적용하는 것 또한 가능하다.)
    위에서 잠깐 정리했었는데, let, run, with 는 return value가 lambda 이다.
    * */
    @Test
    @DisplayName("Lambda result > let, run, with (1) - the return value")
    fun testLetRunWith1(){
        val numbers = mutableListOf("one", "two", "three")
        val countEndsWithE = numbers.run {
            add("four")
            add("five")
            count { it.endsWith("e") }
        }

        println("There are $countEndsWithE elements that end with e.")
    }

    /*
    let, run, with 는 return value로 lambda result 를 리턴하는데
    - 이 return value를 반환한다는 점을 무시하고 scope function 을 이용해 변수에 대한 임시적인 스코프(temporary scope)를 만들수 있다.
    이부분 번역이 조금 애메하다.
    * */
    @Test
    @DisplayName("Lambda result > let, run, with (2) - temporary scope for variables")
    fun testLetRunWith2(){
        val numbers = mutableListOf("one", "two", "three")
        with (numbers){
            val firstItem = first()
            val lastItem = last()
            println("First item : $firstItem, last item : $lastItem")
        }
    }

    /*
    Functions
    https://kotlinlang.org/docs/reference/scope-functions.html#functions
    어떤 경우에 어떤 scope function 을 선택할지 선택에 도움을 줍니다.
    세부적인 내용과 권장 용법을 제공합니다.
    * */

    /**
     let
     context object는 argument 인 it 로 사용가능하다.
     return value 는 lambda result 이다.
     let 은 call chain의 결과에 대해 하나 이상의 함수들을 호출할 때에 사용된다.
     */

    /*
    let (1) : without let
    let 이 없는 아래의 코드는 하나의 컬렉션에 대한 두 작업의 결과를 프린트 한다.
    * */
    @Test
    @DisplayName("Functions > let (1) > without let function")
    fun testLet1WithoutLet(){
        val numbers = mutableListOf("one", "two", "three", "four", "five")
        val resultList = numbers.map { it.length }.filter { it > 3 }
        println(resultList)
    }

    /*
    let (2)
    let을 사용하면 아래의 결과를 낸다.
    * */
    @Test
    @DisplayName("Functions > let (2) > with let function")
    fun testLet2WithLet(){
        val numbers = mutableListOf("one", "two", "three", "four", "five")
        numbers.map { it.length }.filter { it > 3 }
            .let {
                println (it)
            }
    }

    /*
    let (3)
    위의 예는 더 줄여서 아래와 같이 표현 가능하다.
     */
    @Test
    @DisplayName("Functions > let (3) > with let function")
    fun testLet3WithLet(){
        val numbers = mutableListOf("one", "two", "three", "four", "five")
        numbers.map { it.length }.filter { it > 3 }
            .let(::println)
    }

    /**
     let (4) - non-null value 와 사용하기
     일반적으로 let 은 non-null value 들로만 code block을 실행하는데에 자주 사용된다.
     non-null object 들에 대해 작업을 수행하려면, 안전한 호출연산자(safe call operator)인 ?. 을 it에 사용한다.
     */
    @Test
    @DisplayName("Functions > let (4) > with let function, 안전한 호출연산자 ?. 을 사용해 호출하기")
    fun testLet4WithLet_safeCallOperator(){
        val str:String? = "Hello~"

        val length = str?.let {
            println("$it.length")
            processNonNullString(str)
            it.length
        }
    }

    private fun processNonNullString(str: String){}

    /**
    let (5) :: it 대신 이름을 가진 변수 사용

    참고)
      it 대신 local variable 을 사용할 수 있다는 것을 정리했었다.
      it 대신 local variable 을 사용한다는 것은 it 대신 이름을 직접 지어 람다식 내에 전달한다는 의미이다.

    it 대신 local variable 을 사용하면
     - 제한된 scope 와 함께 local variable 을 사용할 수 있고
     - 가독성 또한 향상되므로 좋은 예이다.

    (영문을 해석한 것이라 다소 딱딱하긴 하다.)
    context object it 에 대해 새로운 변수를 정의하려면,
    default 로 사용하게 되는 it 대신
    lambda argument 로서의 context object 에 대한 이름을 제공해주면 된다.
     */
    @Test
    @DisplayName("Functions > let (5) > with let function, it 대신 이름을 가진 변수 사용")
    fun testLet5WithLet_renamedContextObjectParameter(){
        val numbers = listOf("one", "two", "three", "four")

        val modifiedFirstItem = numbers.first().let { firstItem ->
            println("(수정중) 리스트 numbers 의 첫 번째 아이템 :: $firstItem")
            if (firstItem.length >= 5) firstItem else "!$firstItem!"
        }.toUpperCase()

        println("수정 후의 첫번째 아이템 : $modifiedFirstItem")
    }

    /**
     with, run, apply, also
     */

    /**
    with
     : non-extension function 의 한 종류이다.
     : context object 가 argument로 전달되고, lambda 내부에서는 receiver 인 this 로 사용가능하다.
     : with 함수는 lambda 를 result 로 반환하지 않으면서 context object에 대한 함수 호출로 사용하는 것을 권장하고 있다.
     : (lambda 식을 return 하지 않을 때의 경우를 말하는 듯하다.)
     : with 는 문장으로 읽을 때 이렇게 읽으면 된다. "with this object, do the following"
     */
    /**
     with (1) : with 사용해보기
     */
    @Test
    @DisplayName("Functions > with (1)")
    fun testWith1(){
        val numbers = mutableListOf("one", "two", "three")
        with (numbers) {
            println("with this object, do the following")
            println("'with' is called with argument $this")
            println("numbers 가 전달되었으므로 numbers의 this 는 list 이다. 따라서 ...")
            println("size 를 출력해보면 ${size} 이고 ")
            println("this.size 를 해보면 ${this.size} 인데, ")
            println("this.size 와 size 는 같은 표현이고, size 는 축약형 표현이다.")
        }
    }
    /**
     with (2) : with 사용해보기
     with 함수가 내부의 값을 처리해 계산하는 헬퍼함수(helper function)를 만드는 것 역시 가능하다.
     */
    @Test
    @DisplayName("Functions > with (2)")
    fun testWith2(){
        val numbers = mutableListOf("one", "two", "three")
        val strDisplay = with (numbers){
            "첫번째 요소는 ${first()} 이고 제일 마지막 요소는 ${last()} 입니다~"
//            "첫번째 요소는 ${this.first()} 이고 제일 마지막 요소는 ${this.last()} 입니다~"
        }
        println("처리된 문구는 아래와 같습니다...")
        println("${strDisplay}")
    }

    /**
     run (1)
     context object 는 receiver(this)로 사용할 수 있다.
     return value 는 lambda 의 결과이다. (lambda result)
     run 은 with와 동작은 같지만 호출은 let과 같은 방식으로 호출된다.
     run 은 lambda가 object의 초기화와 return value 에 대한 계산(computation)을 포함하고 있을때 유용하다.

     참고)
     let :
        context object의 extension function 역할을 수행한다.
     */

    class MultiportService(var url: String, var port: Int){
        fun prepareRequest(): String = "Default request"
        fun query(request: String): String = "Result for query '$request'"
    }

    @Test
    @DisplayName("Functions > run (1)")
    fun testRun1(){
        val service = MultiportService("http://example.kotlinlang.org", 80)

        val result = service.run {
            port = 8000
            query(prepareRequest() + "to port $port")
        }

        val letResult = service.let {
            it.port = 8000
            it.query(it.prepareRequest() + "to port ${it.port}")
        }

        println(result)
        println(letResult)
    }

    /**
     run (2)
      run 함수를 receiver object 에서 호출하는 것 외에도
      run 함수는 non-extension function(비확장 함수)으로 사용할 수 있다.

     Non-extension run lets you execute a block of several statements where an expression is required.
      non-extension 함수 run 은 표현식이 필요한 여러 구문들의 블록을 실행할 수 있도록 해준다.
     */
    @Test
    @DisplayName("Functions > run (2)")
    fun testRun2(){
        val hexNumberRegex : Regex = run {
            val digits = "0-9"
            val hexDigits = "A-Fa-f"
            val sign = "+-"

            Regex("[$sign]?[$digits$hexDigits]+")
        }

        for (match in hexNumberRegex.findAll("+1234 -FFFF not-a-number")){
            println(match.value)
        }
    }

    /**
     apply
     context object 는 receiver (this) 로 사용할 수 있다.
     return value 는 객체 자신이다.

     value 를 리턴하지 얺는 코드 블록, 주로 receiver 객체의 멤버에 대해 연산을 하는 코드 블록
     에서 apply를 사용해라.

     apply를 사용하는 주된 케이스(실제 사례)는 object configuration 이다.
     apply 함수는 이렇게 읽을수 있다. "apply the following assignments to the object"
    */
    @Test
    @DisplayName("Functions > apply")
    fun testApply(){
        data class Person(val name: String, var age: Int = 0, var city: String = "")

        fun main(){
            println ("apply the following assignments to the object")
            val adam = Person("Adam").apply {
                age = 32
                city = "London"
            }

            println(adam)
        }
    }

    /**
     also
     context object 는 argument,인자(it)로 사용될 수 있다.
     return value 는 context object 자신이다.
     also 는 context object 를 인자로 취하는 몇몇 동작들을 수행하는데에 사용하면 좋다.

     context object 의 프로퍼티들/함수가 아닌 object 에 대한 참조가 필요한 작업이거나 (or)
     outer scope (외부 범위) 에서 이 참조를 섀도잉하지 않으려는 경우
     에 'also'를 사용한다.

     also 는 이렇게 읽을 수 있다. "and also do the following with the object"
     */
    @Test
    @DisplayName("Functions > also")
    fun testAlso(){
        val numbers = mutableListOf("one", "two", "three")
        numbers
            .also { println("The list element before adding new one : $it") }
            .add("four")
    }

    /**
     Function selection
     https://kotlinlang.org/docs/reference/scope-functions.html#function-selection
     */

    /**
     takeIf, takeUnless
     https://kotlinlang.org/docs/reference/scope-functions.html#takeif-and-takeunless
     내피셜)
     실제로 쓸모가 있는지는 잘 모르겠다. NPE를 다시 무력화하는 구문이기도 하고, takeIf, takeUnless를 쓸바에야
     filter를 사용하는 편이 낫다는 생긱이 든다.
     */

    /**
     call chains 에서의 object state 의 검사(check)를 검사할 수 있게 해준다.
     object 에 대한 takeIf 가 내부의 조건자(predicate) 에 따라서 다른 동작을 한다.
     조건자에 따른 takeIf, takeUnless의 동작은 아래와 같다.
     takeIf 의 경우는
        조건자의 결과가 참이면 그대로 객체를 return
        조건자의 결과가 거짓이면 null 을 return 한다.

     takeUnless 의 경우는
        조건자의 결과가 참이면 null 을 return
        조건자의 결과가 거짓이면 그대로 객체를 return 한다.

     // 번역해놓고 보니 쓸데 없는 말들 --- 지울지 결정하기
     조건자(predicate)가 제공된(object with a predicate provided) 객체에 takeIf 가 호출되면,
       takeIf는 조건자(predicate)와 일치하는 경우에  이 객체를 반환한다.
            따라서 takeIf 는 single object 에 대한 filtering function 이다.
       takeUnless 는 조건자와 일치하지 않으면 객체를 반환하고 일치하면 null 을 반환한다.
     */
    @Test
    @DisplayName("Functions > takeIf and takeUnless (1)")
    fun testTakeIfTakeUnless1(){
        val number = Random.nextInt(100)

        println("number : $number")
        val evenOrNull = number.takeIf { it % 2 == 0 }
        val oddOrNull = number.takeUnless { it % 2 == 0 }

        println("even : $evenOrNull, odd: $oddOrNull")
    }

    /**
     takeIf, takeUnless 함수 뒤에 다른 함수들을 체이닝할때,
      - null 체크
     또는
     - safe call (?.)
     을 사용해야 한다는 사실을 잊지 말아야 한다.

     null 객체에 대해 체이닝이 걸리면 그 뒤의 동작들이 모두 멈춰버리기 때문이다.
     */
    @Test
    @DisplayName("Functions > takeIf and takeUnless (2)")
    fun testTakeIfTakeUnless2(){
        val str = "Hello"
        val caps = str.takeIf { it.isNotEmpty() }?.toUpperCase()

        println("caps :: ${caps}")
    }

    /**
     takeIf, takeUnless 는 let 등과 같은 scope function 들과도 사용이 가능하다.
     */
    @Test
    @DisplayName("Functions > takeIf and takeUnless (3)")
    fun testTakeIfTakeUnless3(){
        val input: String = "010000011"
        val sub: String = "11"

        input.indexOf(sub).takeIf { it >= 0 }?.let {
            println("The substring $sub is found in $input")
            println("Its start position is $it.")
        }
    }
}