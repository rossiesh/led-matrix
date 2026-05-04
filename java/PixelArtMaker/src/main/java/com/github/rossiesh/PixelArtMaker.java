package com.github.rossiesh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class PixelArtMaker extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final File TREE_FILE = new File("tree");
	private static final File PIXELART_DIRECTORY = new File("Pixelart");
	JPanel contentPane = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel colorPanel = new JPanel();
	JPanel funtionsPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	JTextField textField = new JTextField();
	JTree tree;
	DefaultMutableTreeNode rootNode;
	static ArrayList<String> txtFileArray = new ArrayList<>();
	static ArrayList<String> storedColors = new ArrayList<>();
	static JButton[] buttons = new JButton[100];
	static JButton[] colorButtons = new JButton[9];
	static JButton[] functionButtons = new JButton[6];
	static RGB[] colors = new RGB[colorButtons.length];
	static JSlider sliderRed = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
	static JSlider sliderGreen = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
	static JSlider sliderBlue = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
	static JLabel labelRed = new JLabel();
	static JLabel labelGreen = new JLabel();
	static JLabel labelBlue = new JLabel();
	static JLabel labelColor = new JLabel();
	static File folder;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PixelArtMaker frame = new PixelArtMaker();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public PixelArtMaker() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 485);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setContentPane(contentPane);
		addWindowListener(new MyWindowListener());

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setBackground(Color.white);
		buttonPanel.setBounds(0, 0, 450, 450);
		buttonPanel.setLayout(new GridLayout(10, 10, 5, 5));
		buttonPanel.setBackground(Color.white);
		contentPane.add(buttonPanel);

		rootNode = loadTreeFromFile(TREE_FILE);
		if (rootNode == null) {
			rootNode = new DefaultMutableTreeNode("Pixelart");
		}
		tree = new JTree(rootNode);
		tree.setEditable(false);
		tree.addKeyListener(new MyKeyAdapter());

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					tree.setSelectionRow(tree.getClosestRowForLocation(e.getX(), e.getY()));
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null) {
						String newName = JOptionPane.showInputDialog(contentPane, "new name");
						if (newName != null && !newName.isEmpty()) {
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
							if (!isDuplicate(parent, newName)) {
								File oldFile = resolveTreeNodeFile(selectedNode);
								File newFile = resolveChildFile(parent, newName);
								oldFile.renameTo(newFile);
								selectedNode.setUserObject(newName);
								((DefaultTreeModel) tree.getModel()).nodeChanged(selectedNode);
							} else {
								JOptionPane.showMessageDialog(contentPane, "node already exists");
							}
						}
					}
				}
			}
		});

		sliderRed.setBounds(500, 50, 300, 26);
		sliderRed.setUI(new CustomSliderUI(sliderRed, 255, 0, 0));
		sliderRed.setMinorTickSpacing(1);
		sliderRed.setBackground(Color.white);
		sliderRed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				updateLabels();
			}
		});
		contentPane.add(sliderRed);

		sliderGreen.setBounds(500, 100, 300, 26);
		sliderGreen.setUI(new CustomSliderUI(sliderGreen, 0, 255, 0));
		sliderGreen.setMinorTickSpacing(1);
		sliderGreen.setBackground(Color.white);
		sliderGreen.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				updateLabels();
			}
		});
		contentPane.add(sliderGreen);

		sliderBlue.setBounds(500, 150, 300, 26);
		sliderBlue.setUI(new CustomSliderUI(sliderBlue, 0, 0, 255));
		sliderBlue.setMinorTickSpacing(1);
		sliderBlue.setBackground(Color.white);
		sliderBlue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				updateLabels();
			}
		});
		contentPane.add(sliderBlue);

		labelRed.setBorder(new LineBorder(Color.black, 2));
		labelRed.setBounds(817, 50, 40, 26);
		labelRed.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(labelRed);

		labelGreen.setBorder(new LineBorder(Color.black, 2));
		labelGreen.setBounds(817, 100, 40, 26);
		labelGreen.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(labelGreen);

		labelBlue.setBorder(new LineBorder(Color.black, 2));
		labelBlue.setBounds(817, 150, 40, 26);
		labelBlue.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(labelBlue);

		labelColor.setBounds(500, 203, 70, 70);
		labelColor.setOpaque(true);
		contentPane.add(labelColor);
		updateLabels();

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton();
			buttons[i].setFocusable(false);
			buttons[i].setRolloverEnabled(false);
			buttons[i].addMouseListener(new MyMouseAdapter());
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			buttons[i].setBackground(Color.black);
			buttons[i].setBorder(new LineBorder(Color.black, 1));
			buttonPanel.add(buttons[i]);
		}

		colorPanel.setBounds(655, 203, 202, 70);
		colorPanel.setLayout(new GridLayout(3, 5, 5, 5));
		colorPanel.setBackground(Color.white);
		contentPane.add(colorPanel);

		scrollPane.setBounds(870, 50, 200, 360);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(tree);

		textField.setBounds(500, 377, 357, 33);
		contentPane.add(textField);
		textField.setColumns(10);

		funtionsPanel.setBackground(new Color(255, 255, 255));
		funtionsPanel.setBounds(500, 320, 357, 50);
		contentPane.add(funtionsPanel);
		funtionsPanel.setLayout(new GridLayout(2, 3, 20, 10));

		for (int i = 0; i < functionButtons.length; i++) {
			functionButtons[i] = new JButton();
			functionButtons[i].setFocusable(false);
			functionButtons[i].setRolloverEnabled(false);
			functionButtons[i].setMargin(new Insets(0, 0, 0, 0));
			functionButtons[i].setBackground(Color.white);
			functionButtons[i].setBorder(new LineBorder(Color.black, 1));
			functionButtons[i].addActionListener(this);
			funtionsPanel.add(functionButtons[i]);
		}
		functionButtons[0].setText("clear all");
		functionButtons[1].setText("save");
		functionButtons[2].setText("restore");
		functionButtons[3].setText("new");
		functionButtons[4].setText("delete");
		functionButtons[5].setText("output");

		colors[0] = new RGB(255, 0, 0);
		colors[1] = new RGB(0, 255, 0);
		colors[2] = new RGB(0, 0, 255);
		colors[3] = new RGB(255, 255, 0);
		colors[4] = new RGB(0, 255, 255);
		colors[5] = new RGB(255, 0, 255);
		colors[6] = new RGB(255, 255, 255);
		colors[7] = new RGB(255, 165, 0);
		colors[8] = new RGB(255, 192, 203);

		for (int i = 0; i < colorButtons.length; i++) {
			colorButtons[i] = new JButton();
			colorButtons[i].setOpaque(true);
			colorButtons[i].setBackground(new Color(colors[i].red, colors[i].green, colors[i].blue));
			colorButtons[i].setFocusable(false);
			colorButtons[i].setRolloverEnabled(false);
			colorButtons[i].addActionListener(this);
			colorPanel.add(colorButtons[i]);
		}
	}

	DefaultMutableTreeNode loadTreeFromFile(File file) {
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
			return (DefaultMutableTreeNode) inputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	public class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TREE_FILE))) {
				outputStream.writeObject(rootNode);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void updateLabels() {
		labelRed.setText(Integer.toString(sliderRed.getValue()));
		labelGreen.setText(Integer.toString(sliderGreen.getValue()));
		labelBlue.setText(Integer.toString(sliderBlue.getValue()));
		labelColor.setBackground(new Color(sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue()));
		if (sliderRed.getValue() + sliderGreen.getValue() + sliderBlue.getValue() >= 720) {
			labelColor.setBorder(new LineBorder(Color.black, 2));
		} else {
			labelColor.setBorder(new LineBorder(Color.white, 2));
		}
	}

	public static void updateSLiders(int red, int green, int blue) {
		sliderRed.setValue(red);
		sliderGreen.setValue(green);
		sliderBlue.setValue(blue);
	}

	public class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			for (int i = 0; i < 100; i++) {
				if (e.getSource() == buttons[i] && SwingUtilities.isLeftMouseButton(e)) {
					buttons[i].setBackground(
							new Color(sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue()));
				}
				if (e.getSource() == buttons[i] && SwingUtilities.isMiddleMouseButton(e)) {
					updateSLiders(buttons[i].getBackground().getRed(), buttons[i].getBackground().getGreen(),
							buttons[i].getBackground().getBlue());
				}
				if (e.getSource() == buttons[i] && SwingUtilities.isRightMouseButton(e)) {
					buttons[i].setBackground(Color.black);
					buttons[i].setBorder(new LineBorder(Color.black, 1));
				}
			}
		}
	}

	public class MyKeyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				readFile("restore");
			}
		}
	}

	public void addNode(String nodeName) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (selectedNode.getLevel() == 0 || selectedNode.getLevel() == 1) {
			selectedNode.add(new DefaultMutableTreeNode(nodeName));
			((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
		}
		if (selectedNode.getLevel() == 2) {
			selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
			selectedNode.insert(new DefaultMutableTreeNode(nodeName),
					selectedNode.getIndex((TreeNode) tree.getLastSelectedPathComponent()) + 1);
			((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
		}
	}

	public void deleteSelectedNode() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (selectedNode != null && selectedNode != rootNode) {
			if (JOptionPane.showConfirmDialog(contentPane, "delete?", "",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (selectedNode.getLevel() == 1) {
					for (int i = 0; i < selectedNode.getChildCount(); i++) {
						folder = resolveTreeNodeFile((DefaultMutableTreeNode) selectedNode.getChildAt(i));
						folder.delete();
					}
					folder = resolveTreeNodeFile(selectedNode);
					folder.delete();
				} else {
					folder = resolveTreeNodeFile(selectedNode);
					folder.delete();
				}
				selectedNode.removeFromParent();
				((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
				for (int i = 0; i < buttons.length; i++) {
					buttons[i].setBackground(Color.black);
				}
			}
		} else {
			JOptionPane.showMessageDialog(contentPane, "select a node (except root) to delete.");
		}
	}

	boolean isDuplicate(DefaultMutableTreeNode parent, String newName) {
		if (parent != null) {
			for (int i = 0; i < parent.getChildCount(); i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
				if (newName.equals(child.getUserObject().toString())) {
					return true;
				}
			}
		}
		return false;
	}

	public File checkIfFileExists(DefaultMutableTreeNode selectedNode) {
		return resolveTreeNodeFile(selectedNode);
	}

	public void readFile(String mode) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (selectedNode != null) {
			if (checkIfFileExists(selectedNode).exists()) {
				if (selectedNode.getChildCount() == 0) {
					File file = resolveTreeNodeFile(selectedNode);
					try {
						storedColors.clear();
						String temp;
						BufferedReader reader = new BufferedReader(new FileReader(file));
						while ((temp = reader.readLine()) != null) {
							storedColors.add(temp);
						}
						reader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (mode.equals("restore") && selectedNode.getChildCount() == 0) {
					for (int i = 0; i < buttons.length; i++) {
						buttons[i].setBackground(new Color(Integer.parseInt(storedColors.get(i * 3)),
								Integer.parseInt(storedColors.get(i * 3 + 1)),
								Integer.parseInt(storedColors.get(i * 3 + 2))));
					}
				}
				if (mode.equals("output")) {
					JTextArea textArea = new JTextArea();
					textArea.setLineWrap(true);
					textArea.setWrapStyleWord(true);
					if (selectedNode.getChildCount() != 0) {
						for (int i = 0; i < selectedNode.getChildCount(); i++) {
							File file = resolveTreeNodeFile((DefaultMutableTreeNode) selectedNode.getChildAt(i));
							try {
								System.out.println(i + "/" + selectedNode.getChildCount());
								storedColors.clear();
								String temp;
								BufferedReader reader = new BufferedReader(new FileReader(file));
								while ((temp = reader.readLine()) != null) {
									storedColors.add(temp);
								}
								reader.close();

							} catch (IOException e1) {
								e1.printStackTrace();
							}
							textArea.setText(textArea.getText() + "case " + i + ":\n");
							for (int i1 = 0; i1 < buttons.length; i1++) {
								textArea.setText(textArea.getText() + "led[" + i1 + "].setRGB("
										+ storedColors.get(i1 * 3) + ", " + storedColors.get(i1 * 3 + 1) + ", "
										+ storedColors.get(i1 * 3 + 2) + ");" + "\n");
							}
							textArea.setText(textArea.getText() + "FastLED.setBrightness(animationBrightness);\n"
									+ "FastLED.show();\n" + "currentState =" + (i + 1) + ";\n" + "break;");
						}
					} else {
						System.out.println("test");
						for (int i = 0; i < buttons.length; i++) {
							textArea.setText(textArea.getText() + "led[" + i + "].setRGB(" + storedColors.get(i * 3)
									+ ", " + storedColors.get(i * 3 + 1) + ", " + storedColors.get(i * 3 + 2) + ");"
									+ "\n");
						}
						textArea.setText(textArea.getText() + "FastLED.setBrightness(animationBrightness);\n"
								+ "FastLED.show();\n");
					}
					JScrollPane scrollPane = new JScrollPane(textArea);
					scrollPane.setPreferredSize(new Dimension(400, 200));
					JOptionPane.showMessageDialog(contentPane, scrollPane);
				}
			} else {
				JOptionPane.showMessageDialog(contentPane, "no file found");
			}
		} else {
			JOptionPane.showMessageDialog(contentPane, "select node");
		}
	}

	public ArrayList<TreeNode> getPathToSelectedNode(DefaultMutableTreeNode selectedNode) {
		ArrayList<TreeNode> pathToNode = new ArrayList<TreeNode>();
		TreeNode[] temp = selectedNode.getPath();
		for (TreeNode node : temp) {
			pathToNode.add(node);
		}
		pathToNode.removeFirst();
		return pathToNode;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < colorButtons.length; i++) {
			if (e.getSource() == colorButtons[i]) {
				updateSLiders(colors[i].red, colors[i].green, colors[i].blue);
			}
		}
		if (e.getSource() == functionButtons[0]) {
			if (JOptionPane.showConfirmDialog(contentPane, "clear?", "",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				for (int i = 0; i < buttons.length; i++) {
					buttons[i].setBackground(Color.black);
				}
				JOptionPane.showMessageDialog(contentPane, "cleared");
			}
		}
		if (e.getSource() == functionButtons[1]) {
			if ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent() != null) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				File file = resolveTreeNodeFile((DefaultMutableTreeNode) selectedNode.getParent());
				if (file.exists() && file.isFile()) {
					file.delete();
				}
				if (selectedNode != null && selectedNode.getChildCount() == 0 && !selectedNode.isRoot()) {
					try {
						boolean isFirstLine = true;
						File targetFile = resolveTreeNodeFile(selectedNode);
						folder = targetFile.getParentFile();
						if (folder != null) {
							folder.mkdirs();
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
						for (int i = 0; i < buttons.length; i++) {
							if (isFirstLine) {
								writer.write("" + buttons[i].getBackground().getRed());
								writer.write(System.lineSeparator() + buttons[i].getBackground().getGreen());
								writer.write(System.lineSeparator() + buttons[i].getBackground().getBlue());
							} else {
								writer.write(System.lineSeparator() + buttons[i].getBackground().getRed());
								writer.write(System.lineSeparator() + buttons[i].getBackground().getGreen());
								writer.write(System.lineSeparator() + buttons[i].getBackground().getBlue());
							}
							isFirstLine = false;
						}
						writer.close();
						JOptionPane.showMessageDialog(contentPane, "backup successful");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(contentPane, "backup not possible");
				}
			} else {
				JOptionPane.showMessageDialog(contentPane, "select node");
			}
		}
		if (e.getSource() == functionButtons[2]) {
			readFile("restore");
		}
		if (e.getSource() == functionButtons[3]) {
			if ((TreeNode) tree.getLastSelectedPathComponent() == null) {
				JOptionPane.showMessageDialog(contentPane, "select node");
				textField.setText("");
				return;
			}
			ArrayList<String> existingNodes = new ArrayList<String>();
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (selectedNode.getLevel() == 1) {
				existingNodes.clear();
				for (int i = 0; i < tree.getModel().getChildCount(selectedNode); i++) {
					existingNodes.add("" + tree.getModel().getChild(selectedNode, i));
				}
			}
			if (selectedNode.getLevel() == 2) {
				existingNodes.clear();
				for (int i = 0; i < tree.getModel().getChildCount(selectedNode.getParent()); i++) {
					existingNodes.add("" + tree.getModel().getChild(selectedNode.getParent(), i));
				}
			}
			if (textField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(contentPane, "input is empty");
				return;
			}
			if (existingNodes.contains(textField.getText())) {
				JOptionPane.showMessageDialog(contentPane, "node already exists");
				textField.setText("");
				return;
			}
			addNode(textField.getText().replaceAll("[\\\\/:*?\"<>|]", ""));
			textField.setText("");
		}
		if (e.getSource() == functionButtons[4]) {
			deleteSelectedNode();
		}
		if (e.getSource() == functionButtons[5]) {
			readFile("output");
		}
	}

	private File resolveTreeNodeFile(DefaultMutableTreeNode selectedNode) {
		File file = PIXELART_DIRECTORY;
		for (TreeNode node : getPathToSelectedNode(selectedNode)) {
			file = new File(file, node.toString());
		}
		return file;
	}

	private File resolveChildFile(DefaultMutableTreeNode parentNode, String childName) {
		if (parentNode == null || parentNode.isRoot()) {
			return new File(PIXELART_DIRECTORY, childName);
		}
		return new File(resolveTreeNodeFile(parentNode), childName);
	}
}
