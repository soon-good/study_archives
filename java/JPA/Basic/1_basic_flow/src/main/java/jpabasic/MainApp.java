package jpabasic;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import jpabasic.entity.Employee;

public class MainApp {
	public static void main(String [] args){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_basic");
		EntityManager em = emf.createEntityManager();

		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		try{
			// @Id가 100L 인 데이터를 가져와 보자.
			System.out.println("======= em.find (Employee.class, 100L) =======");
			Employee emp100 = em.find(Employee.class, 100L);

			// emp100의 이름을 변경해보자
			System.out.println("======= emp100.setName... 엔티티 내부 값 변경 =======");
			emp100.setName("소방관#100");

			// 현재 영속성 컨텍스트인 em 을 비워보자.
			System.out.println("======= em.clear() =======");
			em.clear();

			System.out.println("======= transaction.commit() =======");
			transaction.commit();
		}
		catch (Exception e){
			transaction.rollback();
		}
		finally{
			em.close();
		}
		emf.close();
	}
}