package com.example.magic8ball;

import java.util.ArrayList;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MagicMainActivity extends Activity implements SensorEventListener {

	private Sensor accelerometer;
	private SensorManager sManager;
	public TextView textAnswer;
	private long lastUpdate = 0;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 900;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magic_main);
		
		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		textAnswer = (TextView) findViewById(R.id.text);
		textAnswer.setTextSize(40);
	}

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
			 
	        if ((curTime - lastUpdate) > 100) 
	        {
	            long diffTime = (curTime - lastUpdate);
	            lastUpdate = curTime;
	            
	            float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
	            
	            if (speed > SHAKE_THRESHOLD)
	            {
	            	showAnswer();
	            }
	 
	            last_x = x;
	            last_y = y;
	            last_z = z;
	        }
		}
	}

	private void showAnswer() 
	{
		ArrayList<String> answers = new ArrayList<String>();
		answers.add("Definitivt");
		answers.add("Usikkert");
		answers.add("Selvfølgelig");
		answers.add("Hva tror du?");
		answers.add("Eeh... sure");
		answers.add("Mordi vet");
		answers.add("Tror nok det ja");
		answers.add("Tullete spm");
		
		int listSize = answers.size();
	
		textAnswer.setText(answers.get((int) (Math.random()*listSize)));
	}
}
