package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.NoHandlerSupportedException;
import ntu.csie.transon.server.connect.ServiceHandler;


public abstract class PacketHandler {
	private PacketHandler nextPacketHandler;
	protected ServiceHandler serviceHandler;
	public PacketHandler(ServiceHandler _serviceHandler){
		nextPacketHandler = null;
		serviceHandler = _serviceHandler;
	}
	public void setNextPacketHandler(PacketHandler _nextPacketHandler){
		nextPacketHandler = _nextPacketHandler;
	}
	public void handlePacket(Packet packet) throws NoHandlerSupportedException{
		if(isResponsible(packet)){
			handle(packet);
		}else if(nextPacketHandler != null){
			nextPacketHandler.handlePacket(packet);
		}else{
			throw new NoHandlerSupportedException();
		}
	}
	protected abstract boolean isResponsible(Packet packet);
	protected abstract void handle(Packet packet);
}
