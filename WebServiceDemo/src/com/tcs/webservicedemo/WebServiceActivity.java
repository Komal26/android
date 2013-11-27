package com.tcs.webservicedemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WebServiceActivity extends Activity implements OnClickListener{

	String TAG = "WebServiceActivity";
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_service);
		Button btn = (Button) findViewById(R.id.my_button);
		btn.setOnClickListener(this);
		dialog = new ProgressDialog(this);
		Log.v(TAG,"onCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.v(TAG,"onClick1");
		Button btn = (Button) findViewById(R.id.my_button);
		btn.setClickable(false);
		new LongRunningGetIO().execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			show();
		}
		
		public void show(){
			dialog.setMessage("Please Wait..");
			dialog.show();
		}
		@Override
		protected String doInBackground(Void... params) {
			Log.v(TAG,"doInBackground");
			HttpClient  httpClient  = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost("http://10.136.36.74:8080/ResourceMgmt/resourcemanagement/");
			ArrayList<NameValuePair> postParam = new ArrayList<NameValuePair>();
			postParam.add(new BasicNameValuePair("body", "{\"opcode\":\"authenticateUser\" ,\"employee_id\":\"307650\", \"password\":\"pwds\"}"));
			String text = null;
			try{
				Log.v(TAG,"doInBackground try");
				httpPost.setEntity(new UrlEncodedFormEntity(postParam));
				HttpResponse response = httpClient.execute(httpPost, localContext);
				HttpEntity entity = response.getEntity();
				text = getASCIIContentFromEntity(entity);
				Log.v(TAG,"doInBackground after try");
			}catch(Exception e){
				Log.v(TAG,"doInBackground catch");
				e.printStackTrace();
				return e.getLocalizedMessage();
			}
			return text;
		}

		protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException{
			Log.v(TAG,"getASCIIContentFromEntity");
			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer(); 
			int n =1;
			while(n>0){
				byte[] b = new byte[4096];
				n = in.read(b);
				if(n>0)
					out.append(new String(b, 0, n));
			}
			return out.toString();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.v(TAG,"onPostExecute");
			hide();
			if(result != null){
				EditText edit = (EditText) findViewById(R.id.my_edit);
				edit.setText(result);
				Log.v(TAG,"onPostExecute  if");
			}
			Button b = (Button) findViewById(R.id.my_button);
			b.setClickable(true);
			Log.v(TAG,"onPostExecute finish");
		}
		
		public void hide(){
			dialog.dismiss();
		}
	}
	
}
