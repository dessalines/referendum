package com.referendum.webservice;

import static spark.Spark.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.referendum.DataSources;
import com.referendum.tools.Tools;

public class DynamicPages {

	static final Logger log = LoggerFactory.getLogger(API.class);

	public static void setup() {
		
		get("poll/:pollId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("poll"));
		});
		
		get("private_poll/:pollId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("private_poll"));
		});
		
		get("comment/:commentId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("comment"));
		});
		
		get("tag/:tagId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("tag"));
		});
		
		get("user/:userId", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("user"));
		});
		
		get("trending_tags", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("trending_tags"));
		});
		
		get("trending_polls", (req, res) -> {
			Tools.allowAllHeaders(req, res);
			Tools.set15MinuteCache(req, res);
			
			return Tools.readFile(DataSources.PAGES("trending_polls"));
		});
		
		
	}
}
