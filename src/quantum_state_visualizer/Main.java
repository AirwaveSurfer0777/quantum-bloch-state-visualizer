package quantum_state_visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    private BlochSpherePanel spherePanel;
    private JPanel controlPanel;
    private JTextField thetaField, phiField, psiField;
    private JSlider rotationSlider;
    private Complex[] currentState;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

    public Main() {
        setTitle("Quantum State Bloch Sphere Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(45, 45, 45));
        
        // Set an icon for the JFrame
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/icon.png")));

        // Initialize components
        setupControlPanel();
        setupSpherePanel();
        
        // Default state |0⟩
        currentState = new Complex[]{new Complex(1, 0), new Complex(0, 0)};
        updateVisualization();
    }

    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setBackground(new Color(60, 63, 65));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Use GridBagLayout for more precise control
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        
        // Style for labels and fields
        Font labelFont = new Font("SansSerif", Font.PLAIN, 12);
        Color labelColor = new Color(187, 187, 187);
        Dimension fieldSize = new Dimension(60, 25);
        
        // Create components with styling
        thetaField = createStyledTextField(fieldSize);
        phiField = createStyledTextField(fieldSize);
        psiField = createStyledTextField(fieldSize);
        
        // Setup slider
        rotationSlider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
        rotationSlider.setBackground(new Color(60, 63, 65));
        rotationSlider.setForeground(labelColor);
        rotationSlider.setMajorTickSpacing(90);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setPaintLabels(true);
        
        // Create styled buttons
        JButton[] buttons = {
            createStyledButton("|0⟩"),
            createStyledButton("|1⟩"),
            createStyledButton("|+⟩"),
            createStyledButton("|-⟩")
        };
        
        // Add components with GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(createStyledLabel("θ:", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        controlPanel.add(thetaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(createStyledLabel("φ:", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        controlPanel.add(phiField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(createStyledLabel("ψ:", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        controlPanel.add(psiField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        controlPanel.add(rotationSlider, gbc);
        
        // Add buttons in a 2x2 grid
        gbc.gridy = 4; gbc.gridwidth = 1;
        for (int i = 0; i < buttons.length; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = 4 + (i / 2);
            controlPanel.add(buttons[i], gbc);
        }
        
        // Add action listeners
        ActionListener updateListener = e -> updateVisualization();
        thetaField.addActionListener(updateListener);
        phiField.addActionListener(updateListener);
        psiField.addActionListener(updateListener);
        rotationSlider.addChangeListener(e -> spherePanel.setRotationAngle(rotationSlider.getValue()));
        
        // Add preset button actions
        buttons[0].addActionListener(e -> setPresetState(PresetState.ZERO));
        buttons[1].addActionListener(e -> setPresetState(PresetState.ONE));
        buttons[2].addActionListener(e -> setPresetState(PresetState.PLUS));
        buttons[3].addActionListener(e -> setPresetState(PresetState.MINUS));
        
        // Add panel to frame
        add(controlPanel, BorderLayout.EAST);
    }

    private void setupSpherePanel() {
        spherePanel = new BlochSpherePanel();
        spherePanel.setBackground(new Color(45, 45, 45));
        add(spherePanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(Dimension size) {
        JTextField field = new JTextField("0");
        field.setPreferredSize(size);
        field.setBackground(new Color(69, 73, 74));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        return field;
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(75, 110, 175));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    private void updateVisualization() {
        try {
            double theta = Double.parseDouble(thetaField.getText()) % (2 * Math.PI);
            double phi = Double.parseDouble(phiField.getText()) % (2 * Math.PI);
            double psi = Double.parseDouble(psiField.getText()) % (2 * Math.PI);
            
            // Calculate quantum state
            currentState = calculateQuantumState(theta, phi, psi);
            
            // Update visualization
            spherePanel.setState(currentState);
            spherePanel.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for angles.");
        }
    }

    private Complex[] calculateQuantumState(double theta, double phi, double psi) {
        Complex alpha = new Complex(
            Math.cos(theta / 2),
            0
        );
        Complex beta = new Complex(
            Math.sin(theta / 2) * Math.cos(phi),
            Math.sin(theta / 2) * Math.sin(phi)
        );
        
        // Apply global phase
        Complex phase = new Complex(Math.cos(psi), Math.sin(psi));
        return new Complex[]{
            alpha.multiply(phase),
            beta.multiply(phase)
        };
    }

    private void setPresetState(PresetState state) {
        switch (state) {
            case ZERO:
                thetaField.setText("0");
                phiField.setText("0");
                psiField.setText("0");
                break;
            case ONE:
                thetaField.setText(String.valueOf(Math.PI));
                phiField.setText("0");
                psiField.setText("0");
                break;
            case PLUS:
                thetaField.setText(String.valueOf(Math.PI / 2));
                phiField.setText("0");
                psiField.setText("0");
                break;
            case MINUS:
                thetaField.setText(String.valueOf(Math.PI / 2));
                phiField.setText(String.valueOf(Math.PI));
                psiField.setText("0");
                break;
        }
        updateVisualization();
    }
}

class BlochSpherePanel extends JPanel {
    private static final long serialVersionUID = -2264332308613346585L;
    private static final int SPHERE_RADIUS = 200;
    private Complex[] state;
    private double rotationAngle = 0;
    private List<Point3D> spherePoints;

    public BlochSpherePanel() {
        setPreferredSize(new Dimension(2 * SPHERE_RADIUS, 2 * SPHERE_RADIUS));
        generateSpherePoints();
    }

    private void generateSpherePoints() {
        spherePoints = new ArrayList<>();
        double step = Math.PI / 20;

        for (double theta = 0; theta <= Math.PI; theta += step) {
            for (double phi = 0; phi < 2 * Math.PI; phi += step) {
                double x = SPHERE_RADIUS * Math.sin(theta) * Math.cos(phi);
                double y = SPHERE_RADIUS * Math.sin(theta) * Math.sin(phi);
                double z = SPHERE_RADIUS * Math.cos(theta);
                spherePoints.add(new Point3D(x, y, z));
            }
        }
    }

    public void setState(Complex[] newState) {
        this.state = newState;
    }

    public void setRotationAngle(double angle) {
        this.rotationAngle = Math.toRadians(angle);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set origin to center
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        g2d.translate(centerX, centerY);

        // Draw sphere grid
        drawSphereGrid(g2d);

        // Draw axes
        drawAxes(g2d);

        // Draw state vector if available
        if (state != null) {
            drawStateVector(g2d);
        }
    }

    private void drawSphereGrid(Graphics2D g2d) {
    	g2d.setColor(Color.WHITE);  // Set the color of the points to white
        for (Point3D point : spherePoints) {
            Point3D rotated = rotatePoint(point);
            g2d.fillOval((int)rotated.x - 1, (int)rotated.y - 1, 2, 2); // Draw points
        }
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));

        // X-axis (red)
        g2d.setColor(new Color(255, 100, 100));
        drawRotatedLine(g2d, -SPHERE_RADIUS, 0, 0, SPHERE_RADIUS, 0, 0);

        // Y-axis (green)
        g2d.setColor(new Color(100, 255, 100));
        drawRotatedLine(g2d, 0, -SPHERE_RADIUS, 0, 0, SPHERE_RADIUS, 0);

        // Z-axis (blue)
        g2d.setColor(new Color(100, 100, 255));
        drawRotatedLine(g2d, 0, 0, -SPHERE_RADIUS, 0, 0, SPHERE_RADIUS);
    }

    private void drawStateVector(Graphics2D g2d) {
        if (state != null) {
            // Convert quantum state to Bloch sphere coordinates
            double theta = 2 * Math.acos(state[0].abs());
            double phi = Math.atan2(state[1].getImaginary(), state[1].getReal());

            // Calculate 3D coordinates
            double x = SPHERE_RADIUS * Math.sin(theta) * Math.cos(phi);
            double y = SPHERE_RADIUS * Math.sin(theta) * Math.sin(phi);
            double z = SPHERE_RADIUS * Math.cos(theta);

            // Draw state vector with brighter magenta
            Point3D statePoint = rotatePoint(new Point3D(x, y, z));
            g2d.setColor(new Color(255, 100, 255));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(0, 0, (int)statePoint.x, (int)statePoint.y);
        }
    }

    private void drawRotatedLine(Graphics2D g2d, double x1, double y1, double z1, 
                               double x2, double y2, double z2) {
        Point3D p1 = rotatePoint(new Point3D(x1, y1, z1));
        Point3D p2 = rotatePoint(new Point3D(x2, y2, z2));
        g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
    }

    private Point3D rotatePoint(Point3D p) {
        // Rotate around Y-axis
        double newX = p.x * Math.cos(rotationAngle) + p.z * Math.sin(rotationAngle);
        double newZ = -p.x * Math.sin(rotationAngle) + p.z * Math.cos(rotationAngle);
        return new Point3D(newX, p.y, newZ);
    }
}

class Point3D {
    double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Complex {
    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public double abs() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public Complex multiply(Complex other) {
        return new Complex(
            this.real * other.real - this.imaginary * other.imaginary,
            this.real * other.imaginary + this.imaginary * other.real
        );
    }
}

enum PresetState {
    ZERO, ONE, PLUS, MINUS
}