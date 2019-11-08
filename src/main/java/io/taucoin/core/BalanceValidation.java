package io.taucoin.core;

import io.taucoin.core.transaction.Account;
import io.taucoin.db.RedisDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BalanceValidation {
    private static final Logger logger = LoggerFactory.getLogger("balancevalidation");

    private static Map<String, Long> balanceMap = new HashMap<>();


    public static boolean isCovers(BigInteger covers, BigInteger value){
        return !isNotCovers(covers, value);
    }

    public static boolean isNotCovers(BigInteger covers, BigInteger value){
        return covers.compareTo(value) < 0;
    }

    public static boolean verifyBalance(RedisDb redisDb, byte[] senderAddress, BigInteger totalCost) {
        Account senderAccount = redisDb.getBalance(senderAddress);
        if (senderAccount == null) {
            logger.error("Cannot get address [{}] account info.", Hex.toHexString(senderAddress));
            return false;
        }
        if (!isCovers(senderAccount.balance, totalCost)) {
            logger.error("Sender [{}] balance [{}] cannot cover totalCost [{}].", Hex.toHexString(senderAddress),
                    senderAccount.balance, totalCost);
            return false;
        }
        return true;
    }

    public static void VerifyBalance(RedisDb redisDb) {
        Set<String> txidSet = redisDb.getAllTxidInPool();

        balanceMap.clear();

        if (txidSet.isEmpty()) {
            return;
        }

        for (String txid : txidSet) {
            Transaction tx = redisDb.getTransactionByTxid(txid);
            if (tx == null) {
                logger.error("Cannot fetch tx [{}] from pool. Maybe removed from pool already.", txid);
                continue;
            }
            byte[] address = tx.getSender();
            String key = Hex.toHexString(address);
            Long cost = tx.getTotoalCost().longValue();
            Long sumBalance = balanceMap.get(key);
            if (sumBalance != null) {
                Long totalBalance = sumBalance + cost;
                if (!verifyBalance(redisDb, address, new BigInteger(totalBalance.toString()))) {
                    redisDb.updateMysqlDb(txid, 13);
                    redisDb.removeTransactionFromPool(txid);
                    logger.error("Excess of balance: remove txid [{}] from pool.", txid);
                } else {
                    balanceMap.put(key, totalBalance);
                }
            } else {
                if (!verifyBalance(redisDb, address, new BigInteger(cost.toString()))) {
                    redisDb.updateMysqlDb(txid, 13);
                    redisDb.removeTransactionFromPool(txid);
                    logger.error("Excess of balance: remove txid [{}] from pool.", txid);
                }
                balanceMap.put(key, cost);
            }
        }
    }

}
