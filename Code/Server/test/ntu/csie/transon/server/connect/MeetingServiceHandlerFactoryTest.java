package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;
import ntu.csie.transon.server.connect.packet.PacketHandler;

import org.junit.Test;

public class MeetingServiceHandlerFactoryTest {

	@Test
	public void testCreateServiceHandler() {
		MeetingServiceHandlerFactory factory = new MeetingServiceHandlerFactory(null);
		ServiceHandler sh = factory.createServiceHandler(null);
		assertTrue("The returned service handler should be in MeetingSerivceHandler type",sh instanceof MeetingServiceHandler);
	}

	@Test
	public void testCreatePacketHandlerChain() {
		MeetingServiceHandlerFactory factory = new MeetingServiceHandlerFactory(null);
		PacketHandler handler = factory.createPacketHandlerChain(null);
		assertNotNull("The handler should not be null",handler);
	}

}
