package com.dd.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.referendum.db.Actions;
import com.referendum.db.Transformations;
import com.referendum.tools.Tools;
import com.referendum.voting.ballot.RangeBallot;
import com.referendum.voting.candidate.RangeCandidate;
import com.referendum.voting.election.RangeElection;
import com.referendum.voting.voting_system.choice_type.RangeVotingSystem.RangeVotingSystemType;

import static com.referendum.db.Tables.*;

public class RangeElectionTest2 extends TestCase {

	static final Logger log = LoggerFactory.getLogger(RangeElectionTest2.class);	
	
	public void testRangeElection() {
		
		Tools.dbInit();
	
//		String json = Actions.rangePollResults("1");
//		log.info(json);
		
		
		String json = Actions.rangePollResults("1");
		
		System.out.println(json);
		
		Tools.dbClose();
		
	}
}
