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
            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("Scanned " + rawResult.getText()).setContentText(cnt + " Scans left!!!")
                    .setConfirmText("Ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
                        }
                    }).show();
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
