package trainticketapplication;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TrainBookingServlet
 */
@WebServlet("/TrainBookingServlet")
public class TrainBookingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TrainBookingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		  String source = request.getParameter("source");
	        String destination = request.getParameter("destination");
	        String train_name = request.getParameter("trains");
	        String travelDate = request.getParameter("traveldate");
	        String coach = request.getParameter("coach");
	        String[] names = request.getParameterValues("nm");
	        String[] gender = request.getParameterValues("gen");
	        String[] age = request.getParameterValues("ag");
	        String[] berth = request.getParameterValues("br");
	        System.out.println("Source: " + source);
	        System.out.println("Destination: " + destination);
	        System.out.println("Train Name: " + train_name);
	        System.out.println("Travel Date: " + travelDate);
	        System.out.println("Coach: " + coach);
	        System.out.println("Passengers:");
	        for (int i = 0; i < names.length; i++) {
	            System.out.println("Name: " + names[i] + ", Gender: " + gender[i] + ", Age: " + age[i] + ", Berth: " + berth[i]);
	        }

	        // Generate unique PNR
	        int pnr = 0;
			try {
				pnr = generateUniquePNR();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int distance=0;
			try {
				distance = getDistance(source, destination);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		        BigDecimal[] farePerPassenger = new BigDecimal[names.length];
		        for (int i = 0; i < names.length; i++) {
		            int a = Integer.parseInt(age[i]);
		            BigDecimal fare = calculateFare(a, gender[i], distance, coach);
		            farePerPassenger[i] = fare;
		        }

		        // Calculate total fare
		        BigDecimal totalFare = BigDecimal.ZERO;
		        for (BigDecimal fare : farePerPassenger) {
		            totalFare = totalFare.add(fare);
		        }	        
		        System.out.println(totalFare);
		        try {
	        	Class.forName("org.postgresql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
						"postgres", "Sudheer@21");
				String sql = "INSERT INTO ticketdetails (pnr, departure_from, arrival_to, train_name, travel_date, coach, " +
	                         "p1_name, p1_gender, p1_age, p1_berth, p2_name, p2_gender, p2_age, p2_berth, " +
	                         "p3_name, p3_gender, p3_age, p3_berth, p4_name, p4_gender, p4_age, p4_berth, " +
	                         "p5_name, p5_gender, p5_age, p5_berth,total_fare) " +
	                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	            PreparedStatement stmt = conn.prepareStatement(sql);

	            stmt.setInt(1, pnr);
	            stmt.setString(2, source);
	            stmt.setString(3, destination);
	            stmt.setString(4, train_name);
	            
	            java.sql.Date sqlTravelDate = java.sql.Date.valueOf(travelDate);

	            
	            stmt.setDate(5, sqlTravelDate);
	            stmt.setString(6, coach);

	            // Set passenger details
	            for (int i = 0; i < 5; i++) {
	                if (i < names.length) {
	                    stmt.setString(i * 4 + 7, names[i]); // p1_name, p2_name, ..., p5_name
	                    stmt.setString(i * 4 + 8, gender[i]); // p1_gender, p2_gender, ..., p5_gender
	                    stmt.setInt(i * 4 + 9, Integer.parseInt(age[i])); // p1_age, p2_age, ..., p5_age
	                    stmt.setString(i * 4 + 10, berth[i]); // p1_berth, p2_berth, ..., p5_berth
	                } else {
	                    // If there are fewer than 5 passengers, set remaining columns to null
	                    stmt.setNull(i * 4 + 7, java.sql.Types.VARCHAR);
	                    stmt.setNull(i * 4 + 8, java.sql.Types.VARCHAR);
	                    stmt.setNull(i * 4 + 9, java.sql.Types.INTEGER);
	                    stmt.setNull(i * 4 + 10, java.sql.Types.VARCHAR);
	                }
	            }
	            stmt.setBigDecimal(27, totalFare);
	            stmt.executeUpdate();
	            conn.close();

	            response.getWriter().println("Ticket details inserted successfully. PNR: " + pnr);
//	            response.sendRedirect("TicketServlet?pnr=" + pnr);

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error inserting ticket details.");
	        }
		        
		  

		     
	    }

	    // Generate a unique PNR number
	    private int generateUniquePNR() throws SQLException, ClassNotFoundException {
	        int pnr;
	        do {
	            pnr = generatePNR();
	        } while (!isPNRUnique(pnr));
	        return pnr;
	    }

	    // Generate a random 6-digit PNR number
	    private int generatePNR() {
	        Random random = new Random();
	        return 100000 + random.nextInt(900000); // Generates a random number between 100000 and 999999
	    }

	    // Check if the generated PNR number exists in the database
	    private boolean isPNRUnique(int pnr) throws SQLException, ClassNotFoundException  {
	    	Class.forName("org.postgresql.Driver");
			Connection conn1 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
					"postgres", "Sudheer@21");
	        PreparedStatement stmt = conn1.prepareStatement("SELECT COUNT(*) FROM ticketdetails WHERE pnr = ?");
	        stmt.setInt(1, pnr);
	        ResultSet rs = stmt.executeQuery();
	        rs.next();
	        int count = rs.getInt(1);
	        conn1.close();
	        return count == 0;
	    }
	    private int getDistance(String from, String to) throws ClassNotFoundException {
            int distance = 0;
            try {
            	Class.forName("org.postgresql.Driver");
				Connection conn2 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
						"postgres", "Sudheer@21");
                String sql = "SELECT * FROM station_distance WHERE st_from = ? AND st_to = ?";
                PreparedStatement stmt = conn2.prepareStatement(sql);
                stmt.setString(1, from);
                stmt.setString(2, to);
                System.out.println(from+" "+to);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    distance = rs.getInt(3);
                    System.out.println(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3));
                }
                conn2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println(distance);
            return distance;
        }   

        private BigDecimal calculateFare(int age, String gender, int distance, String coachType) {
            BigDecimal fare = BigDecimal.ZERO;
            double factor = 0;
            if (coachType.equals("1")) {
                factor = 1.5;
            } else if (coachType.equals("2")) {
                factor = 2;
            } else if (coachType.equals("3")) {
                factor = 2.5;
            } else if (coachType.equals("4")) {
                factor = 3;
            }

            if (age < 5) {
                return fare;
            } else if (age >= 58 && gender.equalsIgnoreCase("F")) {
                fare = BigDecimal.valueOf(distance * factor * 0.5);
            } else if (age >= 60 && gender.equalsIgnoreCase("M")) {
                fare = BigDecimal.valueOf(distance * factor * 0.6);
            } else {
                fare = BigDecimal.valueOf(distance * factor);
            }
            System.out.println(fare);
            return fare;
        }

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		 String source = request.getParameter("source");
	        String destination = request.getParameter("destination");
	        String train_name = request.getParameter("trains");
	        String travelDate = request.getParameter("traveldate");
	        String coach = request.getParameter("coach");
	        String[] names = request.getParameterValues("nm");
	        String[] gender = request.getParameterValues("gen");
	        String[] age = request.getParameterValues("ag");
	        String[] berth = request.getParameterValues("br");

	       
	     
	        doGet(request,response);
	}

}
