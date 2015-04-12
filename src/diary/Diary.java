package diary;

import data.MealPeriod;
import windows.MainWindow;

public class Diary {
	public static MainWindow window;
	
	public static void main(String[] args) {
		MealPeriod.createDefault();
		
		window = new MainWindow();
		window.setVisible(true);
	}
}
