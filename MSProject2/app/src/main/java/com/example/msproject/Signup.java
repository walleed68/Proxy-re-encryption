package com.example.msproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;



public class Signup extends AppCompatActivity {
    Button b_server;
    EditText e_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        e_username = (EditText) findViewById(R.id.e_username);
        b_server = (Button) findViewById(R.id.b_server);

        b_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               senddata();
            }
        });

    }


    public void senddata(){
        String username = e_username.getText().toString();

        BackgroundTask b1 = new BackgroundTask();
        b1.execute(username);



    }

    class BackgroundTask extends AsyncTask <String, Void, Void>
    {
        Socket s;
        PrintWriter writer;
        @Override
        protected Void doInBackground(String... voids) {

            try {

                String username = voids[0];
                int a = 1;

                s = new Socket("IP",6000);

                writer = new PrintWriter(s.getOutputStream());

                writer.write(username + a);
                writer.flush();
                writer.close();



            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }

}