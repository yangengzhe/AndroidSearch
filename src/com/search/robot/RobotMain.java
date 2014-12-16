package com.search.robot;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.R;

public class RobotMain extends Activity
{

	private Button button1;
	private Button button2;
	private ListView lv_1;
	private EditText et_1;
	private SimpleAdapter listItemAdapter;
	private TextView tv_title;
	private Boolean websign = false;//网络是否连接判断
	private String re_strString;
	final Handler handler = new Handler() {  
	    @Override  
	    // 当有消息发送出来的时候就执行Handler的这个方法  
	    public void handleMessage(Message msg) {  
	        super.handleMessage(msg);  
	        // 处理UI  
			switch(msg.what){
			case 0:
				SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd  hh:mm:ss"); 
				String date   =   sDateFormat.format(new   java.util.Date());  
		        HashMap<String, Object> map2 = new HashMap<String, Object>();
		        map2.put("ItemImage", R.drawable.dou);//图像资源的ID
		        map2.put("ItemTitle", "小豆 在"+date+"时说：");
		        map2.put("ItemText", re_strString);
		        listItem.add(map2);
		        init();
		        //添加并且显示
		        lv_1.setAdapter(listItemAdapter);
				break;
			}
			
	    }  
	};  
	//生成动态数组，加入数据
    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.robot_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);
		
		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("小豆机器人");
		
		button1 = (Button)findViewById(R.id.button_title_back);
		button2 = (Button)findViewById(R.id.button_robot_1);
		lv_1 = (ListView)findViewById(R.id.listView_robot_1);
		et_1 = (EditText)findViewById(R.id.editText_robot_1);
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
		//检测是否连网
		ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  
		if(wifi|internet){  //执行下一步操作  
			websign = true;
		}else{  
			websign = false;
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
				
		
		//返回按钮
		button1.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		//发送按钮
		button2.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(websign){
				// TODO Auto-generated method stub
				String send_strString=et_1.getText().toString();
				if(send_strString == null ) return;
				else{
					SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");     
		        	String   date   =   sDateFormat.format(new   java.util.Date());  
		            HashMap<String, Object> map = new HashMap<String, Object>();
		            map.put("ItemImage", R.drawable.ic_launcher);//图像资源的ID
		            map.put("ItemTitle", "我 在"+date+"时说：");
		            map.put("ItemText", send_strString);
		            listItem.add(map);
		            Toast.makeText(RobotMain.this, "发送成功，请等待回复！", Toast.LENGTH_SHORT).show();
		            new Thread() {  
		                @Override  
		                public void run() {  
		                	String send_strString=et_1.getText().toString();
		                	re_strString = send(send_strString);  
		                    handler.sendEmptyMessage(0);  
		                }  
		            }.start();  
				}
				init();
		        //添加并且显示
		        lv_1.setAdapter(listItemAdapter);
			}
			}
		});
		
	}
	private void init()
	{
		//生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this,listItem,//数据源 
            R.layout.res_robot_me,//ListItem的XML实现
            //动态数组与ImageItem对应的子项        
            new String[] {"ItemImage","ItemTitle", "ItemText"}, 
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );
	}
	
	//发送并取回信息
	private String send(String str)
	{
		String str_returnString = null;
		try
		// 获取json
		{
			
			String path = "http://xiao.douqq.com/api.php?msg="+URLEncoder.encode(str,"GB2312")+"&type=json";
			URL url = new URL(path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(3000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			int code = connection.getResponseCode();
			if (code == 200)// 接收json
			{

				ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
				byte[] data = new byte[1024];
				int len = 0;
				try
				{
					while ((len = connection.getInputStream().read(data)) != -1)
					{
						outPutStream.write(data, 0, len);
					}
					String jsonString = new String(outPutStream.toByteArray());
					JSONObject jsonObject = new JSONObject(jsonString);

					str_returnString = jsonObject.getString("response");

				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return str_returnString;
	}

}
