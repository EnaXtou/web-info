package cz.plsi.webInfo.shared.dataStore.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import cz.plsi.webInfo.shared.dataStore.EMF;

@Entity
public class Team implements EntityCommon {
	
	@Id
	private String name;
	
	private String code;
	
	public Team(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.code = Team.getCode(name);
	}

	public static String getCode(String name) {
		return name + "_code";
	}

	public String getCode() {
		throw new RuntimeException("Not implemented yet.");
	}

	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(t) FROM "+ Team.class.getName() +" t", Long.class);
		return query.getSingleResult();
	}

	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT t FROM "+ this.getClass().getName() +" t "
			      		+ "where t.name=:name ");
		query.setParameter("name", this.getName());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}

	@Override
	public List<Team> getAll() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Team> query = em.createQuery(
			      "SELECT t FROM "+ this.getClass().getName() +" t", Team.class);
		return query.getResultList();
	}
	
	@Override
	public String toString() {
		return "Team [name=" + name + ", code=" + code + "]";
	}


}
