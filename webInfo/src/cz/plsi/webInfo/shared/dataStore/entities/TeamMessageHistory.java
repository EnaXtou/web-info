package cz.plsi.webInfo.shared.dataStore.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.appengine.api.datastore.Key;

import cz.plsi.webInfo.shared.dataStore.EMF;

@Entity
public class TeamMessageHistory implements EntityCommon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private Date messageDate;
	
	private String teamCode;
	
	private String message;
	
	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	public String getTeamCode() {
		return teamCode;
	}

	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setId(Key id) {
		this.id = id;
	}

	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(h) FROM "+ this.getClass().getName() +" h", Long.class);
		return query.getSingleResult();
	}

	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT h FROM "+ this.getClass().getName() +" h "
			      		+ "where h.message=:message "
			      		+ "and h.teamCode=:teamCode");
		query.setParameter("message", this.getMessage());
		query.setParameter("teamCode", this.getTeamCode());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}

	@Override
	public List<? extends EntityCommon> getList() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<TeamMessageHistory> cq =  criteriaBuilder.createQuery(TeamMessageHistory.class);
		Root<TeamMessageHistory> message = cq.from(TeamMessageHistory.class);
		
		if (this.getMessage() != null) {
			cq.where(criteriaBuilder.equal(message.get("message"), this.getMessage()));
		}
		if (this.getTeamCode() != null) {
			cq.where(criteriaBuilder.equal(message.get("teamCode"), this.getTeamCode()));
		}
		if (this.getMessageDate() != null) {
			cq.where(criteriaBuilder.equal(message.get("messageDate"), this.getMessageDate()));
		}
		cq.orderBy(criteriaBuilder.desc(message.get("messageDate")));
		cq.select(message);
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public Object getId() {
		return id;
	}

}
