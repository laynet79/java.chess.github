package com.lthorup.chess;

public enum ChessPiece {
	
	BlackPawn(ChessColor.Black, -1),
	BlackRook(ChessColor.Black, -5),
	BlackKnight(ChessColor.Black, -5),
	BlackBishop(ChessColor.Black, -5),
	BlackQueen(ChessColor.Black, -10),
	BlackKing(ChessColor.Black, 0),
	WhitePawn(ChessColor.White, 1),
	WhiteRook(ChessColor.White, 5),
	WhiteKnight(ChessColor.White, 5),
	WhiteBishop(ChessColor.White, 5),
	WhiteQueen(ChessColor.White, 10),
	WhiteKing(ChessColor.White, 0),
	Empty(ChessColor.Black, 0);
	
	ChessPiece(ChessColor color, int value) { this.color = color; this.value = value; }
	private int value;
	private ChessColor color;
	public int value() { return value; }
	public ChessColor color() { return color; }
	public boolean empty() { return this == Empty; }
}
