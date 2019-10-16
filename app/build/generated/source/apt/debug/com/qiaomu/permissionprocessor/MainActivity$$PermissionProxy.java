// Generated code. Do not modify!
package com.qiaomu.permissionprocessor;

import com.qiaomu.libpermission.*;

public class MainActivity$$PermissionProxy implements PermissionProxy<MainActivity> {
@Override
 public void grant(MainActivity source , int requestCode,String[] permissions) {
switch(requestCode) {case 100:source.requestSdOnGrant(permissions);break;case 101:source.requestPhoneOnGrant(permissions);break;case 102:source.requestMultiOnGrant(permissions);break;}  }
@Override
 public void denied(MainActivity source , int requestCode,String[] permissions) {
switch(requestCode) {case 100:source.requestSdOnDenied(permissions);break;case 101:source.requestPhoneOnDenied(permissions);break;case 102:source.requestMultiOnDenied(permissions);break;}  }
@Override
 public boolean rationale(MainActivity source , int requestCode,String[] permissions, PermissionRationalCallback rationalCallback) {
switch(requestCode) {case 100:source.whyNeedSdCard(permissions,rationalCallback);return true;}return false;  }
@Override
 public boolean needShowRationale(int requestCode,String[] permission) {
switch(requestCode) {case 100:return true;}
return false;  }

}
