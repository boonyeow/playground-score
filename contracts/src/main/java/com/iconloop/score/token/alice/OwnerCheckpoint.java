package com.iconloop.score.token.alice;

import score.Address;
import score.ObjectReader;
import score.ObjectWriter;
import scorex.util.HashSet;

import java.math.BigInteger;
import java.util.Set;

public class OwnerCheckpoint {
    public BigInteger blockHeight;
    public Set<Address> owners;

    public OwnerCheckpoint(BigInteger blockHeight, Set<Address> owners){
        this.blockHeight = blockHeight;
        this.owners = owners;
    }

    public OwnerCheckpoint(){
        this(BigInteger.ZERO, new HashSet<Address>());
    }

    public void setBlockHeight(BigInteger blockHeight){
        this.blockHeight = blockHeight;
    }

    public void setOwners(Set<Address> owners){
        this.owners = owners;
    }

    public Integer size(){
        return owners.size();
    }

    public static void writeObject(ObjectWriter w, OwnerCheckpoint obj) {
        w.beginList(2);
        w.write(obj.size());
        w.write(obj.blockHeight);
        for (Address addr: obj.owners){
            w.write(addr);
        }
        w.end();
    }

    public static OwnerCheckpoint readObject(ObjectReader r){
        r.beginList();
        int size = r.readInt();
        var blockHeight = r.readBigInteger();
        var oc = new OwnerCheckpoint();
        Set<Address> owners = new HashSet<Address>();

        for(int i = 0; i<size; i++){
            owners.add(r.readAddress());
        }

        oc.setBlockHeight(blockHeight);
        oc.setOwners(owners);
        r.end();
        return oc;
    }
}
