package com.iconloop.score.token.alice;

import score.Address;
import score.ObjectReader;
import score.ObjectWriter;

import java.math.BigInteger;
import java.util.Set;

public class Proposal {
    public BigInteger id;
    public BigInteger startBlockHeight;
    public BigInteger endBlockHeight;
    public long startTimestamp;
    public int status;
    public Address proposer;
    public VoteInfo vote;

    public Proposal(
            BigInteger id,
            BigInteger startBlockHeight,
            BigInteger endBlockHeight,
            long startTimestamp,
            int status,
            Address proposer,
            VoteInfo vote
    ){
        this.id = id;
        this.startBlockHeight = startBlockHeight;
        this.endBlockHeight = endBlockHeight;
        this.startTimestamp = startTimestamp;
        this.status = status;
        this.proposer = proposer;
        this.vote = vote;
    }

    public static void writeObject(ObjectWriter w, Proposal p) {
        w.beginList(7);
        w.write(p.id);
        w.write(p.startBlockHeight);
        w.write(p.endBlockHeight);
        w.write(p.startTimestamp);
        w.write(p.status);
        w.write(p.proposer);
        w.write(p.vote);
        w.end();
    }

    public static Proposal readObject(ObjectReader r){
        r.beginList();
        var p = new Proposal(
                r.readBigInteger(),
                r.readBigInteger(),
                r.readBigInteger(),
                r.readLong(),
                r.readInt(),
                r.readAddress(),
                r.read(VoteInfo.class)
        );
        r.end();
        return p;
    }

    public void updateVote(Address voter, BigInteger votingPower, int voteChoice){
        if(voteChoice == VoteInfo.AGREE_VOTE){
            Set<Address> agreeVotes = vote.agree.getVoters();
            agreeVotes.add(voter);
            BigInteger updatedAmount = vote.agree.getAmount().add(votingPower);
            vote.agree.setVoters(agreeVotes);
            vote.agree.setAmount(updatedAmount);
            updateNoVote(voter, votingPower);
        } else if (voteChoice == VoteInfo.DISAGREE_VOTE) {
            Set<Address> disagreeVotes = vote.disagree.getVoters();
            disagreeVotes.add(voter);
            BigInteger updatedAmount = vote.disagree.getAmount().add(votingPower);
            vote.disagree.setVoters(disagreeVotes);
            vote.disagree.setAmount(updatedAmount);
            updateNoVote(voter, votingPower);
        }
    }

    public void updateNoVote(Address voter, BigInteger votingPower){
        Set<Address> noVotes = vote.noVote.getVoters();
        noVotes.remove(voter);
        BigInteger updatedAmount = vote.noVote.getAmount().subtract(votingPower);
        vote.noVote.setVoters(noVotes);
        vote.noVote.setAmount(updatedAmount);
    }

    public void swapVote(Address voter, BigInteger votingPower, int initialVote){
        if(initialVote == VoteInfo.AGREE_VOTE){
            Set<Address> agreeVotes = vote.agree.getVoters();
            agreeVotes.remove(voter);
            BigInteger updatedAgree = vote.agree.getAmount().subtract(votingPower);
            vote.agree.setVoters(agreeVotes);
            vote.agree.setAmount(updatedAgree);

            Set<Address> disagreeVotes = vote.disagree.getVoters();
            disagreeVotes.add(voter);
            BigInteger updatedDisagree = vote.disagree.getAmount().add(votingPower);
            vote.disagree.setVoters(disagreeVotes);
            vote.disagree.setAmount(updatedDisagree);
        }
        else if(initialVote == VoteInfo.DISAGREE_VOTE){
            Set<Address> disagreeVotes = vote.disagree.getVoters();
            disagreeVotes.remove(voter);
            BigInteger updatedDisagree = vote.disagree.getAmount().subtract(votingPower);
            vote.disagree.setVoters(disagreeVotes);
            vote.disagree.setAmount(updatedDisagree);

            Set<Address> agreeVotes = vote.agree.getVoters();
            agreeVotes.add(voter);
            BigInteger updatedAgree = vote.agree.getAmount().add(votingPower);
            vote.agree.setVoters(agreeVotes);
            vote.agree.setAmount(updatedAgree);
        }
    }

    public void setStatus(int status){
        this.status = status;
    }
}
