package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;
import ntu.csie.transon.server.connect.packet.PacketHandler;

import org.junit.Test;

public class ClientServiceHandlerFactoryTest {

	@Test
	public void testCreateServiceHandler() {
		ClientServiceHandlerFactory factory = new ClientServiceHandlerFactory();
		ServiceHandler sh = factory.createServiceHandler(null);
		assertTrue("The returned service handler should be in ClientSerivceHandler type",sh instanceof ClientServiceHandler);
	}

	@Test
	public void testCreatePacketHandlerChain() {
		ClientServiceHandlerFactory factory = new ClientServiceHandlerFactory();
		PacketHandler handler = factory.createPacketHandlerChain(null);
		assertNotNull("The handler should not be null",handler);
	}

}
