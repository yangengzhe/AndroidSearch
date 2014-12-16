package com.example.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.search.about.AboutMain;
import com.search.chat.ChatMain;
import com.search.compass.CompassMain;
import com.search.face.FaceMain;
import com.search.guishu.GuishuMain;
import com.search.message.MessageMain;
import com.search.robot.RobotMain;
import com.search.weather.TianqiMain;
import com.search.weather.get_id;
import com.search.wuliu.WuliuMain;

public class MainActivity extends Activity
{
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;
	private ImageView iv_4;
	private ImageView iv_5;
	private ImageView iv_6;
	private ImageView iv_7;
	private ImageView iv_8;
	private ImageView iv_9;
	private update update1 = null;
	private TextView tv_gonggaoTextView;
	private ImageView iv_ad;
	private String string_ad = null;
	private Boolean web_sign = true;
	final Handler handler = new Handler() {  
	    @Override  
	    // ������Ϣ���ͳ�����ʱ���ִ��Handler���������  
	    public void handleMessage(Message msg) {  
	        super.handleMessage(msg);  
	        // ����UI  
			switch (msg.what){
			case 1:// �����°汾
				update1.doNewVersionUpdate(MainActivity.this);
				break;
			case 3:// ����û����
				update1.NoNetShow(MainActivity.this);
				break;
			}
			
	    }  
	};  

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// �������⣬������
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.res_title_main);

		tv_gonggaoTextView = (TextView) findViewById(R.id.textView_main_gonggao);

		// ���4.0���������̲߳�������������
		update1 = new update(this);
		new Thread(new Runnable() {
			public void run() {
				// ��ȡ������Ϣ
				try
				{
					String string_gonggao;
					string_gonggao = get_id
							.GetHtml("http://www.i3geek.com/1/gonggao.html");
					if (string_gonggao == null)
						string_gonggao = "��ӭ��ʹ������С���֣�";
					tv_gonggaoTextView.setText(string_gonggao);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					tv_gonggaoTextView.setText("��ӭ��ʹ������С���֣�");
					e.printStackTrace();
				}
				// ������
				int web_state = 0;
				web_state=update1.check(false);
				if (web_state == 3)
				{
					web_sign = false;// û�����ı�־
				}
				handler.sendEmptyMessage(web_state); 
		     }
		}).start();
		

		// ������
		iv_ad = (ImageView) findViewById(R.id.imageView_ad);
		iv_ad.setVisibility(View.GONE);
		try
		{
			string_ad = get_id.GetHtml("http://www.i3geek.com/1/ad.html");
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (string_ad != null)
		{
			iv_ad.setVisibility(View.VISIBLE);
		}
		iv_ad.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				AdDownload adDownload = new AdDownload(MainActivity.this,
						string_ad);
				adDownload.Download();
			}
		});

		// ��ť��Ϣ����
		iv_1 = (ImageView) findViewById(R.id.imageView_tianqi_weather);
		iv_1.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent_1 = new Intent();
				intent_1.setClass(MainActivity.this, WuliuMain.class);
				MainActivity.this.startActivity(intent_1);
			}
		});

		iv_2 = (ImageView) findViewById(R.id.imageView2);
		iv_2.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent_2 = new Intent();
				intent_2.setClass(MainActivity.this, GuishuMain.class);
				MainActivity.this.startActivity(intent_2);
			}
		});

		iv_3 = (ImageView) findViewById(R.id.imageView3);
		iv_3.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (web_sign)
				{
					Intent intent_3 = new Intent();
					intent_3.setClass(MainActivity.this, TianqiMain.class);
					MainActivity.this.startActivity(intent_3);
				} else
				{
					Toast.makeText(getApplicationContext(), "���������磡",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		iv_4 = (ImageView) findViewById(R.id.imageView4);
		iv_4.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent_4 = new Intent();
				intent_4.setClass(MainActivity.this, CompassMain.class);
				MainActivity.this.startActivity(intent_4);
			}
		});

		iv_5 = (ImageView) findViewById(R.id.imageView5);
		iv_5.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent_5 = new Intent();
				intent_5.setClass(MainActivity.this, FaceMain.class);
				MainActivity.this.startActivity(intent_5);
			}
		});

		iv_6 = (ImageView) findViewById(R.id.imageView6);
		iv_6.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (web_sign)
				{
					Intent intent_6 = new Intent();
					intent_6.setClass(MainActivity.this, RobotMain.class);
					MainActivity.this.startActivity(intent_6);
				} else
				{
					Toast.makeText(getApplicationContext(), "���������磡",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		iv_7 = (ImageView) findViewById(R.id.imageView7);
		iv_7.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (web_sign)
				{
					Intent intent_7 = new Intent();
					intent_7.setClass(MainActivity.this, ChatMain.class);
					MainActivity.this.startActivity(intent_7);
				} else
				{
					Toast.makeText(getApplicationContext(), "���������磡",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		iv_8 = (ImageView) findViewById(R.id.imageView8);
		iv_8.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (web_sign)
				{
					Intent intent_8 = new Intent();
					intent_8.setClass(MainActivity.this, MessageMain.class);
					MainActivity.this.startActivity(intent_8);
				} else
				{
					Toast.makeText(getApplicationContext(), "���������磡",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		iv_9 = (ImageView) findViewById(R.id.imageView9);
		iv_9.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent_9 = new Intent();
				intent_9.setClass(MainActivity.this, AboutMain.class);
				MainActivity.this.startActivity(intent_9);
			}
		});

	}

	// �������ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("ȷ��Ҫ�˳���");
			Dialog dialog = new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage(sb.toString())
					// ��������
					.setPositiveButton("ȷ��",// ����ȷ����ť
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									// dialog.dismiss();
									finish();
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int whichButton)
								{
									// ���"ȡ��"��ť֮���˳�����
									dialog.dismiss();
								}
							}).create();// ����
			// ��ʾ�Ի���
			dialog.show();
		}
		// if (keyCode == KeyEvent.KEYCODE_HOME )
		// {
		//
		// }

		return false;

	}
}
