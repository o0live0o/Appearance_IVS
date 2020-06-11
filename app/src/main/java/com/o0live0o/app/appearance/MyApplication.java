package com.o0live0o.app.appearance;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.o0live0o.app.appearance.data.FinalData;
import com.o0live0o.app.appearance.service.ByteArrayBuffer;
import com.o0live0o.app.appearance.service.ByteTools;
import com.o0live0o.app.appearance.service.CRC16;
import com.o0live0o.app.appearance.service.UdpTool;
import com.o0live0o.app.appearance.service.WebServiceHelper;
import com.o0live0o.app.dbutils.SSMSHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyApplication extends Application {

    private static Context context;

    private SharedPreferences pref;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        init();
    }

    public static Context getContext() {
        return context;
    }

    private void init() {

//        StringBuilder sb = new StringBuilder();
//        int upLen = "111".length();
//        int downLen = "11111".length();
//
//        byte[] totalLenByte = new byte[]{0x08};// ByteTools.intToByteArray(upLen+downLen);
//        byte[] upLenByte = new byte[]{0x03};// ByteTools.intToByteArray(upLen);
//        byte[] downByte = new byte[]{0x05};// ByteTools.intToByteArray(downLen);
//        ByteArrayBuffer bufferArray = new ByteArrayBuffer();
//       byte[] y11 =  "成都弥荣科技发展有限公司".getBytes();
//       int len1 = y11.length;
//       byte[] b12 = "成都弥荣科技公司".getBytes();
//        int len2 = b12.length;
//        byte[] b111 = ByteTools.intToByteArray(len1);
//        bufferArray.append(new byte[]{0x01});
//        bufferArray.append(upLenByte);
//        bufferArray.append(downByte);
//        bufferArray.append("111".getBytes());
//        bufferArray.append("11111".getBytes());
//        int crcVal = CRC16.CRC16_MODBUS(bufferArray.toByteArray());
//        ByteArrayBuffer buffer = new ByteArrayBuffer();
//
//        buffer.append(new byte[]{0x02});
//        buffer.append(new byte[]{0x01});
//        buffer.append(new byte[]{0x01});
//        buffer.append(totalLenByte);
//        buffer.append(upLenByte);
//        buffer.append(downByte);
//        buffer.append("111".getBytes());
//        buffer.append("11111".getBytes());
//        buffer.append(ByteTools.intToByteArray(crcVal));
//        buffer.append(new byte[]{0x10,0x11});
//
//        byte[] data = buffer.toByteArray();
//        String s = ByteTools.bytes2HexString(data);
        pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);

        String dataBase = pref.getString("database", this.getString(R.string.db_name));
        String user = pref.getString("db_user",  this.getString(R.string.db_user));
        String pwd = pref.getString("db_pwd",  this.getString(R.string.db_pwd));
        String server = pref.getString("db_server",  this.getString(R.string.db_ip));
        String instance = pref.getString("db_instance",  "");

        String web_url = pref.getString("web_url",getString(R.string.web_url));
        String stationNo = pref.getString("station_no",getString(R.string.web_station_no));
        String web_key = pref.getString("web_key",getString(R.string.web_key));
        String web_jkxlh = pref.getString("web_jkxlh",getString(R.string.web_jcxlh));
        String web_org= pref.getString("web_org",getString(R.string.web_org));

        int UdpPort = pref.getInt("udp_port",54321);
        String UdpIp = pref.getString("udp_ip","192.168.15.30");

        FinalData.setCheckC1(pref.getBoolean("chkc1",true));
        FinalData.setCheckDC(pref.getBoolean("chkdc",true));
        FinalData.setCheckF1(pref.getBoolean("chkf1",true));
        FinalData.setF1_To_DC(pref.getBoolean("chkf1_to_dc",true));
        FinalData.setFirstChekcDC(pref.getBoolean("FirstCheckDC",false));
        FinalData.setDcStationNo(pref.getString("c1_no","1"));
        FinalData.setDiapatchMode(pref.getBoolean("IsDispatch",true));
        FinalData.setStationNo(stationNo);
        FinalData.setWebserviceUrl(web_url);
        FinalData.setWebservicekey(web_key);
        FinalData.setJkxlh(web_jkxlh);

        SSMSHelper.GetInstance().init(
                dataBase,
                server,
                user,
                pwd,instance,context);

        UdpTool.init(UdpIp,UdpPort,1);
        WebServiceHelper.getInstance().init(web_url,web_org);
    }
}
