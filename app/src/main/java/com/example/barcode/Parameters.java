package com.example.barcode;


public class Parameters
{
    public static String DataTransfer;
    public static void Para_DataSend (String Data)
    {
        DataTransfer = Data;
    }
    public static String Para_DataReceive()
    {
        return DataTransfer;
    }
}
