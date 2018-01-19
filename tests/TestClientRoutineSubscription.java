package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import application.ClientOneTimeOrderController;
import application.ClientRoutineSubscriptionController;
import common.Params;
import common.TalkToServer;

class TestClientRoutineSubscription {
	
	boolean wait;
	public TestClientRoutineSubscription() {
		// init connection
		TalkToServer.getInstance(TestingConsts.host, TestingConsts.port);
	}
	
	@Test
	void testRegOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientRoutineSubscriptionController.handleClientRoutineSubscription("TestUser", "TestVehicle", "misgavParking", "01-06-2018", "10:00", "18:30", "TestEmail@gmail.com",  msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("OK") && new Params(msg).getParam("price").equals("20.0"));
		});
		int a = 0;
		while(wait) {
			a++;
			System.out.println("waiting");
		}
		
		// tear down
		TalkToServer.getInstance().sendAndWait(Params.getEmptyInstance().addParam("action", "removeUserAndVehicle").addParam("userID", "TestUser").addParam("vehicleID", "TestVehicle").toString(), msg2->{
			System.out.println("test response2:" + msg2);
			String status2 = new Params(msg2).getParam("status");
			assertTrue(status2.equals("OK"));
		});
		System.out.println("end test");
	}
	
	@Test
	void testInvalidEnterTimeOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientRoutineSubscriptionController.handleClientRoutineSubscription("TestUser", "TestVehicle", "misgavParking", "01-06-2018", "181:00", "18:30", "TestEmail@gmail.com",  msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("BAD"));
		});
		int a = 0;
		while(wait) {
			a++;
			System.out.println("waiting");
		}
		
		// tear down
		TalkToServer.getInstance().sendAndWait(Params.getEmptyInstance().addParam("action", "removeUserAndVehicle").addParam("userID", "TestUser").addParam("vehicleID", "TestVehicle").toString(), msg2->{
			System.out.println("test response2:" + msg2);
			String status2 = new Params(msg2).getParam("status");
			assertTrue(status2.equals("OK"));
		});
		System.out.println("end test");
	}

	@Test
	void testInvalidExitTimeOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientRoutineSubscriptionController.handleClientRoutineSubscription("TestUser", "TestVehicle", "misgavParking", "01-06-2018", "18:00", "181:30", "TestEmail@gmail.com",  msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("BAD"));
		});
		int a = 0;
		while(wait) {
			a++;
			System.out.println("waiting");
		}
		
		// tear down
		TalkToServer.getInstance().sendAndWait(Params.getEmptyInstance().addParam("action", "removeUserAndVehicle").addParam("userID", "TestUser").addParam("vehicleID", "TestVehicle").toString(), msg2->{
			System.out.println("test response2:" + msg2);
			String status2 = new Params(msg2).getParam("status");
			assertTrue(status2.equals("OK"));
		});
		System.out.println("end test");
	}
	
	
}
