package com.lagou.edu.factory;

import com.lagou.edu.pojo.AllClassInterfaces;
import com.lagou.edu.pojo.ClassInterface;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.stereotype.Autowired;
import com.lagou.edu.stereotype.Service;
import com.lagou.edu.stereotype.Transactional;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author 应癫
 *
 *
 * 代理对象工厂：生成代理对象的
 */
@Service
public class ProxyFactory implements InvocationHandler{

    //声明被代理的对象
    @Autowired
    private TransferService transferService;

    @Autowired
    private TransactionManager transactionManager;

    public void setTransferService(TransferService transferService) {
        this.transferService = transferService;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Jdk动态代理
     * @return   代理对象
     */
    public Object getJdkProxy() {
        return Proxy.newProxyInstance(transferService.getClass().getClassLoader(), transferService.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<ClassInterface> classInterfaceList = AllClassInterfaces.classInterfaceList;
        String className = null;
        for (ClassInterface classInterface : classInterfaceList) {
            List<String> interfaceNameList = classInterface.getInterfaceNameList();
            for (String interfaceName : interfaceNameList) {
                //找出这个method方法对应哪个实现类
                if (interfaceName.equals(method.getDeclaringClass().getName())) {
                    className = classInterface.getClassName();
                    break;
                }
                if (className != null) break;
            }
        }
        Class<?> aClass = Class.forName(className);
        Method declaredMethod = aClass.getDeclaredMethod(method.getName(), String.class, String.class, int.class);
        if (!aClass.isAnnotationPresent(Transactional.class) && !declaredMethod.isAnnotationPresent(Transactional.class)) {
            return method.invoke(transferService, args);
        }
        Object result = null;
        try{
            // 开启事务(关闭事务的自动提交)
            transactionManager.beginTransaction();
            System.out.println("开启事务.....");
            result = method.invoke(transferService, args);
            // 提交事务
            transactionManager.commit();
            System.out.println("提交事务.....");
        }catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            System.out.println("事务回滚.....");
            transactionManager.rollback();
            // 抛出异常便于上层servlet捕获
            throw e;
        }
        return result;
    }

    /**
     * 使用cglib动态代理生成代理对象
     * @param obj 委托对象
     * @return
     */
    public Object getCglibProxy(Object obj) {
        return  Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try{
                    // 开启事务(关闭事务的自动提交)
                    transactionManager.beginTransaction();
                    result = method.invoke(obj,objects);
                    // 提交事务
                    transactionManager.commit();
                }catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    transactionManager.rollback();
                    // 抛出异常便于上层servlet捕获
                    throw e;
                }
                return result;
            }
        });
    }

    @Override
    public String toString() {
        return "ProxyFactory{" +
                "transferService=" + transferService +
                ", transactionManager=" + transactionManager +
                '}';
    }
}
