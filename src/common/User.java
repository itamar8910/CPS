package common;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	private String userID, vehicleID, email;
	private Params subscriptionParams;
	private double money;
	
	public User(String userID, String vehicleID, String email, String subscriptionParamsStr, double money) {
		this.userID = userID;
		this.vehicleID = vehicleID;
		this.email = email;
		try {
			//new JSONObject(subscriptionParamsStr);
			this.subscriptionParams = new Params(subscriptionParamsStr);
		}catch(Exception e) {
			e.printStackTrace();
			this.subscriptionParams = Params.getEmptyInstance();
		}
		this.money = money;
	}

	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getVehicleID() {
		return vehicleID;
	}
	public void setVehicleID(String vehicleID) {
		this.vehicleID = vehicleID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Params getSubscriptionParams() {
		return subscriptionParams;
	}
	public void setSubscriptionParams(Params subscriptionParams) {
		this.subscriptionParams = subscriptionParams;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	
	
	
}
