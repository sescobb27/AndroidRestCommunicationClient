package simon.proyecto1.restcommunicationclient;

/**
 * Created by simon on 8/25/13.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.util.LinkedList;
import java.util.Map;

public class ServiceConnection extends AsyncTask<Void, Integer, Long>{

    private final String INDEX_URL   = "/posts.json";
    private final String UPDATE_URL  = "/post/:id.json";
    private final String CREATE_URL  = "/posts.json";
    private final String DESTROY_URL = "/posts/:id.json";
    private final String SERVER_URL  = "http://192.168.1.2";
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
    private URL URL;
    private HttpURLConnection conn_to_server;
    private LinkedList<ServiceSuscriptor> suscriptors;
    private HttpGet get;
    private HttpClient client;

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
    		suscriptor.responseObtained(response);
    	}
    }

    public void setURL( int url )
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
                REQUEST_URL.append( INDEX_URL );
                ACTION = 1;
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
                getIndex();
                break;
            case CREATE:
                postCreate();
                break;
            case UPDATE:
                putUpdate();
                break;
            case DESTROY:
                postDestroy();
                break;
            default:
                getIndex();
        }
        if ( ACTION == CREATE )
            params = new JSONObject(req_params);
        service.execute();
    }

    private void getResponse(  ) throws Exception
    {
        if ( ACTION == INDEX )
        {
        	HttpResponse client_response = client.execute(get);
        	String resp = EntityUtils.toString(client_response.getEntity());
        	JSONObject temp = new JSONObject(resp);
            response = new JSONArray( temp.getString("posts") );
        }
        else
        {
	        int status = conn_to_server.getResponseCode();
	        if (status != 200) {
	            throw new IOException("Request failed with error code " + status);
	        }
        }
    }


    private void send() throws Exception
    {
        DataOutputStream printout = new DataOutputStream(conn_to_server.getOutputStream());
        if ( ACTION == CREATE )
        {
            String request_body = params.toString();
            printout.writeBytes(request_body);
        }
        printout.flush ();
        printout.close ();
    }

    private void tryURL(String requestMethod) throws Exception
    {
        try {
            URL = new URL(REQUEST_URL.toString());
            resetConnectionToService(requestMethod);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + REQUEST_URL.toString());
        }
    }

    private void resetConnectionToService(String requestMethod) throws Exception
    {
        conn_to_server = null;
        conn_to_server = (HttpURLConnection) URL.openConnection();
        conn_to_server.setDoOutput(true);
        conn_to_server.setUseCaches(false);
        conn_to_server.setRequestMethod(requestMethod);
        conn_to_server.setRequestProperty("Content-Type", "application/json");
    }

    private void getIndex() throws Exception
    {
    	get = new HttpGet(REQUEST_URL.toString());
    	get.setHeader("content-type", "application/json");
    }

    private void postCreate() throws Exception
    {
        tryURL("POST");
    }

    private void putUpdate() throws Exception
    {
        tryURL("UPDATE");
    }

    private void postDestroy() throws Exception
    {
        tryURL("DELETE");
    }

	@Override
	protected Long doInBackground(Void... params) {
		try {
			if(ACTION != INDEX)
				send(  );
			getResponse(  );
			if ( conn_to_server != null )
	            conn_to_server.disconnect();
			notifySuscriptors();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
