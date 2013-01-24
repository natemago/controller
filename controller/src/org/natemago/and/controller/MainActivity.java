package org.natemago.and.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.xdruid.ui.ActivityDispatcher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;

public class MainActivity extends ActivityDispatcher {
	
	private SensorManager sensorManager;
	
	private boolean sending = false;
	private HttpClient client = new DefaultHttpClient();
	
	@Override
	protected void addScreens() throws Exception {
		super.addScreens();
	}
	
	@Override
	protected void continueOnCreate() throws Exception {
		
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new DefaultHttpClient(cm, params);
		
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors != null){
			for(Sensor s: sensors){
				System.out.println(s.getName());
			}
		}
		
		 sensors = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
			if(sensors != null){
				for(Sensor s: sensors){
					System.out.println(s.getName());
				}
			}
		
		Sensor accelerometer = sensors.get(0);
		sensorManager.registerListener(new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				boolean send = false;
				for(float val: event.values){
					if(val > 1 || val < -1){
						send = true;
						break;
					}
				}
				if(send)
					send(event.values);
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		}, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	
	private synchronized void send(float [] values){
		if(!sending){
			
			sending = true;
			HttpPost post = new HttpPost("http://192.168.1.212:8080/dispatcher-server/pub");
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("values", Arrays.toString(values)));
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				post.setEntity(entity);
				
				AsyncTask<HttpPost, Integer, Integer> at = new AsyncTask<HttpPost, Integer, Integer>(){

					@Override
					protected Integer doInBackground(HttpPost... params) {
						try {
							
							client.execute(params[0]);
							System.out.println("Sent!");
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							
						}
						
						sending = false;
						return null;
					}
					@Override
					protected void onCancelled() {
						//synchronized (MainActivity.this) {
							sending = false;
						//}
					}
				};
				System.out.println("Boom!");
				at.execute(post);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
			}
			
		}
	}
}
