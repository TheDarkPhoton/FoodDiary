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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

import javafx.util.Pair;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class MealList implements DataView {
	private ArrayList<Meal> _mealList;

	@SuppressWarnings("unchecked")
	public MealList(){
		File f = new File("mealList.dat");
		if(!f.exists()){
			_mealList = new ArrayList<Meal>();
			return;
		}
		
		try {
			FileInputStream is = new FileInputStream("mealList.dat");
			ObjectInputStream ois = new ObjectInputStream(is);
			
			Object o = ois.readObject();
			if (o instanceof ArrayList<?>){
				_mealList = (ArrayList<Meal>)o;
			}
			
			ois.close();
			is.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveMealList(){
		try {
			FileOutputStream is = new FileOutputStream("mealList.dat");
			ObjectOutputStream os = new ObjectOutputStream(is);
			
			os.writeObject(_mealList);
			
			os.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void changeView(Stack<Pair<Integer, DataView>> hierarchy, JPanel content) {
		content.removeAll();

//Details Area
		JPanel detailsPanel = new JPanel(new BorderLayout());
		detailsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		content.add(detailsPanel, BorderLayout.CENTER);
		
		JLabel lblDate = new JLabel("Add Meal", SwingUtilities.CENTER);
		lblDate.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
		detailsPanel.add(lblDate, BorderLayout.NORTH);
		
		JPanel detailsCentrePanel = new JPanel(new BorderLayout());
		detailsPanel.add(detailsCentrePanel, BorderLayout.CENTER);
		
		JPanel centeredDataPanel = new JPanel();
		detailsCentrePanel.add(centeredDataPanel, BorderLayout.NORTH);
		
		JPanel dataPanel = new JPanel(new BorderLayout());
		centeredDataPanel.add(dataPanel);
		
		JPanel dataLabelPanel = new JPanel(new GridLayout(4, 1));
		dataPanel.add(dataLabelPanel, BorderLayout.WEST);

		dataLabelPanel.add(new JLabel("Calories: "));
		dataLabelPanel.add(new JLabel("Fat: "));
		dataLabelPanel.add(new JLabel("Carbohidrates: "));
		dataLabelPanel.add(new JLabel("Protein: "));
		
		JPanel dataNumbersPanel = new JPanel(new GridLayout(4, 1));
		dataPanel.add(dataNumbersPanel, BorderLayout.CENTER);
		
		JLabel lblCals = new JLabel("N/A");
		dataNumbersPanel.add(lblCals);
		JLabel lblFat = new JLabel("N/A");
		dataNumbersPanel.add(lblFat);
		JLabel lblCarbs = new JLabel("N/A");
		dataNumbersPanel.add(lblCarbs);
		JLabel lblProtein = new JLabel("N/A");
		dataNumbersPanel.add(lblProtein);

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Fat", 0);
		dataset.setValue("Carbohydrates", 0);
		dataset.setValue("Protein", 0);
		
		ChartPanel pieChart = new ChartPanel(ChartFactory.createPieChart("Total Nutrients Consumed", dataset));
		pieChart.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
		pieChart.setMinimumDrawWidth(0);
		pieChart.setMaximumDrawWidth(Integer.MAX_VALUE);
		pieChart.setMinimumDrawHeight(0);
		pieChart.setMaximumDrawHeight(Integer.MAX_VALUE);
		detailsCentrePanel.add(pieChart, BorderLayout.CENTER);

		PiePlot plot = (PiePlot) pieChart.getChart().getPlot();
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
				"{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
		plot.setLabelGenerator(gen);
		
//List Area
		JPanel mealsPanel = new JPanel(new BorderLayout());
		mealsPanel.setBorder(new EmptyBorder(2, 2, 2, 0));
		content.add(mealsPanel, BorderLayout.WEST);
		
		DefaultListModel<Meal> listModel = new DefaultListModel<Meal>();
		for (int i = 0; i < _mealList.size(); i++) {
			listModel.addElement(_mealList.get(i));
		}
		
		JList<Meal> list = new JList<Meal>(listModel);
		list.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				Nutrition n = listModel.get(list.getSelectedIndex()).getTotalNutrition();
				lblCals.setText("" + n.getCalories());
				lblFat.setText("" + n.getFat());
				lblCarbs.setText("" + n.getCarbs());
				lblProtein.setText("" + n.getProtein());
				
				DefaultPieDataset dataset = new DefaultPieDataset();
				dataset.setValue("Fat", n.getFat());
				dataset.setValue("Carbohydrates", n.getCarbs());
				dataset.setValue("Protein", n.getProtein());
				
				pieChart.setChart(ChartFactory.createPieChart("Total Nutrients Consumed", dataset));
				pieChart.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
				
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
					Meal selectedMeal = new Meal(listModel.getElementAt(list.getSelectedIndex()));
					hierarchy.pop();
					hierarchy.peek().getValue().addItem(selectedMeal);
					hierarchy.peek().getValue().changeView(hierarchy, content);
				}
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(180, 0));
		mealsPanel.add(listScrollPane,BorderLayout.CENTER);
		
		JPanel south = new JPanel(new BorderLayout());
		mealsPanel.add(south, BorderLayout.SOUTH);
		
		JButton btnRight = new JButton("New Meal");
		btnRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter the name of the meal.", "some name");
				
				if (name == null) return;
				if (name.equals("")) return;
						
				Meal meal = new Meal(name);
				hierarchy.push(new Pair<Integer, DataView>(0, meal));
				hierarchy.peek().getValue().changeView(hierarchy, content);
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
		
//Details' control buttons
		JPanel controlPanel = new JPanel(new BorderLayout());
		detailsPanel.add(controlPanel, BorderLayout.SOUTH);
		
		JPanel controlLeftPanel = new JPanel(new GridLayout(1, 2));
		controlPanel.add(controlLeftPanel, BorderLayout.WEST);
		
		JButton editMeal = new JButton("Edit Meal");
		controlLeftPanel.add(editMeal);
		editMeal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				int selection = list.getSelectedIndex();
				hierarchy.push(new Pair<Integer, DataView>(selection, listModel.get(selection)));
				hierarchy.peek().getValue().changeView(hierarchy, content);
				
				_mealList.remove(selection);
				listModel.remove(selection);
				saveMealList();
			}
		});
		JButton deleteMeal = new JButton("Delete Meal");
		controlLeftPanel.add(deleteMeal);
		deleteMeal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				_mealList.remove(list.getSelectedIndex());
				listModel.remove(list.getSelectedIndex());
				saveMealList();
			}
		});
		
		content.revalidate();
		content.repaint();
	}
	
	@Override
	public void addItem(DataView item) {
		if (item instanceof Meal){
			_mealList.add((Meal)item);
			saveMealList();
		}
		else{
			System.out.println("Adding non-Meal item to MealList object is not allowed");
		}
	}
}