package com.silogood.s_permissions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;


/**
 * Created by ChoiDW on 2015-11-28.
 */
public class Permissions extends AppCompatActivity implements Runnable {    // Runnable 로 쓰레드를 백그라운드에서 데이터처리를 위해 돌림

    private ImageButton manageButton;
    private ImageButton manageButton2;
    private ImageButton manageButton3;
    private ImageButton manageButton4;
    private ImageButton manageButton5;
    private ImageButton manageButton6;
    private ImageButton manageButton7;
    private ImageButton manageButton8;
    private ImageButton manageButton9;
    private static final String TAG = "Permissions";

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";

    // Installed App Details
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private int mDangerousColor;
    private int mDefaultTextColor;

    private static final int PROGRESS_DIALOG = 0;
    private ProgressDialog mProgressDialog;

    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private List<Map<String, String>> mGroupData_S;          //정렬작업을 위한 새로운 리스트 생성
    private List<List<Map<String, String>>> mChildData_S;

    private PackageManager mPm;   //패키지매니저 저장

    PermissionSingleton PS;                                //싱글톤 객체를 이용함


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        manageButton = (ImageButton)findViewById(R.id.CenterBtn);
        manageButton.setImageResource(R.drawable.permission);                      //이미지버튼을 생성
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });

        manageButton = (ImageButton)findViewById(R.id.LocationBtn);
        manageButton.setImageResource(R.drawable.locationicon);
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Permissions.this, HorizontalBarChartActivity.class);
                intent.putExtra("Value", "Location");                           /// 이미지버튼을 눌렀을떄 그 값이 해당 퍼미션 상세구룹으로 전달시키도록함
                startActivity(intent);                                                         //   클릭시 Permissions_in 으로 연결 !!!!!!!!

            }
        });

        manageButton = (ImageButton)findViewById(R.id.MessagesBtn);
        manageButton.setImageResource(R.drawable.locationicon);
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Permissions.this, PieChartActivity.class);
                intent.putExtra("Value", "Location");                           /// 이미지버튼을 눌렀을떄 그 값이 해당 퍼미션 상세구룹으로 전달시키도록함
                startActivity(intent);
            }
        });

        manageButton = (ImageButton)findViewById(R.id.ContactsBtn);
        manageButton.setImageResource(R.drawable.locationicon);
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Permissions.this, Permissions_in.class);
                intent.putExtra("Value", "Location");                           /// 이미지버튼을 눌렀을떄 그 값이 해당 퍼미션 상세구룹으로 전달시키도록함
                startActivity(intent);

            }
        });

        manageButton = (ImageButton)findViewById(R.id.CalendarBtn);
        manageButton.setImageResource(R.drawable.locationicon);
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });




        mPm = getPackageManager();

        // mDangerousColor = getResources().getColor(R.color.perms_dangerous_grp_color);
        mGroupData = new ArrayList<Map<String, String>>();
        mChildData = new ArrayList<List<Map<String, String>>>();
        mGroupData_S = new ArrayList<Map<String, String>>();
        mChildData_S = new ArrayList<List<Map<String, String>>>();
        mDefaultTextColor = Color.DKGRAY;
        PS = PermissionSingleton.getInstance();
        showDialog(PROGRESS_DIALOG);

    };





    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case PROGRESS_DIALOG:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setCancelable(false);
                Thread thread = new Thread(this);
                thread.start();
                return mProgressDialog;
            default:
                return super.onCreateDialog(id, null);
        }
    }



    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            removeDialog(PROGRESS_DIALOG);
            PermissionAdapter mAdapter = new PermissionAdapter(
                    Permissions.this,
                    mGroupData,
                    R.layout.permissions_expandable_list_item,
                    new String[] { NAME, "pack" },                             //퍼미션 한글명이랑 PACK 퍼미션 전체이름 을 부모뷰에 뿌림
                    new int[] { android.R.id.text1, android.R.id.text2 },     // 핸들러부분에서 부모와 차일드를 뷰에 뿌려주는 역할을 함 (이래 런부분에서 받아오는 key값)
                    mChildData,
                    R.layout.permissions_expandable_list_item_child,
                    new String[] { NAME, DESCRIPTION },
                    new int[] { android.R.id.text1, android.R.id.text2 }     // 해당값들을 list_item .  list_item_child 레이아웃안의 객체를 찾아서 뿌려줌
                                                                                // APP 이름, 디스크립션을 뿌려줌
            );
            // Log.i("iver", "XXXXX2" + mGroupData);
            // Log.i("iver", "XXXXX3" + mChildData);
        }
    };

    private class PermissionAdapter extends SimpleExpandableListAdapter {
        public PermissionAdapter(Context context, List<? extends Map<String, ?>> groupData,
                                 int groupLayout, String[] groupFrom, int[] groupTo,
                                 List<? extends List<? extends Map<String, ?>>> childData, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData,
                    childLayout, childFrom, childTo);                                        //어뎁터를 통해서 값들 을 빈깡통에 연결
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            Map<String, String> group = (Map<String, String>) getGroup(groupPosition);
            int secLevel = Integer.parseInt(group.get(SECURITYLEVEL));
            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            if (PermissionInfo.PROTECTION_DANGEROUS == secLevel) {
                textView.setTextColor(mDangerousColor);
            } else {
                textView.setTextColor(mDefaultTextColor);
            }
            return v;                       //뷰가 어떻게 보일지 를 설정해줌   SECURITYLEVEL 값을 받아와서 PROTECTION_DANGEROUS 와 비교를 한후 칼라를 입혀줌
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            final View v = super.getChildView(groupPosition, childPosition, isLastChild,
                    convertView, parent);
            ImageView imageView = (ImageView) v.findViewById(android.R.id.icon);
            Map<String, String> child =
                    (Map<String, String>)getChild(groupPosition, childPosition);
            Drawable icon;
            String packageName = (String)child.get(PACKAGENAME);

            try {
                icon = mPm.getApplicationIcon(packageName);
            } catch (NameNotFoundException e) {
                icon = mPm.getDefaultActivityIcon();
            }
            imageView.setImageDrawable(icon);
            v.setTag(packageName);
            return v;                                                            //차일드 부분은 해당아이콘을 찾아서 옆에 뿌려줌
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void run() {                                               /////실질적으로 값들이 RUNNABLE 을 받아서 쓰레드형태로 돌아가는부분
        mChildData.clear();
        mGroupData.clear();
        List<PackageInfo> appList =
                mPm.getInstalledPackages(PackageManager.GET_PERMISSIONS);                // appList 에  packageManager 를통해 인스톨된 패키지의 퍼미션값들과 정보를저장
        Map<String, List<PackageInfo>> permList = new TreeMap<String, List<PackageInfo>>();
        // Loop through all installed packaged to get a list of used permissions and PackageInfos
        for (PackageInfo pi : appList) {                                                  //  하나하나 값들을 비교하면서 For 문돌림

            // Do not add System Packages
            if (pi.requestedPermissions == null || pi.packageName.equals("android")) {       //pi값 중 퍼미션요구가 Null값이거나 android 로시작하는값이면 컨티뉴
                continue;
            }
            for (String perms : pi.requestedPermissions) {
                                                                                                    //perms  를 다시 pi 의 퍼미션요구값에 맞춰 돌림
                if (!permList.containsKey(perms)) {                                                 // permList PackageInfo 가 저장된 트리에받아온 요구퍼미션값을 찾아서
                    // First time we get this permission so add it and create a new List
                    permList.put(perms, new ArrayList<PackageInfo>());                             //permList 에 새로운값을 추가해줌  (없는값들 찾거나해서 다추가한다고생각하면댐)
                }
                permList.get(perms).add(pi);                                                       //ㅇㅇ 이하동문
            }
        }
        appList.clear();

        Set<String> keys = permList.keySet();

        String sLevel;

        for (String key : keys) {

            Map<String, String> curGroupMap = new HashMap<String, String>();
            try {
                PermissionInfo pinfo =
                        mPm.getPermissionInfo(key, PackageManager.GET_META_DATA);             // key값(요구퍼미션전체이름) 을 이용해서 퍼미션인포값을 뺴내어 pinfo에 저장
                CharSequence label = pinfo.loadLabel(mPm);                                     // 라벨 , 디스크립션같은 값들 따로빼냄
                CharSequence desc = pinfo.loadDescription(mPm);
                curGroupMap.put("pack", key);                                                       // 키값을( "pack" ) 을 따라 저장해서 그룹맵에 추가
                sLevel = String.valueOf(pinfo.protectionLevel);
                curGroupMap.put(SECURITYLEVEL, sLevel);
                curGroupMap.put(NAME, (label == null) ? pinfo.name : label.toString()+"  ★"+sLevel);            // 라벨을 추출해서 스트링형태로 추가
                curGroupMap.put(DESCRIPTION, (desc == null) ? "" : desc.toString());            //디스크립션도 또한 ..

                                                                                                // 레벨도 일단 받아와서 추가해둠
            } catch (NameNotFoundException e) {
                Log.i(TAG, "Ignoring unknown permission " + key);
                continue;
            }
            mGroupData.add(curGroupMap);                                       //그값들을 mGroupData 에 저장해줌


            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            List<PackageInfo> infos = permList.get(key);                        // 그룹( 퍼미션리스트 ) 에 맞는 키값들을 전부 infos 값에 저장
                                                                                // 그룹값을 얻었으니 그에맞는 자식값들을 뽑기위해 리스트생성

            for (PackageInfo child : infos) {                                 // 패키지인포 child 를 infos 에 맞게 하나하나돌림
                Map<String, String> curChildMap = new HashMap<String, String>();          //현재 돌고있는 챠일드를 위한 맵을 생성
                String appName = (child.applicationInfo == null) ?
                        child.packageName : child.applicationInfo.loadLabel(mPm).toString();
                curChildMap.put(NAME, appName);
                curChildMap.put(DESCRIPTION, child.versionName);
                curChildMap.put(PACKAGENAME, child.packageName);                   //////정보를 담음
                curChildMap.put(SECURITYLEVEL, sLevel);
                children.add(curChildMap);                                               // 현재돌고있는값을   퍄일드맵으로 이전
            }
            mChildData.add(children);                                     // mChildData 에 저장
        }


//        for(int i=0; i<mGroupData.size();i++) {
//
//            if(mGroupData.get(i).get("pack").equals("android.permission.INTERNET") ||
//                    mGroupData.get(i).get("pack").equals("android.permission.CAMERA")) {
//                Map<String, String> zzz;
//                List<Map<String, String>> yyy;
//                zzz = mGroupData.get(i);
//                yyy = mChildData.get(i);
//                mGroupData_S.add(zzz);
//                mChildData_S.add(yyy);
//            }
//        }

        PS.setmGroupData(mGroupData);
        PS.setmChildData(mChildData);       // 싱글톤 객체에 지금까지 진행된 정보들을 저장시킴 ( 전역변수로 쓰기위해서 싱글톤에 저장 )

//        mGroupData.clear();
//        mChildData.clear();




        permList.clear();
        handler.sendEmptyMessage(0);
    }

    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }







}
