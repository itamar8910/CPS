package application;
import java.awt.Graphics2D;


import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import common.Params;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author Itamar
 * main methods for testing
 */
public class PDFstatus {

	//------------------- Testing
	  public static void main(String[] args) {
		  /*
	    	JSONArray data;
			try {
				data = new JSONArray("[[-1,2,1],[1,5,-1],[-1,-1,5]]");
		    	createComplaintsReport("Parking Name",data);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  JSONArray data;
			try {
				data = new JSONArray("[{'orderByType':'8','cancelOrders':2,'lateForParking':2,'date':1515841865000},{'orderByType':'9','cancelOrders':2,'lateForParking':6,'date':1515841865000},{'orderByType':'2','cancelOrders':3,'lateForParking':2,'date':1515841865000},{'orderByType':'2','cancelOrders':0,'lateForParking':5,'date':1515841865000},{'orderByType':'2','cancelOrders':0,'lateForParking':2,'date':1515841865000},{'orderByType':'2','cancelOrders':0,'lateForParking':2,'date':1515841865000},{'orderByType':'3','cancelOrders':0,'lateForParking':2,'date':1515841865000},{'orderByType':'3','cancelOrders':1,'lateForParking':0,'date':1515841865000},{'orderByType':'4','cancelOrders':0,'lateForParking':0,'date':1515841865000}]");
				createOrdersReport("Parking Name",data);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  //createCurrentStatusPDF("C:/Users/Itamar/Desktop/currentStatus.pdf","Itamar Parking",4,"0e1e2e3e4e5e6e7e8e9f10e11e12e13e14e15f16e17e18e19e20e21f22e23e24e25e26e27e28e29e30e31e32e33e34e35e");

		  JSONArray data;
			try {
				data = new JSONArray("[0,12,4,5,6,1,0,0,0,3,5,6,1]");
				createDisabledLotsReports("Parking Name",data);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




			//test for activity report
			try {

			  JSONArray data = new JSONArray("[{'orders':5,'cancel':8,'hoursDisabled':7},{'orders':5,'cancel':8,'hoursDisabled':7},{'orders':2,'cancel':4,'hoursDisabled':3},{'orders':1,'cancel':8,'hoursDisabled':4}]");
			  long startTime = System.currentTimeMillis();
			  createActivityReport("Gils Parking", data, startTime, 14);


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Params data = new Params("{'monthly':5,'monthlyWithMoreCars':8}");
			  createCurrentReport("Gils Parking", data);

	  */

	  }


	//test create activity report



	//------------------- end Testing


	//CREATE current report

  	//data = {monthly,monthlyWithMoreCars}
    public static void createCurrentReport(String parkingName, Params  data) {
    	createCurrentReportPDF("currentReport.pdf",parkingName,data);
    }

    public static void createCurrentReportPDF(String dir, String parkName, Params data) {
    	try {
    	Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir));
        document.open();
        createCurrentReportPage(dir,document,parkName, data);
        document.close();
        showFile(dir);
    	} catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param dir = directory for pdf
     * @param document = instance
     * @param parkName = current parking name
     * @param data = current data {monthly,monthlyWithMoreCars}
     * @throws DocumentException
     * returns orders report page
     */
    private static void createCurrentReportPage(String dir, Document document, String parkName, Params data)
        	throws DocumentException {
        	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                    Font.BOLD);
        	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);

        	//init data
    		ReportHandler orderObject = new ReportHandler();


        	//init title
            Paragraph preface = new Paragraph();
            preface.add(new Paragraph("Facility " + parkName +" - current performence report" , catFont));
            preface.add(new Paragraph("Current Date:  " + new java.util.Date() ));
            addEmptyLine(preface, 1);


    		//add data
    		try {

    			//-------------- Total orders data
    			//plot distrabution
    			DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

            	dataSet.addValue(Double.parseDouble(data.getParam("monthly")),"number of users","monthly");
            	dataSet.addValue(Double.parseDouble(data.getParam("monthlyWithMoreCars")),"number of users","more than one car");


        		JFreeChart chart = ChartFactory.createBarChart("Monthly / More that one car Users", "types", "number of users",
        				dataSet, PlotOrientation.VERTICAL, true, true, false);
        		int width = 550;
        		int height = 400;
        		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir));
    			document.open();

        		try {

	    			PdfContentByte contentByte = writer.getDirectContent();

	    			PdfTemplate template = contentByte.createTemplate(width, height);
	    			Graphics2D graphics2d = template.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);
	    			chart.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart.draw(graphics2d, rectangle2d);
	    			graphics2d.dispose();
	    			contentByte.addTemplate(template, 0, 350);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}



        		addEmptyLine(preface, 22);


        		DecimalFormat df2 = new DecimalFormat(".##");
        		preface.add(new Paragraph("Number of current monthly users :" +String.format("%.3f", Double.parseDouble(data.getParam("monthly"))) , smallBold));
        		preface.add(new Paragraph("number of users with more then one car:" +String.format("%.3f", Double.parseDouble(data.getParam("monthlyWithMoreCars"))) , smallBold));

        		document.add(preface);
        		document.newPage();





    		} catch(Exception error) {
    			error.printStackTrace();
    		}

    }




	 //end current report




	//CREATE Activity REPORT------------------------------------------------------------------

    public static void createActivityReport(String parkingName, JSONArray  data, long startTime, int numDaysBack) {
    	long endTime = startTime - numDaysBack*24*60*60*1000;

    	createActivityREportPDF("ActivityReport.pdf",parkingName,data,  startTime, endTime);
    }

    public static void createActivityREportPDF(String dir, String parkName, JSONArray data,long startTime, long endTime) {
    	try {
    	Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir));
        document.open();
        createActivityREportPAGE(dir,document,parkName, data, startTime,  endTime);
        document.close();
        showFile(dir);
    	} catch (Exception e) {
            e.printStackTrace();
        }

    }

    //returns orders report page
    private static void createActivityREportPAGE(String dir, Document document, String parkName, JSONArray data, long startTime, long endTime)
        	throws DocumentException {
        	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                    Font.BOLD);
        	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);

        	//init data
    		ReportHandler orderObject = new ReportHandler();


        	//init title
            Paragraph preface = new Paragraph();
            preface.add(new Paragraph("Facility " + parkName +" - Activity Report" , catFont));
            preface.add(new Paragraph("From Date: " + new java.util.Date((long)startTime)));
            preface.add(new Paragraph("To Date: " + new java.util.Date((long)endTime)));



    		//add data
    		try {

    			//-------------- Total orders data
        		addEmptyLine(preface, 1);
        		preface.add(new Paragraph("Overall Orders Data:", catFont));

        		JSONArray ordersData = orderObject.returnData(data, "orders");
    			Params allData = orderObject.analyzeDisabledParkingPlaces(ordersData);


    			//plot distrabution
    			JSONArray dataProb = new JSONArray(allData.getParam("probDist"));
    			DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
    			int [] a = new int [dataProb.length()];
                for (int i = 0 ; i < dataProb.length(); i++) {
                	JSONObject currentData = dataProb.getJSONObject(i);
                	a[i] = Integer.parseInt(currentData.getString("val"));
                	//dataSet.addValue(Double.parseDouble(currentData.getString("dist")),"orders",currentData.getString("val"));
                }
                if (a.length >0){
                	int min = 0;
                	while (min != Integer.MAX_VALUE){
                		min = Integer.MAX_VALUE;
                		int winnerInd = -1;
                		for(int i = 0; i < a.length; i++){
                			if (a[i] < min){
                				min = a[i];
                				winnerInd = i;
                			}
                		}
                		if (winnerInd == -1)
                			break;
                		JSONObject currentData = dataProb.getJSONObject(winnerInd);
                		dataSet.addValue(Double.parseDouble(currentData.getString("dist")),"orders",currentData.getString("val"));
                		a[winnerInd] = Integer.MAX_VALUE;
                	}
                }


                System.out.println(allData.getParam("probDist"));

        		JFreeChart chart = ChartFactory.createBarChart("Number of orders distrbution", "Number of orders", "probability",
        				dataSet, PlotOrientation.VERTICAL, true, true, false);
        		int width = 550;
        		int height = 400;
        		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir));
    			document.open();

        		try {

	    			PdfContentByte contentByte = writer.getDirectContent();

	    			PdfTemplate template = contentByte.createTemplate(width, height);
	    			Graphics2D graphics2d = template.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);
	    			chart.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart.draw(graphics2d, rectangle2d);
	    			graphics2d.dispose();
	    			contentByte.addTemplate(template, 0, 330);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}



        		addEmptyLine(preface, 20);


        		DecimalFormat df2 = new DecimalFormat(".##");
        		preface.add(new Paragraph("Number of orders :" +String.format("%.3f", Double.parseDouble(allData.getParam("total"))) , smallBold));
        		preface.add(new Paragraph("Mean of orders:" +String.format("%.3f", Double.parseDouble(allData.getParam("mean"))) , smallBold));
        		preface.add(new Paragraph("average of orders:" +String.format("%.3f", Double.parseDouble(allData.getParam("average"))) , smallBold));
        		preface.add(new Paragraph("standard deviation of orders:" +String.format("%.3f", Double.parseDouble(allData.getParam("standardDev"))) , smallBold));

        		document.add(preface);
        		document.newPage();





        		//-------------- add canceld data
        		Paragraph preface1 = new Paragraph();
        		preface1.add(new Paragraph("Overall cancel Data:", catFont));


        		//set chart
        		JSONArray cancelData = orderObject.returnData(data, "cancel");
    			Params allData1 = orderObject.analyzeDisabledParkingPlaces(cancelData);

    			//plot distrabution
    			JSONArray dataProb1 = new JSONArray(allData1.getParam("probDist"));
    			DefaultCategoryDataset dataSet1 = new DefaultCategoryDataset();

                for (int i = 0 ; i < dataProb1.length(); i++) {
                	JSONObject currentData = dataProb1.getJSONObject(i);
                	dataSet1.addValue(Double.parseDouble(currentData.getString("dist")),"canceled",currentData.getString("val"));
                }
                System.out.println(allData1.getParam("probDist"));

        		JFreeChart chart1 = ChartFactory.createBarChart(
        				"    Number of canceling distrbution", "Number of cancel", "probability",
        				dataSet1, PlotOrientation.VERTICAL, true, true, false);


        		try {

	    			PdfContentByte contentByte1 = writer.getDirectContent();
	    			PdfTemplate template1 = contentByte1.createTemplate(width, height);
	    			Graphics2D graphics2d1 = template1.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d1 = new Rectangle2D.Double(0, 0, width,height);
	    			chart1.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart1.draw(graphics2d1, rectangle2d1);
	    			graphics2d1.dispose();
	    			contentByte1.addTemplate(template1, 0, 350);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}


        		addEmptyLine(preface1, 25);


        		preface1.add(new Paragraph("Number of orders that were canceld:" + String.format("%.3f", Double.parseDouble(allData1.getParam("total"))) , smallBold));
        		preface1.add(new Paragraph("Mean of orders that were canceld:" + String.format("%.3f", Double.parseDouble(allData1.getParam("mean"))) , smallBold));
        		preface1.add(new Paragraph("average of orders that were canceld:" +String.format("%.3f", Double.parseDouble(allData1.getParam("average"))),smallBold));
        		preface1.add(new Paragraph("standard deviation of orders that were canceld" +String.format("%.3f", Double.parseDouble(allData1.getParam("standardDev"))),smallBold));


                document.add(preface1);



                document.newPage();



              //-------------- add disabled time
        		Paragraph preface11 = new Paragraph();
        		preface11.add(new Paragraph("Overall hours of parking being disabled:", catFont));


        		//set chart
        		JSONArray hoursDisabled = orderObject.returnData(data, "hoursDisabled");
    			Params allData11 = orderObject.analyzeDisabledParkingPlaces(hoursDisabled);

    			System.out.println("Data2 : " + allData11.toString());

    			//plot distrabution
    			JSONArray dataProb11 = new JSONArray(allData11.getParam("probDist"));
    			DefaultCategoryDataset dataSet11 = new DefaultCategoryDataset();

                for (int i = 0 ; i < dataProb11.length(); i++) {
                	JSONObject currentData = dataProb11.getJSONObject(i);
                	dataSet11.addValue(Double.parseDouble(currentData.getString("dist")),"hours",currentData.getString("val"));
                }

        		JFreeChart chart11 = ChartFactory.createBarChart(
        				"    Number of hours that the parking was disabled", "Number of hours", "probability",
        				dataSet11, PlotOrientation.VERTICAL, true, true, false);

        		try {

	    			PdfContentByte contentByte1 = writer.getDirectContent();
	    			PdfTemplate template1 = contentByte1.createTemplate(width, height);
	    			Graphics2D graphics2d1 = template1.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d1 = new Rectangle2D.Double(0, 0, width,height);
	    			chart11.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart11.draw(graphics2d1, rectangle2d1);
	    			graphics2d1.dispose();
	    			contentByte1.addTemplate(template1, 0, 350);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}


        		addEmptyLine(preface11, 25);


        		preface11.add(new Paragraph("Number of hours :" + String.format("%.3f", Double.parseDouble(allData11.getParam("total"))) , smallBold));
        		preface11.add(new Paragraph("Mean of hours:" + String.format("%.3f", Double.parseDouble(allData11.getParam("mean"))) , smallBold));
        		preface11.add(new Paragraph("average of hours:" +String.format("%.3f", Double.parseDouble(allData11.getParam("average"))),smallBold));
        		preface11.add(new Paragraph("standard deviation of hours:" +String.format("%.3f", Double.parseDouble(allData11.getParam("standardDev"))),smallBold));


                document.add(preface11);


    		} catch(Exception error) {
    			error.printStackTrace();
    		}

    }


	//CREATE disabled lots REPORT------------------------------------------------------------------


    public static void createDisabledLotsReports(String parkingName, JSONArray ordersReportData) {
    	createDisabledLots("OrdersReport.pdf",parkingName,ordersReportData);
    }

    public static void createDisabledLots(String dir, String parkName, JSONArray complaintsData) {
    	try {
    	Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir));
        document.open();
        createDisabledLotsPage(dir,document,parkName, complaintsData);
        document.close();
        showFile(dir);
    	} catch (Exception e) {
            e.printStackTrace();
        }

    }

    //return page for disabled lots
    private static void createDisabledLotsPage(String dir, Document document, String parkName, JSONArray data)
        	throws DocumentException {
        	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                    Font.BOLD);
        	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);

        	//init data
    		ReportHandler orderObject = new ReportHandler(data);


        	//init title
            Paragraph preface = new Paragraph();
            preface.add(new Paragraph("Facility " + parkName +" - Quarterly Report Of Parking Lots Being Disabled" , catFont));
            addEmptyLine(preface, 1);
            preface.add(new Paragraph("Date: " + new Date(), smallBold));


    		//add data

    		try {

    			//-------------- add orders data


    			Params allData = orderObject.analyzeDisabledParkingPlaces(data);

    			System.out.println(allData.toString());

    			//plot distrabution
    			JSONArray dataProb = new JSONArray(allData.getParam("probDist"));
    			DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
                int [] a = new int [dataProb.length()];
                for (int i = 0 ; i < dataProb.length(); i++) {
                	JSONObject currentData = dataProb.getJSONObject(i);
                	a[i] = Integer.parseInt(currentData.getString("val"));
                	//dataSet.addValue(Double.parseDouble(currentData.getString("dist")),"orders",currentData.getString("val"));
                }
                if (a.length >0){
                	int min = 0;
                	while (min != Integer.MAX_VALUE){
                		min = Integer.MAX_VALUE;
                		int winnerInd = -1;
                		for(int i = 0; i < a.length; i++){
                			if (a[i] < min){
                				min = a[i];
                				winnerInd = i;
                			}
                		}
                		if (winnerInd == -1)
                			break;
                		JSONObject currentData = dataProb.getJSONObject(winnerInd);
                		dataSet.addValue(Double.parseDouble(currentData.getString("dist")),"orders",currentData.getString("val"));
                		a[winnerInd] = Integer.MAX_VALUE;
                	}
                }
                System.out.println(allData.getParam("probDist"));

        		JFreeChart chart = ChartFactory.createBarChart("    Number of disabled lots distrbution", "Number of disabled lots", "probability",
        				dataSet, PlotOrientation.VERTICAL, true, true, false);
        		int width = 550;
        		int height = 400;
        		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir));
    			document.open();

        		try {

	    			PdfContentByte contentByte = writer.getDirectContent();

	    			PdfTemplate template = contentByte.createTemplate(width, height);
	    			Graphics2D graphics2d = template.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);
	    			chart.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart.draw(graphics2d, rectangle2d);
	    			graphics2d.dispose();
	    			contentByte.addTemplate(template, 0, 300);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}



        		addEmptyLine(preface, 25);


        		preface.add(new Paragraph("Number of disabled lots :" +String.format("%.3f", Double.parseDouble(allData.getParam("total"))) , smallBold));
        		preface.add(new Paragraph("Mean of disabled lots:" +String.format("%.3f", Double.parseDouble(allData.getParam("mean"))) , smallBold));
        		preface.add(new Paragraph("average of disabled lots:" +String.format("%.3f", Double.parseDouble(allData.getParam("average"))) , smallBold));

        		document.add(preface);

    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}

    }



//CREATE orders REPORT------------------------------------------------------------------


    public static void createOrdersReport(String parkingName, JSONArray ordersReportData) {
    	createOrdersReportPDF("OrdersReport.pdf",parkingName,ordersReportData);
    }

    public static void createOrdersReportPDF(String dir, String parkName, JSONArray complaintsData) {
    	try {
    	Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir));
        document.open();
        addOrdersReportPage(dir,document,parkName, complaintsData);
        document.close();
        showFile(dir);
    	} catch (Exception e) {
            e.printStackTrace();
        }

    }

    //returns orders report page
    private static void addOrdersReportPage(String dir, Document document, String parkName, JSONArray dataArr)
        	throws DocumentException {
        	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                    Font.BOLD);
        	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);

        	//init data
    		ReportHandler orderObject = new ReportHandler(dataArr);


        	//init title
            Paragraph preface = new Paragraph();
            preface.add(new Paragraph("Facility " + parkName +" - Quarterly Orders Report" , catFont));
            addEmptyLine(preface, 1);
            preface.add(new Paragraph("Date: " + new Date(), smallBold));
            addEmptyLine(preface, 1);

    		//add data
    		JSONArray data = orderObject.returnDataInRange(0, Long.MAX_VALUE);

    		try {

    			//-------------- add orders data
        		preface.add(new Paragraph("Overall Orders Data(On pre request):", smallBold));


    			Params allData = new Params(orderObject.analyzeData(data).getParam("orders"));
    			//plot distrabution
    			JSONArray dataProb = new JSONArray(allData.getParam("probDist"));
    			DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
                int [] a = new int [dataProb.length()];
                for (int i = 0 ; i < dataProb.length(); i++) {
                	JSONObject currentData = dataProb.getJSONObject(i);
                	a[i] = Integer.parseInt(currentData.getString("val"));
                	//dataSet.addValue(Double.parseDouble(currentData.getString("dist")),"orders",currentData.getString("val"));
                }
                if (a.length >0){
                	int min = 0;
                	while (min != Integer.MAX_VALUE){
                		min = Integer.MAX_VALUE;
                		int winnerInd = -1;
                		for(int i = 0; i < a.length; i++){
                			if (a[i] < min){
                				min = a[i];
                				winnerInd = i;
                			}
                		}
                		if (winnerInd == -1)
                			break;
                		JSONObject currentData = dataProb.getJSONObject(winnerInd);
                		dataSet.addValue(Double.parseDouble(currentData.getString("dist")),"orders",currentData.getString("val"));
                		a[winnerInd] = Integer.MAX_VALUE;
                	}
                }


                System.out.println(allData.getParam("probDist"));

        		JFreeChart chart = ChartFactory.createBarChart("    Number of orders distrbution", "Number of orders", "probability",
        				dataSet, PlotOrientation.VERTICAL, true, true, false);
        		int width = 550;
        		int height = 400;
        		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir));
    			document.open();

        		try {

	    			PdfContentByte contentByte = writer.getDirectContent();

	    			PdfTemplate template = contentByte.createTemplate(width, height);
	    			Graphics2D graphics2d = template.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);
	    			chart.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart.draw(graphics2d, rectangle2d);
	    			graphics2d.dispose();
	    			contentByte.addTemplate(template, 0, 300);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}



        		addEmptyLine(preface, 18);


        		DecimalFormat df2 = new DecimalFormat(".##");
        		preface.add(new Paragraph("Number of orders :" +String.format("%.3f", Double.parseDouble(allData.getParam("total"))) , smallBold));
        		preface.add(new Paragraph("Mean of orders:" +String.format("%.3f", Double.parseDouble(allData.getParam("mean"))) , smallBold));
        		preface.add(new Paragraph("average of orders:" +String.format("%.3f", Double.parseDouble(allData.getParam("average"))) , smallBold));

        		document.add(preface);
        		document.newPage();





        		//-------------- add canceld data
        		Paragraph preface1 = new Paragraph();
        		preface1.add(new Paragraph("Overall cancel Data:", smallBold));


        		//set chart
        		Params allData1 = new Params(orderObject.analyzeData(data).getParam("cancel"));
    			//plot distrabution
    			JSONArray dataProb1 = new JSONArray(allData1.getParam("probDist"));
    			DefaultCategoryDataset dataSet1 = new DefaultCategoryDataset();

                for (int i = 0 ; i < dataProb1.length(); i++) {
                	JSONObject currentData = dataProb1.getJSONObject(i);
                	dataSet1.addValue(Double.parseDouble(currentData.getString("dist")),"canceled",currentData.getString("val"));
                }
                System.out.println(allData1.getParam("probDist"));

        		JFreeChart chart1 = ChartFactory.createBarChart(
        				"Number of canceling distrbution", "Number of cancel", "probability",
        				dataSet1, PlotOrientation.VERTICAL, true, true, false);


        		try {

	    			PdfContentByte contentByte1 = writer.getDirectContent();
	    			PdfTemplate template1 = contentByte1.createTemplate(width, height);
	    			Graphics2D graphics2d1 = template1.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d1 = new Rectangle2D.Double(0, 0, width,height);
	    			chart1.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart1.draw(graphics2d1, rectangle2d1);
	    			graphics2d1.dispose();
	    			contentByte1.addTemplate(template1, 0, 350);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}


        		addEmptyLine(preface1, 25);


        		preface1.add(new Paragraph("Number of orders that were canceld:" + String.format("%.3f", Double.parseDouble(allData1.getParam("total"))) , smallBold));
        		preface1.add(new Paragraph("Mean of orders that were canceld:" + String.format("%.3f", Double.parseDouble(allData1.getParam("mean"))) , smallBold));
        		preface1.add(new Paragraph("average of orders that were canceld:" +String.format("%.3f", Double.parseDouble(allData1.getParam("average"))),smallBold));


                document.add(preface1);



                document.newPage();
              //-------------- add late data
        		Paragraph preface11 = new Paragraph();
        		preface11.add(new Paragraph("Overall people that were late for their parking:", smallBold));


        		//set chart
        		Params allData11 = new Params(orderObject.analyzeData(data).getParam("late"));
    			//plot distrabution
    			JSONArray dataProb11 = new JSONArray(allData11.getParam("probDist"));
    			DefaultCategoryDataset dataSet11 = new DefaultCategoryDataset();

                for (int i = 0 ; i < dataProb11.length(); i++) {
                	JSONObject currentData = dataProb11.getJSONObject(i);
                	dataSet11.addValue(Double.parseDouble(currentData.getString("dist")),"late",currentData.getString("val"));
                }

        		JFreeChart chart11 = ChartFactory.createBarChart(
        				"    Number of people being late distrbution", "Number of Late", "probability",
        				dataSet11, PlotOrientation.VERTICAL, true, true, false);

        		try {

	    			PdfContentByte contentByte1 = writer.getDirectContent();
	    			PdfTemplate template1 = contentByte1.createTemplate(width, height);
	    			Graphics2D graphics2d1 = template1.createGraphics(width, height,new DefaultFontMapper());
	    			Rectangle2D rectangle2d1 = new Rectangle2D.Double(0, 0, width,height);
	    			chart11.getLegend().setPosition(RectangleEdge.RIGHT);
	    			chart11.draw(graphics2d1, rectangle2d1);
	    			graphics2d1.dispose();
	    			contentByte1.addTemplate(template1, 0, 350);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}


        		addEmptyLine(preface11, 25);


        		preface11.add(new Paragraph("Number of people being Late:" + String.format("%.3f", Double.parseDouble(allData11.getParam("total"))) , smallBold));
        		preface11.add(new Paragraph("Mean of people being Late:" + String.format("%.3f", Double.parseDouble(allData11.getParam("mean"))) , smallBold));
        		preface11.add(new Paragraph("average of people being Late:" +String.format("%.3f", Double.parseDouble(allData11.getParam("average"))),smallBold));


                document.add(preface11);


    		} catch(Exception error) {
    			error.printStackTrace();
    		}

    }

	    //end------------------------------------------------------------------



	//CREATE PARKING status pdf ------------------------------------------------------------------
    public static void createCurrentStatusPDF(String dir,String parkingName, int dimention,String parkData) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dir));
            document.open();
            //String parkData = "0s1f2i3e4e5f6i7s8s9s10f11f12i13i14e15s16f17f18f19e20e21i22f23f24s25f26f27i28i29i30e31e32s33f34i35i";
            addTitlePage(document,parkingName,dimention,parkData);
            document.close();
            showFile(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //------------------------------------------------------------------

    //create PDF with string
    public static void createBoringPdf(String dir, String text) {
    	try {
    	Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir));
        document.open();
        addBoringPage(document,text);
        document.close();
        showFile(dir);
    	} catch (Exception e) {
            e.printStackTrace();
        }

    }



    //CREATE COMPLAINTS REPORT------------------------------------------------------------------



    public static void createComplaintsReport(String parkingName, JSONArray complaintsData) {
    	createCompQuater("ComplaintsReport.pdf",parkingName,complaintsData);
    }

    public static void createCompQuater(String dir, String parkName, JSONArray complaintsData) {
    	try {
    	Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir));
        document.open();
        addCompPage(dir,document,parkName, complaintsData);
        document.close();
        showFile(dir);
    	} catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addCompPage(String dir, Document document, String parkName, JSONArray complaintsData)
        	throws DocumentException {
        	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                    Font.BOLD);
        	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                    Font.BOLD);


            Paragraph preface = new Paragraph();
            preface.add(new Paragraph("CarPark " + parkName +" - Quarterly Complaints Report" , catFont));
            addEmptyLine(preface, 1);
            preface.add(new Paragraph("Date: " + new Date(), smallBold));

            DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

            for (int i = 0 ; i < complaintsData.length(); i++) {
            	int rejected = 0;
            	int accepted = 0;
            	try {
					for(int j = 0; j < complaintsData.getJSONArray(i).length(); j++) {

						if(complaintsData.getJSONArray(i).getInt(j) == -1)
							rejected++;
						else
							accepted++;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            	if (rejected == 0 && accepted == 0)
            		continue;

            	dataSet.addValue(accepted,"Accepted",""+(i+1));
            	dataSet.addValue(rejected,"Rejected",""+(i+1));
            }


    		JFreeChart chart = ChartFactory.createBarChart(
    				"         Regected & Accepted Complaints by day", "Day", "Number Of Complaints",
    				dataSet, PlotOrientation.VERTICAL, true, true, false);
    		int width = 550;
    		int height = 400;
    		PdfWriter writer = null;


    		try {
    			writer = PdfWriter.getInstance(document, new FileOutputStream(dir));
    			document.open();
    			PdfContentByte contentByte = writer.getDirectContent();
    			PdfTemplate template = contentByte.createTemplate(width, height);
    			Graphics2D graphics2d = template.createGraphics(width, height,new DefaultFontMapper());
    			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);
    			chart.getLegend().setPosition(RectangleEdge.RIGHT);
    			chart.draw(graphics2d, rectangle2d);
    			graphics2d.dispose();
    			contentByte.addTemplate(template, 0, 300);

    		} catch (Exception e) {
    			e.printStackTrace();
    		}


    		addEmptyLine(preface, 25);
    		preface.add(new Paragraph("Statistic data:", catFont));
    		addEmptyLine(preface, 1);


    		int totalA = 0 ;
    		int totalR = 0 ;
    		int totalPay = 0 ;
    		 for (int i = 0 ; i < complaintsData.length(); i++) {
	        	int regected = 0;
	        	int accepted = 0;
	        	int money = 0;
	        	try {
					for(int j = 0; j < complaintsData.getJSONArray(i).length(); j++) {
						if (complaintsData.getJSONArray(i).length() == 0)
							continue;

						if(complaintsData.getJSONArray(i).getInt(j) == -1)
							regected++;
						else {
							accepted++;
							money += complaintsData.getJSONArray(i).getInt(j);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	totalPay += money;
	        	totalA += accepted;
	        	totalR += regected;

	        }
    		DecimalFormat df2 = new DecimalFormat(".##");
    		preface.add(new Paragraph("Total Accepted - " + totalA + "\nTotal Rejected - " + totalR + "\nTotal Money Paid - " + totalPay , smallBold));
    		preface.add(new Paragraph("\nAccepted Day Average - " + df2.format(totalA/(double)complaintsData.length()) + "\nRejected Day Average - " + df2.format(totalR/(double)complaintsData.length()) + "\nMoney Paid Day Average - " + df2.format(totalPay/(double)complaintsData.length()) , smallBold));


            document.add(preface);
        }

    //end------------------------------------------------------------------


    //utils









    public static void showFile(String dir) {
        try {

      		if ((new File(dir)).exists()) {
      			String s = "rundll32 url.dll,FileProtocolHandler " + dir;
      			Process p = Runtime.getRuntime().exec(s);
      			p.waitFor();

      		} else {

      			System.out.println("File is not exists");

      		}
        	  } catch (Exception ex) {
      		ex.printStackTrace();
      	  }
    }

    private static void addBoringPage(Document document,String text)
            throws DocumentException {
    	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD);
    	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD);
        Paragraph preface = new Paragraph();
        int index1 = 0;
        int index2 = 0;

        boolean flag = true;
        while(index1 < text.length()) {
        	if(text.charAt(index1) == '\n') {
        		if(flag) {
        			preface.add(new Paragraph(text.substring(index2,index1), catFont));
        	        flag = false;
        		}else
        			 preface.add(new Paragraph(text.substring(index2,index1), smallBold));
        		addEmptyLine(preface, 1);
        		index2 = index1 + 1;
        	}
        	index1++;
        }
        document.add(preface);
    }
    private static void addTitlePage(Document document,String parkName,int N,String parkData)
            throws DocumentException {
    	int index = 0;
    	Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD);
    	Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD);
        Paragraph preface = new Paragraph();
        preface.add(new Paragraph("CarPark " + parkName +":", catFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Orange - saved,     Red - invalid,     Blue - full,     White - empty,     Green - ordered", smallBold));
        preface.add(new Paragraph(
                "first layer:", smallBold));
        addEmptyLine(preface, 1);
        index =createTable(preface, N,parkData,0,index);
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(
                "second layer:", smallBold));
        addEmptyLine(preface, 1);
        index =createTable(preface, N,parkData,0,index);
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(
                "last layer:", smallBold));
        addEmptyLine(preface, 1);
        index =createTable(preface, N,parkData,0,index);
        document.add(preface);
    }

    private static int createTable(Paragraph subCatPart, int N, String parkData,int layerNum,int index)
            throws BadElementException {
        PdfPTable table = new PdfPTable(N);
        int stringIndex = index;
        char[][] a = new char[3][N];
        for(int i = N*3*layerNum;i < N*3*(layerNum+1);i++) {
        	while(parkData.charAt(stringIndex)>='0' && parkData.charAt(stringIndex)<='9') {
        		stringIndex += 1;
        	}
        	a[i/N][i%N] = parkData.charAt(stringIndex);
        	stringIndex++;
        }
        for(int i = 2; i>=0;i-- ) {
        	for(int j=0; j<N;j++) {
        		PdfPCell c1 = new PdfPCell(new Phrase(" "));
        		if(a[i][j] == 'o')
        			c1.setBackgroundColor(BaseColor.GREEN);
        		if(a[i][j] == 's')
            		c1.setBackgroundColor(BaseColor.ORANGE);
        		if(a[i][j] == 'f')
            		c1.setBackgroundColor(BaseColor.BLUE);
        		if(a[i][j] == 'i')
            		c1.setBackgroundColor(BaseColor.RED);

        		table.addCell(c1);
        	}
        }
        subCatPart.add(table);
        return stringIndex;
    }
    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
