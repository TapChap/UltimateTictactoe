package com.example.ultimatetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ultimatetictactoe.Tictactoe.Board;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "GameActivity";

    private Board[][] boards = new Board[3][3];
    private Board mainBoard = new Board();
    private Board selectedBoard;

    private boolean canChoose = true;

    private Button buttons[][] = new Button[3][3];
    private ImageView mainImages[][] = new ImageView[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        for (int i = 0; i < boards.length; i++)
            for (int j = 0; j < boards[i].length; j++) {
                boards[i][j] = new Board(getBoard(i, j));
            }

        selectedBoard = boards[0][0];

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                String strA = "b" + i + j;
                String strB = "boardImage" + i + j;
                int resIdA = getResources().getIdentifier(strA, "id", getPackageName());
                int resIdB = getResources().getIdentifier(strB, "id", getPackageName());
                buttons[i][j] = findViewById(resIdA);
                buttons[i][j].setOnClickListener(this);

                mainImages[i][j] = findViewById(resIdB);
                mainBoard.setImage(i, j, mainImages[i][j]);
                mainImages[i][j].setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {

                // control panel button pressed
                if (buttons[i][j].getId() == id) {
                    selectedBoard.set(i, j, mainBoard.getTurn());
                    selectedBoard = boards[i][j];
                }

                // main image pressed
                if (mainImages[i][j].getId() == id) {
                    if (canChoose) selectedBoard = boards[i][j];

                }
            }
        }
    }

    public ImageView[][] getBoard(int i, int j) {
        ImageView[][] board = new ImageView[3][3];
        int boardIndex = i * 3 + j;

        for (int l = 0; l < 9; l++) {
            String str = "piece" + ((boardIndex * 9) + l);
            int resId = getResources().getIdentifier(str, "id", getPackageName());
            board[l / 3][l % 3] = findViewById(resId);
        }

        return board;
    }
}