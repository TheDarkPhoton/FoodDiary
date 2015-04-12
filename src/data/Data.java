package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import javafx.util.Pair;

public class Data implements Serializable {
	private static final long serialVersionUID = 6632461413087395187L;
	private Stack<Pair<Integer, DataView>> _hierarchy;
	private ArrayList<Day> _days;
	
	public Data() {
		_days = new ArrayList<Day>();
		_hierarchy = new Stack<Pair<Integer,DataView>>();
	}
	
	public Data(ArrayList<Day> days, Stack<Pair<Integer,DataView>> hierarchy) {
		_days = days;
		_hierarchy = hierarchy;
	}
	
	public Stack<Pair<Integer, DataView>> getHierarchy(){
		return _hierarchy;
	}
	
	public ArrayList<Day> getDays(){
		return _days;
	}
}
