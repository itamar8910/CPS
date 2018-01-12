package common;

import org.json.JSONException;
import org.json.JSONObject;

public class Params {

	private JSONObject data;

	public static Params getEmptyInstance(){
		return new Params("{}");
	}

	public Params(String jsonStr){
		try {
			data = new JSONObject(jsonStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getParam(String p){
		try {
			return data.getString(p);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public Params addParam(String p, String val){
		try {
			data.put(p, val);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	public String toString(){
		return data.toString();
	}

	public boolean hasParam(String string) {
		return data.has(string);
	}

}
