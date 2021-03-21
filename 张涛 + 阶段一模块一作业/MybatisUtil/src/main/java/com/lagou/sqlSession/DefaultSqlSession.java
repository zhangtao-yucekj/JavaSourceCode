package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    public <E> List<E> selectList(String statementId, Object... params) throws Exception {

        //将要完成对simpleExecutor里的query方法的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> objectList = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<E>) objectList;
    }

    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        }
        throw new RuntimeException("查询结果为空或者返回结果过多");
    }

    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象，并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            //proxy: 当前代理对象的应用  method: 当前被调用方法的引用  args: 传递的参数
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码、根据不同情况，来调用selectList或者selectOne
                // 准备参数1：statementId : sql语句的唯一标识：namespace.id = 接口全限定名.方法名
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;
//                // 准备参数2：params:args
//                // 获取被调用方法的返回值类型
//                Type genericReturnType = method.getGenericReturnType();
//                // 判断是否进行了泛型类型参数化
//                if (genericReturnType instanceof ParameterizedType) {
//                    List<Object> objects = selectList(statementId, args);
//                    return objects;
//                }
//                return selectOne(statementId, args);

                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                SimpleExecutor simpleExecutor = new SimpleExecutor();
                List<Object> objects = simpleExecutor.query(configuration, mappedStatement, args);
                if (objects == null) return null;
                if (objects.size() == 1) {
                    return objects.get(0);
                }
                return objects;

            }
        });
        return (T) proxyInstance;
    }
}
