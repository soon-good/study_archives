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
			Employee emp_jordan = em.find(Employee.class, 1L);
			emp_jordan.setName("AIR JORDAN");

//			em.persist(emp_jordan);

			System.out.println("before transaction.commit ======= ");
			transaction.commit();
			System.out.println("after transaction.commit ======= ");
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


