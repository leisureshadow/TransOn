package ntu.csie.transon.server.databean;

public class User {
	private String id, name, googleAccount;

	public User(String id, String name, String googleAccount) {
		this.id = id;
		this.name = name;
		this.googleAccount = googleAccount;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getGoogleAccount() {
		return googleAccount;
	}
	
	@Override
	public boolean equals(Object other){
		if (other == null || !(other instanceof User))
			return false;
		else
			return  ((User)other).getId().equals(id);
	}
	
	@Override
	public int hashCode(){
		return this.id.hashCode();
	}
}
