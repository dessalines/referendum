package com.dd.test;

import static com.dd.db.Tables.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.db.Transformations;
import com.dd.db.Transformations.CommentObj;
import com.dd.tools.Tools;

import junit.framework.TestCase;

public class DerpTests extends TestCase {

	static final Logger log = LoggerFactory.getLogger(DerpTests.class);


	public void derp() {
		Tools.dbInit();
		String json = POLL_VIEW.findFirst("id = ?", 1).toJson(false).replace("\r", "").replace("\\\n", "\\u000a");
		System.out.println(json);

		//		System.out.println(StringEscapeUtils.escapeJava(json));

		Tools.dbClose();
	}

	public void testCommentView() {
		Tools.dbInit();


		//		String cv = COMMENT_VIEW.findAll().toJson(true);

		//		CommentView. = COMMENT_VIEW.findBySQL(COMMENT_VIEW_SQL(1,0,1)).toJson(true);

		List<CommentView> cvs = COMMENT_VIEW.find("discussion_id = ?", 1);

		List<CommentObj> cos = Transformations.convertCommentsToEmbeddedObjects(cvs);
		log.info(Tools.GSON2.toJson(cos));

		Tools.dbClose();
	}

	



}
