package norman.jeran.qwikalc;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalcScreen extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calc_screen);

		
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calc_screen, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.aboutPage) {
			
			Context context = getApplicationContext();
			CharSequence about = "A simple, non-buggy calculator designed by Jeran Norman";
			
			Toast aboutMessage = Toast.makeText(context, about, Toast.LENGTH_LONG);
			aboutMessage.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//Using fragment for calculator layout and actions
	public static class PlaceholderFragment extends Fragment {

		private ButtonClickListener buttonClick = new ButtonClickListener();
		
		private EditText textDisp, textDispPre; //Textboxes
		private float preNumber; //Number before operation
		private float postNumber; //Number after operation
		private String calculation; // Save operation as string for parsing
		
		boolean opPressed = false; //Keeps track of when an operation has been pressed
		boolean isValid = false; //Keeps track of when calculation is valid
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_calc_screen,
					container, false);
			
			//Set blue background color
			getActivity().getWindow().getDecorView().setBackgroundColor(Color.argb(100,
					123, 153, 240));
			
			//Assign text fields
			textDisp = (EditText) rootView.findViewById(R.id.textDisplay);
			textDispPre = (EditText) rootView.findViewById(R.id.textDisplayPre);
			
			//List of all buttons on calculator
			int buttonList[] = {R.id.button0, R.id.button1, R.id.button2, R.id.button3,
					R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8,
					R.id.button9, R.id.buttonAdd, R.id.buttonSub, R.id.buttonMult,
					R.id.buttonDiv, R.id.buttonDec, R.id.buttonCalc, R.id.buttonClear};
			
			//set button listener for each number button and decimal
			for (int button:buttonList) {
				View tempV = (View) rootView.findViewById(button);
				tempV.setOnClickListener(buttonClick);
			}
			
			return rootView;
		}
		
		private class ButtonClickListener implements OnClickListener{
			
			public void onClick (View tempV) {
				//Define actions for each button pressed
				switch (tempV.getId()) {
				
				case R.id.buttonClear:
					textDisp.setText("");
					textDispPre.setText("");
					preNumber = 0;
					calculation = "";
					isValid = false;
					opPressed = false;
					break;
					
				case R.id.buttonAdd:
					if (!textDisp.getText().toString().contentEquals("")) {
						opBuffer("+");
						opPressed = true;
					}
					break;
						
				case R.id.buttonSub:
					if (!textDisp.getText().toString().contentEquals("")) {
						opBuffer("-");
						opPressed = true;
					}
					break;
					
				case R.id.buttonMult:
					if (!textDisp.getText().toString().contentEquals("")) {
						opBuffer("*");
						opPressed = true;
					}
					break;
						
				case R.id.buttonDiv:
					if (!textDisp.getText().toString().contentEquals("")) {
						opBuffer("/");
						opPressed = true;
					}
					break;
					
				case R.id.buttonCalc:
					if (isValid) calculate();
					break;
					
				default:
					String tempStr = ((Button) tempV).getText().toString();
					
					//Don't allow for two decimals in a row
					if (!(tempStr.contains(".") && 
							textDisp.getText().toString().contains(".")))
						findNum(tempStr);
					break;
				}
			}
		}
		
		//Used when operation button is pressed
		public void opBuffer(String inStr) {
			
			preNumber = Float.parseFloat(textDisp.getText().toString()); //save already entered numbers
			calculation = inStr;
			textDispPre.setText(textDisp.getText().toString() + " " + inStr); 
			textDisp.setText("");
		}
		
		//Add numerical value to textDisp
		public void findNum(String inStr) {
			
			String tempDisp = textDisp.getText().toString();
			tempDisp += inStr;
			textDisp.setText(tempDisp);
			
			if (opPressed) {
				isValid = true;
			}
		}
		
		//Get final result
		public void calculate() {
			float postNumber = Float.parseFloat(textDisp.getText().toString());
			float result = preNumber;
			
			if (!opPressed) textDisp.setText(String.valueOf(result));
			
			if (calculation.equals("+")) {
				result = preNumber + postNumber;
			}
			if (calculation.equals("-")) {
				result = preNumber - postNumber;
			}
			if (calculation.equals("*")) {
				result = preNumber * postNumber;
			}
			if (calculation.equals("/")) {
				result = preNumber / postNumber;
			}
			
			textDispPre.setText(textDispPre.getText().toString() + " " +
					Float.parseFloat(textDisp.getText().toString()));
			textDisp.setText(String.valueOf(result));
		}
	}

}
