import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 
 * --------------------------------------------------------------------
 * ⚠️ WARNING | THIS TEST ASSUMES `root` IS PUBLIC ON THE RBT CLASS ⚠️
 * --------------------------------------------------------------------
 * 
 * @version 1.0.1
 * @author Saif (syntex)
 */
public class RBTVisualiser extends JPanel {
private RBT rbt;
    private int OFFSET = 50;
    private int YFACTOR = 40;
    private JTextField searchField;

    private double scale = 1.0;

    private void search(String value){
        if (!value.isEmpty()) {
            value = value.trim().replace("\r", "").replace("\n", "").replace(" ", "");
            for(String i : value.split(",")){
                if(i.contains("-")){
                    String[] range = i.split("-");
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    for(int j = start; j <= end; j++){
                        rbt.insert(j);
                    }
                }else{
                    int num = Integer.parseInt(i);
                    rbt.insert(num);
                }
            }
            repaint();
        }
        searchField.setText("");
    }

    public RBTVisualiser(RBT rbt) {
        this.rbt = rbt;
        this.searchField = new JTextField(10);
        JButton addButton = new JButton("Add Node");
        JButton clearButton = new JButton("Clear Tree");
        JButton randomButton = new JButton("Insert random Node");
        JComboBox<String> comboSelector;


        this.searchField.setText("1-20");


        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String value = searchField.getText();
                    search(value);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
            
        });

        randomButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                search(String.valueOf(ThreadLocalRandom.current().nextInt(100)));
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

            
        });

        clearButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                rbt.root = null;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

            
        });

        addButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                String value = searchField.getText();
                search(value);
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
            
        });

        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);

        setLayout(new BorderLayout());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(searchField);
        bottomPanel.add(addButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(randomButton);

        // Add combo selector to bottom panel
        List<String> methods = new ArrayList<>();

        //add all the inner class RBTTest methods to the combo box
        for (java.lang.reflect.Method method : RBTTest.class.getDeclaredMethods()) {
            methods.add(method.getName() + "()");
        }

        methods.remove("testAll()");
        methods.add("testAll()");


        comboSelector = new JComboBox<>(methods.toArray(new String[methods.size()]));
        bottomPanel.add(comboSelector);

        comboSelector.addActionListener(e -> {
            String method = (String) comboSelector.getSelectedItem().toString().replace("()", "");
            if (method != null) {
                RBTTest tester = new RBTTest();
                
                //run a method from the tester class
                try {
                    this.rbt.root = null;
                    java.lang.reflect.Method m = tester.getClass().getMethod(method);
                    m.invoke(tester);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    // Display a popup message with the entered name
                    JOptionPane.showMessageDialog(null, "Your test failed: " + ex.getLocalizedMessage(), "Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        



        bottomPanel.setBackground(Color.black);
        add(bottomPanel, BorderLayout.SOUTH);

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.black);

        JLabel label = new JLabel("You can add multiple nodes with a comma 1,2,3,4,5");
        label.setForeground(Color.white);
        textPanel.add(label);
        

        // Add a new line between the labels
        textPanel.add(Box.createVerticalStrut(10));

        JLabel label2 = new JLabel("You can add numbers in a range 1-20");
        label2.setForeground(Color.white);
        textPanel.add(label2);

        add(textPanel, BorderLayout.NORTH);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background color
        setBackground(Color.BLACK);

        // Set font and color for drawing nodes
        g.setFont(g.getFont().deriveFont(16f));
        g.setColor(Color.WHITE);

        scale = 1.0;

        int height = rbt.maxHeight();

        int scale = 4;

        scale -= height / 20;

        // Draw the RBT recursively
        drawNode((Graphics2D) g, rbt.root, (getWidth()-OFFSET) / 2 + OFFSET/2, YFACTOR, (getWidth()-OFFSET) / scale);
    }

    private void drawNode(Graphics2D g, Node node, int x, int y, int xOffset) {

        if (node == null) {
            return;
        }
    
        // Draw left child and connect with a line
        if (node.getLeft() != null) {
            int childX = x - xOffset;
            int childY = y + YFACTOR;
            g.drawLine(x, y, childX, childY);
            drawNode(g, node.getLeft(), childX, childY, xOffset / 2);
        }
    
        // Draw right child and connect with a line
        if (node.getRight() != null) {
            int childX = x + xOffset;
            int childY = y + YFACTOR;
            g.drawLine(x, y, childX, childY);
            drawNode(g, node.getRight(), childX, childY, xOffset / 2);
        }
    
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        // Draw the current node
        if(node.getColor() == Node.Color.RED){
            g.setColor(Color.red);
        }else{
            g.setColor(Color.gray);
        }
    
        int size = 30;
        int scaledSize = size - (int)(size * (scale-1));
    
        g.fillOval(x - 15, y - 15, scaledSize, scaledSize);
    
        // Center the string inside the oval
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(String.valueOf(node.getData()));
        int stringHeight = fm.getAscent();
        int stringX = x - stringWidth / 2;
        int stringY = y + stringHeight / 2;
    
        // Draw the string
        g.setColor(Color.white);
        g.drawString(String.valueOf(node.getData()), stringX, stringY);
    }

    /**
 * RBTTest
 * 
 * --------------------------------------------------------------------
 * ⚠️ WARNING | THIS TEST ASSUMES `root` IS PUBLIC ON THE RBT CLASS ⚠️
 * --------------------------------------------------------------------
 * 
 * @version 1.0.1
 * @author Daniel Wilson (dancodes.online)
 */
private class RBTTest {

    public void testAll() throws IllegalAccessException, InvocationTargetException{
    
        Method[] methods = RBTTest.class.getDeclaredMethods();
    
        for(int i = 0; i < methods.length; i++){
            rbt.root = null;
            if (!methods[i].getName().equals("testAll")) {
                System.out.println(methods[i].getName());
                methods[i].invoke(RBTTest.this);
            }
        }

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, "All tests have been passed!");
    }
    

    public void testMaxHeight() {

        RBT tree = RBTVisualiser.this.rbt;

        tree.insert(10);

        assertEquals(1, tree.maxHeight());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testMaxHeightDuplicates() {

        RBT tree = RBTVisualiser.this.rbt;

        tree.insert(10);
        tree.insert(10);

        assertEquals(1, tree.maxHeight());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testMaxHeight_2() {
        RBT tree = RBTVisualiser.this.rbt;

        tree.insert(10);
        tree.insert(5);

        assertEquals(2, tree.maxHeight());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }
    public void testTreeInsert() {
        rbt.insert(10);
        rbt.insert(5);
        rbt.insert(15);

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.RED, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.RED, rbt.root.getRight().getColor());

        // Display a popup message with the entered name
                JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testInsertionCase1() {
        rbt.insert(10);

        assertEquals(Node.Color.BLACK, rbt.root.getColor());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testInsertionCase2_Left() {
        rbt.insert(10);
        rbt.insert(5);
        rbt.insert(15);
        rbt.insert(0);

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getColor());
        assertEquals(Node.Color.RED, rbt.root.getLeft().getLeft().getColor());
        assertEquals(null, rbt.root.getRight().getLeft());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testInsertionCase2_Right() {
        rbt.insert(10);
        rbt.insert(5);
        rbt.insert(15);
        rbt.insert(20);

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getColor());
        assertEquals(Node.Color.RED, rbt.root.getRight().getRight().getColor());
        assertEquals(null, rbt.root.getRight().getLeft());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }


    public void testInsertionCase3_Left() {
        rbt.insert(10);
        rbt.insert(12);
        rbt.insert(11);

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.RED, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.RED, rbt.root.getRight().getColor());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testInsertionCase3_Right() {
        rbt.insert(10);
        rbt.insert(8);
        rbt.insert(9);

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.RED, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.RED, rbt.root.getRight().getColor());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testBigTree() {
        for (int i = 100; i > 0; i--) {
            rbt.insert(i);
        }

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getColor());
        assertEquals(Node.Color.RED, rbt.root.getLeft().getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getRight().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getRight().getColor());

        assertEquals(69, rbt.root.getData());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testRandomBigTree() {
        List<Integer> nums = List.of(11, 49, 43, 44, 7, 25, 22, 76, 34, 66, 77, 86, 46, 61, 21, 17, 91, 8, 4, 47, 24,
                20, 1, 63, 38, 27, 29, 80, 26, 88, 97, 48, 36, 89, 28, 6, 99, 42, 14, 68, 59, 37, 79, 69, 84, 50, 92,
                13, 55, 23, 30, 39, 98, 54, 5, 16, 41, 33, 81, 93, 40, 2, 19, 12, 70, 3, 32, 15, 45, 10, 35, 18, 73, 9,
                95, 56, 62, 78, 60, 53, 67, 57, 72, 65, 71, 90, 58, 74, 94, 75, 96, 85, 100, 52, 83, 87, 82, 31, 64,
                51);

        for (int i : nums) {
            rbt.insert(i);
        }

        assertEquals(Node.Color.BLACK, rbt.root.getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getLeft().getRight().getColor());
        assertEquals(Node.Color.RED, rbt.root.getRight().getLeft().getColor());
        assertEquals(Node.Color.BLACK, rbt.root.getRight().getRight().getColor());

        assertEquals(43, rbt.root.getData());
        assertEquals(9, rbt.maxHeight());

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");
    }

    public void testLargeTree() {
        long start = System.currentTimeMillis();

        for (int i = 100000; i > 0; i--) {
            rbt.insert(i);
        }

        long end = System.currentTimeMillis();

        assertEquals(67233, rbt.root.getData());
        assertEquals(rbt.maxHeight(), 31);

        assertTrue(end - start < 5000);

        // Display a popup message with the entered name
        JOptionPane.showMessageDialog(null, new Object(){}.getClass().getEnclosingMethod().getName() + "() Passed!");     
    }

}



    public static void main(String[] args) {
        RBT rbt = new RBT();
        // Perform insertions in the RBT here

        JFrame frame = new JFrame("Red-Black Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));

        RBTVisualiser canvas = new RBTVisualiser(rbt);
        JScrollPane scrollPane = new JScrollPane(canvas);
        frame.add(scrollPane);

        frame.pack();
        frame.setVisible(true);

        new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                canvas.repaint();
            }
        }, 0L, 200L / 1);
    }
}
