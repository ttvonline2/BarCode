package com.example.barcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class screen2 extends AppCompatActivity
{
    Button btScan;
    TextView tvItemCode;
    private String dataaa;
    TextView tvUser;
    LinearLayout layoutStep;
    TextView tvdata;
    Button btCompleted;
    int pos;
    boolean trangthai_complete = false;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2);
        //
        btScan  = findViewById(R.id.bt_scan);
        tvItemCode = findViewById(R.id.tv_itemcode);
        tvUser = findViewById(R.id.tv_user);
        layoutStep = findViewById(R.id.layout_step);
        tvdata = findViewById(R.id.tv_data);
        btCompleted = findViewById(R.id.bt_completed);
        tvUser.setText(MainActivity.Acc);
        pos = MainActivity.int_Pos;
        if(pos != -1)
        {
            layoutStep.setVisibility(View.VISIBLE);
            if(pos == 1)
            {
                btCompleted.setText("SHOP OUT");
            }
            else if(pos == 2)
            {
                btCompleted.setText("PAINTING");
            }
            else if(pos == 3)
            {
                btCompleted.setText("PACKING");
            }
            else if(pos == 4)
            {
                btCompleted.setText("ERECTION");
            }
        }
        //
        Thread thread  = new Thread(new ClientReceive());
        thread.start();

        btScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        btCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String temp = tvdata.getText().toString();
                if(temp.length() > 20)
                {
                    if(trangthai_complete)
                    {
                        String a =  MainActivity.Acc + "|" + tvItemCode.getText() + "|" + "0" + ">";
                        new Thread(new SendData(a)).start();
                    }
                    else
                    {
                        String a =  MainActivity.Acc + "|" + tvItemCode.getText() + "|" + "1" + ">";
                        new Thread(new SendData(a)).start();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You Should Input Items by Scan Button",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    class StartScan implements Runnable
    {

        @Override
        public void run()
        {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
        }
    }
    // client Receive
    class ClientReceive implements  Runnable
    {
        String line;
        @Override
        public void run()
        {
            while (MainActivity.socketClient.isConnected())
            {
                BufferedReader reader;
                try
                {
                    Log.d("vang", "Waiting ReadLine 2");
                    reader = new BufferedReader(new InputStreamReader(MainActivity.socketClient.getInputStream()));
                    line = reader.readLine();    // reads a line of text
                    if(line != null)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                char c = line.charAt(line.length()-1);
                                int b =  (int) c;
                                if(b == 33)   // "!"
                                {
                                    btCompleted.setBackgroundColor(Color.parseColor("#33FF00"));
                                    trangthai_complete = true;
                                }
                                else if(b == 60)
                                {
                                    tvItemCode.setText("");
                                    //Toast.makeText(getApplicationContext(),"Start scanner again", Toast.LENGTH_LONG).show();
                                    new Thread(new StartScan()).start();

                                }
                                else
                                {
                                    btCompleted.setBackgroundColor(Color.parseColor("#C0C0C0"));
                                    trangthai_complete = false;
                                }
                                line = line.replace("|","\r\n");
                                tvdata.setText(line);


                            }
                        });
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            Log.d("vang", "Server Disconnected");
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
        return;
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
                MainActivity.sendDos.writeUTF(message);
                MainActivity.sendDos.flush();
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
    // scanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            dataaa = result.getContents();
            tvItemCode = findViewById(R.id.tv_itemcode);
            if(result.getContents()==null)
            {
                Toast.makeText(this, "You Cancelled The Scanning", Toast.LENGTH_LONG).show();
            }
            else
            {
               // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                tvItemCode.setText(dataaa);
                String a = tvItemCode.getText().toString() +";";
                new Thread(new SendData(a)).start();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
