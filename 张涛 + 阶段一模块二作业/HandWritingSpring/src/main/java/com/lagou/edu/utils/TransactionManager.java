package com.lagou.edu.utils;

import com.lagou.edu.stereotype.Autowired;
import com.lagou.edu.stereotype.Service;
import com.lagou.edu.stereotype.Transactional;

import java.sql.SQLException;

/**
 * @author 应癫
 *
 * 事务管理器类：负责手动事务的开启、提交、回滚
 */
@Service
public class TransactionManager {

    @Autowired
    private ConnectionUtils connectionUtils;

    @Override
    public String toString() {
        return "TransactionManager{" +
                "connectionUtils=" + connectionUtils +
                '}';
    }


    // 开启手动事务控制
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }


    // 提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }


    // 回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }
}
