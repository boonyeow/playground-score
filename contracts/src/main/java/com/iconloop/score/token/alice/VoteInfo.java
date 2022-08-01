package com.iconloop.score.token.alice;

import score.Address;
import score.ObjectReader;
import score.ObjectWriter;
import scorex.util.HashMap;
import scorex.util.HashSet;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class VoteInfo {
    final static int AGREE_VOTE = 1;
    final static int DISAGREE_VOTE = 0;
    Slot noVote;
    Slot agree;
    Slot disagree;

    public static class Slot {
        Set<Address> voters;
        BigInteger amount;
        public Slot(){
            this.voters = new HashSet<Address>();
            this.amount = BigInteger.ZERO;
        }

        public Set<Address> getVoters() {
            return this.voters;
        }
        public void setVoters(Set<Address> voters) {
            this.voters = voters;
        }

        public BigInteger getAmount() {
            return amount;
        }
        public void setAmount(BigInteger amount) {
            this.amount = amount;
        }
        public int size() {
            return voters.size();
        }

        public static void writeObject(ObjectWriter w, Slot obj) {
            w.beginList(2);
            w.write(obj.size());
            for (Address v : obj.voters) {
                w.write(v);
            }
            w.write(obj.amount);
            w.end();
        }

        public static Slot readObject(ObjectReader r) {
            r.beginList();
            int size = r.readInt();
            var n = new Slot();
            Set<Address> voters = new HashSet<Address>();

            for (int i = 0; i < size; i++) {
                voters.add(r.readAddress());
            }
            BigInteger amount = r.readBigInteger();

            n.setVoters(voters);
            n.setAmount(amount);
            r.end();
            return n;
        }
    }

    // VOTE INFO

    public VoteInfo(){
        this.noVote = new Slot();
        this.agree = new Slot();
        this.disagree = new Slot();
    }

    public VoteInfo(
            Slot n,
            Slot a,
            Slot d
    ) {
        this.noVote = n;
        this.agree = a;
        this.disagree = d;
    }

    public static void writeObject(ObjectWriter w, VoteInfo v) {
        w.beginList(3);
        w.write(v.noVote);
        w.write(v.agree);
        w.write(v.disagree);
        w.end();
    }

    public static VoteInfo readObject(ObjectReader r) {
        r.beginList();
        var v = new VoteInfo(
                r.read(Slot.class),
                r.read(Slot.class),
                r.read(Slot.class)
        );
        r.end();
        return v;
    }

    public BigInteger getNoVoteAmount(){
        return noVote.getAmount();
    }
}


