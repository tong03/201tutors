package tech_tutors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBHandler {
	private static final String USERNAME = "root";
	private static final String PASSWORD = "rootpw201";

	public static void main(String[] args) {
		setupDemo();
	}
	
	private static void setupDemo() {
		// Simulate adding some existing tutors, so the Guest can see tutors for certain courses.
		
		// Be sure to run dbinit.sql first before running this, so the database is clean

		ArrayList<Course> courses = getAllCourses();
		
		int id1 = registerUser("testinga@usc.edu", "test", "Alice", "Tutor", "Sophomore", "CSCI");
		int id2 = registerUser("testingb@usc.edu", "test", "Brian", "Tutor", "Senior", "CSBA");
		int id3 = registerUser("testingc@usc.edu", "test", "Charlie", "Coach", "Junior", "CSGA");
		int id4 = registerUser("testingd@usc.edu", "test", "Diana", "Mentor", "Masters Student", "CSCI");
		int id5 = registerUser("testinge@usc.edu", "test", "Eric", "Educator", "PhD Student", "CECS");
		
		ProfileInfo info1 = getProfile(id1);
		ProfileInfo info2 = getProfile(id2);
		ProfileInfo info3 = getProfile(id3);
		ProfileInfo info4 = getProfile(id4);
		ProfileInfo info5 = getProfile(id5);
		
		info1.courses.add(courses.get(1)); // CSCI 103
		info1.courses.add(courses.get(3)); // CSCI 170

		info2.courses.add(courses.get(1)); // CSCI 103
		info2.courses.add(courses.get(2)); // CSCI 104
		info2.courses.add(courses.get(3)); // CSCI 170
		info2.courses.add(courses.get(4)); // CSCI 201
		info2.courses.add(courses.get(5)); // CSCI 270

		info3.courses.add(courses.get(1)); // CSCI 103
		info3.courses.add(courses.get(2)); // CSCI 104
		info3.courses.add(courses.get(3)); // CSCI 170
		info3.courses.add(courses.get(23)); // CSCI 487
		
		info4.courses.add(courses.get(8)); // CSCI 353
		info4.courses.add(courses.get(12)); // CSCI 402
		info4.courses.add(courses.get(13)); // CSCI 420
		
		info5.courses.add(courses.get(9)); // CSCI 356
		info5.courses.add(courses.get(19)); // CSCI 457
		info5.courses.add(courses.get(21)); // CSCI 467
		info5.courses.add(courses.get(38)); // CSCI 555
		
		info1.availability.add(new Availability("Thursday", 16, 21));
		info1.availability.add(new Availability("Friday", 15, 21));
		info1.availability.add(new Availability("Saturday", 12, 21));
		
		info2.availability.add(new Availability("Monday", 17, 22));
		info2.availability.add(new Availability("Tuesday", 15, 22));
		info2.availability.add(new Availability("Thursday", 15, 20));
		
		info3.availability.add(new Availability("Wednesday", 12, 20));
		info3.availability.add(new Availability("Friday", 12, 20));
		
		info4.availability.add(new Availability("Sunday", 13, 21));
		
		info5.availability.add(new Availability("Friday", 16, 20));
		
		changeProfile(id1, info1);
		changeProfile(id2, info2);
		changeProfile(id3, info3);
		changeProfile(id4, info4);
		changeProfile(id5, info5);
		
		System.out.println("Inserted 5 users with some tutor info for the demo.");
	}
	
	@SuppressWarnings("unused")
	private static boolean test() {
		// Test the database according to the testing plan
		// Returns true if all the tests pass.
		// Prints out if any tests fail
		
		// Be sure to run dbinit.sql first before testing so the database is clean
		
		boolean passed = true;
		ArrayList<Course> courses = getAllCourses();
		Connection conn = init();
		Statement statement = null;
		int count = 0;
		
		if (conn != null) {
			try {
				statement = conn.createStatement();
				ResultSet results = statement.executeQuery(
						"SELECT Count(DISTINCT courseCode) FROM Courses;");
				if (results.next()) {
					count = results.getInt(1);
				}
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in courseLengthTest: " + sqle);
				sqle.printStackTrace();
			} finally {
				close(conn, null, statement);
			}
		}
		
		if (count != courses.size()) {
			System.out.println("Not every course in the table is unique.");
			passed = false;
		}
		
		int idTest = registerUser("test", "testpw", "tester", "201", "freshman", "CSCI");
		
		if (idTest != lookupUser("test", "testpw")) {
			System.out.println("Couldn't find user that was just registered.");
			passed = false;
		}
		
		// Add some courses for the tutor
		ProfileInfo testing = getProfile(idTest);
		boolean[] expectedResult = {false, true, true, false, true};
		for (int i = 0; i < expectedResult.length; i++) {
			if (expectedResult[i]) {
				testing.courses.add(courses.get(i));
			}
		}
		
		changeProfile(idTest, testing);
		
		// Ensure that only the courses we added are found for this user
		for (int i = 0; i < 5; ++i) {
			ArrayList<ProfileInfo> tutors = tutorsForCourse(courses.get(i).id);
			boolean hasTutor = false;
			for (ProfileInfo tutor : tutors) {
				if (tutor.id == idTest) {
					hasTutor = true;
					break;
				}
			}
			if (expectedResult[i] != hasTutor) {
				passed = false;
				System.out.println("TutorCourse mismatch for CSCI-" + courses.get(i).code);
			}	
		}
		
		//insert 2 more dummy users
		
		int ChatTest1 = registerUser("test1", "testpw", "tester1", "201", "freshman", "CSCI");
		int ChatTest2 = registerUser("test2", "testpw", "tester2", "201", "freshman", "CSCI");
		
		// start chats between them
		int ChatID1 = startChat(idTest, ChatTest1);
		int ChatID2 = startChat(ChatTest1, ChatTest2);
		
		// This part will take a while.
		
		for (int i = 0; i < 5000; ++i) {
			sendMessage(ChatID1, true, "test1");
			sendMessage(ChatID2, false, "test2");
		}
		
		ChatHistory history1 = loadChatHistory(idTest);
		ChatHistory history2 = loadChatHistory(ChatTest1);
		ChatHistory history3 = loadChatHistory(ChatTest2);
		
		if (history1.messages.get(ChatID1).size() != 5000) {
			System.out.println("Expected 5000 messages between idTest and ChatTest1, got " + history1.messages.get(ChatID1).size());
			passed = false;
		}
		if (history2.messages.get(ChatID1).size() != 5000 || history2.messages.get(ChatID2).size() != 5000) {
			System.out.println("Expected 5000 messages between idTest and ChatTest1, got " + history2.messages.get(ChatID1).size());
			System.out.println("Expected 5000 messages between ChatTest1 and ChatTest2, got " + history2.messages.get(ChatID2).size());
			passed = false;
		}
		if (history3.messages.get(ChatID2).size() != 5000) {
			System.out.println("Expected 5000 messages between ChatTest1 and ChatTest2, got " + history3.messages.get(ChatID2).size());
			passed = false;
		}
		if (history1.messages.containsKey(ChatID2)) {
			System.out.println("There should not be a chat with ChatTest2 for idTest.");
			passed = false;
		}
		if (history3.messages.containsKey(ChatID1)) {
			System.out.println("There should not be a chat with idTest for ChatTest2.");
			passed = false;
		}
		
		for (int i = 0; i < 10000; ++i) {
			sendMessage(ChatID1, true, "test1");
		}
		history1 = loadChatHistory(idTest);
		if (history1.messages.get(ChatID1).size() != 15000) {
			System.out.println("Expected 15000 messages between idTest and ChatTest1, got " + history1.messages.get(ChatID1).size());
			passed = false;
		}
		
		return passed;
	}

	public static Connection init() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost/201FP?user=" + USERNAME +
							"&password=" + PASSWORD);
			return conn;
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ClassNotFoundException thrown during initialization: " + cnfe);
			return null;
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown during initialization: " + sqle);
			return null;
		}
	}

	public static void close(Connection conn, PreparedStatement prepStatement, Statement statement) {
		try {
			if (prepStatement != null) {
				prepStatement.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown while closing: " + sqle);
		}
	}

	public static boolean isEmailTaken(String email) {
		PreparedStatement prepStatement = null;
		Connection conn = init();
		if (conn == null) {
			return true;
		}

		try {
			prepStatement = conn.prepareStatement(
					"SELECT userID FROM Users WHERE email=?;");
			prepStatement.setString(1, email);
			ResultSet results = prepStatement.executeQuery();
			return results.isBeforeFirst();
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown in isEmailTaken: " + sqle);
			return true;
		} finally {
			close(conn, prepStatement, null);
		}
	}

	// Return the user ID of the user if they have created an account, -1 if not
	public static int lookupUser(String email, String password) {
		int id = -1;
		PreparedStatement prepStatement = null;
		Connection conn = init();
		if (conn != null) {
			try {
				prepStatement = conn.prepareStatement(
						"SELECT userID FROM Users WHERE email=? AND pw=?;");
				prepStatement.setString(1, email);
				prepStatement.setString(2, password);
				ResultSet results = prepStatement.executeQuery();
				if (results.next()) {
					id = results.getInt(1);
				}
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in lookupUser: " + sqle);
			} finally {
				close(conn, prepStatement, null);
			}
		}
		
		return id;
	}

	public static int registerUser(
			String email, String password, String fname, String lname, String standing, String major) {
		if (isEmailTaken(email)) {
			return -1;
		}
		
		int id = -1;
		Connection conn = init();
		PreparedStatement prepStatement = null;
		Statement statement = null;
		
		if (conn != null) {
			try {
				prepStatement = conn.prepareStatement(
						"INSERT INTO Users (email, pw, fname, lname, standing, major) VALUES "
								+ " (?,?,?,?,?,?);");
				prepStatement.setString(1, email);
				prepStatement.setString(2, password);
				prepStatement.setString(3, fname);
				prepStatement.setString(4, lname);
				prepStatement.setString(5, standing);
				prepStatement.setString(6, major);
				prepStatement.executeUpdate();
				statement = conn.createStatement();
				ResultSet results = statement.executeQuery("SELECT LAST_INSERT_ID()");
				results.next();
				id = results.getInt(1);
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in registerUser: " + sqle);
			} finally {
				close(conn, prepStatement, statement);
			}
		}
		
		return id;
	}

	public static ArrayList<Course> getAllCourses() {
		ArrayList<Course> courses = new ArrayList<Course>();
		Connection conn = init();
		Statement statement = null;
		
		if (conn != null) {
			try {
				statement = conn.createStatement();
				ResultSet results = statement.executeQuery(
						"SELECT * FROM Courses;");
				while (results.next()) {
					int id = results.getInt("courseID");
					String code = results.getString("courseCode");
					String name = results.getString("courseName");
					courses.add(new Course(id, code, name));
				}
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in getAllCourses: " + sqle);
				sqle.printStackTrace();
			} finally {
				close(conn, null, statement);
			}
		}
		
		return courses;
	}

	// Lookup by database ID
	public static ArrayList<ProfileInfo> tutorsForCourse(int courseID) {
		ArrayList<ProfileInfo> tutors = new ArrayList<ProfileInfo>();
		Connection conn = init();
		PreparedStatement prepStatement = null;
		
		if (conn != null) {
			try {
				prepStatement = conn.prepareStatement(
						"SELECT * FROM Users u, CourseTutors ct WHERE u.userID=ct.userID AND ct.courseID=?");
				prepStatement.setInt(1, courseID);
				ResultSet results = prepStatement.executeQuery();
				while (results.next()) {
					int id = results.getInt("userID");
					tutors.add(getProfile(id));
				}
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in tutorsForCourse: " + sqle);
			} finally {
				close(conn, prepStatement, null);
			}
		}
		
		return tutors;
	}

	// Lookup by database ID
	public static ArrayList<Availability> availabilityForTutor(Connection conn, int userID) {
		ArrayList<Availability> avail = new ArrayList<Availability>();
		try {
			PreparedStatement prepStatement = conn.prepareStatement(
					"SELECT * FROM Availability WHERE userID=?");
			prepStatement.setInt(1, userID);
			ResultSet results = prepStatement.executeQuery();
			while (results.next()) {
				String day = results.getString("dayOfWeek");
				int h1 = results.getInt("startHour");
				int h2 = results.getInt("endHour");
				avail.add(new Availability(day, h1, h2));
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown in availabilityForTutor: " + sqle);
			sqle.printStackTrace();
		}
		
		return avail;
	}

	// Lookup by database ID
	public static ArrayList<Course> coursesForTutor(Connection conn, int userID) {
		ArrayList<Course> courses = new ArrayList<Course>();
		try {
			PreparedStatement prepStatement = conn.prepareStatement(
                "SELECT c.courseID, c.courseCode, c.courseName " +
                "FROM CourseTutors ct " +
                "JOIN Courses c ON ct.courseID = c.courseID " +
                "WHERE ct.userID = ?"
			);
			prepStatement.setInt(1, userID);
			ResultSet results = prepStatement.executeQuery();
			while (results.next()) {
				int id = results.getInt("courseID");
				String code = results.getString("courseCode");
				String name = results.getString("courseName");
				courses.add(new Course(id, code, name));
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown in coursesForTutor: " + sqle);
			sqle.printStackTrace();
		}
		
		return courses;
	}
	

	public static int startChat(int tutorID, int studentID) {
		int id = -1;
		Connection conn = init();
		PreparedStatement prepStatement = null;
		Statement statement = null;
		
		if (conn != null) {
			try {
				prepStatement = conn.prepareStatement(
					"SELECT * FROM Conversations WHERE tutorID=? AND studentID=?;"
				);
				prepStatement.setInt(1, tutorID);
				prepStatement.setInt(2, studentID);
				ResultSet results = prepStatement.executeQuery();
				if (!results.next()) {
					prepStatement = conn.prepareStatement(
						"INSERT INTO Conversations (tutorID, studentID) VALUES "
							+ " (?,?);");
					prepStatement.setInt(1, tutorID);
					prepStatement.setInt(2, studentID);
					prepStatement.executeUpdate();
					statement = conn.createStatement();
					results = statement.executeQuery("SELECT LAST_INSERT_ID()");
					results.next();
					id = results.getInt(1);
				}
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in startChat: " + sqle);
			} finally {
				close(conn, prepStatement, statement);
			}
		}
		
		return id;
	}

	public static Message sendMessage(int chatID, boolean sentByTutor, String content) {
		Message msg = null;
		Connection conn = init();
		PreparedStatement prepStatement = null;
		
		if (conn != null) {
			try {
				prepStatement = conn.prepareStatement(
						"INSERT INTO Messages (chatID, sender, msgTimestamp, content) VALUES "
								+ " (?,?,UTC_TIMESTAMP(),?);");
				prepStatement.setInt(1, chatID);
				prepStatement.setInt(2, sentByTutor ? 0 : 1);
				prepStatement.setString(3, content);
				prepStatement.executeUpdate();
				msg = messagesForConversation(conn, chatID).getLast();
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in sendMessage: " + sqle);
			} finally {
				close(conn, prepStatement, null);
			}
		}
		
		return msg;
	}

	// Lookup by database ID
	private static ArrayList<Message> messagesForConversation(Connection conn, int chatID) {
		ArrayList<Message> messages = new ArrayList<Message>();
		PreparedStatement prepStatement = null;
		try {
			prepStatement = conn.prepareStatement(
					"SELECT * FROM Messages WHERE chatID=?");
			prepStatement.setInt(1, chatID);
			ResultSet results = prepStatement.executeQuery();
			while (results.next()) {
				int id = results.getInt("messageID");
				boolean sentByTutor = results.getInt("sender") == 0;
				String timestamp = results.getString("msgTimestamp");
				String content = results.getString("content");
				messages.add(new Message(id, chatID, sentByTutor, timestamp, content));
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown in messagesForConversation: " + sqle);
		} finally {
			close(null, prepStatement, null);
		}
		
		return messages;
	}

	// Lookup by database ID which chats this user has active (as the student)
	private static ArrayList<Conversation> chatsWithTutors(Connection conn, int userID) {
		ArrayList<Conversation> chats = new ArrayList<Conversation>();
		PreparedStatement prepStatement = null;
		try {
			prepStatement = conn.prepareStatement(
					"SELECT * FROM Conversations c, Users u WHERE c.tutorID=u.userID AND c.studentID=?");
			prepStatement.setInt(1, userID);
			ResultSet results = prepStatement.executeQuery();
			while (results.next()) {
				int id = results.getInt("c.chatID");
				int tutorID = results.getInt("c.tutorID");
				String fname = results.getString("u.fname");
				String lname = results.getString("u.lname");
				chats.add(new Conversation(id, tutorID, userID, fname, lname));
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown in chatsWithTutors: " + sqle);
		} finally {
			close(null, prepStatement, null);
		}
		
		return chats;
	}

	// Lookup by database ID which chats this user has active (as the tutor)
	private static ArrayList<Conversation> chatsWithStudents(Connection conn, int userID) {
		ArrayList<Conversation> chats = new ArrayList<Conversation>();
		PreparedStatement prepStatement = null;
		try {
			prepStatement = conn.prepareStatement(
					"SELECT * FROM Conversations c, Users u WHERE c.studentID=u.userID AND c.tutorID=?");
			prepStatement.setInt(1, userID);
			ResultSet results = prepStatement.executeQuery();
			while (results.next()) {
				int id = results.getInt("c.chatID");
				int studentID = results.getInt("c.studentID");
				String fname = results.getString("u.fname");
				String lname = results.getString("u.lname");
				chats.add(new Conversation(id, userID, studentID, fname, lname));
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException thrown in chatsWithStudents: " + sqle);
		} finally {
			close(null, prepStatement, null);
		}
		
		return chats;
	}
	
	public static ChatHistory loadChatHistory(int userID) {
		ChatHistory history = new ChatHistory();
		Connection conn = init();
		
		if (conn != null) {
			history.tutorChats = chatsWithTutors(conn, userID);
			history.tuteeChats = chatsWithStudents(conn, userID);
			for (Conversation c : history.tutorChats) {
				history.messages.put(c.id, messagesForConversation(conn, c.id));
			}
			for (Conversation c : history.tuteeChats) {
				history.messages.put(c.id, messagesForConversation(conn, c.id));
			}
			close(conn, null, null);
		}
		return history;
	}
	
	public static boolean changeProfile(int userID, ProfileInfo pInfo) {
		boolean success = false;
		Connection conn = init();
		PreparedStatement prepStatement = null;
		
		if (conn != null) {
			try {
				String query = "UPDATE Users SET fname=?, lname=?, standing=?, major=? WHERE userID=?";
				prepStatement = conn.prepareStatement(query);
				conn.setAutoCommit(false);
				
				prepStatement.setString(1, pInfo.fname);
				prepStatement.setString(2, pInfo.lname);
				prepStatement.setString(3, pInfo.standing);
				prepStatement.setString(4,  pInfo.major);
				prepStatement.setInt(5, userID);
				prepStatement.execute();
				
				// Change courses
				changeCourses(conn, userID, pInfo.courses);
				// Change availability
				changeAvailability(conn, userID, pInfo.availability);
				
				success = true;
				conn.commit();
			} catch (SQLException e) {
				System.out.println("SQLException for changeProfile()");
				e.printStackTrace();
				success = false;
				try {
					if (conn != null) {
						conn.rollback();
					}
				} catch(SQLException rbe) {
					System.out.println("Failed to rollback the transaction: " + rbe.getMessage());
				}
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException sqle) {
					System.out.println("Could not reset to autocommit mode: " + sqle.getMessage());
				}
				close(conn, prepStatement, null);				
			}
		}
		return success;
	}
	
	public static void changeAvailability(Connection conn, int userID, ArrayList<Availability> availability) throws SQLException {
		// first delete all the rows in Availability for the userID 
		String deleteQuery = "DELETE FROM Availability WHERE userID=?";
		PreparedStatement prepStatement = conn.prepareStatement(deleteQuery);
        prepStatement.setInt(1, userID);
        prepStatement.executeUpdate();

        
        // now re-insert the availability of the userID accordingly to the parameter input
        String insertQuery = "INSERT INTO Availability (userID, dayOfWeek, startHour, endHour) VALUES (?, ?, ?, ?)";
        prepStatement = conn.prepareStatement(insertQuery);
        
        for (Availability time : availability) {
        	prepStatement.setInt(1, userID);
        	prepStatement.setString(2, time.dayOfWeek);
        	prepStatement.setInt(3, time.startHour);
        	prepStatement.setInt(4, time.endHour);
        	prepStatement.addBatch();
        }
        
		prepStatement.executeBatch();
		close(null, prepStatement, null);
	}
 	
 	public static void changeCourses(Connection conn, int userID, ArrayList<Course> courses) throws SQLException {
		// first delete all the rows in CourseTutors for the userID 
		String deleteQuery = "DELETE FROM CourseTutors WHERE userID=?";
		PreparedStatement prepStatement = conn.prepareStatement(deleteQuery);
        prepStatement.setInt(1, userID);
        prepStatement.executeUpdate();

        
        // now re-insert courses for the userID
        String insertQuery = "INSERT INTO CourseTutors (userID, courseID) VALUES (?, ?)";
        prepStatement = conn.prepareStatement(insertQuery);
        
        for (Course course : courses) {
        	prepStatement.setInt(1, userID);
        	prepStatement.setInt(2, course.id);
        	prepStatement.addBatch();
        }
        
		prepStatement.executeBatch();
		close(null, prepStatement, null);
	}
 	
	public static ProfileInfo getProfile(int userID) {
		ProfileInfo info = null;
		Connection conn = init();
		PreparedStatement prepStatement = null;
		
		if (conn != null) {
			try {
				prepStatement = conn.prepareStatement(
						"SELECT fname, lname, standing, major FROM Users WHERE userID=?");
				prepStatement.setInt(1, userID);
				ResultSet results = prepStatement.executeQuery();
				if (results.next()) {
					String fname = results.getString("fname");
					String lname = results.getString("lname");
					String standing = results.getString("standing");
					String major = results.getString("major");
					ArrayList<Availability> availability = DBHandler.availabilityForTutor(conn, userID);
		            ArrayList<Course> courses = DBHandler.coursesForTutor(conn, userID);
					info = new ProfileInfo(userID, fname, lname, standing, major, courses, availability);
				}
			} catch (SQLException sqle) {
				System.out.println("SQLException thrown in getProfile: " + sqle);
			} finally {
				close(conn, prepStatement, null);
			}
		}
		return info;
	}
}

class TutorInfo {
	public int id;
	public String fname;
	public String lname;
	public String standing;
	public String major;

	public TutorInfo(int id, String fname, String lname, String standing, String major) {
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.standing = standing;
		this.major = major;
	}
}

class Conversation {
	public int id;
	public int tutorID;
	public int studentID;
	public String otherFname;
	public String otherLname;

	public Conversation(int id, int tutorID, int studentID, String otherFname, String otherLname) {
		this.id = id;
		this.tutorID = tutorID;
		this.studentID = studentID;
		this.otherFname = otherFname;
		this.otherLname = otherLname;
	}
}

class Message {
	public int id;
	public int chatID;
	public boolean sentByTutor;
	public String timestamp;
	public String content;

	public Message(int id, int chatID, boolean sentByTutor, String timestamp, String content) {
		this.id = id;
		this.chatID = chatID;
		this.sentByTutor = sentByTutor;
		this.timestamp = timestamp;
		this.content = content;
	}
}

class ProfileInfo {
	public int id;
	public String fname;
	public String lname;
	public String standing;
	public String major;
	public ArrayList<Course> courses;
	public ArrayList<Availability> availability;
	
	public ProfileInfo(int id, String fname, String lname, String standing, String major,
			ArrayList<Course> courses, ArrayList<Availability> availability) {
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.standing = standing;
		this.major = major;
		this.courses = courses;
		this.availability = availability;
	}
}

class Course {
	public int id;
	public String code;
	public String name;

	public Course(int id, String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
}

class Availability {
	public String dayOfWeek;
	public int startHour;
	public int endHour;

	public Availability(String d, int s, int e) {
		dayOfWeek = d;
		startHour = s;
		endHour = e;
	}
}

class ChatHistory {
	public ArrayList<Conversation> tutorChats;
	public ArrayList<Conversation> tuteeChats;
	// Mapping of chatID to messages
	public Map<Integer, ArrayList<Message>> messages;
	public ChatHistory() {
		this.tutorChats = new ArrayList<Conversation>();
		this.tuteeChats = new ArrayList<Conversation>();
		this.messages = new HashMap<Integer, ArrayList<Message>>();
	}
}
