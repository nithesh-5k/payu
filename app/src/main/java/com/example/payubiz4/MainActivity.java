package com.example.payubiz4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Interfaces.GetStoredCardApiListener;
import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Interfaces.SaveCardApiListener;
import com.payu.india.Model.*;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;
import com.payu.india.Tasks.GetStoredCardTask;
import com.payu.india.Tasks.SaveCardTask;
import com.payu.paymentparamhelper.PaymentPostParams;
import com.payu.paymentparamhelper.PostData;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onBiometricKyc();
    }

    String key = "PTGDR2";
    String transactionId = "1153878453334";
    String amount = "100.0";
    String productInfo = "Gold Loan";
    String name = "Nithesh";
    String email = "nithesh@gmail.com";
    String userCredentials = key + ":" + email;
    String cardNumber = "5126520180876620";
    String nameOnCard = "test";
    String cardMonth = "02";
    String cardYear = "2027";
    String cardCvv = "509";
    String bankCode = "HDFB";

    void onBiometricKyc() {
        PaymentParams mPaymentParams = new PaymentParams();
        mPaymentParams.setKey(key);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo(productInfo);
        mPaymentParams.setFirstName(name);
        mPaymentParams.setEmail(email);
        mPaymentParams.setUserCredentials(userCredentials);
        mPaymentParams.setTxnId(transactionId);
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");


        mPaymentParams.setHash(calculateHash(key + "|" + transactionId + "|" + amount + "|" + productInfo + "|" + name + "|" + email + "|||||||||||"));

        PayuConfig payuConfig = new PayuConfig();
        payuConfig.setEnvironment(PayuConstants.STAGING_ENV);

//        mPaymentParams.setCardNumber(cardNumber);
//        mPaymentParams.setNameOnCard(nameOnCard);
//        mPaymentParams.setExpiryMonth(cardMonth);
//        mPaymentParams.setExpiryYear(cardYear);
//        mPaymentParams.setCvv(cardCvv);

//        PostData postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
//        for debit card
//        reference link - https://payumobile.gitbook.io/sdk-integration/android/pg-sdk/supported-payment-types

        //TODO couldn't access the bank servers by directly passing the bank code
        mPaymentParams.setBankCode(bankCode);
        PostData postData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
//        for net banking


        payuConfig.setData(postData.getResult());



        //TODO trying to save and retrieve card data - not working
//
//        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
//        PayuHashes mPayUHashes = new PayuHashes();
//        mPayUHashes.setSaveCardHash(calculateHash(key + "|" + PayuConstants.SAVE_USER_CARD + "|" + var1));
//        mPayUHashes.setStoredCardsHash(calculateHash(key + "|" + PayuConstants.GET_USER_CARDS + "|" + var1));
//        mPayUHashes.setStoredCardsHash(calculateHash(key + "|" + PayuConstants.SAVE_USER_CARD + "|" + var1));
//        String  a = mPayUHashes.getStoredCardsHash();
//        Log.d("Card",a);
//        mPayUHashes.setStoredCardsHash(calculateHash(key + "|" + PayuConstants.DELETE_USER_CARD + "|" + var1));


//        MerchantWebService merchantWebService = new MerchantWebService();
//        merchantWebService.setKey(mPaymentParams.getKey());
//        merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
//        merchantWebService.setVar1(mPaymentParams.getUserCredentials() == null ? "default" : mPaymentParams.getUserCredentials());
//
//        if (mPaymentParams.getSubventionEligibility() != null && !mPaymentParams.getSubventionEligibility().isEmpty())
//            merchantWebService.setVar2(mPaymentParams.getSubventionEligibility());
//        merchantWebService.setHash(mPayUHashes.getSaveCardHash());
//
//
//        PostData tpostData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
//        Log.d("tResult", "Code: " + tpostData.getCode());
//        Log.d("tResult", "Result: " + tpostData.getResult());
//        Log.d("tResult", "Status: " + tpostData.getStatus());
//        if (tpostData.getCode() == PayuErrors.NO_ERROR) {
//            payuConfig.setData(tpostData.getResult());
//
//
//            GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(this);
//            paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
//        } else {
//            Toast.makeText(this, tpostData.getResult(), Toast.LENGTH_LONG).show();
//        }

//        SaveCardTask payuTask = new SaveCardTask( this);
//        payuTask.execute(payuConfig);

//        GetStoredCardTask payuTask = new GetStoredCardTask(this);
//        payuTask.execute(payuConfig);
//        Log.d("Response",payuTask.getStatus().toString());
        //TODO end

        Intent intent = new Intent(this, PaymentsActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
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

    private String calculateHash(String input) {
        String salt = "2KVAqbrt";
        input = input + salt;
        try {
            StringBuilder hash = new StringBuilder();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(input.getBytes());
            byte[] mdbytes = messageDigest.digest();
            byte[] var5 = mdbytes;
            int var6 = mdbytes.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                byte hashByte = var5[var7];
                hash.append(Integer.toString((hashByte & 255) + 256, 16).substring(1));
            }
            Log.d("Hash", hash.toString());

            return hash.toString();
        } catch (NoSuchAlgorithmException var9) {
            return " Message digest sha 512 not found!";
        }
    }

}

