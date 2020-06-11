package com.o0live0o.app.appearance.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.o0live0o.app.appearance.MyApplication;
import com.o0live0o.app.appearance.adapters.ChekItemAdapter.CheckUnpassItem;
import com.o0live0o.app.appearance.bean.C1Bean;
import com.o0live0o.app.appearance.bean.ResultBean;
import com.o0live0o.app.appearance.bean.TempBean;
import com.o0live0o.app.appearance.service.CURDHelper;
import com.o0live0o.app.appearance.data.ExteriorList;
import com.o0live0o.app.appearance.data.FinalData;
import com.o0live0o.app.appearance.service.ICURD;
import com.o0live0o.app.appearance.log.L;
import com.o0live0o.app.appearance.R;
import com.o0live0o.app.appearance.adapters.ChekItemAdapter;
import com.o0live0o.app.appearance.bean.CarBean;
import com.o0live0o.app.appearance.bean.ExteriorBean;
import com.o0live0o.app.appearance.enums.CheckState;
import com.o0live0o.app.appearance.listener.ExteriorChangeListener;
import com.o0live0o.app.appearance.service.WebServiceHelper;
import com.o0live0o.app.appearance.utils.CreateXML;
import com.o0live0o.app.appearance.utils.ResourceMan;
import com.o0live0o.app.appearance.views.LabelView;
import com.o0live0o.app.dbutils.DbResult;
import com.o0live0o.app.dbutils.SSMSHelper;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class C1Activity extends BaseActivity {

    private RecyclerView mRV;
    private EditText mEtRemark;
    private ChekItemAdapter mChekItemAdapter;
    private List<ExteriorBean> mList;
    private CarBean mCar;

    private Button mBtnStart;
    private Button mBtnCapture;
    private Button mBtnSubmit;

    private Map<String,List<String>> remarkMap;
    private Map<String,List<String>> subItemMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c1);
        init();
    }

    private void init(){
        initNavBar(true,"底盘检查",false);
        mRV = findViewById(R.id.c1_rv_checklist);
        mEtRemark = findViewById(R.id.c1_et_remark);
        remarkMap = new HashMap<>();
        subItemMap = new HashMap<>();
        mCar = getIntent().getParcelableExtra("carInfo");
        initBoard(mCar.getPlateNo(),mCar.getTestId(),mCar.getLineNumber());

        initControl();

        mList = ExteriorList.getC1List();
        mChekItemAdapter = new ChekItemAdapter(this,mList);
        mChekItemAdapter.setCheckUnpassItem(new CheckUnpassItem(){
            @Override
            public void onCheck(int i,boolean b) {
                try {
                    if (mRV.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                            && !mRV.isComputingLayout()) {
                        if (b) {

                            List<Integer> list = new ArrayList<>();
                            String[] s1 = getResources().getStringArray(ResourceMan.getResId("c1_" + mList.get(i).getItemId(), R.array.class));
                            AlertDialog alertDialog = new AlertDialog.Builder(C1Activity.this)
                                    .setTitle("不合格原因")
                                    .setMultiChoiceItems(s1, null, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                            if (isChecked) {
                                                list.add(which);
                                            } else {
                                                Iterator iterator = list.iterator();
                                                while (iterator.hasNext()) {
                                                    int tempI = (Integer) iterator.next();
                                                    if (tempI == which) {
                                                        iterator.remove();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            for (int j = 0; j < list.size(); j++) {

                                                String tempStr = s1[list.get(j)];
                                                if (!remarkMap.containsKey(String.valueOf(mList.get(i).getItemId())))
                                                    remarkMap.put(String.valueOf(mList.get(i).getItemId()), new ArrayList<>());
                                                if (!remarkMap.get(String.valueOf(mList.get(i).getItemId())).contains(tempStr))
                                                    remarkMap.get(String.valueOf(mList.get(i).getItemId())).add(tempStr);

                                                String tempSubItem = String.valueOf(mList.get(i).getItemId())+ "_"+String.valueOf(list.get(j)+1);
                                                if (!subItemMap.containsKey(String.valueOf(mList.get(i).getItemId())))
                                                    subItemMap.put(String.valueOf(mList.get(i).getItemId()), new ArrayList<>());
                                                if (!subItemMap.get(String.valueOf(mList.get(i).getItemId())).contains(tempSubItem))
                                                    subItemMap.get(String.valueOf(mList.get(i).getItemId())).add(tempSubItem);
                                            }
                                            //updateReamrk();
                                        }
                                    })
                                    .create();
                            alertDialog.show();

                        } else {
                            if (remarkMap.containsKey(String.valueOf(mList.get(i).getItemId())))
                                remarkMap.get(String.valueOf(mList.get(i).getItemId())).clear();
                            if (subItemMap.containsKey(String.valueOf(mList.get(i).getItemId())))
                                subItemMap.get(String.valueOf(mList.get(i).getItemId())).clear();
                            //updateReamrk();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mChekItemAdapter.setExteriorChangeListener(new ExteriorChangeListener() {
            @Override
            public void onChange(int poison, CheckState checkState) {
                try {
                    if(mRV.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                            && !mRV.isComputingLayout()) {
                        mList.get(poison).setItemState(checkState);
                        mChekItemAdapter.notifyItemChanged(poison);
                    }
                } catch (Exception ex) {
                    L.d(ex.getMessage());
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRV.setLayoutManager(linearLayoutManager);
        mRV.setAdapter(mChekItemAdapter);
    }

    private void initControl(){
        mBtnStart = findViewById(R.id.c1_btn_start);
        mBtnCapture = findViewById(R.id.c1_btn_capture);
        mBtnSubmit = findViewById(R.id.c1_btn_submit);
        changeBtnState(true,false,false);
    }

    private void updateReamrk(){
        StringJoiner stringJoiner = new StringJoiner(";");
        for (List<String> tempList : remarkMap.values()) {
            for (String s : tempList
            ) {
                stringJoiner.add(s);
            }
        }
        mEtRemark.setText(stringJoiner.toString());

    }

    private String getSubItems(){
        StringJoiner stringJoiner = new StringJoiner(",");
        for (List<String> tempList : subItemMap.values()) {
            for (String s : tempList
            ) {
                stringJoiner.add(s);
            }
        }
        return stringJoiner.toString();
    }

    @Override
    protected void onDestroy() {
        BaseActivity.RunThread = false;
        super.onDestroy();
    }

    public void onSubmit(View view) {
        mCar.setEndTime(getTime());
        new SubmitTask().execute(mEtRemark.getText().toString());
    }

    public void onPre(View view) {
        new StatusTask().execute(((Button)view).getText().toString(),"");
    }

    public void onCaputure(View view) {
        new SendService().execute("", "803");   //拍照指令由工位触发
        changeBtnState(false,false,true);
    }

    public void onStart(View view) {
        mCar.setStartTime(getTime());
        new SendStartSerivce().execute();

    }

    //更新LED状态
    class StatusTask extends AsyncTask<String,Void,DbResult>{

        @Override
        protected DbResult doInBackground(String... strings) {
            String led= strings[0];
            return CURDHelper.sendStatus(mCar.getPlateNo()+"@"+led,mCar,"");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("","更新状态……");
        }

        @Override
        protected void onPostExecute(DbResult dbResult) {
            super.onPostExecute(dbResult);
            hideProgressDialog();
        }
    }

    //结束检测
    class SubmitTask extends AsyncTask<String,Void, DbResult> {

        @Override
        protected DbResult doInBackground(String... strings) {

            String subItems = getSubItems();
            TempBean tempBean = new TempBean();
            tempBean.setVal1(strings[0]);
            tempBean.setVal2(subItems);
            tempBean.setBval(true);
            if (FinalData.isDiapatchMode() && mCar.getCheckItem().contains(FinalData.DC) && FinalData.isF1_To_DC() && !FinalData.isFirstChekcDC())
                tempBean.setBval(false);

            //CURDHelper.sendStatus(mCar.getPlateNo() + "@" + "检测完成", mCar, "1002");
            DbResult dbResult = CURDHelper.saveC1(mList, mCar, tempBean);
            return dbResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("", "正在保存……");
        }

        @Override
        protected void onPostExecute(DbResult dbResult) {
            super.onPostExecute(dbResult);
            hideProgressDialog();
            if (dbResult.isSucc()) {
                L.i("底盘数据保存成功！");
                showDialog("保存成功");
                if (FinalData.isF1_To_DC() && mCar.getCheckItem().contains(FinalData.DC) && !FinalData.isFirstChekcDC()) {
                    Intent intent = new Intent(C1Activity.this, DCActivity.class);
                    intent.putExtra("carInfo", mCar);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            } else {
                L.i("底盘数据保存失败！");
                showDialog("保存失败:" + dbResult.getMsg());
            }
        }
    }

    //开始
    class SendStartSerivce extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            ResultBean resultBean = new ResultBean();
            DbResult dbResult = CURDHelper.sendStatus(mCar.getPlateNo() + "@" + "人工检查", mCar, "1001");
            resultBean.setSucc(dbResult.isSucc());
            resultBean.setMsg(dbResult.getMsg());
            return resultBean.isSucc();// + ":" + resultBean.getMsg();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("","正在开始……");
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            hideProgressDialog();
            if (s) {
                changeBtnState(false, true, false);
                startTimes();
            } else
                showToast("发送检测指令失败，请重新发送!");
        }
    }

    //发送拍照指令
    class SendService extends AsyncTask<String,Void,DbResult>{
        @Override
        protected DbResult doInBackground(String... DbResult) {
           return CURDHelper.insertOrUpdate(mCar,"101");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("","正在拍照……");
        }

        @Override
        protected void onPostExecute(DbResult s) {
            super.onPostExecute(s);
            hideProgressDialog();
            if (!s.isSucc())
                showToast("发送拍照指令失败,请重新发送");
        }
    }

    //改变按钮的状态
    private void changeBtnState(boolean bstart,boolean bcapture,boolean bsubmit){

        mBtnStart.setEnabled(bstart);
        mBtnStart.setBackgroundColor(bstart ? Color.parseColor("#87CEEB"): Color.parseColor("#D3D3D3"));

        mBtnCapture.setEnabled(bcapture);
        mBtnCapture.setBackgroundColor(bcapture ? Color.parseColor("#87CEEB"): Color.parseColor("#D3D3D3"));

        mBtnSubmit.setEnabled(bsubmit);
        mBtnSubmit.setBackgroundColor(bsubmit ? Color.parseColor("#87CEEB"): Color.parseColor("#D3D3D3"));
    }


}