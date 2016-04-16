package ntu.csie.transon.server.connect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SelectionKey;

public class Acceptor extends EventHandler{
	
	static final int MAX_LISTEN_BUFFER = 100;
	
	private ServiceHandlerFactory factory;
	private Reactor reactor;
	
	public Acceptor(ServiceHandlerFactory _factory){
		factory = _factory;
		reactor = Reactor.getInstance();
	}
	
	@Override
	public void handle() {
		try {
			SocketChannel serviceChannel = ((ServerSocketChannel)handle).accept();
			serviceChannel.configureBlocking(false);
			System.out.println("New connection request arrived: " + serviceChannel.getRemoteAddress());
			ServiceHandler serviceHandler = factory.createServiceHandler(serviceChannel);
			serviceHandler.open();
		} catch (IOException e) {
			System.err.println("Unabled to accept the connection!");
			e.printStackTrace();
		}
	}

	public void open(){
		this.open(new InetSocketAddress("0.0.0.0",0));
	}
	
	public void open(SocketAddress address) {
		try {
			System.out.println("Try listen on " + address);
			handle = ServerSocketChannel.open();
			((ServerSocketChannel)handle).bind(address,MAX_LISTEN_BUFFER);
			handle.configureBlocking(false);
			reactor.registerHandler(this,SelectionKey.OP_ACCEPT);
			System.out.println("Succeed!!");
		} catch (IOException e) {
			System.err.println("Unabled to open handle or bind address");
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			handle.close();
		} catch (IOException e) {
			System.err.println("Failed to close the socket");
			e.printStackTrace();
		}
	}
	
	public int getPort(){
		return ((ServerSocketChannel)handle).socket().getLocalPort();
	}
}
