package windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Stack;

import javafx.util.Pair;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.Data;
import data.DataView;
import data.Day;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = -3960547557462012371L;
	
	private Stack<Pair<Integer, DataView>> _hierarchy;
	private ArrayList<Day> _days;
	
	private transient WindowAdapter _closingEvent = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			while (_hierarchy.size() > 1)
				_hierarchy.pop();
			
			try {
				FileOutputStream is = new FileOutputStream("dayRecords.dat");
				ObjectOutputStream os = new ObjectOutputStream(is);
				
				os.writeObject(new Data(_days, _hierarchy));
				
				os.close();
				is.close();
			} catch (IOException err) {
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
	};
	
	public MainWindow(){
		super("Meal Diary");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800, 600));

		loadData();
		initialise();
		
		addWindowListener(_closingEvent);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	private void loadData(){
		Data file = new Data();

		File f = new File("dayRecords.dat");
		if(!f.exists()){
			_days = file.getDays();
			_hierarchy = file.getHierarchy();
			_days.add(new Day(_days));
			_hierarchy.push(new Pair<Integer, DataView>(0, _days.get(0)));
			return;
		}
		
		try {
			FileInputStream is = new FileInputStream("dayRecords.dat");
			ObjectInputStream ois = new ObjectInputStream(is);
			
			Object o = ois.readObject();
			if (o instanceof Data){
				file = (Data)o;
			}
			
			ois.close();
			is.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_days = file.getDays();
		_hierarchy = file.getHierarchy();
	}
	
	private void initialise(){
		JPanel content = new JPanel(new BorderLayout());
		add(content, BorderLayout.CENTER);
		
		_hierarchy.peek().getValue().changeView(_hierarchy, content);
	}
}
