package com.example.ultimatetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ultimatetictactoe.Tictactoe.Board;
import com.example.ultimatetictactoe.Tictactoe.Piece;
import com.example.ultimatetictactoe.Tictactoe.Pose2d;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private final String TAG = "GameActivity";

    private Board[][] boards = new Board[3][3];
    private Board mainBoard = new Board();
    private Board selectedBoard;

    private boolean canChoose;

    private final Button[][] buttons = new Button[3][3];
    private final ImageView[][] mainImages = new ImageView[3][3];
    private ImageView turnDisplay;
    private TextView winnerDisplay;

    private Pose2d indicatorPose = new Pose2d(0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        for (int i = 0; i < Math.pow(boards.length, 2); i++) {
            boards[i / 3][i % 3] = new Board(getBoard(i), new Pose2d(i / 3, i % 3));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String strA = "button" + i + j;
                String strB = "boardImage" + i + j;
                int resIdA = getResources().getIdentifier(strA, "id", getPackageName());
                int resIdB = getResources().getIdentifier(strB, "id", getPackageName());
                buttons[i][j] = findViewById(resIdA);
                buttons[i][j].setOnClickListener(this);

                mainImages[i][j] = findViewById(resIdB);
                mainImages[i][j].setOnTouchListener(this);
                mainImages[i][j].setTag("");

                mainBoard.setImage(new Pose2d(i, j), mainImages[i][j]);
            }
        }

        turnDisplay = findViewById(R.id.turnDisplay);
        turnDisplay.setImageResource(Piece.X.getImg());

        winnerDisplay = findViewById(R.id.winnerDisplay);

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
                    Pose2d nextPose = new Pose2d(i, j); // the pose of the next pose
                    Pose2d currentPose = selectedBoard.getPose(); // the pose of the selected board

                    setControlPanelEnabled(true);
                    selectedBoard.set(nextPose, mainBoard.getTurn());
                    selectedBoard.update();

                    // disable clicks & update board image if won board
                    if (selectedBoard.hasWon(mainBoard.getTurn())) {
                        mainBoard.set(currentPose);
                        mainBoard.update();
                        mainImages[currentPose.i][currentPose.j].setClickable(false);
                        mainImages[currentPose.i][currentPose.j].setTag("image");
                    } else if (selectedBoard.isTie()) {
                        selectedBoard.reset();
                        selectedBoard.update();
                    }

                    // check if the entire game was won
                    if (mainBoard.hasWon()) {
                        winnerDisplay.setVisibility(View.VISIBLE);
                        setControlPanelEnabled(false);
                        disableMainImages();
                        return;
                    }

                    // checking if the next board can be played
                    if (boards[i][j].hasWon(Piece.EMPTY) || boards[i][j].isTie()) {
                        // allow the next player to choose the next board, if the previous board was won
                        setControlPanelEnabled(false);
                        canChoose = true;
                    } else {
                        disableButtons(getTakenCells(boards[i][j]));
                        selectedBoard = boards[i][j];
                        canChoose = false;
                    }

                    updateIndicator(nextPose);
                    mainBoard.next();
                    turnDisplay.setImageResource(mainBoard.getTurn().getImg());
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();

        // main image pressed
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (mainImages[i][j].getId() == id) {
                        if (canChoose && !boards[i][j].hasWon(Piece.EMPTY)) {
                            selectedBoard = boards[i][j];
                            updateIndicator(new Pose2d(i, j));
                            setControlPanelEnabled(true);
                            disableButtons(getTakenCells(boards[i][j]));
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void setControlPanelEnabled(boolean enabled) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setEnabled(enabled);
                buttons[i][j].setAlpha(enabled ? 1f : 0.25f);
            }
        }
    }

    private void disableMainImages() {
        for (int i = 0; i < mainImages.length; i++) {
            for (int j = 0; j < mainImages[i].length; j++) {
                mainImages[i][j].setClickable(false);
            }
        }
    }

    private void disableButtons(ArrayList<Button> buttons) {
        buttons.forEach((button -> {
            button.setEnabled(false);
            button.setAlpha(0.25f);
        }));
    }

    private ArrayList<Button> getTakenCells(Board board) {
        ArrayList<Button> takenButtons = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.get(new Pose2d(i, j)) != Piece.EMPTY) takenButtons.add(buttons[i][j]);
            }
        }

        return takenButtons;
    }

    private void updateIndicator(Pose2d pose) {
        // delete the last one
        if (mainImages[indicatorPose.i][indicatorPose.j].getTag().equals("indicator"))
            mainImages[indicatorPose.i][indicatorPose.j].setImageResource(R.drawable.empty);

        if (!mainImages[pose.i][pose.j].getTag().equals("image")) {
            // update the pose to the new pose & display the indicator
            indicatorPose = pose;
            mainImages[pose.i][pose.j].setImageResource(R.drawable.indicator);
            mainImages[pose.i][pose.j].setTag("indicator");
        }
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