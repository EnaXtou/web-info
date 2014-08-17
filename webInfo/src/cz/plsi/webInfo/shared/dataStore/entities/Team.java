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

import org.apache.commons.lang3.builder.EqualsBuilder;

import cz.plsi.webInfo.shared.dataStore.EMF;

@Entity
public class Team implements EntityCommon {
	
	@Id
	private String name;
	
	private String code;
	
	public Team(String name) {
		this.setName(name);
	}

	public Team() {
		super();
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
		return this.code;
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
	public List<Team> getList() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Team> cq =  criteriaBuilder.createQuery(Team.class);
		Root<Team> team = cq.from(Team.class);
		if (this.getCode() != null) {
			cq.where(criteriaBuilder.equal(team.get("code"), this.getCode()));
		}
		if (this.getName() != null) {
			cq.where(criteriaBuilder.equal(team.get("name"), this.getName()));
		}
		cq.select(team);
		
		return em.createQuery(cq).getResultList();
	}
	
	@Override
	public String toString() {
		return "Team [name=" + name + ", code=" + code + "]";
	}
	

	@Override
	public boolean equals(Object anObject) {
		return EqualsBuilder.reflectionEquals(this, anObject);
	}

	public void setCode(String teamCode) {
		this.code = teamCode;
	}

	
	


}
