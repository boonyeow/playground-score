package com.iconloop.score.token.alice;
import score.*;
import score.Context;
import score.annotation.External;
import score.annotation.Optional;
import score.annotation.Payable;
import scorex.util.HashSet;
import java.math.BigInteger;
import scorex.util.HashMap;

import java.util.Map;
import java.util.Set;

public class Alice extends IRC31MintBurn {
    private static final BigInteger EXA = BigInteger.valueOf(1_000_000_000_000_000_000L);
    private final String name;
    private final Address signerAddress;
    // Address, Set() -> dictDB; address custom class

    private final VarDB<BigInteger> proposalCounter = Context.newVarDB("proposalCounter", BigInteger.class);
    private final DictDB<BigInteger, Proposal> proposalDB = Context.newDictDB("proposalDB", Proposal.class);
    private final ArrayDB<BigInteger> activeProposals = Context.newArrayDB("activeProposals", BigInteger.class);
    private final BranchDB<BigInteger, DictDB<Address, Integer>> voteStatusDB = Context.newBranchDB("voteStatusDB", BigInteger.class);
    private final BranchDB<Address, ArrayDB<Checkpoint>> _delegationCheckpoints = Context.newBranchDB("delegationCheckpoints", Checkpoint.class);
    private final ArrayDB<OwnerCheckpoint> _ownerCheckpoints  = Context.newArrayDB("ownerCheckpoints", OwnerCheckpoint.class);

    private final VarDB<ProjectInfo> projectInfo = Context.newVarDB("project_info", ProjectInfo.class);

    final static int PROPOSAL_STATUS_ACTIVE = 0;
    final static int PROPOSAL_STATUS_APPROVED = 1;
    final static int PROPOSAL_STATUS_REJECTED = 2;
    final static int PROPOSAL_STATUS_EXECUTED = 3;
    final static int PROPOSAL_STATUS_STATUS_CANCELLED = 4;

    public Alice(String _name, Address _signerAddress){
        this.name = _name;
        this.signerAddress = _signerAddress;
        ProjectInfo pi = new ProjectInfo(_name);
        projectInfo.set(pi);
    }

    @External
    public void updateProjectInfo(@Optional String name, @Optional String thumbnailSrc, @Optional String description, @Optional String details, @Optional BigInteger fundingGoal, @Optional BigInteger pricePerNFT, @Optional BigInteger startTimestamp, @Optional BigInteger endTimestamp, @Optional BigInteger withdrawalRate){
        ProjectInfo pi = projectInfo.get();
        if(name != ""){
            pi.setName(name);
        }

        if(thumbnailSrc != ""){
            pi.setThumbnailSrc(thumbnailSrc);
        }

        if(description != ""){
            pi.setDescription(description);
        }

        if(details != ""){
            pi.setDetails(details);
        }

        if(!fundingGoal.equals(BigInteger.ZERO)){
            pi.setFundingGoal(fundingGoal);
        }

        if(!pricePerNFT.equals(BigInteger.ZERO)){
            pi.setPricePerNFT(pricePerNFT);
        }

        if(!startTimestamp.equals(BigInteger.ZERO)){
            pi.setStartTimestamp(startTimestamp);
        }

        if(!endTimestamp.equals(BigInteger.ZERO)){
            pi.setEndTimestamp(endTimestamp);
        }

        if(!withdrawalRate.equals(BigInteger.ZERO)){
            pi.setWithdrawalRate(withdrawalRate);
        }

        projectInfo.set(pi);
    }

    @External(readonly=true)
    public Map<String,Object> getProjectInfo(){
        ProjectInfo pi = projectInfo.get();
        return pi.toMap();
    }

    public String getProjectInfo(String returnType){
        ProjectInfo pi = projectInfo.get();

        if(returnType == "thumbnailSrc"){
            return pi.thumbnailSrc;
        }
        else if(returnType == "name"){
            return pi.name;
        }
        else if (returnType == "description"){
            return pi.description;
        }
        else if (returnType == "details"){
            return pi.details;
        }
        else if(returnType == "fundingGoal"){
            return pi.fundingGoal.toString();
        }
        else if(returnType == "pricePerNFT"){
            return pi.pricePerNFT.toString();
        }
        else if(returnType == "startTimestamp"){
            return pi.startTimestamp.toString();
        }
        else if(returnType == "endTimestamp"){
            return pi.endTimestamp.toString();
        }
        return "";
    }

    @External
    public void createProposal(BigInteger startBlockHeight){
        Address caller = Context.getCaller();
        Context.require(balanceOf(caller).intValue() > 0, "caller not allowed to create proposal");

        BigInteger id = proposalCounter.getOrDefault(BigInteger.ZERO);
        BigInteger currentBlockHeight = BigInteger.valueOf(Context.getBlockHeight());
        Proposal p = new Proposal(
                id,
                currentBlockHeight,
                currentBlockHeight.add(BigInteger.valueOf(1000)),
                Context.getTransactionTimestamp(),
                PROPOSAL_STATUS_ACTIVE,
                caller,
                new VoteInfo()
        );

        Set<Address> voters = new HashSet<Address>();
        for(int i = 0; i < totalSupply().intValue(); i++){
            voters.add(ownerOf(BigInteger.valueOf(i)));
        }
        p.vote.noVote.setVoters(voters);
        p.vote.noVote.setAmount(totalSupply());
        proposalDB.set(id, p);
        proposalCounter.set(id.add(BigInteger.ONE));
        activeProposals.add(id);
    }

    @External
    public void cancelProposal(BigInteger id){
        // to do list: write testcase
        Proposal p = proposalDB.getOrDefault(id, null);
        Context.require(p != null, "proposal not found");
        Context.require(p.proposer.equals(Context.getCaller()), "user not authorized to cancel proposal");
        p.setStatus(PROPOSAL_STATUS_STATUS_CANCELLED);

        int index = 0;
        for(int i=0; i < activeProposals.size(); i++){
            if(activeProposals.get(i).equals(p.id)){
                index = i;
                break;
            }
        }

        activeProposals.set(index, activeProposals.get(activeProposals.size()-1));
        activeProposals.removeLast();
        proposalDB.set(p.id, p);
    }

    @External
    public void voteProposal(BigInteger id, int voteChoice){
        Address user = Context.getCaller();
        Context.require(balanceOf(user).intValue() > 0, "user not allowed to vote proposal");
        Proposal p = proposalDB.getOrDefault(id, null);
        Context.require(p != null, "proposal not found");

        // write tc, sm.Block.increase to fast forward to endblock!
//        BigInteger currentBlockHeight = BigInteger.valueOf(Context.getBlockHeight());
//        Context.require(currentBlockHeight.compareTo(p.endBlockHeight) <= 0, "Proposal ended");


        Address delegate = _delegationCheckpoints.at(user).get(_delegationCheckpoints.at(user).size()-1).delegate;
        Context.require(delegate.equals(user), "caller must delegate to themselves");
        Context.require(voteChoice == VoteInfo.AGREE_VOTE || voteChoice == VoteInfo.DISAGREE_VOTE, "invalid vote choice");

        // check if voted
        int userVote = voteStatusDB.at(id).getOrDefault(user,2);
        Context.require(userVote == 2, "user has an existing vote");

        p.updateVote(user, balanceOf(user), voteChoice);
        voteStatusDB.at(id).set(user, voteChoice);
        proposalDB.set(id, p);
    }

    @External
    public void swapVote(BigInteger id){
        Address user = Context.getCaller();

        Context.require(balanceOf(user).intValue() > 0, "user not allowed to vote proposal");
        Proposal p = proposalDB.getOrDefault(id, null);
        Context.require(p != null, "proposal not found");

// write tc, sm.Block.increase to fast forward to endblock!
//        BigInteger currentBlockHeight = BigInteger.valueOf(Context.getBlockHeight());
//        Context.require(currentBlockHeight.compareTo(p.endBlockHeight) <= 0, "Proposal ended");

        int userVote = voteStatusDB.at(id).getOrDefault(user,2);
        Context.require(userVote == VoteInfo.AGREE_VOTE || userVote == VoteInfo.DISAGREE_VOTE, "user must have voted");

        p.swapVote(user, balanceOf(user), userVote);
        int updatedVote = (userVote == VoteInfo.AGREE_VOTE) ? VoteInfo.DISAGREE_VOTE : VoteInfo.AGREE_VOTE;

        voteStatusDB.at(id).set(user, updatedVote);
        proposalDB.set(id, p);
    }

    // this is for testing purposes -- to remove
    @External(readonly = true)
    public BigInteger getVoteAmount(BigInteger id, String voteChoice){
        Proposal p = proposalDB.getOrDefault(id, null);
        Context.require(p != null, "proposal not found");
        if(voteChoice == "a"){
            return p.vote.agree.getAmount();
        }
        else if(voteChoice == "d"){
            return p.vote.disagree.getAmount();
        }
        return p.vote.noVote.getAmount();
    }

    // this is for testing purposes -- to remove
    public Set<Address> getVoteList(BigInteger id, String voteChoice){
        Proposal p = proposalDB.getOrDefault(id, null);
        Context.require(p != null, "proposal not found");
        if(voteChoice == "a"){
            return p.vote.agree.getVoters();
        }
        else if(voteChoice == "d"){
            return p.vote.disagree.getVoters();
        }
        return p.vote.noVote.getVoters();
    }

    @External(readonly = true)
    public BigInteger[] getVotes(BigInteger id){
        Proposal p = proposalDB.getOrDefault(id, null);
        if(p == null){
            return new BigInteger[0];
        }

        Set<Address> owners = getOwnersAtBlock(p.endBlockHeight);
        BigInteger agreeVotes = p.vote.agree.getAmount();
        BigInteger disagreeVotes = p.vote.disagree.getAmount();
        BigInteger noVotes = BigInteger.ZERO;

        Map<Address, Address> mapping = new HashMap<Address, Address>();
        for(Address user : p.vote.noVote.getVoters()){
            Checkpoint cp = _delegationCheckpoints.at(user).get(_delegationCheckpoints.at(user).size() - 1);
            if(cp.delegate.equals(user)){
                // user delegated to him/herself but did not vote
                mapping.put(user, user);
                noVotes = noVotes.add(cp.votingPower);
            }
            else{
                // user delegated to someone else
                Address usr = user; // initialze starting point
                Address delegate = cp.delegate;
                Set<Address> temp = new HashSet<Address>();
                BigInteger consolidatedPower = BigInteger.ZERO;

                while(!usr.equals(delegate)){
                    if(!mapping.containsKey(usr)){
                        consolidatedPower = consolidatedPower.add(getCheckpointAtBlock(usr, p.endBlockHeight).votingPower);
                        temp.add(delegate);
                    }
                    usr = delegate;
                    delegate = getCheckpointAtBlock(usr, p.endBlockHeight).delegate;
                }

                for(Address addr : temp){
                    mapping.put(addr, delegate);
                }

                Integer _voteChoice = voteStatusDB.at(id).getOrDefault(delegate, 2);
                if(_voteChoice == VoteInfo.DISAGREE_VOTE)
                {
                    disagreeVotes = disagreeVotes.add(consolidatedPower);
                }
                else if(_voteChoice == VoteInfo.AGREE_VOTE){
                    agreeVotes = agreeVotes.add(consolidatedPower);
                }
                else if(_voteChoice == 2){
                    noVotes = noVotes.add(consolidatedPower);
                }
            }
        }
        BigInteger[] votes = new BigInteger[3];
        votes[0] = disagreeVotes;
        votes[1] = agreeVotes;
        votes[2] = noVotes;
        return votes;
    }

    // for unit test
    public int checkVoteStatus(BigInteger id, Address user){
        // 0 = Disagree, 1 = Agree, 2 = NoVote (all other addresses)
        Proposal p = proposalDB.getOrDefault(id, null);
        Context.require(p != null, "proposal not found");
        int userVote = voteStatusDB.at(id).getOrDefault(user, 2);
        return userVote;
    }

    /**
     * Delegation functions
     */
    @External
    public void setDelegation(Address delegate){
        Context.require(balanceOf(Context.getCaller()).intValue() > 0, "caller must own at least 1 nft");
        Context.require(balanceOf(delegate).intValue() > 0, "delegate must own at least 1 nft");

        // check for cycles // need to review code
        // scenario 1: 1 => 2 => 3 => 1
        // scenario 2: 1 => 2 => 3 => 2
        Address user = Context.getCaller();

        if(!user.equals(delegate)){
            Address tempDelegate = delegate;
            while(!user.equals(tempDelegate)){
                user = tempDelegate;
                tempDelegate = _delegationCheckpoints.at(user).get(_delegationCheckpoints.at(user).size() - 1).delegate;
            }
            Context.require(!Context.getCaller().equals(tempDelegate), "delegate not allowed, please select another delegate ");
        }

        _delegate(Context.getCaller(), delegate);
    }

    protected void _delegate(Address user, Address delegate){
        BigInteger currentBlockHeight = BigInteger.valueOf(Context.getBlockHeight());
        int userCheckpointSize = _delegationCheckpoints.at(user).size();
        int delegateCheckpointSize = _delegationCheckpoints.at(delegate).size();

        Checkpoint userCheckpoint = _delegationCheckpoints.at(user).get(userCheckpointSize-1);
        Checkpoint delegateCheckpoint = _delegationCheckpoints.at(delegate).get(delegateCheckpointSize-1);
        
        Address prevDelegate = userCheckpoint.delegate;
        if(userCheckpoint.blockHeight.equals(currentBlockHeight)){
            _delegationCheckpoints.at(user).set(userCheckpointSize-1, new Checkpoint(currentBlockHeight, userCheckpoint.votingPower, delegate, userCheckpoint.delegatedBy));
        }
        else{
            _delegationCheckpoints.at(user).add(new Checkpoint(currentBlockHeight, userCheckpoint.votingPower, delegate, userCheckpoint.delegatedBy));
        }

        if(!user.equals(delegate)){
            delegateCheckpoint.delegatedBy.add(user);
            if(delegateCheckpoint.blockHeight.equals(currentBlockHeight)){
                _delegationCheckpoints.at(delegate).set(delegateCheckpointSize-1, new Checkpoint(currentBlockHeight, delegateCheckpoint.votingPower, delegateCheckpoint.delegate, delegateCheckpoint.delegatedBy));
            }
            else{
                _delegationCheckpoints.at(delegate).add(new Checkpoint(currentBlockHeight, delegateCheckpoint.votingPower, delegateCheckpoint.delegate, delegateCheckpoint.delegatedBy));
            }
        }
        
        if(!prevDelegate.equals(delegate)){
            int prevDelegateSize = _delegationCheckpoints.at(prevDelegate).size();
            Checkpoint prevDelegateCheckpoint = _delegationCheckpoints.at(prevDelegate).get(prevDelegateSize - 1);
            prevDelegateCheckpoint.delegatedBy.remove(user);
            if(prevDelegateCheckpoint.blockHeight.equals(currentBlockHeight)){
                _delegationCheckpoints.at(prevDelegate).set(prevDelegateSize-1, new Checkpoint(currentBlockHeight, prevDelegateCheckpoint.votingPower, prevDelegateCheckpoint.delegate, prevDelegateCheckpoint.delegatedBy));
            }
            else {
                _delegationCheckpoints.at(prevDelegate).add(new Checkpoint(currentBlockHeight, prevDelegateCheckpoint.votingPower, prevDelegateCheckpoint.delegate, prevDelegateCheckpoint.delegatedBy));
            }
        }
    }

    // [START] For unit tests
    @External(readonly=true)
    public Address getDelegate(Address user){
        return _delegationCheckpoints.at(user).get(_delegationCheckpoints.at(user).size() - 1).delegate;
    }

    @External(readonly=true)
    public Address[] getDelegatedBy(Address user){
        Set<Address> delegatedBy = _delegationCheckpoints.at(user).get(_delegationCheckpoints.at(user).size() - 1).delegatedBy;
        Address[] array = new Address[delegatedBy.size()];
        int index =0 ;
        for(Address addr : delegatedBy){
            array[index++] = addr;
        }
        return array;
    }

    // [END] For unit tests

    /**
    * Utility functions
    **/

    public Checkpoint getCheckpointAtBlock(Address user, BigInteger blockHeight){
        int high = _delegationCheckpoints.at(user).size();
        int low = 0;

        while (low < high) {
            int mid = (high + low) / 2;
            if (_delegationCheckpoints.at(user).get(mid).blockHeight.compareTo(blockHeight) > 0) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return (high == 0) ? null : _delegationCheckpoints.at(user).get(high-1);
    }

    public Set<Address> getOwnersAtBlock(BigInteger blockHeight){
        int high = _ownerCheckpoints.size();
        int low = 0;
        while (low < high){
            int mid = (high + low) / 2;
            if(_ownerCheckpoints.get(mid).blockHeight.compareTo(blockHeight) > 0){
                high = mid;
            }
            else{
                low= mid + 1;
            }
        }
        return (high == 0) ? new HashSet<Address>() : _ownerCheckpoints.get(high-1).owners;
    }

    /*
    Hooks
    */

    @Override
    protected void _afterTokenTransfer(Address from, Address to, BigInteger tokenId){
        Set<Address> owners = new HashSet<Address>();
        BigInteger currentBlockHeight = BigInteger.valueOf(Context.getBlockHeight());
        for(int i = 0; i < totalSupply().intValue(); i++){
            owners.add(ownerOf(BigInteger.valueOf(i)));
        }
        _ownerCheckpoints.add(new OwnerCheckpoint(currentBlockHeight,owners));

        int size = activeProposals.size();
        for(int i = 0; i < size; i++){
            BigInteger id = activeProposals.get(i);
            Proposal p = proposalDB.get(id);

            int fromVote = voteStatusDB.at(id).getOrDefault(from, 2);
            int fromBalance = balanceOf(from).intValue();

            if(fromBalance == 0 && !from.equals(ZERO_ADDRESS)){
                if(fromVote == 0){
                    p.vote.disagree.voters.remove(from);
                    p.vote.disagree.setVoters(p.vote.disagree.getVoters());
                    p.vote.disagree.setAmount(p.vote.disagree.getAmount().subtract(BigInteger.ONE));
                } else if (fromVote == 1) {
                    p.vote.agree.voters.remove(from);
                    p.vote.agree.setVoters(p.vote.agree.getVoters());
                    p.vote.agree.setAmount(p.vote.agree.getAmount().subtract(BigInteger.ONE));
                } else if (fromVote == 2) {
                    p.vote.noVote.voters.remove(from);
                    p.vote.noVote.setVoters(p.vote.noVote.getVoters());
                    p.vote.noVote.setAmount(p.vote.noVote.getAmount().subtract(BigInteger.ONE));
                }
                proposalDB.set(id, p);
            }
            else if(fromBalance > 0 && !from.equals(ZERO_ADDRESS)){
                if(fromVote == 0){
                    p.vote.disagree.setAmount(p.vote.disagree.getAmount().subtract(BigInteger.ONE));
                } else if(fromVote == 1){
                    p.vote.agree.setAmount(p.vote.agree.getAmount().subtract(BigInteger.ONE));
                } else if(fromVote == 2){
                    p.vote.noVote.setAmount(p.vote.noVote.getAmount().subtract(BigInteger.ONE));
                }
                proposalDB.set(id, p);
            }

            int toVote = voteStatusDB.at(id).getOrDefault(to, 2);
            int toBalance = balanceOf(to).intValue();
            if(toBalance == 1 && !to.equals(ZERO_ADDRESS)){
                Set<Address> voters = p.vote.noVote.getVoters();
                voters.add(to);
                p.vote.noVote.setVoters(voters);
                p.vote.noVote.setAmount(p.vote.noVote.getAmount().add(BigInteger.ONE));
                proposalDB.set(id, p);
            }
            else if(toBalance > 1 && !to.equals(ZERO_ADDRESS)){
                if(toVote == 0){
                    p.vote.disagree.setAmount(p.vote.disagree.getAmount().add(BigInteger.ONE));
                } else if(toVote == 1){
                    p.vote.agree.setAmount(p.vote.agree.getAmount().add(BigInteger.ONE));
                } else if(toVote == 2){
                    p.vote.noVote.setAmount(p.vote.noVote.getAmount().add(BigInteger.ONE));
                }
                proposalDB.set(id, p);
            }
        }

        if(from != to && from != ZERO_ADDRESS)
        {
            int fromCheckpointSize = _delegationCheckpoints.at(from).size();
            if(fromCheckpointSize == 0){
                _delegationCheckpoints.at(from).add(new Checkpoint(currentBlockHeight, balanceOf(from), from, new HashSet<Address>()));
            }
            else{
                Checkpoint latestCheckpoint = _delegationCheckpoints.at(from).get(fromCheckpointSize - 1);
                if(_delegationCheckpoints.at(from).get(fromCheckpointSize - 1).blockHeight.equals(currentBlockHeight)){
                    _delegationCheckpoints.at(from).set(fromCheckpointSize-1, new Checkpoint(currentBlockHeight, balanceOf(from), latestCheckpoint.delegate, latestCheckpoint.delegatedBy));
                }
                else{
                    _delegationCheckpoints.at(from).add(new Checkpoint(currentBlockHeight, balanceOf(from), latestCheckpoint.delegate, latestCheckpoint.delegatedBy));
                }
            }
            //emit event
        }

        if(from != to && to != ZERO_ADDRESS){
            int toCheckpointSize = _delegationCheckpoints.at(to).size();
            if(toCheckpointSize == 0){
                _delegationCheckpoints.at(to).add(new Checkpoint(currentBlockHeight, balanceOf(to), to, new HashSet<Address>()));
            }
            else{
                Checkpoint latestCheckpoint = _delegationCheckpoints.at(to).get(toCheckpointSize - 1);
                if(_delegationCheckpoints.at(to).get(toCheckpointSize - 1).blockHeight.equals(currentBlockHeight)){
                    _delegationCheckpoints.at(to).set(toCheckpointSize-1, new Checkpoint(currentBlockHeight, balanceOf(to), latestCheckpoint.delegate, latestCheckpoint.delegatedBy));
                }
                else{
                    _delegationCheckpoints.at(to).add(new Checkpoint(currentBlockHeight, balanceOf(to), latestCheckpoint.delegate, latestCheckpoint.delegatedBy));
                }
            }
            //emit event
        }
        super._afterTokenTransfer(from, to, tokenId);
    }

    @External(readonly=true)
    public Map<String, BigInteger> getAllVoters(){
        Map<String, BigInteger> temp = new HashMap<>();
        Set<Address> owners = getOwnersAtBlock(BigInteger.valueOf(Context.getBlockHeight()));

        for(Address owner: owners){
            temp.put(owner.toString(), balanceOf(owner));
        }
        return temp;
    }

    @External
    @Payable
    public void test1(BigInteger quantity){ // mint; to change name
        Context.require(quantity.compareTo(BigInteger.ONE) >= 0, "User must mint at least one NFT");
        ProjectInfo pi = projectInfo.get();
        BigInteger totalPrice = quantity.multiply(pi.pricePerNFT).multiply(EXA);
        Context.require(totalPrice.compareTo(Context.getValue()) == 0, "Invalid amount received");

        BigInteger nextID = totalSupply();
        for(int i = 0; i < quantity.intValue(); i++){
            super.mint(nextID.add(BigInteger.valueOf(i)));
        }
    }

    @External(readonly = true)
    public Address[] getValidDelegates(Address user){
        // to test

        Set<Address> delegates = new HashSet<Address>();
        for(int i = 0; i < tokenOwners.length(); i++){
            Address delegate = tokenOwners.get(BigInteger.valueOf(i));
            if(delegate.equals(user)){
                continue;
            }

            Address tempUser = user;
            Address tempDelegate = delegate;
            if(!tempUser.equals(tempDelegate)){
                while(!tempUser.equals(tempDelegate)){
                    tempUser = tempDelegate;
                    tempDelegate = _delegationCheckpoints.at(tempUser).get(_delegationCheckpoints.at(tempUser).size() - 1).delegate;
                }
                if(!user.equals(tempDelegate)){
                    delegates.add(delegate);
                }
            }
        }

        Address[] _delegates = new Address[delegates.size()];
        int index = 0;
        for(Address addr : delegates){
            _delegates[index++] = addr;
        }
        return _delegates;
    }

    /**
     * Readonly functions
     **/

    @External(readonly = true)
    public Map<String, Map<String, String>> getAllProposals(){
        Map<String, Map<String, String>> proposals = new HashMap<>();
        int latest = proposalCounter.get().intValue();
        for(int i = 0; i < latest; i++){
            Proposal p = proposalDB.get(BigInteger.valueOf(i));
            BigInteger[] voteStatus = getVotes(BigInteger.valueOf(i));

            Map<String, String> proposalInfo = new HashMap<>();
            proposalInfo.put("startBlockHeight", p.startBlockHeight.toString());
            proposalInfo.put("startTimestamp", String.valueOf(p.startTimestamp));
            proposalInfo.put("status", String.valueOf(p.status));
            proposalInfo.put("disagreeVotes", voteStatus[0].toString());
            proposalInfo.put("agreeVotes", voteStatus[1].toString());
            proposalInfo.put("noVotes", voteStatus[2].toString());
            proposals.put(String.valueOf(i), proposalInfo);
        }
        return proposals;
    }

    @External(readonly=true)
    public BigInteger getProposalCounter(){
        return proposalCounter.get();
    }

    @External(readonly=true)
    public Map<String, String> getProposal(BigInteger id){
        Proposal p = proposalDB.get(id);
        BigInteger[] voteStatus = getVotes(id);

        Map<String, String> proposalInfo = new HashMap<>();
        proposalInfo.put("startBlockHeight", p.startBlockHeight.toString());
        proposalInfo.put("startTimestamp", String.valueOf(p.startTimestamp));
        proposalInfo.put("status", String.valueOf(p.status));
        proposalInfo.put("disagreeVotes", voteStatus[0].toString());
        proposalInfo.put("agreeVotes", voteStatus[1].toString());
        proposalInfo.put("noVotes", voteStatus[2].toString());
        return proposalInfo;
    }

    @External(readonly=true)
    public Map<String,Map<String, String>> getProposalByUser(Address user){
        BigInteger proposalCounter = getProposalCounter();

        Map<String, Map<String, String>> proposals = new HashMap<>();
        for(int i = 0; i < proposalCounter.intValue(); i++){
            BigInteger proposalID = BigInteger.valueOf(i);
            Proposal p = proposalDB.get(proposalID);
            if(user.equals(p.proposer)){

                BigInteger[] voteStatus = getVotes(proposalID);
                Map<String, String> proposalInfo = new HashMap<>();
                proposalInfo.put("startBlockHeight", p.startBlockHeight.toString());
                proposalInfo.put("startTimestamp", String.valueOf(p.startTimestamp));
                proposalInfo.put("status", String.valueOf(p.status));
                proposalInfo.put("disagreeVotes", voteStatus[0].toString());
                proposalInfo.put("agreeVotes", voteStatus[1].toString());
                proposalInfo.put("noVotes", voteStatus[2].toString());
                proposals.put(proposalID.toString(), proposalInfo);
            }
        }
        return proposals;
    }
}
