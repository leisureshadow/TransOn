package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;
import ntu.csie.transon.server.connect.packet.PacketHandler;

import org.junit.Test;

public class HistoryServiceHandlerFactoryTest {

	@Test
	public void testCreateServiceHandler() {
		HistoryServiceHandlerFactory factory = new HistoryServiceHandlerFactory(null);
		ServiceHandler sh = factory.createServiceHandler(null);
		assertTrue("The returned service handler should be in HistorySerivceHandler type",sh instanceof HistoryServiceHandler);
	}

	@Test
	public void testCreatePacketHandlerChain() {
		HistoryServiceHandlerFactory factory = new HistoryServiceHandlerFactory(null);
		PacketHandler handler = factory.createPacketHandlerChain(null);
		assertNotNull("The handler should not be null",handler);
	}

}
