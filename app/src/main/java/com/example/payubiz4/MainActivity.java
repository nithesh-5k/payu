package com.example.payubiz4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.*;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.paymentparamhelper.PaymentPostParams;
import com.payu.paymentparamhelper.PostData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onBiometricKyc();
    }


    String key = "";
    String salt = "";

    void onBiometricKyc() {

        PaymentParams mPaymentParams = new PaymentParams();
        mPaymentParams.setKey(key);
        mPaymentParams.setAmount("100.0");
        mPaymentParams.setProductInfo("Gold Loan");
        mPaymentParams.setFirstName("Nithesh");
        mPaymentParams.setEmail("5nithesh011@gmail.com");
        mPaymentParams.setTxnId("1102324");
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        mPaymentParams.setCardNumber("5123456789012346");
        mPaymentParams.setCardName("test");
        mPaymentParams.setNameOnCard("test");
        mPaymentParams.setExpiryMonth("06");
        mPaymentParams.setExpiryYear("2023");
        mPaymentParams.setCvv("123");


        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");

        mPaymentParams.setBankCode("HDFCENCC");

        PayuConfig payuConfig = new PayuConfig();
        PostData postData = new PostData();

        PayUChecksum checksum;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();

        PayuHashes payuHashes = new PayuHashes();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        payuConfig.setEnvironment(2);

        mPaymentParams.setHash(payuHashes.getPaymentHash());
//        postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
        // for debit card
        // reference link - https://payumobile.gitbook.io/sdk-integration/android/pg-sdk/supported-payment-types


        postData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
        //for net banking


        Log.d("Result", "Code: " + postData.getCode());
        Log.d("Result", "Result: " + postData.getResult());
        Log.d("Result", "Status: " + postData.getStatus());

        payuConfig.setData(postData.getResult());
        Intent intent = new Intent(this, PaymentsActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        startActivityForResult(intent,PayuConstants.PAYU_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else {
                Toast.makeText(this, "could_not_receive_data", Toast.LENGTH_LONG).show();
            }
        }
    }
}