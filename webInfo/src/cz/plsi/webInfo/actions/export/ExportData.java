package cz.plsi.webInfo.actions.export;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.plsi.webInfo.client.TeamStageClient;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStageHelp;

public class ExportData extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Cookie[] cookies = req.getCookies();
		boolean authorized = false;
		for (Cookie cookie : cookies) {
			if ("PlSiWI_TEAM_CODE".equals(cookie.getName()) && "rudolfove".equals(cookie.getValue())) {
				authorized = true;
			}
		}
		if (!authorized) {
			throw new RuntimeException("Not authorized");
		}
		String type = req.getParameter("type");

		ExportTypes exportType = ExportTypes.valueOf(type);
		StringBuilder sb = exportType.getExportedData();

		byte[] bytes = sb.toString().getBytes("utf-8");
		// do not cache
		resp.setHeader("Expires", "0");
		resp.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		resp.setHeader("Pragma", "public");
		// content length is needed for MSIE
		resp.setContentLength(bytes.length);
		resp.setContentType("text/csv");
		resp.addHeader("Content-Disposition", "attachment;filename=" + exportType.getFileName());
		ServletOutputStream out = resp.getOutputStream();
		out.write(bytes);
		out.flush();
	}

	private enum ExportTypes {
		TEAM_RESULTS("results.txt") {
			
			@Override
			public StringBuilder getExportedData() {
				TeamStage teamStage = new TeamStage();
				List<TeamStage> teamStages = teamStage.getList();
				teamStage.setStageOrder(TeamStage.TEAM_ENDED_GAME);
				List<TeamStage> teamsThatEnded = teamStage.getList();
				HashSet<String> teamEndedNames = new HashSet<String>(15);
				for (TeamStage teamThatEnded : teamsThatEnded) {
					teamEndedNames.add(teamThatEnded.getTeamName());
				}
				
				SimpleDateFormat sdf = getSimpleDateFormat();
				Map<String, TeamStageClient> teamStagesByTeamName = new HashMap<>(36);
				for (TeamStage actualTeamStage : teamStages) {
					String actualTeamName = actualTeamStage.getTeamName();
					try {
						if (!teamStagesByTeamName.containsKey(actualTeamName)
								&& ((sdf.parse("2015-09-20 08:00:00").compareTo(actualTeamStage.getStageDate()) >= 0 
										&& actualTeamStage.getStageOrder() < 14)
									|| actualTeamStage.getStageOrder() == 14)) {
							Date date = new Date(actualTeamStage.getStageDate().getTime());
							TeamStageClient teamStageClient = new TeamStageClient(actualTeamStage.getTeamName(),
																				actualTeamStage.getStageName(),
																				actualTeamStage.getStageOrder(),
																				date);
							teamStageClient.setEnded(teamEndedNames.contains(teamStageClient.getTeamName()));
							teamStagesByTeamName.put(actualTeamName,teamStageClient);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				TreeSet<TeamStageClient> sortedTeamStages = new TreeSet<>(Collections.reverseOrder());
				for (TeamStageClient teamStageClient : teamStagesByTeamName.values()) {
					sortedTeamStages.add(teamStageClient);
				}
				
				SimpleDateFormat stf = getSimpleTimeFormat();
				StringBuilder sb = new StringBuilder();
				sb.append("place;team_name;stageOrder;time\n");
				int place = 1;
				for (TeamStageClient teamStageClientForCsv : sortedTeamStages) {
					sb.append(place++).append(";");
					sb.append(teamStageClientForCsv.getTeamName()).append(";");
					sb.append(teamStageClientForCsv.getOrder()).append(";");
					sb.append(stf.format(teamStageClientForCsv.getStageDate()))
					.append("\n");
				}
				return sb;
			}
			
		},
		TEAM_STAGES("arrivals.txt") {

			@Override
			public StringBuilder getExportedData() {
				List<TeamStage> teamStages = (new TeamStage()).getList();

				SimpleDateFormat sdf = getSimpleDateFormat();

				StringBuilder sb = new StringBuilder();
				sb.append("team_name;code;time\n");
				for (TeamStage teamStage : teamStages) {
					sb.append(teamStage.getTeamName()).append(";");
					sb.append(teamStage.getStageName()).append(";");
					sb.append(sdf.format(teamStage.getStageDate()))
							.append("\n");
				}
				return sb;
			}

		},
		TEAM_STAGE_HELPS("phone_hints.txt") {

			@Override
			public StringBuilder getExportedData() {
				List<TeamStageHelp> teamStageHelps = (new TeamStageHelp()).getList();

				SimpleDateFormat sdf = getSimpleDateFormat();

				StringBuilder sb = new StringBuilder();
				sb.append("code;position;team_name;time\n");
				Set<String> teamStageHelpsPositions = new HashSet<>();
				for (TeamStageHelp teamStageHelp : teamStageHelps) {
					if (!teamStageHelp.isHelp()) {
						continue;
					}
					if (!teamStageHelpsPositions.contains(getHash(teamStageHelp, 1))) {
						sb.append(teamStageHelp.getStageName()).append(";");
						teamStageHelpsPositions.add(getHash(teamStageHelp, 1));
						sb.append(1).append(";");
					} else if (!teamStageHelpsPositions.contains(getHash(teamStageHelp, 2))) {
						sb.append(teamStageHelp.getStageName()).append(";");
						teamStageHelpsPositions.add(getHash(teamStageHelp, 2));
						sb.append(2).append(";");
					} else {
						continue;
					}
					sb.append(teamStageHelp.getTeamName()).append(";");
					sb.append(sdf.format(teamStageHelp.getStageHelpDate()))
							.append("\n");
				}
				return sb;
			}

			private String getHash(TeamStageHelp teamStageHelp, int position) {
				return teamStageHelp.getStageName() + teamStageHelp.getTeamName() + "_" + position;
			}

		},
		TEAM_STAGE_RESULTS("absolute_hints.txt") {
			
			@Override
			public StringBuilder getExportedData() {
				List<TeamStageHelp> teamStageHelps = (new TeamStageHelp()).getList();
				
				SimpleDateFormat sdf = getSimpleDateFormat();
				
				StringBuilder sb = new StringBuilder();
				sb.append("code;team_name;time\n");
				Set<String> teamStageHelpsPositions = new HashSet<>();
				for (TeamStageHelp teamStageHelp : teamStageHelps) {
					if (!teamStageHelp.isHelp()) {
						continue;
					}
					if (!teamStageHelpsPositions.contains(getHash(teamStageHelp, 1))) {
						teamStageHelpsPositions.add(getHash(teamStageHelp, 1));
					} else if (!teamStageHelpsPositions.contains(getHash(teamStageHelp, 2))) {
						teamStageHelpsPositions.add(getHash(teamStageHelp, 2));
					} else {
						sb.append(teamStageHelp.getStageName()).append(";");
						sb.append(teamStageHelp.getTeamName()).append(";");
						sb.append(sdf.format(teamStageHelp.getStageHelpDate()))
						.append("\n");
					}
				}
				return sb;
			}
			
			private String getHash(TeamStageHelp teamStageHelp, int position) {
				return teamStageHelp.getStageName() + teamStageHelp.getTeamName() + "_" + position;
			}
			
		},

		STAGES("puzzles.txt") {

			@Override
			public StringBuilder getExportedData() {
				List<Stage> stages = (new Stage()).getList();

				StringBuilder sb = new StringBuilder();
				sb.append("position;name;code;hint1;hint2\n");
				for (Stage stage : stages) {
					sb.append(stage.getNumber()).append(";");
					sb.append(stage.getDescription()).append(";");
					sb.append(stage.getName()).append(";");
					sb.append(stage.getHelp1()).append(";");
					sb.append(stage.getHelp2())
							.append("\n");
				}
				return sb;
			}

		};

		private static SimpleDateFormat getSimpleDateFormat() {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", new Locale("cs", "CZ"));
			sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
			return sdf;
		}
		
		private static SimpleDateFormat getSimpleTimeFormat() {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"HH:mm:ss", new Locale("cs", "CZ"));
			sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
			return sdf;
		}

		private String fileName;

		private ExportTypes(String fileName) {
			this.fileName = fileName;
		}

		public abstract StringBuilder getExportedData();

		public String getFileName() {
			return fileName;
		}
	}

}
