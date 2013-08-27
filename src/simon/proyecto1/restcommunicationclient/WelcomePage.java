package simon.proyecto1.restcommunicationclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent register_intent = new Intent( getApplicationContext(), IndexActivity.class );
                startActivity( register_intent );
                finish();
            }
        };
        Timer timer_on_task = new Timer();
        timer_on_task.schedule( task, 5000 );
    }
}
