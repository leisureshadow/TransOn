package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.connect.packet.PacketHandler;

import org.junit.Test;

import com.google.gson.Gson;

public class ReactorTest {
	private String testCommand = "testPacketCommand"; 
	class FakePacketHandler extends PacketHandler{
		public FakePacketHandler(ServiceHandler _serviceHandler) {
			super(_serviceHandler);
		}
		@Override
		protected boolean isResponsible(Packet packet) {
			return packet.getCommand().compareTo(testCommand) == 0;
		}
		@Override
		protected void handle(Packet packet) {
			Packet returnPacket = new Packet("HahaCommand");
			serviceHandler.sendCommand(returnPacket);
		}
	}
	class FakeServiceHandler extends ServiceHandler{
		public FakeServiceHandler(SocketChannel _handle) {
			super(_handle);
		}
		@Override
		public void open() {
			Reactor reactor = Reactor.getInstance();
			reactor.registerHandler(this, SelectionKey.OP_READ);
		}
		@Override
		public void endService() {
			// TODO Auto-generated method stub
			
		}
	}
	class FakeServiceHandlerFactory implements ServiceHandlerFactory{
		@Override
		public ServiceHandler createServiceHandler(SocketChannel handle) {
			FakeServiceHandler serviceHandler = new FakeServiceHandler(handle);
			PacketHandler packetHandler = createPacketHandlerChain(serviceHandler);
			serviceHandler.setPacketHandler(packetHandler);
			return serviceHandler;
		}
		@Override
		public PacketHandler createPacketHandlerChain(ServiceHandler serviceHandler) {
			PacketHandler fake = new FakePacketHandler(serviceHandler);
			return fake;
		}
	}
	
	@Test
	public void testGetInstance() {
		Reactor reactor = Reactor.getInstance();
		assertNotNull("The result of getInstance should not be null",reactor);
		assertEquals("The result of two getInstance should be the same",reactor,Reactor.getInstance());
	}


	private int testingPort = 54752;
	@Test
	public void testRun() {
		Reactor reactor = Reactor.getInstance();
		Acceptor acceptor = new Acceptor(new FakeServiceHandlerFactory());
		acceptor.open(new InetSocketAddress("0.0.0.0",testingPort));
		reactor.registerHandler(acceptor, SelectionKey.OP_ACCEPT);
		runReactor(reactor); //run
		
		virtualClient(10);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reactor.cancelRegister(acceptor);
		reactor.join();
		return;
	}
	private void runReactor(Reactor _reactor){
		final Reactor reactor = _reactor;
		Thread runThread = new Thread(){
			@Override
			public void run(){
				reactor.run();
			}
		};
		runThread.start();
	}

	//Try to mimic connection
	private void virtualClient(int numOfConnection){
		final int connectionTimes = numOfConnection;
		Thread tryToConnect = new Thread(){
			@Override
			public void run(){
				for(int i = 0;i < connectionTimes;i++){
					Thread virtualClient = new Thread(){
						@Override
						public void run(){
							SocketChannel channel = null;
							try {
								//open
								channel = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(),testingPort));
								//write
								Packet packet = new Packet(testCommand);
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
								assertEquals("The return string should be {\"command\":\"HahaCommand\",\"items\":{}}","{\"command\":\"HahaCommand\",\"items\":{}}",data);
								Thread.sleep(500);
								//close
								channel.close();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}		
						}
					};
					virtualClient.start();
				}
			}
		};
		tryToConnect.start();
	}
}
