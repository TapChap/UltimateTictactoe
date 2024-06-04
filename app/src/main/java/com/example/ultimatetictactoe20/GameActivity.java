package com.example.ultimatetictactoe20;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.ultimatetictactoe20.Tictactoe.Board;
import com.example.ultimatetictactoe20.Tictactoe.Piece;
import com.example.ultimatetictactoe20.Tictactoe.Pose;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private final String TAG = "GameActivity";

    private Button backButton, resetButton;
    private TextView contactDisplay;

    private Board[][] boards = new Board[3][3];
    private Board mainBoard = new Board();
    private Board selectedBoard;

    private boolean canChoose;

    private final Button[][] buttons = new Button[3][3];
    private ImageView turnDisplay;
    private TextView winnerDisplay;

    private Pose indicatorPose = new Pose(0, 0);

    private boolean hasContact = false;
    private String contactName = "";

    public static Database database;
    private LowBatteryReceiver batteryReceiver;

    private Vibrator vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        database = new Database(this);

        Intent incoming = getIntent();
        hasContact = incoming.hasExtra("CONTACT_NAME");
        contactName = incoming.getStringExtra("CONTACT_NAME");

        batteryReceiver = new LowBatteryReceiver(this::saveGame, hasContact);
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        backButton = findViewById(R.id.backBttn);
        resetButton = findViewById(R.id.resetBttn);
        contactDisplay = findViewById(R.id.contactDisplay);

        backButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        if (hasContact) {
            contactDisplay.setVisibility(View.VISIBLE);
            contactDisplay.setText(contactName);

            resetButton.setVisibility(View.VISIBLE);
        }

        Pose.forEach((i, j, pose)-> boards[i][j] = new Board(getBoard(pose.getPoseIndex()), pose));

            Pose.forEach((i, j, pose)-> {
                String strA = "button" + i + j;
                int resIdA = getResources().getIdentifier(strA, "id", getPackageName());
                buttons[i][j] = findViewById(resIdA);
                buttons[i][j].setOnClickListener(this);

                String strB = "boardImage" + i + j;
                int resIdB = getResources().getIdentifier(strB, "id", getPackageName());
                mainBoard.setImage(pose, findViewById(resIdB));
                mainBoard.getBoardImage(pose).setOnTouchListener(this);
                mainBoard.getBoardImage(pose).setTag("");

                String strC = "Grid" + i + j;
                int resIdC = getResources().getIdentifier(strC, "id", getPackageName());
                boards[i][j].addLayout(findViewById(resIdC));
        });

        turnDisplay = findViewById(R.id.turnDisplay);
        turnDisplay.setImageResource(Piece.X.getImg());

        winnerDisplay = findViewById(R.id.winnerDisplay);

        setControlPanelEnabled(false);
        canChoose = true;

//        this.deleteDatabase("tictactoe.db");
        if (hasContact && database.hasSavedGame(contactName)) loadGame();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        Pose.forEach((i, j, nextPose) -> {
            // control panel button pressed
                if (buttons[i][j].getId() == id) {
                    Pose currentPose = selectedBoard.getPose(); // the pose of the selected board

                    vibrate.vibrate(VibrationEffect.createOneShot(150, 127));

                    setControlPanelEnabled(true);
                    selectedBoard.setPiece(nextPose, mainBoard.getTurn());
                    selectedBoard.update(); // update the image in the board that was just played

                    // disable clicks & update image if won inner board
                    if (selectedBoard.hasWon(mainBoard.getTurn())) {
                        mainBoard.setPiece(currentPose);
                        mainBoard.getBoardImage(currentPose).setClickable(false);
                        mainBoard.getBoardImage(currentPose).setTag("image");
                        mainBoard.update();
                        selectedBoard.update();
                    } else if (selectedBoard.isTie()) {
                        selectedBoard.reset();
                        selectedBoard.update();
                    }

                    // check if the entire game was won
                    if (mainBoard.hasWon()) {
                        winnerDisplay.setVisibility(View.VISIBLE);
                        setControlPanelEnabled(false);
                        database.remove(contactName);
                        vibrate.vibrate(VibrationEffect.createOneShot(500, 255));
                        return;
                    }

                    // check if the entire game was tied
                    if (mainBoard.isTie()) {
                        winnerDisplay.setText("It's a Tie!");
                        winnerDisplay.setVisibility(View.VISIBLE);
                        turnDisplay.setVisibility(View.GONE);
                        setControlPanelEnabled(false);
                        database.remove(contactName);
                        return;
                    }

                    // checking if the next board can be played
                    if (boards[i][j].hasWon(Piece.EMPTY)) {
                        // allow the next player to choose the next board, if the chosen board was won
                        setControlPanelEnabled(false);
                        // crate a new, non-real board in case the game is saved, to not display the indicator when relaunched
                        selectedBoard = Board.emptyBoard;
                        canChoose = true;
                    } else {
                        disableButtons(getTakenCells(boards[i][j]));
                        selectedBoard = boards[i][j];
                        canChoose = false;
                    }

                    updateIndicator(nextPose);
                    mainBoard.next();
                    turnDisplay.setImageResource(mainBoard.getTurn().getImg());
                    if (hasContact) saveGame();
                }
            });

        if (id == R.id.resetBttn) {
            database.remove(contactName);
            setControlPanelEnabled(false);
            canChoose = true;

            mainBoard.reset();
            mainBoard.update();

            Pose.forEach((i, j, pose) -> {
                boards[i][j].reset();
                boards[i][j].update();
                boards[i][j].setBoardVisibility(true);

                mainBoard.getBoardImage(pose).setTag("empty");
            });
        }
        if (id == R.id.backBttn) finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();

        // main image pressed
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            Pose.forEach((i, j, pose) -> {
                if (mainBoard.getBoardImage(pose).getId() == id) {
                    if (canChoose && !boards[i][j].hasWon() && !mainBoard.hasWon()) {
                        selectedBoard = boards[i][j];
                        updateIndicator(pose);
                        setControlPanelEnabled(true);
                        disableButtons(getTakenCells(boards[i][j]));
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void setControlPanelEnabled(boolean enabled) {
        Pose.forEach((i, j, pose) -> {
            buttons[i][j].setEnabled(enabled);
            buttons[i][j].setAlpha(enabled ? 1f : 0.25f);
        });
    }

    private void disableButtons(ArrayList<Button> buttons) {
        buttons.forEach((button -> {
            button.setEnabled(false);
            button.setAlpha(0.25f);
        }));
    }

    private ArrayList<Button> getTakenCells(Board board) {
        ArrayList<Button> takenButtons = new ArrayList<>();

        Pose.forEach((i, j, pose) -> {
            if (board.get(pose) != Piece.EMPTY) takenButtons.add(buttons[i][j]);
        });

        return takenButtons;
    }

    private void updateIndicator(Pose pose) {
        // delete the last one
        if (mainBoard.getBoardImage(indicatorPose).getTag().equals("indicator"))
            mainBoard.getBoardImage(indicatorPose).setImageResource(R.drawable.empty);

        if (!mainBoard.getBoardImage(pose).getTag().equals("image")) {
            // update the pose to the new pose & display the indicator
            indicatorPose = pose;
            mainBoard.getBoardImage(pose).setImageResource(R.drawable.indicator);
            mainBoard.getBoardImage(pose).setTag("indicator");
        }
    }

    public ImageView[][] getBoard(int boardIndex) {
        ImageView[][] board = new ImageView[3][3];

        Pose.forEach((i, j, pose)-> {
            String str = "piece" + ((boardIndex * 9) + pose.getPoseIndex());
            int resId = getResources().getIdentifier(str, "id", getPackageName());
            board[i][j] = findViewById(resId);
        });

        return board;
    }

    private String gameToString(){
        StringBuilder builder = new StringBuilder();
        Pose.forEach((i, j, pose)-> builder.append(boards[i][j].toString()));
        return builder.toString();
    }

    private void saveGame(){
        // save file content
        //(turn: x/o)(selectedBoard: 0 - 8/-)(canChoose: T/F)(mainBoardState: 9 * x/o/-)(gameState: 81 * x/o/-)

        database.saveState(String.valueOf(
                mainBoard.getTurn().getChar()) + selectedBoard.getPose().getPoseIndex() +
                (canChoose? "T" : "F") + mainBoard.toString() + gameToString(),
                contactName);

        Log.d(TAG, "saved: " + database.getState(contactName));
    }

    private void loadGame(){
        String saveFile = database.getState(contactName);

        // turn
        if (saveFile.charAt(0) == 'O') mainBoard.next();

        // current board location
        int currentBoardLocation = saveFile.charAt(1) - '0'; // subtract '0' to get the ASCII index
        if (currentBoardLocation != Board.emptyBoard.getPose().getPoseIndex()) {
            Pose pose = new Pose(currentBoardLocation);
            selectedBoard = boards[pose.i][pose.j];
            new Handler().postDelayed(() -> updateIndicator(selectedBoard.getPose()), 25);
        }

        // main board state:
        mainBoard.loadBoard(saveFile.substring(3, 12));
        mainBoard.update();

        // game state
        AtomicInteger index = new AtomicInteger(12);
        Pose.forEach((i, j, pose)-> {
            boards[i][j].loadBoard(database.getState(contactName).substring(index.get(), index.get() + 9));
            boards[i][j].update();
            index.addAndGet(9);
        });

        //can choose
        char savedCanChoose = saveFile.charAt(2);
        this.canChoose = (savedCanChoose == 'T');
        if (!canChoose) {
            setControlPanelEnabled(true);
            disableButtons(getTakenCells(selectedBoard));
        }
    }
}