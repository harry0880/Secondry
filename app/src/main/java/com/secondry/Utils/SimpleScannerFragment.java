package com.secondry.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;
import com.secondry.GetterSetter.IMEI;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
   /* Barcode barcode;*/
    ArrayList<String> imei=new ArrayList<>();
    int cnt=IMEI.getCnt();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mScannerView = new ZXingScannerView(getActivity());
        /*barcode=new Barcode();*/
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

if(cnt>0) {
    imei.add(rawResult.getText());
    IMEI.setCnt(cnt--);
}
       if(cnt!=0) {
           Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
               }
           }, 500);
       }
        else {
           IMEI.setImei(imei);
           getActivity().finish();
       }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


}
