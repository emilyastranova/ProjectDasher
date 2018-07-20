
package Client;
/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  RPIServoBlasterExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2018 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.RPIServoBlasterProvider;


public class PWMTest {

    public static void main(String[] args) throws Exception {
    	int n = 18;
        System.out.println("Config Servo PWM with pin number: " + n);
        com.pi4j.wiringpi.Gpio.pinMode(n, com.pi4j.wiringpi.Gpio.PWM_OUTPUT);
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetClock(192);
        com.pi4j.wiringpi.Gpio.pwmSetRange(2000);

        for(int i = 0; i < 5; i++){
            System.out.println("Set Servo");
            com.pi4j.wiringpi.Gpio.pwmWrite(n, 30);

            Thread.sleep(2000);

            System.out.println("Change servo state...");
            com.pi4j.wiringpi.Gpio.pwmWrite(n, 90);

            Thread.sleep(2000);
            
            System.out.println("Armed");
            com.pi4j.wiringpi.Gpio.pwmWrite(n, 30);
            Thread.sleep(2000);

        }
    }
}
