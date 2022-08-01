package com.iconloop.score.token;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import com.iconloop.score.token.alice.Alice;
import com.iconloop.score.token.alice.Checkpoint;
import com.iconloop.score.token.alice.Proposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import score.Address;
import score.Context;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AliceTest extends TestBase {
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();

    private static final String _name = "MyIRC3Token";
    private static final Address _signerAddress = Address.fromString("hx1f199d7b495ebb2ce3cf704a5dc3c2ac584b6cd3");
    private Score tokenScore;

    @BeforeEach
    public void setup() throws Exception {
        tokenScore = sm.deploy(owner, Alice.class,_name, _signerAddress);
    }

//    @Test
//    public void testt(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "google.com");
//        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "google.com");
//        Checkpoint cp = (Checkpoint) tokenScore.call("getCheckpointAtBlock", user1.getAddress(), BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(user1.getAddress(), cp.delegate);
//        assertEquals(BigInteger.ONE, cp.votingPower);
//        assertEquals(0, cp.delegatedBy.size());
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(2), BigInteger.ONE, "google.com");
//        cp = (Checkpoint) tokenScore.call("getCheckpointAtBlock", user1.getAddress(), BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(user1.getAddress(), cp.delegate);
//        assertEquals(BigInteger.TWO, cp.votingPower);
//        assertEquals(0, cp.delegatedBy.size());
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress());
//        cp = (Checkpoint) tokenScore.call("getCheckpointAtBlock", user1.getAddress(), BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(user2.getAddress(), cp.delegate);
//        assertEquals(BigInteger.TWO, cp.votingPower);
//        assertEquals(0, cp.delegatedBy.size());
//        cp = (Checkpoint) tokenScore.call("getCheckpointAtBlock", user2.getAddress(), BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(user2.getAddress(), cp.delegate);
//        assertEquals(BigInteger.valueOf(1), cp.votingPower);
//        assertEquals(1, cp.delegatedBy.size());
//    }

//    @Test
//    public void testt1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "google.com");
//        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "google.com");
//        tokenScore.invoke(user1, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(BigInteger.valueOf(2), tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        tokenScore.invoke(user1, "transferFrom", user1.getAddress(), user2.getAddress(), BigInteger.ZERO, BigInteger.ONE, "".getBytes());
////        assertEquals(BigInteger.valueOf(2), tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        Set<Address> voters = (Set<Address>) tokenScore.call("getVoteList", BigInteger.ZERO, "nv");
//        assertEquals(1, voters.size());
//    }

//    @Test
//    public void proposal_tc1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "google.com");
//        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "google.com");
//        tokenScore.invoke(user1, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(BigInteger.valueOf(2), tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        tokenScore.invoke(user1, "voteProposal", BigInteger.valueOf(0), 1);
//        assertEquals(BigInteger.ONE, tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        assertEquals(BigInteger.ONE, tokenScore.call("getVoteAmount", BigInteger.ZERO, "a"));
//        assertEquals(BigInteger.ZERO, tokenScore.call("getVoteAmount", BigInteger.ZERO, "d"));
//        tokenScore.invoke(user2, "setDelegation", user1.getAddress());
//        assertEquals(BigInteger.ONE, tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//    }
//
//    @Test
//    void trySwapVote(){
//        Account user1 = sm.createAccount();
//        user1.addBalance("ICX", BigInteger.valueOf(10000));
//        tokenScore.invoke(owner, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(owner, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(owner, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        tokenScore.invoke(owner, "voteProposal", BigInteger.ZERO, 1);
//        assertEquals(1, tokenScore.call( "checkVoteStatus", BigInteger.ZERO, owner.getAddress()));
//        tokenScore.invoke(owner, "swapVote", BigInteger.ZERO);
//        assertEquals(0, tokenScore.call( "checkVoteStatus", BigInteger.ZERO, owner.getAddress()));
//        tokenScore.invoke(owner, "swapVote", BigInteger.ZERO);
//        assertEquals(1, tokenScore.call( "checkVoteStatus", BigInteger.ZERO, owner.getAddress()));
//    }
//
    @Test
    void delegation_tc1(){
        // 1 => 2
        // 1 => 1
        Account user1 = sm.createAccount();
        Account user2 = sm.createAccount();
        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
        tokenScore.invoke(user1, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
        tokenScore.invoke(user2, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
        assertEquals(user1.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));

        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
        assertEquals(user2.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));
        tokenScore.invoke(user1, "setDelegation", user1.getAddress()); // 1 => 1
        assertEquals(user1.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));

    }
//
    @Test
    void delegation_tc2(){
        // 1 => 2
        // 2 => 3
        // 4 => 2
        Account user1 = sm.createAccount();
        Account user2 = sm.createAccount();
        Account user3 = sm.createAccount();
        Account user4 = sm.createAccount();
        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
        tokenScore.invoke(user2, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
        tokenScore.invoke(user3, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
        tokenScore.invoke(user4, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");

        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
        assertEquals(user2.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));
        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
        assertEquals(user2.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));
        assertEquals(user3.getAddress(), tokenScore.call("getDelegate", user2.getAddress()));
        tokenScore.invoke(user4, "setDelegation", user2.getAddress()); // 4 => 2 => error
        assertEquals(user2.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));
        assertEquals(user3.getAddress(), tokenScore.call("getDelegate", user2.getAddress()));
        assertEquals(user2.getAddress(), tokenScore.call("getDelegate", user4.getAddress()));
    }

    @Test
    void delegation_tc3(){
        // 1 => 2
        // 1 => 3
        Account user1 = sm.createAccount();
        Account user2 = sm.createAccount();
        Account user3 = sm.createAccount();
        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
        tokenScore.invoke(user3, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
        assertEquals(user1.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));

        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
        assertEquals(user2.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));
        tokenScore.invoke(user1, "setDelegation", user3.getAddress()); // 1 => 3
        assertEquals(user3.getAddress(), tokenScore.call("getDelegate", user1.getAddress()));

        Address[] delegatedBy = (Address[]) tokenScore.call("getDelegatedBy", user2.getAddress());
        assertEquals(0, delegatedBy.length);
        delegatedBy = (Address[]) tokenScore.call("getDelegatedBy", user3.getAddress());
        assertEquals(1, delegatedBy.length);
    }


//@Test
//void delegation_tc4(){
//        // to check cuz error delegation_tc4, delegation_tc5 -> expect AssertionError but got UserRevertedException
//    //loop test
//    Account user1 = sm.createAccount();
//    Account user2 = sm.createAccount();
//    Account user3 = sm.createAccount();
//    Account user4 = sm.createAccount();
//    tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//    tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//    tokenScore.invoke(user3, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
//    tokenScore.invoke(user4, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//    tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//    tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//    assertEquals(user3.getAddress(), tokenScore.call("getDelegate", user3.getAddress()));
//    AssertionError exception = assertThrows(AssertionError.class, () -> {
//        tokenScore.invoke(user3, "setDelegation", user1.getAddress()); // 3 => 1
//    });
//    String expectedMessage = "delegate not allowed, please select another delegate ";
//    String actualMessage = exception.getMessage();
//    assertTrue(actualMessage.contains(expectedMessage));
//}
//    @Test
//    void delegation_tc5(){
//        //loop test
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user3, "setDelegation", user4.getAddress()); // 3 => 4
//
//        AssertionError exception = assertThrows(AssertionError.class, () -> {
//            tokenScore.invoke(user4, "setDelegation", user2.getAddress()); // 4 => 2
//        });
//        String expectedMessage = "delegate not allowed, please select another delegate ";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//    }

//    @Test
//    void createProposal(){
//        Account user1 = sm.createAccount();
//        user1.addBalance("ICX", BigInteger.valueOf(10000));
//        tokenScore.invoke(owner, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(owner, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(owner, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        assertEquals(BigInteger.valueOf(3),tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        tokenScore.invoke(owner, "voteProposal", BigInteger.ZERO, 1);
//        assertEquals(BigInteger.valueOf(2),tokenScore.call("getVoteAmount", BigInteger.ZERO, "a"));
//        assertEquals(BigInteger.valueOf(1),tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        tokenScore.invoke(user1, "voteProposal", BigInteger.ZERO, 0);
//        assertEquals(BigInteger.valueOf(0),tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        assertEquals(BigInteger.valueOf(2),tokenScore.call("getVoteAmount", BigInteger.ZERO, "a"));
//        assertEquals(BigInteger.valueOf(1),tokenScore.call("getVoteAmount", BigInteger.ZERO, "d"));
//    }
//
//    @Test
//    void tally_tc1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user3, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user3, "voteProposal", BigInteger.ZERO, 0);
//
//        BigInteger[] votes = (BigInteger[]) tokenScore.call("getVotes", BigInteger.ZERO);
//        assertEquals(BigInteger.valueOf(3), votes[0]);
//        assertEquals(BigInteger.valueOf(0), votes[1]);
//        assertEquals(BigInteger.valueOf(0), votes[2]);
//    }
//
//    @Test
//    void tally_tc2(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.TWO, BigInteger.ONE, "test.com"); // user 1 = 3 voting power
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(4), BigInteger.ONE, "test.com"); // user 2 = 2 voting power
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(5), BigInteger.ONE, "test.com"); // user 3 = 1 voting power
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(6), BigInteger.ONE, "test.com"); // user 4 = 1 voting power
//
//        tokenScore.invoke(user3, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user3, "voteProposal", BigInteger.ZERO, 0);
//        tokenScore.invoke(user4, "voteProposal", BigInteger.ZERO, 1);
//
//        BigInteger[] votes = (BigInteger[]) tokenScore.call("getVotes", BigInteger.ZERO);
//        assertEquals(BigInteger.valueOf(6), votes[0]);
//        assertEquals(BigInteger.valueOf(1), votes[1]);
//        assertEquals(BigInteger.valueOf(0), votes[2]);
//    }
//
//    @Test
//    void tally_tc3(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(4), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(5), BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user3, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user4, "setDelegation", user2.getAddress()); // 4 => 2
//
//        tokenScore.invoke(user3, "voteProposal", BigInteger.ZERO, 0);
//        BigInteger[] votes = (BigInteger[]) tokenScore.call("getVotes", BigInteger.ZERO);
//        assertEquals(BigInteger.valueOf(6), votes[0]);
//        assertEquals(BigInteger.valueOf(0), votes[1]);
//        assertEquals(BigInteger.valueOf(0), votes[2]);
//    }
//
//    @Test
//    void tally_tc4(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(4), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(5), BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user3, "createProposal", BigInteger.valueOf(Context.getBlockHeight()));
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user4, "setDelegation", user2.getAddress()); // 4 => 2
//
//        BigInteger[] votes = (BigInteger[]) tokenScore.call("getVotes", BigInteger.ZERO);
//        assertEquals(BigInteger.valueOf(0), votes[0]);
//        assertEquals(BigInteger.valueOf(0), votes[1]);
//        assertEquals(BigInteger.valueOf(6), votes[2]);
//    }
//    @Test
//    public void delegationCheckpoint_tc1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        assertEquals(BigInteger.valueOf(3), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user2.getAddress()));
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress());
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(4), tokenScore.call("getDelegationPower", user2.getAddress()));
//        tokenScore.invoke(user1, "transferFrom", user1.getAddress(), user2.getAddress(), BigInteger.valueOf(0), BigInteger.valueOf(1), "".getBytes());
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(4), tokenScore.call("getDelegationPower", user2.getAddress()));
//        tokenScore.invoke(user1, "transferFrom", user1.getAddress(), user3.getAddress(), BigInteger.valueOf(2), BigInteger.valueOf(1), "".getBytes());
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(3), tokenScore.call("getDelegationPower", user2.getAddress()));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user3.getAddress()));
//    }
//
//    @Test
//    public void delegationCheckpoint_tc2(){
//        // 1 => 2
//        // 2 => 3
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user2.getAddress()));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user3.getAddress()));
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress());
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(2), tokenScore.call("getDelegationPower", user2.getAddress()));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user3.getAddress()));
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress());
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getDelegationPower", user1.getAddress()));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getDelegationPower", user2.getAddress()));
//        assertEquals(BigInteger.valueOf(2), tokenScore.call("getDelegationPower", user3.getAddress()));
//    }
//
//    @Test
//    public void transfers_tc1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user1, "createProposal");
//        tokenScore.invoke(user1, "voteProposal", BigInteger.ZERO, 1);
//        assertEquals(1, tokenScore.call("getProposalVote", BigInteger.ZERO, user1.getAddress()));
//        tokenScore.invoke(user2, "transferFrom", user2.getAddress(), user1.getAddress(), BigInteger.valueOf(3),BigInteger.ONE,"".getBytes());
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        assertEquals(BigInteger.valueOf(3), tokenScore.call("getVoteAmount", BigInteger.ZERO, "a"));
//        tokenScore.invoke(user2, "transferFrom", user2.getAddress(), user1.getAddress(), BigInteger.valueOf(4),BigInteger.ONE,"".getBytes());
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        assertEquals(BigInteger.valueOf(4), tokenScore.call("getVoteAmount", BigInteger.ZERO, "a"));
//        tokenScore.invoke(user1, "transferFrom", user1.getAddress(), user2.getAddress(), BigInteger.valueOf(4),BigInteger.ONE,"".getBytes());
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getVoteAmount", BigInteger.ZERO, "nv"));
//        assertEquals(BigInteger.valueOf(3), tokenScore.call("getVoteAmount", BigInteger.ZERO, "a"));
//    }
//
//    @Test
//    public void balance_at_block_tc1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        BigInteger blockheight1 = BigInteger.valueOf(sm.getBlock().getHeight());
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        BigInteger blockheight2 = BigInteger.valueOf(sm.getBlock().getHeight());
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        BigInteger blockheight3 = BigInteger.valueOf(sm.getBlock().getHeight());
//        assertEquals(BigInteger.valueOf(0),tokenScore.call("getBalanceAtBlock", user1.getAddress(), blockheight1));
//        assertEquals(BigInteger.valueOf(1),tokenScore.call("getBalanceAtBlock", user1.getAddress(), blockheight2));
//        assertEquals(BigInteger.valueOf(2),tokenScore.call("getBalanceAtBlock", user1.getAddress(), blockheight3));
//        tokenScore.invoke(user1, "transferFrom", user1.getAddress(), user2.getAddress(), BigInteger.ZERO, BigInteger.ONE, "hellog".getBytes());
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress());
//        BigInteger blockheight4 = BigInteger.valueOf(sm.getBlock().getHeight());
//        assertEquals(BigInteger.valueOf(0),tokenScore.call("getBalanceAtBlock", user1.getAddress(), blockheight4));
//        assertEquals(BigInteger.valueOf(2),tokenScore.call("getBalanceAtBlock", user2.getAddress(), blockheight4));
//    }
//
//    @Test
//    public void tally_updated_tc1(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.TWO, BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user3, "createProposal");
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user3, "voteProposal", BigInteger.ZERO, 0);
//
//        assertEquals(BigInteger.valueOf(3), tokenScore.call("getProposalVotes", BigInteger.ZERO, 0));
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 1));
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 2));
//    }
//
//    @Test
//    void tally_updated_tc2(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.ZERO, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.ONE, BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.TWO, BigInteger.ONE, "test.com"); // user 1 = 3 voting power
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(4), BigInteger.ONE, "test.com"); // user 2 = 2 voting power
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(5), BigInteger.ONE, "test.com"); // user 3 = 1 voting power
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(6), BigInteger.ONE, "test.com"); // user 4 = 1 voting power
//
//        tokenScore.invoke(user3, "createProposal");
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user3, "voteProposal", BigInteger.ZERO, 0);
//        tokenScore.invoke(user4, "voteProposal", BigInteger.ZERO, 1);
//
//        assertEquals(BigInteger.valueOf(6), tokenScore.call("getProposalVotes", BigInteger.ZERO, 0));
//        assertEquals(BigInteger.valueOf(1), tokenScore.call("getProposalVotes", BigInteger.ZERO, 1));
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 2));
//    }
//
//    @Test
//    void tally_updated_tc3(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(4), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(5), BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user3, "createProposal");
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user4, "setDelegation", user2.getAddress()); // 4 => 2
//
//        tokenScore.invoke(user3, "voteProposal", BigInteger.ZERO, 0);
//        assertEquals(BigInteger.valueOf(6), tokenScore.call("getProposalVotes", BigInteger.ZERO, 0));
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 1));
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 2));
//    }
//
//    @Test
//    void tally_updated_tc4(){
//        Account user1 = sm.createAccount();
//        Account user2 = sm.createAccount();
//        Account user3 = sm.createAccount();
//        Account user4 = sm.createAccount();
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(0), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user1, "mint", BigInteger.valueOf(1), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(2), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user2, "mint", BigInteger.valueOf(3), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user3, "mint", BigInteger.valueOf(4), BigInteger.ONE, "test.com");
//        tokenScore.invoke(user4, "mint", BigInteger.valueOf(5), BigInteger.ONE, "test.com");
//
//        tokenScore.invoke(user3, "createProposal");
//        tokenScore.invoke(user1, "setDelegation", user2.getAddress()); // 1 => 2
//        tokenScore.invoke(user2, "setDelegation", user3.getAddress()); // 2 => 3
//        tokenScore.invoke(user4, "setDelegation", user2.getAddress()); // 4 => 2
//
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 0));
//        assertEquals(BigInteger.valueOf(0), tokenScore.call("getProposalVotes", BigInteger.ZERO, 1));
//        assertEquals(BigInteger.valueOf(6), tokenScore.call("getProposalVotes", BigInteger.ZERO, 2));
//    }

    @Test
    void project_info_tc1(){
        assertEquals("MyIRC3Token", tokenScore.call("getProjectInfo", "name"));
        tokenScore.invoke(owner, "updateProjectInfo", "hey", "", "", "", BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
        assertEquals(1,1);
        assertEquals("hey", tokenScore.call("getProjectInfo", "name"));
        assertEquals("", tokenScore.call("getProjectInfo", "description"));
        assertEquals("", tokenScore.call("getProjectInfo", "details"));
        assertEquals(BigInteger.ZERO.toString(), tokenScore.call("getProjectInfo", "fundingGoal"));
        tokenScore.invoke(owner, "updateProjectInfo", "hey", "", "", "", BigInteger.ONE , BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
        assertEquals(BigInteger.ONE.toString(), tokenScore.call("getProjectInfo", "fundingGoal"));
    }

    @Test
    void mint_tc1(){
        BigInteger EXA = BigInteger.valueOf(1_000_000_000_000_000_000L);
        assertEquals("MyIRC3Token", tokenScore.call("getProjectInfo", "name"));
        tokenScore.invoke(owner, "updateProjectInfo", "hey", "", "", "", BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO);
//        sm.call(BigInteger.valueOf(10), owner.getAddress(), "test1", BigInteger.valueOf(10));
//        Context.call(BigInteger.valueOf(10), tokenScore.getAddress(), "test1", BigInteger.valueOf(10));
        sm.call(owner, BigInteger.valueOf(10).multiply(EXA), tokenScore.getAddress(), "test1", BigInteger.valueOf(10));
    }

    @Test
    void testest(){
        BigInteger qty = BigInteger.ONE;
        assertEquals(true, qty.compareTo(BigInteger.ONE) >= 0);
    }
}
