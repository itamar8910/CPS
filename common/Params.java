package common;

import org.json.JSONException;
import org.json.JSONObject;

/**
* Params class is the class where the data sent in the communication between client and server is packed 
* Each field can be added easily, and can be accessed easily.
* Acts as a dictionary of param:val.
* Uses JSON Objects.
*/

public class Params {

	private JSONObject data;
	
	/**
	* @return Params empty params structure
	*/
	public static Params getEmptyInstance(){
		return new Params("{}");
	}

	/**
	* Constructor of Params class
	* @param jsonStr string to be wrapped by JSONObject
	*/
	public Params(String jsonStr){
		try {
			data = new JSONObject(jsonStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* @param p name of param to be returned
	* @return value of param in the Params JSON
	*/
	public String getParam(String p){
		try {
			return data.getString(p);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	
	/**
	* @param p name of param
	* @param val value to set param with name p to
	* @return this Params object
	*/
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

		
	/**
	* @param string Check if param with name string exists
	* @return boolean True if param with given name exits, False otherwise
	*/
	public boolean hasParam(String string) {
		return data.has(string);
	}

}