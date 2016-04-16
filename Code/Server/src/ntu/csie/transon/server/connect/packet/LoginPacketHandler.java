package ntu.csie.transon.server.connect.packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ntu.csie.transon.db.DataBase;
import ntu.csie.transon.server.connect.ClientServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.User;

public class LoginPacketHandler extends PacketHandler {

	public LoginPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("Login") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		ClientServiceHandler clientSH = (ClientServiceHandler)serviceHandler;
		String accessToken = (String)packet.getItem("accessToken",String.class);
		User user; 
		String data = new String();
		try {
			URL googleSSO = new URL("https://www.googleapis.com/plus/v1/people/me?access_token=" + accessToken);
			URLConnection conn = googleSSO.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String tmp;
			while((tmp = br.readLine()) != null){
				data += tmp;
			}
			br.close();
		} catch (MalformedURLException e) {
			System.err.println("URL is wrong");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Connection failed.");
			e.printStackTrace();
		} 
		Gson gson = new Gson();
		JsonObject jsonObj = gson.fromJson(data, JsonObject.class);
		//getting google account
		JsonArray emails = jsonObj.getAsJsonArray("emails");
		JsonObject primaryEmail = (JsonObject)emails.get(0);
		String googleAccount = primaryEmail.get("value").getAsString();
		//getting name
		String name = jsonObj.get("displayName").getAsString();
		
		user = DataBase.getUserInformationByGoogleAccount(googleAccount);
		if(user == null){
			//new user
			//generating userId
			String userId = UUID.randomUUID().toString();
			user = new User(userId, name,googleAccount);
			DataBase.createUserInformation(user);
		}
		
		clientSH.setUser(user);
		
		Packet returnPacket = new Packet("Login");
		returnPacket.addItems("user", clientSH.getUser());
		clientSH.sendCommand(returnPacket);
	}

}
