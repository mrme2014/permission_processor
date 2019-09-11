package com.qiaomu.libpermission;

/**
 * Created by qiaomu on 17/10/9.
 */
public interface PermissionProxy<T> {
    void grant(T source, int requestCode, String... permission);

    void denied(T source, int requestCode, String... permission);

    boolean rationale(T source, int requestCode, String[] permission, PermissionRationalCallback rationalCallback);

    boolean needShowRationale(int requestCode, String... permission);
}
