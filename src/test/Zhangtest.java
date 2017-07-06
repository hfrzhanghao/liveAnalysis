package test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import junit.framework.TestCase;

import net.sf.json.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class Zhangtest extends TestCase {

	static RestTemplate restTemplate;
	static {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();
		StringHttpMessageConverter httpMessageConverter = new StringHttpMessageConverter();
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
		httpMessageConverter.setWriteAcceptCharset(false);
		list.add(httpMessageConverter);
		list.add(converter);
		restTemplate.setMessageConverters(list);
	}

	public void testLive() {
		String url = "http://localhost:8080/liveAnalysis/all/liveCount";
		String data = "startTime=1&&endTime=15100000000000&&url=http://pull.live.gmw.cn/live/gmwlivexc.flv";
		ResponseEntity<String> entity = restTemplate.postForEntity(url + "?" + data, null, String.class);
		System.out.println(entity.getBody());
	}
	
	public void testLog() {
		String url = "http://localhost:8080/liveAnalysis/all/logCheck";
		String data = "startTime=1&&endTime=15100000000000&&IP=1.202.14.194";
		ResponseEntity<String> entity = restTemplate.postForEntity(url + "?" + data, null, String.class);
		//String url = "http://baidu.com";
		//String data = "startTime=1&&endTime=15100000000000&&IP=1.202.14.194";
		//ResponseEntity<String> entity = restTemplate.postForEntity(url , null, String.class);
		System.out.println(entity.getBody());
	}
	
	public void testIP() {
		String url = "http://localhost:8080/liveAnalysis/all/ipCheck";
		String data = "startTime=1&&endTime=15100000000000&&url=http://pull.live.gmw.cn/live/gmwlivexc.flv&&result=2000";
		ResponseEntity<String> entity = restTemplate.postForEntity(url + "?" + data, null, String.class);
		System.out.println(entity.getBody());
	}

	private static HttpEntity<String> createHttpEntity(String json) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Accept", "application/json");
		requestHeaders.set("Accept-Charset", "utf-8");
		requestHeaders.set("Content-Type", "application/json;charset=utf-8");
		HttpEntity<String> httpEntity = new HttpEntity<String>(json, requestHeaders);
		return httpEntity;
	}

}
