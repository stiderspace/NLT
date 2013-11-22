package javaBot.Nano.Rescue;

import com.muvium.apt.PeriodicTimer;

/**
 * Opdracht 4A - Vooruit rijden
 * Opdracht 4B - Twee seconden lang rijden
 * Opdracht 4C - Vooruit rijden en stop na 2 sec
 * Opdracht 4D - Een bocht maken
 * Opdracht 4E - maak een subroutine
 * Opdracht 4F - Rijd in een vierkantje
 * Opdracht 5C - Rijd tot aan de zwarte lijn 
 * Opdracht 5E - Doorzoek het moeras
 **/

public class DriveBehavior extends Behavior {
	private BaseController joBot;
	private int state = 0;
	private int	count = 0;
	int fl = 0;
	int finalState = 1000;
	
	
		
	private void joBotDrive(int curState, int newState, int motorLeft, int motorRight, int time) {
		if  (state == curState){ 
			joBot.drive(motorLeft, motorRight);
			if (count++ >= time) {
				state = newState;
				joBot.printLCD("State= " + state);
				count = 0;
			}
		}
	}


	public DriveBehavior(BaseController initJoBot, PeriodicTimer initServiceTick,  int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	public void doBehavior() {
		joBotDrive(0, 1, 50, 50, 15);
		joBotDrive(1, 2, 47, 0, 10);
		joBotDrive(2, 3, 50, 50, 15);
		joBotDrive(3, 4, 47, 0, 10);
		joBotDrive(4, 5, 50, 50, 15);
		joBotDrive(5, 6, 47, 0, 10);
		joBotDrive(6, 7, 50, 50, 15);
		joBotDrive(7, 8, 47, 0, 11);
		joBotDrive(8, 1000, 0, 0, 10);
	  
	 /* joBotDrive(0, 1, 40, 20, 40);
	  	joBotDrive(1, 2, 50, 50, 35);
	  	joBotDrive(2, 3, 40, 20, 40);
	  	joBotDrive(3, 4, 20, 20, 20);
	  	joBotDrive(4, 1000, 0, 0, 1);

		
		
		
		/*joBotDrive(10, 11, 50, 50, 20);
		joBotDrive(11, 12, 50, 20, 40);
		joBotDrive(12, 3, 0, 0, 1);
		
		/*if (state == 0) {
			joBot.drive(100, 100);
			joBot.setStatusLeds(false, false, false); // Turn leds off
			count++;
			if  (count >= 20 ){
				joBot.setStatusLeds(true, false, false); 
				state = 1;
				count = 0;
			}				
		}
		if (state == 1) {
			joBot.drive(100, 50);
			joBot.setStatusLeds(false, false, false); // Turn leds off
			count++;
			if  (count >= 12){
				joBot.setStatusLeds(true, false, false); 
				state = 2;
				count = 0;
			}				
		}
		if (state == 2) {
			joBot.drive(100, 50);
			joBot.setStatusLeds(false, false, false); // Turn leds off
			count++;
			if  (count >= 11){
				joBot.setStatusLeds(true, false, false); 
				state = 3;				
			}				
		}*/
		if (state == 1000 ){
			joBot.drive(0, 0);
			joBot.printLCD("State = 3");
		}
		
	}
}

