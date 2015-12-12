package com.dd.voting.election;

import com.dd.voting.voting_system.choice_type.SingleChoiceVotingSystem;

public interface FPTPElection extends SingleChoiceVotingSystem {

	@Override
	default SingleChoiceVotingSystemType getSingleChoiceVotingSystemType() {
		return SingleChoiceVotingSystemType.FPTP;
	}

	
}
