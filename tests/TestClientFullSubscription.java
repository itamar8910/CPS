package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import application.ClientFullSubscriptionController;
import application.ClientRoutineSubscriptionController;
import common.Params;
import common.TalkToServer;

class TestClientFullSubscription {

	boolean wait;
	public TestClientFullSubscription() {
		// init connection
		TalkToServer.getInstance(TestingConsts.host, TestingConsts.port);
	}
	
	@Test
	void testRegOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientFullSubscriptionController.handleClientFullSubscription("TestUser", "TestVehicle", "01-06-2018", "TestEmail@gmail.com",  msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("OK") && new Params(msg).getParam("price").equals("288.0"));
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
	void testInvalidDateOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientFullSubscriptionController.handleClientFullSubscription("TestUser", "TestVehicle", "01-86-2018", "TestEmail@gmail.com",  msg -> {
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
	void testInvalidDateOrder2() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientFullSubscriptionController.handleClientFullSubscription("TestUser", "TestVehicle", "51-06-2018", "TestEmail@gmail.com",  msg -> {
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
