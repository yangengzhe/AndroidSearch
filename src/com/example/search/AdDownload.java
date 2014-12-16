package com.example.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AdDownload
{
	private ProgressBar mProgress;  
	private boolean interceptFlag = false; 
	private Dialog downloadDialog;
	private Thread downLoadThread;
	private String apkUrl = null;
	private static final String savePath = Environment.getExternalStorageDirectory().getPath()+"/";
	private int progress;
	private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private Context context;
	private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				
				installApk(context);
				break;
			default:
				break;
			}
    	};
    };
	
    public AdDownload(Context context,String urlstr){
    	apkUrl = urlstr;
		this.context = context;
    }
	public void Download(){  //下载对话框
		
        AlertDialog.Builder builder = new Builder(context);  
        builder.setTitle("UC浏览器");  
          
        final LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.progress, null);  
        mProgress = (ProgressBar)v.findViewById(R.id.progress);  
          
        builder.setView(v);  
        builder.setNegativeButton("取消", new OnClickListener() {   
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                interceptFlag = true;  
            }  
        });  
        downloadDialog = builder.create();  
        downloadDialog.show();  
          
        downloadApk(context);  
    }
	
	/** 
     * 下载apk 
     * @param url 
     */  
      
	private Runnable mdownApkRunnable = new Runnable() {      
        @Override  
        public void run() {  
            try {  
                URL url = new URL(apkUrl);  
              
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                conn.connect();  
                int length = conn.getContentLength();  
                InputStream is = conn.getInputStream(); 
                FileOutputStream fos = null;  
                if (is != null) {
                File ApkFile = new File(savePath+ "UCliulanqi.apk");
                
                
                fos = new FileOutputStream(ApkFile);  
                int count = 0;  
                byte buf[] = new byte[1024];  
                  
                do{                   
                    int numread = is.read(buf);  
                    count += numread;  
                    progress =(int)(((float)count / length) * 100);  
                    //更新进度  
                    mHandler.sendEmptyMessage(DOWN_UPDATE);  
                    if(numread <= 0){      
                        //下载完成通知安装  
                        mHandler.sendEmptyMessage(DOWN_OVER);  
                        break;  
                    }  
                    fos.write(buf,0,numread);  
                }while(!interceptFlag);//点击取消就停止下载.  
                }
                fos.close();  
                is.close();  
            } catch (MalformedURLException e) {  
                e.printStackTrace();  
            } catch(IOException e){  
                e.printStackTrace();  
            }  
              
        }  
    };  
    
    private void downloadApk(Context context){  
    	// 检测sd是否可用
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(context.getApplicationContext(), "当前没有SD卡，无法下载",
					Toast.LENGTH_LONG).show();
			return;
		}
//		Log.d("111",apkUrl);
        downLoadThread = new Thread(mdownApkRunnable);  
        downLoadThread.start();  
    } 
    
	 /**
     * 安装apk
     * @param url
     */
	private void installApk(Context context){
		File apkfile = new File(savePath+ "UCliulanqi.apk");
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        context.startActivity(i);
	
	}
}
