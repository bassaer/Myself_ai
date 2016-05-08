package com.nakayama.myself_ai;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by nakayama on 2016/02/09.
 */
public class PhotoFragment extends Fragment{

    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstance){
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        mImageView = (ImageView)rootView.findViewById(R.id.image_view);

        String url_text = getArguments().getString("result");
        //String url_text = "http://ec2-52-36-76-196.us-west-2.compute.amazonaws.com/cgi-bin/image.py?text=アルバイトについて教えて下さい";


        new ConnectTask(url_text,ContentActivity.PHOTO).execute();

        return rootView;

    }

    public class ConnectTask extends AsyncTask<Void,Void,Void> {
        private String mUrl;
        private String mType;
        private DialogFragment mDialogFragment;
        private String mResult = "";
        private Bitmap oBmp;


        public ConnectTask(String url,String type){
            mUrl = url;
            mType = type;
//            mDialogFragment = new MyDialogFragment();
//            mDialogFragment.show(getActivity().getFragmentManager(),"dialog");
        }




        @Override
        protected void onPreExecute(){
//            mDialogFragment = new MyDialogFragment();
//            mDialogFragment.show(getActivity().getFragmentManager(),"dialog");
        }



        @Override
        protected Void doInBackground(Void... params) {

            //String url_text = "https://pbs.twimg.com/media/CavWzHUVAAAWuw1.jpg";
            URL url;
            Log.d("resultdata-photo",mUrl);
            InputStream istream;
            try {
                //画像のURLを直うち
                url = new URL(mUrl);
                //インプットストリームで画像を読み込む
                istream = url.openStream();
                //読み込んだファイルをビットマップに変換
                oBmp = BitmapFactory.decodeStream(istream);
                //ビットマップをImageViewに設定

                //インプットストリームを閉じる
                istream.close();
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }



            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
           //mDialogFragment.dismiss();
            mImageView.setImageBitmap(oBmp);
        }




    }

    public static class MyDialogFragment extends DialogFragment {
        private ProgressDialog mProgressDialog;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading..");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            return mProgressDialog;
        }

        // progressDialog取得
        @Override
        public Dialog getDialog(){
            return mProgressDialog;
        }

        // ProgressDialog破棄
        @Override
        public void onDestroy(){
            super.onDestroy();
            mProgressDialog = null;
        }

    }

}
