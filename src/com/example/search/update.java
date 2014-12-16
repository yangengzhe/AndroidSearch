package com.example.search;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class update
{
	private static String apkname;
	private String verName;
	private int verCode;
	private ProgressBar mProgress;  
	private boolean interceptFlag = false;  
	private Dialog downloadDialog;
	private Thread downLoadThread;
	//返回的安装包url
	private String apkUrl = null;
	/* 下载包安装路径 */
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
    
    //构造函数
    public update(Context context) {  
        this.context = context;  
    }  
    //外部接口函数
	public int check(boolean type)//type=false 为自动检测 type=true为手动检测
	{
		
		if (getServerVer())
		{
			int vercode = getVerCode(context); // 用到前面第一节写的方法
			if (verCode > vercode)
			{
				if(type)
				{
				doNewVersionUpdate(context); // 更新新版本
				}
				return 1;// 更新新版本
			} else if(type)
			{
				notNewVersionShow(context); // 提示当前为最新版本
				return 2;// 提示当前为最新版本
			}
		}
		else {//没获取到更新数据
			if(checkweb(context)){
				//有网络
				if(type)
				{
					NoNetShow(context);//手动检查
				}
			}
			else{//没网络
				if(type)
				{
				NoNetShow(context);
				}
				return 3;//彻底没网络
			}

		}
		return 0;//有网络，连接不到更新数据的网页
	}

	private Boolean checkweb(Context context){
		//检测是否连网
		ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  
		if(wifi|internet){  //执行下一步操作  
			return true;
		}else{  
			return false;
		}
	}
	public void NoNetShow(Context context) {// 提示当前无网络
		StringBuffer sb = new StringBuffer();
		sb.append("当前无法连接到网络，本软件将有部分功能无法使用，请检查网络！");
		Dialog dialog = new AlertDialog.Builder(context).setTitle("无法连接")
				.setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	public void notNewVersionShow(Context context) {// 提示当前为最新版本
//		int verCode = getVerCode(context);
		String verName = getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append(",已是最新版,无需更新!");
		Dialog dialog = new AlertDialog.Builder(context).setTitle("软件更新")
				.setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	
	public void doNewVersionUpdate(final Context context) {// 更新新版本
//		int old_verCode = getVerCode(context);
		String old_verName = getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(old_verName);
//		sb.append(" Code:");
//		sb.append(old_verCode);
		sb.append(", 发现新版本:");
		sb.append(verName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();  
				                showDownloadDialog(context);
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	private void showDownloadDialog(Context context){  //下载对话框
        AlertDialog.Builder builder = new Builder(context);  
        builder.setTitle("软件版本更新");  
          
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
//                File file = new File(savePath);  
//                if(!file.exists()){  
//                    file.mkdir();  
//                }
                File ApkFile = new File(savePath+ apkname);
                
                
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
		File apkfile = new File(savePath+ apkname);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        context.startActivity(i);
	
	}
	
//    void downFile(final String url,Context context) {//文件下载
//    	// 检测sd是否可用
//		String sdStatus = Environment.getExternalStorageState();
//		if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
//		{
//			Toast.makeText(context.getApplicationContext(), "当前没有SD卡，无法使用照相功能",
//					Toast.LENGTH_LONG).show();
//			return;
//		}
//    	pBar.show();
//        new Thread() {
//            public void run() {
//                HttpClient client = new DefaultHttpClient();
//                HttpGet get = new HttpGet(url);
//                HttpResponse response;
//                try {
//                    response = client.execute(get);
//                    HttpEntity entity = response.getEntity();
//                    long length = entity.getContentLength();
//                    InputStream is = entity.getContent();
//                    FileOutputStream fileOutputStream = null;
//                    if (is != null) {
//                        File file = new File(
//                                Environment.getExternalStorageDirectory(),
//                                apkname);
//                        fileOutputStream = new FileOutputStream(file);
//                        byte[] buf = new byte[1024];
//                        int ch = -1;
//                        int count = 0;
//                        while ((ch = is.read(buf)) != -1) {
//                            fileOutputStream.write(buf, 0, ch);
//                            count += ch;
//                            if (length > 0) {
//                            }
//                        }
//                    }
//                    fileOutputStream.flush();
//                    if (fileOutputStream != null) {
//                        fileOutputStream.close();
//                    }
//                    down();
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
//    
//    private void down() {//下载函数
//    	Handler handler = null;
//    	handler.post(new Runnable() {  
//            public void run() {  
//                pBar.cancel();  
//                update();  
//            }  
//        });  
//}
//    
//    private void update() {//安装函数
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File(Environment
//                .getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
//                "application/vnd.android.package-archive");
//        startActivity(intent);
//    }

	private int getVerCode(Context context)//获得目前code
	{
		int verCode = -1;
		try
		{
			verCode = context.getPackageManager().getPackageInfo(
					"com.example.search", 0).versionCode;
		} catch (NameNotFoundException e)
		{
			// Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	private String getVerName(Context context)//获得目前型号名字
	{
		String verName = context.getResources().getText(R.string.versionName)
				.toString();
		return verName;
	}

	private boolean getServerVer()// 获得服务器端的版本信息
	{
		try
		// 获取json
		{
			String path = "http://www.i3geek.com/1/version.php";
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
					// 获取后解析
					JSONObject jsonObject = new JSONObject(jsonString.toString()) 
					.getJSONObject("version");
					
					verCode = jsonObject.getInt("verCode");
					apkname = jsonObject.getString("apkname");
					verName = jsonObject.getString("verName");
					apkUrl = "http://www.i3geek.com/1/"+apkname;//更新下载地址
//					saveFileName = savePath +"/"+ apkname;//更新存储地址

//					Log.d("111", verCode+" "+apkname +" "+verName);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
			return false;
		}
		return true;
	}

}
