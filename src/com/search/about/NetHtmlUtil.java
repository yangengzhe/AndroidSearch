package com.search.about;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetHtmlUtil {
	/**
	 * 获得网页地址对应的网页
	 * @param address 网页地址
	 * @return
	 * @throws Exception 抛出异常
	 */
	public static String getHtml(String address) throws Exception{
		URL url = new URL(address);
		//打开链接
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//设置超时时间
		conn.setReadTimeout(10000);
		//设置访问方式
		conn.setRequestMethod("GET");
		//获得服务器打回来的状态码，如果是  200，就能读取内容，如果是 200以外，就可能有问题
		int code = conn.getResponseCode();
		if(code==200){
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] bb = new byte[1024];
			int len = 0;
			
			//循环读输入流里面的内容
			while((len=is.read(bb)) != -1){
				bos.write(bb,0,len);
			}
			is.close();
			byte[] result = bos.toByteArray();
			return new String(result);
		}else{
			//运行时异常，如果是不等200的话，就是非法状态的异常
			 throw new IllegalStateException("访问网络失败");
		}
		
	}
}

