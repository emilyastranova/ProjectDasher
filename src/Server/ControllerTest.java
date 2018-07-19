package Server;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class ControllerTest {

	public static void main(String[] args) {
		ControllerManager controllers = new ControllerManager();
		controllers.initSDLGamepad();
		//Print a message when the "A" button is pressed. Exit if the "B" button is pressed 
		//or the controller disconnects.
		while(true) {
		  ControllerState currState = controllers.getState(0);
		  
		  if(!currState.isConnected || currState.b) {
		    break;
		  }
		  if(currState.a) {
		    System.out.println(currState.rightTrigger);
		  }
		}

	}

}
