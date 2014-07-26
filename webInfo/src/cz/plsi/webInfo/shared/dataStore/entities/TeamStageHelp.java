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
public class TeamStageHelp implements EntityCommon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private Key teamStageId;
	
	private String help;
	
	private String stageName;
	
	private String teamName;
	
	public Date getStageHelpDate() {
		return stageHelpDate;
	}


	public void setStageHelpDate(Date stageHelpDate) {
		this.stageHelpDate = stageHelpDate;
	}


	public TeamStage getTeamStage() {
		
		return teamStageId == null ? null
								   : TeamStage.getTeamStage(teamStageId);
	}

	private Date stageHelpDate;
	

	public TeamStageHelp(TeamStage teamStage, String help) {
		
		this.teamStageId = teamStage.getId();
		this.stageName = teamStage.getStageName();
		this.teamName = teamStage.getTeamName();
		this.help = help;
		this.stageHelpDate = new Date();
	}


	public TeamStageHelp() {
	}


	public Key getId() {
		return id;
	}



	public Date getStageDate() {
		return stageHelpDate;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.shared.dataStore.entities.EntityCommon#count()
	 */
	@Override
	public long count() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<Long> query = em.createQuery(
			      "SELECT COUNT(tsh) FROM "+ TeamStageHelp.class.getName() +" tsh", Long.class);
		return query.getSingleResult();
	}
	

	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.shared.dataStore.entities.EntityCommon#exists()
	 */
	@Override
	public boolean exists() {
		EntityManager em = EMF.getInstance().createEntityManager();
		Query query = em.createQuery(
			      "SELECT tsh FROM "+ TeamStageHelp.class.getName() +" tsh "
			      		+ "where tsh.teamStageId=:teamStageId "
			      		+ "and tsh.help=:help");
		query.setParameter("teamStageId", this.getTeamStage().getId());
		query.setParameter("help", this.getHelp());
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}
	
	public String getHelp() {
		return help;
	}


	public void setHelp(String help) {
		this.help = help;
	}


	@Override
	public List<TeamStageHelp> getAll() {
		EntityManager em = EMF.getInstance().createEntityManager();
		TypedQuery<TeamStageHelp> query = em.createQuery(
			      "SELECT ts FROM "+ TeamStageHelp.class.getName() +" ts", TeamStageHelp.class);
		return query.getResultList();
	}

	@Override
	public String toString() {
		return "TeamStageHelp [id=" + id + ", teamStage=" + getTeamStage()
				+ ", help=" + help + ", stageHelpDate=" + stageHelpDate + "]";
	}

	
	
	
}

