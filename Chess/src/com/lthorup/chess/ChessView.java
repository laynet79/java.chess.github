package com.lthorup.chess;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChessView extends JPanel implements Runnable {

	final int SIZE = 75;
	
	BufferedImage[] pieceImages = new BufferedImage[12];
	BufferedImage darkSquare;
	BufferedImage lightSquare;
	
	ChessBoard board;
	ChessColor playerTurn;
	boolean running = false;
	boolean exiting = false;
	boolean whiteIsAi=false, blackIsAi=true;
	JTextField turnTextField;
	JTextField statusTextField;
	
	int SX, SY;
	int EX, EY;
	int TileX, TileY;
	boolean movingPiece = false;
	
	public void setTurnTextField(JTextField t) {
		turnTextField = t;
	}
	public void setStatusTextField(JTextField t) {
		statusTextField = t;
	}

	public void newGame() {
		board = new ChessBoard(ChessColor.Black);
		playerTurn = ChessColor.White;
		if (statusTextField != null)
			statusTextField.setText("");
		repaint();
	}
	
	public void start() {
		running = true;
	}
	public void stop() {
		running = false;
	}
	public void whiteAi(boolean ai) {
		whiteIsAi = ai;
	}
	public void blackAi(boolean ai) {
		blackIsAi = ai;
	}

	public ChessView() {
		setBackground(Color.GRAY);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (movingPiece) {
					EX = e.getX();
					EY = e.getY();
					repaint();
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				boolean playerIsAi = playerTurn == ChessColor.White ? whiteIsAi : blackIsAi;
				if (! running || playerIsAi)
					return;
				SX = e.getX();
				SY = e.getY();
				TileX = SX / SIZE;
				TileY = SY / SIZE;
				if (TileX >= 0 && TileX < 8 && TileY >= 0 && TileY < 8 && board.get(TileX, TileY) != ChessPiece.Empty)
					movingPiece = true;
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (movingPiece) {
					int toTileX = e.getX() / SIZE;
					int toTileY = e.getY() / SIZE;
					if (toTileX >= 0 && toTileX < 8 && toTileY >= 0 && toTileY < 8) {
						if (board.get(TileX, TileY).color() == playerTurn) {
							ChessBoard newBoard = board.attemptMove(new ChessMove(new ChessLocation(TileX,TileY), new ChessLocation(toTileX, toTileY)));
							if (newBoard != board) {
								board = newBoard;
								playerTurn = playerTurn.opposite();
								updateStats();
							}
						}
					}
					movingPiece = false;
					repaint();
				}
			}
		});
		
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// load up game board images
	    try {
	        darkSquare = ImageIO.read(getClass().getClassLoader().getResource("Images/DarkSquare.png"));
	        lightSquare = ImageIO.read(getClass().getClassLoader().getResource("Images/LightSquare.png"));
	        pieceImages[ChessPiece.BlackPawn.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/BlackPawn.png"));
	        pieceImages[ChessPiece.BlackRook.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/BlackRook.png"));
	        pieceImages[ChessPiece.BlackKnight.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/BlackKnight.png"));
	        pieceImages[ChessPiece.BlackBishop.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/BlackBishop.png"));
	        pieceImages[ChessPiece.BlackQueen.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/BlackQueen.png"));
	        pieceImages[ChessPiece.BlackKing.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/BlackKing.png"));
	        pieceImages[ChessPiece.WhitePawn.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/WhitePawn.png"));
	        pieceImages[ChessPiece.WhiteRook.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/WhiteRook.png"));
	        pieceImages[ChessPiece.WhiteKnight.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/WhiteKnight.png"));
	        pieceImages[ChessPiece.WhiteBishop.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/WhiteBishop.png"));
	        pieceImages[ChessPiece.WhiteQueen.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/WhiteQueen.png"));
	        pieceImages[ChessPiece.WhiteKing.ordinal()] = ImageIO.read(getClass().getClassLoader().getResource("Images/WhiteKing.png"));
	        
	    } catch (IOException e) {}
	    
	    // create the initial board
	    newGame();
	    
	    // start game thread
	    Thread thread = new Thread(this);
	    thread.start();
	}
	
	//---------------------------------------------
	// game thread
	public void run() {
		while (! exiting) {
			if (running) {
				boolean playerIsAi = playerTurn == ChessColor.White ? whiteIsAi : blackIsAi;
				if (playerIsAi) {
					ChessBoard lastBoard = board;
					statusTextField.setText("thinking");
					board = board.aiMakeMove();
					if (board != lastBoard) {
						playerTurn = playerTurn.opposite();
						repaint();
					}
					updateStats();
				}
			}
			try {
				Thread.sleep(1000);
			}
			catch(Exception e) {}
		}
	}
	
	private void updateStats() {
		if (playerTurn == ChessColor.White)
			turnTextField.setText("WHITE");
		else
			turnTextField.setText("BLACK");
		if (board.isStaleMate())
			statusTextField.setText("StaleMate");
		else if (board.isCheckMate())
			statusTextField.setText("Checkmate");
		else if (board.isCheck())
			statusTextField.setText("Check");
		else
			statusTextField.setText("");		
	}
	
	@Override
	public void paint(Graphics g) {
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				BufferedImage s = ((x+y) & 1) == 0 ? lightSquare : darkSquare;
				int px = x * SIZE;
				int py = y * SIZE;
				g.drawImage(s, px, py, null);
				
				ChessPiece piece = board.get(x,y);
				if (piece != ChessPiece.Empty && (!movingPiece || TileX != x || TileY != y))
					g.drawImage(pieceImages[piece.ordinal()], px, py, null);
			}
		if (movingPiece) {
			ChessPiece piece = board.get(TileX, TileY);
			int px = TileX * SIZE + (EX - SX);
			int py = TileY * SIZE + (EY - SY);
			g.drawImage(pieceImages[piece.ordinal()], px, py, null);
		}
	}

}
