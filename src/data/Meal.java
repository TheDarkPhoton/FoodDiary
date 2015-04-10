package data;

import java.util.ArrayList;

import javafx.util.Pair;

public class Meal {
	private String _name;
//	private ImageIcon _img;
	private ArrayList< Pair<Product, Float> > _products;
	
	public Meal(String name) { 
		_name = name;
	}
	
	public void addProduct(Product p, Float grams){
		_products.add(new Pair<Product, Float>(p, grams));
	}
	
	public String getName(){
		return _name;
	}
	
	public Nutrition getTotalNutrition(){
		float total_grams = 0;
		float cals = 0;
		float f = 0;
		float c = 0;
		float p = 0;
		
		for (int i = 0; i < _products.size(); i++) {
			float grams = _products.get(i).getValue();
			Nutrition n = _products.get(i).getKey().getQuantity(grams);
			
			total_grams += grams;
			cals += n.getCalories();
			f += n.getFat();
			c += n.getCarbs();
			p += n.getProtein();
		}
		
		return new Nutrition(total_grams, cals, f, c, p);
	}
}
