package com.search.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.search.R;

public class ChatMain extends Activity
{
	private EditText showAll;
	private EditText input;
	private Button send;
	private Button returnbutton;
	private TextView tv_title;
	private Boolean websign = false;//网络是否连接判断
	

	String serverIp = "192.200.126.53";
//	String serverIp = "192.168.1.102";
	int port = 999;
	Socket socket = null;
	OutputStream os;
	BufferedWriter bw;
	InputStream is;
	BufferedReader br;

	String Msg = "";

	MyHandler handler = new MyHandler();

	class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// String showMsg;
			showAll.append(Msg);

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.chat_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);
		
		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("聊天室");
		
		showAll = (EditText) findViewById(R.id.edittext_chat_1);
		input = (EditText) findViewById(R.id.edittext_chat_2);
		returnbutton = (Button) findViewById(R.id.button_title_back);
		send = (Button) findViewById(R.id.button_chat_1);

		
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
			showAlert();
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
		
		// 返回按钮
		returnbutton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
//				sendMsg("quit" + "\n");
//				Intent intent3 = new Intent();
//				intent3.setClass(ChatMain.this, MainActivity.class);
//				ChatMain.this.startActivity(intent3);
				finish();
			}
		});
		// 发送按钮
		send.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(websign)
				sendMsg(input.getText().toString() + "\n");
			}
		});

		
	}
//	//监听返回键
//	public boolean onKeyDown(int keyCode, KeyEvent event)  
//    {  
//        if (keyCode == KeyEvent.KEYCODE_BACK )  
//        {  
//        	sendMsg("quit" + "\n");
//			Intent intent3 = new Intent();
//			intent3.setClass(ChatMain.this, MainActivity.class);
//			ChatMain.this.startActivity(intent3);
//  
//        }  
//        if (keyCode == KeyEvent.KEYCODE_HOME )  
//        {  
//        	sendMsg("quit" + "\n");
//			Intent intent3 = new Intent();
//			intent3.setClass(ChatMain.this, MainActivity.class);
//			ChatMain.this.startActivity(intent3);
//  
//        }  
//          
//        return false;  
//          
//    }  

	// 显示提示
	public void showAlert()
	{

		LinearLayout ll = new LinearLayout(ChatMain.this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv = new TextView(ChatMain.this);
		tv.setText("请输入昵称: ");
		final EditText et = new EditText(ChatMain.this);
		et.setWidth(160);
		ll.addView(tv);
		ll.addView(et);

		AlertDialog alertDialog = new AlertDialog.Builder(ChatMain.this)
		// 设置标题使用给定的资源ID,app_about
				.setTitle("聊天设置")
				// 设置一个监听时要调用对话框的积极按钮被按下
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialogInterface, int i)
					{
								sendMsg("start:" + et.getText().toString() + "\n");
						
					}
					// 创建一个提供给此建设者和显示（参数AlertDialog）的对话
				}).create();
		alertDialog.setView(ll);
		alertDialog.show();
	}

	@Override
	public void finish()
	{
		// TODO Auto-generated method stub
		super.finish();
		
		if(websign){
		sendMsg("quit" + "\n");}
//		Intent intent3 = new Intent();
//		intent3.setClass(ChatMain.this, MainActivity.class);
//		ChatMain.this.startActivity(intent3);
	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		
		if(websign){
		sendMsg("quit" + "\n");}
		finish();
	}

	// 发信息
	public void sendMsg(String msg)
	{

		try
		{
			if (socket == null)
			{
				socket = new Socket(serverIp, port);
				is = socket.getInputStream();
				os = socket.getOutputStream();
				bw = new BufferedWriter(new OutputStreamWriter(os));
				br = new BufferedReader(new InputStreamReader(is));

				new Thread()
				{
					public void run()
					{

						while (true)
						{
							// System.out.println("gggggggggg");

							try
							{
								byte[] b = new byte[is.available()];
								if (is.read(b) != -1)
								{
									// String s1 = new String(b,"utf-8");
									// String s2 = new String(b,"gb2312");
									// String s3 = new String(b,"gbk");
									if (b.length != 0)
									{
										Msg = new String(b, "gbk") + "\n";
										handler.sendEmptyMessage(0);
									}

								}
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					};
				}.start();
			}
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			bw.write(msg);
			bw.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
