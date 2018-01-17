package application;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Params;


//handles reports
public class ReportHandler {
	private JSONArray reportData;
	private long currentDate;
	
	
	//set orders report data
	public ReportHandler(JSONArray data ){
		this.reportData = data;
		this.currentDate = this.getStartOfDayInMillis();
	}
	
	public ReportHandler(){
		this.currentDate = this.getStartOfDayInMillis();
	}
	
	
	//turn object with key data to array - for activity report
	public JSONArray returnData(JSONArray data, String key) {
		JSONArray returnData = new JSONArray();
		
		for (int i=0; i < data.length();i++) {
			try {
				JSONObject currData = data.getJSONObject(i);
			
				returnData.put(currData.get(key));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return returnData;
	}
	
	
	//returns start of date
	public long getStartOfDayInMillis() {
	    Date d = new Date();
	    d.setHours(0);
	    d.setMinutes(0);
	    return d.getTime();
	}
	
	
	//gets array list, returns probability dist
	public JSONArray returnProbDist(ArrayList<Integer> allOrders) {
		JSONArray probDataOrders = new JSONArray();
		
		int length = allOrders.size();
		while (allOrders.size() > 0) {
			JSONObject currProb = new JSONObject();
			//count current value
			int val = allOrders.get(0);
			int numOcc = 0;
			
			ArrayList<Integer> temp = new ArrayList<Integer>();
			
			//go over all orders
			for (int i=0; i < allOrders.size();i++) {
				if (allOrders.get(i) == val) {
					numOcc +=1;
				}else
					temp.add(allOrders.get(i));
			}
			
			try {
				currProb.put("val", val);
				currProb.put("dist", 100*((double)numOcc/(double)length));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			probDataOrders.put(currProb);
			
			allOrders = new ArrayList<Integer>();
			for (Integer curr : temp)
				allOrders.add(curr);
		}
		
		return probDataOrders;
		
	}
	
	
	//return data for disabled parking places report
	public Params analyzeDisabledParkingPlaces(JSONArray data) {
				
		ArrayList<Integer> all = new ArrayList<Integer>();
		
		double total = 0;
		double mean = 0;
		double average = 0;
		double standardDev = 0;

		
		//calculate average per order----------------
		for (int i=0; i < data.length();i++) {
			
			try {
				average += data.getInt(i);
			

				//if ordered
				if (data.getInt(i) > 0)
					total +=data.getInt(i);
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//divide by length
		//add to all types
		average /= data.length();

		
		//calculate mean
		ArrayList<Integer> MeanArray = new ArrayList<Integer>();

		for (int i=0; i < data.length();i++) {
			try {
				MeanArray.add(data.getInt(i));
				
				//add to array
				all.add(data.getInt(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		Collections.sort(MeanArray);		
		mean = MeanArray.get(MeanArray.size()/2);
		
		
		//calculate standard deviation
		standardDev = Math.pow(average, 2);
		int minusStandardDev = 0;
		//calculate sum of all numbers squared(decrese)
		for (int i=0; i < data.length();i++) {
			try {
				minusStandardDev += Math.pow(data.getInt(i),2);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		minusStandardDev /= data.length();
		standardDev = minusStandardDev - standardDev;
		
		standardDev = Math.sqrt(standardDev);
		
		
		
		//calculate probality distubtation for orders
		JSONArray probData = this.returnProbDist(all);
		
		//return data
	
		//order date
		Params retData = new Params("{}");		
		retData.addParam("average",  String.valueOf(average));
		retData.addParam("mean",  String.valueOf(mean));
		retData.addParam("total", String.valueOf( total));
		retData.addParam("probDist", String.valueOf( probData));
		retData.addParam("standardDev", String.valueOf( standardDev));


		return retData;
	}
	
	//analyze the data
	public Params analyzeData(JSONArray data) throws JSONException {

		ArrayList<Integer> allOrders = new ArrayList<Integer>();
		ArrayList<Integer> allCanceld = new ArrayList<Integer>();
		ArrayList<Integer> allLate = new ArrayList<Integer>();

		//orders data
		int totalOrders = 0;
		double averageOrders = 0;
		double meanOrders = 0;
		

		//canceldData
		int totalCanceld = 0;
		double averageCanceld = 0;
		double meanCanceld = 0;

		//lateData
		int totalLate = 0;
		double averageLate = 0;
		double meanLate = 0;
		
		//calculate average per order----------------
		for (int i=0; i < data.length();i++) {
			JSONObject currentObject = data.getJSONObject(i);//new JSONArray(this.reportData.get(i).getString("orderByType"));
			
			averageOrders += Integer.parseInt(currentObject.getString("orderByType"));
			averageCanceld += Integer.parseInt(currentObject.getString("cancelOrders"));
			averageLate += Integer.parseInt(currentObject.getString("lateForParking"));

			//if ordered
			if (Integer.parseInt(currentObject.getString("orderByType")) > 0)
				totalOrders +=Integer.parseInt(currentObject.getString("orderByType"));;
			
			//if canceld	
			if (Integer.parseInt(currentObject.getString("cancelOrders")) > 0)
				totalCanceld +=Integer.parseInt(currentObject.getString("cancelOrders"));
			
			//if late	
			if (Integer.parseInt(currentObject.getString("lateForParking")) > 0)
				totalLate +=Integer.parseInt(currentObject.getString("lateForParking"));
		}
		
		//divide by length
		//add to all types
		averageOrders /= data.length();
		averageCanceld /= data.length();
		averageLate /= data.length();

		
		//calculate mean
		ArrayList<Integer> ordersMeanArray = new ArrayList<Integer>();
		ArrayList<Integer> canceldMeanArray = new ArrayList<Integer>();
		ArrayList<Integer> lateMeanArray = new ArrayList<Integer>();
		for (int i=0; i < data.length();i++) {
			JSONObject currentObject = data.getJSONObject(i);
			ordersMeanArray.add(Integer.parseInt(currentObject.getString("orderByType")));
			canceldMeanArray.add(Integer.parseInt(currentObject.getString("cancelOrders")));
			lateMeanArray.add(Integer.parseInt(currentObject.getString("lateForParking")));

			
			//add to array
			allOrders.add(Integer.parseInt(currentObject.getString("orderByType")));
			allCanceld.add(Integer.parseInt(currentObject.getString("cancelOrders")));
			allLate.add(Integer.parseInt(currentObject.getString("lateForParking")));
		}
		
		Collections.sort(ordersMeanArray);		
		Collections.sort(allCanceld);
		Collections.sort(allLate);

		meanOrders = ordersMeanArray.get(ordersMeanArray.size()/2);
		meanCanceld = allCanceld.get(allCanceld.size()/2);
		meanLate = allLate.get(allLate.size()/2);

		
		
		//calculate probality distubtation for orders
		JSONArray probDataOrders = this.returnProbDist(allOrders);
		JSONArray probDataCanceld = this.returnProbDist(allCanceld);
		JSONArray probDataLate = this.returnProbDist(allLate);
		
		
		
		
		//return data
		
		//late data
		Params lateData = new Params("{}");		
		lateData.addParam("average",  String.valueOf(averageLate));
		lateData.addParam("mean",  String.valueOf(meanLate));
		lateData.addParam("total", String.valueOf( totalLate));
		lateData.addParam("probDist", String.valueOf( probDataLate));
		
		//canceld date
		Params cancelData = new Params("{}");		
		cancelData.addParam("average",  String.valueOf(averageCanceld));
		cancelData.addParam("mean",  String.valueOf(meanCanceld));
		cancelData.addParam("total", String.valueOf( totalCanceld));
		cancelData.addParam("probDist", String.valueOf( probDataCanceld));
		
		//order date
		Params orderData = new Params("{}");		
		orderData.addParam("average",  String.valueOf(averageOrders));
		orderData.addParam("mean",  String.valueOf(meanOrders));
		orderData.addParam("total", String.valueOf( totalOrders));
		orderData.addParam("probDist", String.valueOf( probDataOrders));
		
		Params returnData = new Params("{}");
		returnData.addParam("orders", orderData.toString());
		returnData.addParam("late", lateData.toString());
		returnData.addParam("cancel", cancelData.toString());


		return returnData;
		
		
	}
	
	//returns total data
	public JSONArray returnDataInRange(long from,long to) {
	
		JSONArray allCurrData = new JSONArray();
		
		long startOfDate = this.currentDate;
		long endOfDate = currentDate + (long)1000l*60l*60l*24l;
		
		//run on all of data
		for (int i=0; i < this.reportData.length();i++) {
			//if in range add it
			if (startOfDate >= from && endOfDate < to) {
				try {
					allCurrData.put(this.reportData.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//one day before
			startOfDate -= 1000l*60l*60l*24l;
			endOfDate -= 1000l*60l*60l*24l;
		}
		
		//run on data 
		return allCurrData;
	}
	
	

}
