package io.taucoin.config;

import java.math.BigInteger;

public class Constants {

    public static final byte BLOCK_VERSION = (byte) 0;
    public static final byte BLOCK_OPTION = (byte) 0;
    //max block tx number
    public static final int MAX_BLOCKTXSIZE = 50;
    //transaction expiration height
    public static final int TX_MAXEXPIRETIME = 144;
    //forge block time interval
    public static final int BLOCKTIMEINTERVAL = 300;
    //block time drift
    public static final int MAX_TIMEDRIFT = 15; // allow up to 15 s clock difference

    public static BigInteger MINIMUM_DIFFICULTY = BigInteger.valueOf(131072);
    public static BigInteger DIFFICULTY_BOUND_DIVISOR = BigInteger.valueOf(2048);
    public static int EXP_DIFFICULTY_PERIOD = 100000;



    public static final BigInteger SECP256K1N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);
    public static final BigInteger SECP256K1N_HALF = SECP256K1N.divide(BigInteger.valueOf(2));

}
