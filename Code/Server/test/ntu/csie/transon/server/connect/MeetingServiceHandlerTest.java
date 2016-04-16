package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.meetingroom.MeetingRoom;
import ntu.csie.transon.server.meetingroom.Participant;

import org.junit.Test;

public class MeetingServiceHandlerTest {

	@Test
	public void testGetMeetingRoom(){
		MeetingRoom meetingRoom = new MeetingRoom(null);
		MeetingServiceHandler meetingSH = new MeetingServiceHandler(null,meetingRoom);
		assertSame("Result of getHistoryRoom should equal to the one setting in previously.",meetingRoom,meetingSH.getMeetingRoom());
	}
	
	@Test
	public void testParticipantGetSet(){
		MeetingServiceHandler meetingSH = new MeetingServiceHandler(null,null);
		Participant participant = new Participant("123",meetingSH);
		meetingSH.setParticipant(participant);
		assertSame("The user get from client service handler should equals to the one the previous seting in",participant,meetingSH.getParticipant());
	}
	
	private int testHandlePort = 52823;
	@Test
	public void testOpen() throws InterruptedException {
		Acceptor acceptor = new Acceptor(new MeetingServiceHandlerFactory(null));
		acceptor.open(new InetSocketAddress("0.0.0.0",testHandlePort));
		virtualClient();
		Thread.sleep(1000);
		acceptor.handle();
		acceptor.close();
	}
	
	//Try to mimic connection
	private void virtualClient(){
		Thread tryToConnect = new Thread(){
			@Override
			public void run(){
				SocketChannel channel = null;
				try {
					channel = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(),testHandlePort));
					Thread.sleep(1000);
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		tryToConnect.start();
	}

}
