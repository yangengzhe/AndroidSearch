package com.search.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.MainActivity;
import com.example.search.R;

public class MessageMain extends Activity
{
	private Button button1;
	private Button button2;
	private EditText et_edit1;
	private EditText et_edit2;
	private EditText et_edit3;
	private ListView lv_list;
	private TextView tv_title;
	private Boolean websign = false;//网络是否连接判断
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.message_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);
		
		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("留言板");
		
		button1 = (Button) findViewById(R.id.button_title_back);
		button2 = (Button) findViewById(R.id.button_message_1);
		et_edit1 = (EditText) findViewById(R.id.editText_message_1);
		et_edit2 = (EditText) findViewById(R.id.editText_message_2);
		et_edit3 = (EditText) findViewById(R.id.editText_message_3);
		lv_list = (ListView) findViewById(R.id.listView_mesaage);
		
		// 解决4.0以上在主线程不能上网的问题
				String strVer=android.os.Build.VERSION.RELEASE;
				strVer=strVer.substring(0,3).trim();
				float fv=Float.valueOf(strVer);
				if(fv>2.3){
				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
						.detectDiskReads().detectDiskWrites().detectNetwork()
						.penaltyLog().build());
				StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
						.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
						.penaltyLog().penaltyDeath().build());
				}
				
		//检测是否连网
				ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);  
				boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
				boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  
				if(wifi|internet){  //执行下一步操作  
					websign = true;
					load_message();
				}else{  
					StringBuffer sb = new StringBuffer();
					sb.append("网络连接失败！本功能需要联网，请检查网络连接！");
					Dialog dialog = new AlertDialog.Builder(this).setTitle("无法连接")
							.setMessage(sb.toString())// 设置内容
							.setPositiveButton("确定",// 设置确定按钮
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											//dialog.dismiss();
											finish();
										}
									}).create();// 创建
					dialog.show();
				}
				
		
		//返回
		button1.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
				
			}
		});
		
		button2.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(websign){
				// TODO Auto-generated method stub
				if(et_edit1.getText().toString() == null || et_edit2.getText().toString()==null || et_edit3.getText().toString() == null)
				{
				Toast.makeText(getApplicationContext(), "请将信息填写完全", Toast.LENGTH_LONG).show();
				return;
				}
				else {
					try//写入数据库
					{
						HttpClient httpclient = new DefaultHttpClient();
						HttpGet httpget = new HttpGet(
								"http://www.i3geek.com/1/post.php?name="+et_edit1.getText().toString()+"&email="+et_edit2.getText().toString()+"&content="+et_edit3.getText().toString());
						HttpResponse response;
						response = httpclient.execute(httpget);
						HttpEntity entity = response.getEntity();
						entity.getContent();
					} catch (ClientProtocolException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//加载
					load_message();
					
					
				}
				}
			}
		});
		
		
		
		}
	
	private void load_message()
	{
		InputStream is = null;
		JSONArray jArray;
		String result = null;
		StringBuilder sb = null;
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();  
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://www.i3geek.com/1/get.php");
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection" + e.toString());
		}
		// http post
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");

			String line = "0";
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e)
		{
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		// paring data
//		int ct_id;
		String ct_name;
//		String ct_email;
		String ct_content;
		String ct_time;
		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
//			tv.setText("");
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
//				ct_id = json_data.getInt("id");
				ct_name = json_data.getString("name");
//				ct_email = json_data.getString("email");
				ct_content = json_data.getString("content");
				ct_time = json_data.getString("time");
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", "【"+ct_name + "】在" + ct_time+ "时发布：");  
				if(ct_content.getBytes().length > 40)
				map.put("ItemText", ct_content.substring(0, 13)+"[更多]");//防止超出，截取  
				else map.put("ItemText", ct_content);
				data.add(map);
			}
//			lv_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,data));; 
			 SimpleAdapter listItemAdapter = new SimpleAdapter(this,data,//数据源   
					             R.layout.res_message_list,//ListItem的XML实现  
					             //动态数组与ImageItem对应的子项          
					             new String[] {"ItemTitle", "ItemText"},   
					             //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
					             new int[] {R.id.ItemTitle,R.id.ItemText}  
					         );  
					          
					         //添加并且显示  
					         lv_list.setAdapter(listItemAdapter);  

		} catch (JSONException e1)
		{
			// Toast.makeText(getBaseContext(), "No City Found"
			// ,Toast.LENGTH_LONG).show();
		} catch (ParseException e1)
		{
			e1.printStackTrace();
		}
	}
}
