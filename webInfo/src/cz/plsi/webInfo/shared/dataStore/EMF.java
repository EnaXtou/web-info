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
			em.persist(entity);
			em.close();
			return true;
		} else {
			em.close();
			return false;
		}
	}
	
}
