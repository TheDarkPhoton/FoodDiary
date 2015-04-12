package data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javafx.util.Pair;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

public class MealPeriod implements DataView, Serializable {
	private static final long serialVersionUID = -2899744765404867095L;
	private String _name;
	private String _mealTime;
	private String _mealCals;
	
	private ArrayList<Meal> _meals;
	
	public MealPeriod(String name, String mealTime, String mealCals) {
		_name = name;
		_mealTime = mealTime;
		_mealCals = mealCals;
		
		_meals = new ArrayList<Meal>();
	}
	
	public String getName(){
		return _name;
	}
	
	public String getMealTime(){
		return _mealTime;
	}
	
	public int getTargetCalories(){
		return Integer.parseInt(_mealCals);
	}
	
	public float getCaloriesConsumed(){
		float sum = 0;
		
		for (int i = 0; i < _meals.size(); i++) {
			Nutrition n = _meals.get(i).getTotalNutrition();
			sum += n.getCalories();
		}
		
		return sum;
	}
	
	public float getFatConsumed(){
		float sum = 0;
		
		for (int i = 0; i < _meals.size(); i++) {
			Nutrition n = _meals.get(i).getTotalNutrition();
			sum += n.getFat();
		}
		
		return sum;
	}
	
	public float getCarbohydratesConsumed(){
		float sum = 0;
		
		for (int i = 0; i < _meals.size(); i++) {
			Nutrition n = _meals.get(i).getTotalNutrition();
			sum += n.getCarbs();
		}
		
		return sum;
	}
	
	public float getProteinConsumed(){
		float sum = 0;
		
		for (int i = 0; i < _meals.size(); i++) {
			Nutrition n = _meals.get(i).getTotalNutrition();
			sum += n.getProtein();
		}
		
		return sum;
	}
	
	@Override
	public String toString() {
		return _name;
	}

	@Override
	public void changeView(Stack<Pair<Integer, DataView>> hierarchy, JPanel content) {
		content.removeAll();
		
//Details Area
		JPanel detailsPanel = new JPanel(new BorderLayout());
		detailsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		content.add(detailsPanel, BorderLayout.CENTER);
		
		JLabel lblDate = new JLabel(_name, SwingUtilities.CENTER);
		lblDate.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
		detailsPanel.add(lblDate, BorderLayout.NORTH);
		
		JPanel detailsBottomPanel = new JPanel(new BorderLayout());
		detailsPanel.add(detailsBottomPanel, BorderLayout.SOUTH);
		
		detailsBottomPanel.add(new JLabel("Calories Consumed:"), BorderLayout.NORTH);
		JProgressBar progress = new JProgressBar(0, getTargetCalories());
		progress.setStringPainted(true);
		progress.setPreferredSize(new Dimension(0, 26));
		progress.setValue((int)getCaloriesConsumed());
		progress.setString(progress.getValue() + " / " + progress.getMaximum());
		detailsBottomPanel.add(progress, BorderLayout.CENTER);
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Fat", getFatConsumed());
		dataset.setValue("Carbohydrates", getCarbohydratesConsumed());
		dataset.setValue("Protein", getProteinConsumed());
		
		ChartPanel pieChart = new ChartPanel(ChartFactory.createPieChart("Total Nutrients Consumed", dataset));
		pieChart.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
		pieChart.setMinimumDrawWidth(0);
		pieChart.setMaximumDrawWidth(Integer.MAX_VALUE);
		pieChart.setMinimumDrawHeight(0);
		pieChart.setMaximumDrawHeight(Integer.MAX_VALUE);
		detailsPanel.add(pieChart, BorderLayout.CENTER);
		
//List Area
		JPanel mealsPanel = new JPanel(new BorderLayout());
		mealsPanel.setBorder(new EmptyBorder(2, 2, 2, 0));
		content.add(mealsPanel, BorderLayout.WEST);
		
		DefaultListModel<Meal> listModel = new DefaultListModel<Meal>();
		for (int i = 0; i < _meals.size(); i++) {
			listModel.addElement(_meals.get(i));
		}
		
		JList<Meal> list = new JList<Meal>(listModel);
		list.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
					int selection = list.getSelectedIndex();
					hierarchy.push(new Pair<Integer, DataView>(selection, listModel.get(selection)));
					hierarchy.peek().getValue().changeView(hierarchy, content);
					
					_meals.remove(selection);
					listModel.remove(selection);
				}
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(180, 0));
		mealsPanel.add(listScrollPane,BorderLayout.CENTER);
		
		JPanel south = new JPanel(new BorderLayout());
		mealsPanel.add(south, BorderLayout.SOUTH);
		
		JButton btnRight = new JButton("Add Meal");
		btnRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MealList mealList = new MealList();
				hierarchy.push(new Pair<Integer, DataView>(0, mealList));
				mealList.changeView(hierarchy, content);
			}
		});
		south.add(btnRight, BorderLayout.EAST);
		
		JButton btnLeft = new JButton("Back");
		btnLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hierarchy.pop();
				hierarchy.peek().getValue().changeView(hierarchy, content);
			}
		});
		south.add(btnLeft, BorderLayout.WEST);
		
		content.revalidate();
		content.repaint();
	}

	@Override
	public void addItem(DataView item) {
		if (item instanceof Meal){
			_meals.add((Meal)item);
		}
		else{
			System.out.println("Adding non-Meal item to MealPeriod object is not allowed");
		}
	}
	
	public static void createDefault(){
		File f = new File("mealPeriods.ini");
		if(!f.exists()){
			ArrayList<MealPeriod> mealPeriods = new ArrayList<MealPeriod>();
			
			mealPeriods.add(new MealPeriod("Breakfast", "09:00", "600"));
			mealPeriods.add(new MealPeriod("Lunch", "14:00", "600"));
			mealPeriods.add(new MealPeriod("Dinner", "19:00", "600"));
			
			savePreferred(mealPeriods);
		}
	}
	
	public static void savePreferred(ArrayList<MealPeriod> mealPeriods){
		try {
			FileWriter fw = new FileWriter("mealPeriods.ini");
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i = 0; i < mealPeriods.size(); i++) {
				MealPeriod period = mealPeriods.get(i);
				bw.write(period.getName() + ";" + period.getMealTime() + ";" + period.getTargetCalories() + "\n");
			}
			
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<MealPeriod> loadPreferred(){
		ArrayList<MealPeriod> mealPeriods = new ArrayList<MealPeriod>();
		
		try {
			FileReader fr = new FileReader("mealPeriods.ini");
			BufferedReader br = new BufferedReader(fr);
			
			Iterator<String> lines = br.lines().iterator();
			while (lines.hasNext()){
				String line = lines.next();
				String[] param = line.split(";");
				
				mealPeriods.add(new MealPeriod(param[0], param[1], param[2]));
			}
			
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mealPeriods;
	}
}
