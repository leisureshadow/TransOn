package ntu.csie.transon.server.connect;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.meetingroom.MeetingRoom;
import ntu.csie.transon.server.meetingroom.Participant;

public class MeetingServiceHandler extends ServiceHandler {

	private MeetingRoom meetingRoom;
	private Participant participant;
	public MeetingServiceHandler(SocketChannel _handle, MeetingRoom _meetingRoom) {
		super(_handle);
		meetingRoom = _meetingRoom;
	}

	@Override
	public void open() {
		Reactor reactor = Reactor.getInstance();
		reactor.registerHandler(this, SelectionKey.OP_READ);
	}
	
	public void setParticipant(Participant _participant){
		this.participant = _participant;
	}
	
	public Participant getParticipant(){
		return this.participant;
	}
	
	public MeetingRoom getMeetingRoom(){
		return meetingRoom;
	}

	@Override
	public void endService() {
		Reactor.getInstance().cancelRegister(this);
		meetingRoom.leave(participant.getUser().getId());
	}

}
