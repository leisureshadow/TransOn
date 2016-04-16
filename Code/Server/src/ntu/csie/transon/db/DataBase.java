package ntu.csie.transon.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.Message;
import ntu.csie.transon.server.databean.User;

public class DataBase {

	// Server information which database using mySQL
	private static final String serverIP = "140.112.90.151";
	private static final String mysqlPort = ":3306";
	private static final String databaseName = "SE103_project";
	private static final String databaseAccount = "transon";
	private static final String databasePassword = "se103team4";
//	private static final String databaseAccount = "root";
//	private static final String databasePassword = "selabselab";

	// Database implementation
	private static Connection databaseConnection = null;
	private static Statement start = null;

	// Load jdbc library and connect database
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createDataBase(String dbName){
		String[] command = new String[]{
		"SET SQL_MODE = \"NO_AUTO_VALUE_ON_ZERO\";",
		"SET time_zone = \"+00:00\";",
		"CREATE DATABASE IF NOT EXISTS `"+dbName+"` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;",
		"USE `"+dbName+"`;",
		"CREATE TABLE IF NOT EXISTS `Meeting` (  `meeting_id` varchar(100) CHARACTER SET utf8 NOT NULL,  `initiator` varchar(100) CHARACTER SET utf8 DEFAULT NULL,  `subject` varchar(100) CHARACTER SET utf8 DEFAULT NULL, `description` varchar(100) CHARACTER SET utf8 DEFAULT NULL,  `location` varchar(100) CHARACTER SET utf8 DEFAULT NULL,  `lattitude` float NOT NULL,  `longitude` float NOT NULL,  `start_time` datetime NOT NULL,  `end_time` datetime DEFAULT NULL,  `private` tinyint(1) DEFAULT NULL,  `secret` tinyint(1) DEFAULT NULL,  `pause` tinyint(1) DEFAULT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;",
		"CREATE TABLE IF NOT EXISTS `Message` (  `id` int(11) NOT NULL,  `meeting_id` varchar(100) CHARACTER SET utf8 NOT NULL,  `speaker_id` varchar(100) CHARACTER SET utf8 NOT NULL,  `message` text CHARACTER SET utf8 NOT NULL,  `timestamp` datetime NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;",
		"CREATE TABLE IF NOT EXISTS `Relationship` (  `id` int(11) NOT NULL,  `meeting_id` varchar(100) CHARACTER SET utf8 NOT NULL,  `user_id` varchar(100) CHARACTER SET utf8 NOT NULL,  `type` int(11) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;",
		"CREATE TABLE IF NOT EXISTS `User` (  `user_id` varchar(100) CHARACTER SET utf8 NOT NULL,  `name` varchar(100) CHARACTER SET utf8 NOT NULL,  `google_account` varchar(100) CHARACTER SET utf8 NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;",
		"ALTER TABLE `Meeting` ADD PRIMARY KEY (`meeting_id`);",
		"ALTER TABLE `Message` ADD PRIMARY KEY (`id`);",
		"ALTER TABLE `Relationship` ADD PRIMARY KEY (`id`);",
		"ALTER TABLE `User` ADD PRIMARY KEY (`user_id`);",
		"ALTER TABLE `Message` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;",
		"ALTER TABLE `Relationship` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;"};
		for(String tmp: command){
			try {
				start = databaseConnection.createStatement();
				start.executeUpdate(tmp);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				databaseClose();
			}
		}
	}
	
	public static void deleteDataBase(String dbName){
		try {
			start = databaseConnection.createStatement();
			start.executeUpdate("DROP DATABASE IF EXISTS `"+dbName +"`;");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
	}

	private static void getConnection() {
		try {
			// Sign up driver
			databaseConnection = DriverManager.getConnection("jdbc:mysql://" + serverIP + mysqlPort + "/" + databaseName + "?useUnicode=true&characterEncoding=Big5", databaseAccount, databasePassword);
			System.out.println("Connection...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void databaseClose() {
		try {
			if (start != null) {
				start.close();
				start = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Disconnect...");
	}

	public static void updateMeetingInformation(MeetingInformation meeting) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("UPDATE `Meeting` SET `initiator`=?,`subject`=?,`description`=?,`location`=?,`lattitude`=?," + "`longitude`=?,`start_time`=?,`end_time`=?,`private`=?,`secret`=?,`pause`=? WHERE `meeting_id`=?");
			preparedStatement.setString(1, meeting.getInitiator());
			preparedStatement.setString(2, meeting.getSubject());
			preparedStatement.setString(3, meeting.getDescription());
			preparedStatement.setString(4, meeting.getLocation());
			preparedStatement.setDouble(5, meeting.getLatitude());
			preparedStatement.setDouble(6, meeting.getLongitude());
			if (meeting.getStartTime() == null) {
				preparedStatement.setNull(7, Types.TIMESTAMP);
			} else {
				preparedStatement.setTimestamp(7, new Timestamp(meeting.getStartTime().getTime()));
			}
			if (meeting.getEndTime() == null) {
				preparedStatement.setNull(8, Types.TIMESTAMP);
			} else {
				preparedStatement.setTimestamp(8, new Timestamp(meeting.getEndTime().getTime()));
			}
			preparedStatement.setBoolean(9, meeting.is_private());
			preparedStatement.setBoolean(10, meeting.isSecret());
			preparedStatement.setBoolean(11, meeting.isPause());
			preparedStatement.setString(12, meeting.getMeetingID());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ArrayList<String> getUserListFromRelationship(String meetingID, String type) {
		ArrayList<String> uidList = new ArrayList<String>();
		String command = "SELECT user_id FROM `Relationship` WHERE `meeting_id` = '" + meetingID + "' and `type`='" + type + "'";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			while (resultSet.next()) {
				uidList.add(resultSet.getString("user_id"));
			}
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return uidList;
	}

	public static MeetingInformation getMeetingInformation(String meetingID) {
		String command = "SELECT * FROM `Meeting` WHERE `meeting_id` = '" + meetingID + "' ";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			if(!resultSet.next())
			{
				return null;
			}
			Date startTime = null;
			Date endTime = null;
			if (resultSet.getTimestamp("start_time") != null) {
				startTime = new Date(resultSet.getTimestamp("start_time").getTime());
			}
			if (resultSet.getTimestamp("end_time") != null) {
				endTime = new Date(resultSet.getTimestamp("end_time").getTime());
			}
			MeetingInformation meeting = new MeetingInformation(resultSet.getString("meeting_id"), resultSet.getString("subject"), resultSet.getString("location"), startTime, endTime, resultSet.getString("description"), resultSet.getString("initiator"), resultSet.getDouble("lattitude"), resultSet.getDouble("longitude"), resultSet.getBoolean("private"), resultSet.getBoolean("secret"), resultSet.getBoolean("pause"));
			resultSet.close();
			meeting.setStartTime(startTime);
			meeting.setEndTime(endTime);
			for (String uid : getUserListFromRelationship(meetingID, "1")) {
				meeting.restoreParticipant(uid);
			}
			for (String uid : getUserListFromRelationship(meetingID, "2")) {
				meeting.restoreToReadablelist(uid);
			}
			for (String uid : getUserListFromRelationship(meetingID, "3")) {
				meeting.restoreAdmin(uid);
			}
			for (String uid : getUserListFromRelationship(meetingID, "4")) {
				meeting.restoreToBlacklist(uid);
			}
			return meeting;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return null;
	}

	public static void createMeetingInformation(MeetingInformation meeting) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("INSERT INTO `Meeting`(`meeting_id`, `initiator`, `subject`, `description`, `location`, `lattitude`, `longitude`, `start_time`, `end_time`, `private`, `secret`, `pause`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, meeting.getMeetingID());
			preparedStatement.setString(2, meeting.getInitiator());
			preparedStatement.setString(3, meeting.getSubject());
			preparedStatement.setString(4, meeting.getDescription());
			preparedStatement.setString(5, meeting.getLocation());
			preparedStatement.setDouble(6, meeting.getLatitude());
			preparedStatement.setDouble(7, meeting.getLongitude());
			preparedStatement.setTimestamp(8, new Timestamp(meeting.getStartTime().getTime()));
			preparedStatement.setNull(9, Types.TIMESTAMP);
			preparedStatement.setBoolean(10, meeting.is_private());
			preparedStatement.setBoolean(11, meeting.isSecret());
			preparedStatement.setBoolean(12, meeting.isPause());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateUserName(User user) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("UPDATE `User` SET `name`=? WHERE `user_id` = ?");
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getId());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static User getUserInformation(String userID) {
		String command = "SELECT * FROM User WHERE user_id = '" + userID + "' ";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			if (!resultSet.next()){
				return null;
			}
			User user = new User(resultSet.getString("user_id"), resultSet.getString("name"), resultSet.getString("google_account"));
			resultSet.close();
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return null;
	}

	public static User getUserInformationByGoogleAccount(String googleAccount) {
		String command = "SELECT * FROM User WHERE google_account = '" + googleAccount + "' ";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			if (!resultSet.next()){
				return null;
			}
			User user = new User(resultSet.getString("user_id"), resultSet.getString("name"), resultSet.getString("google_account"));
			resultSet.close();
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return null;
	}

	public static void createUserInformation(User user) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("INSERT INTO User(user_id, name, google_account) VALUES (?,?,?)");
			preparedStatement.setString(1, user.getId());
			preparedStatement.setString(2, user.getName());
			preparedStatement.setString(3, user.getGoogleAccount());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveMessage(String meetingID, Message msg) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("insert into Message(meeting_id, speaker_id, message, timestamp) values(?,?,?,?)");
			preparedStatement.setString(1, meetingID);
			preparedStatement.setString(2, msg.getSpeaker().getId());
			preparedStatement.setString(3, msg.getMessage());
			preparedStatement.setTimestamp(4, new Timestamp(msg.getTimestamp().getTime()));
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addRelationshipTable(String meetingID, String userID, String type) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("insert into Relationship(user_id,meeting_id,type) values(?,?,?)");
			preparedStatement.setString(1, userID);
			preparedStatement.setString(2, meetingID);
			preparedStatement.setString(3, type);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
	}

	public static void addUserToParticipant(String meetingID, String userID) {
		
		addRelationshipTable(meetingID, userID, "1");
	}

	public static void addUserToReadablelist(String meetingID, String userID) {
		addRelationshipTable(meetingID, userID, "2");
	}

	public static void addUserToAdminlist(String meetingID, String userID) {
		addRelationshipTable(meetingID, userID, "3");
	}

	public static void addUserToBlacklist(String meetingID, String userID) {
		addRelationshipTable(meetingID, userID, "4");
	}

	private static void deleteRelationshipTable(String meetingID, String userID, String type) {
		try {
			PreparedStatement preparedStatement = databaseConnection.prepareStatement("DELETE FROM Relationship WHERE user_id = ? and meeting_id = ? and type = ?");
			preparedStatement.setString(1, meetingID);
			preparedStatement.setString(2, userID);
			preparedStatement.setString(3, type);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
	}

	public static void deleteUserToParticipant(String meetingID, String userID) {
		deleteRelationshipTable(meetingID, userID, "1");
	}

	public static void deleteUserToReadablelist(String meetingID, String userID) {
		deleteRelationshipTable(meetingID, userID, "2");
	}

	public static void deleteUserToAdminlist(String meetingID, String userID) {
		deleteRelationshipTable(meetingID, userID, "3");
	}

	public static void deleteUserToBlacklist(String meetingID, String userID) {
		deleteRelationshipTable(meetingID, userID, "4");
	}

	public static ArrayList<Message> getHistory(String meetingID, int number, Date endTime) {
		String command = "SELECT * FROM `Message` WHERE meeting_id='" + meetingID + "' and timestamp<='" + new java.sql.Timestamp(endTime.getTime()) + "' ORDER BY timestamp DESC LIMIT " + number + " ";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			ArrayList<Message> msg_list = new ArrayList<Message>();
			while (resultSet.next()) {
				msg_list.add(new Message(getUserInformation(resultSet.getString("speaker_id")), resultSet.getString("message"), resultSet.getTimestamp("timestamp")));
			}
			resultSet.close();
			return msg_list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return null;
	}

	public static ArrayList<MeetingInformation> getReadableHistoryList(String userID) {
		ArrayList<MeetingInformation> meetingList = new ArrayList<MeetingInformation>();
		String command = "SELECT meeting_id FROM `Relationship` WHERE `user_id` = '" + userID + "' and `type`='2'";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			while (resultSet.next()) {
				meetingList.add(getMeetingInformation(resultSet.getString("meeting_id")));
			}
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return meetingList;
	}

	private static float distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371; // kilometers
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}

	public static ArrayList<MeetingInformation> getAllMeeting(){
		ArrayList<MeetingInformation> meetingList = new ArrayList<MeetingInformation>();
		String command = "SELECT * FROM `Meeting` WHERE end_time is null";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			while (resultSet.next()) {
				meetingList.add(getMeetingInformation(resultSet.getString("meeting_id")));
			}
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return meetingList;
	}
	
	public static ArrayList<MeetingInformation> getNearbyMeeting(double latitude, double longitude) {
		ArrayList<MeetingInformation> meetingList = new ArrayList<MeetingInformation>();
		String command = "SELECT * FROM `Meeting` WHERE end_time is null and secret is False";
		try {
			start = databaseConnection.createStatement();
			ResultSet resultSet = start.executeQuery(command);
			while (resultSet.next()) {
				if (distFrom(latitude, longitude, resultSet.getDouble("lattitude"), resultSet.getDouble("longitude"))<10000)
				{
					Date startTime = null;
					Date endTime = null;
					if (resultSet.getTimestamp("start_time") != null) {
						startTime = new Date(resultSet.getTimestamp("start_time").getTime());
					}
					if (resultSet.getTimestamp("end_time") != null) {
						endTime = new Date(resultSet.getTimestamp("end_time").getTime());
					}
					meetingList.add(new MeetingInformation(resultSet.getString("meeting_id"), resultSet.getString("subject"), 
							resultSet.getString("location"), startTime, endTime, resultSet.getString("description"), 
							resultSet.getString("initiator"), resultSet.getDouble("lattitude"), resultSet.getDouble("longitude"), 
							resultSet.getBoolean("private"), resultSet.getBoolean("secret"), resultSet.getBoolean("pause")));
				}
			}
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			databaseClose();
		}
		return meetingList;
	}

	public static void main(String[] args) {
		// DataBase.addUserToParticipant("fuck", "meetingID");
		// DataBase.addUserToReadablelist("fuck1", "meetingID~~");
		// DataBase.addUserToAdminlist("fuck2", "meetingID");
		// DataBase.addUserToBlacklist("fuck3", "meetingID");
		// DataBase.addUserToParticipant("fuck--", "meetingID");
		// DataBase.addUserToReadablelist("fuck1--", "meetingID");
		// DataBase.addUserToAdminlist("fuck2--", "meetingID");
		// DataBase.addUserToBlacklist("fuck3--", "meetingID");
		// DataBase.deleteUserToParticipant("fuck", "fuck");
		// DataBase.deleteUserToReadablelist("fuck1", "fuck");
		// DataBase.deleteUserToAdminlist("fuck2", "fuck");
		// DataBase.deleteUserToBlacklist("fuck3", "fuck");
		// DataBase.saveMessage("what the fuck", new Message(new User("fuck-c",
		// "fuck2", "fuck3"), "fuck-c", new Date()));
		// DataBase.createUserInformation(new User("fuck", "fuck2", "fuck3"));
		// DataBase.createUserInformation(new User("fuck2", "fuck2", "fuck3"));
		// DataBase.createUserInformation(new User("fuck3", "fuck2", "fuck3"));
		// DataBase.createUserInformation(new User("fuck--", "fuck2", "fuck3"));
		// DataBase.createUserInformation(new User("fuck1--", "fuck2",
		// "fuck3"));
		// DataBase.createUserInformation(new User("fuck2--", "fuck2",
		// "fuck3"));
		// DataBase.createUserInformation(new User("fuck3--", "fuck2",
		// "fuck3"));
		// User fuck = DataBase.getUserInformation("fuck1");
		// System.out.println(fuck.getUserid()+fuck.getName()+fuck.getGoogleAccount());
		// MeetingInformation meeting = new
		// MeetingInformation("meetingID~~","subject","location",new Date(),new
		// Date(),"description", "initiator", 0.1, 0.2, true, false, true);
		// DataBase.createMeetingInformation(meeting);
		// MeetingInformation meet =
		// DataBase.getMeetingInformation("meetingID");
		// System.out.println(meet.getMeetingID());
		// System.out.println(meet.getSubject());
		// System.out.println(meet.getLocation());
		// System.out.println(meet.getStartTime());
		// System.out.println(meet.getEndTime());
		// System.out.println(meet.getDescription());
		// System.out.println(meet.getInitiator());
		// System.out.println(meet.getLatitude());
		// System.out.println(meet.getLongitude());
		// System.out.println(meet.is_private());
		// System.out.println(meet.isSecret());
		// System.out.println(meet.isPause());
		// MeetingInformation meeting = new MeetingInformation("meetingID",
		// null, "location3", new Date(), null, "description12", "initiator",
		// 0.1, 0.2, true, false, true);
		// DataBase.updateMeetingInformation(meeting);
		// DataBase.getHistory("what the fuck", 2, new Date());
		// getReadableHistoryList("fuck1");
//		DataBase.createDataBase("qaqaq");
//		DataBase.addUserToParticipant("fuck", "meetingID");
	}
}
