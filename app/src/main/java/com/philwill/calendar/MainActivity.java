package com.philwill.calendar;

// https://github.com/Applandeo/Material-Calendar-View

//

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



public class MainActivity extends AppCompatActivity {

    /////////////////////////////////////////////////////////////
    // Set Up Logcat Filtering Variables
    String TAG = "Response";
    String PhilTag = "Phil";

    String resultArr[]; // This will hold the list of categories as returned from the Web Service
    String resultArrTrimmed[]; // This will hold the reformatted list of categories as returned from the Web Service

    String diaryDate;
    WebServiceToAccess ServiceHost;

    Boolean justStarted = true;


    //---18/01/2016-------The following parameters can be changed to the respective service
    String aspxserver = "hp-proliant";
    String aspxMethod = "BandBookings";
    String aspxBandName = "King";
    String aspxPeriod = "Past";
    String xmldelimiter = "BandBookings";
    String xmldelimiter_1 = "Contact";
    String bandName = "";
    String details = "";
    TextView bandAndVenue;
    Integer x;
    MyClasses FNLib;



    //----- Add This In for New Code-----
    String days, dayname, xDate, venue,xDays,xWeeks;
    ArrayList searchResults;
    /////////////////////////////////////////////////////////////

    List<EventDay> events = new ArrayList<>();
    CalendarView calendarView;

    Calendar calendar = Calendar.getInstance();
    // events.add(new EventDay(calendar, R.drawable.));
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    Date date = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////////////////////////////////////////////////////////
        FNLib = new MyClasses();
        if(bandName.equals("All")) {
            aspxBandName = bandName;
        }

        aspxserver = FNLib.getWebServiceName();
        ServiceHost = new WebServiceToAccess(aspxserver, aspxMethod);





       showCalendar();
        //showDiaryEntries();
        ///////////////////////////////////////////////////////////////////////

        //events.clear();
        //setupTestData();

    }



    public void showCalendar() {
        Log.i(PhilTag, "Update Unavailability Button was pressed......");

        Log.i(PhilTag, "BookingList: Calculate START");
        Log.i(PhilTag, "BookingList: Calculate - URL IS:... " + ServiceHost.httpAddress);
        Log.i(PhilTag, "BookingList: Calculate - NAMESPACE IS:.... " + ServiceHost.webSrv);
        Log.i(PhilTag, "BookingList: Calculate - ASMX FILE IS:.... " + ServiceHost.ASMXFile);
        Log.i(PhilTag, "BookingList: Calculate - METHOD NAME IS:.. " + ServiceHost.fName);
        Log.i(PhilTag, "BookingList: Calculate - SOAP_ACTION IS:.. " + ServiceHost.sOAPACTION);



        AsyncCallWS task = new AsyncCallWS();

        task.execute();

    }


    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.VISIBLE);

            Log.i(PhilTag, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            Log.i(PhilTag, "doInBackground");
            //*******************************************
            getCalendarDataFromServer();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(PhilTag, "onPostExecute START");
            //ToastMessage("Data Received From Server");
            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);


            if(justStarted) {
                //toastMessage(FNLib.todayWithSeperators());
                diaryDate = FNLib.todayWithSeperators();
                showDiaryEntries();
                justStarted = false;
            }

            calendarView = (CalendarView) findViewById(R.id.calendarView);
            calendarView.setEvents(events);

           calendarView.setOnDayClickListener(new OnDayClickListener() {
                @Override
                public void onDayClick(EventDay eventDay) {
                    Calendar clickedDayCalendar = eventDay.getCalendar();

                    int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                    int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                    int year = clickedDayCalendar.get(Calendar.YEAR);

                   // prefix day and month with a 0 if only one character.
                    String sDay = "0" + String.valueOf(day);
                    String sMonth = "0" + String.valueOf(month);
                    sDay = sDay.substring(sDay.length() -2);
                    sMonth = sMonth.substring(sMonth.length() -2);

                    diaryDate = sDay + "/" + sMonth + "/" + String.valueOf(year);

                // toastMessage(diaryDate);
                 showDiaryEntries();
                }
            });

         /*   try {
                final ListView lv2 = (ListView) findViewById(R.id.lvw_diary);
                lv2.setAdapter(new DiaryListAdapter(getApplicationContext(), searchResults));

                // Make Listview rows selectable
                lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        Object o = lv2.getItemAtPosition(position);
                        DiarySearchResults fullObject = (DiarySearchResults) o;
                        toastMessage("You Clicked ID: " + ((DiarySearchResults) o).getDetails());
                    }
                });

            } catch (Exception ex) {
                Log.e(PhilTag, "No Records : " + ex.getMessage());
            }*/


            //ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);

            Log.i(PhilTag, "onPostExecute END");
        }
    }

    public void getCalendarDataFromServer() {
        Log.i(PhilTag, "Calculate START");
        Log.i(PhilTag, "Calculate - URL IS:... " + ServiceHost.httpAddress);
        Log.i(PhilTag, "Calculate - NAMESPACE IS:.... " + ServiceHost.webSrv);
        Log.i(PhilTag, "Calculate - ASMX FILE IS:.... " + ServiceHost.ASMXFile);
        Log.i(PhilTag, "Calculate - METHOD NAME IS:.. " + ServiceHost.fName);
        Log.i(PhilTag, "Calculate - SOAP_ACTION IS:.. " + ServiceHost.sOAPACTION);

        String SOAP_ACTION = ServiceHost.sOAPACTION;
        String METHOD_NAME = ServiceHost.fName;
        String NAMESPACE = ServiceHost.nameSpace;
        String URL = ServiceHost.httpAddress;

        Boolean bandEvent = false;

        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        Request.addProperty("BandName", "All");
        Request.addProperty("Period", "All");

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(Request);
        soapEnvelope.implicitTypes = false;

        HttpTransportSE transport = new HttpTransportSE(URL);

        try {
            transport.debug = true;
            transport.call(SOAP_ACTION, soapEnvelope);
            String xml = transport.responseDump;
            // 12/02/2016 - Get rid of those pesky diffgr things............
            xml = xml.replaceAll(" diffgr:id=", ">");

            resultArr = xml.split("<" + xmldelimiter + ">");
            resultArrTrimmed = xml.split("<" + xmldelimiter + ">");

            Integer ArrLength = resultArr.length;
            Log.i(PhilTag, "------------------------------------------------");
            Log.i(PhilTag, "----------HERE IS THE DATA FROM SERVER----------");
            Log.i(PhilTag, "------------------------------------------------");

            //*********************************************************


            //************************************************************

            for (int i = 0; i < ArrLength; ++i) {
                if (resultArr[i].substring(0, 7).equals("<?xml v")) {
                    Log.i(PhilTag, " Ignoring xml tag ");
                    resultArrTrimmed[i] = "   ";
                } else {
                    bandEvent = false;
                    resultArrTrimmed[i] = resultArr[i].substring(0, resultArr[i].indexOf("</" + xmldelimiter + ">"));
          ;          resultArrTrimmed[i] = resultArrTrimmed[i].replaceAll("@", " ");

                    dayname = FNLib.GetField("DayName", resultArr[i]);
                    xDate =  FNLib.GetField("xDate", resultArr[i]);
                    venue = FNLib.GetField("Venue",resultArr[i]);
                    xDays =  FNLib.GetField("TimeTo",resultArr[i]);
                    //xWeeks = FNLib.GetField("xWeeks",resultArr[i]);


                    Calendar cal = Calendar.getInstance();



                    try {
                        date = sdf.parse(xDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    cal = Calendar.getInstance();
                    cal.setTime(date);

                    //events.add(new EventDay(cal, R.drawable.dot));

                    if(FNLib.GetField("Category", resultArr[i]).equals("KING") ) {
                        if (!FNLib.GetField("SubCategory", resultArr[i]).contains("CANCELLED")) {
                            events.add(new EventDay(cal, R.drawable.king1));
                            bandEvent = true;
                        }
                    }

                    if(FNLib.GetField("Category", resultArr[i]).equals("VINTAGETHREE")) {
                        if (!FNLib.GetField("SubCategory", resultArr[i]).contains("CANCELLED")) {
                            events.add(new EventDay(cal, R.drawable.vintage));
                            bandEvent = true;
                        }
                    }

                    if(FNLib.GetField("Category", resultArr[i]).equals("RUBBER SOUL")) {
                        events.add(new EventDay(cal, R.drawable.robot14));
                        bandEvent = true;}

                    if(FNLib.GetField("Category", resultArr[i]).equals("HOLIDAYS")) {
                        events.add(new EventDay(cal, R.drawable.aeroplane1));
                        bandEvent = false;}

                    if(FNLib.GetField("Category", resultArr[i]).contains("PERSONAL - HEALTH")) {
                        events.add(new EventDay(cal, R.drawable.redcross));
                        bandEvent = false;}
                    else {
                        Log.i(PhilTag,"NOT HEALTH "+FNLib.GetField("Category", resultArr[i]));

                    }

                    if(FNLib.GetField("Priority", resultArr[i]).equals("HIGH")) {
                        events.add(new EventDay(cal, R.drawable.star));
                        bandEvent = false;}


                   // if(!bandEvent) {
                     //   events.add(new EventDay(cal, R.drawable.cleff2));
                       // Log.i(PhilTag,"*****************"+FNLib.GetField("Category", resultArr[i]));
                    //}
                    Log.i(PhilTag,">>>>>>>>>>>>>>>>>>"+FNLib.GetField("Category", resultArr[i]));

                    //Log.i(PhilTag, "Days: " + days);
                    Log.i(PhilTag, "Dayname: " + dayname);
                    Log.i(PhilTag, "Date: " + xDate);
                    Log.i(PhilTag, "Venue: " + venue);
                    Log.i(PhilTag, "Days: " + xDays);
                    Log.i(PhilTag, "Weeks: " + xWeeks);

                    ////// add the dates here

                    Log.i(PhilTag, i + " - " + resultArrTrimmed[i]);
                }
            }



            Log.i("Phil", "Array Length: " + ArrLength);

        } catch (Exception ex) {
            Log.e(PhilTag, "SOAP Transport Error: " + ex.getMessage());
        }


    }


    private OnSelectDateListener listener = new OnSelectDateListener() {
        @Override
        public void onSelect(List<Calendar> calendars) {
        ////
        }
    };



    public void setupTestData(){

        new DatePickerBuilder(this, listener)
                .setTodayColor(R.color.teal_200) // Color of the today number
                .setTodayLabelColor(R.color.purple_700);


        try {
            date = sdf.parse("29 Oct 2022");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        events.add(new EventDay(cal, R.drawable.cleff2));

        try {
            date = sdf.parse("2022-10-20");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal = Calendar.getInstance();
        cal.setTime(date);
        events.add(new EventDay(cal, R.drawable.cleff2));

        try {
            date = sdf.parse("2022-10-20");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal = Calendar.getInstance();
        cal.setTime(date);
        events.add(new EventDay(cal, R.drawable.music1));



        try {
            date = sdf.parse("2022-10-22");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal = Calendar.getInstance();
        cal.setTime(date);
        events.add(new EventDay(cal, R.drawable.music1));



        //events.add(new EventDay(cal, R.drawable.cleff2));
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setEvents(events);



    }

    ////////////////// DIARY ENTRIES /////////////////////////

    public void showDiaryEntries() {

        String aspxserver = "hp-proliant";
        aspxMethod = "Diary_Search_By_Date";

        xmldelimiter = "Diary_Entries";

        aspxserver = FNLib.getWebServiceName();
        ServiceHost = new WebServiceToAccess(aspxserver, aspxMethod);



        Log.i(PhilTag, "BookingList: Calculate START");
        Log.i(PhilTag, "BookingList: Calculate - URL IS:... " + ServiceHost.httpAddress);
        Log.i(PhilTag, "BookingList: Calculate - NAMESPACE IS:.... " + ServiceHost.webSrv);
        Log.i(PhilTag, "BookingList: Calculate - ASMX FILE IS:.... " + ServiceHost.ASMXFile);
        Log.i(PhilTag, "BookingList: Calculate - METHOD NAME IS:.. " + ServiceHost.fName);
        Log.i(PhilTag, "BookingList: Calculate - SOAP_ACTION IS:.. " + ServiceHost.sOAPACTION);


        AsyncCallDiaryEntries task = new AsyncCallDiaryEntries();

        task.execute();

    }

    private class AsyncCallDiaryEntries extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.VISIBLE);
            Log.i(PhilTag, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            Log.i(PhilTag, "doInBackground");
            //*******************************************
            calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(PhilTag, "onPostExecute START");
            //ToastMessage("Data Received From Server");
            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);

            try {
                final ListView lv2 = (ListView) findViewById(R.id.lvw_diary);
                lv2.setAdapter(new DiaryListAdapter(getApplicationContext(), searchResults));




            } catch (Exception ex) {
                Log.e(PhilTag, "** FINANCE Error********: " + ex.getMessage());
                toastMessage(ex.getMessage());
            }

                try {
                final ListView lv2 = (ListView) findViewById(R.id.lvw_diary);
                lv2.setAdapter(new DiaryListAdapter(getApplicationContext(), searchResults));

                // Make Listview rows selectable
                lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        Object o = lv2.getItemAtPosition(position);
                        DiarySearchResults fullObject = (DiarySearchResults) o;
                        toastMessage("You Clicked ID: " + ((DiarySearchResults) o).getId());

                        //////////////////////////////////////////////////////

                        Intent diaryRecord = new Intent(getApplicationContext(), DiaryRecordByID.class);
                        diaryRecord.putExtra("ID", ((DiarySearchResults) o).getId());
                        startActivity(diaryRecord);

                    }
                });

            } catch (Exception ex) {
                Log.e(PhilTag, "No Records : " + ex.getMessage());
            }



            Log.i(PhilTag, "onPostExecute END");
        }
    }

    public void toastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void calculate() {
        Log.i(PhilTag, "Calculate START");
        Log.i(PhilTag, "Calculate - URL IS:... " + ServiceHost.httpAddress);
        Log.i(PhilTag, "Calculate - NAMESPACE IS:.... " + ServiceHost.webSrv);
        Log.i(PhilTag, "Calculate - ASMX FILE IS:.... " + ServiceHost.ASMXFile);
        Log.i(PhilTag, "Calculate - METHOD NAME IS:.. " + ServiceHost.fName);
        Log.i(PhilTag, "Calculate - SOAP_ACTION IS:.. " + ServiceHost.sOAPACTION);


        String SOAP_ACTION = ServiceHost.sOAPACTION;
        String METHOD_NAME = "Diary_Search_By_Date";
        String NAMESPACE = ServiceHost.nameSpace;
        String URL = ServiceHost.httpAddress;



        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        Request.addProperty("EventDate",diaryDate);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(Request);
        soapEnvelope.implicitTypes = false;

        HttpTransportSE transport = new HttpTransportSE(URL);

        try {
            transport.debug = true;
            transport.call(SOAP_ACTION, soapEnvelope);
            String xml = transport.responseDump;

            // 12/02/2016 - Get rid of those pesky diffgr things............
            xml = xml.replaceAll(" diffgr:id=", ">");

            resultArr = xml.split("<" + xmldelimiter + ">");
            resultArrTrimmed = xml.split("<" + xmldelimiter + ">");

            Integer ArrLength = resultArr.length;
            Log.i(PhilTag, "------------------------------------------------");
            Log.i(PhilTag, "----------HERE IS THE DATA FROM SERVER----------");
            Log.i(PhilTag, "------------------------------------------------");
            Log.i(PhilTag, "XML RAW DATA: " + xml);

            ArrayList<DiarySearchResults> results = new ArrayList<DiarySearchResults>();
            DiarySearchResults sr1 = new DiarySearchResults();

            for (int i = 0; i < ArrLength; ++i) {
                if (resultArr[i].substring(0, 7).equals("<?xml v")) {
                    Log.i(PhilTag, " Ignoring xml tag ");
                    resultArrTrimmed[i] = "  ";
                } else {
                    resultArrTrimmed[i] = resultArr[i].substring(0, resultArr[i].indexOf("</" + xmldelimiter + ">"));
                    resultArrTrimmed[i] = resultArrTrimmed[i].replaceAll("@", " ");



                   // Log.i(PhilTag, "ID: " + ID);


                    sr1 = new DiarySearchResults();
                    sr1.setDetails(FNLib.GetField("details", resultArr[i]));
                    sr1.setId(FNLib.GetField("id", resultArr[i]));


                    results.add(sr1);

                    Log.i(PhilTag, i + " - " + resultArrTrimmed[i]);
                }
            }

            //-------New Code Start
            searchResults = results;
            //-------New Code End

        } catch (Exception ex) {
            Log.e(PhilTag, "******Error********: " + ex.getMessage());
        }
    }
}