package javaBot.Nano.Rescue;

import com.muvium.apt.PeriodicTimer;

/**
 * Opdracht - 6 Eenvoudige Line Follower met enkele sensor
 * 
 * De lijnvolger gebruikt de reflectiesensoren (fieldsensoren)
 * om de kleur van het veld onder de robot te bepalen.
 * Hij volgt de zwarte lijn, eerst met een enkele sensor.
 * In opdracht 7 ga je de lijn met twee sensoren volgen.
 * Daarnaast ga je ook een subroutine gebruiken.
 * Als laatste ga je ook de gele lijn volgen.
 */

public class LineFollowerBehavior extends Behavior
{
	private BaseController	joBot;
	private int	state	= 0;
	private int count   = 0;
	private int speed	= 50;
	private int fl	    = 0;
	private int blkLs	= 400;   // Value of black on your field

	public LineFollowerBehavior(BaseController initJoBot, PeriodicTimer
			initServiceTick,int servicePeriod)
	{
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	// Maak een eenvoudige lijnvolger met een enkele sensor
	// Opdracht 6
	
	public void doBehavior() {
		if (state == 0) {
			System.out.println("Enkelvoudige Line Follower");
			joBot.setStatusLeds(false, false, false);
			joBot.drive(speed, speed);	// Rijd rechtuit
			joBot.setFieldLeds(true);	// Zet field leds aan
			state = 1;
		}

		if (state == 1) {	
			fl = joBot.getSensorValue(BaseController.SENSOR_FL); // Left sensor
			if (fl >= blkLs) {
				joBot.drive(speed, 0);	// Go right
				joBot.setLed(BaseController.LED_GREEN, true);
			}
			if (fl < blkLs) {
				joBot.drive(0, speed);	// Go left
				joBot.setLed(BaseController.LED_GREEN, false);
			}
		}	
	}
}


