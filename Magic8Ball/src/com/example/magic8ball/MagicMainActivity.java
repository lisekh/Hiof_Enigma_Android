package com.example.magic8ball;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MagicMainActivity extends Activity implements SensorEventListener {

	private Sensor accelerometer;
	private SensorManager sManager;
	private TextView textAnswer;
	private long lastUpdate = 0;
	private float prevX, prevY, prevZ;
	private static final int SHAKE_THRESHOLD = 1200;
	private Vibrator vib;
	private final String[] THEMES = {"Normal", "Ocean", "Enigma", "Disco", "Fire", "8 ball on fire"};
	AlertDialog.Builder alert;
	ImageButton iButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magic_main);
		
		// Initialize sensor management
		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		// Initialize imagebitton
		iButton = (ImageButton) findViewById(R.id.magic8ball);
		iButton.setOnClickListener(iButtonHandler);
		
		// Initialize the answer textfield
		textAnswer = (TextView) findViewById(R.id.text);
		textAnswer.setTextSize(35);
		
		vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	}

	// Create dialog for changing themes
	public void showThemes()
	{
		AlertDialog.Builder showThemes = new AlertDialog.Builder(this);
		showThemes.setTitle("Choose theme");
       
		// Show possible themes
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.themetext, THEMES);
	 
		showThemes.setAdapter(adapter, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichTheme) 
			{
				switch(whichTheme) 
				{
					case 0:
						iButton.setBackgroundResource(R.drawable.magicball);
						break;
					case 1:
						iButton.setBackgroundResource(R.drawable.ball3);
						break;
					case 2:
						iButton.setBackgroundResource(R.drawable.ball4);
						break;
					case 3:
						iButton.setBackgroundResource(R.drawable.ball5);
						break;
					case 4:
						iButton.setBackgroundResource(R.drawable.ball6);
						break;
					case 5:
						iButton.setBackgroundResource(R.drawable.ball7);
						break;
				}
			}
		 });
		
		// Show cancel button
		showThemes.setCancelable(true);
        showThemes.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int id) 
            {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = showThemes.create();
        alertDialog.show();
	}
	
	// Initialize when imagebutton is pressed
	View.OnClickListener iButtonHandler = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			showThemes();
		}
	};

	protected void onPause() 
	{
	    super.onPause();
	    sManager.unregisterListener(this);
	}
	
	protected void onResume() 
	{
	    super.onResume();
	    sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		
	}

	/**
	 * Activating the accelerometer threshold
	 * provides the user with an answer
	 * @param SensorEvent event
	 */
	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		Sensor sens = event.sensor;
		
		if(sens.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			// Get coordinates
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			long curTime = System.currentTimeMillis();
			 
	        if((curTime - lastUpdate) > 150) 
	        {
	            long diffTime = (curTime - lastUpdate);
	            lastUpdate = curTime;
	            
	            float speed = Math.abs(x + y + z - prevX - prevY - prevZ)/ diffTime * 10000;
	            
	            if(speed > SHAKE_THRESHOLD)
	            {
	            	vib.vibrate(100);
	            	showAnswer();
	            }
	            prevX = x;
	            prevY = y;
	            prevZ = z;
	        }
		}
	}

	/**
	 * Generate an answer
	 */
	private void showAnswer() 
	{
		ArrayList<String> answers = new ArrayList<String>();
			answers.add("Definitely");
			answers.add("Uncertain");
			answers.add("Of course");
			answers.add("Retorical question, right?");
			answers.add("Probably");
			answers.add("Who knows?!");
			answers.add("Indeed");
			answers.add("Don't count on it");
			
		int listSize = answers.size();
	
		textAnswer.setText(answers.get((int) (Math.random()*listSize)));
	}
}
