package winw.ai.util.raspberrypi;

import com.pi4j.Pi4J;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

public class RaspberryPiTest {
	private static final int PIN_GREEN_LED = 5;
	private static final int PIN_RED_LED = 6;

	public static void main(String[] args) throws InterruptedException {
		// mvn exec:java -Dexec.mainClass="winw.ai.util.raspberrypi.RaspberryPiTest"

		try {
			var pi4j = Pi4J.newAutoContext();
//			Platforms platforms = pi4j.platforms();

			var greenLedConfig = DigitalOutput.newConfigBuilder(pi4j).id("greenLed").name("greenLed")
					.address(PIN_GREEN_LED).shutdown(DigitalState.LOW).initial(DigitalState.LOW)
					.provider("pigpio-digital-output");
			var greenLed = pi4j.create(greenLedConfig);

			var redLedConfig = DigitalOutput.newConfigBuilder(pi4j).id("redLed").name("redLed").address(PIN_RED_LED)
					.shutdown(DigitalState.LOW).initial(DigitalState.LOW).provider("pigpio-digital-output");
			var redLed = pi4j.create(redLedConfig);
			

//			var redLedConfig = DigitalOutput.newConfigBuilder(pi4j).id("redLed").name("redLed").address(PIN_RED_LED)
//					.shutdown(DigitalState.LOW).initial(DigitalState.LOW).provider("pigpio-digital-output");
//			var redLed = pi4j.create(redLedConfig);
			for (int i = 0; i < 100; i++) {

//	if (greenLed.equals(DigitalState.HIGH)) {
				System.out.println("redLed");
				greenLed.low();
				redLed.high();
				Thread.sleep(500);
				System.out.println("greenLed");
				greenLed.high();
				redLed.low();
				Thread.sleep(500);
				
				
			}
			pi4j.shutdown();
		} catch (ShutdownException e) {
			e.printStackTrace();
		}
	}
}
