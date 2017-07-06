/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

public class RequestParams {
	private Logger logger = Logger.getLogger(RequestParams.class);
	private static String URL_ENCODING = "UTF-8";

	private List<Header> headers;
	private List<NameValuePair> queryStringParams;
	private HttpEntity bodyEntity;
	private List<NameValuePair> bodyParams;

	public RequestParams() {
	}

	public void addHeader(Header header) {
		if (this.headers == null) {
			this.headers = new ArrayList<Header>();
		}
		this.headers.add(header);
	}

	public void addHeader(String name, String value) {
		if (this.headers == null) {
			this.headers = new ArrayList<Header>();
		}
		this.headers.add(new BasicHeader(name, value));
	}

	public void addHeaders(List<Header> headers) {
		if (this.headers == null) {
			this.headers = new ArrayList<Header>();
		}
		this.headers.addAll(headers);
	}

	public void addQueryStringParameter(String name, String value) {
		if (queryStringParams == null) {
			queryStringParams = new ArrayList<NameValuePair>();
		}
		queryStringParams.add(new BasicNameValuePair(name, value));
	}

	public void addQueryStringParameter(NameValuePair nameValuePair) {
		if (queryStringParams == null) {
			queryStringParams = new ArrayList<NameValuePair>();
		}
		queryStringParams.add(nameValuePair);
	}

	public void addQueryStringParameter(List<NameValuePair> nameValuePairs) {
		if (queryStringParams == null) {
			queryStringParams = new ArrayList<NameValuePair>();
		}
		queryStringParams.addAll(nameValuePairs);
	}

	public void addBodyParameter(String name, String value) {
		if (bodyParams == null) {
			bodyParams = new ArrayList<NameValuePair>();
		}
		bodyParams.add(new BasicNameValuePair(name, value));
	}

	public void addBodyParameter(NameValuePair nameValuePair) {
		if (bodyParams == null) {
			bodyParams = new ArrayList<NameValuePair>();
		}
		bodyParams.add(nameValuePair);
	}

	public void addBodyParameter(List<NameValuePair> nameValuePairs) {
		if (bodyParams == null) {
			bodyParams = new ArrayList<NameValuePair>();
		}
		bodyParams.addAll(nameValuePairs);
	}

	public void setBodyEntity(HttpEntity bodyEntity) {
		this.bodyEntity = bodyEntity;
		if (bodyParams != null) {
			bodyParams.clear();
			bodyParams = null;
		}
	}

	/**
	 * Returns an HttpEntity containing all request parameters
	 */
	public HttpEntity getEntity() {

		if (bodyEntity != null) {
			return bodyEntity;
		}

		HttpEntity result = null;
		if (bodyParams == null) {
			return null;
		}

		result = new BodyParamsEntity(bodyParams, URL_ENCODING);

		return result;
	}

	public List<NameValuePair> getQueryStringParams() {
		return this.queryStringParams;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void printAllParams() {
		if (headers != null && headers.size() > 0) {
			for (Header item : headers) {
				logger.info("header parameter :" + item.getName() + " " + item.getValue());
			}
		} else {
			logger.info("headers params is empty");
		}

		if (queryStringParams != null && queryStringParams.size() > 0) {
			for (NameValuePair item : queryStringParams) {
				logger.info("queryStringParams parameter :" + item.getName() + " " + item.getValue());
			}
		} else {
			logger.info("queryStringParams params is empty");
		}

		if (bodyParams != null && bodyParams.size() > 0) {
			for (NameValuePair item : bodyParams) {
				logger.info("bodyParams parameter :" + item.getName() + " " + item.getValue());
			}
		} else {
			logger.info("bodyParams params is empty");
		}
	}
}