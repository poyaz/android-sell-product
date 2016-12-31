package com.example.woods.amin;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class config {
    public static String DB_NAME = "sell";

    public static String REGEX_EMAIL_ADDRESS = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static String REGEX_INT_VALID = "^([1-9]|([1-9][0-9]{0,8})|(1[1-9]|21)([1-4][1-7][1-4][1-8][1-3][1-6][1-4][1-7]))$";
    public static String REGEX_PHONE_NUMBER = "^([0-9]+)$";

    public static Integer PASSWORD_LENGTH = 4;

    public static Integer REQUEST_PERMISSION_ALL = 0;
    public static Integer REQUEST_PERMISSION_READ_CONTACTS = 1;
    public static Integer REQUEST_PERMISSION_EXTERNAL_READ = 2;

    public static Integer ANDROID_INTENT_SELECT_IMAGE = 100;
    public static Integer ANDROID_INTENT_SELECT_IMAGES = 101;
    public static Integer ANDROID_INTENT_SELECT_CONTACT = 102;

    public static Integer REQUEST_INTENT_ADD_PRODUCT_ACTIVITY = 1000;
    public static Integer REQUEST_INTENT_EDIT_PRODUCTS_ACTIVITY = 1001;
    public static Integer REQUEST_INTENT_VIEW_PRODUCT_ACTIVITY = 1002;
    public static Integer REQUEST_INTENT_ORDER_PRODUCTS_ACTIVITY = 1003;
    public static Integer REQUEST_INTENT_USER_ACTIVITY = 1004;
    public static Integer REQUEST_INTENT_SETTINGS_ACTIVITY = 1005;
    public static Integer REQUEST_INTENT_ACCEPT_ORDER_ACTIVITY = 1006;

    public static Integer BROADCAST_NOTIFICATION = 10000;

    public static Integer ORDERS_STATUS_PRE_ORDER = 0;
    public static Integer ORDERS_STATUS_ACCEPT = 1;
    public static Integer ORDERS_STATUS_PAY = 2;
    public static Integer ORDERS_STATUS_SEND = 3;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void changeDirection(Window window) {
        window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    public static void removeFocus(Window window) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static String getRealPath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
