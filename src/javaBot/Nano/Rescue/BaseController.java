package javaBot.Nano.Rescue;

import javaBot.Nano.Rescue.ServoLinearizator;
import com.muvium.apt.ADCReader;
import com.muvium.apt.MultiServoController;
import com.muvium.apt.PeripheralFactory;
import com.muvium.io.EEPROMStore;
import com.muvium.io.PinIO;
import com.muvium.io.PortIO;
import javaBot.lcdHandler;

/**
 * Created on 20-02-2006
 * Copyright: (c) 2006
 * Company: Dancing Bear Software
 *
 * @version $Revision$
 * last changed 20-02-2006
 */

public class BaseController
{
	public static final byte		LED_RED		= (byte) 0x04;
	public static final byte		LED_YELLOW	= (byte) 0x05;
	public static final byte		LED_GREEN	= (byte) 0x06;
	public static final byte		LED_BLUE	= (byte) 0x07;

	public static final int			SENSOR_IL	= 0; // AN0
	public static final int			SENSOR_FL	= 1; // AN1
	public static final int			SENSOR_MC	= 2; // AN2
	public static final int			SENSOR_DS	= 3; // AN3
	public static final int			SENSOR_VM	= 4; // AN4
	public static final int			SENSOR_FR	= 5; // AN5
	public static final int			SENSOR_IR	= 6; // AN6
	public static final int			SENSOR_X1	= 7; // AN7
	public static final int			SENSOR_X2	= 8; // AN8
	public static final int			SENSOR_X3	= 9; // AN9
	public static final int			SENSOR_V9	= -1;
	public static final int			SENSOR_MX   = 9;
	public static final byte 		LED_LEFT_FIELD 	= PinIO.PINB1;
	public static final byte 		LED_RIGHT_FIELD = PinIO.PINB2;
	public static final byte 		LED_FL 		= 1;
	public static final byte 		LED_FR 		= 2;
	
	public static final int			STORE_L_YEL	= 0;
	public static final int			STORE_R_YEL	= 1;
	public static final int			STORE_L_GRN	= 2;
	public static final int			STORE_R_GRN	= 3;
	public static final int			STORE_L_BLK	= 4;
	public static final int			STORE_R_BLK	= 5;
	
    public  EEPROMStore 			store = new EEPROMStore();	
    private ToneGenerator			tonePlayer;
	private MultiServoController	servoDirect;
	private int						servoMax;
	private int						servoMid;
	private ADCReader				adc;
	private UVMDemo					robot;
	private int 					reporting = 0;
	
	/**
	 * Creates a new JobotJrBaseController object.
	 *
	 * @param factory TODO PARAM: DOCUMENT ME!
	 * @param theDemo TODO PARAM: DOCUMENT ME!
	 */
	public BaseController(PeripheralFactory factory, UVMDemo theDemo)
	{
		servoDirect = factory
				.createMultiServoController(MultiServoController.IMPLEMENTATION_DIRECT);
		servoMax = servoDirect.getMaxPosition();
		servoMid = servoMax >> 1;
		adc = factory.createADCReader(0, ADCReader.READ_INT);
		robot = theDemo;
		tonePlayer = new ToneGenerator(factory);

		try
		{
			servoDirect.start();
		}
		catch (Exception e)
		{
//			System.out.println("Failed!");
		}
	}

	/**
	 * TODO METHOD: DOCUMENT ME!
	 */
	public void heartBeat()
	{
		PortIO.toggleOutputPin(LED_RED, PortIO.PORTE);
	}

	public void setStatusLeds(boolean yel, boolean grn, boolean blu)
	{
		PortIO.setOutputPin(yel, LED_YELLOW, PortIO.PORTE);
		PortIO.setOutputPin(grn, LED_GREEN, PortIO.PORTE);
		PortIO.setOutputPin(blu, LED_BLUE, PortIO.PORTE);
	}

	public void setStatusLeds(boolean red, boolean yel, boolean grn, boolean blu)
	{
		PortIO.setOutputPin(red, LED_RED, PortIO.PORTE);
		PortIO.setOutputPin(yel, LED_YELLOW, PortIO.PORTE);
		PortIO.setOutputPin(grn, LED_GREEN, PortIO.PORTE);
		PortIO.setOutputPin(blu, LED_BLUE, PortIO.PORTE);
	}
	
	public void setFieldLeds (boolean on) {
		if (on == true) {
			PortIO.setPinHigh(LED_FR, PortIO.PORTB);
			PortIO.setPinHigh(LED_FL, PortIO.PORTB);
		}
		else {
			PortIO.setPinLow(LED_FR, PortIO.PORTB);
			PortIO.setPinLow(LED_FL, PortIO.PORTB);
		}
	}

	public void setLed(int led, boolean state)
	{
		PortIO.setOutputPin(state, led, PortIO.PORTE);
	}
	
	public void printLCD (String txt) {
		lcdHandler.println (txt);  
	}

	public void toggleLed(int led)
	{
		PortIO.toggleOutputPin(led, PortIO.PORTE);
	}

	public void setServo(int servo, int value) {
		servoDirect.setPosition(value, servo);
	}

	/**
	 * getSensorValue reads the raw sensor value
	 */
	public int getSensorValue(int s) {
		if (s < 0) return 0;
		adc.setChannel(s);
		int i = adc.getSample(); 
		return i;
	}
	
	public int getFieldSensorValue(int s) {
		if (s < 0) return 0;
		adc.setChannel(s);
		int i = adc.getSample();   // Get value before
		if (s == SENSOR_FL || s == SENSOR_FR) {
			PortIO.setPinHigh(LED_FR, PortIO.PORTB);
			PortIO.setPinHigh(LED_FL, PortIO.PORTB);
			try{ Thread.sleep(60);}catch(Exception e){}
			// 80ms=257,100 60ms=180,100 50ms=175,81 20ms=80,55 10ms=47,30
			i = adc.getSample() - i;
			if (i < 0) i = 0;
			PortIO.setPinLow(LED_FR, PortIO.PORTB);
			PortIO.setPinLow(LED_FL, PortIO.PORTB);
		}
		return i;
	}
	
	public int getSensorValueCal(int sensor) {
		int value = getSensorValue(sensor);
		if (value < minSensor(sensor))
			storeCalMin(sensor, value);
		if (value > maxSensor(sensor))
			storeCalMax(sensor, value);
		return value;
	}
	
	/**
	 * getSensor returns the calibrated sensor value
	 * It will always return a value between 0 and 100
	 * If values outside this range are returned, the calibration
	 * if the sensor is wrong.
	 */
	
	public int getSensor(int sensor) {
		int val = getSensorValue(sensor);
		val = (val - minSensor(sensor)) * 100 / maxSensor(sensor);
		return 0;
	}
	
	public int minSensor(int sensor) {
		int sens = sensor * 2;
		return (int) store.peekInt32(sens);
	}
	
	public int maxSensor(int sensor) {
		int sens = sensor * 2 + 1;
		return (int) store.peekInt32(sensor);
	}
	
	/**
	 * We use a total of 13 calibration fields.
	 * Each field has a min and max value that needs to be filled
	 * during calibration.
	 * Fields 0 - 6 correspond with the sensors with the same number.
	 * Fields 7 - 12 correspond with values for White, Yellow, Green and Black
	 */
	public void clearCalibration() {
		for (int i=0; i<12; i++) {
			storeCalMin(i, 1023);
			storeCalMax(i, 0);
		}
	}
	
	public void storeCalMin(int sensor, int value) {
        store.pokeInt32(sensor*2, value );
	}
	public void storeCalMax(int sensor, int value) {
        store.pokeInt32(sensor*2+1, value );
	}
	
	/**
	 * getSwitch
	 * Is only used on digital inputs.
	 * Currently no digital input sensors exist.
	 */
	
	public int getSwitch(int sw) {
		boolean on;
		on = PortIO.getInputPin(sw, PortIO.PORTD);
		if (on)
			return 1;
		else
			return 0;
	}

	/*
	 * Drive directly drives the servo's
	 */
	public void drive(int x, int y)
	{
		servoDirect.setPosition(scaleToServoSpeed(x), 0);
		servoDirect.setPosition(scaleToServoSpeed(0 - y), 1);
	}

	/**
	 * Checks the boundary's and makes sure the preconditions are met.
	 * @param val Percentage between -100 to 100
	 * @return The non linear speed of the servo motor
	 */
	public int scaleToServoSpeed(int val)
	{
		int value = val;
		if (val >= 100)	value = 99;
		if (val <= -100) value = -99;
		int servoValue = ServoLinearizator.convertPowerToServoSpeed(value);
		int servoCalculated = servoValue * servoMid;
		int roundCorrection = 0;
		
		/* Because the muvium controller cannot use doubles, the formula in the return statement
		 * of this method, servoCalculated / 100 can result in a double value, losing the rounding
		 * precision. To correct this, we see if the servoCalculated value is a number with the last
		 * two digits > 50. If it is, add 1 to the result. Do the opposite for negative values.
		 * 
		 *  Examples:
		 *  167 ===> 67 is larger than 50	==> result is 2 (167/100 + 1)
		 *  410 ===> 10 is smaller than 50	==> result is 4 (410/100)
		 *  -310 ==> -10 is larger than -50 ==>	result is -3 (-310/100)
		 *  -193 ==> -93 is smaller than -50 =>	result is -2 (-193/100 - 1) 
		 */
		if (servoValue > 0 && servoCalculated % 100 >= 50)
			roundCorrection = 1;
		else if (servoCalculated % 100 < -50)
			roundCorrection = -1;		
		return (((servoCalculated / 100) + servoMid) + roundCorrection);		
	}

	public void tone(int toneId) {
		tonePlayer.playTone(toneId);
	}

	public void setState(int state) {
		robot.setState(state);
	}

	/**
	 * reportState shows the current calculated states
	 * when the reporting is turned on
	 * This is used to communicate with the simulator
	 * when the robot is online.
	 * The code underneath is not yet functional.
	 */
	public void reportState () {
		String s = "";
		int v1 = 0;
		int v2 = 0;
		if (reporting > 0) {
			if (robot.getState() == UVMDemo.STATE_CALIBRATE)
				s += " V1" + v1 + " V2=" + v2;
			if (s != "")
				System.out.println(s);
		}
	}
	
	public void reportState (int level) {
		reporting = level;
	}
	
}
