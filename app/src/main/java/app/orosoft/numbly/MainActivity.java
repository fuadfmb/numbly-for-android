package app.orosoft.numbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button btn_enter, btn_clear, btn_reset;
    LinearLayout linear1, linear2;
    TextView tv_input,tv_hint;

    String generatedString = generateNumber();
    String userInputs;
    int trials = 0;
    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_enter = findViewById(R.id.btn_enter);
        btn_clear = findViewById(R.id.btn_clear);
        btn_reset = findViewById(R.id.btn_reset);
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        tv_input = findViewById(R.id.tv_input);
        tv_hint = findViewById(R.id.tv_hint);

        showHint();

        btn_reset.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // this will show the generated string when long pressed..! for debugging :)
                Toast.makeText(MainActivity.this, generatedString, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = MainActivity.this;

                if (gameOver){
                    btn_reset.performClick();
                    gameOver = false;
                }

                userInputs = tv_input.getText().toString();
                if (userInputs.length() != 4 ){
                    // Toast.makeText(MainActivity.this, "Enter 4 Digit number.", Toast.LENGTH_SHORT).show();
                    shakeInputArea();
                    return;
                }

                trials++;

                // the game ends at 8 inputs...
                if (trials == 8){
                    new AlertDialog.Builder(context)
                            .setTitle("Game Over!")
                            .setMessage("The number is : "+generatedString)
                            .setCancelable(true)
                            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // resets the game...
                                    btn_reset.performClick();
                                }
                            })
                            .create().show();
                    gameOver = true;
                }

                // we will generate 6 different buttons every time the user clicks enter button.
                // the first 4 buttons on the left, with user input set on them,
                // and the 5th and 6th buttons on the right with the total number of digits that are
                // in the generated string and the total number of digits on the right position set on them...
                // for that we will create 2 horizontal linear layout, one for the first 4 buttons, and
                // the second for the last two buttons(4th and 5th buttons).
                
                LinearLayout linear = new LinearLayout(context);
                linear.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 80));
                linear.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                        );
                params.setMargins(5,5,5,5);

                // generate buttons...
                boolean done = countCorrectNumbers(userInputs) == 4 && countNumbersOnPosition(userInputs) == 4;
                for (int i=0;i<userInputs.length();i++){
                    Button b = new Button(context);
                    b.setLayoutParams(params); // adds android:layout_weight="1" attribute to this button.
                    b.setTextColor(Color.parseColor("#ffffff"));
                    b.setText( String.valueOf( userInputs.charAt(i) ) );
                    if (done){
                        b.setBackgroundResource(R.drawable.btn_green);
                    }
                    else{
                        b.setBackgroundResource(R.drawable.btn_dark);
                    }
                    b.setClickable(false);
                    linear.addView(b);
                }

                linear1.addView( linear );

                // for linear layout on the right
                LinearLayout linear22 = new LinearLayout(context);
                linear22.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, 80));
                linear22.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                );
                params2.setMargins(5,5,5,5);

                for(int i=0;i<2;i++){
                    Button b = new Button(context);
                    b.setLayoutParams(params2);
                    b.setClickable(false);
                    if (i==0){ // Yellow button
                        b.setText( String.valueOf( countCorrectNumbers(userInputs) ) );
                        if (done) {
                            b.setBackgroundResource(R.drawable.btn_green);
                            b.setTextColor(Color.parseColor("#ffffff"));
                        }
                        else {
                            b.setBackgroundResource(R.drawable.btn_yellow);
                            b.setTextColor(Color.parseColor("#000000"));
                        }
                    }
                    else{ // green button
                        b.setBackgroundResource(R.drawable.btn_green);
                        b.setTextColor(Color.parseColor("#ffffff"));
                        b.setText( String.valueOf( countNumbersOnPosition(userInputs) ) );
                    }
                    linear22.addView(b);
                }
                linear2.addView( linear22 );

                if ( done ){ // correct input...
                    gameOver = true;
                    new AlertDialog.Builder(context)
                            .setTitle("Awesome!")
                            .setMessage("You finished the game in "+trials+" steps.\nPress Reset or Enter to restart the game.")
                            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    btn_reset.performClick();
                                }
                            })
                    .create().show();
                }
                else { //wrong guess, clear input area...
                    tv_input.setText("");
                }

            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear1.removeAllViews();
                linear2.removeAllViews();

                generatedString = generateNumber();
                trials = 0;
                // tv_hint.setText( generatedString );
                tv_input.setText("");
                gameOver = false;
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_input.setText("");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.hint){
            showHint();
        }
        else if (item.getItemId() == R.id.about){
            aboutBox();
        }
        else if (item.getItemId() == R.id.showinbrowser){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://numbly.abdiadem.com"));
            Intent urlintent = Intent.createChooser(intent, "Browse With :");
            startActivity(urlintent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    public void handleNumButtons(View v){
        int number = 0;
        switch (v.getId()){
            case R.id.btn_0:
                number = 0; break;
            case R.id.btn_1:
                number = 1; break;
            case R.id.btn_2:
                number = 2; break;
            case R.id.btn_3:
                number = 3; break;
            case R.id.btn_4:
                number = 4; break;
            case R.id.btn_5:
                number = 5; break;
            case R.id.btn_6:
                number = 6; break;
            case R.id.btn_7:
                number = 7; break;
            case R.id.btn_8:
                number = 8; break;
            case R.id.btn_9:
                number = 9; break;
        }

        if (gameOver){
            shakeInputArea();
            return;
        }

        String inputs = tv_input.getText().toString();
        if (inputs.length() < 4){
            userInputs = tv_input.getText().toString();
            if ( userInputs.contains( String.valueOf( number ) ) ){
                //Toast.makeText(this, String.valueOf(number) + " is already there in the input.", Toast.LENGTH_SHORT).show();
                shakeInputArea();
                return;
            }
            tv_input.setText( inputs + String.valueOf( number ) );
        }
        else{
            shakeInputArea();
//            tv_input.setText( String.valueOf( number ) );
            // let's not accept more than 4 inputs...
        }
    }

    public String generateNumber(){
        Random rand = new Random();
        String Number = "";
        for(int i=0; i<4;i++){
            int generatedString = rand.nextInt(9);
            // A number cannot start with zero.
            if (i==0){
                while (generatedString == 0){
                    generatedString = rand.nextInt(9);
                }
            }
            else{
                while ( Number.contains( String.valueOf(generatedString) ) ){
                    generatedString = rand.nextInt(9);
                }
            }
            Number += generatedString;
        }
        return Number;
    }

    public int countNumbersOnPosition(String givenNumbers){
        int count = 0;
        for(int i=0; i<4;i++){
            if ( generatedString.charAt(i) == givenNumbers.charAt(i) ){
                count++;
            }
        }
        return count;
    }

    public int countCorrectNumbers(String givenNumbers){
        int count = 0;
        for(int i=0; i<4;i++){
            if ( generatedString.contains( String.valueOf( givenNumbers.charAt(i) ) ) ){
                count++;
            }
        }
        return count;
    }

    public void shakeInputArea(){
        tv_input.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake));

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else{
            v.vibrate(200);
        }
    }

    public void showHint(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Numbly Rules!")
                .setMessage("I will pick a random 4 digit number. then the game is to guess that number. For each guess you make, " +
                        "I will give you some hints.\n" +
                        "1. The total number of digits that are in the number, but \"may or may not be at the proper position\" are " +
                        "given in the yellow box.\n" +
                        "2. The number of digits that are in the number and at the proper position are given in the Green box.\n\n" +
                        "To restart the game, just hit Reset button.\n\n" +
                        "To read this again, press the 3 dots on the toolbar and choose show hint.")
                .setCancelable(true)

                .create().show();
    }

    public void aboutBox(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("About Us")
                .setMessage("Developed by @fuadfmb based on \nhttp://numbly.abdiadem.com")
                .setCancelable(true)
                .create().show();
    }

}
