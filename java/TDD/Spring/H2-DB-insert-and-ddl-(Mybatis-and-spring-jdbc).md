# Spring-JDBC 를 이용한 가상 DB insert 로직 추가 (Mybatis)

# 1. 스키마 생성/INSERT 스크립트 추가
## 스키마 생성/INSERT 하는 두가지 방법들 요약 (자동설정/커스텀설정)

먼저, 테스트가 시작되기 전에 스키마를 생성하고, INSERT 하는 SQL 스크립트 들을 실행해야 한다.

보통 src/main/resources 밑에 schema.xml 파일을 두면 자동으로 파악하여 insert 할 수 있는데, 항상 그렇듯 커스텀한 설정 역시 필요하다. 기본 설정은 스키마 생성과 데이터 INSERT 를 하나의 파일에서 수행하는 방식이다. 스키마를 통으로 하나의 파일에 생성하면 여러 개발자들이 동시에 수정하므로 컨텐츠마다 기획이 달라질 경우 테스트하려는 데이터의 insert 구문이 꼬일 가능성이 크다.

여기서는 커스텀 설정을 다룬다. 기본 설정방식에 대해서는 https://howtodoinjava.com/spring-boot2/h2-database-example/ 을 참고하면 된다.

## 참고 - 자동설정 (스프링부트)
참고) 스프링 부트에서의 자동적인 설정
스프링 부트에서는 단순히 src/test/resources 아래에 
* schema.sql
* data.sql
을 두면 자동으로 데이터를 생성한다.

## 커스텀 설정
### 참고할 만한 자료
[baeldung.com](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)

스프링 부트 뿐만 아니라 스프링에서도 사용가능한 설정이다. 또한 schema를 테스트 케이스마다 다르게 생성했다가 지우고 싶을 때가 있다. 이러한 경우에 대한 설정이다.

여러가지 방법을 사용할 수 있다. 가능한 방법들은 아래와 같다.
1. jpa 속성값 설정
- application.properties 또는 yml 내의 spring.jpa.hibernate.ddl-auto 속성에 대한 값을 정의하는 것으로도 해결이 가능하다. (create, update, create-drop, validate, none)
2. jpa 속성 값을 none으로 두고 @Sql 어노테이션 활용
3. @Sql
4. @SqlConfig
5. @SqlGroup

### 예제
여기서는 스프링 부트가 아닌 스프링에서도 통하는 방식을 다룰 것이기 때문에 @Sql, @SqlConfig 방식을 다룬다.
회사 일로 설정을 추가해서… 이름만 살짝 고쳐서 예제를 들어본다.
#### StudyImplMockTestConfig.java
```java
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StudyMockTest extends StudyImplMockTestConfig {

    @Resource(name = "mpocSqlMapClientTemplate")
    private MelonSqlMapClientTemplate mpocSqlMapClientTemplate;

    @Resource(name = "mpocDataSource")
    private DataSource dataSource;

    @InjectMocks
    private StudyImpl mock;

    // …
    @BeforeEach
    void setup() throws Exception{
        Whitebox.setInternalState(mock, "mpocSqlMapClientTemplate", mpocSqlMapClientTemplate);
        Whitebox.setInternalState(mpocSqlMapClientTemplate, "dataSource", dataSource);
        
        queryIdPrefix = mock.getClass().getName();
    }

    // …

    @DisplayName("listProgramSong > bpId 가 정상적인 파라미터일 경우")
    @Sql(scripts = {
       "/datasets/study/ddl/schema.sql",
       "/datasets/study/insert/insert_members.sql",
       "/datasets/study/insert/insert_study_team.sql"
    }, config = @SqlConfig(dataSource = "mpocDataSource"))
    @Test
    void list_program_song_valid_parameters(){
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("memberId", 834);
        
        String queryId = queryIdPrefix + "." + "listStudy";
        
        List<EntityMap> result = mock.listStudy(parameter);
        
        assertThat(result.size()).isEqualTo(1);
        
        result.stream().forEach(entityMap -> {
            logger.info(entityMap.toString());
        });
    }
}
```



위의 코드를 작성하면서 혼동되었던 부분만을 주석으로 따로 설명을 추가해봤다.

- @Mock 이 아닌 @InjectMocks를 사용해야 한다.
- 이 객체에 필요한 실제 객체들을 로컬 멤버 필드에 의존성 주입으로 가져온다.

```java
	// ...

	@Resource(name = "mpocSqlMapClientTemplate")
	private MelonSqlMapClientTemplate mpocSqlMapTemplate;

	@Resource(name = "mpocDataSource")
	private DataSource dataSource;

	// 아래 부분이 중요했다.
	// @Mock 이 아닌 @InjectMocks 를 사용해야 했다.
	@InjectMocks
	private StudyImpl mock;

	// ...
```



그리고 이 실제 객체들을 mock객체 내의 멤버필드에 연결해주어야 한다. 예제에서는 테스트가 시작되기 전에 항상 주입하도록 했다.

```java
	@BeforeEach
	void setup() throws Exception{
    	WhiteBox.setInternalState(mock, "mpocSqlMapClientTemplate", mpocSqlMapClientTemplate);
    	WhiteBox.setInternalState(mock, "dataSource", dataSource);
    	// ...
  }
```



# 2. Mock, Datasource 설정

## StudyImplMockTestConfig.java
Test 에 관련된 애노테이션과 각종 설정들을 모아놓은 XXXTestConfig 클래스이다.
```java
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StudyH2Configuration.class}, loader = MelonAnnotationConfigContextLoader.class)
@PowerMockIgnore({ "javax.net.ssl.*", "javax.security.*" }) // 3.
@ActiveProfiles("dev")
public class BroadsongImplMockTestConfig {

   @Autowired
   ApplicationContext applicationContext;

   @Rule
   public PowerMockRule rule = new PowerMockRule();

   @BeforeEach
   public void junit5Setup(){
      MockitoAnnotations.initMocks(this);
   }
}
```



## StudyH2Configuration.java

H2 설정을 하는 동시에 WebApplicationContext 관련 설정 역시 모두 수행하도록 하는 클래스이다. 분리해서 세분화할 수도 있지만, 아직은 그럴 필요성을 못느껴서 H2Configuration에서 WabApplicationContext 관련 설정도 모두 설정하도록 구성했다.

```java
@Configuration
@Import({ 설정1.class, 설정2.class, 설정3.class, … 설정n.class})
public class StudyH2Configuration{
    @Autowired
    ApplicationContext applicationContext;

    @Bean(name = "mpocDataSource", destroyMethod = "close")
    public DataSource dataSource() throws IOException{
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDefaultAutoCommit(false);
        dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:h2:~/melon-h2");
        return dataSource;
    }

    @Bean
    public FactoryBean<SqlMapClient> sqlMapClient() throws IOException {
        SqlMapClientFactoryBean sqlMapClientFactoryBean = new SqlMapClientFactoryBean();
        Resource[] configLocations = applicationContext
                .getResources("classpath:META-INF/study/ibatis/sql-map-config.xml");
        sqlMapClientFactoryBean.setConfigLocations(configLocations);
        sqlMapClientFactoryBean.setDataSource(dataSource());
    
        sqlMapClientFactoryBean.setMappingLocations(applicationContext.getResources("classpath*:com/study/**/sql/*SqlMap.xml"));
        sqlMapClientFactoryBean.setLobHandler(new DefaultLobHandler());
        return sqlMapClientFactoryBean;
    }

    @Bean
    public PlatformTransactionManager txManager() throws IOException{
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }


    @Bean
    public MelonSqlMapClientTemplate sqlMapClientTemplate() throws Exception{
        MelonSqlMapClientTemplate sqlMapClientTemplate = new MelonSqlMapClientTemplate();
        sqlMapClientTemplate.setDataSource(dataSource());
        sqlMapClientTemplate.setSqlMapClient(sqlMapClient().getObject());
        return sqlMapClientTemplate;
    }
}
```


