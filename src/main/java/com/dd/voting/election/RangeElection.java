package com.dd.voting.election;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.voting.ballot.RangeBallot;
import com.dd.voting.candidate.RangeCandidate;
import com.dd.voting.results.RangeElectionResults;
import com.dd.voting.voting_system.choice_type.RangeVotingSystem;

public class RangeElection implements RangeVotingSystem, RangeElectionResults {

	static final Logger log = LoggerFactory.getLogger(RangeElection.class);

	public RangeVotingSystemType type;
	public List<RangeBallot> ballots;
	public List<RangeCandidate> rankings;
	
	public RangeElection(RangeVotingSystemType type, List<RangeBallot> ballots) {
		this.type = type;
		this.ballots = ballots;

		runElection();
	}




	@Override
	public void runElection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RangeCandidate> getRankings() {
		return rankings;
	}
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RangeVotingSystemType getRangeVotingSystemType() {
		return type;
	}

	@Override
	public ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}


}
