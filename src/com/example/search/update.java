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
	//���صİ�װ��url
	private String apkUrl = null;
	/* ���ذ���װ·�� */
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
    
    //���캯��
    public update(Context context) {  
        this.context = context;  
    }  
    //�ⲿ�ӿں���
	public int check(boolean type)//type=false Ϊ�Զ���� type=trueΪ�ֶ����
	{
		
		if (getServerVer())
		{
			int vercode = getVerCode(context); // �õ�ǰ���һ��д�ķ���
			if (verCode > vercode)
			{
				if(type)
				{
				doNewVersionUpdate(context); // �����°汾
				}
				return 1;// �����°汾
			} else if(type)
			{
				notNewVersionShow(context); // ��ʾ��ǰΪ���°汾
				return 2;// ��ʾ��ǰΪ���°汾
			}
		}
		else {//û��ȡ����������
			if(checkweb(context)){
				//������
				if(type)
				{
					NoNetShow(context);//�ֶ����
				}
			}
			else{//û����
				if(type)
				{
				NoNetShow(context);
				}
				return 3;//����û����
			}

		}
		return 0;//�����磬���Ӳ����������ݵ���ҳ
	}

	private Boolean checkweb(Context context){
		//����Ƿ�����
		ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  
		if(wifi|internet){  //ִ����һ������  
			return true;
		}else{  
			return false;
		}
	}
	public void NoNetShow(Context context) {// ��ʾ��ǰ������
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�޷����ӵ����磬��������в��ֹ����޷�ʹ�ã��������磡");
		Dialog dialog = new AlertDialog.Builder(context).setTitle("�޷�����")
				.setMessage(sb.toString())// ��������
				.setPositiveButton("ȷ��",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}
	
	public void notNewVersionShow(Context context) {// ��ʾ��ǰΪ���°汾
//		int verCode = getVerCode(context);
		String verName = getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾:");
		sb.append(verName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append(",�������°�,�������!");
		Dialog dialog = new AlertDialog.Builder(context).setTitle("�������")
				.setMessage(sb.toString())// ��������
				.setPositiveButton("ȷ��",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}
	
	
	public void doNewVersionUpdate(final Context context) {// �����°汾
//		int old_verCode = getVerCode(context);
		String old_verName = getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾:");
		sb.append(old_verName);
//		sb.append(" Code:");
//		sb.append(old_verCode);
		sb.append(", �����°汾:");
		sb.append(verName);
//		sb.append(" Code:");
//		sb.append(verCode);
		sb.append(", �Ƿ����?");
		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle("�������")
				.setMessage(sb.toString())
				// ��������
				.setPositiveButton("ȷ��",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();  
				                showDownloadDialog(context);
							}
						})
				.setNegativeButton("�ݲ�����",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ���"ȡ��"��ť֮���˳�����
								dialog.dismiss();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}
	
	private void showDownloadDialog(Context context){  //���ضԻ���
        AlertDialog.Builder builder = new Builder(context);  
        builder.setTitle("����汾����");  
          
        final LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.progress, null);  
        mProgress = (ProgressBar)v.findViewById(R.id.progress);  
          
        builder.setView(v);  
        builder.setNegativeButton("ȡ��", new OnClickListener() {   
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
     * ����apk 
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
                    //���½���  
                    mHandler.sendEmptyMessage(DOWN_UPDATE);  
                    if(numread <= 0){      
                        //�������֪ͨ��װ  
                        mHandler.sendEmptyMessage(DOWN_OVER);  
                        break;  
                    }  
                    fos.write(buf,0,numread);  
                }while(!interceptFlag);//���ȡ����ֹͣ����.  
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
    	// ���sd�Ƿ����
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(context.getApplicationContext(), "��ǰû��SD�����޷�����",
					Toast.LENGTH_LONG).show();
			return;
		}
//		Log.d("111",apkUrl);
        downLoadThread = new Thread(mdownApkRunnable);  
        downLoadThread.start();  
    } 
    
	 /**
     * ��װapk
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
	
//    void downFile(final String url,Context context) {//�ļ�����
//    	// ���sd�Ƿ����
//		String sdStatus = Environment.getExternalStorageState();
//		if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
//		{
//			Toast.makeText(context.getApplicationContext(), "��ǰû��SD�����޷�ʹ�����๦��",
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
//    private void down() {//���غ���
//    	Handler handler = null;
//    	handler.post(new Runnable() {  
//            public void run() {  
//                pBar.cancel();  
//                update();  
//            }  
//        });  
//}
//    
//    private void update() {//��װ����
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File(Environment
//                .getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
//                "application/vnd.android.package-archive");
//        startActivity(intent);
//    }

	private int getVerCode(Context context)//���Ŀǰcode
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

	private String getVerName(Context context)//���Ŀǰ�ͺ�����
	{
		String verName = context.getResources().getText(R.string.versionName)
				.toString();
		return verName;
	}

	private boolean getServerVer()// ��÷������˵İ汾��Ϣ
	{
		try
		// ��ȡjson
		{
			String path = "http://www.i3geek.com/1/version.php";
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
					// ��ȡ�����
					JSONObject jsonObject = new JSONObject(jsonString.toString()) 
					.getJSONObject("version");
					
					verCode = jsonObject.getInt("verCode");
					apkname = jsonObject.getString("apkname");
					verName = jsonObject.getString("verName");
					apkUrl = "http://www.i3geek.com/1/"+apkname;//�������ص�ַ
//					saveFileName = savePath +"/"+ apkname;//���´洢��ַ

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
