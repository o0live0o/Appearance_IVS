package com.o0live0o.app.appearance.data;

public class FinalData {

    public final static String F1 = "F1";
    public final static String DC = "DC";
    public final static String C1 = "C1";


    private static String DC_STATION_NO;
    private static boolean CheckF1;
    private static boolean CheckC1;
    private static boolean CheckDC;
    private static boolean F1_To_DC;
    private static boolean FirstChekcDC = false;


    public static boolean isFirstChekcDC() {
        return FirstChekcDC;
    }

    public static void setFirstChekcDC(boolean firstChekcDC) {
        FirstChekcDC = firstChekcDC;
    }

    private static String operator = ""; //操作员
    private static String operator_ID_Car_No = ""; //操作员身份证号
    private static String stationNo = ""; //检验机构编
    private static String webserviceUrl = "";
    private static String webservicekey ="";
    private static String jkxlh = "";

    public static boolean isDiapatchMode() {
        return DIAPATCH_MODE;
    }

    public static void setDiapatchMode(boolean diapatchMode) {
        DIAPATCH_MODE = diapatchMode;
    }

    private  static boolean DIAPATCH_MODE  = false;

    public static String getWebserviceUrl() {
        return webserviceUrl;
    }

    public static void setWebserviceUrl(String webserviceUrl) {
        FinalData.webserviceUrl = webserviceUrl;
    }

    public static String getWebservicekey() {
        return webservicekey;
    }

    public static void setWebservicekey(String webservicekey) {
        FinalData.webservicekey = webservicekey;
    }

    public static String getJkxlh() {
        return jkxlh;
    }

    public static void setJkxlh(String jkxlh) {
        FinalData.jkxlh = jkxlh;
    }

    public static String getStationNo() {
        return stationNo;
    }

    public static void setStationNo(String stationNo) {
        FinalData.stationNo = stationNo;
    }

    public static String getOperator_ID_Car_No() {
        return operator_ID_Car_No;
    }

    public static void setOperator_ID_Car_No(String operator_ID_Car_No) {
        FinalData.operator_ID_Car_No = operator_ID_Car_No;
    }

    public static String getOperator() {
        return operator;
    }

    public static void setOperator(String operator) {
        FinalData.operator = operator;
    }

    public static boolean isCheckF1() {
        return CheckF1;
    }

    public static void setCheckF1(boolean checkF1) {
        CheckF1 = checkF1;
    }

    public static boolean isCheckC1() {
        return CheckC1;
    }

    public static void setCheckC1(boolean checkC1) {
        CheckC1 = checkC1;
    }

    public static boolean isCheckDC() {
        return CheckDC;
    }

    public static void setCheckDC(boolean checkDC) {
        CheckDC = checkDC;
    }

    public static boolean isF1_To_DC() {
        return F1_To_DC;
    }

    public static void setF1_To_DC(boolean f1_To_DC) {
        F1_To_DC = f1_To_DC;
    }

    public static String getDcStationNo() {
        return DC_STATION_NO;
    }

    public static void setDcStationNo(String dcStationNo) {
        DC_STATION_NO = dcStationNo;
    }
}
