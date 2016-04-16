package ntu.csie.transon.db;

public class DataBaseCommand {
		
		//User and Meeting Relationship table information and implementation
		public static String relationshipDeleteTable = "DROP TABLE Relationship ";
		public static String relationshipCreateTable = "CREATE TABLE Relationship (" +
				" number INTEGER NOT NULL" +
				" , user_id VARCHAR(255) NOT NULL"  +
				" , meeting_id VARCHAR(255) NOT NULL" +
				" , type INTEGER NOT NULL" +
				" , PRIMARY KEY (user_id, meeting_id)" +
				" , FOREIGN KEY (user_id) REFERENCES User (user_id)" +
				" , FOREIGN KEY (meeting_id) REFERENCES Meeting (meeting_id) )" +
				" ENGINE=INNODB";
		public static String databaseInsertRelationshipTable = "insert into Relationship(number,user_id,meeting_id,type) " +
				"select ifNULL(max(number),0)+1,?,?,? FROM Relationship";
		
		
		//User table information and implementation
		public static String userDeleteTable = "DROP TABLE User ";
		public static String userCreateTable = "CREATE TABLE User (" +
				" number INTEGER NOT NULL" +
				" , user_id VARCHAR(255) NOT NULL PRIMARY KEY" +
				" , name VARCHAR(255) NOT NULL" +
				" , nick_name VARCHAR(255) NOT NULL" +
				" , google_account VARCHAR(255) NOT NULL" +
				" , user_icon VARBINARY(255) NOT NULL)" +
				" ENGINE=INNODB";
		public static String databaseInsertUserTable = "insert into User(number,user_id,name,nick_name,google_account,user_icon) " +
				"select ifNULL(max(number),0)+1,?,?,?,?,? FROM User";
		
		
		//Meeting table information and implementation
		public static String meetingDeleteTable = "DROP TABLE Meeting ";
		public static String meetingCreateTable = "CREATE TABLE Meeting (" +
				" number INTEGER NOT NULL" +
				" , meeting_id VARCHAR(255) NOT NULL PRIMARY KEY" +
				" , initiator_id VARCHAR(255) NOT NULL" +
				" , subject VARCHAR(255) NOT NULL" +
				" , description VARCHAR(255) NOT NULL" +
				" , location VARBINARY(255) NOT NULL" +
				" , lattitude DOUBLE NOT NULL" +
				" , longitude DOUBLE NOT NULL" +
				" , initiate_time TIMESTAMP NOT NULL" +
				" , terminate_time TIMESTAMP NOT NULL" +
				" , _private BOOLEAN NOT NULL" +
				" , secret BOOLEAN NOT NULL" +
				" , terminate BOOLEAN NOT NULL)" + 
				" ENGINE=INNODB";
		public static String databaseInsertMeetingTable = "insert into Meeting(number,meeting_id,initiator_id,subject,description,location"
				+ ",lattitude,longitude,initiate_time,terminate_time,_private,secret,terminate) " +
				"select ifNULL(max(number),0)+1,?,?,?,?,?,?,?,?,?,?,?,? FROM Meeting";
		
		
		//User table information and implementation
		public static String messageDeleteTable = "DROP TABLE Message ";
		public static String messageCreateTable = "CREATE TABLE Message (" +
				" number INTEGER NOT NULL" +
				" , message_id VARCHAR(255) NOT NULL" +
				" , meeting_id VARCHAR(255) NOT NULL" +
				" , speaker_id VARCHAR(255) NOT NULL" +
				" , message VARCHAR(255) NOT NULL" +
				" , _timestamp TIMESTAMP NOT NULL)" +
				" ENGINE=INNODB";
		public static String databaseInsertMessageTable = "insert into Message(number,message_id,meeting_id,speaker_id,message,_timestamp) " +
				"select ifNULL(max(number),0)+1,?,?,?,?,? FROM Message";

}
