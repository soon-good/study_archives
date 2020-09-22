package io.study.tdd.tddforall.grade;

import io.study.tdd.tddforall.grade.entity.Score;
import java.util.List;

public interface GradeRepository {
	List<Score> findAllScore();
}
