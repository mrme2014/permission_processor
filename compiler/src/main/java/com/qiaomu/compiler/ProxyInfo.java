package com.qiaomu.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by qiaomu on 2017/10/9.
 */
public class ProxyInfo {
    public String packageName;
    public String proxyClassName;
    public TypeElement typeElement;

    Map<Integer, String> grantMethodMap = new HashMap<>();
    Map<Integer, String> deniedMethodMap = new HashMap<>();
    Map<Integer, String> rationaleMethodMap = new HashMap<>();

    public static final String PROXY = "PermissionProxy";

    public ProxyInfo(Elements elementUtils, TypeElement classElement) {
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        Logger.getGlobal().log(Level.ALL, "ProxyInfo:" + packageName);
        //classname
        String className = ClassValidator.getClassName(classElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = className + "$$" + PROXY;
    }


    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import com.qiaomu.libpermission.*;\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements " + ProxyInfo.PROXY + "<" + typeElement.getSimpleName() + ">");
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }


    private void generateMethods(StringBuilder builder) {

        generateGrantMethod(builder);
        generateDeniedMethod(builder);
        generateRationaleMethod(builder);
    }

    private void generateRationaleMethod(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public boolean rationale(" + typeElement.getSimpleName() + " source , int requestCode,String[] permissions, PermissionRationalCallback rationalCallback) {\n");
        builder.append("switch(requestCode) {");
        for (int code : rationaleMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("source." + rationaleMethodMap.get(code) + "(permissions,rationalCallback);");
            builder.append("return true;");
        }
        builder.append("}");
        builder.append("return false;");
        builder.append("  }\n");

        ///

        builder.append("@Override\n ");
        builder.append("public boolean needShowRationale(int requestCode,String[] permission) {\n");
        builder.append("switch(requestCode) {");
        for (int code : rationaleMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("return true;");
        }
        builder.append("}\n");
        builder.append("return false;");
        builder.append("  }\n");
    }

    private void generateDeniedMethod(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public void denied(" + typeElement.getSimpleName() + " source , int requestCode,String[] permissions) {\n");
        builder.append("switch(requestCode) {");
        for (int code : deniedMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("source." + deniedMethodMap.get(code) + "(permissions);");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");
    }

    private void generateGrantMethod(StringBuilder builder) {
        builder.append("@Override\n ");
        builder.append("public void grant(" + typeElement.getSimpleName() + " source , int requestCode,String[] permissions) {\n");
        builder.append("switch(requestCode) {");
        for (int code : grantMethodMap.keySet()) {
            builder.append("case " + code + ":");
            builder.append("source." + grantMethodMap.get(code) + "(permissions);");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }


}