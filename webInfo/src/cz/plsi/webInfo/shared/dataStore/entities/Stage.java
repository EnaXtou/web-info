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

/**
 * @author koty
 *
 */
@Entity
public class Stage implements EntityCommon {
	
	@Id
	private String name;
	
	private int number;
	
	private String help1;
	
	private String help2;
	
	private String result;
	
		
	public Stage(String name) {
		super();
		this.setName(name);
	}

	public Stage(String name, int number, String help1, String help2, String result) {
		super();
		this.name = name;
		this.setNumber(number);
		this.help1 = help1;
		this.help2 = help2;
		this.result = result;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getHelp1() {
		return help1;
	}

	public void setHelp1(String help1) {
		this.help1 = help1;
	}

	public String getHelp2() {
		return help2;
	}

	public void setHelp2(String help2) {
		this.help2 = help2;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(s) FROM "+ Stage.class.getName() +" s", Long.class);
		return query.getSingleResult();
	}

	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT t FROM "+ this.getClass().getName() +" t "
			      		+ "where t.name=:name"
			      + "");
		query.setParameter("name", this.getName());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
		
	}
	
	public boolean existsNumber() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
				"SELECT t FROM "+ this.getClass().getName() +" t "
						+ "where t.number=:number"
						+ "");
		query.setParameter("number", this.getNumber());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
		
	}

	@Override
	public List<Stage> getList() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Stage> cq =  criteriaBuilder.createQuery(Stage.class);
		Root<Stage> stage = cq.from(Stage.class);
		
		if (this.name != null) {
			cq.where(criteriaBuilder.equal(stage.get("name"), this.name));
		}
		
		cq.select(stage);
		cq.orderBy(criteriaBuilder.desc(stage.get("number")));
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public String toString() {
		return "Stage [name=" + name + ", number=" + number + ", help1="
				+ help1 + ", help2=" + help2 + ", result=" + result + "]";
	}

	@Override
	public Object getId() {
		return getName();
	}
	

}
