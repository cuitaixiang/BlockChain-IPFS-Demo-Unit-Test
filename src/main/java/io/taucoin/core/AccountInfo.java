package io.taucoin.core;

import io.taucoin.util.RLP;
import io.taucoin.util.RLPList;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

public class AccountInfo {
    private BigInteger balance;
    private BigInteger power;
    private BigInteger income;

    private byte[] rlpEncoded ;
    private boolean parsed = false;

    public AccountInfo(BigInteger balance, BigInteger power, BigInteger income) {
        this.balance = balance;
        this.power = power;
        this.income = income;
    }

    public AccountInfo(String data) {
        this.rlpEncoded = Hex.decode(data);
    }

    public AccountInfo(byte[] rlpEncoded) {
        this.rlpEncoded = rlpEncoded;
    }

    public void parseRLP() {
        RLPList decodedAccountList = RLP.decode2(rlpEncoded);
        RLPList account = (RLPList) decodedAccountList.get(0);
        this.balance = account.get(0).getRLPData() == null ? BigInteger.ZERO :
                new BigInteger(1, account.get(0).getRLPData());
        this.power = account.get(1).getRLPData() == null ? BigInteger.ZERO :
                new BigInteger(1, account.get(1).getRLPData());
        this.income = account.get(2).getRLPData() == null ? BigInteger.ZERO :
                new BigInteger(1, account.get(2).getRLPData());
        this.parsed = true;
    }

    public byte[] getEncoded() {
        byte[] balance = RLP.encodeBigInteger(this.balance);
        byte[] power = RLP.encodeBigInteger(this.power);
        byte[] income = RLP.encodeBigInteger(this.income);
        return RLP.encodeList(balance, power, income);
    }

    public BigInteger getBalance() {
        if (!parsed) {
            parseRLP();
        }
        return balance;
    }

    public BigInteger getPower() {
        if (!parsed) {
            parseRLP();
        }
        return power;
    }

    public BigInteger getIncome() {
        if (!parsed) {
            parseRLP();
        }
        return income;
    }

    public void setIncome(BigInteger income) {
        this.income = income;
    }

}
