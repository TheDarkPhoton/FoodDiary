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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
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

import javafx.util.Pair;

public class Meal implements DataView, Serializable {
	private static final long serialVersionUID = 1955433511318756468L;
	private String _name;
//	private ImageIcon _img;
	private ArrayList<ProductRation> _products;

	public Meal(String name) {
		_name = name;
		_products = new ArrayList<ProductRation>();
	}
	
	public Meal(Meal m){
		_name = m.getName();
		_products = new ArrayList<ProductRation>();
		
		Iterator<ProductRation> i = m.getIterator();
		while (i.hasNext()){
			_products.add(i.next());
		}
	}
	
	public Iterator<ProductRation> getIterator(){
		return _products.iterator();
	}
	
	public void addProduct(Product p, Float grams){
		_products.add(new ProductRation(p, grams));
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
			float grams = _products.get(i).getGrams();
			Nutrition n = _products.get(i).getProduct().getQuantity(grams);
			
			total_grams += grams;
			cals += n.getCalories();
			f += n.getFat();
			c += n.getCarbs();
			p += n.getProtein();
		}
		
		return new Nutrition(total_grams, cals, f, c, p);
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
		
		DefaultListModel<ProductRation> listModel = new DefaultListModel<ProductRation>();
		for (int i = 0; i < _products.size(); i++) {
			listModel.addElement(_products.get(i));
		}
		
		JList<ProductRation> list = new JList<ProductRation>(listModel);
		list.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				float grams = listModel.get(list.getSelectedIndex()).getGrams();
				Product product = listModel.get(list.getSelectedIndex()).getProduct();
				
				Nutrition n = product.getQuantity(grams);
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
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(180, 0));
		mealsPanel.add(listScrollPane,BorderLayout.CENTER);
		
		JPanel south = new JPanel(new BorderLayout());
		mealsPanel.add(south, BorderLayout.SOUTH);
		
		JButton btnRight = new JButton("Add Product");
		btnRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProductList productList = new ProductList();
				hierarchy.push(new Pair<Integer, DataView>(0, productList));
				productList.changeView(hierarchy, content);
			}
		});
		south.add(btnRight, BorderLayout.EAST);
		
		JButton btnLeft = new JButton("Back");
		btnLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hierarchy.pop();
				if (!listModel.isEmpty())
					hierarchy.peek().getValue().addItem(Meal.this);
				hierarchy.peek().getValue().changeView(hierarchy, content);
			}
		});
		south.add(btnLeft, BorderLayout.WEST);
		
//Details' control area
		JPanel controlPanel = new JPanel(new BorderLayout());
		detailsPanel.add(controlPanel, BorderLayout.SOUTH);

		JPanel controlLeftPanel = new JPanel(new GridLayout(1, 3));
		controlPanel.add(controlLeftPanel, BorderLayout.WEST);
		
		JButton remove = new JButton("Remove");
		controlLeftPanel.add(remove);
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				_products.remove(list.getSelectedIndex());
				listModel.remove(list.getSelectedIndex());
			}
		});
		
		content.revalidate();
		content.repaint();
	}

	@Override
	public void addItem(DataView item) {
		System.out.println("To add product to a meal you must use AddProduct method.");
	}
}
