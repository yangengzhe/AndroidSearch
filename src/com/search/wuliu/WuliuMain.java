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
	private Boolean websign = false;//网络是否连接判断

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.wuliu_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title_main);

		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("物流查询");
		
		bt_subButton = (Button) findViewById(R.id.button_face_2);
		bt_return1 = (Button) findViewById(R.id.button_face_1);
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
				
		// 建立快递对应的地图
		map1.put("ems快递", "ems");
		map1.put("申通", "shentong");
		map1.put("顺丰", "shunfeng");
		map1.put("天地华宇", "tiandihuayu");
		map1.put("天天快递", "tiantian");
		map1.put("圆通速递", "yuantong");
		map1.put("韵达快运", "yunda");
		map1.put("运通快递", "yuntongkuaidi");
		map1.put("宅急送", "zhaijisong");
		map1.put("中通速递", "zhongtong");
		// 快递的下拉菜单
		list.add("ems快递");
		list.add("申通");
		list.add("顺丰");
		list.add("天地华宇");
		list.add("天天快递");
		list.add("圆通速递");
		list.add("韵达快运");
		list.add("运通快递");
		list.add("宅急送");
		list.add("中通速递");
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
				/* 将所选mySpinner 的值带入myTextView 中 */
				kuaidiString = adapter.getItem(arg2);
				/* 将mySpinner 显示 */
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub

			}

		});
		// 提交按钮
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
				// 返回按钮2
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
		// 返回按钮1
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

	// web显示的类
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
