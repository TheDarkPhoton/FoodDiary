package data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javafx.util.Pair;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import windows.SetMealPeriods;

public class Day implements DataView, Serializable {
	private static final long serialVersionUID = 5607525692382353006L;
	private ArrayList<Day> _days;
	private ArrayList<MealPeriod> _mealPeriods;
	private Date _dateOfDay;
	
	public Day(ArrayList<Day> days){
		this(new Date(new Date().getTime() - 24 * 60 * 60 * 1000), days);
	}
	
	public Day(Date previous, ArrayList<Day> days) {
		_dateOfDay = new Date(previous.getTime() + 24 * 60 * 60 * 1000);
		
		_days = days;
		_mealPeriods = MealPeriod.loadPreferred();
	}
	
	public Date getDate(){
		return _dateOfDay;
	}
	
	public String getStringDate(){
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		return df.format(_dateOfDay);
	}

	public int getTargetCalories(){
		int sum = 0;
		for (int i = 0; i < _mealPeriods.size(); i++) {
			sum += _mealPeriods.get(i).getTargetCalories();
		}
		return sum;
	}
	
	public float getCaloriesConsumed(){
		float sum = 0;
		for (int i = 0; i < _mealPeriods.size(); i++) {
			sum += _mealPeriods.get(i).getCaloriesConsumed();
		}
		return sum;
	}
	
	public float getFatConsumed(){
		float sum = 0;
		for (int i = 0; i < _mealPeriods.size(); i++) {
			sum += _mealPeriods.get(i).getFatConsumed();
		}
		return sum;
	}

	public float getCarbohydratesConsumed(){
		float sum = 0;
		for (int i = 0; i < _mealPeriods.size(); i++) {
			sum += _mealPeriods.get(i).getCarbohydratesConsumed();
		}
		return sum;
	}

	public float getProteinConsumed(){
		float sum = 0;
		for (int i = 0; i < _mealPeriods.size(); i++) {
			sum += _mealPeriods.get(i).getProteinConsumed();
		}
		return sum;
	}
	
	@Override
	public void changeView(Stack<Pair<Integer, DataView>> hierarchy, JPanel content) {
		content.removeAll();
		
//Details Area
		JPanel detailsPanel = new JPanel(new BorderLayout());
		detailsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		content.add(detailsPanel, BorderLayout.CENTER);
		
		JPanel titlePanel = new JPanel(new GridLayout(2, 1));
		detailsPanel.add(titlePanel, BorderLayout.NORTH);
		
		JLabel lblDate = new JLabel(getStringDate() + " - day " + (hierarchy.peek().getKey() + 1) + " of the Program", SwingUtilities.CENTER);
		lblDate.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
		titlePanel.add(lblDate);
		
		JLabel lblCaloriesConsumed = new JLabel(getCaloriesConsumed() + " Calories Consumed", SwingUtilities.CENTER);
		lblCaloriesConsumed.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
		titlePanel.add(lblCaloriesConsumed);
		
		JPanel detailsBottomPanel = new JPanel(new BorderLayout());
		detailsPanel.add(detailsBottomPanel, BorderLayout.SOUTH);
		
		detailsBottomPanel.add(new JLabel("Calories Consumed:"), BorderLayout.NORTH);
		JProgressBar progress = new JProgressBar(0, getTargetCalories());
		progress.setStringPainted(true);
		progress.setPreferredSize(new Dimension(0, 26));
		progress.setValue((int)getCaloriesConsumed());
		progress.setString(progress.getValue() + " / " + progress.getMaximum());
		detailsBottomPanel.add(progress, BorderLayout.CENTER);

		JButton newDay = new JButton("Start New Day");
		detailsBottomPanel.add(newDay, BorderLayout.EAST);
		
		int currentDay = hierarchy.peek().getKey();
		newDay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(null, "Do you want to use default Meal Periods?");
				
				if (answer == JOptionPane.CANCEL_OPTION)
					return;
				else if (answer == JOptionPane.NO_OPTION)
					new SetMealPeriods().setVisible(true);
				
				_days.add(new Day(_days.get(currentDay).getDate(), _days));
				int newDay = hierarchy.pop().getKey() + 1;
				hierarchy.push(new Pair<Integer, DataView>(newDay, _days.get(newDay)));
				hierarchy.peek().getValue().changeView(hierarchy, content);
			}
		});
		
		if (currentDay != _days.size() -1){
			newDay.setEnabled(false);
		}
		
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
		
		PiePlot plot = (PiePlot) pieChart.getChart().getPlot();
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
				"{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
		plot.setLabelGenerator(gen);
		
//List Area
		JPanel mealsPanel = new JPanel(new BorderLayout());
		mealsPanel.setBorder(new EmptyBorder(2, 2, 2, 0));
		content.add(mealsPanel, BorderLayout.WEST);
		
		DefaultListModel<MealPeriod> listModel = new DefaultListModel<MealPeriod>();
		for (int i = 0; i < _mealPeriods.size(); i++) {
			listModel.addElement(_mealPeriods.get(i));
		}
		
		JList<MealPeriod> list = new JList<MealPeriod>(listModel);
		list.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.isSelectionEmpty())
					return;

				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
					hierarchy.push(new Pair<Integer, DataView>(list.getSelectedIndex(), listModel.getElementAt(list.getSelectedIndex())));
					hierarchy.peek().getValue().changeView(hierarchy, content);
				}
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(180, 0));
		mealsPanel.add(listScrollPane,BorderLayout.CENTER);
		
		JPanel south = new JPanel(new BorderLayout());
		mealsPanel.add(south, BorderLayout.SOUTH);
		
		JButton btnRight = new JButton("Next");
		btnRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int nextDay = hierarchy.pop().getKey() + 1;
				hierarchy.push(new Pair<Integer, DataView>(nextDay, _days.get(nextDay)));
				hierarchy.peek().getValue().changeView(hierarchy, content);
			}
		});
		if (hierarchy.peek().getKey() == _days.size() - 1){
			btnRight.setEnabled(false);
		}
		south.add(btnRight, BorderLayout.EAST);
		
		JButton btnLeft = new JButton("Previous");
		btnLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int prevDay = hierarchy.pop().getKey() - 1;
				hierarchy.push(new Pair<Integer, DataView>(prevDay, _days.get(prevDay)));
				hierarchy.peek().getValue().changeView(hierarchy, content);
			}
		});
		if (hierarchy.peek().getKey() == 0){
			btnLeft.setEnabled(false);
		}
		south.add(btnLeft, BorderLayout.WEST);
		
		content.revalidate();
		content.repaint();
	}

	@Override
	public void addItem(DataView item) {
		System.out.println("Day's addItem called...");
	}
}
