package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import application.ClientOneTimeOrderController;
import application.ClientPhysicalOrderController;
import common.Params;
import common.TalkToServer;

class TestClientOneTimeOrderController {

	boolean wait;
	public TestClientOneTimeOrderController() {
		// init connection
		TalkToServer.getInstance(TestingConsts.host, TestingConsts.port);
	}
	
	@Test
	void testRegOrder() {
		wait = true;
		
		//ClientPhysicalOrderController controller = new ClientPhysicalOrderController();
		ClientOneTimeOrderController.handleClientOneTimeOrder("TestUser", "TestVehicle", 
				"TestParkingLot", "01-05-2018", "09:00", "01-05-2018", "17:00", "TestEmail@gmail.com", msg -> {
			System.out.println("test response:" + msg);
			String status1 = new Params(msg).getParam("status");
			wait = false;
			assertTrue(status1.equals("OK") && new Params(msg).getParam("price").equals("32.0"));
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
		ClientOneTimeOrderController.handleClientOneTimeOrder("TestUser", "TestVehicle", 
				"TestParkingLot", "01-15-2018", "49:00", "01-05-2018", "17:00", "TestEmail@gmail.com", msg -> {
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
		ClientOneTimeOrderController.handleClientOneTimeOrder("TestUser", "TestVehicle", 
				"TestParkingLot", "01-5-2018", "09:00", "01-85-2018", "127:00", "TestEmail@gmail.com", msg -> {
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
		ClientOneTimeOrderController.handleClientOneTimeOrder("TestUser", "TestVehicle", 
				"TestParkingLot", "01-05-2018", "09:00", "01-05-2018", "17:00", "TestEmailgmail.com", msg -> {
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
		ClientOneTimeOrderController.handleClientOneTimeOrder("TestUser", "TestVehicle", 
				"TestParkingLot", "01-05-2018", "09:00", "01-05-2018", "17:00", "TestEmail@gmailcom", msg -> {
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
