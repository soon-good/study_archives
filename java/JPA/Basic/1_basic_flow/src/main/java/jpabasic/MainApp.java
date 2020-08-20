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
			System.out.println("======= em.find(Employee.class, 1L) =======");
			Employee emp1 = em.find(Employee.class, 1L);

			System.out.println("======= em.remove(\"소방관\") =======");
			em.remove(emp1);

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