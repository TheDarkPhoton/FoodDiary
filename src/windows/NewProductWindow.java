package windows;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import data.Nutrition;
import data.Product;
import diary.Diary;

public class NewProductWindow extends JDialog {
	private static final long serialVersionUID = -7226213203047919561L;

	private Product _product = null;

	JTextField _txtName = new JTextField(10);
	private JTextField _txtGrams = new JTextField();
	private JTextField _txtCals = new JTextField();
	private JTextField _txtFat = new JTextField();
	private JTextField _txtCarbs = new JTextField();
	private JTextField _txtProtein = new JTextField();
	
	public NewProductWindow() {
		super(Diary.window, "New Product", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		initialise();
		
		pack();
		setLocationRelativeTo(null);
	}
	
	public NewProductWindow(Product p){
		super(Diary.window, "Edit Product", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		initialise();
		
		Nutrition n = p.getQuantity(100);
		
		_txtName.setText(p.getName());
		_txtGrams.setText("100");
		_txtCals.setText("" + n.getCalories());
		_txtFat.setText("" + n.getFat());
		_txtCarbs.setText("" + n.getCarbs());
		_txtProtein.setText("" + n.getProtein());
		
		pack();
		setLocationRelativeTo(null);
	}
	
	public Product getProduct(){
		return _product;
	}
	
	private void initialise(){
		JPanel productPanel = new JPanel(new BorderLayout());
		productPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(productPanel, BorderLayout.NORTH);
		productPanel.add(new JLabel("Product Name: "), BorderLayout.WEST);
		productPanel.add(_txtName, BorderLayout.CENTER);
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(new EmptyBorder(0, 10, 0, 10));
		add(center, BorderLayout.CENTER);
		
		JPanel dataPanel = new JPanel(new BorderLayout());
		center.add(dataPanel, BorderLayout.NORTH);
		
		JPanel labelPanel = new JPanel(new GridLayout(5, 1));
		dataPanel.add(labelPanel, BorderLayout.WEST);

		labelPanel.add(new JLabel("Grams: "));
		labelPanel.add(new JLabel("Calories: "));
		labelPanel.add(new JLabel("Fat: "));
		labelPanel.add(new JLabel("Carbohydrates: "));
		labelPanel.add(new JLabel("Protein: "));
		
		JPanel textPanel = new JPanel(new GridLayout(5, 1));
		dataPanel.add(textPanel, BorderLayout.CENTER);

		_txtGrams.setText("100");
		textPanel.add(_txtGrams);
		textPanel.add(_txtCals);
		textPanel.add(_txtFat);
		textPanel.add(_txtCarbs);
		textPanel.add(_txtProtein);
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(controlPanel, BorderLayout.SOUTH);
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		controlPanel.add(buttonPanel, BorderLayout.EAST);
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				float grams = 0;
				float cals = 0;
				float fat = 0;
				float carbs = 0;
				float protein = 0;
				
				try{
					grams = Float.parseFloat(_txtGrams.getText()); 
					cals = Float.parseFloat(_txtCals.getText());
					fat = Float.parseFloat(_txtFat.getText());
					carbs = Float.parseFloat(_txtCarbs.getText());
					protein = Float.parseFloat(_txtProtein.getText());
				}
				catch (NumberFormatException err){
					JOptionPane.showMessageDialog(null, "All fields must contain decimal numbers.");
					return;
				}
				
				if (grams <= 0){
					JOptionPane.showMessageDialog(null, "Grams entered must be more then 0.");
					return;
				}

				_product = new Product(_txtName.getText());
				_product.setNutrition(grams, cals, fat, carbs, protein);
				NewProductWindow.this.dispose();
			}
		});
		buttonPanel.add(save);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_product = null;
				NewProductWindow.this.dispose();
			}
		});
		buttonPanel.add(cancel);
	}
}
