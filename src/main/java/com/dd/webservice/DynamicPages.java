package com.dd.webservice;

import static spark.Spark.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.DataSources;
import com.dd.tools.Tools;

public class DynamicPages {

	static final Logger log = LoggerFactory.getLogger(API.class);

	public static void setup() {
		
		get("poll/:pollId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			
			return Tools.readFile(DataSources.PAGES("poll"));
		});
		
		get("private_poll/:pollId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			
			return Tools.readFile(DataSources.PAGES("private_poll"));
		});
		
		get("comment/:commentId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			
			return Tools.readFile(DataSources.PAGES("comment"));
		});
	}
}
