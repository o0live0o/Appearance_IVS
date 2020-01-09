package com.o0live0o.app.appearance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.o0live0o.app.appearance.R;
import com.o0live0o.app.appearance.activitys.F1Activity;
import com.o0live0o.app.appearance.bean.CarBean;
import com.o0live0o.app.appearance.data.FinalData;

import java.util.List;


public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.ViewHolder> {


    private Context mContext;
    private List<CarBean> mCarList;
    private String mCheckType;

    private View mItemView;

    public CarListAdapter(Context context,List<CarBean> list,String checkType){
        this.mContext = context;
        this.mCarList = list;
        this.mCheckType = checkType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_list_car,viewGroup,false);
        return new ViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final CarBean car = mCarList.get(i);
        viewHolder.tvPlateNo.setText(car.getPlateNo());
        viewHolder.tvPlateType.setText(car.getPlateType());
        viewHolder.tvTestId.setText(car.getTestId());
        viewHolder.tvVIN.setText(car.getVin());
        viewHolder.tvItem.setText(car.getCheckItem());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "com.o0live0o.app.appearance.activitys." + mCheckType+"Activity";
                //如果程序流程先检测底盘，但是项目只有DC，则直接跳转到DC检测
                if (!FinalData.isFirstChekcDC() && mCheckType.equals(FinalData.C1) && !car.getCheckItem().contains(FinalData.C1) && car.getCheckItem().contains(FinalData.DC)) {
                    if (!FinalData.isF1_To_DC()) return;
                    className = "com.o0live0o.app.appearance.activitys.DCActivity";
                }

                if (FinalData.isFirstChekcDC()){
                    className = "com.o0live0o.app.appearance.activitys.DCActivity";
                    if ( mCheckType.equals(FinalData.C1)&& !car.getCheckItem().contains(FinalData.DC)&& car.getCheckItem().contains(FinalData.C1))
                        className = "com.o0live0o.app.appearance.activitys.C1Activity";
                }


                try {
                    Class activity = Class.forName(className);
                    Intent intent = new Intent(mContext, activity);
                    intent.putExtra("carInfo",car);
                    mContext.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCarList!=null) return mCarList.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        //ImageView imageView;
        TextView tvPlateNo;
        TextView tvPlateType;
        TextView tvVIN;
        TextView tvTestId;
        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            //imageView = itemView.findViewById(R.id.car_item_logo);
            tvPlateType = itemView.findViewById(R.id.car_item_plateType);
            tvPlateNo = itemView.findViewById(R.id.car_item_plateNo);
            tvVIN = itemView.findViewById(R.id.car_item_vin);
            tvTestId = itemView.findViewById(R.id.car_item_testId);
            tvItem = itemView.findViewById(R.id.car_item_testItem);
        }
    }
}
