package com.referendum.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.referendum.DataSources;

import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;

public class Tools {

	static final Logger log = LoggerFactory.getLogger(Tools.class);

	public static final Kryo KRYO  = new KryoReflectionFactorySupport();

	public static final Gson GSON = new Gson();
	public static final Gson GSON2 = new GsonBuilder().setPrettyPrinting().create();

	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static final SimpleDateFormat RESPONSE_HEADER_DATE_FORMAT = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	
	public static final BaseX ALPHA_ID = new BaseX();
	
	private static final SecureRandom RANDOM = new SecureRandom();
	
	public static final BasicPasswordEncryptor PASS_ENCRYPT = new BasicPasswordEncryptor();

	public static void allowOnlyLocalHeaders(Request req, Response res) {


		log.debug("req ip = " + req.ip());


		//		res.header("Access-Control-Allow-Origin", "http://mozilla.com");
		//		res.header("Access-Control-Allow-Origin", "null");
		//		res.header("Access-Control-Allow-Origin", "*");
		//		res.header("Access-Control-Allow-Credentials", "true");


		if (!isLocalIP(req.ip())) {
			throw new NoSuchElementException("Not a local ip, can't access");
		}
	}

	public static Boolean isLocalIP(String ip) {
		Boolean isLocalIP = (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1"));
		return isLocalIP;
	}

	public static void allowAllHeaders(Request req, Response res) {
		String origin = req.headers("Origin");
		res.header("Access-Control-Allow-Credentials", "true");
		res.header("Access-Control-Allow-Origin", origin);


	}
	
	public static void set15MinuteCache(Request req, Response res) {
		res.header("Cache-Control", "private,max-age=300,s-maxage=900");
		res.header("Last-Modified", RESPONSE_HEADER_DATE_FORMAT.format(DataSources.APP_START_DATE));
	}



	public static void logRequestInfo(Request req) {
		String origin = req.headers("Origin");
		String origin2 = req.headers("origin");
		String host = req.headers("Host");


		log.debug("request host: " + host);
		log.debug("request origin: " + origin);
		log.debug("request origin2: " + origin2);


		//		System.out.println("origin = " + origin);
		//		if (DataSources.ALLOW_ACCESS_ADDRESSES.contains(req.headers("Origin"))) {
		//			res.header("Access-Control-Allow-Origin", origin);
		//		}
		for (String header : req.headers()) {
			log.debug("request header | " + header + " : " + req.headers(header));
		}
		log.debug("request ip = " + req.ip());
		log.debug("request pathInfo = " + req.pathInfo());
		log.debug("request host = " + req.host());
		log.debug("request url = " + req.url());
	}

	public static final Map<String, String> createMapFromAjaxPost(String reqBody) {
		log.debug(reqBody);
		Map<String, String> postMap = new HashMap<String, String>();
		String[] split = reqBody.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] keyValue = split[i].split("=");
			try {
				if (keyValue.length > 1) {
					postMap.put(URLDecoder.decode(keyValue[0], "UTF-8"),URLDecoder.decode(keyValue[1], "UTF-8"));
				}
			} catch (UnsupportedEncodingException |ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				throw new NoSuchElementException(e.getMessage());
			}
		}

		log.debug(GSON2.toJson(postMap));

		return postMap;

	}



	public static void setupDirectories() {
		if (!new File(DataSources.HOME_DIR()).exists()) {
			log.info("Setting up ~/." + DataSources.APP_NAME + " dirs");
			new File(DataSources.HOME_DIR()).mkdirs();
		} else {
			log.info("Home directory already exists");
		}
	}

	

	public static String encodeURL(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	public static void copyResourcesToHomeDir(Boolean copyAnyway) {


		String zipFile = null;

		if (copyAnyway || !new File(DataSources.SOURCE_CODE_HOME()).exists()) {
			log.info("Copying resources to  ~/." + DataSources.APP_NAME + " dirs");

			try {
				if (new File(DataSources.SHADED_JAR_FILE).exists()) {
					java.nio.file.Files.copy(Paths.get(DataSources.SHADED_JAR_FILE), Paths.get(DataSources.ZIP_FILE()), 
							StandardCopyOption.REPLACE_EXISTING);
					zipFile = DataSources.SHADED_JAR_FILE;

				} else if (new File(DataSources.SHADED_JAR_FILE_2).exists()) {
					java.nio.file.Files.copy(Paths.get(DataSources.SHADED_JAR_FILE_2), Paths.get(DataSources.ZIP_FILE()),
							StandardCopyOption.REPLACE_EXISTING);
					zipFile = DataSources.SHADED_JAR_FILE_2;
				} else {
					log.info("you need to build the project first");
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			Tools.unzip(new File(zipFile), new File(DataSources.SOURCE_CODE_HOME()));
			//		new Tools().copyJarResourcesRecursively("src", configHome);
			
//			WriteMultilingualHTMLFiles.write();
			
		} else {
			log.info("The source directory already exists");
		}
	}

	public static void unzip(File zipfile, File directory) {
		try {
			ZipFile zfile = new ZipFile(zipfile);
			Enumeration<? extends ZipEntry> entries = zfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(directory, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					InputStream in = zfile.getInputStream(entry);
					try {
						copy(in, file);
					} finally {
						in.close();
					}
				}
			}

			zfile.close();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	public static void runSQLFile(Connection c,File sqlFile) {

		try {
			Statement stmt = null;
			stmt = c.createStatement();
			String sql;

			sql = Files.toString(sqlFile, Charset.defaultCharset());
			log.info(sql);
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static final void dbInit() {
		
		Properties prop = DataSources.DB_PROP;
		
		try {
			new DB("default").open("com.mysql.jdbc.Driver", 
					prop.getProperty("dburl") + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull", 
					prop.getProperty("dbuser"), 
					prop.getProperty("dbpassword"));
		} catch (DBException e) {
			e.printStackTrace();
			dbClose();
			dbInit();
		}

	}

	public static final void dbClose() {
		new DB("default").close();
	}

	public static String extractInfoHashFromMagnetLink(String magnetLink) {
		// magnet:?xt=urn:btih:09c17295ccc24af400a2a91495af440b27766b5e&dn=Fugazi+-+Studio+Discography+1989-2001+%5BFLAC%5D

		return magnetLink.split("btih:")[1].split("&dn")[0].toLowerCase();

	}

	public static String extractNameFromMagnetLink(String magnetLink) {
		String encoded = magnetLink.split("&dn=")[1];
		String name = null;
		try {
			name = URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return name;
	}



	public static JsonNode jsonToNode(String json) {

		try {
			JsonNode root = MAPPER.readTree(json);
			return root;
		} catch (Exception e) {
			log.error("json: " + json);
			e.printStackTrace();
		}
		return null;
	}

	public static String nodeToJson(ObjectNode a) {
		try {
			return Tools.MAPPER.writeValueAsString(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String nodeToJsonPretty(JsonNode a) {
		try {
			return Tools.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void uninstall() {

		try {
			FileUtils.deleteDirectory(new File(DataSources.HOME_DIR()));
			log.info(DataSources.APP_NAME + " uninstalled successfully.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String convertWikimediaCommonsURLToImageUrl(String wmLink) {	

		String fileName = wmLink.split("File:")[1];

		String md5 = Hashing.md5().hashString(fileName, Charsets.UTF_8).toString();

		String weirdPathString = md5.substring(0, 1) + "/" + md5.substring(0, 2) + "/";
		String imageURL = "https://upload.wikimedia.org/wikipedia/commons/" + weirdPathString + 
				fileName;

		return imageURL;
	}

	public static String readFile(String path) {
		String s = null;

		byte[] encoded;
		try {
			encoded = java.nio.file.Files.readAllBytes(Paths.get(path));
			
			s = new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			throw new NoSuchElementException("Couldn't write result");
		}
		return s;
	}
	
	public static HttpServletResponse writeFileToResponse(File file, Response res) {
		return writeFileToResponse(file.getAbsolutePath(), res);
	}
	
	public static HttpServletResponse writeFileToResponse(String path, Response res) {

		byte[] encoded;
		try {
			encoded = java.nio.file.Files.readAllBytes(Paths.get(path));

			ServletOutputStream os = res.raw().getOutputStream();
			os.write(encoded);
			os.close();
			return res.raw();
			
		} catch (IOException e) {
			throw new NoSuchElementException("Couldn't write response from path: " + path);
		}
	}

	public static void addExternalWebServiceVarToTools(Boolean local) {

		log.info("tools.js = " + DataSources.TOOLS_JS());
		try {
			List<String> lines = java.nio.file.Files.readAllLines(Paths.get(DataSources.TOOLS_JS()));

			String interalServiceLine = "var localSparkService = '" + 
					DataSources.WEB_SERVICE_URL + "';";

			String ddServiceLine = "var ddSparkService ='" + 
					DataSources.DD_URL + "';";
			
			String externalServiceLine = "var externalSparkService ='" + 
					DataSources.EXTERNAL_URL + "';";

      String sparkServiceLine = (local) ? "var sparkService = '" +  DataSources.WEB_SERVICE_URL + "';" : 
        "var sparkService = '" +  DataSources.EXTERNAL_URL + "';";
			
      lines.set(0, interalServiceLine);
			lines.set(1, ddServiceLine);
			lines.set(2, externalServiceLine);
			lines.set(3, sparkServiceLine);

			java.nio.file.Files.write(Paths.get(DataSources.TOOLS_JS()), lines);
			Files.touch(new File(DataSources.TOOLS_JS()));


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static final String httpGetString(String url) {
		String res = "";
		try {
			URL externalURL = new URL(url);

			URLConnection yc = externalURL.openConnection();
			//			yc.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) 
				res+="\n" + inputLine;
			in.close();

			return res;
		} catch(IOException e) {}
		return res;
	}

	public static void setContentTypeFromFileName(String pageName, Response res) {
		
		if (pageName.endsWith(".css")) {
			res.type("text/css");
		} else if (pageName.endsWith(".js")) {
			res.type("application/javascript");
		} else if (pageName.endsWith(".png")) {
			res.type("image/png");
			res.header("Content-Disposition", "attachment;");
		} else if (pageName.endsWith(".svg")) {
			res.type("image/svg+xml");
		}
	}

	public static Properties loadProperties(String propertiesFileLocation) {

		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propertiesFileLocation);

			// load a properties file
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;

	}
	
	public static void setJsonContentType(Response res) {
		res.type("application/json; charset=utf-8");
	}
	
	public static String replaceNewlines(String text) {
		return text.replace("\r", "").replace("\n", "--lb--").replace("\\", "");
	}
	
	public static String generateSecureRandom() {
		return new BigInteger(256, RANDOM).toString(32);
	}
	
	public static Timestamp newExpireTimestamp() {
		return new Timestamp(new Date().getTime() + 1000 * DataSources.EXPIRE_SECONDS);
	}
	
	public static Timestamp newCurrentTimestamp() {
		return new Timestamp(new Date().getTime());
	}
	
	public static final String wrapPaginatorArray(String json, Long totalRecordCount) {
		String jtableJson = "{" + 
				"\"records\": " + json + "," + 
				"\"record_count\": " + totalRecordCount + 
				"}";
		return jtableJson;
	}

}
