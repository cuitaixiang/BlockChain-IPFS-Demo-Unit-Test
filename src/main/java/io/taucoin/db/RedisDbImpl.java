package io.taucoin.db;

import io.taucoin.core.Block;
import io.taucoin.core.Transaction;
import io.taucoin.core.transaction.Account;
import io.taucoin.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.math.BigInteger;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RedisDbImpl implements RedisDb{
    private static final Logger logger = LoggerFactory.getLogger("redisdb");
    private Jedis localJedis;
    private Jedis sharedJedis;
    private JedisPool jedisPool;

    private static final Long MAXTXINPOOL = 7200L;

    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = "password";

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/tau?useSSL=false";
    private static final String DB_USER = "user";
    private static final String DB_PASS = "password";
    private static final String TXTABLE = "tautransactions";
    private static final String BLOCKTABLE = "taublocks";

    private Connection conn;
    private PreparedStatement psql;

    @Override
    public void init() {
//        initRedisPool();
        initRedisDb();
        initMysqlDb();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public void initMysqlDb() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = getConnection();
        } catch (Exception e) {
            logger.error("init mysql db fail!");
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void initRedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(1000L);
        config.setTestOnBorrow(false);
        jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT);

        localJedis = new Jedis("localhost", REDIS_PORT);
        localJedis.auth(REDIS_PASSWORD);
    }

    @Override
    public void getResourceFromPool() {
        sharedJedis = jedisPool.getResource();
    }

    @Override
    public void releaseResource() {
        if (sharedJedis != null) {
            sharedJedis.close();
        }
    }

    public void initRedisDb() {
        localJedis = new Jedis("localhost", REDIS_PORT);
        localJedis.auth(REDIS_PASSWORD);
        sharedJedis = new Jedis(REDIS_HOST, REDIS_PORT);
        sharedJedis.auth(REDIS_PASSWORD);
    }

    @Override
    public Long getBestHeight() {
        if (sharedJedis.hexists("chaininfo", "totalheight")) {
            return Long.valueOf(sharedJedis.hget("chaininfo", "totalheight"));
        }
        return null;
    }

    @Override
    public Account getBalance(byte[] address) {
        String accountinfo = sharedJedis.hget("accountinfo", Hex.toHexString(address));
        if (accountinfo != null) {
            return new Account(Hex.decode(accountinfo));
        }
        logger.error("Cannot get chaininfo account info:{}!", Hex.toHexString(address));
        return null;
    }

    @Override
    public Block getBlockByHeight(long height) {
        Set<String> blockSet = sharedJedis.zrangeByScore("blockinfo", height, height);
        Iterator<String> it = blockSet.iterator();
        if (it.hasNext()) {
            return new Block(Hex.decode(it.next()), true);
        }
        logger.error("Cannot get block from blockinfo, height:{}!", height);
        return null;
    }

    @Override
    public boolean isInPool(String txid) {
        if (sharedJedis.zscore("transfeeinfo", txid) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOnChain(String txid) {
        return sharedJedis.sismember("ttcinfo", txid);
    }

    @Override
    public Transaction getBlockingNewTransaction() {
        List<String> tx = localJedis.brpop("transqueue", String.valueOf(0));
        if (tx.size() == 2 && tx.get(0).equals("transqueue")) {
            logger.info("tx:{}", tx.get(1));
            return new Transaction(Hex.decode(tx.get(1)));
        }
        logger.error("Tx in transqueue is null!");
        return null;
    }

    @Override
    public Set<String> getAllTxidInPool() {
        return sharedJedis.zrevrange("transfeeinfo", 0, -1);
    }

    @Override
    public Transaction getTransactionByTxid(String txid) {
        String tx = sharedJedis.hget("transtxidpool", txid);
        if (tx != null) {
            return new Transaction(Hex.decode(tx));
        }
        logger.error("Cannot get tx [{}] in pool.", txid);
        return null;
    }

    @Override
    public void updateMysqlDb(String txid, int status) {
        try {
            try {
                if (!conn.isValid(5)) {
                    logger.info("Mysql Db is closed. Reconnecting ...");
                    conn = getConnection();
                }
            } catch (Exception e) {
                logger.error("Connect is broken! Reconnecting ...");
                logger.error(e.getMessage(), e);
                conn = getConnection();
            }

            //update table apptrans
            psql = conn.prepareStatement("insert into apptrans values(?,?) on duplicate key update status=values(status)");
            psql.setString(1, txid);
            psql.setInt(2, status);
            psql.executeUpdate();
        } catch (Exception e) {
            logger.error("Update mysql fail!");
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                psql.close();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeTransactionFromPool(String txid) {
        sharedJedis.zrem("transfeeinfo", txid);
        sharedJedis.hdel("transtxidpool", txid);
        sharedJedis.zrem("transexpiredinfo", txid);
    }

    public void putIntoPool(Transaction tx) {
        String txid = Hex.toHexString(tx.getHash());
        sharedJedis.zadd("transfeeinfo",
                tx.getBigIntegerFee().doubleValue(), txid);
        sharedJedis.hset("transtxidpool",
                txid, Hex.toHexString(tx.getEncoded()));
        BigInteger currentHeight = new BigInteger(sharedJedis.hget("chaininfo", "totalheight"));
        BigInteger expireHeight = new BigInteger(1, tx.getExpireTime());
        sharedJedis.zadd("transexpiredinfo",
                currentHeight.add(expireHeight).doubleValue(), txid);

        updateMysqlDb(txid, 0);
    }

    @Override
    public void tryToPutIntoPool(Transaction tx) {
        String txid = Hex.toHexString(tx.getHash());
        logger.info("txid:[{}]", txid);
        if (sharedJedis.zcard("transfeeinfo") < MAXTXINPOOL) {
            putIntoPool(tx);
        } else {
            Set<Tuple> txTuples = sharedJedis.zrangeWithScores("transfeeinfo", 0, 0);
            Iterator<Tuple> it = txTuples.iterator();
            if (it.hasNext()) {
                Tuple tuple = it.next();
                if (tuple.getScore() < tx.getBigIntegerFee().doubleValue()) {
                    //new tx fee is larger than min fee in pool
                    logger.info("Tx [{}] will be instead of new tx [{}]", tuple.getElement(), txid);
                    removeTransactionFromPool(tuple.getElement());
                    updateMysqlDb(tuple.getElement(), 3);
                    putIntoPool(tx);
                } else {
                    //new tx fee is too small
                    logger.warn("New tx [{}] fee is too small.", txid);
                    updateMysqlDb(txid, 3);
                }
            }
        }
    }

}
