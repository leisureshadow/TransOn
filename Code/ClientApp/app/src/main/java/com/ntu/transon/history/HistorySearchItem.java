package com.ntu.transon.history;

import java.util.Date;
import java.util.Locale;

public class HistorySearchItem implements java.io.Serializable {

	private String id;
    private String subject;
    private String location;



	public HistorySearchItem() {
		id = "";
		subject = "";
        location = "";
	}
	
	public HistorySearchItem(String id, String subject, String location) {
		this.id = id;
		this.subject = subject;
        this.location = location;
	}





	public String getId() {
		return id;
	}

	public void setId(String title) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

    public String getlocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

}
