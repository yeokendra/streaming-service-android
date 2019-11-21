package com.nontivi.nonton.app;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.nontivi.nonton.BuildConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Created by hizkia on 12/03/18.
 */

public class StaticGroup {
    public static final int HOME_TRENDING = 1;
    public static final int HOME_ADS1 = 2;
    public static final int HOME_GENRE = 3;
    public static final int HOME_CHANNEL_LIST = 4;


    public static String os = Build.VERSION.RELEASE;
    public static String VERSION = null;
    public static long VERSION_CODE = 0;

    public static void initialize(Context context) {
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION = i.versionName;
            VERSION_CODE = BuildConfig.VERSION_CODE;
            Log.v(ConstantGroup.LOG_TAG,"VERSION : " + VERSION + " " + VERSION_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//            @Override
//            public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                Log.v(ConstantGroup.LOG_TAG,"StaticGroup", "FCM onComplete: ");
//                if (!task.isSuccessful()) {
//                    KLog.e("StaticGroup", "FCM onComplete: is Not Success " + task.getException());
//                    return;
//                }
//
//                StaticGroup.reg_id = task.getResult().getToken();
//                TpUtil tpUtil = new TpUtil(App.getContext());
//                tpUtil.put(TpUtil.KEY_REG_ID, StaticGroup.reg_id);
//                Log.v(ConstantGroup.LOG_TAG,"StaticGroup", "FCM preference regId : " + tpUtil.getString(TpUtil.KEY_REG_ID, ""));
//            }
//        });
//    }
    
    }

    public static void shareWithShareDialog(Context context, String message, String dialogTitle) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setType("text/plain");
        String title = dialogTitle.isEmpty() ? "Share" : dialogTitle;

        try {
            if (context != null) {
                Intent chooserIntent = Intent.createChooser(sendIntent, title);
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooserIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean openShareIntent(Context context, String packageName, String className, String message, String gaLabel, String alterPackageName) {
        if (context != null && !TextUtils.isEmpty(packageName)) {
            try {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

                if (intent != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    if (!TextUtils.isEmpty(className)) {
                        shareIntent.setPackage(packageName);
                    } else {
                        shareIntent.setClassName(packageName, className);
                    }
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        Log.v(ConstantGroup.LOG_TAG,"openShareIntent : " + packageName);
                        context.startActivity(shareIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!TextUtils.isEmpty(alterPackageName)) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static void syncCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().flush();
        }
    }

    public static void copyToClipboard(Context context, String content) {
        try {
            if (!TextUtils.isEmpty(content)) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", content);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private static void clearCookies(Context context) {
        Log.v(ConstantGroup.LOG_TAG,"clearCookies");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }


//    public static void sendLocalNotification(Context context, String title, String message, String url) {
//        NotificationCompat.Builder builder = StaticGroup.getDefaultNotificationBuilder(context, message, title, null, System.currentTimeMillis(), 0, true);
//        Intent targetIntent;
//        if (!getRecentTaskInfo(context)) {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } else {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
//        targetIntent.putExtra("t_url", url);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        builder.setCategory(Notification.CATEGORY_MESSAGE);
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
//
//        createNotificationChannel(context);
//
//        Notification notification = builder.build();
//        manager.cancel(ConstantGroup.ID_LOCAL_PUSH);
//        manager.notify(ConstantGroup.ID_LOCAL_PUSH, notification);
//
//        // TODO: 20/08/18 check setting
//
//    }

//    public static void sendLocalNotificationWithBigImage(Context context, String title, String message, String url, Bitmap img) {
//        NotificationCompat.Builder builder = StaticGroup.getDefaultNotificationBuilder(context, message, title, null, System.currentTimeMillis(), 0, true);
//        Intent targetIntent;
//        if (!getRecentTaskInfo(context)) {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } else {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
//        targetIntent.putExtra("t_type", "noti");
//        targetIntent.putExtra("t_url", url);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent)
//                .setCategory(Notification.CATEGORY_MESSAGE)
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(img));
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
//
//        createNotificationChannel(context);
//
//        Notification notification = builder.build();
//        manager.cancel(ConstantGroup.ID_LOCAL_PUSH);
//        manager.notify(ConstantGroup.ID_LOCAL_PUSH, notification);
//
//        // TODO: 20/08/18 check setting
//    }

//    private static void createNotificationChannel(Context context) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String name = ConstantGroup.NOTIF_CHANNEL_NAME;
//            String description = ConstantGroup.NOTIF_CHANNEL_DESC;
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    private static String[] findPackNameClass(Context context, String pkName) {
        String[] packName = null;
        PackageManager mPm = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_SEND, null);
        mainIntent.setType("image/jpeg");
        List<ResolveInfo> resolveInfoList = mPm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo != null) {
                String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
                String className = resolveInfo.activityInfo.name;
                ComponentName componentName = new ComponentName(pkgName, className);
                CharSequence title = resolveInfo.loadLabel(mPm);
                Log.v(ConstantGroup.LOG_TAG,pkgName + " " + className + " " + componentName + " " + title);

                if (pkgName.equalsIgnoreCase(pkName)) {
                    packName = new String[2];
                    packName[0] = pkgName;
                    packName[1] = className;
                    break;
                }
            }
        }

        return packName;
    }

    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean isGoogleMarketUrl(String url) {
        return url.startsWith("market://") || ((url.startsWith("http://") || url.startsWith("https://")) && url.contains("play.google.com"));
    }

    public static void shareWithEmail(Context context, String address, String subject, String message) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            if (!TextUtils.isEmpty(address)) {
                Log.v(ConstantGroup.LOG_TAG,"address : " + address);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            }
            if (!TextUtils.isEmpty(subject)) {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            if (!TextUtils.isEmpty(message)) {
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            }

            try {
                String[] pkName = StaticGroup.findPackNameClass(context, "com.google.android.gm");
                if (pkName != null) {
                    if (!TextUtils.isEmpty(pkName[0])) {
                        emailIntent.setComponent(new ComponentName(pkName[0], pkName[1]));
                        context.startActivity(emailIntent);
                    } else {
                        context.startActivity(Intent.createChooser(emailIntent, "Report Select"));
                    }
                } else {
                    context.startActivity(Intent.createChooser(emailIntent, "Report Select"));
                }
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void shareWithSms(Context context, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            try {
                context.startActivity(sendIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            //sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("sms_body", message);
            try {
                context.startActivity(sendIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    public static boolean handleUrl(Context context, String url) {
//        boolean isAlreadyHandled = false;
//        if (isGoogleMarketUrl(url)) {
//            StaticGroup.openGooglePlay(context, url);
//            isAlreadyHandled = true;
//        } else if (url.startsWith("http://") || url.startsWith("https://")) {
//            isAlreadyHandled = false;
//        } else if (url.startsWith("vexgift://link")) {
//            isAlreadyHandled = false;
//        } else if (url.startsWith("vexgift://open.web")) {
//            try {
//                Uri uri = Uri.parse(url);
//                String content = uri.getQueryParameter("url");
//                StaticGroup.openAndroidBrowser(context, content);
//                isAlreadyHandled = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (url.startsWith("vexgift://share")) {
//            try {
//                Uri uri = Uri.parse(url);
//                String method = uri.getQueryParameter("mt");
//                String packageName = uri.getQueryParameter("pn");
//                String alternativePackageName = uri.getQueryParameter("apn");
//                String content = uri.getQueryParameter("msg");
//
//                if (!TextUtils.isEmpty(content)) {
//                    if (!TextUtils.isEmpty(method)) {
//                        if (method.equalsIgnoreCase("sms")) {
//                            StaticGroup.shareWithSms(context, content);
//                            return true;
//                        } else if (method.equalsIgnoreCase("email")) {
//                            StaticGroup.shareWithEmail(context, "", "", content);
//                            return true;
//                        } else if (method.equalsIgnoreCase("copy")) {
//                            StaticGroup.copyToClipboard(context, content);
//                            return true;
//                        }
//                    }
//
//                    if (!TextUtils.isEmpty(packageName)) {
//                        if (packageName.contains("facebook")) {
//                            StaticGroup.copyToClipboard(context, content);
//                        }
//                        StaticGroup.shareWithPackageName(context, packageName, "", content, method, alternativePackageName, "");
//                    } else {
//                        StaticGroup.shareWithShareDialog(context, content, "Share link");
//                    }
//                    isAlreadyHandled = true;
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (url.startsWith("mailto")) {
//            final Activity activity = (Activity) context;
//            if (activity != null) {
//                MailTo mt = MailTo.parse(url);
//                StaticGroup.shareWithEmail(context, mt.getTo(), mt.getSubject(), mt.getBody());
//                isAlreadyHandled = true;
//                Log.v(ConstantGroup.LOG_TAG,"to2 : " + mt.getTo() + "\nsubject2 : " + mt.getSubject() + "\nbody2 : " + mt.getBody());
//            }
//
//        } else if (url.startsWith("vexgift://mail")) {
//            try {
//                Uri uri = Uri.parse(url);
//                String to = uri.getQueryParameter("to");
//                String subject = uri.getQueryParameter("subject");
//                String body = uri.getQueryParameter("body");
//                StaticGroup.shareWithEmail(context, to, subject, body);
//                isAlreadyHandled = true;
//                Log.v(ConstantGroup.LOG_TAG,"to : " + to + "\nsubject : " + subject + "\nbody : " + body);
//            } catch (Exception e) {
//                Log.v(ConstantGroup.LOG_TAG,"e : " + e.getMessage());
//            }
//
//        } else if (url.startsWith("vexgift://notification")) {
//            try {
//                Uri uri = Uri.parse(url);
//
//                String title = uri.getQueryParameter("title");
//                String message = uri.getQueryParameter("msg");
//                String timeStampStr = uri.getQueryParameter("at");
//                long timeStamp = TextUtils.isEmpty(timeStampStr) ? System.currentTimeMillis() : Long.parseLong(timeStampStr) * 1000;
//                String targetUrl = uri.getQueryParameter("url");
//
//                Log.v(ConstantGroup.LOG_TAG,"timeStamp : " + timeStampStr + " / " + timeStamp);
//
//                AlarmUtil.getInstance().startLocalNotiAlarm(context, title, message, targetUrl, timeStamp);
//                isAlreadyHandled = true;
//            } catch (Exception e) {
//                Log.v(ConstantGroup.LOG_TAG,"e : " + e.getMessage());
//            }
//
//        }
//
//        return isAlreadyHandled;
//    }

    public static boolean isInIDLocale() {
        boolean ret = true;

        if (Locale.getDefault() != null && !TextUtils.isEmpty(Locale.getDefault().toString())) {
            ret = Locale.getDefault().toString().contains("in");
        }

        return ret;
    }

//    public static NotificationCompat.Builder getDefaultNotificationBuilder(Context context, String message, String title, Intent targetIntent, long whenTimeStamp, int badgeNumber, boolean autoCancel) {
//
//        NotificationCompat.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
//        } else {
//            builder = new NotificationCompat.Builder(context);
//        }
//
//        builder.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_logo_notif : R.mipmap.ic_launcher);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_notif));
//            builder.setColor(ColorUtil.getColor(context, R.color.point_color));
//        }
//
//        if (autoCancel) {
//            builder.setAutoCancel(true);
//        }
//
//        if (context.getResources() != null && TextUtils.isEmpty(title)) {
//            title = context.getResources().getString(R.string.app_name);
//        }
//        builder.setContentTitle(title);
//
//        if (targetIntent == null) {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//
//        builder.setWhen(whenTimeStamp);
//        if (badgeNumber > 0) {
//            builder.setNumber(badgeNumber);
//        }
//        //----------------------------//
//        builder.setTicker(message);
//        builder.setContentText(message);
//
//        return builder;
//    }

    public static boolean getRecentTaskInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTaskList = am.getRecentTasks(100, Intent.FLAG_ACTIVITY_NEW_TASK);
        if (recentTaskList != null) {
            for (ActivityManager.RecentTaskInfo recentTask : recentTaskList) {
                Intent intent = recentTask.baseIntent;
                ComponentName cName = intent.getComponent();
                if (cName.getPackageName().contains("com.vexanium")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isScreenOn(Context context, boolean defaultValue) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            return pm.isInteractive();
        }

        return defaultValue;
    }


    public static void openVexgiftGooglePlay(Context context) {
        String storeUrl = "https://play.google.com/store/apps/details?id=com.vexanium.vexgift";
        StaticGroup.openGooglePlay(context, storeUrl);
    }

    public static void openGooglePlay(Context context, String marketUrl) {
        if (!TextUtils.isEmpty(marketUrl)) {
            try {
                context.getPackageManager().getPackageInfo("com.android.vending", 0);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.v(ConstantGroup.LOG_TAG,"openGooglePlay via market : " + marketUrl);
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                String replacedUrl;
                if (marketUrl.startsWith("market://")) {
                    String detailUrl = marketUrl.replace("market://", "");
                    replacedUrl = "https://play.google.com/store/apps/" + detailUrl;
                } else {
                    replacedUrl = marketUrl;
                }
                Log.v(ConstantGroup.LOG_TAG,"openGooglePlay via browser : " + replacedUrl);
                openAndroidBrowser(context, replacedUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void shareWithPackageName(Context context, String packageName, String className, String message, String gaLabel, String alterPackageName, String url) {
        if (context != null && !TextUtils.isEmpty(packageName)) {
            boolean isCompleted = openShareIntent(context, packageName, className, message, gaLabel, alterPackageName);
            if (!isCompleted) {
                isCompleted = openShareIntent(context, alterPackageName, "", message, gaLabel, "");
                if (!isCompleted) {

                }
            }
        }
    }

    public static void openAndroidBrowser(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) return;

        try {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PackageManager packageManager = context.getPackageManager();
            Intent fakeIntent = new Intent();
            fakeIntent.setAction("android.intent.action.VIEW");
            fakeIntent.setData(Uri.parse(url));

            ResolveInfo selectedBrowserInfo = packageManager.resolveActivity(fakeIntent, PackageManager.MATCH_DEFAULT_ONLY);

            ActivityInfo targetBrowserInfo = null;

            if (selectedBrowserInfo != null && selectedBrowserInfo.activityInfo != null && !selectedBrowserInfo.activityInfo.name.endsWith("ResolverActivity")) {
                targetBrowserInfo = selectedBrowserInfo.activityInfo;
                Log.v(ConstantGroup.LOG_TAG, "Default Browser package name : " + targetBrowserInfo.applicationInfo.packageName);

            } else {
                fakeIntent.addCategory("android.intent.category.BROWSABLE");
                List<ResolveInfo> localBrowserList = packageManager.queryIntentActivities(fakeIntent, 0);

                if (localBrowserList.size() > 0) {
                    ArrayList<String> browserList = new ArrayList<>();
                    browserList.add("com.UCMobile.intl");
                    browserList.add("com.android.chrome");
                    browserList.add("com.android.browser");
                    browserList.add("org.mozilla.firefox");
                    browserList.add("com.opera.browser");

                    outerLoop:
                    for (String packageName : browserList) {
                        for (ResolveInfo resolveInfo : localBrowserList) {
                            if (resolveInfo != null) {
                                final ActivityInfo activity = resolveInfo.activityInfo;
                                if (activity != null) {
                                    String pkgName = activity.applicationInfo.packageName;
                                    if (pkgName != null && pkgName.equalsIgnoreCase(packageName)) {
                                        targetBrowserInfo = activity;
                                        break outerLoop;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (targetBrowserInfo != null && !TextUtils.isEmpty(targetBrowserInfo.name) && !TextUtils.isEmpty(targetBrowserInfo.applicationInfo.packageName)) {
                Log.v(ConstantGroup.LOG_TAG,"Browser package name : " + targetBrowserInfo.applicationInfo.packageName);
                browserIntent.setClassName(targetBrowserInfo.applicationInfo.packageName, targetBrowserInfo.name);
            } else {
                Log.v(ConstantGroup.LOG_TAG,"Default Browser or Select dialog opened");
            }

            context.startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void showCommonErrorDialog(Context context, String title, String message) {
//        showCommonErrorDialog(context, title, message, null);
//    }
//
//    public static void showCommonErrorDialog(Context context, String title, String message, final CommonErrorDialogListener listener) {
//        new VexDialog.Builder(context)
//                .optionType(DialogOptionType.OK)
//                .title(title)
//                .content(message)
//                .autoDismiss(true)
//                .dismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        if (listener != null) {
//                            listener.onErrorDismiss();
//                        }
//                    }
//                })
//                .show();
//    }
//
//    public static void showCommonErrorDialog(Context context, String message) {
//        showCommonErrorDialog(context, message, (CommonErrorDialogListener) null);
//    }
//
//    public static void showCommonErrorDialog(Context context, String message, CommonErrorDialogListener listener) {
//        String title = context.getString(R.string.cm_dialog_all_error_title);
//        String desc = message;
//        showCommonErrorDialog(context, title, desc, listener);
//    }
//
//    public static void showCommonErrorDialog(Context context, int code) {
//        showCommonErrorDialog(context, code, null);
//    }
//
//    public static void showCommonErrorDialog(Context context, int code, CommonErrorDialogListener listener) {
//        String title = context.getString(R.string.cm_dialog_all_error_title);
//        String desc = String.format(context.getString(R.string.cm_dialog_all_error_desc), String.valueOf(code));
//        showCommonErrorDialog(context, title, desc, listener);
//    }
//
//    public static void showCommonErrorDialog(Context context, HttpResponse errorResponse) {
//        showCommonErrorDialog(context, errorResponse, null);
//    }
//
//    public static void showCommonErrorDialog(Context context, HttpResponse errorResponse, CommonErrorDialogListener listener) {
//        if (errorResponse.getMeta() != null) {
//            Answers.getInstance().logCustom(new CustomEvent("Common Error")
//                    .putCustomAttribute("Error Code", String.valueOf(errorResponse.getMeta().getStatus()))
//                    .putCustomAttribute("Error Message", errorResponse.getMeta().getMessage()));
//            if (errorResponse.getMeta().isRequestError()) {
//                if (errorResponse.getMeta().getStatus() != 408)
//                    showCommonErrorDialog(context, errorResponse.getMeta().getMessage(), listener);
//            } else {
//                showCommonErrorDialog(context, errorResponse.getMeta().getStatus(), listener);
//            }
//        } else {
//            Answers.getInstance().logCustom(new CustomEvent("Unknown Error"));
//            showCommonErrorDialog(context, -1);
//        }
//    }



    public static String getDate(String mDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat dateOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(mDate);
            return dateOutput.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return mDate;
        }
    }

    public static long getDateFromString(String mDate) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat dateOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = dateFormat.parse(mDate);
            calendar.setTime(date);
            return TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
            return TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        }
    }

}
