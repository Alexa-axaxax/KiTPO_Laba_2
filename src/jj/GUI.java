package jj;

import Tree.VerticalTree;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.event.ItemEvent;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class GUI extends JFrame {
    private VerticalTree verticalTree;
    
    private UserType sample;
    
    private JTextArea treeArea;
    private JTextArea outputArea;
    
    JComboBox<String> typeComboBox;
    private JTextField valueTextField;
    private JTextField indexTextField;
    
    private JButton addButton;
    private JButton getButton;
    private JButton removeButton;
    private JButton balanceButton;
    private JButton savebutton;
    private JButton loadButton;
    public GUI() {
        setTitle("Vertical tree");
        setPreferredSize(new Dimension(600, 600));
        setResizable(false);
       
        getContentPane().setLayout(new BorderLayout());
        treeArea = new JTextArea();
        treeArea.setEditable(false);
        treeArea.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane treeScrollPane = new JScrollPane(treeArea);
        treeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(treeScrollPane, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setRows(5);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.black));
        outputArea.setLineWrap(true);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outputScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(outputScrollPane, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(4, 1);
        gridLayout.setVgap(30);
        controlPanel.setLayout(gridLayout);
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        getContentPane().add(controlPanel, BorderLayout.EAST);


        JPanel inputButtonPanel = new JPanel(new BorderLayout());
        UserFactory userFactory = new UserFactory();
        typeComboBox = new JComboBox<>(new DefaultComboBoxModel<>(userFactory.getTypeNameList().toArray(new String[0])));
        typeComboBox.setSelectedIndex(0);
        sample = userFactory.getBuilderByName(typeComboBox.getSelectedItem().toString());
        verticalTree = new VerticalTree(sample);
        typeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = e.getItem().toString();
                sample = userFactory.getBuilderByName(item);
                verticalTree = new VerticalTree(sample);
                updateTreeView();
            }
        });
        controlPanel.add(typeComboBox);
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Value:"));
        valueTextField = new JTextField();
        inputPanel.add(valueTextField);

        addButton = new JButton("Add");
        inputButtonPanel.add(inputPanel, BorderLayout.CENTER);
        inputButtonPanel.add(addButton, BorderLayout.SOUTH);
        inputButtonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        addButton.addActionListener(e -> {
            UserType value;
            try {
                value = (UserType) sample.parseValue(valueTextField.getText());
            }
            catch (Exception ex) {
//                addErrorMessage("Can not parse " + userType.typeName() + " from text field");
                return;
            }
            addMessage(value + " was added to the tree");
            verticalTree.add(value);
            updateTreeView();
        });
        controlPanel.add(inputButtonPanel);

        JPanel elementPanel = new JPanel(new GridLayout(3, 1));
        indexTextField = new JTextField();
        indexTextField.setHorizontalAlignment(SwingConstants.CENTER);
        elementPanel.add(indexTextField);
        getButton = new JButton("Get at index");
        getButton.addActionListener(e -> {
            try {
                int index = java.lang.Integer.parseInt(indexTextField.getText());
                UserType t = verticalTree.get(index);
                addMessage(t + " is at index " + index);
            }
            catch (IndexOutOfBoundsException ex) {
                addErrorMessage("Input index is out of bounds");
            }
            catch (Exception ex) {
                addErrorMessage("Can not parse index from input index field");
            }
        });
        elementPanel.add(getButton);
        removeButton = new JButton("Remove at index");
        removeButton.addActionListener(e -> {
            try {
                int index = java.lang.Integer.parseInt(indexTextField.getText());
                UserType u = verticalTree.remove(index);
                updateTreeView();
                addMessage(u + " removed from index " + index);
            }
            catch (IndexOutOfBoundsException ex) {
                addErrorMessage("Input index is out of bounds");
            }
            catch (Exception ex) {
                addErrorMessage("Can not parse index from input index field");
            }
        });
        elementPanel.add(removeButton);
        elementPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        controlPanel.add(elementPanel);

        JPanel buttonPanel = new JPanel(new GridLayout(3,1));
        balanceButton = new JButton("Balance tree");
        buttonPanel.add(balanceButton);
        balanceButton.addActionListener(e -> {
            verticalTree.balance();
            updateTreeView();
            addMessage("Tree was balanced");
        });
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        savebutton = new JButton("Save tree");
        buttonPanel.add(savebutton);
        savebutton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.showSaveDialog(this);
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.print(verticalTree.serialize());
            } catch (Exception ex) {
                addErrorMessage("Can not save to file: " + ex.getMessage());
            }
        });
        loadButton = new JButton("Load tree");
        buttonPanel.add(loadButton);
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.showOpenDialog(this);
            try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())) {
                StringBuilder builder = new StringBuilder();
                while(scanner.hasNextLine()) {
                    builder.append(scanner.nextLine()).append(System.lineSeparator());
                }
                verticalTree = (VerticalTree) verticalTree.deserialize(builder.toString(), (Class<UserType>) sample.getClass());
                updateTreeView();
            } catch (Exception ex) {
                addErrorMessage("Can not load from file: " + ex.getMessage());
            }
        });
        controlPanel.add(buttonPanel);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    private void updateTreeView() {
        treeArea.setText(verticalTree.toString());
    }

    private void addMessage(String message) {
        outputArea.setText(outputArea.getText() + message + System.lineSeparator());
    }

    private void addErrorMessage(String message) {
        outputArea.setText(outputArea.getText() + "ERROR!: " + message + System.lineSeparator());
    }

    public static void main(String[] args) {
        UserFactory userFactory = new UserFactory();
        SwingUtilities.invokeLater(GUI::new);
//        GUI gui = new GUI();
    }
}
