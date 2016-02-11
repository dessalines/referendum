package com.referendum.voting.election;

import com.referendum.voting.voting_system.choice_type.SingleChoiceVotingSystem;

public interface FPTPElection extends SingleChoiceVotingSystem {

	@Override
	default SingleChoiceVotingSystemType getSingleChoiceVotingSystemType() {
		return SingleChoiceVotingSystemType.FPTP;
	}

	
}
