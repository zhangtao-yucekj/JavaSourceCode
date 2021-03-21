package com.lagou.edu.pojo;

import java.util.ArrayList;
import java.util.List;

public class ClassInterface {
    //类全限定类名
    private String className;
    //类实现了哪些接口
    public List<String> interfaceNameList = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getInterfaceNameList() {
        return interfaceNameList;
    }

    public void setInterfaceNameList(List<String> interfaceNameList) {
        this.interfaceNameList = interfaceNameList;
    }

    @Override
    public String toString() {
        return "ClassInterface{" +
                "className='" + className + '\'' +
                ", interfaceNameList=" + interfaceNameList +
                '}';
    }
}
