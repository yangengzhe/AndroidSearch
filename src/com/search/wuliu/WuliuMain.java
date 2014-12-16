package com.search.wuliu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.search.MainActivity;
import com.example.search.R;

public class WuliuMain extends Activity
{
	private TextView tv_title;
	private Spinner mySpinner;
	private EditText myedittext;
	private List<String> list = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private Map<String, String> map1 = new HashMap<String, String>();
	private Button bt_subButton;
	private WebView webView1;
	private String kuaidiString;
	private Button bt_return1;
	private Button bt_return2;
	private Boolean websign = false;//�����Ƿ������ж�

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.wuliu_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title_main);

		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("������ѯ");
		
		bt_subButton = (Button) findViewById(R.id.button_face_2);
		bt_return1 = (Button) findViewById(R.id.button_face_1);
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
				
		// ������ݶ�Ӧ�ĵ�ͼ
		map1.put("ems���", "ems");
		map1.put("��ͨ", "shentong");
		map1.put("˳��", "shunfeng");
		map1.put("��ػ���", "tiandihuayu");
		map1.put("������", "tiantian");
		map1.put("Բͨ�ٵ�", "yuantong");
		map1.put("�ϴ����", "yunda");
		map1.put("��ͨ���", "yuntongkuaidi");
		map1.put("լ����", "zhaijisong");
		map1.put("��ͨ�ٵ�", "zhongtong");
		// ��ݵ������˵�
		list.add("ems���");
		list.add("��ͨ");
		list.add("˳��");
		list.add("��ػ���");
		list.add("������");
		list.add("Բͨ�ٵ�");
		list.add("�ϴ����");
		list.add("��ͨ���");
		list.add("լ����");
		list.add("��ͨ�ٵ�");
		mySpinner = (Spinner) findViewById(R.id.spinner1);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mySpinner.setAdapter(adapter);
		mySpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				/* ����ѡmySpinner ��ֵ����myTextView �� */
				kuaidiString = adapter.getItem(arg2);
				/* ��mySpinner ��ʾ */
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub

			}

		});
		// �ύ��ť
		bt_subButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if(websign){
				// TODO Auto-generated method stub
				myedittext = (EditText) findViewById(R.id.editText_message_1);
				setContentView(R.layout.wuliu_web);

				bt_return2 = (Button) findViewById(R.id.button4);
				// ���ذ�ť2
				bt_return2.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						finish();
						Intent intent3 = new Intent(WuliuMain.this, WuliuMain.class);
						startActivity(intent3);
						

					}
				});
				webView1 = (WebView) findViewById(R.id.webView1);
				webView1.getSettings().setJavaScriptEnabled(true);
//				Log.d("url", "http://m.kuaidi100.com/index_all.html?type="
//						+ map1.get(kuaidiString) + "&postid="
//						+ myedittext.getText().toString());
				new Thread(new Runnable() {
					public void run() {
						webView1.loadUrl("http://m.kuaidi100.com/index_all.html?type="
								+ map1.get(kuaidiString) + "&postid="
								+ myedittext.getText().toString());
				     }
				}).start();
				webView1.setWebViewClient(new HelloWebViewClient());
			}
			}
		});
		// ���ذ�ť1
		bt_return1.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	// web��ʾ����
	private class HelloWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}
	}
}
