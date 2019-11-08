package io.taucoin.core;

import io.taucoin.config.Constants;
import io.taucoin.core.transaction.Account;
import io.taucoin.core.transaction.TransactionOptions;
import io.taucoin.core.transaction.TransactionVersion;
import io.taucoin.db.RedisDb;
import io.taucoin.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

public class TransactionValidation {
    private static final Logger logger = LoggerFactory.getLogger("transactionvalidation");

    public static boolean isCovers(BigInteger covers, BigInteger value){
        return !isNotCovers(covers, value);
    }

    public static boolean isNotCovers(BigInteger covers, BigInteger value){
        return covers.compareTo(value) < 0;
    }

    public static Long getBlockTimeByHeight(RedisDb redisDb, Long height) {
        Block block = redisDb.getBlockByHeight(height);
        if (block == null) {
            logger.error("Block is null!");
            return null;
        }
        return ByteUtil.byteArrayToLong(block.getTimestamp());
    }

    public static boolean verifyLength(Transaction tx) {
        return tx.validate();
    }

    public static boolean isInPool(RedisDb redisDb, String txid) {
        return redisDb.isInPool(txid);
    }

    public static boolean isOnChain(RedisDb redisDb, String txid) {
        return redisDb.isOnChain(txid);
    }

    /**
     * verify transaction version
     * @param tx
     * @return
     */
    private static boolean verifyTransactionVersion(Transaction tx) {
        if (tx.getVersion() != TransactionVersion.V01.getCode()) {
            logger.error("Tx [{}] version [{}] is mismatch!", Hex.toHexString(tx.getHash()),
                    tx.getVersion());
            return false;
        }
        return true;
    }

    /**
     * verify transaction option
     * @param tx
     * @return
     */
    private static boolean verifyTransactionOption(Transaction tx) {
        if (tx.getOption() != TransactionOptions.TRANSACTION_OPTION_DEFAULT) {
            logger.error("Tx [{}] option [{}] is mismatch!", Hex.toHexString(tx.getHash()),
                    tx.getOption());
            return false;
        }
        return true;
    }

    private static boolean verifyTransactionExpireTime(Transaction tx) {
        int expireTime = new BigInteger(1, tx.getExpireTime()).intValue();
        if (expireTime > Constants.TX_MAXEXPIRETIME) {
            logger.error("Tx [{}] expire time [{}] > {}.", Hex.toHexString(tx.getHash()),
                    expireTime, Constants.TX_MAXEXPIRETIME);
            return false;
        }
        return true;
    }

    public static boolean verifyTxTime(RedisDb redisDb, long txTime, long expireHeight) {
        Long bestHeight = redisDb.getBestHeight();
        if (bestHeight == null) {
            logger.error("Cannot get best height!");
            return false;
        }
        if (expireHeight >= bestHeight)
            return true;
        long referenceHeight = bestHeight - expireHeight;

        Long referenceTime = getBlockTimeByHeight(redisDb, referenceHeight);
        if (referenceTime == null) {
            logger.error("Cannnot get block time at height:{}.", referenceHeight);
            return false;
        }
        if (txTime < referenceTime) {
            logger.error("Tx time [{}] < reference time [{}].", txTime, referenceTime);
            return false;
        } else {
            return true;
        }
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


    public static boolean accept(RedisDb redisDb, Transaction tx) {
        try {
            tx.rlpParse();
        } catch (Exception e) {
            logger.error("Parse exception!");
            logger.error(e.getMessage(), e);
            return false;
        }

        String txid = Hex.toHexString(tx.getHash());
        long txTime = ByteUtil.byteArrayToLong(tx.getTime());

        BigInteger tatalCost = tx.getTotoalCost();
        long expireHeight = (new BigInteger(1, tx.getExpireTime())).longValue();

        if (!verifyTransactionVersion(tx)) {
            logger.error("Tx version mismatch!");
            redisDb.updateMysqlDb(txid, 14);
            return false;
        }

        if (!verifyTransactionOption(tx)) {
            logger.error("Tx option mismatch!");
            redisDb.updateMysqlDb(txid, 15);
            return false;
        }

        if (!verifyTransactionExpireTime(tx)) {
            logger.error("Tx expire time exceeds the limit!");
            redisDb.updateMysqlDb(txid, 16);
            return false;
        }

        byte[] senderAddress = tx.getSender();
        if (senderAddress == null) {
            logger.error("Bad Signature! txid [{}]", txid);
            redisDb.updateMysqlDb(txid, 12);
            return false;
        }

        if (!verifyLength(tx)) {
            logger.error("Exceeding the expected length! txid [{}]", txid);
            redisDb.updateMysqlDb(txid, 11);
            return false;
        }

        if (!verifyBalance(redisDb, senderAddress, tatalCost)) {
            redisDb.updateMysqlDb(txid, 13);
            logger.error("Verify balance fail! txid [{}]", txid);
            return false;
        }

        if (isInPool(redisDb, txid)) {
            logger.info("tx [{}] is already in the pool.", txid);
            return false;
        }

        if (isOnChain(redisDb, txid)) {
            logger.info("tx [{}] is already on the chain.", txid);
            return false;
        }

        if (!verifyTxTime(redisDb, txTime, expireHeight)) {
            redisDb.updateMysqlDb(Hex.toHexString(tx.getHash()), 1);
            logger.warn("tx [{}] is already expired!", txid);
            return false;
        }

        return true;
    }
}
