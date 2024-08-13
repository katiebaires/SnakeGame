package cmsc256;

import bridges.base.Symbol;
import bridges.games.NonBlockingGame;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;

import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Objects;

public class RamSnakeGame extends NonBlockingGame{
    static int[] boardSize = new int[]{20, 20};                 // controls board size (rows, cols)
    private static NamedColor[] colorList;  // colors for snake
    private static final int ASSIGNMENT_NUMBER = 2;

    private NamedColor backgroundColor;     // background color
    private ArrayList<int[]> snake;         //location of all snake parts
    private ArrayList<int[]> moves;         //moves that each part of the snake will take
    private int[] apple;                    //location of apple
    private String dir;                     //direction snake is moving
    private int frame;                      //num of frames
    private int points;                     //points you have
    private boolean gameOver;
    private int[] lastPos = {0, 0};
    private int[] temp;
    private Random randomGenerator = new Random();

    //constructor
    public RamSnakeGame(int assid, String login, String apiKey, int r, int c){
        // set up bridges
        super(assid, login, apiKey, r, c);
        // set title (add you name to the title) and provide a description with instructions
        setTitle("RamSnakeGame");
        setDescription("katiemartinez cmsc256-001");

        // initialize background color
        backgroundColor = NamedColor.beige;

        // start the game engine
        start();

    }

    public static void main(String args[]) {
        //enter your username and api key here
        RamSnakeGame  mg = new RamSnakeGame(ASSIGNMENT_NUMBER, "katiemartinez", "7707975508",
                                                    boardSize[0], boardSize[1]);
    }


    public void initialize() {
        //set frames and points variables to 0
        frame = 0;
        points = 0;
        //set gameOver to false
        gameOver = false;
        //and make the starting direction be "right"
        dir = "right";


        // using nested for loops for each tile,
        // set the background color and set the symbol to None
        for(int i = 0; i < getBoardHeight(); i++){
            for(int j = 0; j < getBoardWidth(); j++){
                setBGColor(i, j, NamedColor.beige);
               drawSymbol(i, j, NamedSymbol.none, NamedColor.beige);
            }
        }

        //reset snake and moves Lists with an empty list
        snake = new ArrayList<>();
        moves = new ArrayList<>();



        //add some parts to snake
        //make 2d array put snakes in
        snake.add(new int[]{5, 5});
        snake.add(new int[]{5, 4});
        snake.add(new int[]{5, 3});
        snake.add(new int[]{5, 2});

        //add moves for each snake part
        moves.add(0,new int[]{0, 1});
        moves.add(0,new int[]{0, 1});
        moves.add(0,new int[]{0, 1});
        moves.add(0,new int[]{0, 1});


        // set starting position for the apple
        apple = new int[] {13,10};


    }

    private void draw() {
        // Change the last location of the snake to the color of the board.
        // Use the lastPos array
        lastPos[0] = snake.get(snake.size() - 1)[0];
        lastPos[1] = snake.get(snake.size() - 1)[1];

        setBGColor(lastPos[0],lastPos[1], NamedColor.beige);



        // Use a loop to tranverse the Snake arraylist and
        // draw the snake on all the tiles
        colorList = new NamedColor[6];
        colorList[0] = NamedColor.red;
        colorList[1] = NamedColor.orange;
        colorList[2] = NamedColor.yellow;
        colorList[3] = NamedColor.green;
        colorList[4] = NamedColor.blue;
        colorList[5] = NamedColor.purple;

        for(int i = 0; i < snake.size() - 1; i++){
            setBGColor(snake.get(i)[0], snake.get(i)[1], colorList[i % colorList.length]);
        }

        //draw in the apple using an apple symbol
        drawSymbol(apple[0],apple[1], NamedSymbol.apple, NamedColor.red);

    }

    private void changeDirection(String newDirection) {
        // check what direction the snake is going and
        // set the String oppositeDirection to the opposite of that direction
        String oppositeDirection;

        if (newDirection.equals("up")) {
            oppositeDirection = "down";
        }
        else if (newDirection.equals("down")) {
            oppositeDirection = "up";
        }
        else if (newDirection.equals("left")) {
            oppositeDirection = "right";
        }
        else{
            oppositeDirection = "left";
        }

        // check to see if newDirection is equal to oppositeDirection.
        // Since the snake can't turn around and go back on itself,
        // we can only change the direction if is not turning completely around
        // If they are not equal, then the change in direction is allowed and
        // you can update its direction.
        if (!(Objects.equals(newDirection,oppositeDirection))) {
            dir = newDirection;
            if (dir.equals("up")) {
                addMove(-1,0);
            }
            else if (dir.equals("down")) {
                addMove(1,0);
            }
            else if (dir.equals("left")) {
                addMove(0,-1);
            }
           else {
                addMove(0,1);
            }

        }
        // based on the dir that was just set, add a move to the moves List


    }



    private void handleInput() {
        //test to see if each key is pressed, and call function change direction;
        if(keyLeft() || keyA())
           changeDirection("left");
        else if(keyRight() || keyD())
           changeDirection("right");
        else if(keyUp() || keyW())
           changeDirection("up");

        else if(keyDown() || keyS())
           changeDirection("down");
        else
            //call function but dont change direction
            changeDirection(dir);
    }

    private boolean moveSnake() {
        //returns true if snake moves into a spot which would mean game over

        //set last pos to pos of last part of snake
        lastPos[0] = snake.get(snake.size() - 1)[0];
        lastPos[1] = snake.get(snake.size() - 1)[1];

        //look at new pos of snake
        int newCol = snake.get(0)[0] + moves.get(0)[0];
        int newRow = snake.get(0)[1] + moves.get(0)[1];
        //see if it is a valid move
        if(newRow < 0 || newRow > boardSize[1] - 1)
            return true;

        if(newCol < 0 || newCol > boardSize[0] - 1)
            return true;

        for(int i = 0; i <=snake.size() - 1; i++) {
            if(snake.get(i)[0] == newCol && snake.get(i)[1] == newRow)
                return true;
        }
        //test to see if you are moving to space with apple in it
        if(newCol == apple[0] && newRow == apple[1]) {
            //increase points and move apple
            points++;
            grow();
            //remove apple symbol
            drawSymbol(apple[0], apple[1], NamedSymbol.none, NamedColor.red);
            //move apple to new spot
            apple[0] = randomGenerator.nextInt(boardSize[0]);
            apple[1] = randomGenerator.nextInt(boardSize[1]);
            //make sure new apple is not on a space with a snake on it
            while(inSnake(apple)) {
                apple[0] = randomGenerator.nextInt(boardSize[0]);
                apple[1] = randomGenerator.nextInt(boardSize[1]);
            }

        }

        //move each part of the snake to a new position
        for(int i = 0; i < snake.size(); i++) {
            snake.get(i)[0] += moves.get(i)[0];
            snake.get(i)[1] += moves.get(i)[1];
        }
        return false;
    }

    public void gameLoop() {
        //moves every 4 frames and if the game is not over
        if(frame % 4 == 0 && !gameOver) {
            //handles input to move snake
            handleInput();
            //moves snake returns true if game is over
            if(moveSnake()) {
                //end game and print out points
                gameOver = true;
                System.out.println("Game Over\n you got " + points + " points");
            }
            //update snake and apple on screen
            draw();
        }

        //if game is over and space key is pressed the restart the game
        if(gameOver && keySpace())
            initialize();

        //incrmement frames
        frame++;
    }

    //add move to moves
    private void addMove(int y, int x) {
        //creates new array of x and y move
        temp = new int[2];
        temp[0] = y;
        temp[1] = x;

        //adds it to moves
        moves.add(0, temp);

        //removes the last move that is replaced by new one
        moves.remove(moves.size() - 1);
    }

    private void grow() {
        //grow snake
        temp = new int[2];
        //calculate where the new snake part should go
        temp[0] = snake.get(snake.size() - 1)[0] - (snake.get(snake.size() - 2)[0] - snake.get(snake.size() - 1)[0]);
        temp[1] = snake.get(snake.size() - 1)[1] - (snake.get(snake.size() - 2)[1] - snake.get(snake.size() - 1)[1]);
        //add new part to snake
        snake.add(temp);

        //creates new move to correspond with new part of the snake
        temp = new int[2];
        temp[0] = moves.get(moves.size() - 1)[0];
        temp[1] = moves.get(moves.size() - 1)[1];
        moves.add(temp);
    }


    //checks to see if location is part of the snake
    private boolean inSnake(int[] x) {
        //looks at all snake parts and checks to see of a location is ewuall to that parts loc.
        for(int[] i : snake){
            if(i[0] == x[0] && i[1] == x[0])
                return true;
        }
        return false;
    }
}
