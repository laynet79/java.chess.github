package com.lthorup.chess;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ChessFrame extends JFrame {

	private JPanel contentPane;
	private ChessView chessView;
	private JTextField turn;
	private JTextField status;
	private JButton btnStartStop;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChessFrame frame = new ChessFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChessFrame() {
		setResizable(false);
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 751, 665);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chessView = new ChessView();
		boolean running = false;
		chessView.setBounds(22, 20, 600, 600);
		contentPane.add(chessView);
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chessView.stop();
				btnStartStop.setText("Start");
				chessView.newGame();
			}
		});
		btnNewGame.setBounds(628, 20, 117, 29);
		contentPane.add(btnNewGame);
		
		turn = new JTextField();
		turn.setEditable(false);
		turn.setHorizontalAlignment(SwingConstants.CENTER);
		turn.setText("WHITE");
		turn.setBounds(643, 165, 88, 26);
		contentPane.add(turn);
		turn.setColumns(10);
				
		status = new JTextField();
		status.setEditable(false);
		status.setBounds(643, 203, 88, 26);
		contentPane.add(status);
		status.setColumns(10);
		
		chessView.setTurnTextField(turn);
		chessView.setStatusTextField(status);
		
		JCheckBox cbWhiteAi = new JCheckBox("White AI");
		cbWhiteAi.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				chessView.whiteAi(cbWhiteAi.isSelected());
			}
		});
		cbWhiteAi.setBackground(Color.BLACK);
		cbWhiteAi.setForeground(Color.WHITE);
		cbWhiteAi.setBounds(634, 91, 97, 23);
		contentPane.add(cbWhiteAi);
		
		JCheckBox cbBlackAi = new JCheckBox("Black AI");
		cbBlackAi.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				chessView.blackAi(cbBlackAi.isSelected());
			}
		});
		cbBlackAi.setSelected(true);
		cbBlackAi.setForeground(Color.WHITE);
		cbBlackAi.setBackground(Color.BLACK);
		cbBlackAi.setBounds(634, 124, 97, 23);
		contentPane.add(cbBlackAi);
		
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnStartStop.getText().equals("Stop")) {
					chessView.stop();
					btnStartStop.setText("Start");
				}
				else {
					chessView.start();
					btnStartStop.setText("Stop");	
				}
			}
		});
		btnStartStop.setBounds(628, 50, 117, 29);
		contentPane.add(btnStartStop);
	}
}
