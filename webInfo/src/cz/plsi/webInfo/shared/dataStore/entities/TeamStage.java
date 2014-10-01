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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.appengine.api.datastore.Key;

import cz.plsi.webInfo.shared.dataStore.EMF;

@Entity
public class TeamStage implements EntityCommon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String teamName;
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}


	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	private String stageName;
	
	private Date stageDate;
	

	public TeamStage(String teamName, String stageName) {
		this.teamName = teamName;
		this.stageName = stageName;
		this.stageDate = new Date();
	}


	public TeamStage() {
		super();
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
	//TODO refaktor z team name na team code
	public static TeamStage getLastTeamStage(String team) {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<TeamStage> query = em.createQuery(
				"SELECT ts FROM "+ TeamStage.class.getName() +" ts "
						+ "where ts.teamName=:teamName "
						+ "ORDER BY ts.stageDate DESC", TeamStage.class);
		query.setParameter("teamName", team);
		query.setMaxResults(1);
		List<TeamStage> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
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
	public List<TeamStage> getList() {
		EntityManager em = EMF.getInstance().createEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<TeamStage> cq =  criteriaBuilder.createQuery(TeamStage.class);
		Root<TeamStage> teamStage = cq.from(TeamStage.class);
		
		Predicate criteria = null;
		if (this.teamName != null) {
			criteria = criteriaBuilder.equal(teamStage.get("teamName"), this.teamName);
			cq.where(criteria);
		}
		
		if (this.stageName != null) {
			criteria = criteriaBuilder.and(criteria, criteriaBuilder.equal(teamStage.get("stageName"), this.stageName));
		}
		
		if (criteria != null) {
			cq.where(criteria);
		}
		
		cq.select(teamStage);
		cq.orderBy(criteriaBuilder.desc(teamStage.get("stageDate")));
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public String toString() {
		return "TeamStage [id=" + id + ", teamName=" + teamName
				+ ", stageName=" + stageName + ", stageDate=" + stageDate + "]";
	}
	
	
}
