package windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import data.MealPeriod;
import diary.Diary;

public class SetMealPeriods extends JDialog {
	private static final long serialVersionUID = -5941331269792908234L;
	private ArrayList<MealPeriod> _mealPeriods;

	private MealPeriod _currentMealPeriod = null;
	
	private JButton _up = new JButton("Up");
	private JButton _down = new JButton("Down");
	
	private JTextField _txtName = new JTextField(10);
	private JTextField _txtCals = new JTextField(10);
	private JTextField _txtTime = new JTextField(10);
	
	private WindowListener _closingEvent = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			MealPeriod.savePreferred(_mealPeriods);
		}
	};
	
	public SetMealPeriods() {
		super(Diary.window, "Customize Meal Periods", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		addWindowListener(_closingEvent);
		
		_mealPeriods = MealPeriod.loadPreferred();
		initialise();
		
		pack();
		setLocationRelativeTo(null);
	}
	
	private void initialise(){
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(leftPanel, BorderLayout.WEST);
		
		DefaultListModel<MealPeriod> listModel = new DefaultListModel<MealPeriod>();
		for (int i = 0; i < _mealPeriods.size(); i++) {
			listModel.addElement(_mealPeriods.get(i));
		}
		
		JList<MealPeriod> list = new JList<MealPeriod>(listModel);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(100, 200));
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		list.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				int selection = list.getSelectedIndex();
				
				_currentMealPeriod = listModel.get(selection);
				updateTextFields();
				
				if (listModel.size() == 1){
					_up.setEnabled(false);
					_down.setEnabled(false);
				}
				if (selection == 0){
					_up.setEnabled(false);
					_down.setEnabled(true);
				}
				else if (selection == listModel.size() - 1){
					_up.setEnabled(true);
					_down.setEnabled(false);
				}
				else{
					_up.setEnabled(true);
					_down.setEnabled(true);
				}
			}
		});
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				if (e.getKeyCode() == KeyEvent.VK_DELETE){
					int selection = list.getSelectedIndex();
					_mealPeriods.remove(selection);
					listModel.remove(selection);
				}
			}
		});
		
		JPanel listButtons = new JPanel(new BorderLayout());
		leftPanel.add(listButtons, BorderLayout.SOUTH);
		
		_up.setEnabled(false);
		listButtons.add(_up, BorderLayout.WEST);
		_up.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				int selection = list.getSelectedIndex();
				
				MealPeriod mp = listModel.get(selection);
				
				_mealPeriods.remove(selection);
				listModel.remove(selection);
				_mealPeriods.add(selection - 1, mp);
				listModel.add(selection - 1, mp);
				
				list.setSelectedIndex(selection - 1);
				
				if (selection - 1 == 0)
					_up.setEnabled(false);
				
				if (selection == listModel.size() - 1)
					_down.setEnabled(true);
			}
		});
		
		_down.setEnabled(false);
		listButtons.add(_down, BorderLayout.EAST);
		_down.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.isSelectionEmpty())
					return;
				
				int selection = list.getSelectedIndex();
				
				MealPeriod mp = listModel.get(selection);
				
				_mealPeriods.remove(selection);
				listModel.remove(selection);
				_mealPeriods.add(selection + 1, mp);
				listModel.add(selection + 1, mp);
				
				list.setSelectedIndex(selection + 1);
				
				if (selection + 1 == listModel.size() - 1)
					_down.setEnabled(false);
				
				if (selection == 0)
					_up.setEnabled(true);
			}
		});
		
		JPanel centrePanel = new JPanel(new BorderLayout());
		centrePanel.setBorder(new EmptyBorder(2, 0, 2, 2));
		add(centrePanel, BorderLayout.CENTER);
		
		JPanel inputPanel = new JPanel(new BorderLayout());
		centrePanel.add(inputPanel, BorderLayout.NORTH);
		
		JPanel inputLabelPanel = new JPanel(new GridLayout(3, 1));
		inputPanel.add(inputLabelPanel, BorderLayout.WEST);
		
		inputLabelPanel.add(new JLabel("Period Name: "));
		inputLabelPanel.add(new JLabel("Calories: "));
		inputLabelPanel.add(new JLabel("Time: "));
		
		JPanel inputTextPanel = new JPanel(new GridLayout(3, 1));
		inputPanel.add(inputTextPanel, BorderLayout.CENTER);
		
		inputTextPanel.add(_txtName);
		inputTextPanel.add(_txtCals);
		inputTextPanel.add(_txtTime);
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		centrePanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton newPeriod = new JButton("New Period");
		buttonPanel.add(newPeriod, BorderLayout.WEST);
		newPeriod.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_currentMealPeriod = new MealPeriod("Name Period", "00:00", "0");
				updateTextFields();
			}
		});
		
		JButton apply = new JButton("Apply Changes");
		buttonPanel.add(apply, BorderLayout.EAST);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				_currentMealPeriod.setName(_txtName.getText());
				_currentMealPeriod.setMealCalories(_txtCals.getText());
				_currentMealPeriod.setMealTime(_txtTime.getText());
				
				if (listModel.contains(_currentMealPeriod)){
					list.repaint();
				}
				else{
					_mealPeriods.add(_currentMealPeriod);
					listModel.addElement(_currentMealPeriod);
				}
			}
		});
	}
	
	private void updateTextFields(){
		if (_currentMealPeriod == null)
			return;
		
		_txtName.setText(_currentMealPeriod.getName());
		_txtCals.setText(_currentMealPeriod.getMealCalories());
		_txtTime.setText(_currentMealPeriod.getMealTime());
	}
}
