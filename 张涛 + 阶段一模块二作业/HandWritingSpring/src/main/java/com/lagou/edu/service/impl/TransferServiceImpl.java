package com.lagou.edu.service.impl;

import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.stereotype.Autowired;
import com.lagou.edu.stereotype.Service;
import com.lagou.edu.stereotype.Transactional;
import com.lagou.edu.utils.TransactionManager;

/**
 * @author zhangtao
 */
@Service("transferService")
@Transactional
public class TransferServiceImpl implements TransferService {

    // 最佳状态
    @Autowired
    private AccountDao accountDao;


    //@Transactional
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);
        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);
        accountDao.updateAccountByCardNo(to);
        //int c = 1/0;
        accountDao.updateAccountByCardNo(from);

    }

    @Override
    public String toString() {
        return "TransferServiceImpl{" +
                "accountDao=" + accountDao +
                '}';
    }
}
