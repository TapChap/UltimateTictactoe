package com.example.ultimatetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ultimatetictactoe.Tictactoe.Board;
import com.example.ultimatetictactoe.Tictactoe.Piece;
import com.example.ultimatetictactoe.Tictactoe.Pose2d;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "GameActivity";

    private Board[][] boards = new Board[3][3];
    private Board mainBoard = new Board();
    private Board selectedBoard;

    private boolean canChoose, removeIndicator = true;

    private final Button[][] buttons = new Button[3][3];
    private final ImageView[][] mainImages = new ImageView[3][3];
    private ImageView turnDisplay;

    private Pose2d indicatorPose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        for (int i = 0; i < Math.pow(boards.length, 2); i++) {
            boards[i / 3][i % 3] = new Board(getBoard(i), new Pose2d(i / 3, i % 3));
        }

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                String strA = "b" + i + j;
                String strB = "boardImage" + i + j;
                int resIdA = getResources().getIdentifier(strA, "id", getPackageName());
                int resIdB = getResources().getIdentifier(strB, "id", getPackageName());
                buttons[i][j] = findViewById(resIdA);
                buttons[i][j].setOnClickListener(this);

                mainImages[i][j] = findViewById(resIdB);
                mainBoard.setImage(new Pose2d(i, j), mainImages[i][j]);
                mainImages[i][j].setOnClickListener(this);
            }
        }

        turnDisplay = findViewById(R.id.turnDisplay);
        turnDisplay.setImageResource(Piece.X.getImg());

        setControlPanelEnabled(false);
        canChoose = true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {

                // control panel button pressed
                if (buttons[i][j].getId() == id) {
                    Pose2d pose = new Pose2d(i, j);

                    setControlPanelEnabled(true);
                    selectedBoard.set(pose, mainBoard.getTurn());
                    selectedBoard.update();

                    // disable clicks & update board image if won board
                    if (selectedBoard.hasWon(mainBoard.getTurn())) {
                        int row = selectedBoard.row(), col = selectedBoard.col();
                        removeIndicator = false;
                        mainImages[row][col].setImageResource(boards[row][col].getWinner().getImg());
                        mainImages[row][col].setClickable(false);
                    }

                    // checking if the next board can be played
                    if (boards[i][j].hasWon(Piece.EMPTY) || boards[i][j].isTie()){
                        // allow the next player to choose the next board, if the previous board was won
                        setControlPanelEnabled(false);
                        if (removeIndicator) mainImages[indicatorPose.i][indicatorPose.j].setImageResource(R.drawable.empty);
                        canChoose = true;
                    } else {
                        disableButtons(getTakenCells(boards[i][j]));
                        selectedBoard = boards[i][j];
                        updateIndicator(pose);
                        canChoose = false;
                    }

                    mainBoard.next();
                    turnDisplay.setImageResource(mainBoard.getTurn().getImg());
                }

                // main image pressed
                if (mainImages[i][j].getId() == id) {
                    Pose2d pose = new Pose2d(i, j);

                    if (canChoose && !boards[i][j].hasWon(Piece.EMPTY)) {
                        selectedBoard = boards[i][j];
                        updateIndicator(pose);
                        setControlPanelEnabled(true);
                        disableButtons(getTakenCells(boards[i][j]));
                    }
                }
            }
        }

        Log.d(TAG, "canChoose:" + canChoose);
    }

    private void setControlPanelEnabled(boolean enabled) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setEnabled(enabled);
                buttons[i][j].setAlpha(enabled? 1f: 0.25f);
            }
        }
    }

    private void disableButtons(ArrayList<Button> buttons){
        for (Button button : buttons) {
            button.setEnabled(false);
            button.setAlpha(0.25f);
        }
    }

    private ArrayList<Button> getTakenCells(Board board){
        ArrayList<Button> takenButtons = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.get(new Pose2d(i, j)) != Piece.EMPTY) takenButtons.add(buttons[i][j]);
            }
        }

        return takenButtons;
    }

    private void updateIndicator(Pose2d pose){
        // delete the last one
        if (indicatorPose != null && removeIndicator) {
            mainImages[indicatorPose.i][indicatorPose.j].setImageResource(R.drawable.empty);
        } else removeIndicator = true;

        // update the pose to the new one & display the indicator
        indicatorPose = pose;
        mainImages[pose.i][pose.j].setImageResource(R.drawable.indicator);
    }

    public ImageView[][] getBoard(int boardIndex) {
        ImageView[][] board = new ImageView[3][3];

        for (int i = 0; i < 9; i++) {
            String str = "piece" + ((boardIndex * 9) + i);
            int resId = getResources().getIdentifier(str, "id", getPackageName());
            board[i / 3][i % 3] = findViewById(resId);
        }

        return board;
    }
}