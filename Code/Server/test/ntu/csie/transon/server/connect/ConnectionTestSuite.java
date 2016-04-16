package ntu.csie.transon.server.connect;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value={
	AcceptorTest.class,
	ClientServiceHandlerTest.class,
	HistoryServiceHandlerTest.class,
	MeetingServiceHandlerTest.class,
	ClientServiceHandlerFactoryTest.class,
	HistoryServiceHandlerFactoryTest.class,
	MeetingServiceHandlerFactoryTest.class,
	ServiceHandlerTest.class,
	ReactorTest.class
})
public class ConnectionTestSuite {
}
