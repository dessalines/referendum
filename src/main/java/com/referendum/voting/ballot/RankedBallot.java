package com.referendum.voting.ballot;

import java.util.Collections;
import java.util.List;

import com.referendum.voting.candidate.RankedCandidate;
import com.referendum.voting.candidate.RankedCandidate.RankedCandidateComparator;

public class RankedBallot implements Ballot {

	private List<RankedCandidate> rankings;

	public RankedBallot(List<RankedCandidate> rankings) {
		this.rankings = rankings;
		Collections.sort(this.rankings, new RankedCandidateComparator());
	}

	public List<RankedCandidate> getRankings() {
		return rankings;
	}
	




}
