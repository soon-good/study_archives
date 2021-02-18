package io.study.tdd.tddforall.grade;

import io.study.tdd.tddforall.grade.entity.Score;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class GradeRepositoryImpl implements GradeRepository{

	private final EntityManager entityManager;

	public GradeRepositoryImpl(EntityManager entityManager){
		this.entityManager = entityManager;
	}

	@Override
	public List<Score> findAllScore() {
		return entityManager.createQuery("SELECT s FROM Score s").getResultList();
	}

}
