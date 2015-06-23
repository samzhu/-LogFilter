package com.common.log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.common.log.AccessConstants.TEE_FILTER_ACTIVE_PARAM;
import static com.common.log.AccessConstants.TEE_FILTER_EXCLUDES_PARAM;

@WebFilter(description = "logAllInputAndOutput", urlPatterns = { "/*" })
public class LogFilter implements Filter{
	private static Logger logger = LoggerFactory.getLogger(LogFilter.class);
	private static List<String> excludeList;
	private static boolean active = true;
	private static Random random = new Random();

	public void init(FilterConfig filterConfig) throws ServletException {

		String activeStr = filterConfig.getInitParameter(TEE_FILTER_ACTIVE_PARAM);
		if(activeStr != null)
			active = Boolean.valueOf(activeStr);

		String excludeListAsStr = filterConfig.getInitParameter(TEE_FILTER_EXCLUDES_PARAM);
		excludeList = extractNameList(excludeListAsStr);	

		String localhostName = getLocalhostName();

		if (active)
			System.out.println("LogFilter will be ACTIVE on this host [" + localhostName + "] excludeList " + excludeList + "");
		else
			System.out.println("LogFilter will be DISABLED on this host [" + localhostName + "] excludeList " + excludeList + "");

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long startTime;
		long endTime;
		if(active){
			try {
				//String randomno = String.valueOf(random.nextInt(99999));
				HttpServletRequest servletRequest = (HttpServletRequest) request;
				String method = servletRequest.getMethod();
				String path = servletRequest.getRequestURI() + ((servletRequest.getQueryString()!=null && servletRequest.getQueryString().length() > 0)?"?"+servletRequest.getQueryString():"");
				String protocol = servletRequest.getProtocol();
				MHttpServletRequest teeRequest = new MHttpServletRequest( (HttpServletRequest) request);
				MHttpServletResponse teeResponse = new MHttpServletResponse( (HttpServletResponse) response);

				//System.out.println("BEFORE TeeFilter. filterChain.doFilter()");
				startTime = System.currentTimeMillis();
				chain.doFilter(teeRequest, teeResponse);
				endTime = System.currentTimeMillis();
				//System.out.println("AFTER TeeFilter. filterChain.doFilter()");

				teeResponse.finish();
				// let the output contents be available for later use by
				// logback-access-logging
				//teeRequest.setAttribute(LB_OUTPUT_BUFFER, teeResponse.getOutputBuffer());
				//teeRequest.getAsyncContext()
				
				logger.info("\r\nRequest {} {} {} \r\n {} \r\nResponse {} usedtime {} \r\n {}", 
						method, path, protocol, 
						new String(teeRequest.getInputBuffer()),
						teeResponse.getStatus(),
						String.valueOf(endTime - startTime),
						new String(teeResponse.getOutputBuffer())
						);
				//System.out.println("輸入 => "+new String(teeRequest.getInputBuffer()));
				//System.out.println("輸出 => "+new String(teeResponse.getOutputBuffer()));

			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} catch (ServletException e) {
				e.printStackTrace();
				throw e;
			}
		}else{
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	private String getLocalhostName() {
		String hostname = "127.0.0.1";

		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		}
		return hostname;
	}

	private List<String> extractNameList(String nameListAsStr) {
		List<String> nameList = new ArrayList<String>();
		if (nameListAsStr == null) {
			return nameList;
		}

		nameListAsStr = nameListAsStr.trim();
		if (nameListAsStr.length() == 0) {
			return nameList;
		}

		String[] nameArray = nameListAsStr.split("[,;]");
		for (String n : nameArray) {
			n = n.trim();
			nameList.add(n);
		}
		return nameList;
	}
}
