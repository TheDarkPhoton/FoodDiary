package data;

import java.io.Serializable;

public class Product implements Serializable {
	private static final long serialVersionUID = 4250812290390980520L;
	private String _name;
//	private ImageIcon _img;
	private Nutrition _nutrition;

	public Product(String name) {
		_name = name;
	}
	
	public Product(Product p){
		_name = p.getName();
		Nutrition n = p.getQuantity(100);
		setNutrition(100, n.getCalories(), n.getFat(), n.getCarbs(), n.getProtein());
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
	
	@Override
	public String toString() {
		return _name;
	}
}
