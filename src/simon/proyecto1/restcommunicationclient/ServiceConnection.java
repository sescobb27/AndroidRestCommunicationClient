package simon.proyecto1.restcommunicationclient;

/**
 * Created by simon on 8/25/13.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

public class ServiceConnection/* extends AsyncTask<Void, Integer, Long>*/{

    private final String INDEX_URL   = "/posts.json";
    private final String UPDATE_URL  = "/post/:id.json";
    private final String CREATE_URL  = "/posts.json";
    private final String DESTROY_URL = "/posts/:id.json";
    private final String SERVER_URL  = "http://192.168.1.2";
    //private final String SERVER_URL  = "http://10.0.44.84";
    private final String PORT = ":3000";
    private       StringBuilder REQUEST_URL;

    private static ServiceConnection service = null;

    public static final int INDEX   = 1;
    public static final int CREATE  = 2;
    public static final int UPDATE  = 3;
    public static final int DESTROY = 4;
    private             int ACTION  = 1;
    private JSONObject params;
    private JSONArray response;
    private LinkedList<ServiceSuscriptor> suscriptors;
    
    private HttpGet get;
    // private HttpPost post;
    private HttpPut update;
    private HttpDelete delete;
    private HttpClient client;
    private HttpResponse client_response;

    private ServiceConnection() {
    	client = new DefaultHttpClient();
    }

    public static ServiceConnection getServiceConnection() {
        if ( service == null ) {
            service = new ServiceConnection();
        }
        return  service;
    }
    
    public void suscribe(ServiceSuscriptor suscriptor)
    {
    	if(suscriptors == null)
    	{
    		suscriptors = new LinkedList<ServiceSuscriptor>();
    	}
    	suscriptors.add(suscriptor);
    }
    
    public void unsuscribe(ServiceSuscriptor suscriptor)
    {
    	if(suscriptors != null && suscriptors.contains(suscriptor))
    		suscriptors.remove(suscriptor);
    }
    
    private void notifySuscriptors() throws Exception
    {
    	for (ServiceSuscriptor suscriptor : suscriptors)
    	{
    		if(ACTION == INDEX)
    			suscriptor.responseObtained(response);
    		else
    			suscriptor.responseObtained(response.getJSONObject(0));
    	}
    }

    public void setURL( int url ) throws Exception
    {
        ACTION = url;
        if ( REQUEST_URL == null || !REQUEST_URL.toString().equals( SERVER_URL + PORT) )
        {
            REQUEST_URL = new StringBuilder( SERVER_URL );
            REQUEST_URL.append( PORT );
        }
        switch ( ACTION ){
            case INDEX:
                REQUEST_URL.append( INDEX_URL );
                break;
            case CREATE:
                REQUEST_URL.append( CREATE_URL );
                break;
            case UPDATE:
                REQUEST_URL.append( UPDATE_URL );
                break;
            case DESTROY:
                REQUEST_URL.append( DESTROY_URL );
                break;
            default:
            	throw new IllegalStateException("Invalid Action, only GET, PUT, POST, DELETE, and you send "+ACTION);
        }
    }

    public void setPostId(int id)
    {
        if ( ACTION != INDEX || ACTION != CREATE )
        {
            int colon = REQUEST_URL.indexOf( ":" );
            if ( colon != -1 )
                REQUEST_URL.replace( colon, colon+3, String.valueOf( id ) );
        }
    }

    public void sendRequest(Map<String, String> req_params) throws Exception
    {
        switch ( ACTION )
        {
            case INDEX:
            	get = new HttpGet(REQUEST_URL.toString());
                break;
            case CREATE:
            	//post = new HttpPost(REQUEST_URL.toString());
                break;
            case UPDATE:
                update = new HttpPut(REQUEST_URL.toString());
                break;
            case DESTROY:
            	delete = new HttpDelete(REQUEST_URL.toString());
                break;
            default:
                throw new IllegalStateException("Invalid Action, only GET, PUT, POST, DELETE, and you send "+ACTION);
        }
        if ( ACTION == CREATE )
            params = new JSONObject(req_params);
        // service.execute();
        AsyncTaskRunnable async_task = new AsyncTaskRunnable();
        async_task.execute();
    }

    private void executeAndGetResponse(  ) throws Exception
    {
    	String resp;
    	switch ( ACTION )
        {
            case INDEX:
            	get.setHeader("content-type", "application/json");
            	client_response = client.execute(get);
                break;
            case CREATE:
                /*StringEntity request_body = new StringEntity( params.toString() );
                post.setHeader("Accept", "application/json");
                post.setHeader(HTTP.CONTENT_TYPE, "application/json");
                post.setEntity(request_body);
            	client_response = client.execute( post );*/
            	doPost();
            	return;
            case UPDATE:
            	client_response = client.execute( update );
                break;
            case DESTROY:
            	client_response = client.execute( delete );
                break;
        }
    	resp = EntityUtils.toString(client_response.getEntity());
    	JSONObject temp = new JSONObject(resp);
        if ( ACTION == INDEX )
        {
            response = new JSONArray( temp.getString("posts") );
        }
        else
        {
        	response = new JSONArray(  );
        	response.put( temp );
        }
    }

	private void doPost() throws Exception {
		HttpURLConnection conn_to_server = null;
		try{
			URL url;
			url = new URL(REQUEST_URL.toString());
			conn_to_server = (HttpURLConnection) url.openConnection();
			conn_to_server.setDoOutput(true);
			conn_to_server.setUseCaches(false);
			conn_to_server.setRequestMethod("POST");
			conn_to_server.setRequestProperty("Content-Type", "application/json");
			DataOutputStream printout =
    				new DataOutputStream(conn_to_server.getOutputStream());
			// convert the json message to be send into string
			String body = params.toString();
			// write into the output stream the message to be send
			printout.writeBytes(body);
			// send the request
			printout.flush ();
			// close the output stream
			printout.close ();
			// handle the response
			int status = conn_to_server.getResponseCode();
			if (status >= 400) {	 
				 throw new Exception("Post failed with error code " + status);
			}
			BufferedReader response_reader = new BufferedReader(new InputStreamReader(conn_to_server.getInputStream()));
			StringBuilder response_parser = new StringBuilder();
			response_parser.append( response_reader.readLine() );
			response = new JSONArray(  );
        	response.put( new JSONObject( response_parser.toString() ).getJSONObject("post") );
    	}finally{
    		if (conn_to_server != null) {
    			conn_to_server.disconnect();
            }
    	}
	}

	/*@Override
	protected Long doInBackground(Void... params) {
		try {
			executeAndGetResponse();
			notifySuscriptors();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	private class AsyncTaskRunnable extends AsyncTask<Void, Integer, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			try {
				executeAndGetResponse();
				notifySuscriptors();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
