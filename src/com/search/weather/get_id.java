package com.search.weather;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class get_id
{
	public static byte[] readStream(InputStream inputStream) throws Exception
	{
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		while ((len = inputStream.read(buffer)) != -1)
		{
			byteArrayOutputStream.write(buffer, 0, len);
		}

		inputStream.close();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}
//读取源码
	public static String GetHtml(String urlpath) throws Exception
	{
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(6 * 1000);
		conn.setRequestMethod("GET");

		if (conn.getResponseCode() == 200)
		{
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);
			String html = new String(data);
			return html;
		}
		return null;
		
//		try
//		// 获取json
//		{
//			String path = urlpath;
//			URL url = new URL(path);
//			HttpURLConnection connection = (HttpURLConnection) url
//					.openConnection();
//			connection.setConnectTimeout(3000);
//			connection.setRequestMethod("GET");
//			connection.setDoInput(true);
//			int code = connection.getResponseCode();
//			if (code == 200)// 接收json
//			{
//
//				ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
//				byte[] data = new byte[1024];
//				int len = 0;
//				try
//				{
//					while ((len = connection.getInputStream().read(data)) != -1)
//					{
//						outPutStream.write(data, 0, len);
//					}
//					String jsonString = new String(outPutStream.toByteArray());
//					return jsonString;
//
//				} catch (Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		} catch (Exception e)
//		{
//			// TODO: handle exception
//		}
//		return null;
	}
}
