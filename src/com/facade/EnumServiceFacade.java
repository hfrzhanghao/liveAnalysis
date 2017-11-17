package com.facade;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.dictionary.EnumDic;
//import com.result.UsersResult;
/*
 * 获取枚举值接口
 * */
@Service
@Path("/enum")
public class EnumServiceFacade {
	private Logger logger = Logger.getLogger(this.getClass());

	@Path("/enumValue")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String liveCount() {

		//UsersResult result = new UsersResult();
		JSONObject jobj = new JSONObject();
		try {
			Map<String,String> map = EnumDic.m_tab;
			for(String key:map.keySet()){
				jobj.put(key, map.get(key).split("@"));
			}

		} catch (Exception e) {
			String message = e.getMessage() == null ? "" : e.getMessage();
			logger.error(message, e);
			//result.setResult(-1);
		}
		String data = jobj.toString();
		return data;
	}
}
