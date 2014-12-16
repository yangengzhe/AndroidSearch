package com.search.face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.MainActivity;
import com.example.search.R;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class FaceMain extends Activity
{

	private HttpRequests httpRequests = null;
	private Bitmap curPicBitmap;

	private Button bt_returnButton;
	private Button bt_xiangjiButton;
	private Button bt_xiangceButton;
	private Button bt_subButton;
	private ImageView ivImageView;
	private TextView tv_ageTextView;
	private TextView tv_sexTextView;
	private TextView tv_roseTextView;
	private TextView tv_smileTextView;
	final public int CHOOSE_PICTURE = 1;
	final public int TAKE_PICTURE = 2;
	private TextView tv_title;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog = null;
	private int result;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.face_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.res_title);
		
		tv_title= (TextView) findViewById(R.id.textview_title);
		tv_title.setText("人脸识别");
		
		bt_returnButton = (Button) findViewById(R.id.button_title_back);
		bt_xiangceButton = (Button) findViewById(R.id.button_face_1);
		bt_xiangjiButton = (Button) findViewById(R.id.button_face_2);
		bt_subButton = (Button) findViewById(R.id.button_face_3);
		ivImageView = (ImageView) findViewById(R.id.imageView_face_1);
		tv_ageTextView = (TextView) findViewById(R.id.textView_face_1);
		tv_sexTextView = (TextView) findViewById(R.id.textView_face_2);
		tv_roseTextView = (TextView) findViewById(R.id.textView_face_3);
		tv_smileTextView = (TextView) findViewById(R.id.textView_face_4);
		
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
		// 返回按钮
		bt_returnButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		// 相册按钮
		bt_xiangceButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent.setType("image/*");
				startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
			}
		});
		// 相机按钮
		bt_xiangjiButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// 检测sd是否可用
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
				{
					Toast.makeText(getApplicationContext(), "当前没有SD卡，无法使用照相功能",
							Toast.LENGTH_LONG).show();
					return;
				}
				// 照相
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "image.jpg"));
				// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
				
			}
		});
		// 提交按钮
		bt_subButton.setOnClickListener(new OnClickListener()
		{

			JSONArray result_face;
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (curPicBitmap == null)
				{
					Toast.makeText(getApplicationContext(), "没有选择图片",
							Toast.LENGTH_LONG).show();
				} else
				{
				progressDialog = ProgressDialog.show(FaceMain.this, "请稍等...", "获取数据中...", true);
				new Thread(new Runnable(){
                    @Override
                    public void run() {
                   //加载数据
                         result=0;
                          try{
                              //要耗时运行的程序
                        	  httpRequests = new HttpRequests(
          							"8143d77b88c26a202ef4527869d619e8",
          							"oLVsicf69RhsL7b3BnqFw5AzRrvJxI1w");
          					// 图片的处理方式 官方的
          					ByteArrayOutputStream stream = new ByteArrayOutputStream();
          					float scale = Math.min(1, Math.min(
          							600f / curPicBitmap.getWidth(),
          							600f / curPicBitmap.getHeight()));
          					Matrix matrix = new Matrix();
          					matrix.postScale(scale, scale);

          					Bitmap imgSmall = Bitmap.createBitmap(curPicBitmap, 0, 0,
          							curPicBitmap.getWidth(), curPicBitmap.getHeight(),
          							matrix, false);

          					imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
          					JSONObject js_result = httpRequests
    								.detectionDetect(new PostParameters()
    										.setImg(stream.toByteArray()));
          					result_face = js_result.getJSONArray("face");
                             result=1;
                          }
                         catch(Exception ex){
                             result=-1; 
                         }           
                        
                    //更新界面
                         handler.post(new Runnable() {     
                             public void run() {                          
                                 if(result==1)
                                 {
                                	//耗时程序完成后，进行更新界面
                                	 try
                 					{
                 						
                 						// System.out.println(result);
                 						// json解析
                 						
                 						// String session_id = result.getString("session_id");
                 						// 获取session_id
                 						// 循环获取数组face
                 						if(result_face.length() == 0) 
                 							Toast.makeText(getApplicationContext(), "没有找到人脸", Toast.LENGTH_LONG).show();
                 						for (int i = 0; i < result_face.length(); i++)
                 						{// 每一个人的数据
                 							JSONObject jo = result_face.getJSONObject(i);
                 							// 解析attribute内内容
                 							JSONObject jo_attribute = jo
                 									.getJSONObject("attribute");
                 							JSONObject jo_attribute_age = jo_attribute
                 									.getJSONObject("age");
                 							int age_range = jo_attribute_age.getInt("range");
                 							int age_value = jo_attribute_age.getInt("value");
                 							JSONObject jo_attribute_gender = jo_attribute
                 									.getJSONObject("gender");
                 							int gender_confidence = jo_attribute_gender
                 									.getInt("confidence");
                 							String gender_value = jo_attribute_gender
                 									.getString("value");

                 							// JSONObject jo_attribute_glass = jo_attribute
                 							// .getJSONObject("glass");
                 							// int glass_confidence = jo_attribute_glass
                 							// .getInt("confidence");
                 							// String glass_value = jo_attribute_glass
                 							// .getString("value");

                 							// JSONObject jo_attribute_pose = jo_attribute
                 							// .getJSONObject("pose");
                 							// JSONObject jo_attribute_pose_pitch_angle =
                 							// jo_attribute_pose
                 							// .getJSONObject("pitch_angle");
                 							// int pose_pitch_angle_value =
                 							// jo_attribute_pose_pitch_angle
                 							// .getInt("value");
                 							// JSONObject jo_attribute_pose_roll_angle =
                 							// jo_attribute_pose
                 							// .getJSONObject("roll_angle");
                 							// int pose_roll_angle_value =
                 							// jo_attribute_pose_roll_angle
                 							// .getInt("value");
                 							// JSONObject jo_attribute_pose_yaw_angle =
                 							// jo_attribute_pose
                 							// .getJSONObject("yaw_angle");
                 							// int pose_yaw_angle_value =
                 							// jo_attribute_pose_yaw_angle
                 							// .getInt("value");
                 							JSONObject jo_attribute_race = jo_attribute
                 									.getJSONObject("race");
                 							int race_confidence = jo_attribute_race
                 									.getInt("confidence");
                 							String race_value = jo_attribute_race
                 									.getString("value");
                 							JSONObject jo_attribute_smiling = jo_attribute
                 									.getJSONObject("smiling");
                 							int smiling_value = jo_attribute_smiling
                 									.getInt("value");
                 							// 解析position内内容

                 							JSONObject jo_position = jo
                 									.getJSONObject("position");
                 							JSONObject jo_position_center = jo_position
                 									.getJSONObject("center");
                 							int center_x = jo_position_center.getInt("x");
                 							int center_y = jo_position_center.getInt("y");
                 							JSONObject jo_position_eye_left = jo_position
                 									.getJSONObject("eye_left");
                 							int eye_left_x = jo_position_eye_left.getInt("x");
                 							int eye_left_y = jo_position_eye_left.getInt("y");
                 							JSONObject jo_position_eye_right = jo_position
                 									.getJSONObject("eye_right");
                 							int eye_right_x = jo_position_eye_right.getInt("x");
                 							int eye_right_y = jo_position_eye_right.getInt("y");
                 							JSONObject jo_position_mouth_left = jo_position
                 									.getJSONObject("mouth_left");
                 							int mouth_left_x = jo_position_mouth_left
                 									.getInt("x");
                 							int mouth_left_y = jo_position_mouth_left
                 									.getInt("y");
                 							JSONObject jo_position_mouth_right = jo_position
                 									.getJSONObject("mouth_right");
                 							int mouth_right_x = jo_position_mouth_right
                 									.getInt("x");
                 							int mouth_right_y = jo_position_mouth_right
                 									.getInt("y");
                 							JSONObject jo_position_nose = jo_position
                 									.getJSONObject("nose");
                 							int nose_x = jo_position_nose.getInt("x");
                 							int nose_y = jo_position_nose.getInt("y");
                 							int height = jo_position.getInt("height");
                 							int width = jo_position.getInt("width");

                 							// 画图
                 							Bitmap newBitmap = Bitmap
                 									.createBitmap(curPicBitmap);
                 							Canvas canvas = new Canvas(newBitmap);
                 							Paint paint = new Paint();
                 							int w = curPicBitmap.getWidth();
                 							int h = curPicBitmap.getHeight();

                 							paint.setAntiAlias(true);// 设置画笔无锯齿(如果不设置可以看到效果很差)
                 							/* 设置paint的　style　为STROKE：空心 */
                 							paint.setStyle(Paint.Style.STROKE);
                 							paint.setColor(Color.RED);
                 							paint.setAlpha(125);
                 							paint.setStrokeWidth(5);
                 							// 画脸的框
                 							canvas.drawRect(center_x * w / 100 - width * w
                 									/ 100 / 2, center_y * h / 100 - height * h
                 									/ 100 / 2, center_x * w / 100 + width * w
                 									/ 100 / 2, center_y * h / 100 + height * h
                 									/ 100 / 2, paint);
                 							// 画眼睛
                 							canvas.drawCircle(eye_left_x * w / 100, eye_left_y
                 									* h / 100, 3, paint);
                 							canvas.drawCircle(eye_right_x * w / 100,
                 									eye_right_y * h / 100, 3, paint);
                 							// 画鼻子
                 							canvas.drawCircle(nose_x * w / 100, nose_y * h
                 									/ 100, 3, paint);
                 							// 画嘴
                 							canvas.drawLine(mouth_left_x * w / 100,
                 									mouth_left_y * h / 100, mouth_right_x * w
                 											/ 100, mouth_right_y * h / 100,
                 									paint);

                 							canvas.save(Canvas.ALL_SAVE_FLAG);
                 							// 存储新合成的图片
                 							canvas.restore();
                 							ivImageView.setImageBitmap(newBitmap);

                 							// 文字部分
                 							int s1 = age_value - age_range;
                 							int s2 = age_value + age_range;
                 							tv_ageTextView.setText("：" + s1 + "~" + s2 + "岁");
                 							tv_sexTextView.setText("：" + gender_confidence
                 									+ "% " + gender_value);
                 							tv_roseTextView.setText("：" + race_confidence
                 									+ "% " + race_value);
                 							tv_smileTextView.setText("：微笑度 " + smiling_value);
                 						}
                 						// System.out.println(result);
                 					} catch (JSONException e)
                 					{
                 						// TODO Auto-generated catch block
                 						e.printStackTrace();
                 					}
                                 }
                                       else
                                           Toast.makeText(getApplication(), "数据获取失败,请检查网络连接", Toast.LENGTH_SHORT).show();    
                                 }                
                             });
                         progressDialog.dismiss();
                    }}).start();
				
				}
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try
				{
					// 使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					if (photo != null)
					{
						// //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						curPicBitmap = ImageTools.zoomBitmap(photo,
								photo.getWidth() / 10, photo.getHeight() / 10);
						// //释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						ivImageView.setImageBitmap(curPicBitmap);
					}
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case TAKE_PICTURE:
				// 将保存在本地的图片取出并缩小后显示在界面上
				Bitmap bitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory() + "/image.jpg");
				curPicBitmap = ImageTools.zoomBitmap(bitmap,
						bitmap.getWidth() / 10, bitmap.getHeight() / 10);
				// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
				bitmap.recycle();

				// 将处理过的图片显示在界面上，并保存到本地
				ivImageView.setImageBitmap(curPicBitmap);
				ImageTools.savePhotoToSDCard(curPicBitmap, Environment
						.getExternalStorageDirectory().getAbsolutePath(),
						String.valueOf(System.currentTimeMillis()));
				break;
			default:
				break;
			}
		}
	}

}
