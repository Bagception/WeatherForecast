package de.uniulm.bagception.weatherforecastservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class WeatherForecastService extends IntentService{
	
	private ResultReceiver resultReceiver;

	public WeatherForecastService() {
		super("WeatherForecastService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		resultReceiver = intent.getParcelableExtra("receiverTag");
		double lat1 = intent.getDoubleExtra("lat1",0);
		double long1 = intent.getDoubleExtra("long1",0);
		double lat2 = intent.getDoubleExtra("lat2",0);
		double long2 = intent.getDoubleExtra("long2", 0);
		
		// TODO: check, if arguments = 0 && if 1 or 2 coords
		JSONObject result = getWeatherFromCoords(10, 9);
	}
	
	
	public JSONObject getWeatherFromCoords(double lat1, double long1){
		double latOffset = 0.00001d;
		double longOffset = 0.00001d;
		return getWeatherFromCoords(lat1, long1, lat1+latOffset, long1+longOffset);
	}
	
	public JSONObject getWeatherFromCoords(double lat1, double long1, double lat2, double long2){
		
		String uri = "http://openweathermap.org/data/getrect?type=city&lat1="+
						lat1+"&lat2="+lat2+"&lng1="+long1+"&lng2="+long2;
//		uri = "http://openweathermap.org/data/getrect?type=city&lat1=48.39&lat2=48.40&lng1=9.99&lng2=10.00";
		DownloadJSONWeatherForecast task = new DownloadJSONWeatherForecast(resultReceiver);
		task.execute(uri);
		return null;
	}
	
	
private class DownloadJSONWeatherForecast extends AsyncTask<String, Void, JSONObject>{
		
		private ResultReceiver resultReceiver;
		
		public DownloadJSONWeatherForecast(ResultReceiver resultReceiver){
			this.resultReceiver = resultReceiver;
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
	        Bundle b= new Bundle();
	        b.putString("payload",jsonObject.toString());
	        resultReceiver.send(0, b);
	    }
		
	}

}
