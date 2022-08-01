package com.iconloop.score.token.alice;

import score.Address;
import score.ObjectReader;
import score.ObjectWriter;
import scorex.util.HashSet;

import java.math.BigInteger;
import java.util.Set;

public class Checkpoint {
    public BigInteger blockHeight;
    public BigInteger votingPower;
    public Address delegate;
    public Set<Address> delegatedBy;
    public Checkpoint(BigInteger blockHeight, BigInteger votingPower, Address delegate, Set<Address> delegatedBy){
        this.blockHeight = blockHeight;
        this.votingPower = votingPower;
        this.delegate = delegate;
        this.delegatedBy = delegatedBy;
    }

    public Checkpoint(){
        this(BigInteger.ZERO, BigInteger.ZERO, null, new HashSet<Address>());
    }

    public void setDelegate(Address delegate){
        this.delegate = delegate;
    }

    public void setDelegatedBy(Set<Address> delegatedBy){
        this.delegatedBy = delegatedBy;
    }

    public Integer size(){
        return delegatedBy.size();
    }

    public static void writeObject(ObjectWriter w, Checkpoint obj) {
        w.beginList(4);
        w.write(obj.size());
        w.write(obj.blockHeight);
        w.write(obj.votingPower);
        w.write(obj.delegate);
        for (Address addr: obj.delegatedBy){
            w.write(addr);
        }
        w.end();
    }

    public static Checkpoint readObject(ObjectReader r) {
        var obj = new Checkpoint();
        r.beginList();
        int size = r.readInt();
        obj.blockHeight = r.readBigInteger();
        obj.votingPower = r.readBigInteger();
        Address delegate = r.readAddress();
        Set<Address> delegatedBy = new HashSet<Address>();
        for(int i = 0; i<size; i++){
            delegatedBy.add(r.readAddress());
        }
        obj.setDelegate(delegate);
        obj.setDelegatedBy(delegatedBy);
        r.end();
        return obj;
    }
}
