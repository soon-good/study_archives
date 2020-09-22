package io.study.tdd.tddforall.grade;

import io.study.tdd.tddforall.calculator.CalculatorDto;
import io.study.tdd.tddforall.calculator.FxType;
import io.study.tdd.tddforall.grade.entity.Score;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GradeServiceImpl implements GradeService{

	private final GradeRepository gradeRepository;

	public GradeServiceImpl(GradeRepository gradeRepository){
		this.gradeRepository = gradeRepository;
	}

	/**
	 * 전 사원의 점수 평균 구하기
	 * @return CalculatorDto
	 */
	public CalculatorDto getAverageScoreForAllEmployees(){
		List<Score> allScore = gradeRepository.findAllScore();

		double sum = allScore.stream()
			.mapToDouble(Score::getScore)
			.sum();

		int numOfEmployees = allScore.size();

		return CalculatorDto.builder()
			.fxType(FxType.AVG)
			.value(sum/numOfEmployees)
			.build();
	}

	/**
	 * 전 사원 평균 점수의 등급 구하기
	 * @return GradeLevel
	 */
	@Override
	public GradeLevel getGradeLevelForAllEmployees() {
		CalculatorDto avg = getAverageScoreForAllEmployees();
		Double score = avg.getValue();
		return GradeLevel.gradeLevel(score);
	}

}
