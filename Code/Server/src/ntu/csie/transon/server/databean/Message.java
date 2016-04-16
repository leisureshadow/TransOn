package ntu.csie.transon.server.databean;

import java.util.Date;

public class Message {
	private User speaker;
	private String message;
	private Date timestamp;
	
	public Message(User speaker , String message , Date timestamp)
	{
		this.speaker = speaker;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	public User getSpeaker() {
		return speaker;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
}
