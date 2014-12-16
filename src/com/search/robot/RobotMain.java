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
	private Boolean websign = false;//�����Ƿ������ж�
	private String re_strString;
	final Handler handler = new Handler() {  
	    @Override  
	    // ������Ϣ���ͳ�����ʱ���ִ��Handler���������  
	    public void handleMessage(Message msg) {  
	        super.handleMessage(msg);  
	        // ����UI  
			switch(msg.what){
			case 0:
				SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd  hh:mm:ss"); 
				String date   =   sDateFormat.format(new   java.util.Date());  
		        HashMap<String, Object> map2 = new HashMap<String, Object>();
		        map2.put("ItemImage", R.drawable.dou);//ͼ����Դ��ID
		        map2.put("ItemTitle", "С�� ��"+date+"ʱ˵��");
		        map2.put("ItemText", re_strString);
		        listItem.add(map2);
		        init();
		        //��Ӳ�����ʾ
		        lv_1.setAdapter(listItemAdapter);
				break;
			}
			
	    }  
	};  
	//���ɶ�̬���飬��������
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
		tv_title.setText("С��������");
		
		button1 = (Button)findViewById(R.id.button_title_back);
		button2 = (Button)findViewById(R.id.button_robot_1);
		lv_1 = (ListView)findViewById(R.id.listView_robot_1);
		et_1 = (EditText)findViewById(R.id.editText_robot_1);
//		// ���4.0���������̲߳�������������
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
		//����Ƿ�����
		ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  
		if(wifi|internet){  //ִ����һ������  
			websign = true;
		}else{  
			websign = false;
			StringBuffer sb = new StringBuffer();
			sb.append("��������ʧ�ܣ���������Ҫ�����������������ӣ�");
			Dialog dialog = new AlertDialog.Builder(this).setTitle("�޷�����")
					.setMessage(sb.toString())// ��������
					.setPositiveButton("ȷ��",// ����ȷ����ť
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									//dialog.dismiss();
									finish();
								}
							}).create();// ����
			dialog.show();
		}
				
		
		//���ذ�ť
		button1.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		//���Ͱ�ť
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
		            map.put("ItemImage", R.drawable.ic_launcher);//ͼ����Դ��ID
		            map.put("ItemTitle", "�� ��"+date+"ʱ˵��");
		            map.put("ItemText", send_strString);
		            listItem.add(map);
		            Toast.makeText(RobotMain.this, "���ͳɹ�����ȴ��ظ���", Toast.LENGTH_SHORT).show();
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
		        //��Ӳ�����ʾ
		        lv_1.setAdapter(listItemAdapter);
			}
			}
		});
		
	}
	private void init()
	{
		//������������Item�Ͷ�̬�����Ӧ��Ԫ��
        listItemAdapter = new SimpleAdapter(this,listItem,//����Դ 
            R.layout.res_robot_me,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemImage","ItemTitle", "ItemText"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );
	}
	
	//���Ͳ�ȡ����Ϣ
	private String send(String str)
	{
		String str_returnString = null;
		try
		// ��ȡjson
		{
			
			String path = "http://xiao.douqq.com/api.php?msg="+URLEncoder.encode(str,"GB2312")+"&type=json";
			URL url = new URL(path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(3000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			int code = connection.getResponseCode();
			if (code == 200)// ����json
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
