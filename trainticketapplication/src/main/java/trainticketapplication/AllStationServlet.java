package trainticketapplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AllStationServlet")
public class AllStationServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		StringBuilder dataString = new StringBuilder();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
					"postgres", "Sudheer@21");
			PreparedStatement ps = conn.prepareStatement("select * from stations");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String stationName = rs.getString(2);
				dataString.append(stationName).append(",");
			}
			if (dataString.length() > 0) {
				dataString.setLength(dataString.length() - 1);
			}

			out.print(dataString.toString());
			out.flush();
		} catch (Exception e) {

		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
