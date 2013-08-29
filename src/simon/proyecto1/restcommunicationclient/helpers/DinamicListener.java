package simon.proyecto1.restcommunicationclient.helpers;

import simon.proyecto1.restcommunicationclient.models.Post;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class DinamicListener implements OnClickListener{

	private Post post;
	private Context current_context;
	
	public DinamicListener(Post post, Context current_context) {
		this.post = post;
		this.current_context = current_context;
	}
	@Override
	public void onClick(View v) {
		Toast.makeText(current_context, this.post.toString(), Toast.LENGTH_LONG).show();
	}

}
