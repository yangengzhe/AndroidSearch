package com.example.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class welcome extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		setTheme(R.style.Theme_Welcome);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		Start();// Æô¶¯µÄ»­Ãæ
		
	}

	public void Start()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(2500);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.setClass(welcome.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}.start();
	}
}
