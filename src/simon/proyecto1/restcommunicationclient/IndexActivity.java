package simon.proyecto1.restcommunicationclient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeSet;

public class IndexActivity extends Activity implements View.OnClickListener, ServiceSuscriptor{

    private ConnectionDetector internet_detector;
    private ServiceConnection  serviceConnection;
    private TreeSet<Post>      posts;


    private Button             btn_index, btn_new;
    private TableLayout        post_table;
    private TableRow           tableRow;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.index_layout );
        internet_detector = ConnectionDetector.getConnectionDetector( getApplicationContext() );
        boolean is_connected = internet_detector.isConnectingToInternet();
        if ( !is_connected ) {
            Log.e( "Connexion", "Please connect to working Internet connection" );
            Toast.makeText( this, "Please connect to working Internet connection", Toast.LENGTH_LONG ).show();
            return;
        }
        serviceConnection = ServiceConnection.getServiceConnection();
        serviceConnection.suscribe(this);
        btn_index = (Button) findViewById( R.id.btn_index );
        btn_index.setOnClickListener( this );
        btn_new = (Button) findViewById( R.id.btn_new );
        btn_new.setOnClickListener( this );
        post_table = (TableLayout) findViewById( R.id.post_table );
        posts = new TreeSet<Post>(  );
    }

    @Override
    public void onClick( View v ) {
        try {
            switch ( v.getId() )
            {
                case R.id.btn_index:
                    getIndexAction();
                    break;
                case R.id.btn_new:
                    startPostActivity();
                    break;
            }
        }catch ( Exception ex ){
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

    private void startPostActivity() {
    	Intent new_post_intent = new Intent(getApplicationContext(), PostActivity.class);
    	new_post_intent.putExtra("posts", posts);
    	serviceConnection.unsuscribe(this);
    	startActivityForResult(new_post_intent, RESULT_OK);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == RESULT_OK)
    	{
    		
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onBackPressed() {
    	this.finish();
    }

    private void getIndexAction() throws Exception {
        serviceConnection.setURL( ServiceConnection.INDEX );
        serviceConnection.sendRequest( null );
    }

	@Override
	public void responseObtained(JSONArray response) throws Exception {
		for ( int pos = 0; pos < response.length(); ++pos )
        {
            JSONObject json_post = (JSONObject) response.get( pos );
            Post post = new Post(
            		json_post.getInt("id"),
                    json_post.getInt( "rating" ),
                    json_post.getString( "author" ),
                    json_post.getString( "content" ),
                    json_post.getString( "title" )
            );
            posts.add( post );
        }
	}
	@Override
	public void responseObtained(JSONObject response) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
