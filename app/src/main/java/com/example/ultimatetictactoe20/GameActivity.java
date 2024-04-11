package com.example.ultimatetictactoe20;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ultimatetictactoe20.Tictactoe.Board;
import com.example.ultimatetictactoe20.Tictactoe.Piece;
import com.example.ultimatetictactoe20.Tictactoe.Pose2d;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private final String TAG = "GameActivity";

    private Board[][] boards = new Board[3][3];
    private Board mainBoard = new Board();
    private Board selectedBoard;

    private boolean canChoose;

    private final Button[][] buttons = new Button[3][3];
    private ImageView turnDisplay;
    private TextView winnerDisplay;

    private Pose2d indicatorPose = new Pose2d(0, 0);

    private boolean hasContact;
    private String contactName = "";

    private final int NULL_BOARD = 9;

    public static Database database;
    private LowBatteryReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        database = new Database(this);

        Intent incoming = getIntent();
        hasContact = incoming.hasExtra("CONTACT_NAME");
        contactName = incoming.getStringExtra("CONTACT_NAME");

        batteryReceiver = new LowBatteryReceiver(this::saveGame, hasContact);

        database = new Database(this);
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

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

                mainBoard.setImage(new Pose2d(i, j), findViewById(resIdB));
                Pose2d pose = new Pose2d(i, j);
                mainBoard.getBoardImage(pose).setOnTouchListener(this);
                mainBoard.getBoardImage(pose).setTag("");
            }
        }

        turnDisplay = findViewById(R.id.turnDisplay);
        turnDisplay.setImageResource(Piece.X.getImg());

        winnerDisplay = findViewById(R.id.winnerDisplay);

        setControlPanelEnabled(false);
        canChoose = true;

//        this.deleteDatabase("memory_db.db");
        if (hasContact && database.hasSavedGame(contactName)) loadGame();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        foreach((i, j) -> {
            // control panel button pressed
                if (buttons[i][j].getId() == id) {
                    Pose2d nextPose = new Pose2d(i, j); // the pose of the next board
                    Pose2d currentPose = selectedBoard.getPose(); // the pose of the selected board

                    setControlPanelEnabled(true);
                    selectedBoard.setPiece(nextPose, mainBoard.getTurn());
                    selectedBoard.update(); // update the image in the board that was just played

                    // disable clicks & update image if won inner board
                    if (selectedBoard.hasWon(mainBoard.getTurn())) {
                        mainBoard.setPiece(currentPose);
                        mainBoard.getBoardImage(currentPose).setClickable(false);
                        mainBoard.getBoardImage(currentPose).setTag("image");
                        mainBoard.update();
                    } else if (selectedBoard.isTie()) {
                        selectedBoard.reset();
                        selectedBoard.update();
                    }

                    // check if the entire game was won
                    if (mainBoard.hasWon()) {
                        winnerDisplay.setVisibility(View.VISIBLE);
                        setControlPanelEnabled(false);
                        disableMainImages();
                        database.remove(contactName);
                        return;
                    }

                    // check if the entire game was tied
                    if (mainBoard.isTie()) {
                        winnerDisplay.setVisibility(View.VISIBLE);
                        winnerDisplay.setText("It's a Tie!");
                        turnDisplay.setVisibility(View.GONE);
                        setControlPanelEnabled(false);
                        disableMainImages();
                        database.remove(contactName);
                        return;
                    }

                    // checking if the next board can be played
                    if (boards[i][j].hasWon(Piece.EMPTY)) {
                        // allow the next player to choose the next board, if the chosen board was won
                        setControlPanelEnabled(false);
                        // crate a new, non-real board in case the game is saved, to not display the indicator when relaunched
                        selectedBoard = new Board(new ImageView[3][3], new Pose2d(0, NULL_BOARD));
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
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();

        // main image pressed
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            foreach((i, j) -> {
                if (mainBoard.getBoardImage(new Pose2d(i, j)).getId() == id) {
                    if (canChoose && !boards[i][j].hasWon(Piece.EMPTY)) {
                        selectedBoard = boards[i][j];
                        updateIndicator(new Pose2d(i, j));
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
        foreach((i, j) -> {
            buttons[i][j].setEnabled(enabled);
            buttons[i][j].setAlpha(enabled ? 1f : 0.25f);
        });
    }

    private void disableMainImages() {
     foreach((i, j) -> mainBoard.getBoardImage(new Pose2d(i, j)) .setClickable(false));
    }

    private void disableButtons(ArrayList<Button> buttons) {
        buttons.forEach((button -> {
            button.setEnabled(false);
            button.setAlpha(0.25f);
        }));
    }

    private ArrayList<Button> getTakenCells(Board board) {
        ArrayList<Button> takenButtons = new ArrayList<>();

        foreach((i, j) -> {
            if (board.get(new Pose2d(i, j)) != Piece.EMPTY) takenButtons.add(buttons[i][j]);
        });

        return takenButtons;
    }

    private void updateIndicator(Pose2d pose) {
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

        for (int i = 0; i < 9; i++) {
            String str = "piece" + ((boardIndex * 9) + i);
            int resId = getResources().getIdentifier(str, "id", getPackageName());
            board[i / 3][i % 3] = findViewById(resId);
        }

        return board;
    }

    private void foreach(BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                consumer.accept(i, j);
            }
        }
    }

    private String gameToString(){
        StringBuilder builder = new StringBuilder();
        foreach((i, j)-> builder.append(boards[i][j].toString()));
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
        if (currentBoardLocation != NULL_BOARD) {
            selectedBoard = boards[currentBoardLocation / 3][currentBoardLocation % 3];
            new Handler().postDelayed(() -> updateIndicator(selectedBoard.getPose()), 25);
        }

        // main board state:
        mainBoard.loadBoard(saveFile.substring(3, 12));
        mainBoard.update();

        // game state
        AtomicInteger index = new AtomicInteger(12);
        foreach((i, j)-> {
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