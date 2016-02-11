package com.referendum.voting.ballot;

import com.referendum.voting.candidate.RangeCandidate;

public class RangeBallot implements Ballot {
	private RangeCandidate candidate;

	public RangeBallot(RangeCandidate candidate) {
		this.candidate = candidate;
	}

	public RangeCandidate getCandidate() {
		return candidate;
	}
}
