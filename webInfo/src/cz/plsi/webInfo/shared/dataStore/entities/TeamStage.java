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
	
	public static final int TEAM_ENDED_GAME = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String teamName;
	
	private int stageOrder;
	
	private String stageName;
	
	private Date stageDate;
	
	private String stageBranch;
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}


	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	

	public TeamStage(String teamName, String stageName, int stageOrder) {
		this.teamName = teamName;
		this.stageName = stageName;
		this.stageDate = new Date();
		this.stageOrder = stageOrder;
		this.stageBranch = Stage.DEFAULT_LINEAR_BRANCH;
	}

	
	public TeamStage(String teamName, String stageName, int stageOrder, String stageBranch) {
		this.teamName = teamName;
		this.stageName = stageName;
		this.stageDate = new Date();
		this.stageOrder = stageOrder;
		this.stageBranch = stageBranch;
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
	
	public int getStageOrder() {
		return stageOrder;
	}


	public void setStageOrder(int stageOrder) {
		this.stageOrder = stageOrder;
	}
	
	public String getStageBranch() {
		return stageBranch;
	}


	public void setStageBranch(String stageBranch) {
		this.stageBranch = stageBranch;
	}

	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.shared.dataStore.entities.EntityCommon#count()
	 */
	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT COUNT(ts) FROM ");
		queryBuilder.append(TeamStage.class.getName());
		queryBuilder.append(" ts");
		if (this.teamName != null) {
			queryBuilder.append(" where ts.teamName=:teamName");
		}
		TypedQuery<Long> query = em.createQuery(
				queryBuilder.toString(), Long.class);
		if (this.teamName != null) {
			query.setParameter("teamName", this.teamName);
		}
		
					
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
	
	public static TeamStage getLastTeamStage(String team, String stageBranch) {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<TeamStage> query = em.createQuery(
				"SELECT ts FROM "+ TeamStage.class.getName() +" ts "
						+ "where ts.teamName=:teamName "
						+ "and ts.stageBranch=:stageBranch "
						+ "ORDER BY ts.stageDate DESC", TeamStage.class);
		query.setParameter("teamName", team);
		query.setParameter("stageBranch", stageBranch);
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
		}
		
		if (this.stageName != null) {
			criteria = criteriaBuilder.and(criteria, criteriaBuilder.equal(teamStage.get("stageName"), this.stageName));
		}
		
		if (this.stageBranch != null) {
			criteria = criteriaBuilder.and(criteria, criteriaBuilder.equal(teamStage.get("stageBranch"), this.stageBranch));
		}
		
		if (this.stageOrder == TEAM_ENDED_GAME) {
			Predicate equalEnded = criteriaBuilder.equal(teamStage.get("stageOrder"), this.stageOrder);
			criteria = criteria != null ? criteriaBuilder.and(criteria, equalEnded) : equalEnded;
		} else {
			Predicate notEqualOrder = criteriaBuilder.notEqual(teamStage.get("stageOrder"), TEAM_ENDED_GAME);
			criteria = criteria != null ? criteriaBuilder.and(criteria, notEqualOrder) : notEqualOrder;
		}
		
		if (criteria != null) {
			cq.where(criteria);
		}
		
		cq.select(teamStage);
		cq.orderBy(criteriaBuilder.desc(teamStage.get("stageOrder")),
				criteriaBuilder.asc(teamStage.get("stageDate")));
		
		em.getTransaction().begin();
		List<TeamStage> resultList = em.createQuery(cq).getResultList();
		em.getTransaction().commit();
		return resultList;
	}

	@Override
	public String toString() {
		return "TeamStage [id=" + id + ", teamName=" + teamName
				+ ", stageName=" + stageName + ", stageDate=" + stageDate + "]";
	}

}
