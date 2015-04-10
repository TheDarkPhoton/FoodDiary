package data;

public class Product {
	private String _name;
//	private ImageIcon _img;
	private Nutrition _nutrition;

	public Product(String name) {
		_name = name;
	}
	
	public boolean setNutrition(float grams, float cals, float f, float c, float p){
		if (grams == 0){
			return false;
		}
		
		_nutrition = new Nutrition(100, cals / grams * 100, f / grams * 100, c / grams * 100, p / grams * 100);
		return true;
	}
	
	public String getName(){
		return _name;
	}
	
	public Nutrition getQuantity(float grams){
		return _nutrition.getQuantity(grams);
	}
}
