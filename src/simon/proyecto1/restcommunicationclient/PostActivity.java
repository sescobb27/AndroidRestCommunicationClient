package simon.proyecto1.restcommunicationclient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class PostActivity extends Activity implements View.OnClickListener, ServiceSuscriptor{

    private Button crear;
    private EditText txt_title, txt_content, txt_author;
    private RatingBar post_rating;
    private Map<String, String> params;
    private JSONArray response;
    private TreeSet<Post>      posts;

    private ConnectionDetector internet_detector;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layouts);
        internet_detector = ConnectionDetector.getConnectionDetector(getApplicationContext());
        boolean is_connected = internet_detector.isConnectingToInternet();
        if(!is_connected)
        {
            Log.e( "Connexion", "Please connect to working Internet connection" );
            Toast.makeText( this, "Please connect to working Internet connection", Toast.LENGTH_LONG  ).show();
            return;
        }
        if(getIntent() != null)
        {
	        Bundle extras = getIntent().getExtras();
	        posts = (TreeSet<Post>) extras.get("posts");
        }
        crear = (Button) findViewById( R.id.btn_crear );
        txt_content = (EditText) findViewById( R.id.txt_content );
        txt_title = (EditText) findViewById( R.id.txt_title );
        txt_author = (EditText) findViewById( R.id.txt_author );
        post_rating = (RatingBar) findViewById( R.id.post_rating );
        crear.setOnClickListener( this );
        serviceConnection = ServiceConnection.getServiceConnection();
        serviceConnection.suscribe(this);
    }

    private void sendParams()
    {
        try {
            params = new HashMap<String, String>(  );
            params.put( "title", txt_title.getText().toString() );
            params.put( "content", txt_content.getText().toString() );
            params.put( "rating", String.valueOf( post_rating.getRating() ) );
            params.put( "author", txt_author.getText().toString() );
            serviceConnection.setURL( ServiceConnection.CREATE );
            params.put( "post", params.toString() );
            serviceConnection.sendRequest( params );
        }catch ( Exception ex ) {
            if ( ex instanceof IOException )
            {

            }
            else if ( ex instanceof NullPointerException )
            {

            }
            else if ( ex instanceof JSONException )
            {

            }
            else if( ex instanceof IllegalArgumentException )
            {

            }
        }
    }
    @Override
    public void onClick( View v ) {
        switch ( v.getId() )
        {
            case R.id.btn_crear :
                sendParams();
                break;
        }
    }

	@Override
	public void responseObtained(JSONArray response) throws Exception {
		//don't apply
	}
	
	@Override
	public void responseObtained(JSONObject response) throws Exception {
		Post post = new Post(
				response.getInt("id"),
				response.getInt("rating"),
				response.getString("author"),
				response.getString("content"),
				response.getString("title")
		);
		posts.add( post );
	}
}
