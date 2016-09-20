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
public class MessageToTeams implements EntityCommon {
	
	@Id
	private String message;
	
	private int fromStageNumber;
	private int toStageNumber;
	private String branch;

	public MessageToTeams() {
		super();
	}


	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(h) FROM "+ MessageToTeams.class.getName() +" h", Long.class);
		return query.getSingleResult();
	}

	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT h FROM "+ this.getClass().getName() +" h "
			      		+ "where h.message=:message ");
		query.setParameter("message", this.getMessage());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}

	@Override
	public List<MessageToTeams> getList() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<MessageToTeams> cq =  criteriaBuilder.createQuery(MessageToTeams.class);
		Root<MessageToTeams> message = cq.from(MessageToTeams.class);
		
		if (this.getMessage() != null) {
			cq.where(criteriaBuilder.equal(message.get("message"), this.getMessage()));
		}
		cq.select(message);
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public Object getId() {
		return getMessage();
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public int getFromStageNumber() {
		return fromStageNumber;
	}


	public void setFromStageNumber(int fromStageNumber) {
		this.fromStageNumber = fromStageNumber;
	}


	public int getToStageNumber() {
		return toStageNumber;
	}


	public void setToStageNumber(int toStageNumber) {
		this.toStageNumber = toStageNumber;
	}


	public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}

}
