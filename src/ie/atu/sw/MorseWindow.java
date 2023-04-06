package ie.atu.sw;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.text.DecimalFormat;

public class MorseWindow {
	private Colour[] colours = Colour.values();
	private ThreadLocalRandom rand = ThreadLocalRandom.current(); //This will definitely come in handy
	private JFrame win; //The GUI Window
	private JTextArea txtOutput = new JTextArea(); //The text box to output the results to
	private JTextField txtFilePath; //The file name to process
	private JPanel dot; //Moved dot declaration up here so I can modify it.
	private int clickCounter = 0;
	
	//Used to keep track of time, using the method we used in earlier labs.
	private float startTime = System.nanoTime(); 
	private float finalTime;
	private final DecimalFormat timeFormatter = new DecimalFormat("0.00");
	private final int nanoToSec = 1000000000;
	
	public MorseWindow(){
		/*
		 * Create a window for the application. Building a GUI is an example of 
		 * "divide and conquer" in action. A GUI is really a tree. That is why
		 * we are able to create and configure GUIs in XML.
		 */
		win = new JFrame();
		win.setTitle("Data Structures & Algorithms 2023 - Morse Encoder/Decoder");
		win.setSize(650, 400);
		win.setResizable(false);
		win.setLayout(new FlowLayout());
		
        /*
         * The top panel will contain the file chooser and encode / decode buttons
         */
        var top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.setBorder(new javax.swing.border.TitledBorder("Select File"));
        top.setPreferredSize(new Dimension(600, 80));

        txtFilePath =  new JTextField(20);
		txtFilePath.setPreferredSize(new Dimension(100, 30));

		
		var chooser = new JButton("Browse...");
		chooser.addActionListener((e) -> {
			var fc = new JFileChooser(System.getProperty("user.dir"));
			var val = fc.showOpenDialog(win);
			if (val == JFileChooser.APPROVE_OPTION) {
				var file = fc.getSelectedFile().getAbsoluteFile();
				txtFilePath.setText(file.getAbsolutePath());
			}
		});
		
		//Adds a save button, enabling users to save decoded/encoded text.
		//Opens FileChooser in the documents folder, and by default the file is named output.txt
		var saveChooser = new JButton("Save...");
		saveChooser.addActionListener((e) -> {
			var fc = new JFileChooser(System.getProperty("user.documents"));
			
			fc.setDialogTitle("Save output...");
			fc.setApproveButtonText("Save"); 
			fc.setSelectedFile(new File("output.txt")); //Default filename
			fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt")); //Default filetype
			
			var val = fc.showOpenDialog(win);
			if (val == JFileChooser.APPROVE_OPTION) {
				var file = fc.getSelectedFile().getAbsoluteFile();
				txtFilePath.setText(file.getAbsolutePath());
				
				saveResult(txtFilePath);
			}
		});
		
		//Passes the txtFilePath to the encode method, displays the encoded text.
		//When the encoding has been completed, the encoding time is displayed and the box color switches to green.
		var btnEncodeFile = new JButton("Encode");
		btnEncodeFile.addActionListener(e -> {
			startTime = System.nanoTime();
			
			StringBuilder encodedText = new StringBuilder(EncodeAndDecode.encode(txtFilePath));
			
			appendText(encodedText.toString());
			switchColour(colours[15]);
			finalTime = (System.nanoTime()-startTime)/nanoToSec;

			win.setTitle("Encoded in "+timeFormatter.format(finalTime)+"s!");
		});
		
		//Passes the txtFilePath to the decode method, displays the decoded text.
		//Like the encode button, also shows the time and box color changes.
		var btnDecodeFile = new JButton("Decode");
		btnDecodeFile.addActionListener(e -> {
			startTime = System.nanoTime();
			
			StringBuilder decodedText = new StringBuilder(EncodeAndDecode.decode(txtFilePath));
			
			replaceText(decodedText.toString());
			switchColour(colours[15]);
			finalTime = (System.nanoTime()-startTime)/nanoToSec;
			
			win.setTitle("Decoded in "+timeFormatter.format(finalTime)+"s!");
		});
		
		//Add all the components to the panel and the panel to the window
        top.add(txtFilePath);
        top.add(chooser);
        top.add(saveChooser);
        top.add(btnEncodeFile);
        top.add(btnDecodeFile);
        win.getContentPane().add(top); //Add the panel to the window
        
        
        /*
         * The middle panel contains the coloured square and the text
         * area for displaying the outputted text. 
         */
        var middle = new JPanel(new FlowLayout(FlowLayout.LEADING));
        middle.setPreferredSize(new Dimension(600, 200));

        dot = new JPanel();
        dot.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        dot.setBackground(getRandomColour());
        dot.setPreferredSize(new Dimension(140, 150));
        dot.addMouseListener(new MouseAdapter() { 
        	//Can't use a lambda against MouseAdapter because it is not a SAM
        	public void mousePressed( MouseEvent e ) {  
        		dot.setBackground(getRandomColour());
        		
        		//Easter egg implementation - if the panel is clicked 10 times, the title changes.
        		clickCounter++;
        		if(clickCounter ==  10) {
        			win.setTitle("Remember Windows ME?");
        		}
        	}
        });
        

        
        //Add the text area
		txtOutput.setLineWrap(true);
		txtOutput.setWrapStyleWord(true);
		txtOutput.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		
		var scroller = new JScrollPane(txtOutput);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setPreferredSize(new Dimension(450, 150));
		scroller.setMaximumSize(new Dimension(450, 150));
		
		//Add all the components to the panel and the panel to the window
		middle.add(dot);
		middle.add(scroller);
		win.getContentPane().add(middle);
		
		
		/*
		 * The bottom panel contains the clear and quit buttons.
		 */
		var bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setPreferredSize(new java.awt.Dimension(500, 50));

        //Create and add Clear and Quit buttons
        var clear = new JButton("Clear");
        clear.addActionListener((e) -> txtOutput.setText(""));
        
        var quit = new JButton("Quit");
        quit.addActionListener((e) -> System.exit(0));
        
        //Add all the components to the panel and the panel to the window
        bottom.add(clear);
        bottom.add(quit);
        win.getContentPane().add(bottom);       
        
        
        /*
         * All done. Now show the configured Window.
         */
		win.setVisible(true);
	}
	
	private Color getRandomColour() {
		Colour c = colours[rand.nextInt(0, colours.length)];
		return Color.decode(c.hex() + "");
	}
	
	protected void replaceText(String text) {
		txtOutput.setText(text);
	}
	
	protected void appendText(String text) {
		txtOutput.setText(txtOutput.getText() + " " + text);
	}
	
	//Switch the dot's colour. Used by the encode/decode methods to show completeness.
	private void switchColour(Colour col) {
		Color newColour = Color.decode(col.hex() + "");
		
		dot.setBackground(newColour);
	}
	
	//Save the current contents of txtOutput to a user-designated file.
	private void saveResult(JTextField file) {
		FileWriter filepath;
		try {
			filepath = new FileWriter(file.getText());
			try(var bw = new BufferedWriter(filepath)) {
				bw.write(txtOutput.getText());
				win.setTitle("Saved file!");
				
			} catch (IOException f) {
				f.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}