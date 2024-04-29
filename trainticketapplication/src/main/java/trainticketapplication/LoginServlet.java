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


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		boolean authenticated = false;
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
					"postgres", "Sudheer@21");
			String sql = "SELECT * FROM train_users WHERE username = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				authenticated = rs.next();
				System.out.println(authenticated + username + password);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (authenticated) {
			response.sendRedirect("trainbooking.html");

		} else {
			out.println("<html><h1>Failure</h1></html>");
			out.println("<script>");
			out.println("setTimeout(function() { window.location.href = 'login.html'; }, 2000);");
			out.println("</script>");
		}
	}
}
