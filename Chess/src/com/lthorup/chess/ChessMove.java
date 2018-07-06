package com.lthorup.chess;

public class ChessMove {
	public ChessLocation from, to;
	public ChessMove(ChessLocation from, ChessLocation to) { this.from = from; this.to = to; }
	
	@Override
	public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            ChessMove m = (ChessMove)obj;
            return to.x == m.to.x && to.y == m.to.y && from.x == m.from.x && from.y == m.from.y;
        }
        return false;
    }
}
