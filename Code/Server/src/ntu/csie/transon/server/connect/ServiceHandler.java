package ntu.csie.transon.server.connect;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.google.gson.Gson;

import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.connect.packet.PacketHandler;

public abstract class ServiceHandler extends EventHandler{
	protected PacketHandler packetHandler;
	public ServiceHandler(SocketChannel _handle){
		handle = _handle;
	}
	public abstract void open();
	public void close(){
		try {
			handle.close();
		} catch (IOException e) {
			System.err.println("Failed to close channel");
			e.printStackTrace();
		}
	}
	@Override
	public void handle(){
		String packetString;
		try {
			packetString = readPacket();
			System.out.println("Data received: " + packetString);
		} catch (IOException e1) {
			System.err.println("Failed to reading data, remote socket might be closed.");
			System.err.println("Remove channel from reactor");
			Reactor.getInstance().cancelRegister(this);
			return;
		}
		Gson gson = new Gson();
		Packet inputPacket = null;
		try {
			inputPacket = gson.fromJson(packetString,Packet.class);
			if(inputPacket == null){
				System.err.println("This is a blank packet.");
				return;
			}
			packetHandler.handlePacket(inputPacket);
		} catch (NoHandlerSupportedException e) {
			System.err.println("No handler for this command: " + inputPacket.getCommand());
			e.printStackTrace();
		}
	}
	public void sendCommand(Packet packet){
		Gson gson = new Gson();
		String stringToSend = gson.toJson(packet);
		//Write to remote socket
		try {
			System.out.println("Data sent: " + stringToSend);
			writePacket(stringToSend);
		} catch (IOException e) {
			System.err.println("Failed to writing data, remote socket might be closed.");
			System.err.println("Remove channel from reactor");
			Reactor.getInstance().cancelRegister(this);
			return;
		}
	}
	
	public abstract void endService();
	
	public void setPacketHandler(PacketHandler _packetHandler){
		packetHandler = _packetHandler;
	}

	protected String readPacket() throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.clear();
		String data = null;
		int bytes = ((SocketChannel)handle).read(buffer);
		if(bytes < 0){
			System.err.println("The stream has reach the end-of-stream.");
			Reactor.getInstance().cancelRegister(this);
			throw new IOException();
		}
		buffer.rewind();
		int length = buffer.getInt();
		buffer = ByteBuffer.allocate(length);
		buffer.clear();
		((SocketChannel)handle).read(buffer);
		data = new String(buffer.array());
		return data;
	}
	
	protected void writePacket(String returnData) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(4+returnData.getBytes().length);
		buffer.clear();
		buffer.putInt(returnData.getBytes().length);
		buffer.put(returnData.getBytes());
		buffer.flip();
		((SocketChannel)handle).write(buffer);
	}
}
