package com.search.compass;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.search.MainActivity;
import com.example.search.R;

public class CompassMain extends Activity implements SensorEventListener
{
	ImageView image;  //ָ����ͼƬ
	private Button button1;
	float currentDegree = 0f; //ָ����ͼƬת���ĽǶ�
	
	SensorManager mSensorManager; //������
	private TextView tv_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.compass_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);
        
        tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("ָ����");
		
        image = (ImageView)findViewById(R.id.imageview_compass_1);
        button1 = (Button)findViewById(R.id.button_title_back);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE); //��ȡ�������
        
//     // ���4.0���������̲߳�������������
//     		String strVer=android.os.Build.VERSION.RELEASE;
//     		strVer=strVer.substring(0,3).trim();
//     		float fv=Float.valueOf(strVer);
//     		if(fv>2.3){
//     		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//     				.detectDiskReads().detectDiskWrites().detectNetwork()
//     				.penaltyLog().build());
//     		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//     				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//     				.penaltyLog().penaltyDeath().build());
//     		}
     		
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
    }
    
    @SuppressWarnings("deprecation")
	@Override 
    protected void onResume(){
    	super.onResume();
    	//ע�������
    	mSensorManager.registerListener(this
    			, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }
    
    //ȡ��ע��
    @Override
    protected void onPause(){
    	mSensorManager.unregisterListener(this);
    	super.onPause();
    	
    }
    
    @Override
    protected void onStop(){
    	mSensorManager.unregisterListener(this);
    	super.onStop();
    	
    }

    //������ֵ�ı�
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
		
	}
    //���ȸı�
	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//��ȡ����event�Ĵ���������
		int sensorType = event.sensor.getType();
		
		switch(sensorType){
		case Sensor.TYPE_ORIENTATION:
			float degree = event.values[0]; //��ȡzת���ĽǶ�
			//������ת����
			RotateAnimation ra = new RotateAnimation(currentDegree,-degree,Animation.RELATIVE_TO_SELF,0.5f
					,Animation.RELATIVE_TO_SELF,0.5f);
		 ra.setDuration(100);//��������ʱ��
		 image.startAnimation(ra);
		 currentDegree = -degree;
		 break;
		
		}
	}
}
