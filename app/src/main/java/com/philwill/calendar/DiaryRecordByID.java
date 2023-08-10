package com.philwill.calendar;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DiaryRecordByID extends AppCompatActivity {

    String logTag = "PhilWill";

    String resultArr[]; // This will hold the list of categories as returned from the Web Service
    String resultArrTrimmed[]; // This will hold the reformatted list of categories as returned from the Web Service

    //---18/01/2016-------The following parameters can be changed to the respective service
    String aspxserver = "";
    String aspxMethod = "Diary_Search_By_ID";   //18183
    String aspxID = "18183";
    String xmldelimiter = "Diary_Search_By_ID";

    Integer recordsFoundCount;

    String iD, year, dayName, dateofEvent, dateDifference, detail, category, subCategory, weather,stones,lbs,bmi,average_weight,current_weight;

    String testDetail = "Initialisation Value";

    String attachments;

    View view;
    ArrayList searchResults;
    MyClasses FNLib;
    WebServiceToAccess ServiceHost;
    WebServiceToAccess ServiceHostAttachments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FNLib = new MyClasses();
        setContentView(R.layout.diary_record);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            try {
                aspxID  = extras.getString("ID").trim();
            } catch (Exception ex) {
                // ex.getMessage();
            }
        }


        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

      //  Objects.requireNonNull(getSupportActionBar()).setTitle("THIS WEEK " + FNLib.today().substring(0, 6));

        aspxserver = FNLib.getWebServiceName();
        ServiceHost = new WebServiceToAccess(aspxserver, aspxMethod);

        ProgressBar pb;
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        Get_Data();
    }




    public void Get_Data() {

        //Log.i(logTag, ": START");

        FNLib = new MyClasses();

        aspxserver = FNLib.getWebServiceName();
        ServiceHost = new WebServiceToAccess(aspxserver, aspxMethod);

        DiaryRecordByID.AsyncCallWS task = new AsyncCallWS();

        task.execute();

    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i(logTag, "doInBackground");
            //*******************************************
            getDiaryRecord();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(logTag, "onPostExecute START");
            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);


            TextView eventdate = findViewById(R.id.tvw_dateOfEvent);
            eventdate.setText(dateofEvent);

            TextView categoryx = findViewById(R.id.tvw_category);
            categoryx.setText(category);

            TextView subcategoryx = findViewById(R.id.tvw_subCategory);
            subcategoryx.setText(subCategory);


            TextView details = findViewById(R.id.tvw_eventDetails);
            details.setText(detail);

            getAttachmentsFromServer();


            Log.i("ClassTest", "**********************Detail is " + detail);
        }
    }

    public void getDiaryRecord() {
        Log.i(logTag, "Calculate START");
        Log.i(logTag, "Calculate - URL IS:... " + ServiceHost.httpAddress);
        Log.i(logTag, "Calculate - NAMESPACE IS:.... " + ServiceHost.webSrv);
        Log.i(logTag, "Calculate - ASMX FILE IS:.... " + ServiceHost.ASMXFile);
        Log.i(logTag, "Calculate - METHOD NAME IS:.. " + ServiceHost.fName);
        Log.i(logTag, "Calculate - SOAP_ACTION IS:.. " + ServiceHost.sOAPACTION);

        String SOAP_ACTION = ServiceHost.sOAPACTION;
        String METHOD_NAME = ServiceHost.fName;
        String NAMESPACE = ServiceHost.nameSpace;
        String URL = ServiceHost.httpAddress;

        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        Request.addProperty("ID", aspxID);

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
            recordsFoundCount = resultArr.length;
            Log.i(logTag, "------------------------------------------------");
            Log.i(logTag, "----------HERE IS THE DATA FROM SERVER----------");
            Log.i(logTag, "------------------------------------------------");
            Log.i(logTag, "XML RAW DATA: " + xml);

            ArrayList<DiaryRecordSearchResults> results = new ArrayList<DiaryRecordSearchResults>();
            DiaryRecordSearchResults sr1 = new DiaryRecordSearchResults();
            String previousYear = "";
            for (int i = 0; i < ArrLength; ++i) {
                if (resultArr[i].substring(0, 7).equals("<?xml v")) {
                    Log.i(logTag, " Ignoring xml tag ");
                    resultArrTrimmed[i] = "  ";
                } else {
                    resultArrTrimmed[i] = resultArr[i].substring(0, resultArr[i].indexOf("</" + xmldelimiter + ">"));
                    resultArrTrimmed[i] = resultArrTrimmed[i].replaceAll("@", " ");

                    iD = FNLib.GetField("ID", resultArr[i]);
                    year = FNLib.GetField("Year", resultArr[i]);
                    dayName = FNLib.GetField("DayName", resultArr[i]);
                    dateofEvent = FNLib.GetField("EventDate", resultArr[i]);
                    dateDifference = FNLib.GetField("DateDifference", resultArr[i]);
                    detail = FNLib.GetField("ShortDetail", resultArr[i]);
                    category = FNLib.GetField("Category", resultArr[i]);
                    subCategory = FNLib.GetField("SubCategory", resultArr[i]);
                    attachments = FNLib.GetField("Attachments", resultArr[i]);
                    weather = FNLib.GetField("Weather", resultArr[i]);

                    // 28th January 2021
                    stones = FNLib.GetField("Stones", resultArr[i]);
                    lbs = FNLib.GetField("lbs", resultArr[i]);
                    bmi = FNLib.GetField("BMI", resultArr[i]);
                    average_weight = FNLib.GetField("Average_Weight", resultArr[i]);

                    current_weight = FNLib.GetField("Current_Weight", resultArr[i]);

                    Log.i(logTag, "Detail is >>>> " + i + "  " + detail);


                    sr1 = new DiaryRecordSearchResults();






                    Log.i(logTag, "ID: " + iD);

                    // sr1 = new DiarySearchResults();

                    sr1.setId(iD);
                    sr1.setyear(year);
                    sr1.setdayName(dayName.substring(0, 3));
                    sr1.setdateofEvent(dateofEvent);
                    sr1.setdateDifference(dateDifference);
                    sr1.setdetail(detail);
                    sr1.setcategory(category);
                    sr1.setsubCategory(subCategory);

                    sr1.setStones(stones);
                    sr1.setLbs(lbs);
                    sr1.setBmi(bmi);
                    sr1.setAverage_weight(average_weight);

                    sr1.setCurrent_weight(current_weight);
                    sr1.setweather(weather);
                    sr1.setAttachments(attachments);


                    results.add(sr1);

                    Log.i(logTag, i + " - " + resultArrTrimmed[i]);
                }

            }
            //-------New Code Start
            searchResults = results;
            //-------New Code End

        } catch (Exception ex) {
            Log.e(logTag, "******Error********: " + ex.getMessage());

        }
    }
    
    ////////////// Attachments starts here ///////////////

    public void Get_Attachments() {

        String aspxserver = "";
        String aspxMethod = "Diary_Search_By_ID";   //18183
        String aspxID = "18183";
        String xmldelimiter = "Diary_Search_By_ID";

        String SOAP_ACTION = ServiceHost.sOAPACTION;

        String METHOD_NAME = ServiceHost.fName;

        String NAMESPACE = ServiceHost.nameSpace;

        String URL = ServiceHost.httpAddress;




        //showToast(PID,"Information");
        Log.i(logTag, ": START");
        Log.i(logTag, ": URL IS:... " + ServiceHostAttachments.httpAddress);
        Log.i(logTag, ": NAMESPACE IS:.... " + ServiceHostAttachments.webSrv);
        Log.i(logTag, ": Calculate - ASMX FILE IS:.... " + ServiceHostAttachments.ASMXFile);
        Log.i(logTag, ": Calculate - METHOD NAME IS:.. " + ServiceHostAttachments.fName);
        Log.i(logTag, ": Calculate - SOAP_ACTION IS:.. " + ServiceHostAttachments.sOAPACTION);
        //Log.i(logTag, ": Calculate - SEARCH TEXT IS:.. " + aspxParam);

//        Process process = new ProcessBuilder()
//                .command("logcat", "-c").redirectErrorStream(true)
//                .start();

        AsyncCallAttchments task = new AsyncCallAttchments();

        task.execute();

    }

    private class AsyncCallAttchments extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.VISIBLE);
            Log.i(logTag, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i(logTag, "doInBackground");
            //*******************************************
            getAttachmentsFromServer();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(logTag, ">>>>>>>>>>>>>>>>>>>>onPostExecute START");

           /* Intent wv = new Intent(getApplicationContext(), WebView_PAW.class);
            startActivity(wv);*/

            ProgressBar pb;
            pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);

            final ListView lv2 = (ListView) findViewById(R.id.lvw_attachments);
            lv2.setAdapter(new AttachmentsListAdapter(getApplicationContext(), searchResults));
            //lv2.setAdapter(new DiarySearchListAdapter(DiarySearch.this, searchResults));


            //diaryRecord.putExtra("ID", ((BMIAvgByYearSearchResults) o).getId());
            //diaryRecord.putExtra("ItemDate", ((BMIAvgByYearSearchResults) o).getxDate());

            // Add a header to the ListView
           /* LayoutInflater inflater = getLayoutInflater();
            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.listview_header, lv2, false);
            lv2.addHeaderView(header);
            lv2.smoothScrollToPosition(0);

            // Add a footer to the ListView
            //LayoutInflater inflater1 = getLayoutInflater();*/
            //ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.listview_footer, lv2, false);
        /*
            addFooterView(View v)
                Add a fixed view to appear at the bottom of the list.

            addFooterView(View v, Object data, boolean isSelectable)
                Add a fixed view to appear at the bottom of the list.
         */
            // So, this footer is non selectable
           // lv2.addFooterView(footer, null, false);

            if (recordsFoundCount == 1) {
                // ToastMessage("No attachment records were found.");

            }
            // Position Listview at end
            lv2.setSelection(lv2.getCount() - 1);

            // Make Listview rows selectable
            lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    Object o = lv2.getItemAtPosition(position);
                    AttachmentsSearchResults fullObject = (AttachmentsSearchResults) o;
// Open Android Browser

                    String attachmentFileName = ((AttachmentsSearchResults) o).getFileAddress();
                    String attachmentFileType = ((AttachmentsSearchResults) o).getFileType();

                    ////////////////27 & 28th June 2022///////////////////////////////////////////////////////

                    File filey = new File(((AttachmentsSearchResults) o).getOriginalFileName());
                    /*if(filey.exists()){
                        ToastMessage(((AttachmentsSearchResults) o).getOriginalFileName());
                    } */


                    if (!((AttachmentsSearchResults) o).getOriginalFileName().trim().equals("None Specified") && filey.exists()) {

                        Intent intentx = new Intent();
                        intentx.setAction(Intent.ACTION_VIEW);
                        intentx.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intentx.setDataAndType(Uri.parse("file://" + ((AttachmentsSearchResults) o).getOriginalFileName()), "image/*");
                        startActivity(intentx);

                    } else {


                        //////////////////////////////////////////////////////////////////////


                        String docURL = "";
                        Log.i(logTag, "====== At case statement ");
                        switch (attachmentFileType) {

                            case "JPG":
                            case "PNG":
                            case "TXT":
                                //ToastMessage("JPG/PNG File " + attachmentFileName);
                                docURL = "http://www.philwill.com/gallery/displayimage.aspx?imagename=" + attachmentFileName;
                                docURL = "http://www.philwill.com/diary/androiddownloadimage.aspx?imagefilename=" + attachmentFileName;
                                Log.i(logTag, "jpg,png,txt ... " + docURL);


                                break;
                            case "PDF":
                                // ToastMessage("PDF File " + attachmentFileName);
                                Log.i(logTag, "***** PDF File " + attachmentFileName + "   *****");

                                docURL = "http://www.philwill.com/myfiles/" + attachmentFileName;
                                docURL = "http://philwilliams.blogdns.com/MyFiles/" + attachmentFileName;
                                //docURL = "www.philwill.com/myfiles/" + attachmentFileName;
                                Log.i(logTag, "PDF ... " + docURL);


                          /*  Intent diaryRecord = new Intent(getApplicationContext(), DiaryRecord.class);
                            diaryRecord.putExtra("ID", ((DiarySearchResults) o).getId());
                            */


                                break;
                            case "DOCX":
                            case "DOC":

                                //ToastMessage("DOCX/DOC File " + attachmentFileName);
                                docURL = "http://www.philwill.com/myfiles/" + attachmentFileName;
                                Log.i(logTag, "doc, docx ... " + docURL);
                                break;
                            default:
                                //ToastMessage("Don't Know What File Type This Is? " + attachmentFileType);
                                docURL = "http://www.philwill.com/myfiles/" + attachmentFileName;
                                Log.i(logTag, "Default ... " + docURL);
                                break;
                        }

                        ////// put this back
                        //Intent wv = new Intent(getApplicationContext(), WebView_PAW.class);
                        //wv.putExtra("URL_Address",docURL);
                        //startActivity(wv);


                        Log.i(logTag, "====== End of case statement ");
//                    String pathx = Environment.getExternalStorageDirectory().toString()+"/Download/";
//                    Log.i(logTag, "pathx:>>>>>>>> " + pathx);
//                    File directory = new File(pathx);
//                    File[] files = directory.listFiles();
//                    Log.i(logTag, "Size: "+ files.length);
//                    for (int i = 0; i < files.length; i++)
//                    {
//                        Log.d("Files", "FileName:" + files[i].getName());
//                    }

                        //String fileName = Environment.DIRECTORY_DOWNLOADS + "/" + attachmentFileName;

                        //String fileName = "/storage/emulated/0/Download/" + attachmentFileName;

// file wasn't deleting so trying this......
                        File path = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS);

                        File file = new File(path, attachmentFileName.trim());

                        String fileName = file.toString();

                        Log.i(logTag, "Full File Name " + file.getParent() + file.getName());

                        if (file.exists()) {
                            // ToastMessage(file.toString());
                            // ToastMessage(attachmentFileName + " Already exists");
                            Log.i(logTag, file.toString() + " ***************Exists");
                            boolean deleted = file.delete();
                            if (deleted) {
                                //ToastMessage(fileName + " Deleted");
                                Log.i(logTag, fileName + " ***************Deleted");
                            } else {
                                //ToastMessage(fileName + " Not Deleted");
                                Log.i(logTag, fileName + " ***************Not Deleted");
                            }

                            File filex = new File(path, "attachmentFileName");
                            filex.delete();

                        } else {
                            // ToastMessage(fileName);
                            // ToastMessage(fileName + " Does not exist");
                            Log.i(logTag, fileName + " ***************Does Not Exist");
                            //ToastMessage("File Does Not Exist");
                        }


                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(docURL));
                            // ToastMessage("Intent Starting" + docURL );
                            startActivity(intent);
                            // ToastMessage("Intent Finished" + docURL );

                        } catch (Exception ex) {
                            ToastMessage("Cant start Intent" + ex.toString());
                        }


                    }


                    //
                }

            });

            // Position at bottom of Listview
            /*
            final Button button_bottom;
            button_bottom = (Button) findViewById(R.id.btn_bottom);
            button_bottom.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // ToastMessage("Bottom");
                            // lv2.setSelection(new AttachmentsSearchListAdapter(AttachmentsSearch.this, searchResults).getCount() - 1);
                            Log.i(logTag, "lv2 Move bottom. ");
                            lv2.setSelection(lv2.getCount() - 1);
                        }
                    }
            );

            // Position at top of Listview
            final Button button_top;
            button_top = (Button) findViewById(R.id.btn_top);
            button_top.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // ToastMessage("Bottom");
                            Log.i(logTag, "lv2 Move top. ");
                            lv2.setSelection(0);

                        }
                    }
            );
*/
            Log.i(logTag, ">>>>>>>>>>>>>>>>>>>>onPostExecute END");

        }
    }

    public void getAttachmentsFromServer() {

        String aspxserver = "";
        String aspxMethod = "DiaryAttachments";   //18183
        String aspxID = "20117";
        String xmldelimiter = "DiaryAttachments";
        aspxserver = FNLib.getWebServiceName();
        ServiceHost = new WebServiceToAccess(aspxserver, aspxMethod);
        ServiceHost = new WebServiceToAccess(aspxserver, aspxMethod);
        ServiceHostAttachments = new WebServiceToAccess(aspxserver, "DiaryAttachments");



        Log.i(logTag, "Calculate START");
        Log.i(logTag, "Calculate - URL IS:... " + ServiceHostAttachments.httpAddress);
        Log.i(logTag, "Calculate - NAMESPACE IS:.... " + ServiceHostAttachments.webSrv);
        Log.i(logTag, "Calculate - ASMX FILE IS:.... " + ServiceHostAttachments.ASMXFile);
        Log.i(logTag, "Calculate - METHOD NAME IS:.. " + ServiceHostAttachments.fName);
        Log.i(logTag, "Calculate - SOAP_ACTION IS:.. " + ServiceHostAttachments.sOAPACTION);

        String SOAP_ACTION = ServiceHostAttachments.sOAPACTION;
        String METHOD_NAME = "DiaryAttachments";
        String NAMESPACE = ServiceHostAttachments.nameSpace;
        String URL = ServiceHostAttachments.httpAddress;

        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        //SoapObject Request = new SoapObject(NAMESPACE, "DiaryAttachments");
        Request.addProperty("Parent_ID", "20042");

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
            xmldelimiter = "DiaryAttachments";
            resultArr = xml.split("<" + xmldelimiter + ">");
            resultArrTrimmed = xml.split("<" + xmldelimiter + ">");

            Integer ArrLength = resultArr.length;
            recordsFoundCount = resultArr.length;
            Log.i(logTag, "------------------------------------------------");
            Log.i(logTag, "----------HERE IS THE DATA FROM SERVER----------");
            Log.i(logTag, "------------------------------------------------");
            Log.i(logTag, "XML RAW DATA: " + xml);

            ArrayList<AttachmentsSearchResults> results = new ArrayList<AttachmentsSearchResults>();
            AttachmentsSearchResults sr1 = new AttachmentsSearchResults();

            for (int i = 0; i < ArrLength; ++i) {
                if (resultArr[i].substring(0, 7).equals("<?xml v")) {
                    Log.i(logTag, " Ignoring xml tag ");
                    resultArrTrimmed[i] = "  ";
                } else {
                    resultArrTrimmed[i] = resultArr[i].substring(0, resultArr[i].indexOf("</" + xmldelimiter + ">"));
                    resultArrTrimmed[i] = resultArrTrimmed[i].replaceAll("@", " ");

                    sr1 = new AttachmentsSearchResults();
                    //sr1.setUser_ID(user_ID);

                    sr1.setUploadDate(FNLib.GetField("Upload_Date", resultArr[i]));
                    sr1.setDetails(FNLib.GetField("Comments", resultArr[i]));
                    sr1.setFileAddress(FNLib.GetField("AttachmentAddress", resultArr[i]));
                    sr1.setFileType(FNLib.GetField("FileType", resultArr[i]));
                    sr1.setOriginalFileName(FNLib.GetField("Original_File_Name", resultArr[i]));

                    results.add(sr1);

                    Log.i(logTag, i + " - " + resultArrTrimmed[i]);
                }

            }
            //-------New Code Start
            searchResults = results;
            //-------New Code End

        } catch (Exception ex) {
            Log.e(logTag, "******Error********: " + ex.getMessage());
            ToastMessage(ex.getMessage());
        }
    }

    public void ToastMessage(String message) {
      //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
