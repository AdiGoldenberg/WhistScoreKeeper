package com.example.adi.whistscorekeeper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Global variables
    int roundNum = 1;
    int[] totalScores = {0, 0, 0, 0};
    int[] guessTakeValArray = {0, 0, 0, 0, 0, 0, 0, 0};
    int[] guessInd = {0, 1, 2, 3};
    int[] takingInd = {4, 5, 6, 7};
    int[] totalScoresIDs = {R.id.totalScoreA, R.id.totalScoreB, R.id.totalScoreC, R.id.totalScoreD};
    int[] lastScoreIDs = {R.id.lastScoreA, R.id.lastScoreB, R.id.lastScoreC, R.id.lastScoreD};
    int[] plusButtonArray = {R.id.plusGuessA, R.id.plusGuessB, R.id.plusGuessC, R.id.plusGuessD,
            R.id.plusTakingsA, R.id.plusTakingsB, R.id.plusTakingsC, R.id.plusTakingsD};
    int[] minusButtonArray = {R.id.minusGuessA, R.id.minusGuessB, R.id.minusGuessC, R.id.minusGuessD,
            R.id.minusTakingsA, R.id.minusTakingsB, R.id.minusTakingsC, R.id.minusTakingsD};
    int[] guessTakeIDArray = {R.id.guessA, R.id.guessB, R.id.guessC, R.id.guessD,
            R.id.takingsA, R.id.takingsB, R.id.takingsC, R.id.takingsD};

    // Upon app starting, insert round number to the title and lock the buttons for takings and submitTakings
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableClick(false, takingInd);
        enableStartRound(true);
        updateTitle("");


        // Create a ListArray of CardsSuit objects
        ArrayList<CardsSuit> cardsSuitList = new ArrayList<CardsSuit>() {{
            add(new CardsSuit(getResources().getString(R.string.spade), R.drawable.spade));
            add(new CardsSuit(getResources().getString(R.string.heart), R.drawable.heart));
            add(new CardsSuit(getResources().getString(R.string.clover), R.drawable.clover));
            add(new CardsSuit(getResources().getString(R.string.diamond), R.drawable.diamond));
        }};

        // Create an object for the cardsSuit spinner, it's adapter, and associate them
        Spinner suitSpinner = (Spinner) findViewById(R.id.suitSpinner);
        SuitAdapter suitAdapter = new SuitAdapter(this, cardsSuitList);
        suitSpinner.setAdapter(suitAdapter);
    }

    /**
     * Close the keyboard when the user touches outside an EditText view and make the cursor disappear.
     * If the user touches inside an editText, make the cursor appear again.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && view instanceof EditText && ev.getAction() == MotionEvent.ACTION_DOWN) {
            int viewLoc[] = new int[2];
            view.getLocationOnScreen(viewLoc);
            float rawX = ev.getRawX();
            float viewLeft = view.getLeft();
            float rawY = ev.getRawY();
            float viewTop = view.getTop();
            float x = ev.getRawX() + view.getLeft() - viewLoc[0];
            float y = ev.getRawY() + view.getTop() - viewLoc[1];
            if (x < view.getLeft() || x > view.getRight() ||
                    y < view.getTop() || y > view.getBottom()) {
                hideKeyboard();
                view.clearFocus();
                ((EditText) view).setCursorVisible(false);
            } else {
                ((EditText) view).setCursorVisible(true);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * This method gets a view ID, checks its value, and if smaller than 13 increases it by 1
     *
     * @param i index of the entry in guessTakeArray (both ID and Val) of the textView to increment
     */
    public void increment(int i) {
        if (guessTakeValArray[i] < 13) {
            guessTakeValArray[i] += 1;
            displayVal(guessTakeIDArray[i], guessTakeValArray[i]);
        }
    }

    /**
     * This method gets a view ID, checks its value, and if bigger than 0 decreases it by 1
     *
     * @param i index of the entry in guessTakeArray (both ID and Val) of the textView to decrement
     */
    public void decrement(int i) {
        if (guessTakeValArray[i] > 0) {
            guessTakeValArray[i] -= 1;
            displayVal(guessTakeIDArray[i], guessTakeValArray[i]);
        }
    }

    /**
     * This method gets the ID of the clicked plus button and calls the increment method on the
     * appropriate textView
     */
    public void plusButtonClick(View view) {
        int buttonId = view.getId();
        for (int i = 0; i <= 7; i++) {
            if (buttonId == plusButtonArray[i]) {
                increment(i);
            }
        }
    }

    /**
     * This method gets the ID of the clicked plus button and calls the increment method on the
     * appropriate textView
     */
    public void minusButtonClick(View view) {
        int buttonId = view.getId();
        for (int i = 0; i <= 7; i++) {
            if (buttonId == minusButtonArray[i]) {
                decrement(i);
            }
        }
    }

    /**
     * This method locks the guess buttons and unlocks the takings if the total guesses is legal
     */
    public void startRound(View view) {
        // Getting the sum of the guesses and verifying it's legal
        int guessSum = sumPlayers(guessInd);
        if (guessSum == 13) {
            Toast.makeText(this, R.string.incorrectTotalGuess, Toast.LENGTH_LONG).show();
            return;
        }
        // Switch enable state of the guess and takings
        enableClick(true, takingInd);
        enableClick(false, guessInd);
        // Enable submit takings
        enableStartRound(false);
        // Note the round type - Down or Up
        String titleSuffix = " - ";
        if (guessSum > 13) {
            titleSuffix += getString(R.string.up);
        } else {
            titleSuffix += getString(R.string.down);
        }
        updateTitle(titleSuffix);
        // Scroll to top
        scrollToTop();
    }

    /**
     * This method finishes the current round, calculates the scores, and starts another round
     */
    public void submitTakings(View view) {
        // Getting the sum of the guesses and verifying it's legal
        int takingSum = sumPlayers(takingInd);
        if (takingSum != 13) {
            Toast.makeText(this, R.string.incorrectTotalTakings, Toast.LENGTH_LONG).show();
            return;
        }
        // Switch enable state of the guess and takings
        enableClick(false, takingInd);
        enableClick(true, guessInd);
        // Switch the enabled button between submitTakings and startRound
        enableStartRound(true);
        // Calculate scores
        for (int playerInd : guessInd) {
            int roundScore;
            // If took as guessed
            if (guessTakeValArray[playerInd] == guessTakeValArray[playerInd + 4]) {
                if (guessTakeValArray[playerInd] == 0) {    // If guessed zero
                    if (sumPlayers(guessInd) < 13) {       // If it's a down game
                        roundScore = 50;
                    } else {                    // If it's an up game
                        roundScore = 25;
                    }
                } else {        // If guessed other than zero
                    roundScore = 10 + (int) Math.pow(guessTakeValArray[playerInd], 2);
                }
            }
            // If didn't take as guessed
            else {
                if (guessTakeValArray[playerInd] == 0) {    // If guessed zero
                    roundScore = -50 + 10 * (guessTakeValArray[playerInd + 4] - 1);
                } else {        // If guessed other than zero
                    roundScore = -10 * Math.abs(guessTakeValArray[playerInd] - guessTakeValArray[playerInd + 4]);
                }
            }
            // Add the round score to the total score and update both text views
            totalScores[playerInd] += roundScore;
            displayVal(lastScoreIDs[playerInd], roundScore);
            displayVal(totalScoresIDs[playerInd], totalScores[playerInd]);
            // Returning the player takings to zero
            guessTakeValArray[playerInd + 4] = 0;
            displayVal(guessTakeIDArray[playerInd + 4], guessTakeValArray[playerInd + 4]);
        }
        // advance the roundNum and update the title
        roundNum += 1;
        updateTitle("");
        // Scroll to top
        scrollToTop();
    }

    /**
     * This method resets the app
     */
    public void reset(View view) {
        // reset the scores, guesses and takings
        for (int i = 0; i <= 7; i++) {
            guessTakeValArray[i] = 0;
            if (i <= 3) {
                totalScores[i] = 0;
                displayVal(totalScoresIDs[i], totalScores[i]);
                displayVal(lastScoreIDs[i], 0);
            }
            displayVal(guessTakeIDArray[i], guessTakeValArray[i]);
        }
        // reset the roundNum and title
        roundNum = 1;
        updateTitle("");
        // restore the enable state of the guess, takings startRound and submitTakings buttons
        enableClick(false, takingInd);
        enableClick(true, guessInd);
        enableStartRound(true);
        // Scroll to top
        scrollToTop();
    }

    /**
     * This method changes the enable state of a group of buttons
     *
     * @param enable   is the desired enable state (true or false)
     * @param indecies is the indecies in plusButtonArray and minusButtonArray to be changed (e.g. {0,1,2,3})
     */
    public void enableClick(boolean enable, int[] indecies) {
        for (int i : indecies) {
            Button plusButton = (Button) findViewById(plusButtonArray[i]);
            Button minusButton = (Button) findViewById(minusButtonArray[i]);
            plusButton.setEnabled(enable);
            minusButton.setEnabled(enable);
        }
    }

    /**
     * This method flipps the enable state of submitTakings and startRound
     *
     * @param enable is the desired enable state (true or false) of startRound. submitTakings would have the opposite
     */
    public void enableStartRound(boolean enable) {
        Button submitTakings = (Button) findViewById(R.id.submitTakings);
        Button startRound = (Button) findViewById(R.id.startRound);
        Spinner suitSpinner = (Spinner) findViewById(R.id.suitSpinner);
        submitTakings.setEnabled(!enable);
        startRound.setEnabled(enable);
        suitSpinner.setEnabled(enable);
    }

    /**
     * This method calculates the sum of the guesses
     *
     * @param inds calculates the sum of enteries in guessTakeValArray at the given inds
     */
    public int sumPlayers(int[] inds) {
        int sumPlayers = 0;
        for (int i : inds) {
            sumPlayers += guessTakeValArray[i];
        }
        return sumPlayers;
    }

    /**
     * This method displays a value from guessTakeValArray in the corresponding view
     *
     * @param viewId the view ID to put the value in
     * @param value  the value to put in the view
     */
    public void displayVal(int viewId, int value) {
        TextView textView = (TextView) findViewById(viewId);
        textView.setText(String.valueOf(value));
    }

    /**
     * This method updated the title to be "Round #" with a possible given suffix
     *
     * @param str2add is the suffix to add after the round number. For nothing put "".
     */
    public void updateTitle(String str2add) {
        String title = getString(R.string.round) + " " + String.valueOf(roundNum) + str2add;
        TextView roundNumView = (TextView) findViewById(R.id.roundNumber);
        roundNumView.setText(title);
    }

    /**
     * This function scrolls to the top of the scroll view
     */
    public void scrollToTop() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0, 0);
    }

    /**
     * This method hides the keyboard
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getApplicationWindowToken(), 0);
    }
}

