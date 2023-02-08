package com.example.msproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.Base64;

import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.encrypt.NtruEncrypt;

public class Login_page extends AppCompatActivity {
    NtruEncrypt ntru = new NtruEncrypt(EncryptionParameters.APR2011_439_FAST);
    EncryptionKeyPair kp = ntru.generateKeyPair();

    private static final int READ_REQUEST_CODE = 42;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;

    Button b_upload, b_download;
    EditText e_fusername;
    Socket s;
    PrintWriter writer;
    String str;
    TextView t_time;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState  ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        b_upload = (Button) findViewById(R.id.b_upload);
        t_time = (TextView)findViewById(R.id.t_time);
        b_download = (Button)  findViewById(R.id.b_download);
        e_fusername = (EditText) findViewById(R.id.e_fusername) ;
        Intent intent = getIntent();

        str = intent.getStringExtra("username");


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        b_download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                download();

            }
        });

        b_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload();

            } });
    }

    public void upload(){
        String username = e_fusername.getText().toString();
        performFileSearch();
        Login_page.BackgroundTask2 b2 = new Login_page.BackgroundTask2();
        b2.execute(username);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String readText (String input){
        File file = new File(input);
        StringBuilder text =  new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                text.append(line);
                text.append("\n");
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }


        String s ="";
        String d1 ="";


        if(text.length()>65) {
            int b = (text.length() / 65) + 1;
            String divided;
            int c =65;
            int d = 0;

            while (b >= 1) {

                if (c>text.length()){
                    c=text.length();
                }

                divided = text.substring(d, c);
                byte[] enc = ntru.encrypt(divided.getBytes(), kp.getPublic());



                s =  Base64.getEncoder().encodeToString(enc);


                File root = new File(Environment.getExternalStorageDirectory()+ java.io.File.separator +str);
                if (!root.exists()){
                    root.mkdir();
                }
                File filepath = new File(root,str+ d +".txt");


                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
                    writer.write(s);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                b--;
                c = c+65;
                d = c-65;

            }




        }        else    {


            byte[] enc = ntru.encrypt(text.toString().getBytes(), kp.getPublic());
            System.out.println("Encrypted data");
            System.out.println(new String(enc));
            s = Base64.getEncoder().encodeToString(enc);
            File root = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + str);
            if (!root.exists()) {
                root.mkdir();
            }

            File filepath = new File(root, str + ".txt");
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
                writer.write(s);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Encrypted data after base 64 encoding");
            System.out.println(new String(s));

        }



        saveInFirebase();







        return text.toString();
    }

    public void saveInFirebase(){
        System.out.println(str);
        System.out.println(str);
        File root = new File(Environment.getExternalStorageDirectory()+ java.io.File.separator +str);
        File [] file2 = root.listFiles();
        StorageReference refrence = storageReference.child(str);
        for (File file1 : file2) {
            refrence.putFile(Uri.fromFile(file1));

        }
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void download(){

        String decrypted = "";

        File root = new File(Environment.getExternalStorageDirectory()+ java.io.File.separator +str);
        int length = root.listFiles().length;
        File [] file2 = root.listFiles();


        if (length>1){



            for (File file1 : file2) {
                StringBuilder text1 =  new StringBuilder();
                System.out.println("encrypted data readed by files using base 64 decoding");

                System.out.println(text1);

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file1));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text1.append(line);
                        text1.append("\n");
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] abc = Base64.getMimeDecoder().decode(text1.toString());
                byte[] dec = ntru.decrypt(abc, kp);
                decrypted = decrypted+ new String(dec);

            }



            System.out.println("Decrypted data");
            System.out.println(decrypted);
        }

        else{


            StringBuilder text1 =  new StringBuilder();
            File file3 = new File(root,str+".txt");

            try {
                BufferedReader br = new BufferedReader(new FileReader(file3));
                String line;
                while ((line = br.readLine()) != null) {
                    text1.append(line);
                    text1.append("\n");
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("encrypted data readed by files using base 64 decoding");
            System.out.println(text1);
            byte[] abc = Base64.getMimeDecoder().decode(text1.toString());
            byte[] dec = ntru.decrypt(abc, kp);
            decrypted = decrypted+ new String(dec);
            System.out.println("Decrypted data");
            System.out.println(new String(decrypted));



        }


    }

    private void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory((Intent.CATEGORY_OPENABLE));
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (data!= null){
                Uri uri = data.getData();
                String path = uri.getPath();
                path = path.substring(path.indexOf(":")+1);
                readText(path);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    class BackgroundTask2 extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try {

                String username = voids[0];
                int a = 3;

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