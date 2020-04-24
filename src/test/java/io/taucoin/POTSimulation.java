package io.taucoin;

import io.taucoin.core.Sha256Hash;
import io.taucoin.crypto.ECKey;
import io.taucoin.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

import static java.lang.Math.abs;
import static java.lang.Math.log;

public class POTSimulation {

    private final static int MAXRATIO = 335;
    private final static int MINRATIO = 265;
    private final static int AVERTIME = 300; //5 min
    private final static BigInteger DiffAdjustNumerator = new BigInteger("010000000000000000",16);
    private final static BigInteger DiffAdjustNumeratorHalf = new BigInteger("0100000000",16);
    private final static BigInteger DiffAdjustNumeratorCoe = new BigInteger("800000000000000",16); //2^59

    static Logger logger = LoggerFactory.getLogger("POTSimulation");

    public static void main(String[] args) {
        byte[] genesisGenerationSignature = Hex.decode("442c29a4d18f192164006030640fb54c8b9ffd4f5750d2f6dca192dc653c52ad");
        BigInteger genesisBaseTarget = new BigInteger("0fffffffffffffff", 16);
        // base target    ave(s) max(s) 1000000 addresses
        // 369D0369D036978   1  30/32
        // 19D0369D036978    78 1044
        // 12D0369D036978    108 1514
        // 1FD0369D036978   63  866
        // 20D0369D036978   61  857
        // 21D0369D036978   60  819
        BigInteger baseTarget = new BigInteger("21D0369D036978",16);
        byte[] generationSignature;
        BigInteger hit;
        BigInteger timeInterval;
        byte[] pubkey;

        BigInteger maxTimeInterval = BigInteger.ZERO;
        BigInteger totalTimeInterval = BigInteger.ZERO;

        int N = 1000000;
        for (int i = 0; i < N; i++) {
            // get a public key
            ECKey ecKey = new ECKey(Utils.getRandom());
            // calculate hit
            pubkey = ecKey.getPubKey();
            generationSignature = calculateNextBlockGenerationSignature(genesisGenerationSignature, pubkey);
            hit = calculateRandomHit(generationSignature);
            // calculate time interval = hit / base target
            timeInterval = hit.divide(baseTarget);
            logger.info("Time interval: {}", timeInterval);

            totalTimeInterval = totalTimeInterval.add(timeInterval);
            if (timeInterval.compareTo(maxTimeInterval) > 0 ) {
                maxTimeInterval = timeInterval;
            }
        }
        logger.info("Base target:{}", baseTarget);
        logger.info("Average time interval:{}", totalTimeInterval.divide(BigInteger.valueOf(N)));
        logger.info("Max time interval:{}", maxTimeInterval);
    }

    /**
     * get next block generation signature
     *     Gn+1 = hash(Gn, pubkey)
     * @param preGenerationSignature
     * @param pubkey
     * @return
     */
    public static byte[] calculateNextBlockGenerationSignature(byte[] preGenerationSignature, byte[] pubkey){
        byte[] data = new byte[preGenerationSignature.length + pubkey.length];

        System.arraycopy(preGenerationSignature, 0, data, 0, preGenerationSignature.length);
        System.arraycopy(pubkey, 0, data, preGenerationSignature.length, pubkey.length);

        return Sha256Hash.hash(data);
    }

    /**
     * calculate hit
     * @param generationSignature
     * @return
     */
    public static BigInteger calculateRandomHit(byte[] generationSignature){
        byte[] headBytes = new byte[8];
        System.arraycopy(generationSignature,0,headBytes,0,8);

        BigInteger bhit = new BigInteger(1, headBytes);
//        logger.info("bhit:{}", bhit);

        BigInteger bhitUzero = bhit.add(BigInteger.ONE);
//        logger.info("bhitUzero:{}", bhitUzero);

        double logarithm = abs(log(bhitUzero.doubleValue()) - 2 * log(DiffAdjustNumeratorHalf.doubleValue()));
        logarithm = logarithm * 1000;
//        logger.info("logarithm:{}", logarithm);

        long ulogarithm = (new Double(logarithm)).longValue();
//        logger.info("ulogarithm:{}", ulogarithm);

        BigInteger adjustHit = DiffAdjustNumeratorCoe.multiply(BigInteger.valueOf(ulogarithm)).divide(BigInteger.valueOf(1000));
//        logger.info("adjustHit:{}", adjustHit);

        return adjustHit;
    }
}
