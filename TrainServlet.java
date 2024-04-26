package sample;

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

@WebServlet("/TrainServlet")
public class TrainServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String source = request.getParameter("source");
		String destination = request.getParameter("destination");

		System.out.println("Source: " + source);
		System.out.println("Destination: " + destination);
		StringBuilder dataString = new StringBuilder();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://192.168.110.48:5432/plf_training",
					"plf_training_admin", "pff123");
			PreparedStatement ps = conn.prepareStatement("Select train_name from trains_207 where train_no IN(\r\n"
					+ "SELECT t1.train_no\r\n" + "FROM train_schedule_207 t1\r\n"
					+ "JOIN train_schedule_207 t2 ON t1.train_no = t2.train_no\r\n" + "WHERE t1.station_name = ?\r\n"
					+ "  AND t2.station_name = ?\r\n" + "  AND t1.train_index < t2.train_index) Order by train_no;");
			ps.setString(1, source);
			ps.setString(2, destination);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String TrainName = rs.getString(1);
				dataString.append(TrainName).append(",");
			}
			if (dataString.length() > 0) {
				dataString.setLength(dataString.length() - 1);
			}

			out.print(dataString.toString());
			out.flush();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
