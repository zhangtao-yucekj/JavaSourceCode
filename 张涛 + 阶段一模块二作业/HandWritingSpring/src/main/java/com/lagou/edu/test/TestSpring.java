package com.lagou.edu.test;

import com.lagou.edu.factory.ApplicationContext;
import com.lagou.edu.factory.BeanFactory;
import com.lagou.edu.factory.ProxyFactory;
import com.lagou.edu.service.TransferService;
import org.junit.Test;

public class TestSpring {

    @Test
    public void testIOC() throws Exception {

        ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");
        TransferService transferService = (TransferService) proxyFactory.getJdkProxy();
        transferService.transfer("1", "2", 100);
    }

    @Test
    public void testPackageScan() throws ClassNotFoundException {
        TransferService transferService = (TransferService) ApplicationContext.getBean("transferServiceImpl");
    }

    @Test
    public void testIOCByAnnotation() throws Exception {
        TransferService transferService = (TransferService) ApplicationContext.getBean("transferService");
        transferService.transfer("1", "2", 100);
    }
}
