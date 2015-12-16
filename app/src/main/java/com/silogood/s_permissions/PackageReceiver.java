package com.silogood.s_permissions;

/**
 * Created by 7217-182 on 2015-12-01.
 */

import android.content.BroadcastReceiver;
import  android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by silogood on 2015-11-19.
 */
public class PackageReceiver extends BroadcastReceiver {







    @Override
    public void onReceive(Context context, Intent intent) {


        // Get application status(Install/ Uninstall)
        boolean applicationStatus = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        String toastMessage = null;
        Log.v("XXXX5", "     Intent ?   "+intent);
        // Check if the application is install or uninstall and display the message accordingly
        if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
                                                                         //브로드캐스트로 인텐드값을  (" ADD " ) 로받았는지를 비교해서 명령을 수행

            String A = intent.getData().toString();                     // 인텐트에서 우리가 필요한 스트링값을 추출
            // Application Install
            Log.v("XXXX5", "     1  "+A);
            toastMessage = "PACKAGE_INSTALL: "+  intent.getData().toString() + getApplicationName(context, intent.getData().toString(), PackageManager.GET_UNINSTALLED_PACKAGES);
            Intent i = new Intent(context,Applications_Permissions.class);            // 그 인텐트값을 뿌려주기위해서 클래스연결을 해주고 TITLE 이라는 이름으로 보냄
            i.putExtra("title", A);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                             // 새로운 Marketplay 에서는 액티비티를 띄워줄떄 단계별로 들어가는 액티비티가 아님으로
                                                                                    // 새로운 테스크를 만들고 멀티테스크 위에다가 뿌림 ( 프로세스중 우리 액티비티가 자르고들어오는느낌 )
            i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(i);

        }else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){
            // Application Uninstall

        }else if(intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){

        }

        //Display Toast Message
        if(toastMessage != null){
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method get application name name from application package name
     */
    private String getApplicationName(Context context, String data, int flag) {

        final PackageManager pckManager = context.getPackageManager();
        ApplicationInfo applicationInformation;
        try {
            applicationInformation = pckManager.getApplicationInfo(data, flag);
        } catch (PackageManager.NameNotFoundException e) {                     // 패키지에서 이름을 추출해내는과정(라벨)
            applicationInformation = null;
        }
        final String applicationName = (String) (applicationInformation != null ? pckManager.getApplicationLabel(applicationInformation) : "(unknown)");

        return applicationName;
    }
}

