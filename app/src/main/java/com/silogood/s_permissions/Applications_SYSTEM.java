package com.silogood.s_permissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.Manifest.permission;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
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
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.TextView;


/**
 * Created by ChoiDW on 2015-11-28.
 */
public class Applications_SYSTEM extends Activity {

    private String title;
    private String description;
    private int thumbnail;
    private static final String TAG = "Permissions";

    private static final String NAME = "Name";
    private static final String SYSTEM = "System";
    private static final String SYSTEMON = "1";
    private static final String SYSTEMOFF = "0";
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

    private List<Map<String, Object>> mGroupData;   //그룹데이터를 이용하므로 리스트 /맵 /스트링 오브젝트형태의 구조를 만듬
    private List<List<Map<String, String>>> mChildData;

    private PackageManager mPm;     // 패키지매니저 객체생성


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mPm = getPackageManager();    //패키지 매니저 받아옴
        mGroupData = new ArrayList<Map<String, Object>>();   //어레이리스트만듬
        mGroupData.clear();
        Drawable icon = null;

        String permissionName;
        String applicationLabel;
        String packageName;
        PackageInfo pi = null;
        int packageVersionCode;
        String packageVersionName;
        int system;
        String se;
        List<ApplicationInfo> appList = mPm.getInstalledApplications(PackageManager.GET_META_DATA);  //인스톨 되어있는애들 패키지매니저에서 뺴서
                                                                                                        //앱리스트에 저장(엡인포)

        // Parcourt chaque package du syst�me
        for (ApplicationInfo ai : appList) {           //for문 으로 ai 값(앱인포)  값을 하나하나 appList 와 비교하면서 전체 프로세스를 돌림


            // R�cup�re le nom du package et si possible le label
            packageName = ai.packageName;                       //패키지네임을 받아오고
            try {
                applicationLabel = mPm.getApplicationLabel(ai).toString();           //라벨을 받아옴
            } catch (Exception ex) { // application not found
                applicationLabel = packageName;
            }

            try {
                icon = mPm.getApplicationIcon(packageName);               //아이콘을 받아옴
            } catch (Exception ex) {
                icon = mPm.getDefaultActivityIcon();
            }

            // R�cup�re si possible les versions
            try {
                pi = mPm.getPackageInfo(packageName, PackageManager.GET_META_DATA);     //패키지인포를 받아와서 코드 네임등을 저장
                packageVersionCode = pi.versionCode;
                packageVersionName = pi.versionName;
            } catch (Exception ex) {
                packageVersionCode = 0;
                packageVersionName = "n/a";
                //Log.e("PM", "Error fetching app version");
            }

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                system = 1;
            else
                system = 0;                  //플래그값으로 시스템인지 아닌지를 구별해옴

            try {
                pi = mPm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            } catch (NameNotFoundException e) {                             //패키지인포값에서 퍼미션을 추출해옴
                e.printStackTrace();
            }

           /// int a = mPm.checkPermission("android.permission_SEND_SMS",packageName);
            Map<String, Object> curGroupMap = new HashMap<String, Object>();      //현재구룹맵을  해쉬맵형태로 생성

            int count = 0;

            try {
                for (String key : pi.requestedPermissions) {              // String Key 값이      pi(패키지인포 안의 이름별 퍼미션애들) 을 하나하나 비교하면서
                    if (key.startsWith("android.permission.")) count++;   // key 값의 시작이 android.permission 으로 시작하는 애들 값을 가져와서 카운트를 늘림
                }
                curGroupMap.put(NAME, applicationLabel + "(" + count + ")");      //현재 그룹맵에 name 값으로 저장
            } catch (NullPointerException e) {
                curGroupMap.put(NAME, applicationLabel + "(" + 0 + ")");
            }
            curGroupMap.put(SYSTEM, system);
            curGroupMap.put(PACKAGENAME, packageName);
            curGroupMap.put("icon", icon);
            //      Log.d("aaa",icon.toString());                                  ////  그룹맵 에 해당 정보들을 저장한후
            // Log.d("YYYYYY",curGroupMap.toString());

            mGroupData.add(curGroupMap);        //최종적으로 mGroupdata에 값을 저장
        }
        appList.clear();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);     //리사이클뷰 생성


        List<Recycler_item> System_items = new ArrayList<>();           //리스트를 시스템 and User app 으로 나누어 저장할 배열리스트생성
        List<Recycler_item> No_System_items = new ArrayList<>();

            for (int i = 0; i < mGroupData.size(); i++) {      // 변수 i 값을 mGroupData 의 전체 사이즈만큼 돌림
                if (SYSTEMON.equals(mGroupData.get(i).get(SYSTEM).toString())) {    ///시스템 값이 1인지 비교해서
                    //Boolean A= mGroupData.equals(SYSTEM);

                    No_System_items.add(new Recycler_item((Drawable) mGroupData.get(i).get("icon"), mGroupData.get(i).get(NAME).toString(), mGroupData.get(i).get(PACKAGENAME).toString()));
                                        //노시스템 배열에 (아이콘)(네임)(패키지명) 을 저장
                    recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), System_items, R.layout.applications_detail));   //레이아웃통해서 뿌려주고싶은데 탭을 적용시켜야할지모름  ㅜㅜㅜㅜㅜ
                                    // 어뎁터로 연결시켜줌
                }else if(SYSTEMOFF.equals(mGroupData.get(i).get(SYSTEM).toString()))
                {                   // 유저app 인 애들 비교문을 거친후
                    System_items.add(new Recycler_item((Drawable) mGroupData.get(i).get("icon"), mGroupData.get(i).get(NAME).toString(), mGroupData.get(i).get(PACKAGENAME).toString()));
         //           recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), No_System_items, R.layout.applications_detail));
                                    //시스템아이템에 저장
                }
            }

            PermissionSingleton PS = PermissionSingleton.getInstance();
            PS.setNo_System_items(No_System_items);
                ///싱글톤에   시스템아이템을 저장


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




}
