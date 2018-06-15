package com.rgk.android.translator;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import android.content.Context;

import com.rgk.android.translator.database.TranslatorStorage;

/**
 * Activity that requests permissions needed for activities exported from Contacts.
 */
public class RequestPermissionsActivity extends RequestPermissionsActivityBase {

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            // "Contacts" group. Without this permission, the Contacts app is useless.
            //permission.READ_CONTACTS,
            // "Phone" group. This is only used in a few places such as QuickContactActivity and
            // ImportExportDialogFragment. We could work around missing this permission with a bit
            // of work.
            //permission.READ_CALL_LOG,
            /// M: The basic permissions of Contacts. If not have, Contacts can't be used. @{
            permission.READ_PHONE_STATE,
            //permission.WRITE_CONTACTS,
            //permission.CALL_PHONE,
            //permission.GET_ACCOUNTS
            /// @}
    };

    @Override
    protected String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }

    @Override
    protected String[] getDesiredPermissions() {
        return new String[]{
                //permission.ACCESS_FINE_LOCATION, // Location Group
                //permission.READ_CONTACTS, // Contacts group
                //permission.READ_CALL_LOG, // Permission group phone
                //permission.READ_CALENDAR, // Calendar group
                //permission.READ_SMS, // SMS group
                /// M: The desired permissions of Contacts. If not have, some
                // functions can't be used. @{
                permission.READ_PHONE_STATE,
                //permission.WRITE_CONTACTS,
                //permission.CALL_PHONE,
                //permission.GET_ACCOUNTS,
                //permission.ACCESS_COARSE_LOCATION,
                //permission.READ_EXTERNAL_STORAGE,
                //permission.WRITE_EXTERNAL_STORAGE
                /// @}
        };
    }

    public static boolean startPermissionActivity(Activity activity) {
        return startPermissionActivity(activity, REQUIRED_PERMISSIONS,
                RequestPermissionsActivity.class);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        if (permissions != null && permissions.length > 0
                && isAllGranted(permissions, grantResults)) {
            TranslatorStorage.getInstance().update(TranslatorStorage.getInstance().getUser()); //update device_id
            mPreviousActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mPreviousActivityIntent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            Toast.makeText(this, R.string.missing_required_permission, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * M: Add for check basic permissions state.
     */
    public static boolean hasBasicPermissions(Context context) {
        return hasPermissions(context, REQUIRED_PERMISSIONS);
    }
}
