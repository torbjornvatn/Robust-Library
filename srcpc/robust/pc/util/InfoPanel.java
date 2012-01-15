package robust.pc.util;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * @author Konrad Kulakowski
 */
public abstract class InfoPanel {
	private JFrame frame = null;
	private Container mainPanel;
	private LayoutManager mainLayout;

	protected JLabel label11;
	protected JLabel label21;
	protected JLabel label31;
	protected JLabel label41;
	protected JLabel label51;
	protected JLabel label61;

	protected JLabel label12;
	protected JLabel label22;
	protected JLabel label32;
	protected JLabel label42;
	protected JLabel label52;
	protected JLabel label62;

	public abstract void doActionOnClick();

	public InfoPanel(String title, String label1, String label2, String label3,
			String label4, String label5, String label6) {
		/* Instantiation */
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		label11 = new JLabel(label1, JLabel.RIGHT);
		label21 = new JLabel(label2, JLabel.RIGHT);
		label31 = new JLabel(label3, JLabel.RIGHT);
		label41 = new JLabel(label4, JLabel.RIGHT);
		label51 = new JLabel(label5, JLabel.RIGHT);
		label61 = new JLabel(label6, JLabel.RIGHT);

		label12 = new JLabel("", JLabel.LEFT);
		label22 = new JLabel("", JLabel.LEFT);
		label32 = new JLabel("", JLabel.LEFT);
		label42 = new JLabel("", JLabel.LEFT);
		label52 = new JLabel("", JLabel.LEFT);
		label62 = new JLabel("", JLabel.LEFT);

		mainPanel = frame.getContentPane();

		JPanel row1 = new JPanel(new GridLayout(1, 2));
		JPanel row2 = new JPanel(new GridLayout(1, 2));
		JPanel row3 = new JPanel(new GridLayout(1, 2));
		JPanel row4 = new JPanel(new GridLayout(1, 2));
		JPanel row5 = new JPanel(new GridLayout(1, 2));
		JPanel row6 = new JPanel(new GridLayout(1, 2));

		row1.add(label11);
		row1.add(label12);
		row2.add(label21);
		row2.add(label22);
		row3.add(label31);
		row3.add(label32);
		row4.add(label41);
		row4.add(label42);
		row5.add(label51);
		row5.add(label52);
		row6.add(label61);
		row6.add(label62);

		JButton buton = new JButton("Stop Tracking");
		buton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doActionOnClick();
			}
		});

		JPanel row7 = new JPanel(new GridLayout(1, 1));
		row7.add(buton);

		mainLayout = new GridLayout(7, 1);
		mainPanel.setLayout(mainLayout);

		frame.getContentPane().add(row1);
		frame.getContentPane().add(row2);
		frame.getContentPane().add(row3);
		frame.getContentPane().add(row4);
		frame.getContentPane().add(row5);
		frame.getContentPane().add(row6);
		frame.getContentPane().add(row7);

		/* Decoration */
		mainPanel.setBackground(Color.lightGray);

		frame.pack();
		frame.setVisible(true);
	}

	public void updateData() {
		SwingUtilities.updateComponentTreeUI(label12);
		SwingUtilities.updateComponentTreeUI(label22);
		SwingUtilities.updateComponentTreeUI(label32);
		SwingUtilities.updateComponentTreeUI(label42);
		SwingUtilities.updateComponentTreeUI(label52);
		SwingUtilities.updateComponentTreeUI(label62);
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		InfoPanel me = new InfoPanel("title", "lab1", "lab2", "lab3", "lab4",
				"lab5", "lab6") {
			@Override
			public void doActionOnClick() {
				System.exit(0);
			}
		};

		for (int i = 0;; i++) {
			me.label12.setText("\t" + i + "");
			me.label22.setText("\t" + i % 100 * (-2) + "");
			SwingUtilities.updateComponentTreeUI(me.label12);
			SwingUtilities.updateComponentTreeUI(me.label22);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
}