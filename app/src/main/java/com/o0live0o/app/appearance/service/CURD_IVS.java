package com.o0live0o.app.appearance.service;

import com.o0live0o.app.appearance.bean.TempBean;
import com.o0live0o.app.appearance.data.FinalData;
import com.o0live0o.app.appearance.log.L;
import com.o0live0o.app.appearance.bean.CarBean;
import com.o0live0o.app.appearance.bean.ExteriorBean;
import com.o0live0o.app.appearance.enums.CheckState;
import com.o0live0o.app.dbutils.DbResult;
import com.o0live0o.app.dbutils.SSMSHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CURD_IVS implements ICURD {

    private static SSMSHelper ssmsHelper = SSMSHelper.GetInstance();

    @Override
    public <T> DbResult login(String user, String pwd, T t) {
        DbResult dbResult = new DbResult();
        dbResult.setSucc(false);
        String sql = "SELECT COUNT(*) AS ct FROM EMPLOYEE_USER WHERE EMPLOYEE_ID = ?";
        sql = "SELECT EMPLOYEE_NAME FROM EMPLOYEE_USER WHERE EMPLOYEE_ID = ? ";
        List<Object> params = new ArrayList<>();
        params.add(user);
        Map<String, String> map = ssmsHelper.searchSet(sql, params);
        if (map != null && map.size() > 0 && map.containsKey("EMPLOYEE_NAME")) {
            dbResult.setSucc(true);
            dbResult.setMsg(map.get("EMPLOYEE_NAME"));
        }
        else if (map != null && map.size() > 0 && map.containsKey("error")){
            dbResult.setSucc(false);
            dbResult.setMsg(map.get("error"));

        } else {
            dbResult.setSucc(false);
            dbResult.setMsg("登录失败,用户名或密码不正确！");
        }
        return dbResult;
    }

    @Override
    public <T> DbResult getCarList(CarBean car,String type, T t) {
        String sql = "SELECT TOP 20 HPHM AS plateNo,JCLSH AS testId,VIN AS vin,HPZL AS plateType,JYXM AS checkItem ";
        if (car != null && type.equals(FinalData.C1)){
            sql += ", JCXH AS testLine ";
        }
        sql += " FROM VEHICLE_DISPATCH WHERE 1 = 1";

        if (type.equals((FinalData.C1))) {
            sql += "  AND (JCZT_STATUS = 0 or JCZT_STATUS = 1 or JCZT_STATUS = 2)  AND (CHARINDEX('C1',JYXM) > 0  OR CHARINDEX('DC',JYXM) > 0) ";
            if (FinalData.isDiapatchMode())
                sql += " AND ZDGWBH=1000 ";
            else
                sql += " AND (CHARINDEX('C1',YJXM) = 0 OR  CHARINDEX('C1',YJXM) IS NULL) ";
        }
        else if ("00".equals(type))
            sql+=" AND (JCZT_STATUS = 0 or JCZT_STATUS = 1) ";
        else
            sql+=" AND (JCZT_STATUS = 0 or JCZT_STATUS = 1 or JCZT_STATUS = 2)  AND JYXM LIKE '%"+type+"%'";
        if (car != null) {
            if (car.getPlateNo().length() > 0)
                sql += " AND HPHM LIKE '%"+car.getPlateNo()+"%'";
            if (car.getVin().length() > 0)
                sql += " AND VIN LIKE '%"+car.getVin()+"%'";
            if (type.equals(FinalData.C1) && !car.getLineNumber().equals("全部"))
                sql += " AND JCXH = '"+ car.getLineNumber() +"' ";
        }
        sql += " ORDER BY ID DESC";
        DbResult result = ssmsHelper.search(sql);
        return result;
    }

    @Override
    public <T> DbResult saveF1(List<ExteriorBean> list,CarBean car, T t) {
        //TODO 处理数据
        //外检检验项目
        String jyxm = "";
        //外检不合格项目
        String bhgx = "";
        for (ExteriorBean bean:list
        ) {
            if (bean.getItemState() != CheckState.NOJUDGE) {
                if (jyxm == "") {
                    jyxm = bean.getItemId()+"-1";
                } else {
                    jyxm += "," + bean.getItemId()+"-1";
                }
            }

            if (bean.getItemState() == CheckState.FAIL){
                if (bhgx == "") {
                    bhgx = bean.getItemId()+"-1";
                } else {
                    bhgx += "," + bean.getItemId()+"-1";
                }
            }
        }

        List<Object> params = new ArrayList<>();
        params.add(jyxm);
        params.add(bhgx);
        params.add(FinalData.getOperator());
        params.add(car.getTestId());

        String searchSql = "SELECT COUNT(*) AS ct FROM RESULT_CHASISS_MANUAL WHERE JCLSH = '"+car.getTestId()+"'";
        String sql = "";
        if (ssmsHelper.exist(searchSql,null)){
            sql = "UPDATE RESULT_CHASISS_MANUAL SET RGJYBJCX = ?,RGJYBHGX = ?,WGJCCZY=? WHERE JCLSH = ?";

        }else {
            sql = "INSERT INTO RESULT_CHASISS_MANUAL (RGJYBJCX,RGJYBHGX,WGJCCZY,JCLSH) VALUES (?,?,?,?)";
        }
        DbResult dbResult = ssmsHelper.insertAndUpdateWithPara(sql,params);
        L.d(String.valueOf(dbResult.isSucc()));

        //TODO 更新调度表状态

        return dbResult;
    }

    @Override
    public <T> DbResult saveC1(List<ExteriorBean> list,CarBean car, T t) {
        String c1_pd = "0";
        String c1_bhgx = "-";
        String c1_jyxm = "";
        String c1_bhgnr = "无";
        List<ExteriorBean> failList = list.stream().filter((ExteriorBean bean)->bean.getItemState().equals(CheckState.FAIL)).collect(Collectors.toList());
        List<ExteriorBean> passList = list.stream().filter((ExteriorBean bean)->bean.getItemState().equals(CheckState.PASS)).collect(Collectors.toList());

        if (passList.size() > 0){
            c1_pd = "1";
        }

        if (failList.size() > 0){
            c1_pd = "2";
            c1_bhgnr = failList.stream().map(item->item.getItemName()).collect(Collectors.joining(","));
            c1_bhgx = failList.stream().map(item->item.getItemId()+"-1").collect(Collectors.joining(","));
        }



        TempBean tempBean = (TempBean)t;
        List<Object> params = new ArrayList<>();
        params.add(c1_pd);
        params.add(FinalData.getOperator());
        params.add(c1_bhgx);
        params.add(car.getStartTime());
        params.add(car.getEndTime());
        params.add(tempBean.getVal1() == null ?"" :tempBean.getVal1()+","+c1_bhgnr);
        params.add(car.getTestId());

        String searchSql = "SELECT COUNT(*) AS ct FROM RESULT_CHASISS_MANUAL WHERE JCLSH = '"+car.getTestId()+"'";
        String sql = "";
        if (ssmsHelper.exist(searchSql,null)){
            sql = "UPDATE RESULT_CHASISS_MANUAL SET DPBJ_PD = ?,DPBJCZY = ?,DGJYBHGX = ?,KSSJ = ?,JSSJ = ?,DPBJ_MS = ? WHERE JCLSH = ?";

        }else {
            sql = "INSERT INTO RESULT_CHASISS_MANUAL (DPBJ_PD,DPBJCZY,DGJYBHGX,KSSJ,JSSJ,DPBJ_MS,JCLSH) VALUES (?,?,?,?,?,?,?)";
        }
        DbResult dbResult = ssmsHelper.insertAndUpdateWithPara(sql,params);

        List params1 = new ArrayList();
        //保存完后更新调度表状态
        if (dbResult.isSucc() && tempBean.isBval()&& FinalData.isDiapatchMode()) {
            sql = "UPDATE VEHICLE_DISPATCH SET ZDGWBH = ? , LED = ? WHERE JCLSH = ?";
            params1.add(1002);
            params1.add(car.getPlateNo() + "@" + (c1_pd == "1" ? "合格" : "不合格"));
            params1.add(car.getTestId());
            dbResult = ssmsHelper.insertAndUpdateWithPara(sql, params1);
        }else {
            sql = "UPDATE VEHICLE_DISPATCH SET  LED = ? WHERE JCLSH = ?";
            params1.add(car.getPlateNo() + "@" + (c1_pd == "1" ? "合格" : "不合格"));
            params1.add(car.getTestId());
            dbResult = ssmsHelper.insertAndUpdateWithPara(sql, params1);
        }
        if(!FinalData.isDiapatchMode()) //非调度模式下，更新调度表的已检项目
        {
            sql = "UPDATE VEHICLE_DISPATCH SET YJXM = ISNULL(YJXM,'')+'C1,' WHERE JCLSH = ? AND ISNULL(YJXM,'') NOT LIKE '%C1%'";
            params1.clear();
            params1.add(car.getTestId());
            ssmsHelper.insertAndUpdateWithPara(sql, params1);
        }
        return dbResult;
    }

    @Override
    public <T> DbResult saveDC(List<ExteriorBean> list,CarBean car, T t) {
        String dc_pd = "0";
        String dc_bhgx = "-";
        String dc_jyxm = "";

        List<ExteriorBean> failList = list.stream().filter((ExteriorBean bean)->bean.getItemState().equals(CheckState.FAIL)).collect(Collectors.toList());
        List<ExteriorBean> passList = list.stream().filter((ExteriorBean bean)->bean.getItemState().equals(CheckState.PASS)).collect(Collectors.toList());

        if (passList.size() > 0){
            dc_pd = "1";
        }

        if (failList.size() > 0){
            dc_pd = "2";
        }
        dc_bhgx = failList.stream().map(item->item.getItemId()+"-1").collect(Collectors.joining(","));
        List<Object> params = new ArrayList<>();
        params.add(dc_pd);
        params.add(FinalData.getOperator());
        params.add(car.getStartTime());
        params.add(car.getEndTime());
        params.add(car.getTestId());

        String searchSql = "SELECT COUNT(*) AS ct FROM RESULT_CHASISS_MANUAL WHERE JCLSH = '"+car.getTestId()+"'";
        String sql = "";
        if (ssmsHelper.exist(searchSql,null)){
            sql = "UPDATE RESULT_CHASISS_MANUAL SET DTDP_PD = ?,DTDPCZY = ?,KSSJ = ?,JSSJ = ? WHERE JCLSH = ?";

        }else {
            sql = "INSERT INTO RESULT_CHASISS_MANUAL (DTDP_PD,DTDPCZY,KSSJ,JSSJ,JCLSH) VALUES (?,?,?,?,?)";
        }
        DbResult dbResult = ssmsHelper.insertAndUpdateWithPara(sql,params);


        TempBean tempBean = (TempBean)t;

        //保存完后更新调度表状态
        if (dbResult.isSucc() && tempBean.isBval() && FinalData.isDiapatchMode() ) { //调度模式写入状态
            sql = "UPDATE VEHICLE_DISPATCH SET ZDGWBH = ? , LED = ? WHERE JCLSH = ?";
            params.clear();
            params.add(1002);
            params.add(car.getPlateNo() + "@" + (dc_pd == "1" ? "合格" : "不合格"));
            params.add(car.getTestId());
            dbResult = ssmsHelper.insertAndUpdateWithPara(sql, params);
        }else {
            sql = "UPDATE VEHICLE_DISPATCH SET LED = ? WHERE JCLSH = ?";
            params.clear();
            params.add(car.getPlateNo() + "@" + (dc_pd == "1" ? "合格" : "不合格"));
            params.add(car.getTestId());
            dbResult = ssmsHelper.insertAndUpdateWithPara(sql, params);
        }
        if(!FinalData.isDiapatchMode()) //非调度模式下，更新调度表的已检项目
        {
            sql = "UPDATE VEHICLE_DISPATCH SET YJXM = ISNULL(YJXM,'')+'DC,' WHERE JCLSH = ? AND ISNULL(YJXM,'') NOT LIKE '%DC%'";
            params.clear();
            params.add(car.getTestId());
            ssmsHelper.insertAndUpdateWithPara(sql, params);
        }
        return dbResult;
    }

    @Override
    public <T> DbResult sendStatus(String str, CarBean car,String status, T t) {
        DbResult dbResult = null;
        if(FinalData.isDiapatchMode()) {
            List<Object> params = new ArrayList<>();
            String sql = "UPDATE VEHICLE_DISPATCH SET LED = ? ";
            params.add(str);
            if (status.length() > 0) {
                sql += ",ZDGWBH = ? ";
                params.add(status);
            }
            sql += " WHERE JCLSH = ?";
            params.add(car.getTestId());
            dbResult= ssmsHelper.insertAndUpdateWithPara(sql, params);
        }else {
            dbResult = new DbResult();
            dbResult.setSucc(true);
            dbResult.setMsg("状态更新成功");
        }
        return dbResult;
    }

    //提车上线
    @Override
    public <T> DbResult onLine(CarBean car, T t) {
        List<Object> params = new ArrayList<>();
        String sql = "UPDATE VEHICLE_DISPATCH SET JCXH = ?,YCY = ? ,JCZT_STATUS = 1 WHERE JCLSH = ?";
        params.add(t);
        params.add(FinalData.getOperator());
        params.add(car.getTestId());
        DbResult dbResult = ssmsHelper.insertAndUpdateWithPara(sql,params);
        return dbResult;
    }

    @Override
    public <T> DbResult insertOrUpdate(CarBean car, T t) {
        List<Object> params = new ArrayList<>();
        String sql = "UPDATE VEHICLE_DISPATCH SET JCXHMS = ? WHERE JCLSH = ?";
        params.add(t);
        params.add(car.getTestId());
        DbResult dbResult = ssmsHelper.insertAndUpdateWithPara(sql,params);
        return dbResult;
    }
}
