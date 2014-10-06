package cz.plsi.webInfo.shared.dataStore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cz.plsi.webInfo.shared.dataStore.entities.EntityCommon;

public class EMF {
	
	private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");
	
	private EMF() {
	}

	public static EntityManagerFactory getInstance() {
        return emfInstance;
    }
	
	public static boolean add(EntityCommon entity) {
		EntityManager em = EMF.getInstance().createEntityManager();
		if (!entity.exists()) {
			em.getTransaction().begin();
			em.persist(entity);
			em.getTransaction().commit();
			em.close();
			
			return true;
		} else {
			em.close();
			return false;
		}
	}
	
	public static boolean update(EntityCommon entity) {
		EntityManager em = EMF.getInstance().createEntityManager();
		if (entity.exists()) {
			em.getTransaction().begin();
			em.merge(entity);
			em.getTransaction().commit();
			em.close();
			
			return true;
		} else {
			em.close();
			return false;
		}
	}
	
	public static EntityCommon find(EntityCommon entity) {
		EntityManager em = EMF.getInstance().createEntityManager();
		EntityCommon entityToReturn = em.find(entity.getClass(), entity.getId());
		em.close();
		return entityToReturn;
	}
	
}
