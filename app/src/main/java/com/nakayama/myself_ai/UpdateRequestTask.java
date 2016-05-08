package com.nakayama.myself_ai;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

/**
 * Created by nakayama on 2016/02/10.
 */
public class UpdateRequestTask extends AsyncTask<Void,Void,Void> {

    private String mUrl;

    public UpdateRequestTask(String userId){
        mUrl = "http://ec2-52-36-76-196.us-west-2.compute.amazonaws.com/cgi-bin/update.py?text="+userId;
    }


    @Override
    protected Void doInBackground(Void... params) {

        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(mUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            Log.d("ResultData-menu-doin", httpURLConnection.getURL().toString());

            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            Log.d("ResultData-menu-doin",readIt(in));


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
}
