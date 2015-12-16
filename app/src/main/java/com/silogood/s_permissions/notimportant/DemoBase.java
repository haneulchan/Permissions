
package com.silogood.s_permissions.notimportant;

import android.support.v4.app.FragmentActivity;

import com.silogood.s_permissions.R;
/**
 * Baseclass of all Activities of the Demo Application.
 * 
 * @author Philipp Jahoda
 */
public abstract class DemoBase extends FragmentActivity {

    protected String[] mMonths = new String[] {
            "SEND_SNS", "READ_SNS", "CALL_PHONE", "WRITE_SMS", "RECEIVE_SMS", "WRITE_EXTERMINAL_STORAGE", "READ_PHONE_STATE", "ACCESS_COARSE_LOCATION", "ACCESS_FINE_LOCATION", "READ_CONTACTS", "WRITE_CONTACTS", "INTERNET","CHANGE_WIFI_STATE","ACCESS_NETWORK_STATE","VIBRATE","ACCESS_WIFI_STATE","WAKE_LOCK","RECEIVE_BOOT_COMPLETED"
    };

    protected String[] mParties = new String[] {
            "Normal", "Signature And System", "Dangerous", "DEVELOPMENT"
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
