# data 클래스 (1) - 제공되는 기능들

> data 클래스가 제공해주는 기본적인 기능들을 정리해보자.

코틀린 문법 기초 개념들을 언제 정리하지? 하는 그런 좌절감과 압박감을 느껴왔는데, 일주일에 딱 하루 한개 내지 두개의 개념으로 정리할까 생각중이다. 이렇게 하면 다른 공부 들과 비율이 분배가 되는 것 같아서이다.

# 참고자료

[https://kotlinlang.org/docs/reference/data-classes.html](https://kotlinlang.org/docs/reference/data-classes.html)  

외국 자료들 중 공식문서들을 보면 가끔 너무 쓰잘 데기 없이 함축적으로 비유해서 설명하는 경우가 있는 것 같다는 느낌을 받을때가 있다... 그래서 여러 번 경험에 의해 써봤을때에 비로소 조금이나마 이해하게 되는 경우가 있는 것 같다...  코틀린 공식문서도 가끔은 그런것 같다. 뭔가 코틀린을 여러번 써온 사람들이 보면 의역으로 결론을 내주실것 같다는 생각도 든다.   

일단 오늘 정리하는 글에서는 data 클래스가 가진 프로퍼티들에 대해 코틀린 내부적으로 제공하는 유틸리티 함수들을 정리해보려고 한다. 조금 더 자세한 내용으로 주 생성자(primary constructor) 등에 따라서 어떻게 변하고 등등에 대해 공식문서에 대해서 다루고 있는데 여기에 대해서는 다음 글에서 정리할 예정이다.  

오늘 공부의 목적은 단지 data 클래스의 toString(), hashCode(), equals(), copy() 함수가 어떻게 생겼는지 확인하고 테스트 코드를 작성해보는 것이 목적이다.  

## 테스트 코드 위치

- [kotlin-study](https://github.com/soongujung/study_archives/tree/master/kotlin/examples/kotlin-study)  
- [EmployeeTest.kt](https://github.com/soongujung/study_archives/blob/master/kotlin/examples/kotlin-study/src/test/kotlin/EmployeeTest.kt)  



# data 클래스가 제공하는 기능들 (요약)

공식 문서 (kotlinlang.org)의 내용을 그대로 해석하면 국어책같기도 하고 기계어 같기도 해서 이상한 말이 되버린다. 이런 이유로 주관적인 의견을 섞어서 의역으로 정리해봤다...  

data 를 hold 하는 성격의 클래스라면 data 클래스를 도입해보는 것을 검토해볼 만 하다. 코틀린에서는 data 키워드를 붙여서 선언한 클래스인 data 클래스들에 대해 이 data 클래스에 대한 표준 기능, 유틸리티성의 함수들을 Kotlin 내부적으로 컴파일러에의해 생성(파생 - derived)된 코드로 제공해준다.  

```kotlin
data class User(val name: String, val age: Int)
```

data 클래스의 주 생성자 (primary constructor)에 선언된 각각의 프로퍼티에 대해 코틀린 내부적으로 아래의 함수가 빌트인으로 생성되는데, 각 프로퍼티에 대한 동등비교, copy() 연산, hashCode() 코드를 생성해내는 역할을 한다. (아래의 함수들은 왠지... Effective Java 에서도 많이 봐었던 함수들이다.)  

- copy()
- toString()
- equals()
- hashCode()



# 예제 클래스 "Employee"

예제로 사용할 클래스는 Employee 이다. Employee 클래스에는 data 클래스로 선언되어 있고 아래와 같이 세가지의 필드가 있다.

- val name: String
- val deptId: Long
- val salary: Double

코틀린으로 작성하면 아래의 코드가 된다.  



# Employee.kt

```kotlin
data class Employee (
    val name: String,
    val deptId: Long,
    val salary: Double
){
}
```

이제 이 data 클래스를 Byte Code 로 분해한 후 Java 코드로 decompile 해서 어떻게 변환되는지 확인해볼 예정이다. (참고: [Java 변환 코드 확인방법](https://gosgjung.gitbook.io/lognomy/kotlin-study/3/java))  



# Employee.decompiled.java

> Java 로 변환된 코드이다.

자바로 변환된 코드를 보면 아래와 같다.

```java
public final class Employee {
   @NotNull
   private final String name;
   private final long deptId;
   private final double salary;

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final long getDeptId() {
      return this.deptId;
   }

   public final double getSalary() {
      return this.salary;
   }

   public Employee(@NotNull String name, long deptId, double salary) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      super();
      this.name = name;
      this.deptId = deptId;
      this.salary = salary;
   }

   @NotNull
   public final String component1() {
      return this.name;
   }

   public final long component2() {
      return this.deptId;
   }

   public final double component3() {
      return this.salary;
   }

   @NotNull
   public final Employee copy(@NotNull String name, long deptId, double salary) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      return new Employee(name, deptId, salary);
   }

   // $FF: synthetic method
   public static Employee copy$default(Employee var0, String var1, long var2, double var4, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.name;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.deptId;
      }

      if ((var6 & 4) != 0) {
         var4 = var0.salary;
      }

      return var0.copy(var1, var2, var4);
   }

   @NotNull
   public String toString() {
      return "Employee(name=" + this.name + ", deptId=" + this.deptId + ", salary=" + this.salary + ")";
   }

   public int hashCode() {
      String var10000 = this.name;
      int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
      long var10001 = this.deptId;
      var1 = (var1 + (int)(var10001 ^ var10001 >>> 32)) * 31;
      var10001 = Double.doubleToLongBits(this.salary);
      return var1 + (int)(var10001 ^ var10001 >>> 32);
   }

   public boolean equals(@Nullable Object var1) {
      if (this != var1) {
         if (var1 instanceof Employee) {
            Employee var2 = (Employee)var1;
            if (Intrinsics.areEqual(this.name, var2.name) && this.deptId == var2.deptId && Double.compare(this.salary, var2.salary) == 0) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}
```



# 생성자

Employee.kt 에 선언했던 각각의 프로퍼티 name, deptId, salary 는 Java 코드 내에서는 각각 name, deptId, salary 필드에 대한 getter/setter, 생성자로 변환된다.  

java 로 변환된 Employee 클래스를 보면 아래와 같다.  

```java
public final class Employee {

   @NotNull
   private final String name;
   private final long deptId;
   private final double salary;

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final long getDeptId() {
      return this.deptId;
   }

   public final double getSalary() {
      return this.salary;
   }

   public Employee(@NotNull String name, long deptId, double salary) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      super();
      this.name = name;
      this.deptId = deptId;
      this.salary = salary;
   }
   
   // .... 
}
```

  

# copy(...)

> copy() 함수는 생성자에 전달된 인자에 의한 또 다른 객체를 생성해서 반환한다. 따라서 copy() 로 생성된 인스턴스의 caller인스턴스와 callee 인스턴스는 서로 다른 인스턴스이다.  

  

Java 로 변환된 Employee 클래스의 copy(...) 함수를 보면 아래와 같다.  

```java
public final class Employee {

   // ...
   
   @NotNull
   public final Employee copy(@NotNull String name, long deptId, double salary) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      return new Employee(name, deptId, salary);
   }
   
   // ...
}
```

kotlin 으로 작성한 테스트 코드로 동작을 확인해보자.  

**EmployeeTest.kt**  

```kotlin
class EmployeeTest {

    // ... 

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
    
    // ...
}
```

**출력결과**  

```plain
true
true
false
```



# toString()

각 필드들은 "Employee(name=누구, deptId=1)" 과 같은 형식으로 변환된다. java 로 변환된 Employee 클래스는 아래와 같다.

```java
public final class Employee {
   // ...
   
   @NotNull
   public String toString() {
      return "Employee(name=" + this.name + ", deptId=" + this.deptId + ", salary=" + this.salary + ")";
   }
   
   // ...
}
```

테스트 코드로 동작을 확인해보자.  

**EmployeeTest.kt**  

```java
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class EmployeeTest {

    // ...

    @Test
    fun testToString() : Unit {
        val fireFighter1 = Employee("소방관", 1L, 2000.0)
        println(fireFighter1.toString())
    }
}
```



**출력결과**  

```plain
Employee(name=소방관, deptId=1, salary=2000.0)
```



# equals()

> 인스턴스 간의 비교가 아닌 프로퍼티 각각의 값을 1:1 비교하는 것으로 보인다.

java 코드로 변환된 Employee 클래스 내의 equals() 코드는 아래와 같다.

```java
public final class Employee {

   // ...

   public boolean equals(@Nullable Object var1) {
      if (this != var1) {
         if (var1 instanceof Employee) {
            Employee var2 = (Employee)var1;
            if (Intrinsics.areEqual(this.name, var2.name) && this.deptId == var2.deptId && Double.compare(this.salary, var2.salary) == 0) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   // ...
   
}
```



이 equals () 의 동작을 확인해보기 위해 Kotlin 으로 아래의 테스트 코드를 작성했다.  

```kotlin
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
}
```



# hashCode()

Java 로 변환된 Employee 클래스의 hashCode() 는 아래와 같다.  

```java
public final class Employee {

   // ...

   public int hashCode() {
      String var10000 = this.name;
      int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
      long var10001 = this.deptId;
      var1 = (var1 + (int)(var10001 ^ var10001 >>> 32)) * 31;
      var10001 = Double.doubleToLongBits(this.salary);
      return var1 + (int)(var10001 ^ var10001 >>> 32);
   }

   // ...
   
}
```

  

코틀린으로 작성한 테스트 코드를 작성해보면 아래와 같다.

```kotlin
class EmployeeTest {

    // ...

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
```



출력결과

```plain
fireFighter's hashCode :: 1075720162
copyFireFighter1's hashCode :: 1075720162
copyFireFighter2's hashCode :: 822299789
```

copy() 를 통해 생성된 인스턴스와 원본 인스턴스가 서로 같은 hashCode()를 가지는 것을 확인 가능하다.  

