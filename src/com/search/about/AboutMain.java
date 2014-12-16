package com.search.about;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.R;
import com.example.search.update;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AboutMain extends Activity
{
	private Button button1;
	private Button button2;
	private TextView tv_text1;
	private TextView tv_title;
	private update update1 =new update(AboutMain.this);
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.about_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);
		
		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("关于");
		button1= (Button) findViewById(R.id.button_title_back);
		
		button2= (Button) findViewById(R.id.button_about_2);
		tv_text1 = (TextView) findViewById(R.id.textView_about_1);
		
		
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
		//加载数据
				try {
					tv_text1.setText(NetHtmlUtil.getHtml("http://www.i3geek.com/1/about.html"));
				} catch (Exception e) {
					Toast.makeText(AboutMain.this, "更新记录获取失败，请检查网络", Toast.LENGTH_LONG).show();
					e.printStackTrace();
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
		//更新按钮
		button2.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
						update1.check(true); 
			}
		});
		
	}

}
