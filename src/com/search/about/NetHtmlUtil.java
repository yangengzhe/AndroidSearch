package com.search.about;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetHtmlUtil {
	/**
	 * �����ҳ��ַ��Ӧ����ҳ
	 * @param address ��ҳ��ַ
	 * @return
	 * @throws Exception �׳��쳣
	 */
	public static String getHtml(String address) throws Exception{
		URL url = new URL(address);
		//������
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//���ó�ʱʱ��
		conn.setReadTimeout(10000);
		//���÷��ʷ�ʽ
		conn.setRequestMethod("GET");
		//��÷������������״̬�룬�����  200�����ܶ�ȡ���ݣ������ 200���⣬�Ϳ���������
		int code = conn.getResponseCode();
		if(code==200){
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] bb = new byte[1024];
			int len = 0;
			
			//ѭ�������������������
			while((len=is.read(bb)) != -1){
				bos.write(bb,0,len);
			}
			is.close();
			byte[] result = bos.toByteArray();
			return new String(result);
		}else{
			//����ʱ�쳣������ǲ���200�Ļ������ǷǷ�״̬���쳣
			 throw new IllegalStateException("��������ʧ��");
		}
		
	}
}

