package arafath.myappcom.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLS = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();
    int chosenCeleb = 0;
    ImageView imageView;
    String []answers = new String[4];
    int locationOfRightAnswer = 0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    public void celebChosen(View view){
    if(view.getTag().toString().equals(Integer.toString(locationOfRightAnswer))){
        Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_SHORT).show();
    }else{
        Toast.makeText(getApplicationContext(),"Wrong! "+"it was "+celebNames.get(chosenCeleb),Toast.LENGTH_SHORT).show();
    }
    newQuestion();
    }


    public class ImageDownloader extends AsyncTask<String , Void , Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream in = connection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(in);
                return myBitMap;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String , Void , String>{
        @Override
        protected String doInBackground(String... urls) {
        String result = "";
            URL url ;
            HttpURLConnection connection = null;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();


                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data!= -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

                return result;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void newQuestion() {
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebURLS.size());

            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = imageTask.execute(celebURLS.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            locationOfRightAnswer = rand.nextInt(4);
            int locationOfWrongAnswer;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfRightAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    locationOfWrongAnswer = rand.nextInt(celebURLS.size());

                    while (locationOfWrongAnswer == chosenCeleb) {
                        locationOfWrongAnswer = rand.nextInt(celebURLS.size());
                    }
                    answers[i] = celebNames.get(locationOfWrongAnswer);
                }
            }
            button4.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button1.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        DownloadTask task = new DownloadTask();
        String result = null;


        try{
           result =  task.execute("http://www.posh24.se/kandisar").get();
          String []splitResult = result.split("<div class=\"listedArticles\">");

          Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebURLS.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){
                celebNames.add(m.group(1));
            }

            newQuestion();

        }catch(Exception e){
           e.printStackTrace();
        }
    }
}
