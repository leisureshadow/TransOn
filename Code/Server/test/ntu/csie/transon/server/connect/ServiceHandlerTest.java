package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.connect.packet.PacketHandler;

import org.junit.Test;

import com.google.gson.Gson;

public class ServiceHandlerTest {
	class TmpServiceHandler extends ServiceHandler{
		public TmpServiceHandler(SocketChannel channel){
			super(channel);
		}

		@Override
		public void open() {
			try {
				handle.configureBlocking(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void endService() {
			// TODO Auto-generated method stub
			
		}
	}
	class FakePacketHandler extends PacketHandler{

		public FakePacketHandler(ServiceHandler _serviceHandler) {
			super(_serviceHandler);
		}

		@Override
		protected boolean isResponsible(Packet packet) {
			return true;
		}

		@Override
		protected void handle(Packet packet) {
			assertEquals("The command should be test123","test123",packet.getCommand());
			Packet returnPacket = new Packet("haha123");
			serviceHandler.sendCommand(returnPacket);
			return;
		}
		
	}
	
	private int testingPort = 54352;
	@Test
	public void testHandleAndSend() {
		ServerSocketChannel serverChannel;
		SocketChannel channel = null;
		try {
			serverChannel= ServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress("0.0.0.0",testingPort));
			virtualClient();
			channel = serverChannel.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServiceHandler serviceHandler = new TmpServiceHandler(channel);
		serviceHandler.open();
		PacketHandler packetHandler = new FakePacketHandler(serviceHandler);
		serviceHandler.setPacketHandler(packetHandler);
		serviceHandler.handle();
		serviceHandler.close();
	}
	
	//Try to mimic connection
	private void virtualClient(){
		Thread tryToConnect = new Thread(){
			@Override
			public void run(){
				SocketChannel channel = null;
				try {
					//open
					channel = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(),testingPort));
					//write
					Packet packet = new Packet("test123");
					Gson gson = new Gson();
					String testPacket = gson.toJson(packet);
					ByteBuffer buffer = ByteBuffer.allocate(4+testPacket.getBytes().length);
					buffer.clear();
					buffer.putInt(testPacket.getBytes().length);
					buffer.put(testPacket.getBytes());
					buffer.flip();
					channel.write(buffer);
					//read
					buffer = ByteBuffer.allocate(4);
					buffer.clear();
					String data = null;
					channel.read(buffer);
					buffer.rewind();
					int length = buffer.getInt();
					buffer = ByteBuffer.allocate(length);
					buffer.clear();
					channel.read(buffer);
					data = new String(buffer.array());

					assertEquals("The return string should be {\"command\":\"haha123\",\"items\":{}}","{\"command\":\"haha123\",\"items\":{}}",data);
					Thread.sleep(1000);
					//close
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
