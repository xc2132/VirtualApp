package com.lody.virtual.client.hook.patchs.notification;

import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/12.
 */
class NotificationUtil {
    private static Map<Integer, String> sSystemLayoutResIds = new HashMap<Integer, String>(0);

    private static void init() {
        if (sSystemLayoutResIds.size() == 0) {
            try {
                //read all com.android.internal.R
                Class clazz = Class.forName("com.android.internal.R$layout");
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    //public static final
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        try {
                            int id = field.getInt(null);
                            sSystemLayoutResIds.put(id, field.getName());
                        } catch (IllegalAccessException e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public static boolean dealNotification(Object... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Notification) {
                Notification notification = (Notification) args[i];//nobug
                if (isPluginNotification(notification)) {
                    if (shouldBlock(notification)) {
                        return true;
                    } else {
                        //这里要修改通知。
                        hackNotification(notification);
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isPluginNotification(Notification notification) {
        if (notification == null) {
            return false;
        }


        if (notification.contentView != null && !VirtualCore.getCore().isHostPackageName(notification.contentView.getPackage())) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (notification.tickerView != null && !VirtualCore.getCore().isHostPackageName(notification.tickerView.getPackage())) {
                return true;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (notification.bigContentView != null && !VirtualCore.getCore().isHostPackageName(notification.bigContentView.getPackage())) {
                return true;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (notification.headsUpContentView != null && !VirtualCore.getCore().isHostPackageName(notification.headsUpContentView.getPackage())) {
                return true;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.graphics.drawable.Icon icon = notification.getSmallIcon();
            if (icon != null) {
                try {
                    Object mString1Obj = Reflect.on(icon).get("mString1");
                    if (mString1Obj instanceof String) {
                        String mString1 = ((String) mString1Obj);
                        if (!VirtualCore.getCore().isHostPackageName(mString1)) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.graphics.drawable.Icon icon = notification.getLargeIcon();
            if (icon != null) {
                try {
                    Object mString1Obj = Reflect.on(icon).get("mString1");
                    if (mString1Obj instanceof String) {
                        String mString1 = ((String) mString1Obj);
                        if (!VirtualCore.getCore().isHostPackageName(mString1)) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        try {
            Bundle mExtras = Reflect.on(notification).get("extras");
            for (String s : mExtras.keySet()) {
                if (mExtras.get(s) != null && mExtras.get(s) instanceof ApplicationInfo) {
                    ApplicationInfo applicationInfo = (ApplicationInfo) mExtras.get(s);
                    return !VirtualCore.getCore().isHostPackageName(applicationInfo.packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean shouldBlockByRemoteViews(RemoteViews remoteViews) {
        init();
        if (remoteViews == null) {
            return false;
        } else if (remoteViews != null && sSystemLayoutResIds.containsKey(remoteViews.getLayoutId())) {
            return false;
        } else {
            return true;
        }
    }

    static boolean shouldBlock(Notification notification) {
        if (shouldBlockByRemoteViews(notification.contentView)) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (shouldBlockByRemoteViews(notification.tickerView)) {
                return true;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (shouldBlockByRemoteViews(notification.bigContentView)) {
                return true;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (shouldBlockByRemoteViews(notification.headsUpContentView)) {
                return true;
            }
        }
        return false;
    }

    private static void hackNotification(Notification notification) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        //        remoteViews com.android.internal.R.layout.notification_template_material_media
        //        com.android.internal.R.layout.notification_template_material_big_media_narrow;
        //        com.android.internal.R.layout.notification_template_material_big_media;
        //        //getBaseLayoutResource
        //        R.layout.notification_template_material_base;
        //        //getBigBaseLayoutResource
        //        R.layout.notification_template_material_big_base;
        //        //getBigPictureLayoutResource
        //        R.layout.notification_template_material_big_picture;
        //        //getBigTextLayoutResource
        //        R.layout.notification_template_material_big_text;
        //        //getInboxLayoutResource
        //        R.layout.notification_template_material_inbox;
        //        //getActionLayoutResource
        //        R.layout.notification_material_action;
        //        //getActionTombstoneLayoutResource
        //        R.layout.notification_material_action_tombstone;
        if (notification != null) {
            notification.icon = VirtualCore.getCore().getContext().getApplicationInfo().icon;
            Log.i("kk", "intent=" + notification.contentIntent.getIntent());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                hackRemoteViews(notification.tickerView);
            }
            hackRemoteViews(notification.contentView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                hackRemoteViews(notification.bigContentView);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hackRemoteViews(notification.headsUpContentView);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.graphics.drawable.Icon icon = notification.getSmallIcon();
                if (icon != null) {
                    Bitmap bitmap = drawableToBitMap(icon.loadDrawable(VirtualCore.getCore().getContext()));
                    if (bitmap != null) {
                        android.graphics.drawable.Icon newIcon = android.graphics.drawable.Icon.createWithBitmap(bitmap);
                        Reflect.on(notification).set("mSmallIcon", newIcon);
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.graphics.drawable.Icon icon = notification.getLargeIcon();
                if (icon != null) {
                    Bitmap bitmap = drawableToBitMap(icon.loadDrawable(VirtualCore.getCore().getContext()));
                    if (bitmap != null) {
                        android.graphics.drawable.Icon newIcon = android.graphics.drawable.Icon.createWithBitmap(bitmap);
                        Reflect.on(notification).set("mLargeIcon", newIcon);
                    }
                }
            }
        }
    }

    private static void hackRemoteViews(RemoteViews remoteViews) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        if (remoteViews != null && !VirtualCore.getCore().isHostPackageName(remoteViews.getPackage())) {
            if (sSystemLayoutResIds.containsKey(remoteViews.getLayoutId())) {
                Object mActionsObj = Reflect.on(remoteViews).get("mActions");
                if (mActionsObj instanceof Collection) {
                    Collection mActions = (Collection) mActionsObj;
                    String aPackage = remoteViews.getPackage();
                    Resources resources = VirtualCore.getCore().getResources(aPackage);
//                    Application pluginContent =VirtualCore.getCore().getResources() PluginProcessManager.getPluginContext(aPackage);
                    if (resources != null) {
                        Iterator iterable = mActions.iterator();
                        Class TextViewDrawableActionClass = null;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                TextViewDrawableActionClass = Class.forName(RemoteViews.class.getName() + "$TextViewDrawableAction");
                            }
                        } catch (ClassNotFoundException e) {
                        }
                        Class ReflectionActionClass = Class.forName(RemoteViews.class.getName() + "$ReflectionAction");
                        while (iterable.hasNext()) {
                            Object action = iterable.next();
                            if (ReflectionActionClass.isInstance(action)) {//???这里这样是对的么？
                                String methodName = Reflect.on(action).get("methodName");
                                //String methodName;,int type; Object value;
                                if ("setImageResource".equals(methodName)) { //setInt(viewId, "setImageResource", srcId);
                                    Object BITMAP = Reflect.on(action.getClass()).get("BITMAP");
                                    int resId = Reflect.on(action).get("value");
                                    Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
                                    Reflect.on(action).set("type", BITMAP);
                                    Reflect.on(action).set("value", bitmap);
                                    Reflect.on(action).set("methodName", "setImageBitmap");
                                } else if ("setImageURI".equals(methodName)) {//setUri(viewId, "setImageURI", uri);
                                    iterable.remove();   //TODO RemoteViews.setImageURI 其实应该适配的。
                                } else if ("setLabelFor".equals(methodName)) {
                                    iterable.remove();   //TODO RemoteViews.setLabelFor 其实应该适配的。
                                }
                            } else if (TextViewDrawableActionClass != null && TextViewDrawableActionClass.isInstance(action)) {
                                iterable.remove();
//                                if ("setTextViewCompoundDrawables".equals(methodName)) {
//                                    iterable.remove();   //TODO RemoteViews.setTextViewCompoundDrawables 其实应该适配的。
//                                } else if ("setTextViewCompoundDrawablesRelative".equals(methodName)) {
//                                    iterable.remove();   //TODO RemoteViews.setTextViewCompoundDrawablesRelative 其实应该适配的。
//                                }
                            }
                        }
                    }
                }

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Reflect.on(remoteViews).set("mApplication", VirtualCore.getCore().getContext().getApplicationInfo());
            } else {
                Reflect.on(remoteViews).set("mPackage", VirtualCore.getCore().getContext().getPackageName());
            }
        }
    }

    private static Bitmap drawableToBitMap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            return bitmapDrawable.getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
}
