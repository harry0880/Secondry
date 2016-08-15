package com.secondry.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.secondry.GetterSetter.IMEI;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
   /* Barcode barcode;*/
    ArrayList<String> imei=new ArrayList<>();
    int cnt=IMEI.getCnt();
    boolean notexistBefore=true;


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


        String imeino=rawResult.getText();

            if(imeino.length()==15) {
                for(int i=0;i<imei.size();i++)
                {
                    if(imeino.equals(imei.get(i)))
                    {
                        notexistBefore=false;
                        break;
                    }
                }
                if(notexistBefore)
                {
                    if (cnt > 0) {

                        imei.add(imeino);
                        IMEI.setCnt(cnt--);
                    }
                    if (cnt != 0) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("Scanned " + rawResult.getText()).setContentText(cnt + " Scans left!!!")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
                                    }
                                }).show();
                    } else {
                        IMEI.setImei(imei);
                        getActivity().finish();
                    }
                }
                else
                {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText( rawResult.getText() +" is already Scanned!!!").setContentText("Please scan a different IMEI")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
                                }
                            }).show();
                }
            }
            else
            {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText(rawResult.getText()).setContentText("IMEI not correct!!!")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
                            }
                        }).show();
            }


    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
