package javaBot.Nano.Rescue;

import com.muvium.apt.PeriodicTimer;

/**
 * Opdracht 4A - Vooruit rijden Opdracht 4B - Twee seconden lang rijden Opdracht
 * 4C - Vooruit rijden en stop na 2 sec Opdracht 4D - Een bocht maken Opdracht
 * 4E - maak een subroutine Opdracht 4F - Rijd in een vierkantje Opdracht 5C -
 * Rijd tot aan de zwarte lijn Opdracht 5E - Doorzoek het moeras
 **/

public class DriveBehavior extends Behavior {
	private BaseController joBot;
	private int state = 0;
	private int count = 0;
	int fl = 0;
	int finalState = 1000;

	//private int leftSensor = joBot.getSensorValue(1);

	private void joBotDrive(int curState, int newState, int motorLeft, int motorRight, int time) {
		if (state == curState) { 
			joBot.drive(motorLeft, motorRight);
			if (count++ >= time) {
				state = newState;
				joBot.printLCD("State= " + state);
				count = 0;
			}
		}
	}

	public DriveBehavior(BaseController initJoBot,
			PeriodicTimer initServiceTick, int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	public void doBehavior() {
		while (joBot.getSensorValue(1) >= 250) {
			joBot.drive(50, 50);

		}
		
		if (joBot.getSensorValue(1) <= 250 || joBot.getSensorValue(5) <= 250){
			joBot.drive(0, 0);
			joBot.setStatusLeds(true, false, false); 
		}
	}
}
