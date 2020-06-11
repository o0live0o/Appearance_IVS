package com.o0live0o.app.appearance.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import com.o0live0o.app.appearance.ActivityStack;
import com.o0live0o.app.appearance.MyApplication;
import com.o0live0o.app.appearance.data.FinalData;
import com.o0live0o.app.appearance.R;
import com.o0live0o.app.appearance.views.InputWithHeadView;

public class HelpActivity extends BaseActivity {

    private InputWithHeadView iwhC1_NO,iwhLedIp,iwdLedPort;
    private CheckBox chkF1,chkDC,chkC1,chkF1_To_DC,chkIsDispatch,chkFirstCheckDC;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        init();
        readData();
        ActivityStack.getInstance().attach(this);
    }

    private void init(){
        initNavBar(true,getString(R.string.grzx),false);
        iwhC1_NO = findViewById(R.id.help_c1_no);
        chkC1 = findViewById(R.id.chkc1);
        chkDC = findViewById(R.id.help_chkdc);
        chkF1 = findViewById(R.id.help_chkf1);
        chkF1_To_DC = findViewById(R.id.help_chkf1_to_dc);
        chkIsDispatch = findViewById(R.id.help_chkDispatch);
        chkFirstCheckDC= findViewById(R.id.chkDcFirst);
        iwhLedIp = findViewById(R.id.help_led_ip);
        iwdLedPort = findViewById(R.id.help_led_port);
        pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
    }

    private void readData(){
        String c1_no = pref.getString("c1_no",  "1");
        int ledPort =  pref.getInt("udp_port",54321);
        String  ledIp=   pref.getString("udp_ip","192.168.15.30");
        boolean bF1 = pref.getBoolean("chkf1",true);
        boolean bc1 = pref.getBoolean("chkc1",true);
        boolean bDC = pref.getBoolean("chkdc",true);
        boolean bF1_to_DC = pref.getBoolean("chkf1_to_dc",true);
        boolean bIsDspatch = pref.getBoolean("IsDispatch",true);
        boolean bFirstCheckDc =  pref.getBoolean("FirstCheckDC",true);
        iwhC1_NO.setInputStr(c1_no);
        iwdLedPort.setInputStr(String.valueOf(ledPort));
        iwhLedIp.setInputStr(ledIp);
        chkC1.setChecked(bc1);
        chkF1.setChecked(bF1);
        chkDC.setChecked(bDC);
        chkF1_To_DC.setChecked(bF1_to_DC);
        chkIsDispatch.setChecked(bIsDspatch);
        chkFirstCheckDC.setChecked(bFirstCheckDc);
    }

    public void onLoginClick(View view) {
//        Intent intent = new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        FinalData.setOperator("");
//        finish();
        //android.os.Process.killProcess(android.os.Process.myPid());
        ActivityStack.getInstance().exit();

    }

    public void onSaveClick(View view) {
        editor = pref.edit();
        editor.putString("c1_no", iwhC1_NO.getInputStr());
        editor.putString("udp_ip", iwhLedIp.getInputStr());
        editor.putInt("udp_port", Integer.parseInt(iwdLedPort.getInputStr()));
        editor.putBoolean("chkf1",chkF1.isChecked());
        editor.putBoolean("chkc1",chkC1.isChecked());
        editor.putBoolean("chkdc",chkDC.isChecked());
        editor.putBoolean("IsDispatch",chkIsDispatch.isChecked());
        editor.putBoolean("chkf1_to_dc",chkF1_To_DC.isChecked());
        editor.putBoolean("FirstCheckDC",chkFirstCheckDC.isChecked());
        editor.apply();
        showDialog("保存成功,重启程序后生效");
    }
}
