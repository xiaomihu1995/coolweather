package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class HttpUtil {
public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpURLConnection connection = null;
			try {
				URL url = new URL(address);
				connection = (HttpURLConnection)url.openConnection();
				connection.setReadTimeout(8000);
				connection.setConnectTimeout(8000);
				connection.setRequestMethod("GET");
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = "";
				StringBuilder response = new StringBuilder();
				while((line = reader.readLine()) != null){
					response.append(line);
				}
				if(listener != null){
					listener.onFinish(response.toString());
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
				if(listener != null){
					listener.onError(e);
				}
			}finally {
				if(connection != null){
					connection.disconnect();
				}
			}
		}
	}).start();
}
}

