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
public class TeamStage implements EntityCommon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String teamName;
	
	private String stageName;
	
	private Date stageDate;
	

	public TeamStage(String teamName, String stageName) {
		this.teamName = teamName;
		this.stageName = stageName;
		this.stageDate = new Date();
	}


	public Key getId() {
		return id;
	}


	public String getTeamName() {
		return teamName;
	}
	
	public String getStageName() {
		return stageName;
	}


	public Date getStageDate() {
		return stageDate;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.shared.dataStore.entities.EntityCommon#count()
	 */
	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(ts) FROM "+ TeamStage.class.getName() +" ts", Long.class);
		return query.getSingleResult();
	}
	
	public static TeamStage getTeamStage(String team, String stage) {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<TeamStage> query = em.createQuery(
			      "SELECT ts FROM "+ TeamStage.class.getName() +" ts "
			      		+ "where ts.teamName=:teamName "
			      		+ "and ts.stageName=:stageName", TeamStage.class);
		query.setParameter("teamName", team);
		query.setParameter("stageName", stage);
		query.setMaxResults(1);
		List<TeamStage> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
	}
	
	public static TeamStage getTeamStage(Key id) {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<TeamStage> query = em.createQuery(
				"SELECT ts FROM "+ TeamStage.class.getName() +" ts "
						+ "where ts.id=:id", TeamStage.class);
		query.setParameter("id", id);
		query.setMaxResults(1);
		List<TeamStage> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.shared.dataStore.entities.EntityCommon#exists()
	 */
	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT ts FROM "+ TeamStage.class.getName() +" ts "
			      		+ "where ts.teamName=:teamName "
			      		+ "and ts.stageName=:stageName");
		query.setParameter("teamName", this.getTeamName());
		query.setParameter("stageName", this.getStageName());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}
	
	@Override
	public List<TeamStage> getAll() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<TeamStage> cq =  criteriaBuilder.createQuery(TeamStage.class);
		Root<TeamStage> teamSage = cq.from(TeamStage.class);
		cq.select(teamSage);
		cq.orderBy(criteriaBuilder.asc(teamSage.get("stageDate")));
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public String toString() {
		return "TeamStage [id=" + id + ", teamName=" + teamName
				+ ", stageName=" + stageName + ", stageDate=" + stageDate + "]";
	}
	
	
}
