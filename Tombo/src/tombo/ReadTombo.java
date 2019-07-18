package tombo;

import java.io.
BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



import javax.net.ssl.HttpsURLConnection;

public class ReadTombo {
	
	String lastPrinted = null;
	
	static boolean RUNNING_BAPTISM = false;

	public static void main(String[] args) {
		   
		ReadTombo readTombo = new ReadTombo();
//		String[] urls = new String[] {"https://tombo.pt/d/aveiro"
//		           , "https://tombo.pt/d/beja"
//				   , "https://tombo.pt/d/braga"
//				   , "https://tombo.pt/d/braganca"
//				   , "https://tombo.pt/d/castelo-branco"
//				   , "https://tombo.pt/d/coimbra"
//				   , "https://tombo.pt/d/evora"
//				   , "https://tombo.pt/d/faro"
//				   , "https://tombo.pt/d/guarda"
//				   , "https://tombo.pt/d/leiria"
//				   , "https://tombo.pt/d/lisboa"
//				   , "https://tombo.pt/d/portalegre"
//				   , "https://tombo.pt/d/porto"
//				   , "https://tombo.pt/d/santarem"
//				   , "https://tombo.pt/d/setubal"
//				   , "https://tombo.pt/d/viana-do-castelo"
//				   , "https://tombo.pt/d/vila-real"
//				   , "https://tombo.pt/d/viseu"
//				   , "https://tombo.pt/d/acores"
//				   , "https://tombo.pt/d/madeira"};
//		String[] urls = new String[] {"https://tombo.pt/d/porto"};
		String[] urls = new String[] {"https://tombo.pt/d/viana-do-castelo"};
		   
		for (String url:urls) {
			readTombo.readPage(url, "MunicÃ­pios", null);
		}
	}
		
	private Boolean readPage(String urlStr, String sectioStartStr, String parentLocation){
		
		System.setProperty("http.agent", "Chrome");
		String https_url = urlStr;
		URL url;
		try {
			url = new URL(https_url);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			return printContent(con, sectioStartStr, parentLocation);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		
		
	private Boolean printContent(HttpsURLConnection con, String sectioStartStr, String parentLocation){
		Boolean returnValue = null;
		if (con!=null){
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String input;
				boolean found = false;
				String thisUrl = null;
				while ((input = br.readLine()) != null) {
					if (sectioStartStr != null) {
						if (input.contains(sectioStartStr) && input.contains("::")) {
							found = true;
							thisUrl = input.split("::")[1].replaceAll("\">", "");
						} else if (found) {
							if (input.contains("value")) {
								String thisLocation = input.split("<")[0].trim();
								if (!sectioStartStr.equals("Freguesias")) {
									String thisLocationStr = con.getURL().getPath().split("/")[2];
									readPage(thisUrl, "Freguesias", thisLocationStr + ";" + thisLocation);
								} else {
									lastPrinted = parentLocation + ";" + thisLocation + ";" + thisUrl;
									if (lastPrinted.contains("Cedofeita")) {
										readPage(thisUrl, null, null);
									}
									if (readPage(thisUrl, null, null) == null) {
										System.out.println(lastPrinted);	
									}
								}
								if (input.contains("::")) {
									thisUrl = input.split("::")[1].replaceAll("\">", "");
								} else {
									found = false;
								}
							}
						}
						if (input.contains("</section>")) {
							found = false;
						}
					} else {
//						if (lastPrinted.contains("https://tombo.pt/f/bja17") && input.contains("Batismos")) {
//							System.out.println("==> " + input);
//						}
//						String baptismStr = "Batismos</a></td><td>1900 ";
						String outStr;
						if (RUNNING_BAPTISM) {
							outStr = isBaptimsLine(input);
						} else {
							outStr = isWeddingLine(input);
						}
						if (outStr != null) {
						//if (isBaptimsLine(input)) {
//							String outStr = input.substring(input.indexOf(baptismStr) + baptismStr.length(), input.length());
//							outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
//							outStr = outStr.substring(0, outStr.indexOf(" "));
							System.out.println(lastPrinted + ";" + outStr.replaceAll("\"", ""));
							returnValue = true;
						}
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		
		return returnValue;
			
	}
    
	private String isBaptimsLine(String thisLine) {
//		String[] baptismStr = new String[] {"atismos</a></td><td>", "aptismos</a></td><td>", "atismo</a></td><td>", "aptismo</a></td><td>"};
//		if (!thisLine.contains(baptismStr[0]) && !thisLine.contains(baptismStr[1]) && !thisLine.contains(baptismStr[2]) && !thisLine.contains(baptismStr[3])) {
//		   return null;
//		}
//		if (thisLine.contains(baptismStr[2]) || thisLine.contains(baptismStr[3])) {
//			System.out.println("AAAA");
//		}
//		for (String thisBaptismStr:baptismStr) {
//			if (thisLine.contains(thisBaptismStr + "1900")) {
//				String outStr = thisLine.substring(thisLine.indexOf(thisBaptismStr + "1900") + thisBaptismStr.length(), thisLine.length());
//				outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
//				outStr = outStr.substring(0, outStr.indexOf(" "));
//				//return outStr;
//			}
//		}
		
		String regex = "(tismo(s|)<.a><.td><td>(1900|[1][8][0-9][0-9]).{2,16}[1][9][0-4][0-9].*href.*>)";
		Pattern pattern0 = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher0 = pattern0.matcher(thisLine);
		if(matcher0.find()) {
			String outStr = matcher0.group(1);
			outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
			outStr = outStr.substring(1, outStr.indexOf(" ")-1);
			return outStr;
		}
		return null;
	}
	
	private String isWeddingLine(String thisLine) {
		
		String regex = "(asamento(s|)<.a><.td><td>(1900|[1][8][0-9][0-9]).{2,16}(19[0-2][0-9]|[1][8][9][5-9]).*href.*)";
		Pattern pattern0 = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher0 = pattern0.matcher(thisLine);
		if(matcher0.find()) {
			String outStr = matcher0.group(1);
			outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
			outStr = outStr.substring(1, outStr.indexOf(" ")-1);
			return outStr;
		}
		return null;
	}
	

	private String isBaptimsLine_old(String thisLine) {
		String[] baptismStr = new String[] {"Batismos</a></td><td>", "Baptismos</a></td><td>"};
		if (!thisLine.contains(baptismStr[0]) && !thisLine.contains(baptismStr[1])) {
		   return null;
		}
		for (String thisBaptismStr:baptismStr) {
			if (thisLine.contains(baptismStr + "1900")) {
				String outStr = thisLine.substring(thisLine.indexOf(thisBaptismStr) + thisBaptismStr.length(), thisLine.length());
				outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
				outStr = outStr.substring(0, outStr.indexOf(" "));
				return outStr;
			}
		}
		
		//String linesNoSpaces = thisLine.replaceAll(" – ", "-");
		
//		if (thisLine.contains("1899-01-01")) {
//			System.out.println(thisLine);
//			String regex = "(.*)[1][8][7-9][0-9]-[0-9][0-9]-[0-9][0-9](.*.)[1][9][0-1][0-9]-[0-9][0-9]-[0-9][0-9](.*)";
//			if (thisLine.matches(regex)) {
//				System.out.println("SIM");
//				Pattern pattern = Pattern.compile(regex);
//				Matcher matcher = pattern.matcher(thisLine);
//				if(matcher.matches()) {
//		            String outStr = matcher.group(2);
//		            outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
//					outStr = outStr.substring(0, outStr.indexOf(" "));
//		        }
//			}
//		}
		
		//System.out.println(linesNoSpaces);
		String regex = "(.*)[1][8][0-9][0-9]-[0-9][0-9]-[0-9][0-9](.*.)[1][9][0-4][0-9]-[0-9][0-9]-[0-9][0-9](.*)";
		String regex2 = "(.*)[1][8][0-9][0-9](.*.)[1][9][0-4][0-9](.*)";
//		String testDate = ">Batismos</a></td><td>1871-01-01–1906-04-06</td><td><a href";
//		if (testDate.matches(regex)) {
//			System.out.println("SIM");
//		}
		String regexToUse = null;
		if (thisLine.matches(regex)) {
			regexToUse = regex;
		} else if (thisLine.matches(regex2)) {
			regexToUse = regex2;
		}
		if (regexToUse != null 
				&& thisLine.matches(regexToUse)) {
			Pattern pattern = Pattern.compile(regexToUse);
			Matcher matcher = pattern.matcher(thisLine);
			if(matcher.matches()) {
	            String outStr;
	            if (regexToUse.equals(regex)) {
	            	outStr = matcher.group(2);
	            } else {
	            	outStr = matcher.group(3);
	            }
	            for (int i=0;i<matcher.groupCount();i++) {
	            	if (matcher.group(i).matches(regexToUse)) {
	            		outStr = matcher.group(i);
	            		break;
	            	}
	            }
	            String temp = "";
	            if (matcher.find()) {
	            	temp = temp + matcher.group();
	                System.out.println(temp);
	            }
	            outStr = outStr.substring(outStr.indexOf("=") + 1, outStr.length());
	            try {
	            	outStr = outStr.substring(0, outStr.indexOf(" "));
	            } catch (Exception e) {
	            	outStr = outStr.substring(0, outStr.indexOf(" "));
				}
				return outStr;
	        }
		}
		return null;
	}
        
}
