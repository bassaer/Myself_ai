package com.nakayama.myself_ai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.MalformedInputException;


/**
 * Created by nakayama on 2016/02/09.
 */
public class MenuActivity extends Activity implements PushListenerInterface{

    /** 音声認識アクティビティのリクエストID */
    private static final int RECOGNIZE_ACTIVITY_REQUEST_ID = 1;
    private String mResultString = "";
    static private String API_KEY = "63665674784f766a76526c305632536c5778425a7651577644764b3971665242775a523853324e65573738";
    public static String currentType= "";

    private PushNotify mPushNotify = null;



    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstancedState){
        super.onCreate(savedInstancedState);
        setContentView(R.layout.activity_menu);
        Button summaryButton = (Button)findViewById(R.id.summary_button);
        Button photoButton = (Button)findViewById(R.id.photo_button);
        Button videoButton = (Button)findViewById(R.id.video_button);

        mPushNotify = new PushNotify();
        mPushNotify.setListener(this);

        summaryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening(ContentActivity.SUMMARY);
            }
        });

        photoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening(ContentActivity.PHOTO);
            }
        });

        videoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening(ContentActivity.VIDEO);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECOGNIZE_ACTIVITY_REQUEST_ID:
                mResultString = data.getStringExtra((resultCode == RESULT_OK) ? "replace_key" : "error_message");
                String type = data.getStringExtra(ContentActivity.CONTENTS_TYPE);
                Log.d("ResultData-mresul", mResultString);
                if(!mResultString.equals("キャンセルされました。")) {
                    connectServer(mResultString, currentType);
                    Log.d("ResultData-menu", mResultString);
                }
                Log.d("ResultData-test",mResultString);
                break;
        }
    }


    public void connectServer(String input,String type){
        URI url = null;
        try{
            url = new URI("http://ec2-52-36-76-196.us-west-2.compute.amazonaws.com/cgi-bin/"+type+".py?text="+input);
        }catch(URISyntaxException e){

        }
//        Log.d("resultData-http4",url.toString());
        new ConnectTask(url.toString(),type).execute();

    }



    public class ConnectTask extends AsyncTask<Void,Void,Void>{
        private String mUrl;
        private String mType;
        private DialogFragment mDialogFragment;
        private String mResult = "";


        public ConnectTask(String url,String type){
            mUrl = url;
            mType = type;
//            mDialogFragment = new MyDialogFragment();
//            mDialogFragment.show(getFragmentManager(),"dialog");
            mResult = url;
        }




        @Override
        protected void onPreExecute(){
//            mDialogFragment = new MyDialogFragment();
//            mDialogFragment.show(getFragmentManager(),"dialog");
        }



        @Override
        protected Void doInBackground(Void... params) {

            //String defoult_url = "http://ec2-52-36-76-196.us-west-2.compute.amazonaws.com/cgi-bin/"+currentType+".py?text="+mResult;
            HttpURLConnection httpURLConnection = null;
            try{
                URL url = new URL(mUrl);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                //httpURLConnection.setRequestProperty("text", "python");
//                String parameterString = new String("text="+mResultString);
//                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//                Log.d("ResultData",printWriter.toString());
//                printWriter.print(parameterString);
//                printWriter.close();

                Log.d("ResultData-menu-doin",httpURLConnection.getURL().toString());

                httpURLConnection.connect();

                InputStream in = httpURLConnection.getInputStream();
                mResult = readIt(in);

            }catch(MalformedInputException e){

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return null;
        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            StringBuffer sb = new StringBuffer();
            String line = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            try {
                stream.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(Void result) {
            //mDialogFragment.dismiss();
            Intent intent = new Intent(MenuActivity.this,ContentActivity.class);
            intent.putExtra(ContentActivity.CONTENTS_TYPE,currentType);
            intent.putExtra("result",mResult);
            startActivity(intent);
        }




    }

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Loading..");
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }




    @Override
    public void informMessage(String message) {
        if(message.equals(ContentActivity.SUMMARY) || message.equals(ContentActivity.PHOTO) || message.equals(ContentActivity.VIDEO)){
            startListening(message);
        }
    }


    public void startListening(String type) {
        currentType = type;
        int sbm_mode = 0;//0：雑音、１静か
        Intent intent = new Intent(this, RecognitionActivity.class);
        intent.putExtra(RecognitionActivity.KEY_SBM_MODE, sbm_mode);
        intent.putExtra(ContentActivity.CONTENTS_TYPE,type);
        // 別途発行されるAPIキーを設定してください(以下の値はダミーです)
        intent.putExtra(RecognitionActivity.KEY_API_KEY, API_KEY);
        startActivityForResult(intent, RECOGNIZE_ACTIVITY_REQUEST_ID);
    }







}
