package com.qiaomu.libpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    private static final String SUFFIX = "$$PermissionProxy";

    public static void requestPermissions(Activity object, int requestCode, String... permissions) {
        _requestPermissions(object, requestCode, permissions);
    }

    public static void requestPermissions(Fragment object, int requestCode, String... permissions) {
        _requestPermissions(object, requestCode, permissions);
    }

    private static boolean shouldShowRequestPermissionRationale(final Object context, final int requestCode, final String[] permissions) {
        Activity source = getActivity(context);
        PermissionProxy proxy = findPermissionProxy(source);
        if (!proxy.needShowRationale(requestCode, permissions)) return false;
        List<String> rationalList = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(source, permission)) {
                rationalList.add(permission);
            }
        }


        if (!rationalList.isEmpty()) {
            return proxy.rationale(source, requestCode, permissions, new PermissionRationalCallback() {
                @Override
                public void onRationalExecute() {
                    _doRequestPermissions(context, requestCode, permissions);
                }
            });
        }
        return false;
    }

    @NonNull
    private static Activity getActivity(Object context) {
        Activity source = null;
        if (context instanceof Activity) {
            source = (Activity) context;
        } else if (context instanceof Fragment) {
            source = ((Fragment) context).getActivity();
        }
        if (source == null) {
            throw new IllegalStateException("context is null");
        }
        return source;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void _requestPermissions(Object object, int requestCode, String... permissions) {
        if (!Utils.isOverMarshmallow()) {
            doExecuteSuccess(object, requestCode, permissions);
            return;
        }

        boolean rationale = shouldShowRequestPermissionRationale((Activity) object, requestCode, permissions);
        if (rationale) {
            return;
        }

        _doRequestPermissions(object, requestCode, permissions);
    }

    private static void _doRequestPermissions(Object object, int requestCode, String[] permissions) {
        List<String> deniedPermissions = Utils.findDeniedPermissions(getActivity(object), permissions);
        if (deniedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(object), permissions, requestCode);
        } else {
            doExecuteSuccess(object, requestCode, permissions);
        }
    }


    private static PermissionProxy findPermissionProxy(Object activity) {
        try {
            Class clazz = activity.getClass();
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            return (PermissionProxy) injectorClazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format(" generate class not found %s , please check the file is exit.", activity.getClass().getSimpleName() + SUFFIX));
    }


    private static void doExecuteSuccess(Object activity, int requestCode, String... permission) {
        findPermissionProxy(activity).grant(activity, requestCode, permission);

    }

    private static void doExecuteFail(Object activity, int requestCode, String... permission) {
        findPermissionProxy(activity).denied(activity, requestCode, permission);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        requestResult(activity, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        requestResult(fragment, requestCode, permissions, grantResults);
    }

    private static void requestResult(Object obj, int requestCode, String[] permissions, int[] grantResults) {
        List<String> grant = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                denied.add(permissions[i]);
            } else {
                grant.add(permissions[i]);
            }
        }

        if (!grant.isEmpty()) {
            doExecuteSuccess(obj, requestCode, grant.toArray(new String[grant.size()]));
        }

        if (!denied.isEmpty()) {
            doExecuteFail(obj, requestCode, denied.toArray(new String[denied.size()]));
        }
    }
}
