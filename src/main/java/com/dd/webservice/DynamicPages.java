package com.dd.webservice;

import static spark.Spark.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.DataSources;
import com.dd.tools.Tools;

public class DynamicPages {

	static final Logger log = LoggerFactory.getLogger(API.class);

	public static void setup() {
		
		get("edit_poll/:pollId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("edit_poll"));
		});
	}
}
