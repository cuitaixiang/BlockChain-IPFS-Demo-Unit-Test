package io.taucoin.db;

import io.taucoin.core.Block;
import io.taucoin.core.Transaction;
import io.taucoin.core.transaction.Account;

import java.util.Set;

public interface RedisDb {
    void init();

    void getResourceFromPool();

    void releaseResource();

    Long getBestHeight();

    Account getBalance(byte[] address);

    Block getBlockByHeight(long height);

    boolean isInPool(String txid);

    boolean isOnChain(String txid);

    Transaction getBlockingNewTransaction();

    Set<String> getAllTxidInPool();

    void removeTransactionFromPool(String txid);

    Transaction getTransactionByTxid(String txid);

    void tryToPutIntoPool(Transaction transaction);

    void updateMysqlDb(String txid, int status);
}
