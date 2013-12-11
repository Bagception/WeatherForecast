package de.uniulm.bagception.weatherforecast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.broadcastconstants.BagceptionBroadcastContants;
import de.uniulm.bagception.services.ServiceNames;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements ServiceObservationReactor{

	private TextView mLatitude1;
	private TextView mLatitude2;
	private TextView mLongitude1;
	private TextView mLongitude2;
	private EditText mJsonViewer;
	private ServiceObservationActor observationActor;
	private MyIntentService mIntentService;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIntentService = new MyIntentService();
		observationActor = new ServiceObservationActor(this,ServiceNames.WEATHER_FORECAST_SERVICE);
		
		mLatitude1 = (TextView)findViewById(R.id.lat1TextView);
		mLatitude2 = (TextView)findViewById(R.id.lat2TextView);
		mLongitude1 = (TextView)findViewById(R.id.long1TextView);
		mLongitude2 = (TextView)findViewById(R.id.long2TextView);
		mJsonViewer = (EditText) findViewById(R.id.jsonViewer);
		getWeatherFromCoords(40.00, 40.00);
		
	}

	BroadcastReceiver weatherForecastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			double lat1 = (double) intent.getDoubleExtra(BagceptionBroadcastContants.
					BROADCAST_WEATHERFORECAST_LATITUDE1, 0);
			double lat2 = (double) intent.getDoubleExtra(BagceptionBroadcastContants.
					BROADCAST_WEATHERFORECAST_LATITUDE2, 0);
			double long1 = (double) intent.getDoubleExtra(BagceptionBroadcastContants.
					BROADCAST_WEATHERFORECAST_LONGITUDE1, 0);
			double long2 = (double) intent.getDoubleExtra(BagceptionBroadcastContants.
					BROADCAST_WEATHERFORECAST_LONGITUDE2, 0);
			
			// checks, if just one location point or a area were given
			if(lat2 == 0 || long2 == 0){
				getWeatherFromCoords(lat1, long1);
			}else{
				getWeatherFromCoords(lat1, long1, lat2, long2);
			}
			
			mLatitude1.setText("Latitude1: " + lat1);
			mLatitude2.setText("Latitude2: " + lat2);
			mLongitude1.setText("Longitude1: " + long1);
			mLongitude2.setText("Longitude2: " + long2);
		}
	};
	
	@Override
	protected void onResume() {
		observationActor.register(this);
		super.onResume();
		
		Intent startServiceIntent = new Intent(ServiceNames.WEATHER_FORECAST_SERVICE);
		startService(startServiceIntent);	
		
		{
            IntentFilter f = new IntentFilter();
            f.addAction(BagceptionBroadcastContants.BROADCAST_WEATHERFORECAST_GET_FORECAST);
            registerReceiver(weatherForecastReceiver, f);
		}

	}
	
	@Override
	protected void onPause() {
		observationActor.unregister(this);
		unregisterReceiver(weatherForecastReceiver);
		super.onPause();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public JSONObject getWeatherFromCoords(double lat1, double long1){
		double latOffset = 0.00001d;
		double longOffset = 0.00001d;
		return getWeatherFromCoords(lat1, long1, lat1+latOffset, long1+longOffset);
	}
	
	public JSONObject getWeatherFromCoords(double lat1, double long1, double lat2, double long2){
		
		String uri = "http://openweathermap.org/data/getrect?type=city&lat1="+
						lat1+"&lat2="+lat2+"&lng1="+long1+"&lng2="+long2;
		uri = "http://openweathermap.org/data/getrect?type=city&lat1=48.39&lat2=48.40&lng1=9.99&lng2=10.00";
//		mJsonViewer.setText("hier kommt dann das JSON zum viewen rein...");
		DownloadJSONWeatherForecast task = new DownloadJSONWeatherForecast(this);
		task.execute(uri);
		return null;
	}
	
	public void setWeatherForecast(JSONObject weatherForecastData){
		mJsonViewer.setText(weatherForecastData.toString());
	}
	
	@Override
	public void onServiceStarted(String serviceName) {
	}

	@Override
	public void onServiceStopped(String serviceName) {
	}
	
	
	
	
	private class DownloadJSONWeatherForecast extends AsyncTask<String, Void, JSONObject>{
		
		private MainActivity mainActivity;
		
		public DownloadJSONWeatherForecast(MainActivity mainActivity){
			this.mainActivity = mainActivity;
		}
		@Override
		protected JSONObject doInBackground(String... urls) {
			String response = "";
			JSONObject jsonObject = null;
		      for (String url : urls) {
		        DefaultHttpClient client = new DefaultHttpClient();
		        HttpGet httpGet = new HttpGet(url);
		        try {
		          HttpResponse execute = client.execute(httpGet);
		          InputStream content = execute.getEntity().getContent();

		          BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		          String s = "";
		          while ((s = buffer.readLine()) != null) {
		            response += s;
		          }
		          jsonObject = new JSONObject(response);

		        } catch (Exception e) {
		          e.printStackTrace();
		        }
		      }
		      return jsonObject;
		}
		
		@Override
	    protected void onPostExecute(JSONObject jsonObject) {
			Log.d("jsonObject",jsonObject.toString());
			mainActivity.setWeatherForecast(jsonObject);
			
	    }
		
	}
}

