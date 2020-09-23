package io.study.tdd.tddforall.mockito;

import io.study.tdd.tddforall.calculator.Calculator;
import io.study.tdd.tddforall.grade.GradeLevel;
import io.study.tdd.tddforall.grade.GradeRepository;
import io.study.tdd.tddforall.grade.GradeRepositoryImpl;
import io.study.tdd.tddforall.grade.GradeService;
import io.study.tdd.tddforall.grade.GradeServiceImpl;
import io.study.tdd.tddforall.grade.entity.Score;
import io.study.tdd.tddforall.util.timezone.CountryCode;
import io.study.tdd.tddforall.util.timezone.LocaleProcessor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MockitoStep1Test {

	@Mock
	private LocaleProcessor localeProcessor;

	@InjectMocks
	private Calculator calculator;

	@Autowired
	EntityManager entityManager;

	@Test
	@DisplayName("#0 Mocking 쌩기초")
	void testObjectMockingBasic(){
		List mockedList = Mockito.mock(List.class);

		Mockito.when(mockedList.get(0))
			.thenReturn("sample1")
			.thenReturn("sample2");

		System.out.println(mockedList.get(0));
		Assertions.assertThat(mockedList.get(0)).isEqualTo("sample2");
	}

	/**
	 * 1) 테스트 클래스 내의 멤버 필드를 @Mock, @InjectMocks 로 Mocking 한다.
	 * 2) Calculator 내의 LocaleProcessor를 Calculator 외부인  add 함수를 호출할 때 환영 인사를 출력하는데, 이
	 */
	@Test
	@DisplayName("#0 객체 Mocking (1) >>> 테스트 클래스의 멤버필드에 @Mock, @InjectMocks로 필드 인젝션")
	void testObjectMocking1(){
		Mockito.when(localeProcessor.getServerCountryCode())
			.thenReturn(CountryCode.JAPAN);

		int result = calculator.add(1, 2);
		Assertions.assertThat(result).isEqualTo(1+2);
	}

	/**
	 * 이 경우 Calculator 클래스는 실제 객체이다.
	 * 생성자를 활용한 Mocking 코드가 잘 되어 있다면 임시 테스트용으로 간단히 빠르게 테스트를 수행해볼 수 있다.
	 * */
	@Test
	@DisplayName("#0 객체 Mocking (2) >>> 직접 Mocking 하기")
	void testObjectMocking2(){
		LocaleProcessor mockedLoc = Mockito.mock(LocaleProcessor.class);
		Calculator cal = new Calculator(mockedLoc);

		Mockito.when(mockedLoc.getServerCountryCode())
			.thenReturn(CountryCode.CHINA);

		int result = cal.add(1, 2);
		Assertions.assertThat(result).isEqualTo(1+2);
	}

	/**
	 * 이 경우 Calculator 클래스는 실제 객체이다.
	 * 생성자를 활용한 Mocking 코드가 잘 되어 있다면 임시 테스트용으로 간단히 빠르게 테스트를 수행해볼 수 있다.
	 * */
	@Test
	@DisplayName("#0 객체 Mocking (3) >>> 파라미터를 Mocking")
	void testObjectMocking3(@Mock LocaleProcessor mockedLoc){
		Calculator calculator = new Calculator(mockedLoc);

		Mockito.when(mockedLoc.getServerCountryCode())
			.thenReturn(CountryCode.KOREA);

		int result = calculator.add(1, 2);
		Assertions.assertThat(result).isEqualTo(1+2);
	}

	@Test
	@DisplayName("#0 stubbing > when~then 절을 활용한 stubbing 쌩 기초")
	void testStubbingVeryBasic(){
		List l = Mockito.mock(List.class);
		l.add("aaaaa");

		Mockito.when(l.get(0))
			.thenReturn("hello~!!");

		Object o = l.get(0);
		System.out.println(o);
	}

	/**
	 * 행위 검증 : verify
	 */
	@Test
	@DisplayName("#1 mockito > verify 를 이용한 행위 검증 쌩 기초")
	void testVerifyList(){
		List mockedList = Mockito.mock(List.class);
		mockedList.add("one");

		Mockito.verify(mockedList).add("one");

		Map mockedMap = Mockito.mock(Map.class);
		mockedMap.put("test", 1);

		Mockito.verify(mockedMap).put("test",1);
	}

	@Test
	@DisplayName("#1 stubbing 중급 ")
	void testVerifyCalculator(){
		Score score1 = Score.builder()
			.score(100D)
			.subject("국어")
			.build();

		Score score2 = Score.builder()
			.score(99D)
			.subject("국어")
			.build();

		List<Score> scores = Arrays.asList(score1, score2);

		// given ~ when
		GradeRepository gradeRepository = Mockito.mock(GradeRepositoryImpl.class);
		Mockito.when(gradeRepository.findAllScore())
			.thenReturn(scores);

		GradeService gradeService = new GradeServiceImpl(gradeRepository);
		// gradeService.getGradeLevelForAllEmployees() 에서는 내부적으로 gradeRepository.findAllScore() 을 호출한다.
		// 내부적으로 gradeRepository.findAllScore() 을 호출할 때 scores 를 return 하도록 해서
		// 		기대 결과 값에 맞는 GradeLevel 이 나오는 지를 측정한다.
		GradeLevel gradeLevel = gradeService.getGradeLevelForAllEmployees();

		Assertions.assertThat(gradeLevel).isEqualTo(GradeLevel.A);
	}

	@Test
	@DisplayName("#2 stubbing > Calcultor 를 stubbing 하기")
	void testStubbingCalculator(){

	}
}
