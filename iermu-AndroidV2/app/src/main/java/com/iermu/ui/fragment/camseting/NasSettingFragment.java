package com.iermu.ui.fragment.camseting;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cms.iermu.cmsUtils;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.listener.OnGetNasParamListener;
import com.iermu.client.listener.OnGetSmbFolderListener;
import com.iermu.client.listener.OnSetNasParamListener;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.NasParamResult;
import com.iermu.lan.model.Result;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnCheckedChange;
import com.viewinject.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zsj on 15/12/30.
 */
public class NasSettingFragment extends BaseFragment implements OnGetSmbFolderListener, OnGetNasParamListener, OnSetNasParamListener, CompoundButton.OnCheckedChangeListener {
    @ViewInject(R.id.actionbar_title)
    TextView mActionBarTitle;
    @ViewInject(R.id.nas_switch)
    SwitchButtonNew swNas;
    @ViewInject(R.id.cms_nas_ip)
    EditText etIp;
    @ViewInject(R.id.cms_nas_user)
    EditText etUser;
    @ViewInject(R.id.cms_nas_pwd)
    EditText etPwd;
    @ViewInject(R.id.ck_guest)
    CheckBox ckGuest;
    @ViewInject(R.id.cms_nas_path)
    Spinner spPath;
    @ViewInject(R.id.cms_nas_folder)
    EditText etFolder;
    @ViewInject(R.id.cms_nas_size)
    EditText etSize;
    @ViewInject(R.id.btn_scan)
    TextView btnScanNas;

    ArrayList<String> list_path;
    ArrayList<Boolean> path_smb = new ArrayList<Boolean>();
    ;
    ArrayAdapter<String> path_adapter;

    private int count = 0;

    boolean swNasCheck = false;

    boolean isSmb;

    private Map map = new HashMap();


    private String deviceId;
    private static final String INTENT_DEVICEID = "deviceId";
    Context c = ErmuApplication.getContext();
    IMineRecordBusiness business;
    private CommonCommitDialog cusDialog;
    private String uid;

    public static Fragment actionInstance(String deviceId) {
        NasSettingFragment fragment = new NasSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.iermu_setting);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mActionBarTitle.setText(getResources().getString(R.string.nas_setting));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nas_setting, container, false);
        ViewHelper.inject(this, view);
        uid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
        deviceId = getArguments().getString(INTENT_DEVICEID);
        business = ErmuBusiness.getMineRecordBusiness();
        business.getNasParam(c, deviceId, uid);

        business.registerListener(OnGetNasParamListener.class, this);
        business.registerListener(OnGetSmbFolderListener.class, this);
        business.registerListener(OnSetNasParamListener.class, this);
        swNas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swNasCheck = isChecked;
            }
        });

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etIp.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        business.unRegisterListener(OnGetNasParamListener.class, this);
        business.unRegisterListener(OnGetSmbFolderListener.class, this);
        business.unRegisterListener(OnSetNasParamListener.class, this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


//    ckGuest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            etUser.setText("guest");
//            etPwd.setText("");
//        }
//    });


    @OnCheckedChange(value = {R.id.ck_guest})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            etUser.setText("guest");
            etPwd.setText("");
        } else {
            etUser.setText("");
            etPwd.setText("");
        }
    }


    @OnClick(value = {R.id.actionbar_back, R.id.btn_scan, R.id.cms_nas_sure})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_back:
                popBackStack();
                break;
            case R.id.btn_scan:
                final String ip = etIp.getText().toString().trim();
                final String[] strIP = cmsUtils.split(ip, ".");
                boolean bOk = true;
                if (strIP == null || strIP.length != 4) { // 提示输入ip地址错误
                    bOk = false;
                } else {
                    for (int i = 0; i < 4; i++) {
                        if (!cmsUtils.isNumeric(strIP[i])) {
                            bOk = false;
                            break;
                        }
                    }
                }

                if (!bOk) {
                    Toast.makeText(c, getResources().getString(R.string.dev_rec_nas_ip_err),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
//                btnScanNas.setEnabled(false);
                list_path = new ArrayList<String>();
                path_smb = new ArrayList<Boolean>();
                String username = etUser.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                count = 0;
                path_adapter.clear();
                map = new HashMap();
                business.getSmbFolder(ip,username,pwd);
                business.getNfsPath(c,deviceId,uid,ip);
                cusDialog = new CommonCommitDialog(getActivity());
                cusDialog.show();
                cusDialog.setStatusText(getString(R.string.loading_data));
                break;
            case R.id.cms_nas_sure:
                String Ip = etIp.getText().toString().toString();
                if (Ip == null) return;
                String[] strT = cmsUtils.split(Ip, ".");
                if (strT.length != 4 || TextUtils.isEmpty(strT[3])) {
                    Toast.makeText(c, getResources().getString(R.string.dev_rec_nas_err_ip), Toast.LENGTH_SHORT).show();
                    return;
                }
                int iSize = Integer.parseInt(etSize.getText().toString());
                if (iSize < 4) {
                    Toast.makeText(c, getResources().getString(R.string.dev_rec_nas_err_size), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (path_smb.size() > 0) {
                    int co = spPath.getSelectedItemPosition();
                    isSmb = path_smb.get(co);
                }
                business.setNasParam(c, deviceId, uid, swNasCheck, etUser.getText().toString().trim(), etPwd.getText().toString().trim(), spPath.getSelectedItem().toString(), Ip, iSize, isSmb);
                break;
        }
    }


    @Override
    public void OnGetNasParamListener(NasParamResult result) {
        Context c = ErmuApplication.getContext();
        if(result.getErrorCode()!= ErrorCode.SUCCESS){
            Toast.makeText(c, R.string.dev_rec_nas_param_fail,Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap map = result.getMap();
        isSmb = (Boolean) map.get("isSmb");
        swNasCheck = (Boolean) map.get("bNasON");
        swNas.setSwitchOn((Boolean) map.get("bNasON"));
        etIp.setText((String) map.get("ip"));

        etUser.setText((String) map.get("uName"));
        etPwd.setText((String) map.get("pwd"));

        String[] strNasPath = new String[]{(String) map.get("nasPath")};
        list_path = new ArrayList<String>(Arrays.asList(strNasPath));
//        path_smb = new ArrayList<Boolean>();  // true: samba  false: nfs
        path_adapter = new ArrayAdapter<String>(c, R.layout.cms_spinner, list_path);
//        path_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // path
        spPath.setAdapter(path_adapter);
        spPath.setSelection(0);

        // folder
        etFolder.setText(deviceId);

        // size
        etSize.setText((String) map.get("size"));

    }

    @Override
    public void OnGetSmbFolderListener(Result result) {
        count++;
        if (("".equals(result.getResultString()) || result.getResultString() == null) && count == 2) {
            Toast.makeText(c, R.string.btn_nas_scan_no,
                    Toast.LENGTH_SHORT).show();
            if (cusDialog != null) cusDialog.dismiss();
        }

        String strPaths = result.getResultString();
        String[] strNfsPath = cmsUtils.split(strPaths, "\n");
        if (strNfsPath == null) return;

        int iLen = strNfsPath.length;
        for(int i=0; i<iLen; i++){
            if (map.get(strNfsPath[i])==null){
                list_path.add(strNfsPath[i]);
                path_smb.add(result.isResultBooean());
                Log.i("nastype", strNfsPath[i]+" isNas:"+(result.isResultBooean()?"true":"false"));
                path_adapter.add(strNfsPath[i]);
                map.put(strNfsPath[i],true);
            }
        }
        count++;
        path_adapter.notifyDataSetChanged();
        spPath.performClick();
        if (cusDialog != null) cusDialog.dismiss();

    }

    @Override
    public void OnSetNasParamListener(Result result) {
        Context c = ErmuApplication.getContext();
        if (result.isResultBooean()) {
            Toast.makeText(c, R.string.tip_savemenu_ok,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(c, R.string.tip_savemenu_fail,
                    Toast.LENGTH_SHORT).show();
        }

    }
}
