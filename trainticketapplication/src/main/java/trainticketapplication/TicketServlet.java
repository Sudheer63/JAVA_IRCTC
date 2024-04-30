package trainticketapplication;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TicketServlet")
public class TicketServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "Sudheer@21");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ticketdetails");

            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();

            // Create a list to hold ticket details
            List<List<String>> ticketDetails = new ArrayList<>();

            // Iterate through the result set and add ticket details to the list
            while (rs.next()) {
                List<String> ticketInfo = new ArrayList<>();
                ticketInfo.add(rs.getString("pnr"));
                ticketInfo.add(rs.getString("departure_from"));
                ticketInfo.add(rs.getString("arrival_to"));
                ticketInfo.add(rs.getString("train_name"));
                ticketInfo.add(rs.getString("travel_date"));
                ticketInfo.add(rs.getString("coach"));
                // Add passenger details
                for (int i = 1; i <= 5; i++) {
                    String passengerName = rs.getString("p" + i + "_name");
                    if (passengerName != null && !passengerName.isEmpty()) {
                        ticketInfo.add(passengerName);
                        ticketInfo.add(rs.getString("p" + i + "_gender"));
                        ticketInfo.add(rs.getString("p" + i + "_age"));
                        ticketInfo.add(rs.getString("p" + i + "_berth"));
                    }
                }
                ticketInfo.add(rs.getString("total_fare"));
                ticketDetails.add(ticketInfo);
            }

            // Close the result set, statement, and connection
            rs.close();
            stmt.close();
            conn.close();

            // Set ticket details as request attribute
            request.setAttribute("ticketDetails", ticketDetails);

//            request.getRequestDispatcher("ticket.html").forward(request, response);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handling POST requests if needed
    }
}
