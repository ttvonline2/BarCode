package com.example.barcode;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity
{
    public static String Acc;
    public static String SERVER_IP = "10.12.20.77";
    public static int SERVER_PORT = 100;
    // Class Thread Connect to server
    public static DataOutputStream sendDos;
    public static Socket socketClient;
    public  static boolean blNextScreen = false;
    public  static int int_Pos = -1;

    Thread ThreadToServer = null;
    EditText etAccount, etPassword;
    public static TextView tvStatus;
    Button btLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_login);
        tvStatus = findViewById(R.id.tv_status);
        // Start Server
        ThreadToServer = new Thread(new ConnectServer());
        ThreadToServer.start();


        // Button Login Click
        btLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String Pass = etPassword.getText().toString().trim();
                Acc = etAccount.getText().toString().trim();


                if(Pass.isEmpty() || Acc.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Input Account & Password", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(socketClient != null)
                    {
                        String Acc_Pass = Acc + "|" +  Pass +"'";
                        new Thread(new SendData(Acc_Pass)).start();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Server is Offline.", Toast.LENGTH_LONG).show();
                        tvStatus.setText("Contract to IT for Opening Server");
                    }
                }


            }
        });
    }
    public void btclick (View view)
    {
        String value="Hello world";
        Intent i = new Intent(MainActivity.this, screen2.class);
        i.putExtra("key",value);
        startActivity(i);
    }

    // Class Thread Connect to server
    class ConnectServer implements Runnable {
        public void run()
        {
            try
            {
                socketClient = new Socket(SERVER_IP, SERVER_PORT);
                Log.d("vang", "Client connected to server");
                sendDos = new DataOutputStream(socketClient.getOutputStream());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tvStatus.setText("Connected\n");
                    }
                });
                //Start Receive
                Thread ClientReceive = new Thread(new ClientReceive());
                ClientReceive.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // client Receive
    class ClientReceive implements  Runnable
    {
        String line;
        boolean allow = true;
        @Override
        public void run()
        {
            while (socketClient.isConnected() & allow)
            {
                final BufferedReader reader;
                try
                {
                    Log.d("vang", "Waiting ReadLine1");
                    reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                    line = reader.readLine();    // reads a line of text

                    if(line != null)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                tvStatus.setText(line);
                                char c = line.charAt(line.length()-1);
                                int b =  (int) c;
                                Log.d("vang", line);
                                if(b == 63)   // "?"
                                {
                                    allow = false;
                                    blNextScreen = true;
                                    Intent in = new Intent(getBaseContext(), screen2.class);
                                    startActivity(in);
                                }
                                else if(b == 33)
                                {
                                    Toast.makeText(getApplicationContext(),
                                            "Your Account or Password is incorrect",
                                            Toast.LENGTH_LONG).show();
                                }
                                else if(b == 49)
                                {
                                    int_Pos = 1;
                                }
                                else if(b == 50)
                                {
                                    int_Pos = 2;
                                }
                                else if(b == 51)
                                {
                                    int_Pos = 3;
                                }
                                else if(b == 52)
                                {
                                    int_Pos = 4;
                                }

                            }
                        });
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    // Send Data
    class SendData implements Runnable
    {
        private String message;
        SendData(String message)
        {
            this.message = message;
        }
        @Override
        public void run()
        {
            try
            {
                sendDos.writeUTF(message);
                sendDos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    Log.d("vang", message);
                }
            });
        }
    }

}
