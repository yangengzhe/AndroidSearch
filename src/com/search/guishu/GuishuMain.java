package com.search.guishu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.MainActivity;
import com.example.search.R;

public class GuishuMain extends Activity
{
	EditText et_shouji;
	Button bt_sub;
	Button bt_returnButton;
	TextView tv_gui;
	TextView tv_type;
	private TextView tv_title;

    private Handler handler = new Handler();
    private ProgressDialog progressDialog = null;
	private int result;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.guishu_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);

		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("号码归属地");
		
		et_shouji = (EditText) findViewById(R.id.editText_guishu_1);
		bt_sub = (Button) findViewById(R.id.button_guishu_2);
		tv_gui = (TextView) findViewById(R.id.textView_guishu_1);
		tv_type = (TextView) findViewById(R.id.textView_guishu_2);
		bt_returnButton =(Button) findViewById(R.id.button_title_back);
//		// 解决4.0以上在主线程不能上网的问题
//				String strVer=android.os.Build.VERSION.RELEASE;
//				strVer=strVer.substring(0,3).trim();
//				float fv=Float.valueOf(strVer);
//				if(fv>2.3){
//				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//						.detectDiskReads().detectDiskWrites().detectNetwork()
//						.penaltyLog().build());
//				StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//						.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//						.penaltyLog().penaltyDeath().build());
//				}
		//返回按钮
		bt_returnButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		//提交按钮
		bt_sub.setOnClickListener(new OnClickListener()
		{
			String string = null;
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				progressDialog = ProgressDialog.show(GuishuMain.this, "请稍等...", "获取数据中...", true);
				
				new Thread(new Runnable(){
                    @Override
                    public void run() {
                   //加载数据
                         result=0;
                          try{
                              //要耗时运行的程序
                        	  URL url;
              				InputStream inStream = null;
                        	//提交get，获取xml
          					url = new URL(
          							"http://api.k780.com:88/?app=phone.get&phone="
          									+ et_shouji.getText().toString()
          									+ "&appkey=10398&sign=539a995867a0dca0f3c272ae6b00003e&format=xml");
          					HttpURLConnection conn = (HttpURLConnection) url
          							.openConnection();
          					conn.setRequestMethod("GET");
          					conn.setDoInput(true);
          					conn.setConnectTimeout(6 * 1000);
          					if (conn.getResponseCode() == 200)
          					{
          						inStream = conn.getInputStream();

          						// 使用输出流来输出字符
          						ByteArrayOutputStream out = new ByteArrayOutputStream();
          						byte[] buf = new byte[1024];
          						int len;
          						while ((len = inStream.read(buf)) != -1)
          						{
          							out.write(buf, 0, len);
          						}
          						string = out.toString("UTF-8");
          						out.close();

          					}
          					
                             result=1;
                          }
                         catch(Exception ex){
                             result=-1; 
                         }           
                        
                    //更新界面
                         handler.post(new Runnable() {     
                             public void run() {                          
                                 if(result==1)
                                 {
                                	//耗时程序完成后，进行更新界面
                                	 try
                     				{
                     					
                     					//解析xml
                     					XmlPullParser parser = Xml.newPullParser();
                     					parser.setInput(
                     							new ByteArrayInputStream(string.getBytes("UTF-8")),
                     							"UTF-8");
                     					// parser.setInput(inStream, "UTF-8");
                     					int eventType = parser.getEventType();

                     					while (eventType != XmlPullParser.END_DOCUMENT)
                     					{
                     						if (eventType == XmlPullParser.START_TAG)
                     						{
                     							// if ("SSOMessage".equals(parser.getName()))
                     							// {
                     							// version = parser.getAttributeValue(0);
                     							// } else
                     							if ("success".equals(parser.getName()))
                     							{
                     								String s = parser.nextText();
                     								int i = Integer.parseInt(s);
                     								if (i != 1)
                     								{
                     									tv_gui.setText("手机号输入错误！");
                     									tv_type.setText("");
                     								}
                     							} else if ("att".equals(parser.getName()))
                     							{
                     								tv_gui.setText(parser.nextText());
                     							} else if ("ctype".equals(parser.getName()))
                     							{
                     								tv_type.setText(parser.nextText());
                     							}

                     						}
                     						eventType = parser.next();
                     					}

                     				} catch (MalformedURLException e)
                     				{
                     					// TODO Auto-generated catch block
                     					e.printStackTrace();
                     				} catch (ProtocolException e)
                     				{
                     					// TODO Auto-generated catch block
                     					e.printStackTrace();
                     				} catch (IOException e)
                     				{
                     					// TODO Auto-generated catch block
                     					e.printStackTrace();
                     				} catch (XmlPullParserException e)
                     				{
                     					// TODO Auto-generated catch block
                     					e.printStackTrace();
                     				}
                                 }
                                       else
                                           Toast.makeText(getApplication(), "数据获取失败,请检查网络连接", Toast.LENGTH_SHORT).show();    
                                 }                
                             });
                         progressDialog.dismiss();
                    }}).start();

			}
		});

	}
}
