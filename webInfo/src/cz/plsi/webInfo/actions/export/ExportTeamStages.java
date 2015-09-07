package cz.plsi.webInfo.actions.export;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;

public class ExportTeamStages extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	    // TODO Auto-generated method stub
	    super.doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {

		// process the data (In your case go get it)
		List<TeamStage> teamStages = (new TeamStage()).getList();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		
		StringBuilder sb = new StringBuilder();
		sb.append("team_name;code;time\n");
		for (TeamStage teamStage : teamStages) {
			sb.append(teamStage.getTeamName()).append(";");
			sb.append(teamStage.getStageName()).append(";");
			sb.append(sdf.format(teamStage.getStageDate())).append("\n");
		}
		
		byte[] bytes = sb.toString().getBytes();
		// do not cache
		resp.setHeader("Expires", "0");  
		resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");  
		resp.setHeader("Pragma", "public");
		// content length is needed for MSIE
		resp.setContentLength(bytes.length);
		// set the filename and the type
		resp.setContentType("text/csv");  
		resp.addHeader("Content-Disposition", "attachment;filename=arrivals.txt");  
		ServletOutputStream out = resp.getOutputStream();
		out.write(bytes);
		out.flush();
	}
}
