package com.dd;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.tools.Tools;
import com.dd.voting.ballot.RankedBallot;
import com.dd.voting.candidate.RankedCandidate;
import com.dd.voting.election.ElectionRoundItem;
import com.dd.voting.election.STVElection;
import com.dd.voting.election.STVElection.Quota;

import junit.framework.TestCase;

public class STVTest2 extends TestCase {

	static final Logger log = LoggerFactory.getLogger(STVTest2.class);

	Integer andrea = 1, brad = 2, carter = 3, delilah = 4;
	
	private List<RankedBallot> setupBallots() {

		List<RankedBallot> ballots = new ArrayList<>();
		
		for (int i = 0; i < 3; i++) {
			List<RankedCandidate> candidates = new ArrayList<>();
			
			candidates.add(new RankedCandidate(andrea, 1));
			candidates.add(new RankedCandidate(brad, 2));
			candidates.add(new RankedCandidate(carter, 3));
			candidates.add(new RankedCandidate(delilah, 4));
			
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}
		
		for (int i = 0; i < 8; i++) {
			List<RankedCandidate> candidates = new ArrayList<>();
			
			candidates.add(new RankedCandidate(andrea, 1));
			candidates.add(new RankedCandidate(carter, 2));
			candidates.add(new RankedCandidate(brad, 3));
			candidates.add(new RankedCandidate(delilah, 4));
			
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}
		
		for (int i = 0; i < 2; i++) {
			List<RankedCandidate> candidates = new ArrayList<>();
			
			candidates.add(new RankedCandidate(andrea, 1));
			candidates.add(new RankedCandidate(brad, 2));
			candidates.add(new RankedCandidate(carter, 3));
			candidates.add(new RankedCandidate(delilah, 4));
			
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}
		
		for (int i = 0; i < 9; i++) {
			List<RankedCandidate> candidates = new ArrayList<>();
			
			candidates.add(new RankedCandidate(andrea, 1));
			candidates.add(new RankedCandidate(carter, 2));
			candidates.add(new RankedCandidate(brad, 3));
			candidates.add(new RankedCandidate(delilah, 4));
			
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}
		
		for (int i = 0; i < 8; i++) {
			List<RankedCandidate> candidates = new ArrayList<>();
			
			candidates.add(new RankedCandidate(delilah, 1));
			
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}
		
		
		return ballots;
		
	}
	
public void testSTVElection() {
		
		Integer seats = 2;
		
		List<RankedBallot> ballots = setupBallots();
		
		STVElection stv = new STVElection(Quota.DROOP, ballots, seats);
		
		log.info(Tools.GSON2.toJson(stv.getRounds()));
		
		Integer rn = 0, c;
		
		// first round
		c = 0;
		assertEquals(Integer.valueOf(22), stv.getRounds().get(rn).getRoundItems().get(c++).getVotes());
		assertEquals(Integer.valueOf(8), stv.getRounds().get(rn).getRoundItems().get(3).getVotes());

		assertEquals(ElectionRoundItem.Status.ELECTED, stv.getRounds().get(rn++).getRoundItems().get(0).getStatus());
		
		
		// second round
		c = 0;
		assertEquals(Integer.valueOf(3), stv.getRounds().get(rn).getRoundItems().get(c++).getVotes());
		assertEquals(Integer.valueOf(8), stv.getRounds().get(rn).getRoundItems().get(c++).getVotes());
		assertEquals(Integer.valueOf(8), stv.getRounds().get(rn).getRoundItems().get(c++).getVotes());

		assertEquals(ElectionRoundItem.Status.DEFEATED, stv.getRounds().get(rn++).getRoundItems().get(0).getStatus());

		// third round
		c = 0;
		assertEquals(Integer.valueOf(11), stv.getRounds().get(rn).getRoundItems().get(c++).getVotes());
		assertEquals(Integer.valueOf(8), stv.getRounds().get(rn).getRoundItems().get(c++).getVotes());

		assertEquals(ElectionRoundItem.Status.ELECTED, stv.getRounds().get(rn).getRoundItems().get(0).getStatus());
		assertEquals(ElectionRoundItem.Status.DEFEATED, stv.getRounds().get(rn++).getRoundItems().get(1).getStatus());

		
	}

}
