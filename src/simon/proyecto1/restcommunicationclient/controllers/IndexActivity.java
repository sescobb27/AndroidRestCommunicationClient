package simon.proyecto1.restcommunicationclient.controllers;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import simon.proyecto1.restcommunicationclient.R;
import simon.proyecto1.restcommunicationclient.R.id;
import simon.proyecto1.restcommunicationclient.R.layout;
import simon.proyecto1.restcommunicationclient.helpers.ConnectionDetector;
import simon.proyecto1.restcommunicationclient.helpers.DinamicListener;
import simon.proyecto1.restcommunicationclient.helpers.ServiceConnection;
import simon.proyecto1.restcommunicationclient.models.Post;

import java.util.TreeSet;

public class IndexActivity extends Activity implements View.OnClickListener, ServiceSuscriptor{

    private ConnectionDetector internet_detector;
    private ServiceConnection  serviceConnection;
    private TreeSet<Post>      posts;


    private Button             btn_index, btn_new;
    private TableLayout        post_table;
    // private ScrollView scroller;
    private TableRow           tableRow;
    private TextView id, title;

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
        // scroller = (ScrollView) findViewById(R.id.scroll);
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
        	ex.printStackTrace();
        }
    }

    private void startPostActivity() {
    	Intent new_post_intent = new Intent(getApplicationContext(), PostActivity.class);
    	serviceConnection.unsuscribe(this);
    	startActivityForResult(new_post_intent, 0);
    }
    
    private void createRow(final Post post)
    {
    	this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				tableRow = new TableRow(getApplicationContext());
		        
		        id = new TextView(getApplicationContext());
		        id.setText(post.getId().toString());
		        id.setTextColor(Color.RED);
		        id.setTextSize(30.0f);
		        id.setFocusable(true);
		        
		        title = new TextView(getApplicationContext());
		        title.setText(post.getTitle());
		        title.setTextColor(Color.BLACK);
		        title.setFocusable(true);
		        title.setTextSize(30.0f);
		        
		        tableRow.addView(id);
		        tableRow.addView(title);
		        
		        tableRow.setOnClickListener(new DinamicListener(post, getApplicationContext()));
		        
		        post_table.addView(tableRow);
			}
		});
    }
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
    	if(requestCode == RESULT_OK)
    	{
    		Post new_post = ( Post ) data.getSerializableExtra( "post" );
    		posts.add( new_post );
    		createRow(new_post);
    		serviceConnection.suscribe(this);
    	}
    	if(resultCode == RESULT_OK)
    	{
    		Post new_post = ( Post ) data.getSerializableExtra( "post" );
    		posts.add( new_post );
    		createRow(new_post);
    		serviceConnection.suscribe(this);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	this.finish();
    }
    
    private void getIndexAction() throws Exception {
        serviceConnection.setURL( ServiceConnection.INDEX );
        serviceConnection.sendRequest( null );
        btn_index.setClickable(false);
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
            if(! posts.contains(post))
            {
            	posts.add( post );
            	createRow(post);
            }
        }
	}
	@Override
	public void responseObtained(JSONObject response) throws Exception {
		// don't apply
		
	}
}
