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
		tv_title.setText("����ʶ��");
		
		bt_returnButton = (Button) findViewById(R.id.button_title_back);
		bt_xiangceButton = (Button) findViewById(R.id.button_face_1);
		bt_xiangjiButton = (Button) findViewById(R.id.button_face_2);
		bt_subButton = (Button) findViewById(R.id.button_face_3);
		ivImageView = (ImageView) findViewById(R.id.imageView_face_1);
		tv_ageTextView = (TextView) findViewById(R.id.textView_face_1);
		tv_sexTextView = (TextView) findViewById(R.id.textView_face_2);
		tv_roseTextView = (TextView) findViewById(R.id.textView_face_3);
		tv_smileTextView = (TextView) findViewById(R.id.textView_face_4);
		
//		// ���4.0���������̲߳�������������
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
		// ���ذ�ť
		bt_returnButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		// ��ᰴť
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
		// �����ť
		bt_xiangjiButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// ���sd�Ƿ����
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
				{
					Toast.makeText(getApplicationContext(), "��ǰû��SD�����޷�ʹ�����๦��",
							Toast.LENGTH_LONG).show();
					return;
				}
				// ����
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "image.jpg"));
				// ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
				
			}
		});
		// �ύ��ť
		bt_subButton.setOnClickListener(new OnClickListener()
		{

			JSONArray result_face;
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (curPicBitmap == null)
				{
					Toast.makeText(getApplicationContext(), "û��ѡ��ͼƬ",
							Toast.LENGTH_LONG).show();
				} else
				{
				progressDialog = ProgressDialog.show(FaceMain.this, "���Ե�...", "��ȡ������...", true);
				new Thread(new Runnable(){
                    @Override
                    public void run() {
                   //��������
                         result=0;
                          try{
                              //Ҫ��ʱ���еĳ���
                        	  httpRequests = new HttpRequests(
          							"8143d77b88c26a202ef4527869d619e8",
          							"oLVsicf69RhsL7b3BnqFw5AzRrvJxI1w");
          					// ͼƬ�Ĵ���ʽ �ٷ���
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
                        
                    //���½���
                         handler.post(new Runnable() {     
                             public void run() {                          
                                 if(result==1)
                                 {
                                	//��ʱ������ɺ󣬽��и��½���
                                	 try
                 					{
                 						
                 						// System.out.println(result);
                 						// json����
                 						
                 						// String session_id = result.getString("session_id");
                 						// ��ȡsession_id
                 						// ѭ����ȡ����face
                 						if(result_face.length() == 0) 
                 							Toast.makeText(getApplicationContext(), "û���ҵ�����", Toast.LENGTH_LONG).show();
                 						for (int i = 0; i < result_face.length(); i++)
                 						{// ÿһ���˵�����
                 							JSONObject jo = result_face.getJSONObject(i);
                 							// ����attribute������
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
                 							// ����position������

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

                 							// ��ͼ
                 							Bitmap newBitmap = Bitmap
                 									.createBitmap(curPicBitmap);
                 							Canvas canvas = new Canvas(newBitmap);
                 							Paint paint = new Paint();
                 							int w = curPicBitmap.getWidth();
                 							int h = curPicBitmap.getHeight();

                 							paint.setAntiAlias(true);// ���û����޾��(��������ÿ��Կ���Ч���ܲ�)
                 							/* ����paint�ġ�style��ΪSTROKE������ */
                 							paint.setStyle(Paint.Style.STROKE);
                 							paint.setColor(Color.RED);
                 							paint.setAlpha(125);
                 							paint.setStrokeWidth(5);
                 							// �����Ŀ�
                 							canvas.drawRect(center_x * w / 100 - width * w
                 									/ 100 / 2, center_y * h / 100 - height * h
                 									/ 100 / 2, center_x * w / 100 + width * w
                 									/ 100 / 2, center_y * h / 100 + height * h
                 									/ 100 / 2, paint);
                 							// ���۾�
                 							canvas.drawCircle(eye_left_x * w / 100, eye_left_y
                 									* h / 100, 3, paint);
                 							canvas.drawCircle(eye_right_x * w / 100,
                 									eye_right_y * h / 100, 3, paint);
                 							// ������
                 							canvas.drawCircle(nose_x * w / 100, nose_y * h
                 									/ 100, 3, paint);
                 							// ����
                 							canvas.drawLine(mouth_left_x * w / 100,
                 									mouth_left_y * h / 100, mouth_right_x * w
                 											/ 100, mouth_right_y * h / 100,
                 									paint);

                 							canvas.save(Canvas.ALL_SAVE_FLAG);
                 							// �洢�ºϳɵ�ͼƬ
                 							canvas.restore();
                 							ivImageView.setImageBitmap(newBitmap);

                 							// ���ֲ���
                 							int s1 = age_value - age_range;
                 							int s2 = age_value + age_range;
                 							tv_ageTextView.setText("��" + s1 + "~" + s2 + "��");
                 							tv_sexTextView.setText("��" + gender_confidence
                 									+ "% " + gender_value);
                 							tv_roseTextView.setText("��" + race_confidence
                 									+ "% " + race_value);
                 							tv_smileTextView.setText("��΢Ц�� " + smiling_value);
                 						}
                 						// System.out.println(result);
                 					} catch (JSONException e)
                 					{
                 						// TODO Auto-generated catch block
                 						e.printStackTrace();
                 					}
                                 }
                                       else
                                           Toast.makeText(getApplication(), "���ݻ�ȡʧ��,������������", Toast.LENGTH_SHORT).show();    
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
				// ��Ƭ��ԭʼ��Դ��ַ
				Uri originalUri = data.getData();
				try
				{
					// ʹ��ContentProviderͨ��URI��ȡԭʼͼƬ
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					if (photo != null)
					{
						// //Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
						curPicBitmap = ImageTools.zoomBitmap(photo,
								photo.getWidth() / 10, photo.getHeight() / 10);
						// //�ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
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
				// �������ڱ��ص�ͼƬȡ������С����ʾ�ڽ�����
				Bitmap bitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory() + "/image.jpg");
				curPicBitmap = ImageTools.zoomBitmap(bitmap,
						bitmap.getWidth() / 10, bitmap.getHeight() / 10);
				// ����Bitmap�ڴ�ռ�ýϴ�������Ҫ�����ڴ棬����ᱨout of memory�쳣
				bitmap.recycle();

				// ���������ͼƬ��ʾ�ڽ����ϣ������浽����
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
