package com.dd.voting.ballot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dd.voting.candidate.RankedCandidate;

public class RankedBallot implements Ballot {

	private List<RankedCandidate> rankings;

	public RankedBallot(List<RankedCandidate> rankings) {
		this.rankings = rankings;
		Collections.sort(this.rankings, new RankedCandidateComparator());
	}

	public List<RankedCandidate> getRankings() {
		return rankings;
	}
	
	public class RankedCandidateComparator implements Comparator<RankedCandidate> {

		@Override
		public int compare(RankedCandidate o1, RankedCandidate o2) {
			return o1.getRank().compareTo(o2.getRank());
		}

	}



}
