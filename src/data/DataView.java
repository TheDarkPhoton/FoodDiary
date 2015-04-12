package data;

import java.util.Stack;

import javax.swing.JPanel;

import javafx.util.Pair;

public interface DataView {
	public void changeView(Stack<Pair<Integer, DataView>> hierarchy, JPanel content);
	public void addItem(DataView item);
}
