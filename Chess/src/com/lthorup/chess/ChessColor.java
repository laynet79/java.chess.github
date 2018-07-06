package com.lthorup.chess;

public enum ChessColor {
	White, Black;
	public ChessColor opposite() { return this == White ? Black : White; }
}
