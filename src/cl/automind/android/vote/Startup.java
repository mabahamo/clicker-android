package cl.automind.android.vote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cl.automind.android.application.AppContext;
import cl.automind.android.utils.RutValidator;

public class Startup extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(AppContext.InitialState);
	}
	@Override
	public void setContentView(int layoutResID){
		super.setContentView(layoutResID);
		AppContext.getInstance().setState(layoutResID);
	}
	public void actionButton(View view){
		if (AppContext.getInstance().getState() == AppContext.InitialState){
			EditText text_input = (EditText)this.findViewById(R.id.RutInputTextField);
			AppContext.getInstance().setRut(text_input.getText().toString());
			if (RutValidator.validateRut(AppContext.getInstance().getRut())) {
                Intent myIntent = new Intent(view.getContext(), Vote.class);
                startActivity(myIntent);
                finish();
			} else {
				cleanTextField(text_input);
			}
		}
	}
	public void cleanTextField(View view){
		EditText text_input = (EditText)this.findViewById(R.id.RutInputTextField);
		text_input.setText("");
	}

}