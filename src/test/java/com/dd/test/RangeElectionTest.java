package com.dd.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.tools.Tools;
import com.dd.voting.ballot.RangeBallot;
import com.dd.voting.candidate.RangeCandidate;
import com.dd.voting.election.RangeElection;
import com.dd.voting.voting_system.choice_type.RangeVotingSystem.RangeVotingSystemType;

public class RangeElectionTest extends TestCase {
	
	static final Logger log = LoggerFactory.getLogger(RangeElectionTest.class);

	
	Integer orange = 1, pear = 2, chocolate = 3, strawberry = 4, mixed = 5;

	private List<RangeBallot> setupBallots() {
		List<RangeBallot> ballots = new ArrayList<>();
		
		for (int i = 0; i < 4; i++) {
			RangeBallot b = new RangeBallot(new RangeCandidate(orange, Double.valueOf(i)));
			ballots.add(b);
		}
		
		for (int i = 0; i < 5; i++) {
			RangeBallot b = new RangeBallot(new RangeCandidate(pear, Double.valueOf(i)));
			ballots.add(b);
		}
		
		for (int i = 0; i < 2; i++) {
			RangeBallot b = new RangeBallot(new RangeCandidate(chocolate, Double.valueOf(i)));
			ballots.add(b);
		}
		
		for (int i = 10; i > 7; i--) {
			RangeBallot b  = new RangeBallot(new RangeCandidate(strawberry, Double.valueOf(i)));
			ballots.add(b);
		}
		for (int i = 10; i > 4; i--) {
			RangeBallot b = new RangeBallot(new RangeCandidate(mixed, Double.valueOf(i)));
			ballots.add(b);
		}
		for (int i = 0; i < 22; i++) {
			RangeBallot b = new RangeBallot(new RangeCandidate(orange, Double.valueOf(2)));
			ballots.add(b);
		}
		
		return ballots;
	}
	
	public void testRangeElection() {
		
		List<RangeBallot> ballots = setupBallots();
		
		RangeElection re = new RangeElection(1, RangeVotingSystemType.AVERAGE, ballots);
		
		log.info(Tools.GSON2.toJson(re.getRankings()));
		
	}
	
}
