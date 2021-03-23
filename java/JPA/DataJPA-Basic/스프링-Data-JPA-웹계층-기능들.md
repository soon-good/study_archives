# 3. 도메인 클래스 컨버터

그다지 권장하는 방식은 아니다. Controller 내의 HTTP 요청을 받는 메서드에서 메서드의 파라미터로 도메인 클래스(엔티티 클래스)를 두면, HTTP 요청에 의해 전달되는 데이터를 스프링 내부에서 실제 인자값을 도메인 클래스내의 해당 멤버 필드로 바인딩해준다.  

  

단점은 화면에 종속적인 정보를 도메인에 연결시킨다는 점이다. 이렇게 할 경우, 기획이 자주 바뀔수 밖에 없는 화면단의 관점에 따라 도메인이 변하게 되고, 도메인에 종속된 repository 들의 로직들도 일괄적으로 변할 수 밖에 없다는 점이 단점이다.  

  

가급적이면, 굉장히 급한 프로토타입을 만드는 경우가 아니고서는 권장되지 않는 방식이다. 주의할것!!!

## 예제

### EmpController.java

```java
@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}

	// 도메인 클래스 컨버터 예제 - 일반 요청 (비교를 위해 예제로)
	@GetMapping("/employee/v1/{id}")
	public String getEmployeeById(@PathVariable("id") Long id){
		Employee employee = empDataRepository.findById(id).get();
		return employee.getUsername();
	}

	// 도메인 클래스 컨버터 예제 - 도메인 클래스 컨버터를 사용
	@GetMapping("/employee/v2/{id}")
	public String getEmployeeById2(@PathVariable("id") Employee employee){
		return employee.getUsername();
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
    
    em.flush();
    em.clear();
	}
}

```



# 4. Web 계층에서의 페이징 & 정렬

> 웹(Web) 계층인 컨트롤러 측에서의 데이터 전달방식 및 페이징에 관련된 세부 특화된 설정들을 다룬다.  
>
> 이전에 정리한 문서에서 Repository 계층에서 페이징을 적용할 때 반환값 및 인자값을 Pageable로 지정하여 쿼리를 하면 페이징이 적용된 결과값을 가져오는 것을 확인했었다. 웹 계층, 즉, 컨트롤러에서도 이러한 방식이 동일하게 적용될 수 있다.
>
> 웹(Web) 계층인 컨트롤러에서 반환타입을  Page\<E\> 를 지정해주게 되면 페이징이 적용된 결과를 클라이언트에게 전달해줄 수 있다. Page\<E\> 정보 내에 포함된 정보들은 아래와 같다.
>
> - content : {...}
>   - 본문데이터
> - pageable
>   - 페이징 관련 데이터
> - last, totalPages, totalElements, size, number, numberOfElements ...
>   - 부가적으로 위와 같은 필드들 또한 클라이언트 측에 전달해줄 수 있게 도와준다.
>   - Page\<E\> 를 반환타입으로 하는 메서드에서 제공되는 부가정보이다.
>
> Page\<E\> 는  org.springframework.data.domain 내에 포함된 클래스인데, 주로  Page\<Dto\> 와 같은 형식으로 리턴하는 것이 관례이다. (Entity 클래스를 직접 전달하지는 않는다). JPA 기반의 어떤 메서드에서 Page\<E\> 타입으로 데이터를 리턴하면 페이징이 적용된 데이터를 받아볼수 있다.

컨트롤러의 파라미터로 Pageable을 인자값으로 지정하면, 브라우저에서 Pageble의 하위 구현체인 PageRequest의 형식에 맞게끔 파라미터를 전달해주면, 컨트롤러에서 이 것을 해석해 페이지네이션을 적용하게 된다. 음... 조금 말을 정리할 필요가 있다. 너무 두서가 없다. 하지만 시간이 없으니...  

org.springframework.data.domain.Page 를 컨트롤러의 해당 메서드의 리턴타입으로 사용하게 되면 컨트롤러의 해당 메서드는 페이징이 적용된 결과를 json 으로 변환하여 브라우저에게 전달해주게 된다.


```java
@RestController
public class SampleController{
  @GetMapping("sample/index")
  public Page<EmployeeDto> getSampleIndex(){
    return empDataRepository.findAll();
  }
}
```

이렇게 작성된 Url Mapping 으로 브라우저에서 요청을 보내면 브라우저에서는 응답값에 아래와 같은 형식의 데이터를 받을 수 있다.

```json
{
    "content": [
        {
            "empNo": 1,
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        // ...
    ],
    "pageable": {
        "sort": {
        	// ...
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 5,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 20,
    "totalElements": 100,
    "size": 5,
    "number": 0,
    "numberOfElements": 5,
    "first": true,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "empty": false
}
```

본문 내용인 content, 페이징 관련 데이터인 pageable 외에도 

- last
- totalPages
- totalElements
- size
- number
- numberOfElements
- first

등의 파라미터를 제공해준다. 위의 필드들은 Pageable 을 웹 계층인 Controller에서 return 타입으로 제공할 때 응답값에 포함되는 값들이다. 

## Pageable 을 사용하게 되면 사용할 수 있는 파라미터들

자세한 내용은 공식 문서를 보는 것이 낫다. 하지만, 정말 급할 때 보고 참고할 수 있도록 몇가지의 파라미터 들을 정리해보려 한다.

- page
  - **0 부터 시작하는 것이 원칙이다.**
  - 몇 번째 페이지를 가져올 지를 지정한다.
  - 만약 page=9, size=7 을 지정했다면 쿼리는 limit 63, 7 이 된다. (63 번째 데이터에서부터 7개의 데이터를 들고온다)
- size
  - 몇개의 데이터를 들고 올 지를 지정한다.
- sort
  - asc, desc 를 지정가능하다.



## 예제) Controller

예제로 사용할 컨트롤러의 코드이다. Service 계층을 두어 별도의 가공 및 처리를 두는 계층을 두는 것이 코드의 응집도를 낮추는 좋은 방법이다. 하지만, 예제의 단순함이 복습에 더 유리하다고 판단되어 Service 계층없이  Controller에서 모든 가공 및 처리를 모두 수행하도록 했다.

### EmpController.java

```java
@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}

	// ...
  
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

    // Dto 로 반환 
		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}
```



## 예제 1) 아무인자값 없이 요청해보기

> Spring Data JPA는 default page size 를 20으로 지정하고 있기 때문에, 아무 인자값 없이 Pageable 한 리퀘스트를 받으면, Spring Data JPA는 자동으로 page size를 20으로 지정한 가장 첫번째 결과를 돌려준다.  

  

아무 인자값 없이 [http://localhost:8080/employees](http://localhost:8080/employees) 로 요청을 보내면, 해당 URL 에 매핑된 컨트롤러의 파라미터로 Pageable 이 지정되어 있다면 스프링 부트는 자동으로 Pageable 의 하위구현체인 PageRequest 객체를 생성해낸다. 위의 컨트롤러의 list 에서는 Pageable 을 인자로 받을수 있는 Spring Data JPA 메서드를 사용하고 있다.

> empDataRepository.findAll(pageable);

즉, 위와 같은 쿼리 메서드를 컨트롤러에서 호출하고 있기 때문에 페이징이 적용된 결과를 클라이언트에게 돌려준다. 

### URL

postman 또는 insomnia 와 같은 rest client 에서 아래와 같은 요청을 보내보자.

> [http://localhost:8080/employees](http://localhost:8080/employees)

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #2",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #3",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #4",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #5",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #6",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #7",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #8",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #9",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #11",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #12",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #13",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #14",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #15",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #16",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #17",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #18",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #19",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "pageNumber": 0,
        "pageSize": 20,
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 200,
    "totalPages": 10,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 20,
    "size": 20,
    "number": 0,
    "first": true,
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
limit 20;
```

처음 데이터에서부터 20개의 데이터를 가져왔다.



## 예제 2) page 번호 지정해보기

### URL

postman 또는 insomnia 와 같은 rest client 에서 아래와 같은 요청을 보내보자.

> [http://localhost:8080/employees?page=9&size=7](http://localhost:8080/employees?page=9&size=7)

이 요청에 대한 결과는 아래와 같다.

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #63",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #64",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #65",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #66",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #67",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #68",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #69",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "pageNumber": 9,
        "pageSize": 7,
        "offset": 63,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 200,
    "totalPages": 29,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 7,
    "size": 7,
    "number": 9,
    "first": false,
    "empty": false
}
```



### SQL

생성된 쿼리는 아래와 같다.

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
limit 63, 7;
```

> SQL의 의미는 '63번째 데이터에서부터 7개의 데이터를 가져왕~' 이다.



## 예제 3) sorting 사용해보기

> sorting 사용시 [url]?sort=[필드명],[ASC/DESC]&sort=[필드명],[ASC/DESC]&... 과 같은 형식으로 요청을 보내면 된다.  



### URL

> [http://localhost:8080/employees?page=0&size=5&sort=salary,desc&sort=username,asc](http://localhost:8080/employees?page=0&size=5&sort=salary,desc&sort=username,asc)

파라미터 값으로 

- page = 0
- size = 5
- sort = salary, desc
- sort = username, asc

를 지정해주었다.

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "pageNumber": 0,
        "pageSize": 5,
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 200,
    "totalPages": 40,
    "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
    },
    "numberOfElements": 5,
    "size": 5,
    "number": 0,
    "first": true,
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
order by 
	employee0_.salary desc, 
	employee0_.username asc 
limit 5;
```



## 예제 4) 디폴트 페이징 size 를 변경하기 (default-page-size)

> 스프링 부트에서 spring data jpa 를 의존성으로 추가해주었다면, 기본으로 지정된 페이징 사이즈는 디폴트 값이 20이다. 이렇게 지정된 디폴트 페이징 size를 커스터마이징할 필요가 있을 때가 있다. 

별다른 설정 없이 스프링 부트의 페이징 기능을 사용하고 있다면, 프로젝트 내에 기본으로 적용된 페이징 size는 20이다. 즉, 클라이언트에서 아무런 인자값 없이 요청이 왔다면, 디폴트 페이징 size가 20 인채로 요청이 수행되게 된다.  

기본으로 지정된 페이징 size를 바꾸려 한다면 application.properties 또는 application.yml 에서  

- spring.data.web.pageable.default-page-size

에 값을 지정해주면 된다.  

### application.yml

디플트 페이징 사이즈를 변경해보자

```yaml
spring:
  profiles:
    active: testdocker
---
spring:
  profiles: testdocker

	# spring.data.web.pageable.default-page-size 를 10으로 지정해주었다.
  data:
    web:
      pageable:
        default-page-size: 10

  jpa:
    hibernate:
      ddl-auto: update
      use-new-id-generator-mappings: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL5InnoDBDialect
        
  datasource:
    url: jdbc:mariadb://localhost:23306/ec2_web_stockdata
    username: testuser
    password: 1111
    hikari:
      auto-commit: true
    driver-class-name: org.mariadb.jdbc.Driver

logging.level:
  org.hibernate.SQL: debug
```



### URL

> [http://localhost:8080/employees](http://localhost:8080/employees)

  

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #2",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #3",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #4",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #5",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #6",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #7",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #8",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #9",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 10,
    "last": false,
    "totalElements": 100,
    "size": 10,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
limit 10;
```



## 예제 5) 최대 한계 페이징 size 변경하기 (max-page-size)

> 간혹 클라이언트 단에서 이상한 요청을 보낼때가 있다. 악의적인 공격일 수도 있고, 프로그램상의 오류일 수도 있다. 그런데, 그런 요청중에 DB의 데이터에서 10만개를 페이징 size로 지정하고 2020 번째 페이징을 요청하게 되는 경우가 생길 수 있다. 트래픽이 많은 서비스라면 생각만 해도 아찔하긴 하다.

스프링 부트내의 설정에서 spring data web 에서 제공하는 파라미터가 있다. 

- spring.data.web.pageable.max-page-size

를 원하는 값으로 지정해주면 지정해준 값 만큼만 클라이언트에서 온 요청의 페이징 사이즈가 max-page-size를 넘어서면 자동으로 max-page-size 만큼만 return 해주게 된다.  

예제는 나중에 추가하자. (default-page-size = 3, max-page-size = 5 로 지정해서 예제 돌려보고 정리합시다~)



## 예제 6) URL 매핑 하나에 대해서만 특화된 설정을 하려 할 때

> 트래픽이 많은 서비스의 경우 간혹 정말 많은 트래픽이 집중되는 페이지나, URL이 있을 수 있다. 이런 경우 application.yml 에 설정해둔 설정을 오버라이딩 해서 해당 요청에 대해서만 특화된 설정이 적용되도록 할수도 있다. 이때 사용하는 것이 @PageableDefault 어노테이션이다.  



### EmpController.java

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
// ... 중략 ... 

@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}
  
	// ...
  
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

    // Dto 로 변환 
		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}

```



### URL

> [http://localhost:8080/employees](http://localhost:8080/employees)

  

### 응답결과

아무런 설정을 하지 않고, Controller의 URL Mapping 에서만 @PageableDefault 를 적용한 결과 5개의 데이터만 들고온 것을 확인 가능하다. username으로 sorting 되어있는 것 또한 확인 가능하다.

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #11",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #12",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 5,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 20,
    "last": false,
    "totalElements": 100,
    "size": 5,
    "number": 0,
    "first": true,
    "numberOfElements": 5,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "empty": false
}
```



### SQL

아무런 설정을 하지 않고, Controller의 URL Mapping 에서만 @PageableDefault 를 적용한 결과 5개의 데이터만 들고온 것을 확인 가능하다. username으로 sorting 되어있는 것 또한 확인 가능하다.

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
order by employee0_.username asc 
limit 5;
```



## 예제 7) 접두사 (prefix)

컨트롤러에 요청을 보낼때 요청을 받은 컨트롤러 입장에서 두가지의 DB 테이블에서 데이터를 인출해와야 하는 경우가 있다. 그리고, 두 테이블은 성능이슈로 인해 각 테이블에 대해 서로 다른 페이징 방식으로 데이터를 인출해와야 한다. 이런 경우에 대해 사용하는 방식이다.

```java
	@GetMapping("/employees")
	@ResponseBody
	public Page<GroupDto> list(
    		@Qualifier("employee") Pageable empPageable,
    		@Qualifier("department") Pageable deptPageable
  ){
		Page<Employee> all = empDataRepository.findAll(empPageable);

    // Dto 로 변환 
		Page<EmployeeDto> map1 = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});
    
    Page<DepartmentDto> deptDataRepository.findAll(deptPageable);

    // Dto 로 변환 
		Page<DepartmentDto> map2 = all.map(d -> {
			final String localName = d.getLocalName();
      final String deptName = d.getDeptName();
			return new DepartmentDto(username, salary);
		});

    Page<GroupDto> result = groupService.processEmpAndDept(map1, map2);
    
		return result;
	}
```

자세한 예제는 생략~~!! 절대 귀찮아서는 아니다 ㅋㅋ ㅠㅜ  



## 예제 8) Dto로의 변환 을 조금 더 편하게!!

> repository에서 데이터를 가져올 때 Dto의 생성자로 반환하는 편의메서드를 작성하는 방법을 알아보자.

Dto 측에서 Entity를 바라보는 것은 크게 문제가 되지 않는다. Dto의 생성자를 통해 Entity의 정보를 가져오도록 편의성을 제공할 수 있다.  단, Entity 인스턴스를 필드로 가지고 있지 않아야 하고, Entity를 접근하는  getter/setter가 없어야 한다.

보통 아래와 같이 매핑을 한다.

```java
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}
```

그런데 이 방식 외에도 Dto의 생성자를 커스터마이징하면 더 간편하게 편의성을 제공할 수 있다.

```java
	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			return new EmployeeDto(e);
		});

		return map;
	}
```

코드가 더 간결해졌다.  

더 간결하게 해보자

```java
	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);
    
		Page<EmployeeDto> map = all.map(e->new EmployeeDto(e));

		return map;
	}
```

이번엔 메서드 레퍼런스를 사용해보자.

```java
	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(EmployeeDto::new);

		return map;
	}
```

더 간단해졌다. 어떤 방식을 사용하던 개인적인 취향이지만 개인적으로 나는 첫 번째 방식을 선호한다. 명확하게 의미를 표시하는 느낌이어서 ... 



### EmpController.java

```java
// ... 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

// ...

@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}
  
  // ...

	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			return new EmployeeDto(e);
		});

		return map;
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}
```



### URL

> [http://localhost:8080/v2/employees](http://localhost:8080/v2/employees)

  

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #11",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #12",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 5,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 20,
    "totalElements": 100,
    "size": 5,
    "number": 0,
    "numberOfElements": 5,
    "first": true,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
order by employee0_.username asc 
limit 5;
```



## 별첨) 커스터마이징

> 페이징을 적용할 때 page의 기본 시작 index가 0 이다. page 를 1 부터 시작하도록 하려면 두가지의 해결책이 있다.

- Page, PageRequest, Page 등을 직접 커스터마이징하고, 이것들을 반환하도록 하는 방식이다. 
  - 가장 에러없이 잘 동작하는 해결책이다.
  - 프로젝트 진행시 규칙을 잘 정해놓고 한다면 가능한 방식이다.
- spring.data.web.pageable.one-indexed-parameters 를  true로 설정하는 방식
  - spring.data.web 계층에서 page를 단순 증감(-1) 처리하는 방식이다.
  - 한계가 있다. (pageable 및 나머지 필드들에는 적용이 안된다.)

