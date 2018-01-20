package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import application.ClientPhysicalOrderController;
import common.Params;
import common.TalkToServer;

class TestClientPhysicalOrderController {
	boolean wait;
	public TestClientPhysicalOrderController() {
		// init connection
		TalkToServer.getInstance(TestingConsts.host, TestingConsts.port);
	}
	

	@Test
	void testRegOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientPhysicalOrderController.handleClientPhysicalOrder("TestUser", "TestVehicle", "23:00", "TestEmail@gmail.com", "TestParkinglot", msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("OK"));
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
	void testDoubleOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientPhysicalOrderController.handleClientPhysicalOrder("TestUser", "TestVehicle", "23:00", "TestEmail@gmail.com", "TestParkinglot", msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("OK"));
		});
		int a = 0;
		while(wait) {
			a++;
			System.out.println("waiting");
		}
		
		//now add the same user again
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientPhysicalOrderController.handleClientPhysicalOrder("TestUser", "TestVehicle", "23:00", "TestEmail@gmail.com", "TestParkinglot", msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("BAD"));
		});
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
	void testInvalidTime() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientPhysicalOrderController.handleClientPhysicalOrder("TestUser", "TestVehicle", "11:70", "TestEmail@gmail.com", "TestParkinglot", msg -> {
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
	void testBadEmailOrder() {
		
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientPhysicalOrderController.handleClientPhysicalOrder("TestUser", "TestVehicle", "23:00", "TestEmailgmail.com", "TestParkinglot", msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("BAD") && new Params(msg).getParam("message").equals("Invalid email address"));
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
	void testBadEmailOrder2() {
		
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientPhysicalOrderController.handleClientPhysicalOrder("TestUser", "TestVehicle", "23:00", "TestEmail@gmailcom", "TestParkinglot", msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("BAD") && new Params(msg).getParam("message").equals("Invalid email address"));
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
