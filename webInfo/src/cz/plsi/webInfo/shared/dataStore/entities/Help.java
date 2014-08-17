package cz.plsi.webInfo.shared.dataStore.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import cz.plsi.webInfo.shared.dataStore.EMF;

@Entity
public class Help implements EntityCommon {
	
	@Id
	private String name;
	
	public Help(String name) {
		this.setName(name);
	}

	public Help() {
		super();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(h) FROM "+ Help.class.getName() +" h", Long.class);
		return query.getSingleResult();
	}

	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT h FROM "+ this.getClass().getName() +" h "
			      		+ "where h.name=:name ");
		query.setParameter("name", this.getName());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}

	@Override
	public List<Help> getList() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Help> cq =  criteriaBuilder.createQuery(Help.class);
		Root<Help> help = cq.from(Help.class);
		
		if (this.getName() != null) {
			cq.where(criteriaBuilder.equal(help.get("name"), this.getName()));
		}
		cq.select(help);
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public String toString() {
		return "Help [name=" + name + "]";
	}

}
