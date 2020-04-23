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

    private final static BigInteger DiffAdjustNumerator = new BigInteger("010000000000000000",16);
    private final static BigInteger DiffAdjustNumeratorHalf = new BigInteger("0100000000",16);
    private final static BigInteger DiffAdjustNumeratorCoe = new BigInteger("800000000000000",16); //2^59

    static Logger logger = LoggerFactory.getLogger("POTSimulation");

    public static void main(String[] args) {
        byte[] genesisGenerationSignature = Hex.decode("442c29a4d18f192164006030640fb54c8b9ffd4f5750d2f6dca192dc653c52ad");
        BigInteger genesisBaseTarget = new BigInteger("0fffffffffffffff", 16);
        BigInteger baseTarget = new BigInteger("369D0369D036978",16);
        byte[] generationSignature;
        byte[] pubkey;

        for (int i = 0; i < 3; i++) {
            ECKey ecKey = new ECKey(Utils.getRandom());
            pubkey = ecKey.getPubKey();
            byte[] data = new byte[genesisGenerationSignature.length + pubkey.length];
            System.arraycopy(genesisGenerationSignature, 0, data, 0, genesisGenerationSignature.length);
            System.arraycopy(pubkey, 0, data, genesisGenerationSignature.length, pubkey.length);
            generationSignature = Sha256Hash.hash(data);
            BigInteger hit = calculateRandomHit(generationSignature);
        }
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
        logger.info("bhit:{}", bhit);

        BigInteger bhitUzero = bhit.add(BigInteger.ONE);
        logger.info("bhitUzero:{}", bhitUzero);

        double logarithm = abs(log(bhitUzero.doubleValue()) - 2 * log(DiffAdjustNumeratorHalf.doubleValue()));
        logarithm = logarithm * 1000;
        logger.info("logarithm:{}", logarithm);

        long ulogarithm = (new Double(logarithm)).longValue();
        logger.info("ulogarithm:{}", ulogarithm);

        BigInteger adjustHit = DiffAdjustNumeratorCoe.multiply(BigInteger.valueOf(ulogarithm)).divide(BigInteger.valueOf(1000));
        logger.info("adjustHit:{}", adjustHit);

        return adjustHit;
    }
}
