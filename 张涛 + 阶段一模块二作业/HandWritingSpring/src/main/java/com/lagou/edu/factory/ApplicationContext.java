package com.lagou.edu.factory;

import com.lagou.edu.pojo.AllClassInterfaces;
import com.lagou.edu.pojo.ClassInterface;
import com.lagou.edu.stereotype.Autowired;
import com.lagou.edu.stereotype.Service;
import com.lagou.edu.stereotype.Transactional;
import com.lagou.edu.utils.PackageScanUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ApplicationContext {

    private static Map<String,Object> map = new HashMap<>();  // 存储对象

    static {
        //key为对象名称，存储@service注解对应的value值
        Map<String, String> beanNames = new HashMap<>();
        //扫描所有类，如果类上有@Service注解就通过反射创建对象加入到ioc容器中
        String packageFirst = "com.lagou.edu";
        List<String> packageList = Collections.singletonList(packageFirst);
        List<Class> classes = null;
        for (String s : packageList) {
            try {
                classes = PackageScanUtils.searchClass(s);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        List<ClassInterface> classInterfaces = AllClassInterfaces.classInterfaceList;
        for (Class aClass : classes) {
            if (aClass.isAnnotationPresent(Service.class)) {
                Service annotation = (Service) aClass.getAnnotation(Service.class);
                beanNames.put(aClass.getSimpleName(), annotation.value());
            }
            ClassInterface classInterface = new ClassInterface();
            classInterface.setClassName(aClass.getName());
            Class[] interfaces = aClass.getInterfaces();
            for (Class anInterface : interfaces) {
                classInterface.getInterfaceNameList().add(anInterface.getName());
            }
            classInterfaces.add(classInterface);
        }

        //检验前面程序是否正确
//        for (ClassInterface classInterface : classInterfaces) {
//            System.out.println("className: " + classInterface.getClassName());
//            List<String> classNameList = classInterface.getInterfaceNameList();
//            for (String s : classNameList) {
//                System.out.println("InterfaceName: " + s);
//            }
//        }

        for (Class aClass : classes) {
            //如果该类上有Service注解就实例化这个对象并把它加到ioc容器当中
            if (aClass.isAnnotationPresent(Service.class)) {
                Object o = null;
                try {
                    String simpleName = beanNames.get(aClass.getSimpleName());
                    if (simpleName != null && simpleName.equals("")) {
                        simpleName = aClass.getSimpleName();
                        simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                    }
                    if (map.get(simpleName) == null){
                        o = aClass.newInstance();
                    } else {
                        o = map.get(simpleName);
                    }

                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                //如果service注解上定义了名称就用注解定义的，没有定义就默认首字母小写
                String beanName = beanNames.get(aClass.getSimpleName());
                if (beanName != null && beanName.equals("")) {
                    beanName = aClass.getSimpleName();
                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                }
                //判断该类上所有属性是否有Autowired注解
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.isAnnotationPresent(Autowired.class)) {
                        String fieldTypeName = declaredField.getType().getName();
                        //如果该属性是一个类
                        if (!declaredField.getType().toString().contains("interface")) {
                            try {
                                String simpleName = beanNames.get(declaredField.getType().getSimpleName());
                                if (simpleName != null && simpleName.equals("")) {
                                    simpleName = declaredField.getType().getSimpleName();
                                    simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                                }
                                declaredField.setAccessible(true);
                                Object o1 = map.get(simpleName);
                                if (o1 == null) {
                                    Class<?> aClass1 = Class.forName(fieldTypeName);
                                    o1 = aClass1.newInstance();
                                    //把新创建的对象加入到ioc容器中
                                    map.put(simpleName, o1);
                                }
                                declaredField.set(o, o1);
                                continue;
                            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        //接下来判断接口的实现类是哪个
                        for (ClassInterface classInterface : classInterfaces) {
                            List<String> interfaceNameList = classInterface.getInterfaceNameList();
                            for (String s : interfaceNameList) {
                                if (s.equals(fieldTypeName)) {
                                    try {
                                        String className = classInterface.getClassName();
                                        Class<?> aClass2 = Class.forName(className);
                                        String bean = beanNames.get(aClass2.getSimpleName());
                                        if (bean != null && bean.equals("")) {
                                            bean = aClass2.getSimpleName();
                                            bean = bean.substring(0, 1).toLowerCase() + bean.substring(1);
                                        }
                                        declaredField.setAccessible(true);
                                        //如果这个对象已经实例化了就直接注入
                                        if (map.get(bean) != null) {
                                            declaredField.set(o, map.get(bean));
                                            continue;
                                        }
                                        Class<?> aClass1 = Class.forName(className);
                                        Object o1 = aClass1.newInstance();
                                        //把新创建的对象加入到ioc容器中
                                        map.put(bean, o1);
                                        declaredField.set(o, o1);
                                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                if (map.get(beanName) != null) continue;
                map.put(beanName, o);
            }
        }
    }

    public static Object getBean(String id) throws ClassNotFoundException {
        Object o = map.get(id);
        String className = o.getClass().getName();
        Class<?> aClass = Class.forName(className);
        //如果当前类上面有transactional注解
        if (aClass.isAnnotationPresent(Transactional.class)) {
            ProxyFactory proxyFactory = (ProxyFactory) ApplicationContext.getBean("proxyFactory");
            return proxyFactory.getJdkProxy();
        }
        //判断该类的方法上是否有transactional注解
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(Transactional.class)) {
                System.out.println("method: " + declaredMethod.getName());
                ProxyFactory proxyFactory = (ProxyFactory) ApplicationContext.getBean("proxyFactory");
                System.out.println("proxyFactory: " + proxyFactory.toString());
                return proxyFactory.getJdkProxy();
            }
        }
        return map.get(id);
    }
}
