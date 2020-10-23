package com.commonutils.util.http;

import com.commonutils.util.json.JSONObject;
import com.commonutils.util.string.StringUtil;
import com.commonutils.util.validate.ObjectCensor;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class HttpClientPoolUtil {
	private static CloseableHttpClient httpClient;

	private static final Logger logger = LoggerFactory.getLogger(HttpClientPoolUtil.class);

	static {
			//�����ƹ���֤�ķ�ʽ����https����
			SSLContext sslcontext = createIgnoreVerifySSL();
			//����Э��http��https��Ӧ�Ĵ���socket���ӹ����Ķ���
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslcontext))
					.build();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cm.setDefaultMaxPerRoute(20);
			cm.setMaxTotal(500);
			httpClient = HttpClients.custom().setKeepAliveStrategy((HttpResponse response, HttpContext context)-> {
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				int keepTime = 30;
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							return Long.parseLong(value) * 1000;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				return keepTime * 1000;
			}).setConnectionManager(cm).build();
	}


	/**
	 * �ƹ���֤
	 */
	private static SSLContext createIgnoreVerifySSL()  {
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSLv3");
			// ʵ��һ��X509TrustManager�ӿڣ������ƹ���֤�������޸�����ķ���
			X509TrustManager trustManager = new X509TrustManager() {
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
						String paramString) {
				}
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
						String paramString) {
				}
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sc.init(null, new TrustManager[] { trustManager }, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sc;
	}

	/**
	 * ��������
	 *
	 * @param url ����url
	 * @param methodName ����ķ�������
	 * @param headMap ����ͷ
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private static HttpRequestBase getRequest(String url, String methodName,Map<String, String> headMap)
			throws KeyManagementException, NoSuchAlgorithmException {
		HttpRequestBase method;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30 * 1000).setConnectTimeout(30 * 1000)
				.setConnectionRequestTimeout(30 * 1000).setExpectContinueEnabled(false).build();

		if (HttpPut.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpPut(url);
		} else if (HttpPost.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpPost(url);
		} else if (HttpGet.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpGet(url);
		} else if (HttpDelete.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpDelete(url);
		} else {
			method = new HttpPost(url);
		}
		if(!headMap.isEmpty()){
			for(Entry<String, String> value:headMap.entrySet()){
				method.addHeader(value.getKey(), value.getValue());
			}
		}
		method.setConfig(requestConfig);
		return method;
	}

	/**
	 ****************************************************** �Ĵ��������*********************************************
	 */

	/**
	 * ִ��http get����
	 *
	 * @param url ����url
	 * @param headMap ����ͷ
	 * @return
	 */
	public static <R> R get(String url,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		HttpEntity httpEntity = null;
		HttpRequestBase method = null;
		R responseBody;
		try {
			method = getRequest(url, HttpGet.METHOD_NAME,headMap);
			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = httpClient.execute(method, context);
			responseBody = afterResposne.apply(httpResponse);
		} catch (Exception e) {
			if(method != null){
				method.abort();
			}
			throw e;
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return responseBody;
	}

	/**
	 * ִ��http post����
	 *
	 * @param url 		�����ַ
	 * @param data  	��������		��Ϊapplication/x-www-form-urlencoded,dataΪ��ֵ���ַ�������Ϊapplication/json��dataΪjson�ַ���
	 * @param headMap  	����ͷ 		headMap����Ҫָ��Content-Type
	 * @return
	 */
	public static <R> R post(String url, String data,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		HttpEntity httpEntity = null;
		HttpEntityEnclosingRequestBase method = null;
		R responseBody;
		try {
			method = (HttpEntityEnclosingRequestBase) getRequest(url, HttpPost.METHOD_NAME,headMap);
			method.setEntity(new StringEntity(data,Charset.forName("UTF-8")));
			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = httpClient.execute(method, context);
			responseBody = afterResposne.apply(httpResponse);

		} catch (Exception e) {
			if(method != null){
				method.abort();
			}
			throw e;
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return responseBody;
	}

	/**
	 * ִ��http put����
	 *
	 * @param url 		�����ַ
	 * @param data  	��������
	 * @param headMap  	����ͷ
	 * @return
	 */
	public static <R> R put(String url, String data,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		HttpEntity httpEntity = null;
		HttpEntityEnclosingRequestBase method = null;
		R responseBody;
		try {
			method = (HttpEntityEnclosingRequestBase) getRequest(url, HttpPut.METHOD_NAME,headMap);
			method.setEntity(new StringEntity(data,Charset.forName("UTF-8")));
			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = httpClient.execute(method, context);
			responseBody = afterResposne.apply(httpResponse);

		} catch (Exception e) {
			if(method != null){
				method.abort();
			}
			throw e;
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return responseBody;
	}

	/**
	 * ִ��DELETE ����
	 *
	 * @param url 		�����ַ
	 * @param headMap  	����ͷ
	 * @return
	 */
	public static <R> R delete(String url,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		HttpEntity httpEntity = null;
		HttpRequestBase method = null;
		R responseBody;
		try {
			method = getRequest(url, HttpDelete.METHOD_NAME,headMap);
			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = httpClient.execute(method, context);
			responseBody = afterResposne.apply(httpResponse);
		} catch (Exception e) {
			if(method != null){
				method.abort();
			}
			throw e;
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return responseBody;
	}

	/**
	 ****************************************************** ����Э��*********************************************
	 */
	/**
	 * @param url
	 * @param param		JSON�ַ���,��תΪ��ֵ����ʽ׷�ӵ�url����
	 * @param body		JSON�ַ���
	 * @param headMap	��Ϣͷ���ں�"Content-Type"="application/x-www-form-urlencoded"
	 * @return
	 * @throws Exception
	 */
	public static <R> R postForm(String url, String param,String body, Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		headMap.put("Content-Type","application/x-www-form-urlencoded");

		// �����������
		StringBuffer sb = new StringBuffer();
		if (!ObjectCensor.checkObjectIsNull(body)) {
			JSONObject joinParam = JSONObject.fromObject(body);
			Iterator iter = joinParam.keys();
			while (iter.hasNext())
			{
				String keyT =iter.next()+"";
				String key  = StringUtil.getRstFieldName_ByVoFldName(keyT).toLowerCase();
				String val= StringUtil.getJSONObjectKeyVal(joinParam,keyT);
				sb.append(key);
				sb.append("=");
				sb.append(URLEncoder.encode(val, "UTF-8"));
				sb.append("&");
			}
			sb.replace(0, sb.length(), sb.substring(0, sb.length() - 1));
		}
		return HttpClientPoolUtil.post(appendParamToURL(url,param), sb.toString(),headMap,afterResposne);
	}

	/**
	 * @param url
	 * @param param		JSON�ַ���,��תΪ��ֵ����ʽ׷�ӵ�url����
	 * @param body		JSON�ַ���
	 * @param headMap	��Ϣͷ���ں�"Content-Type"="application/json"
	 * @return
	 * @throws Exception
	 */
	public static <R> R postJSON(String url, String param,String body,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		headMap.put("Content-Type","application/json");
		return HttpClientPoolUtil.post(appendParamToURL(url,param), body,headMap,afterResposne);
	}

	/**
	 * @param url
	 * @param headMap		��Ϣͷ���ײ�API�ṹ��"Content-Type"="multipart/form-data"����Ҫ�ٰ���Content-Type������Ḳ��
	 * @param formBodyParts	��Ϣ�壬��ͨ�ֶ��Լ��ļ�
	 * @return
	 * @throws Exception
	 */
	public static <R> R postMultipartData(String url, Map<String, String> headMap, ArrayList<FormBodyPart> formBodyParts, Function<CloseableHttpResponse,R> afterResposne) throws Exception {
		HttpEntity httpEntity = null;
		HttpEntityEnclosingRequestBase method = null;
		R responseBody;
		try {
			method = (HttpEntityEnclosingRequestBase) getRequest(url,HttpPost.METHOD_NAME,headMap);
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			for (int i = 0; i < formBodyParts.size(); i++) {
				multipartEntityBuilder.addPart(formBodyParts.get(i).getName(),formBodyParts.get(i).getBody());
			}
			httpEntity = multipartEntityBuilder.build();
			method.setEntity(httpEntity);

			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = httpClient.execute(method, context);
			responseBody = afterResposne.apply(httpResponse);
		} catch (Exception e) {
			if(method != null){
				method.abort();
			}
			throw e;
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return responseBody;
	}
	/**
	 * @param url
	 * @param param		JSON�ַ���,��תΪ��ֵ����ʽ׷�ӵ�url����
	 * @param body		JSON�ַ���
	 * @param headMap	��Ϣͷ���ں�"Content-Type"="application/x-www-form-urlencoded"
	 * @return
	 * @throws Exception
	 */
	public static <R> R putForm(String url, String param,String body,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		headMap.put("Content-Type","application/x-www-form-urlencoded");

		// �����������
		StringBuffer sb = new StringBuffer();
		if (!ObjectCensor.checkObjectIsNull(body)) {
			JSONObject joinParam = JSONObject.fromObject(body);
			Iterator iter = joinParam.keys();
			while (iter.hasNext())
			{
				String keyT =iter.next()+"";
				String key  = StringUtil.getRstFieldName_ByVoFldName(keyT).toLowerCase();
				String val= StringUtil.getJSONObjectKeyVal(joinParam,keyT);
				sb.append(key);
				sb.append("=");
				sb.append(URLEncoder.encode(val, "UTF-8"));
				sb.append("&");
			}
			sb.replace(0, sb.length(), sb.substring(0, sb.length() - 1));
		}
		return HttpClientPoolUtil.put(appendParamToURL(url,param), sb.toString(),headMap,afterResposne);
	}

	/**
	 * @param url
	 * @param param		JSON�ַ���,��תΪ��ֵ����ʽ׷�ӵ�url����
	 * @param body		JSON�ַ���
	 * @param headMap	��Ϣͷ���ں�"Content-Type"="application/json"
	 * @return
	 * @throws Exception
	 */
	public static <R> R putJSON(String url, String param,String body,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		headMap.put("Content-Type","application/json");
		return HttpClientPoolUtil.put(appendParamToURL(url,param),body,headMap,afterResposne);
	}

	/**
	 * @param url
	 * @param param		JSON�ַ���,��תΪ��ֵ����ʽ׷�ӵ�url����
	 * @return
	 * @throws Exception
	 */
	public static <R> R delete(String url,String param,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		return HttpClientPoolUtil.delete(appendParamToURL(url,param),headMap,afterResposne);
	}

	/**
	 * @param url
	 * @param param		JSON�ַ���,��תΪ��ֵ����ʽ׷�ӵ�url����
	 * @return
	 * @throws Exception
	 */
	public static <R> R get(String url, String param,Map<String, String> headMap,Function<CloseableHttpResponse,R> afterResposne) throws Exception{
		return HttpClientPoolUtil.get(appendParamToURL(url,param),headMap,afterResposne);
	}

	private static String appendParamToURL(String url,String param) throws Exception{
		if(ObjectCensor.isStrRegular(param)){
			JSONObject json  = JSONObject.fromObject(param);
			Iterator iter =  json.keys();
			if(json.size() > 0 ){
				StringBuffer sb = new StringBuffer(url+"?");
				while(iter.hasNext())
				{
					String key =iter.next()+"";
					String val= StringUtil.getJSONObjectKeyVal(json,key);
					sb.append(key+"="+URLEncoder.encode(val, "UTF-8")+"&");
				}
				sb.deleteCharAt(sb.length() - 1);
				url = sb.toString();
			}
		}
		return url;
	}
	/**
	 ****************************************************** Response����*********************************************
	 */
	public static Function getResponseString = new Function<CloseableHttpResponse,String>() {
		@Override
		public String apply(CloseableHttpResponse httpResponse) {
			String str = "";
			int httpResponseCode = httpResponse.getStatusLine().getStatusCode();//״̬��
			logger.info("httpResponseCode[{}]",httpResponseCode);
			try {
				str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
			return str;
		}
		@Override
		public <V> Function<V, String> compose(Function<? super V, ? extends CloseableHttpResponse> before) {
			return null;
		}
		@Override
		public <V> Function<CloseableHttpResponse, V> andThen(Function<? super String, ? extends V> after) {
			return null;
		}
	};
	/**
	 ****************************************************** example*********************************************
	 */
	public static void main(String[] args) throws Exception {
		Object result = HttpClientPoolUtil.postMultipartData("http://192.168.137.67:8080/file/upload",
				new HashMap<String,String>(),
				new ArrayList<FormBodyPart>(){{
					add(new FormBodyPart(
							"uploadFile",
							new FileBody(
									new File(
											"C:\\Users\\asus\\Desktop\\log\\1.zip"),
									ContentType.create("application/x-gzip-compressed"),
									"1.zip")
					));
					add(new FormBodyPart(
							"key",
							new StringBody("value", ContentType.TEXT_PLAIN)
					));
				}},getResponseString);
		System.out.println("http result: " + result);
	}
}
