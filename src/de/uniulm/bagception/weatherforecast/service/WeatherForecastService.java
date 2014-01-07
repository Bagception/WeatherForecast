package de.uniulm.bagception.weatherforecast.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class WeatherForecastService extends IntentService {

	private ResultReceiver resultReceiver;

	public WeatherForecastService() {
		super("WeatherForecastService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		resultReceiver = intent.getParcelableExtra("receiverTag");
		Log.d("SERVICE", "event handled");
		double lat = intent.getDoubleExtra("lat", 0);
		double lng = intent.getDoubleExtra("lng", 0);
		String unit = intent.getStringExtra("unit");
		
		if(unit!=null){
			unit = "&units="+ unit;
		}else{
			unit = "&units=metric";
		}
		
		String address = "http://api.openweathermap.org/data/2.5/find?";
		String uri = address+"lat="+lat+"&lon="+lng+unit;
		DownloadJSONWeatherForecast task = new DownloadJSONWeatherForecast(resultReceiver);
		task.execute(uri);
	}


	private class DownloadJSONWeatherForecast extends
			AsyncTask<String, Void, JSONObject> {

		private ResultReceiver resultReceiver;

		public DownloadJSONWeatherForecast(ResultReceiver resultReceiver) {
			this.resultReceiver = resultReceiver;
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			String response = "";
			JSONObject jsonObject = null;
			JSONObject answer = null;
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
					jsonObject = new JSONObject(response);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				
				answer = new JSONObject();
				JSONObject list = jsonObject.getJSONArray("list").getJSONObject(0);
				JSONObject main = list.getJSONObject("main");
				JSONObject wind = list.getJSONObject("wind");
				JSONObject rain = list.getJSONObject("rain");
				JSONObject clouds = list.getJSONObject("clouds");
				
				answer.put("city", list.getString("name"));
				answer.put("temp", main.getString("temp"));
				answer.put("tempMin", main.getString("temp_min"));
				answer.put("tempMax", main.getString("temp_max"));
				answer.put("wind", wind.getString("speed"));
				answer.put("rain", rain.getString("3h"));
				answer.put("clouds", clouds.getString("all"));

				
				Log.d("answer", answer.toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return answer;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			Bundle b = new Bundle();
			b.putString("payload", jsonObject.toString());
			resultReceiver.send(0, b);
		}

	}

}
