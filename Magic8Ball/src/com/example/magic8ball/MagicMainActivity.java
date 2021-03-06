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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
	private static final int SHAKE_THRESHOLD = 1100;
	private Vibrator vib;
	private final String[] THEMES = {"Normal", "Ocean", "Enigma", "Disco", "Fire", "8 ball on fire"};
	AlertDialog.Builder alert;
	ImageButton iButton;
	RotateAnimation anima;
	
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
		
		// Initialize vibrator
		vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		// Initialize animation object
		anima = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
		anima.setInterpolator(new LinearInterpolator());
		anima.setRepeatCount(0);
		anima.setDuration(700);
		
	}

	// Create dialog for changing themes
	public void showThemes()
	{
		AlertDialog.Builder themesDialog = new AlertDialog.Builder(this);
		themesDialog.setTitle("Choose theme");
       
		// Show possible themes
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.themetext, THEMES);
	 
		themesDialog.setAdapter(adapter, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichTheme) 
			{
				switch(whichTheme) 
				{
					case 0:	// Normal
						iButton.setBackgroundResource(R.drawable.magicball);
						break;
					case 1:	// Ocean
						iButton.setBackgroundResource(R.drawable.ball3);
						break;
					case 2:	// Enigma
						iButton.setBackgroundResource(R.drawable.ball4);
						break;
					case 3:	// Disco
						iButton.setBackgroundResource(R.drawable.ball5);
						break;
					case 4:	// Fire
						iButton.setBackgroundResource(R.drawable.ball6);
						break;
					case 5:	// 8 ball on fire
						iButton.setBackgroundResource(R.drawable.ball7);
						break;
				}
			}
		 });
		
		// Show cancel button
		themesDialog.setCancelable(true);
        themesDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int id) 
            {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = themesDialog.create();
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
	    iButton.setAnimation(null);
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
			 
	        if((curTime - lastUpdate) > 110) 
	        {
	            long diffTime = (curTime - lastUpdate);
	            lastUpdate = curTime;
	            
	            float speed = Math.abs(x + y + z - prevX - prevY - prevZ)/ diffTime * 10000;
	            
	            if(speed > SHAKE_THRESHOLD)
	            {
	            	vib.vibrate(100);
	            	iButton.startAnimation(anima);
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
			answers.add("Looking good");
			
		int listSize = answers.size();
	
		textAnswer.setText(answers.get((int) (Math.random()*listSize)));
	}
}
