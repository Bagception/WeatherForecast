package de.uniulm.bagception.weatherforecast;


import org.json.JSONObject;

import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.broadcastconstants.BagceptionBroadcastContants;
import de.uniulm.bagception.services.ServiceNames;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		observationActor = new ServiceObservationActor(this,ServiceNames.WEATHER_FORECAST_SERVICE);
		
		mLatitude1 = (TextView)findViewById(R.id.lat1TextView);
		mLatitude2 = (TextView)findViewById(R.id.lat2TextView);
		mLongitude1 = (TextView)findViewById(R.id.long1TextView);
		mLongitude2 = (TextView)findViewById(R.id.long2TextView);
		mJsonViewer = (EditText) findViewById(R.id.jsonViewer);
		
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * returns JSONObject with weather forecast information from given latitude and longitude
	 * @param lat1	
	 * @param long1
	 * @return 
	 */
	public JSONObject getWeatherFromCoords(double lat1, double long1){
		double latOffset = 0.00001d;
		double longOffset = 0.00001d;
		return getWeatherFromCoords(lat1, long1, lat1+latOffset, long1+longOffset);
	}
	
	/**
	 * returns JSONObject with weather forecast information from given latitude and longitude square
	 * @param lat1
	 * @param long1
	 * @param lat2
	 * @param long2
	 * @return
	 */
	public JSONObject getWeatherFromCoords(double lat1, double long1, double lat2, double long2){
		
		String uri = "http://openweathermap.org/data/getrect?type=city&lat1="+
						lat1+"&lat2="+lat2+"&lng1="+long1+"&lng2="+long2;
		uri = "http://openweathermap.org/data/getrect?type=city&lat1=48.39&lat2=48.40&lng1=9.99&lng2=10.00";
		// TODO: JSON request
		mJsonViewer.setText("hier kommt dann das JSON zum viewen rein...");
		return null;
	}

	@Override
	public void onServiceStarted(String serviceName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceStopped(String serviceName) {
		// TODO Auto-generated method stub
		
	}
}
