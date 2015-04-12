package data;

import java.io.Serializable;

public class Nutrition implements Serializable {
	private static final long serialVersionUID = -7945612544534182608L;
	private float _grams;
	private float _cals;
	private float _f;
	private float _c;
	private float _p;
	
	public Nutrition(float grams, float cals, float f, float c, float p) {
		_grams = grams;
		_cals = cals;
		_f = f;
		_c = c;
		_p = p;
	}
	
	public Nutrition getQuantity(float grams){
		return new Nutrition(grams, _cals * (grams / _grams), _f * (grams / _grams), _c * (grams / _grams), _p * (grams / _grams));
	}
	
	public float getCalories(){
		return _cals;
	}
	
	public float getFat(){
		return _f;
	}
	
	public float getCarbs(){
		return _c;
	}
	
	public float getProtein(){
		return _p;
	}
}
