package com.lthorup.chess;

public class ChessLocation {
	public int x, y;
	public ChessLocation(int x, int y) { this.x = x; this.y = y; }
	
	@Override
	public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            ChessLocation m = (ChessLocation)obj;
            return x == m.x && y == m.y;
        }
        return false;
    }
}
