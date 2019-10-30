package com.qiaomu.permissionprocessor;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.qiaomu.annotation.PermissionDenied;
import com.qiaomu.annotation.PermissionGrant;
import com.qiaomu.annotation.PermissionRationale;
import com.qiaomu.libpermission.PermissionHelper;
import com.qiaomu.libpermission.PermissionRationalCallback;

/**
 * Created by qiaomu on 2017/10/9.
 */

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_SD = 100;
    private static final int PERMISSION_REQUEST_CODE_PHONE = 101;
    private static final int PERMISSION_REQUEST_CODE_MULTIPLE = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sdcardTip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(MainActivity.this, PERMISSION_REQUEST_CODE_SD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });

        findViewById(R.id.phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(MainActivity.this, PERMISSION_REQUEST_CODE_PHONE, Manifest.permission.CALL_PHONE);
            }
        });


        findViewById(R.id.multi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(MainActivity.this, PERMISSION_REQUEST_CODE_MULTIPLE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.SEND_SMS);
            }
        });
    }

    @PermissionGrant(PERMISSION_REQUEST_CODE_PHONE)
    public void requestPhoneOnGrant(String[] permissions) {
        Toast.makeText(this, "phone permission grant", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(PERMISSION_REQUEST_CODE_PHONE)
    public void requestPhoneOnDenied(String[] permissions) {
        Toast.makeText(this, " phone  permission denied", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(PERMISSION_REQUEST_CODE_SD)
    public void requestSdOnGrant(String[] permissions) {
        Toast.makeText(this, " sdcard permission grant", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(PERMISSION_REQUEST_CODE_SD)
    public void requestSdOnDenied(String[] permissions) {
        Toast.makeText(this, "sdcard permission denied", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(PERMISSION_REQUEST_CODE_MULTIPLE)
    public void requestMultiOnDenied(String[] permissions) {
        StringBuilder builder = new StringBuilder();
        for (String permission : permissions) {
            builder.append(permission + "\n");
        }
        Toast.makeText(this, builder.toString() + "\n以上权限被禁止了", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(PERMISSION_REQUEST_CODE_MULTIPLE)
    public void requestMultiOnGrant(String[] permissions) {
        StringBuilder builder = new StringBuilder();
        for (String permission : permissions) {
            builder.append(permission + "\n");
        }
        Toast.makeText(this, builder.toString() + "\n以上权限被授权了", Toast.LENGTH_SHORT).show();
    }

    @PermissionRationale(PERMISSION_REQUEST_CODE_SD)
    public void whyNeedSdCard(String[] permissions, @NonNull final PermissionRationalCallback callback) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String permission : permissions) {
            stringBuilder.append(permission + "\n");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请说明");
        builder.setMessage("请授权以下权限的申请,以继续功能的使用\n" + stringBuilder.toString());
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "取消该流程的权限申请", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onRationalExecute();
            }
        }).create().show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
