package com.example.barcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class screen2 extends AppCompatActivity
{
    Button btScan;
    TextView tvItemCode;
    private String dataaa;
    TextView tvUser;
    Button btCheck;
    LinearLayout layoutStep;
    TextView tvdata;

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
        btCheck = findViewById(R.id.bt_check);
        tvdata = findViewById(R.id.tv_data);

        Thread th_update = new Thread(new Th_DataUpdate());
        th_update.start();


        final Activity activity = this;
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
        btCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layoutStep.setVisibility(View.VISIBLE);
                //SetDataByExtras();
                String a = Parameters.Para_DataReceive();
                tvdata.setText(a);
            }
        });

    }
    public void SetDataByExtras()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            //The key argument here must match that used in the other activity
            tvdata.setText(value);
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
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                tvItemCode.setText(dataaa);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //thread Update daata

    class Th_DataUpdate implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                if(Parameters.DataTransfer!= null)
                {
                    tvdata.setText(Parameters.DataTransfer);
                }

            }
        }
    }
}
