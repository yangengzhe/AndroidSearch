package com.search.weather;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.R;

public class TianqiMain extends Activity
{
	private TextView tv_title;
	private List<String> list_1 = new ArrayList<String>();
	private ArrayAdapter<String> adapter_1;
	private Map<String, Integer> map_1 = new HashMap<String, Integer>();
	private List<String> list_2 = new ArrayList<String>();
	private ArrayAdapter<String> adapter_2;
	private Map<String, String> map_2 = new HashMap<String, String>();
	private Spinner s_1Spinner;
	private Spinner s_2Spinner;
	private Button bt_subButton;
	private Button bt_return;
	private Button bt_subButton2;
	private String jsonString = "";// 提交后获得的json

	private TextView tv_city;
	private TextView tv_tem;
	private TextView tv_weather;
	private TextView tv_time;
	private ImageView iv_weather;

	private ProgressDialog progressDialog = null;
	private Handler handler = new Handler();
	private int result;
	private String imageUrl;
	private Bitmap temp_bitBitmap;

	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tianqi_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.res_title);

		tv_title = (TextView) findViewById(R.id.textview_title);
		tv_title.setText("天气查询");

		s_1Spinner = (Spinner) findViewById(R.id.spinner_tianqi_1);
		s_2Spinner = (Spinner) findViewById(R.id.spinner_tianqi_2);
		bt_subButton = (Button) findViewById(R.id.button_tianqi_1);
		bt_subButton2 = (Button) findViewById(R.id.button_tianqi_2);
		bt_return = (Button) findViewById(R.id.button_title_back);
		tv_city = (TextView) findViewById(R.id.textView_tianqi_city);
		tv_tem = (TextView) findViewById(R.id.textView_tianqi_tem);
		tv_weather = (TextView) findViewById(R.id.textView_tianqi_weather);
		tv_time = (TextView) findViewById(R.id.textView_tianqi_time);
		iv_weather = (ImageView) findViewById(R.id.imageView_tianqi_weather);
		// Log.d("111", city_id.city_id);
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
				//获取数据
		JSONObject jsonObject;
		try
		{
			jsonObject = new JSONObject(city_id.city_id)
					.getJSONObject("weather");
			final JSONArray jsonArray = jsonObject.getJSONArray("城市代码");
			for (int i = 0; i < jsonArray.length(); i++)
			{// 省下拉框
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				list_1.add(jsonObject2.getString("省"));
				map_1.put(jsonObject2.getString("省"), i);
				// Log.d("111", jsonObject2.getString("省"));
			}
			adapter_1 = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list_1);
			adapter_2 = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list_2);
			adapter_1
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapter_2
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			s_1Spinner.setAdapter(adapter_1);

			s_1Spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3)
				{

					// TODO Auto-generated method stub
					String temp_str = adapter_1.getItem(arg2);
					arg0.setVisibility(View.VISIBLE);

					JSONObject jsonObject3 = (JSONObject) jsonArray.opt(map_1
							.get(temp_str));
					try
					{// 城市下拉框
						list_2.clear();
						map_2.clear();
						JSONArray jsonArray2;
						jsonArray2 = jsonObject3.getJSONArray("市");
						for (int i = 0; i < jsonArray2.length(); i++)
						{
							JSONObject jsonObject2 = (JSONObject) jsonArray2
									.opt(i);
							list_2.add(jsonObject2.getString("市名"));
							map_2.put(jsonObject2.getString("市名"),
									jsonObject2.getString("编码"));
						}

					} catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					s_2Spinner.setAdapter(adapter_2);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{
					// TODO Auto-generated method stub

				}

			});

		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 提交按钮
		bt_subButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (map_2.get(s_2Spinner.getSelectedItem().toString()) != null)
				{
					progressDialog = ProgressDialog.show(TianqiMain.this,
							"请稍等...", "获取数据中...", true);
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							// 加载数据
							result = 0;

							// 显示天气
							try
							// 获取json
							{
								String path = "http://www.weather.com.cn/data/cityinfo/"
										+ map_2.get(s_2Spinner
												.getSelectedItem().toString())
										+ ".html";
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
										while ((len = connection
												.getInputStream().read(data)) != -1)
										{
											outPutStream.write(data, 0, len);
										}
										jsonString = new String(outPutStream
												.toByteArray());
										result = 1;
										
										JSONObject jsonObject;
										try
										{
											jsonObject = new JSONObject(
													jsonString)
													.getJSONObject("weatherinfo");
											// 加载天气图片
											String newimgString = jsonObject
													.getString("img1")
													.substring(1);
											imageUrl = "http://m.weather.com.cn/img/b"
													+ newimgString;
											temp_bitBitmap =returnBitMap(imageUrl);
											
										} catch (JSONException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									} catch (Exception e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
										result = -1;
									}
								}
							} catch (Exception e)
							{
								// TODO: handle exception
								result = -1;
							}

							// 更新界面
							handler.post(new Runnable()
							{
								public void run()
								{
									if (result == 1)
									{
										// 解析json
										JSONObject jsonObject;
										try
										{
											jsonObject = new JSONObject(
													jsonString)
													.getJSONObject("weatherinfo");

											tv_city.setText(jsonObject
													.getString("city"));
											tv_tem.setText(jsonObject
													.getString("temp2")
													+ "~"
													+ jsonObject
															.getString("temp1"));
											tv_weather.setText(jsonObject
													.getString("weather"));
											tv_time.setText(jsonObject
													.getString("ptime"));
											// 显示天气图片
											iv_weather
											.setImageBitmap(temp_bitBitmap);
											
											
										} catch (JSONException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else
										Toast.makeText(getApplication(),
												"数据获取失败,请检查网络连接",
												Toast.LENGTH_SHORT).show();
								}
							});
							progressDialog.dismiss();
						}
					}).start();

				} else
				{
					Toast.makeText(getApplicationContext(), "请选择城市",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// 获取当前天气按钮
		bt_subButton2.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				progressDialog = ProgressDialog.show(TianqiMain.this, "请稍等...",
						"获取数据中...", true);

				new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						// 加载数据
						result = 0;
						try
						{
							// 下载文件
							String newStr = get_id
									.GetHtml("http://www.i3geek.com/1/getcityid.php");
							// String newStr =
							// string.substring(string.indexOf("id=") +
							// 3,string.indexOf(";if"));

							// 显示天气
							try
							// 获取json
							{
								String path = "http://www.weather.com.cn/data/cityinfo/"
										+ newStr + ".html";
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
										while ((len = connection
												.getInputStream().read(data)) != -1)
										{
											outPutStream.write(data, 0, len);
										}
										jsonString = new String(outPutStream
												.toByteArray());

									} catch (Exception e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} catch (Exception e)
							{
								// TODO: handle exception
							}

							result = 1;
							JSONObject jsonObject;
							try
							{
								jsonObject = new JSONObject(
										jsonString)
										.getJSONObject("weatherinfo");
								// 加载天气图片
								String newimgString = jsonObject
										.getString("img1")
										.substring(1);
								imageUrl = "http://m.weather.com.cn/img/b"
										+ newimgString;
								temp_bitBitmap =returnBitMap(imageUrl);
								
							} catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (Exception ex)
						{
							result = -1;
						}

						// 更新界面
						// Update the progress bar
						handler.post(new Runnable()
						{
							public void run()
							{
								if (result == 1)
								{
									// 解析json
									JSONObject jsonObject;
									try
									{
										jsonObject = new JSONObject(jsonString)
												.getJSONObject("weatherinfo");

										tv_city.setText(jsonObject
												.getString("city"));
										tv_tem.setText(jsonObject
												.getString("temp2")
												+ "~"
												+ jsonObject.getString("temp1"));
										tv_weather.setText(jsonObject
												.getString("weather"));
										tv_time.setText(jsonObject
												.getString("ptime"));
										// 显示天气图片
										iv_weather
										.setImageBitmap(temp_bitBitmap);
									} catch (JSONException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else
									Toast.makeText(getApplication(),
											"连接失败,请检查网络连接", Toast.LENGTH_SHORT)
											.show();
							}
						});

						progressDialog.dismiss();
					}
				}).start();

			}
		});
		// 返回按钮
		bt_return.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	// 返回BitMap图片函数
	public Bitmap returnBitMap(String url)
	{
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try
		{
			myFileUrl = new URL(url);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		try
		{
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}
}
