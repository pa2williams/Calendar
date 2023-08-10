package com.philwill.calendar;


import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by USER on 18/01/2016.
 */
public class MyClasses {

    public String ReadConfig(Context ctx) // 7th January 2016
            // Added the Context ctx 30th Jan '16 to get openFileInput to work.
    {

        try {
            InputStream in = ctx.openFileInput("Config.txt");
            if (in != null) {
                InputStreamReader tmp = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                StringBuilder buf = new StringBuilder();
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
                in.close();

                //UpdateResult = xml.substring(xml.indexOf("<UpdateUAResult>") + 16, xml.indexOf("</UpdateUAResult>"));
                //String BN = buf.toString();
                //return BN.substring(BN.indexOf("<Bandname>") + 10, BN.indexOf("</Bandname>"));

                return buf.toString();

                //Toast
                //     .makeText(this, buf , Toast.LENGTH_LONG)
                //    .show();
                //TextView LogData;
                //LogData = (TextView) findViewById(R.id.textbox);
                //LogData.setText(buf.toString());
                //LogData.setTextColor(Color.BLUE);
            }
        } catch (java.io.FileNotFoundException e) {

// that's OK, we probably haven't created it yet

        } catch (Throwable t) {
            //Toast
                 //   .makeText(MyClasses.this, "Exception: " + t.toString(), Toast.LENGTH_LONG)
                  //  .show();
        }

        return "Error reading configuration file";
    }



    public static int minFunction(int n1, int n2) {
        int min;
        if (n1 > n2)
            min = n2;
        else
            min = n1;

        return min;
    }

    public static void xmain() {
        int a = 11;
        int b = 6;
        int c = minFunction(a, b);
        System.out.println("Minimum Value = " + c);
    }

    String WebServiceName;

    public static String WebServiceName() {

        return "hp-proliant";
    }


    public String getWebServiceName() {

        return "hp-proliant";

    }

    public static Boolean isFuture (String DateToCheck) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(DateToCheck);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (new Date().after(strDate)) {
            return false;
        }
        else
        {
            return true;
        }

    }


    public String GetField(String FieldName, String ArrayElement) {
        // 12th February 2016

        String xmlSectionStart = "<" + FieldName + ">";
        String xmlSectionEnd = "</" + FieldName + ">";
        String returnString;

        // Added 23rd February 2016 to Catch instances of section start or section end not found.
        try {
            returnString = ArrayElement.substring(ArrayElement.indexOf(xmlSectionStart) + xmlSectionStart.length(), ArrayElement.indexOf(xmlSectionEnd));
            return returnString;}
        catch (Exception e) {
            return "???";
        }

    }

    public String getFulldate(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy");
        Date dateAsDate = stringToDate(strDate);
        String fullDate = sdf.format(dateAsDate);
        return fullDate;
    }

    public Date stringToDate(String strDate){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            //Toast.makeText(getApplicationContext(), "Cant convert date " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return date;
    }

    public String today()

    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;

    }

    public String todayWithSeperators()

    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;

    }


    public String ToCurrency(String stringToFormat)

    {

        Double x;
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        x = new Double(stringToFormat.toString());
        return format.format(x);

    }





}
