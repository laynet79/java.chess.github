package com.lthorup.chess;

import java.util.ArrayList;

//-------------------------------------------------------
// This class represents a chess game board and is also
// used as a node in the chess move search tree.
public class ChessBoard {

	//---------------------------------------------------
	// private data members
	private ChessColor color; // color whose move created this board
	private ChessPiece[][] board = new ChessPiece[8][8];
	private ChessBoard[] children;
	private ChessLocation whiteKing, blackKing;
	private ArrayList<ChessMove> validMoves;
	private int value;
	
	//---------------------------------------------------
	// valid move directions/offsets for piece types
    private static Dir[] rookDirections;
    private static Dir[] knightPositions;
    private static Dir[] bishopDirections;
    private static Dir[] queenDirections;
    private static Dir[] kingPositions;
	
    static {
        rookDirections = new Dir[] { new Dir(0, -1), new Dir(0, 1), new Dir(1, 0), new Dir(-1, 0) };
        knightPositions = new Dir[] { new Dir(-2, -1), new Dir(-2, 1), new Dir(-1, -2), new Dir(-1, 2), new Dir(2, -1), new Dir(2, 1), new Dir(1, -2), new Dir(1, 2) };
        bishopDirections = new Dir[] { new Dir(-1, -1), new Dir(1, -1), new Dir(1, 1), new Dir(-1, 1) };
        queenDirections = new Dir[] { new Dir(-1, -1), new Dir(1, -1), new Dir(1, 1), new Dir(-1, 1), new Dir(0, -1), new Dir(0, 1), new Dir(1, 0), new Dir(-1, 0) };
        kingPositions = new Dir[] { new Dir(-1, -1), new Dir(1, -1), new Dir(1, 1), new Dir(-1, 1), new Dir(0, -1), new Dir(0, 1), new Dir(1, 0), new Dir(-1, 0) };    	
    }
    
	//---------------------------------------------------
	// create starting chess board
	public ChessBoard(ChessColor color) {
		this.color = color; // color whose move created this board
		newGame();
		validMoves = validMoves(color.opposite());
		evaluate();
	}
	//---------------------------------------------------
	// create next move board based on parent board and move
	public ChessBoard(ChessBoard parent, ChessMove move) {
		color = parent.color.opposite();
		board = new ChessPiece[8][8];
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++)
				board[x][y] = parent.board[x][y];
		whiteKing = parent.whiteKing;
		blackKing = parent.blackKing;
		makeMove(move);
		validMoves = validMoves(color.opposite());
		evaluate();
	}
	//---------------------------------------------------
	// set/get chess pieces
	public ChessPiece get(int x, int y) { return board[x][y]; }
	public ChessPiece get(ChessLocation loc) { return board[loc.x][loc.y]; }
	public void set(int x, int y, ChessPiece piece) { board[x][y] = piece; }
	public void set(ChessLocation loc, ChessPiece piece) { board[loc.x][loc.y] = piece; }
	
	//---------------------------------------------------
	// test for check mate
	public boolean isCheckMate() {
		return validMoves.size() == 0;
	}
	
	//---------------------------------------------------
	// test for check
	public boolean isCheck() {
		return validMoves.size() > 0 && inCheck(color.opposite());
	}
	
	//---------------------------------------------------
	// test stale mate
	public boolean isStaleMate() {
		return validMoves.size() == 0 && !inCheck(color.opposite());
	}
	
	//---------------------------------------------------
    static int CHECKMATE_VALUE = 100;
    void evaluate()
    {
        value = 0;
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
            		value += board[x][y].value();

        // If there are no valid moves, then we have check mate.
        if (validMoves.size() == 0)
        		value += (color == ChessColor.White) ? CHECKMATE_VALUE : -CHECKMATE_VALUE;
    }
	//---------------------------------------------------
    ChessBoard getChild(int i)
    {
        // allocate child array if needed
        if (children == null)
            children = new ChessBoard[validMoves.size()];

        // if the child hasn't been created, we create it and return it
        if (children[i] == null)
            children[i] = new ChessBoard(this, validMoves.get(i));
        return children[i];
    }
	//---------------------------------------------------
	// test a move for validity
	boolean validMove(ChessMove move) {
		ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
		validMovesForPiece(move.from, moves);
		for (ChessMove m : moves)
			if (move.equals(m))
				return true;
		return false;
	}
	//---------------------------------------------------
	// make move and return displaced piece
	public ChessPiece makeMove(ChessMove move) {
		ChessPiece piece = get(move.to);
		set(move.to, get(move.from));
		set(move.from, ChessPiece.Empty);
		ChessPiece p = get(move.to);
		if (p.equals(ChessPiece.WhiteKing))
			whiteKing = move.to;
		else if (p.equals(ChessPiece.BlackKing))
			blackKing = move.to;
		return piece;
	}
	//---------------------------------------------------
	// undo a move using the saved piece from the original move
	public void undoMove(ChessMove move, ChessPiece savedPiece) {
		set(move.from, get(move.to));
		set(move.to, savedPiece);
		ChessPiece p = get(move.from);
		if (p.equals(ChessPiece.WhiteKing))
			whiteKing = move.from;
		else if (p.equals(ChessPiece.BlackKing))
			blackKing = move.from;		
	}
	//---------------------------------------------------
	public ChessBoard attemptMove(ChessMove move) {
		for (int i = 0; i < validMoves.size(); i++) {
			if (move.equals(validMoves.get(i)))
				return getChild(i);
		}
		return this;
	}
	//---------------------------------------------------
	// initialize the board to the new game configuration
	private void newGame() {
		board[0][0] = ChessPiece.BlackRook;
		board[1][0] = ChessPiece.BlackKnight;
		board[2][0] = ChessPiece.BlackBishop;
		board[3][0] = ChessPiece.BlackQueen;
		board[4][0] = ChessPiece.BlackKing;
		board[5][0] = ChessPiece.BlackBishop;
		board[6][0] = ChessPiece.BlackKnight;
		board[7][0] = ChessPiece.BlackRook;
		
		board[0][1] = ChessPiece.BlackPawn;
		board[1][1] = ChessPiece.BlackPawn;
		board[2][1] = ChessPiece.BlackPawn;
		board[3][1] = ChessPiece.BlackPawn;
		board[4][1] = ChessPiece.BlackPawn;
		board[5][1] = ChessPiece.BlackPawn;
		board[6][1] = ChessPiece.BlackPawn;
		board[7][1] = ChessPiece.BlackPawn;
		
		board[0][7] = ChessPiece.WhiteRook;
		board[1][7] = ChessPiece.WhiteKnight;
		board[2][7] = ChessPiece.WhiteBishop;
		board[3][7] = ChessPiece.WhiteQueen;
		board[4][7] = ChessPiece.WhiteKing;
		board[5][7] = ChessPiece.WhiteBishop;
		board[6][7] = ChessPiece.WhiteKnight;
		board[7][7] = ChessPiece.WhiteRook;
		
		board[0][6] = ChessPiece.WhitePawn;
		board[1][6] = ChessPiece.WhitePawn;
		board[2][6] = ChessPiece.WhitePawn;
		board[3][6] = ChessPiece.WhitePawn;
		board[4][6] = ChessPiece.WhitePawn;
		board[5][6] = ChessPiece.WhitePawn;
		board[6][6] = ChessPiece.WhitePawn;
		board[7][6] = ChessPiece.WhitePawn;
		
		for (int y = 2; y <= 5; y++)
			for (int x = 0; x < 8; x++)
				board[x][y] = ChessPiece.Empty;		
		
		whiteKing = new ChessLocation(4,7);
		blackKing = new ChessLocation(4,0);
	}
	//---------------------------------------------------
	// Generate a list of valid moves for the given color on this board
	ArrayList<ChessMove> validMoves(ChessColor playerColor) {
		ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++)
				if (! board[x][y].empty() && board[x][y].color() == playerColor)
					validMovesForPiece(new ChessLocation(x,y), moves);
		return moves;
	}
	
	//---------------------------------------------------
	// test to see if the move puts the player of the given color in check
	boolean inCheck(ChessColor playerColor, ChessMove move) {
        ChessPiece savedPiece = makeMove(move);
		boolean inCheck = inCheck(playerColor);
        undoMove(move, savedPiece);
        return inCheck;
	}
    boolean inCheck(ChessColor playerColor) {
        // make the move, remembering displaced piece
        boolean inCheck = false;
        //ChessPiece savedPiece = makeMove(move);

        // get the king location of the current color
        ChessLocation kingLoc = (playerColor == ChessColor.White) ? whiteKing : blackKing;

        // see if king can be killed by knight
        ChessPiece oppKnight = (playerColor == ChessColor.White) ? ChessPiece.BlackKnight : ChessPiece.WhiteKnight;
        for (Dir d : knightPositions)
        {
            int x = kingLoc.x + d.dx;
            int y = kingLoc.y + d.dy;
            if (x >= 0 && x < 8 && y >= 0 && y < 8 && board[x][y] == oppKnight)
            {
                inCheck = true;
                break;
            }
        }

        // see if king can be killed by pawn
        if (! inCheck)
        {
            ChessPiece oppPawn = (playerColor == ChessColor.White) ? ChessPiece.BlackPawn : ChessPiece.WhitePawn;
            int oppKillDir = (playerColor == ChessColor.White) ? 1 : -1;
            int y = kingLoc.y - oppKillDir;
            if (y >= 0 && y < 8)
            {
                int x = kingLoc.x - 1;
                if (x >= 0 && board[x][y] == oppPawn)
                    inCheck = true;
                else
                {
                    x += 2;
                    if (x < 8 && board[x][y] == oppPawn)
                        inCheck = true;
                }
            }
        }
        
        // see if king can be killed by opponent king
        if (! inCheck)
        {
            ChessPiece oppKing = (color == ChessColor.White) ? ChessPiece.BlackKing : ChessPiece.WhiteKing;
            for (Dir d : kingPositions)
            {
                int x = kingLoc.x + d.dx;
                int y = kingLoc.y + d.dy;
                if (x >= 0 && x < 8 && y >= 0 && y < 8 && board[x][y] == oppKing)
                {
                    inCheck = true;
                    break;
                }
            }
        }

        // see if king can be killed by continuous move piece
        if (! inCheck)
        {
            for (Dir d : queenDirections)
            {
                int x = kingLoc.x + d.dx;
                int y = kingLoc.y + d.dy;
                boolean pieceHit = false;
                while (!pieceHit && x >= 0 && x < 8 && y >= 0 && y < 8)
                {
                    ChessPiece piece = board[x][y];
                    if (piece != ChessPiece.Empty)
                    {
                        pieceHit = true;
                        if (piece.color() != playerColor) // piece is opponent
                        {
                            switch (piece)
                            {
                                case WhiteRook:
                                case BlackRook:
                                    inCheck = (d.dx == 0 || d.dy == 0);
                                    break;
                                case WhiteBishop:
                                case BlackBishop:
                                    inCheck = (d.dx != 0 && d.dy != 0);
                                    break;
                                case WhiteQueen:
                                case BlackQueen:
                                    inCheck = true;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    // move to next tile along path
                    x += d.dx;
                    y += d.dy;
                }
                if (inCheck)
                    break;
            }
        }

        //undoMove(move, savedPiece);
        return inCheck;
    }

	//---------------------------------------------------
	// Generate a list of valid moves for the given piece
	void validMovesForPiece(ChessLocation loc, ArrayList<ChessMove> moves) {
		
		switch (board[loc.x][loc.y]) {
		case BlackPawn:
		case WhitePawn:
			validPawnMoves(loc, moves);
			break;
		case BlackRook:
		case WhiteRook:
			validRookMoves(loc, moves);
			break;
		case BlackKnight:
		case WhiteKnight:
			validKnightMoves(loc, moves);
			break;
		case BlackBishop:
		case WhiteBishop:
			validBishopMoves(loc, moves);
			break;
		case BlackQueen:
		case WhiteQueen:
			validQueenMoves(loc, moves);
			break;
		case BlackKing:
		case WhiteKing:
			validKingMoves(loc, moves);
			break;
		case Empty:
			break;
		}
	}
	
	//---------------------------------------------------
	// Generate a list of valid moves for a pawn
	void validPawnMoves(ChessLocation loc, ArrayList<ChessMove> moves) {
		ChessColor playerColor = board[loc.x][loc.y].color();
        int dy = (playerColor == ChessColor.White) ? -1 : 1;      // y direction pawn can move based on color
        int startRow = (playerColor == ChessColor.White) ? 6 : 1; // pawn start row based on color
        int endRow = (playerColor == ChessColor.White) ? 0 : 7;   // pawn end row base on color
        if (loc.y != endRow) {
            if (board[loc.x][loc.y+dy] == ChessPiece.Empty) // check space in front of pawn
            {
            		ChessMove move = new ChessMove(loc, new ChessLocation(loc.x, loc.y+dy));
            		if (!inCheck(playerColor, move))
            			moves.add(move);
                if (loc.y == startRow && board[loc.x][loc.y+(2*dy)] == ChessPiece.Empty) {
                		move = new ChessMove(loc, new ChessLocation(loc.x, loc.y+(2*dy)));
                		if (!inCheck(playerColor, move))
                			moves.add(move);
                }
            }
            if (loc.x >= 1) { // kill to the left
                ChessPiece piece = board[loc.x - 1][loc.y + dy];
                if (piece != ChessPiece.Empty && piece.color() != playerColor) {
                		ChessMove move = new ChessMove(loc, new ChessLocation(loc.x - 1, loc.y + dy));
                		if (!inCheck(playerColor, move))
                			moves.add(move);
                }
            }
            if (loc.x < 7) { // kill to the right
                ChessPiece piece = board[loc.x + 1][loc.y + dy];
                if (piece != ChessPiece.Empty && piece.color() != playerColor) {
                		ChessMove move = new ChessMove(loc, new ChessLocation(loc.x+ 1, loc.y + dy));
                		if (!inCheck(playerColor, move))
                			moves.add(move);
                }
            }
        }
	}
	//---------------------------------------------------
	// Generate a list of valid moves for a pawn
	void validRookMoves(ChessLocation loc, ArrayList<ChessMove> moves) {
        validMovesForContinuousPiece(loc, moves, rookDirections);
	}
	//---------------------------------------------------
	// Generate a list of valid moves for a pawn
	void validKnightMoves(ChessLocation loc, ArrayList<ChessMove> moves) {
		ChessColor playerColor = board[loc.x][loc.y].color();
        for (Dir d : knightPositions) {
            int x = loc.x + d.dx;
            int y = loc.y + d.dy;
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            		ChessPiece piece = board[x][y];
                if (piece == ChessPiece.Empty || piece.color() != playerColor) {
                		ChessMove move = new ChessMove(loc, new ChessLocation(x, y));
                		if (!inCheck(playerColor, move))
                			moves.add(move);
                }
            }
        }
	}
	//---------------------------------------------------
	// Generate a list of valid moves for a pawn
	void validBishopMoves(ChessLocation loc, ArrayList<ChessMove> moves) {
        validMovesForContinuousPiece(loc, moves, bishopDirections);
	}
	//---------------------------------------------------
	// Generate a list of valid moves for a pawn
	void validQueenMoves(ChessLocation loc, ArrayList<ChessMove> moves) {
        validMovesForContinuousPiece(loc, moves, queenDirections);		
	}
	//---------------------------------------------------
	// Generate a list of valid moves for a pawn
	void validKingMoves(ChessLocation loc, ArrayList<ChessMove> moves) {
		ChessColor playerColor = board[loc.x][loc.y].color();
        for (Dir d : kingPositions) {
            int x = loc.x + d.dx;
            int y = loc.y + d.dy;
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            		ChessPiece piece = board[x][y];
                if (piece == ChessPiece.Empty || piece.color() != playerColor) {
                		ChessMove move = new ChessMove(loc, new ChessLocation(x, y));
                		if (!inCheck(playerColor, move))
                			moves.add(move);
                }
            }
        }
	}
	//---------------------------------------------------
	// Generate a list of valid moves for a continuous moving piece (rook, bishop, queen, king)
    void validMovesForContinuousPiece(ChessLocation loc, ArrayList<ChessMove> moves, Dir[] dir)
    {
		ChessColor playerColor = board[loc.x][loc.y].color();
        for (Dir d : dir) {
            int x = loc.x + d.dx;
            int y = loc.y + d.dy;
            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
            		ChessPiece piece = board[x][y];
                if (piece == ChessPiece.Empty || piece.color() != playerColor) {
                		ChessMove move = new ChessMove(loc, new ChessLocation(x, y));
                		if (!inCheck(playerColor, move))
                			moves.add(move);
                }
                if (piece != ChessPiece.Empty) // if we hit another piece we're done walking in this direction, go to next dir.
                    break;
                x += d.dx;
                y += d.dy;
            }
        }
    }
    
    //-------------------------------------------------------
    // Do an AI search for the best move for the given player color and select
    // that board as the new root
    final int MAX_DEPTH = 4;
    final int MAX = 1000000;
    final int MIN = -1000000;
    final int MAX_DEPTH_CHILDREN = 10;    // max number of children that will be searched to max depth
    
    public ChessBoard aiMakeMove()
    {
    		// return immediately if no valid moves (check mate or stale mate)
    		if (validMoves.size() == 0)
    			return this;
    	
        // Get the minimum value for each child, and find the largest of those values.
        // As we do this, we keep a list of all children/moves that have the same best/largest value.
    		ChessColor playerColor = color.opposite();
        ArrayList<ChessBoard> bestMoves = new ArrayList<ChessBoard>();
        int maxValue = MIN;
        int A = MIN;
        int B = MAX;
        int depth = 1;
        for (int i = 0; i < validMoves.size(); i++)
        {
            ChessBoard child = getChild(i);
            int val = child.minValue(playerColor, depth + 1, A, B);
            if (bestMoves.size() == 0 || val == maxValue)
            {
                bestMoves.add(child);
                maxValue = val;
            }
            else if (val > maxValue)
            {
                bestMoves.clear();
                bestMoves.add(child);
                maxValue = val;
            }
            A = Math.max(maxValue, A);
        }
        
        // If there are multiple best moves (equally good), return a random move from the best list.
        int index = (int)(Math.random() * bestMoves.size());
        return bestMoves.get(index);
    }

    //-------------------------------------------------------
    // Get maximum value of node.
    int maxValue(ChessColor playerColor, int depth, int A, int B)
    {
        // If max depth has been reached or there are no children (no possible moves), return node's heuristic value.
        if (depth >= MAX_DEPTH || validMoves.size() == 0) 
            return playerColor == ChessColor.White ? value : -value;

        // Get the minimum value for each child, and return the largest of those values.
        int maxValue = MIN;
        int depthChange = 1;
        int mod = Math.max(validMoves.size() / MAX_DEPTH_CHILDREN, 1);
        for (int i = 0; i < validMoves.size(); i++)
        {
            // Prune search tree by limiting how many children are searched at max depth.
            if (i % mod == 0)
                depthChange = 1;    // will continue to increment depth by one until it reaches max depth (4)
            else
                depthChange = 3;    // will increment by three, causing search to stop at earlier depth (3)

            maxValue = Math.max(getChild(i).minValue(playerColor, depth + depthChange, A, B), maxValue);
            
            //If best max so far is greater than best min so far, then don't look at remaining children.
            if (maxValue > B)
                return maxValue;
            
            A = Math.max(maxValue, A);
        }
        return maxValue;
    }

    //-------------------------------------------------------
    // Get minimum value of node.
    int minValue(ChessColor playerColor, int depth, int A, int B)
    {
        // If max depth has been reached or there are no children (no possible moves), return node's heuristic value.
        if (depth >= MAX_DEPTH || validMoves.size() == 0)
        		return playerColor == ChessColor.White ? value : -value;

        // Get the maximum value for each child, and return the smallest of those values.
        int minValue = MAX;
        int depthChange = 1;
        int mod = Math.max(validMoves.size() / MAX_DEPTH_CHILDREN, 1);
        for (int i = 0; i < validMoves.size(); i++)
        {
            // Prune search tree by limiting how many children are searched at max depth.
            if (i % mod == 0)
                depthChange = 1;
            else
                depthChange = 3;

            minValue = Math.min(getChild(i).maxValue(playerColor, depth + depthChange, A, B), minValue);

            // If best max so far is greater than best min so far, then don't look at remaining children.
            if (minValue < A)
                return minValue;

            B = Math.min(minValue, B);
        }
        return minValue;
    }
	//---------------------------------------------------
}