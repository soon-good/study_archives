package com.study.qdsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class MemberJpaJpqlRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaJpqlRepository(EntityManager em) {
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
	}

	public void save(Member member){
		em.persist(member);
	}

	public Optional<Member> findById(Long id){
		Member foundMember = em.find(Member.class, id);
		return Optional.ofNullable(foundMember);
	}

	public List<Member> findAll(){
		return em.createQuery("select m from Member m", Member.class).getResultList();
	}

	public List<Member> findByUsername(String username){
		return em.createQuery("select m from Member m where m.username = :username", Member.class)
			.setParameter("username", username)
			.getResultList();
	}
}
