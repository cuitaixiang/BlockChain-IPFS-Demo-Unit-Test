package io.taucoin.core.transaction;

import io.taucoin.util.RLP;
import io.taucoin.util.RLPList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class Account {
    private static final Logger logger = LoggerFactory.getLogger("account");
    public BigInteger balance;
    public BigInteger power;

    public  Account(byte[] rlpEncoded) {
        RLPList decodedAccountList = RLP.decode2(rlpEncoded);
        RLPList account = (RLPList) decodedAccountList.get(0);
        this.balance = account.get(0).getRLPData() == null ? BigInteger.ZERO :
                new BigInteger(1, account.get(0).getRLPData());
        this.power = account.get(1).getRLPData() == null ? BigInteger.ZERO :
                new BigInteger(1, account.get(1).getRLPData());
    }

    public byte[] getEncoded() {
        byte[] balance = RLP.encodeBigInteger(this.balance);
        byte[] power = RLP.encodeBigInteger(this.power);
        return RLP.encodeList(balance, power);
    }

}
