package com.example.speechtotext2019;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private TextView txtSpeechInput,QuestionText;

    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    static Timer timer;
    private static int sec=3,period=1000;
    Random rnd=new Random();
    public static Boolean pause=true;
    //固定的文字資料初始設定
    static String Quest;
    private static int index=0;
    Vector vector=new Vector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //按鈕實體化
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        QuestionText=(TextView) findViewById(R.id.QuestionText);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);


        //時間函示初始化
        timer = new Timer();
        //這邊開始跑時間執行緒
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //秒數設定
                        if(pause==false){
                            if(sec>0){
                                txtSpeechInput.setText(String.valueOf(sec));
                                sec=sec-1;
                            }
                            else{ QuestionText.setText(String.valueOf(vector.elementAt(index)));
                                promptSpeechInput();
                                pause=true;
                                sec=3;
                            }

                        }
                    }
                });
            }
        };
        timer.schedule(task, 1000, period);//時間在幾毫秒過後開始以多少毫秒執行


        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //promptSpeechInput();
                pause=false;
                getQuest();
                QuestionText.setText("題目即將出現");
                //btnSpeak.setClickable(false);
                btnSpeak.setVisibility(View.GONE);
                Log.d("Quest=",String.valueOf(Quest));
                Enumeration vEnum = vector.elements();
                while(vEnum.hasMoreElements())
                    Log.d("vEnum=",String.valueOf(vEnum.nextElement()));

                //for(int i=0;i<=vector.size()-1;i++)
               // Log.d("v=",String.valueOf(Quest));


            }
        });
    }

    private void getQuest(){
        Resources res=getResources(); //抓放在String裡的固定資源
        //依據題目性質分成Q1/Q2兩個字串陣列
        vector.clear();
        final String[]Q1=res.getStringArray(R.array.Q1);
        final String[]Q2=res.getStringArray(R.array.Q2);
        //Quest=Q1[rnd.nextInt(Q1.length-1)];
        Quest=Q2[rnd.nextInt(Q2.length-1)];
        String[]Quests=Quest.split(",");
        for(int i=0;i<Quests.length;i++)
        {vector.addElement(Quests[i]);

           // Log.d("Quests","Quests:"+i+"="+Quests[i]);
            Log.d("vector","vector:"+i+"="+vector.elementAt(i));
        }


    }
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    Log.d("result=",String.valueOf(result.get(0)));
                    String s1=String.valueOf(QuestionText.getText());
                    String s2=String.valueOf(txtSpeechInput.getText());
                    if(s1.equals(s2))
                    {Toast.makeText(this,"pass",Toast.LENGTH_SHORT).show();/*pause=true;period=1000*/;
                    }
                    else
                    {Toast.makeText(this,"fail",Toast.LENGTH_SHORT).show();}
                    index++;
                    if (index<=vector.size()-1)
                    {pause=false;QuestionText.setText("下一行即將出現");}
                    else{index=0;pause=true;Toast.makeText(this,"恭喜答完題了,有羞恥到嗎?",Toast.LENGTH_SHORT).show();


                        btnSpeak.setVisibility(View.VISIBLE);
                    Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/embed/OpUJqAWgtCw"));
                        i.setPackage("com.google.android.youtube");
                        i.putExtra("force_fullscreen",true);
                        startActivity(i);


                    }

                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speech, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
