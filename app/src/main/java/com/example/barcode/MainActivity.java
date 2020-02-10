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
    String Acc;
    final public static String strDataReceive = "strStatus";
    Thread ThreadToServer = null;
    EditText etAccount, etPassword;
    TextView tvStatus;
    Button btLogin;
    String SERVER_IP = "10.12.20.77";
    int SERVER_PORT = 100;
    boolean AccessScreen2 = false;
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

        // Start loop for next Screen screen 2;
        Thread Screen2 = new Thread(new Screen2());
        Screen2.start();

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
                        String Acc_Pass = Acc + "|" +  Pass;
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

        //Intent intent = new Intent(MainActivity.this, screen2.class);
        //startActivity(intent);
    }

    // Class Thread Connect to server
    private DataOutputStream sendDos;
    public Socket socketClient;
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
        @Override
        public void run()
        {
            while (socketClient.isConnected())
            {
                BufferedReader reader;
                try
                {
                    Log.d("vang", "Waiting ReadLine");
                    reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                    line = reader.readLine();    // reads a line of text
                    if(line != null)
                    {
                        Parameters.Para_DataSend(line);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                tvStatus.setText(line);
                                char c = line.charAt(line.length()-1);
                                int b =  (int) c;
                                if(b == 63)   // "?"
                                {
                                    Intent in = new Intent(getBaseContext(), screen2.class);
                                    in.putExtra("vang",line);
                                    startActivity(in);
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

    // Screen 2
    class Screen2 implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                if(AccessScreen2)
                {

                }
            }
        }
    }

}
