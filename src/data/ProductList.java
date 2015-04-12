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
import java.io.Serializable;
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
import org.jfree.data.general.DefaultPieDataset;

import windows.NewProductWindow;

public class ProductList implements DataView, Serializable {
	private static final long serialVersionUID = 5986175942046848556L;

	private ArrayList<Product> _productList;
	
	@SuppressWarnings("unchecked")
	public ProductList() {
		File f = new File("productList.dat");
		if(!f.exists()){
			_productList = new ArrayList<Product>();
			return;
		}
		
		try {
			FileInputStream is = new FileInputStream("productList.dat");
			ObjectInputStream ois = new ObjectInputStream(is);
			
			Object o = ois.readObject();
			if (o instanceof ArrayList<?>){
				_productList = (ArrayList<Product>)o;
			}
			
			ois.close();
			is.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveProductList(){
		try {
			FileOutputStream is = new FileOutputStream("productList.dat");
			ObjectOutputStream os = new ObjectOutputStream(is);
			
			os.writeObject(_productList);
			
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
		
		JLabel lblDate = new JLabel("Product List", SwingUtilities.CENTER);
		lblDate.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 20));
		detailsPanel.add(lblDate, BorderLayout.NORTH);
		
		JPanel detailsCentrePanel = new JPanel(new BorderLayout());
		detailsPanel.add(detailsCentrePanel, BorderLayout.CENTER);
		
		JPanel centeredDataPanel = new JPanel();
		detailsCentrePanel.add(centeredDataPanel, BorderLayout.NORTH);
		
		JPanel dataPanel = new JPanel(new BorderLayout());
		centeredDataPanel.add(dataPanel);
		
		JPanel dataLabelPanel = new JPanel(new GridLayout(5, 1));
		dataPanel.add(dataLabelPanel, BorderLayout.WEST);

		dataLabelPanel.add(new JLabel("Grams: "));
		dataLabelPanel.add(new JLabel("Calories: "));
		dataLabelPanel.add(new JLabel("Fat: "));
		dataLabelPanel.add(new JLabel("Carbohidrates: "));
		dataLabelPanel.add(new JLabel("Protein: "));
		
		JPanel dataNumbersPanel = new JPanel(new GridLayout(5, 1));
		dataPanel.add(dataNumbersPanel, BorderLayout.CENTER);
		
		dataNumbersPanel.add(new JLabel("100g"));
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
		
//List Area
		JPanel mealsPanel = new JPanel(new BorderLayout());
		mealsPanel.setBorder(new EmptyBorder(2, 2, 2, 0));
		content.add(mealsPanel, BorderLayout.WEST);
		
		DefaultListModel<Product> listModel = new DefaultListModel<Product>();
		for (int i = 0; i < _productList.size(); i++) {
			listModel.addElement(_productList.get(i));
		}
		
		JList<Product> list = new JList<Product>(listModel);
		list.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				Product product = listModel.get(list.getSelectedIndex());
				
				Nutrition n = product.getQuantity(100);
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
					Product selectedProduct = listModel.get(list.getSelectedIndex());
					String input = JOptionPane.showInputDialog("Enter quantity of the ingrediant in grams.", "100");
					
					if (input == null) return;
					
					while (!input.matches("[-+]?[0-9]*\\.?[0-9]+")){
						input = JOptionPane.showInputDialog("Enter quantity of the ingrediant in grams.", "100");
						if (input == null) return;
					}
					
					hierarchy.pop();
					Meal meal = (Meal)hierarchy.peek().getValue();
					meal.addProduct(selectedProduct, Float.parseFloat(input));
					hierarchy.peek().getValue().changeView(hierarchy, content);
				}
			}
		});
		
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(180, 0));
		mealsPanel.add(listScrollPane, BorderLayout.CENTER);
		
		JPanel south = new JPanel(new BorderLayout());
		mealsPanel.add(south, BorderLayout.SOUTH);
		
		JButton btnRight = new JButton("New Product");
		btnRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewProductWindow wndProduct = new NewProductWindow();
				wndProduct.setVisible(true);
				
				Product p = wndProduct.getProduct();
				if (p != null){
					_productList.add(p);
					saveProductList();
					listModel.addElement(p);
				}
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
		
//details area buttons
		JPanel controlPanel = new JPanel(new BorderLayout());
		detailsPanel.add(controlPanel, BorderLayout.SOUTH);

		JPanel controlLeftPanel = new JPanel(new GridLayout(1, 3));
		controlPanel.add(controlLeftPanel, BorderLayout.WEST);
		
		JButton editProduct = new JButton("Edit Product");
		controlLeftPanel.add(editProduct);
		editProduct.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				int selection = list.getSelectedIndex();
				
				NewProductWindow wndProduct = new NewProductWindow(listModel.get(selection));
				wndProduct.setVisible(true);
				
				Product p = wndProduct.getProduct();
				if (p != null){
					_productList.remove(selection);
					_productList.add(selection, p);
					saveProductList();
					listModel.remove(selection);
					listModel.add(selection, p);
				}
			}
		});
		JButton deleteProduct = new JButton("Delete Product");
		controlLeftPanel.add(deleteProduct);
		deleteProduct.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;

				_productList.remove(list.getSelectedIndex());
				listModel.remove(list.getSelectedIndex());
				saveProductList();
			}
		});
		
		content.revalidate();
		content.repaint();
	}

	@Override
	public void addItem(DataView item) {
		if (item instanceof Product){
			_productList.add((Product)item);
			saveProductList();
		}
		else{
			System.out.println("Adding non-Product item to ProductList object is not allowed");
		}
	}
}
