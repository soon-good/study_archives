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

	//
	public CalculatorDto getAverageScoreForAllEmployees(){
		List<Score> allScore = gradeRepository.findAllScore();

		double sum = allScore.stream()
			.mapToDouble(s -> s.getScore())
			.sum();

		int numOfEmployees = allScore.size();

		return CalculatorDto.builder()
			.fxType(FxType.AVG)
			.value(sum/numOfEmployees)
			.build();
	}

}
