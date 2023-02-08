package com.example.msproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.IOException;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Login extends AppCompatActivity {

    Button b_login;
    EditText e_login;
    String username;
    Socket s;
    PrintWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b_login = (Button) findViewById(R.id.b_login);
        e_login = (EditText) findViewById(R.id.e_login);





        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loginpage();
            }

        });

    }
    public void Loginpage(){



       username = e_login.getText().toString();
       Login.BackgroundTask2 b2 = new Login.BackgroundTask2();
       b2.execute(username);
       Thread myThread = new Thread(new MyServerThread());
       myThread.start();


    }

    class MyServerThread implements Runnable {

            ServerSocket ss;
            InputStreamReader isr;
            BufferedReader br;
            Handler h = new Handler();
            String message;



        @Override
        public void run() {
                try{

               ss = new ServerSocket(6001);
                while (true){
                    s = ss.accept();
                    isr = new InputStreamReader(s.getInputStream());
                    br = new BufferedReader(isr);
                    message = br.readLine();
                    h.post(new Runnable() {
                        @Override
                        public void run() {

                           if (message.equals("ok")) {
                               e_login.setText("correct");
                               Intent intent = new Intent(Login.this, Login_page.class);
                               intent.putExtra("username",username);
                               startActivity(intent);
                            }else {
                               e_login.setText("incorrect credentials");

                               Toast toast=Toast.makeText(getApplicationContext(),"Wrong Credential",Toast.LENGTH_SHORT);
                               toast.show();

                            }

                            }
                    });
                }



            } catch (IOException e){
                e.printStackTrace();

            }

        }
    }

    class BackgroundTask2 extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try {

                String username = voids[0];
                int a = 2;

                s = new Socket("IP", 6000);

                writer = new PrintWriter(s.getOutputStream());

                writer.write(username + a);
                writer.flush();
                writer.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}