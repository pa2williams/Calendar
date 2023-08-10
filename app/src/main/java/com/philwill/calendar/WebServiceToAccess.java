package com.philwill.calendar;

/**
 * Created by USER on 12/01/2016. I'M Very pleased with this !!!! VERY PLEASED!!!!!
 */
public class WebServiceToAccess {

    String webSrv;
    String fName;
    String ASMXFile;
    String httpAddress;
    String nameSpace;
    String sOAPACTION;

    public WebServiceToAccess(String xServer, String aspFunctionName) {

        this.webSrv = "hp-proliant";
        this.fName = aspFunctionName;
        this.ASMXFile = "WebService.asmx";
        this.httpAddress = "http://philwilliams.blogdns.com/webservices/" + ASMXFile;
        //this.httpAddress = "http://192.168.1.169/webservices/" + ASMXFile;

        this.nameSpace = "http://" + webSrv + "/webservices/";
        this.sOAPACTION = "http://" + webSrv + "/webservices/" + fName;


    }


    public void setWebSrv(String webSrv) {
        this.webSrv = webSrv;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setASMXFile(String ASMXFile) {
        this.ASMXFile = ASMXFile;
    }

    public void setHttpAddress(String httpAddress) {
        this.httpAddress = httpAddress;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public void setsOAPACTION(String sOAPACTION) {
        this.sOAPACTION = sOAPACTION;
    }

    public String getWebSrv() {
        return webSrv;
    }

    public String getfName() {
        return fName;
    }

    public String getASMXFile() {
        return ASMXFile;
    }

    public String getHttpAddress() {
        return httpAddress;
    }

    public String getNameSpace() { return nameSpace; }

    public String getsOAPACTION() { return sOAPACTION; }





}




