package com.nakayama.myself_ai;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import jp.ne.docomo.smt.dev.aitalk.AiTalkTextToSpeech;
import jp.ne.docomo.smt.dev.aitalk.data.AiTalkSsml;
import jp.ne.docomo.smt.dev.common.exception.SdkException;
import jp.ne.docomo.smt.dev.common.exception.ServerException;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;

/**
 * Created by nakayama on 2016/02/09.
 */
public class SummaryFragment extends Fragment{
    // 警報ダイアログ
    private android.app.AlertDialog.Builder dlg;
    // 音声文字列ＳＳＭＬ
    private AiTalkSsml ssml;
    // 非同期タスク
    private AitestAsyncTask task;
    // ＡＰＩキー
    static final String APIKEY = "63665674784f766a76526c305632536c5778425a7651577644764b3971665242775a523853324e65573738";

    private TextView mSummaryText;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstancedState){
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        String result = getArguments().getString("result");

        startReading(result);
        //String url = "http://ec2-52-36-76-196.us-west-2.compute.amazonaws.com/cgi-bin/text.py?text=python";

        mSummaryText = (TextView)rootView.findViewById(R.id.summary_text);
        new ConnectTask(result,ContentActivity.SUMMARY).execute();

        Log.d("ResultData-summary-onc", result);

        getActivity().finish();

        return rootView;
    }

    private void setSummary(String text){
        mSummaryText.setText(text);
    }

    public class ConnectTask extends AsyncTask<Void,Void,Void> {
        private String mUrl;
        private String mType;
        private DialogFragment mDialogFragment;
        private String mResult = "";


        public ConnectTask(String url,String type){
            mUrl = url;
            mType = type;
            //mDialogFragment = new MyDialogFragment();
            //mDialogFragment.show(getActivity().getFragmentManager(),"dialog");
        }




        @Override
        protected void onPreExecute(){
//            mDialogFragment = new MyDialogFragment();
//            mDialogFragment.show(getActivity().getFragmentManager(),"dialog");
        }



        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            HttpURLConnection httpURLConnection = null;
            try{
                url = new URL(mUrl);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setRequestProperty("text", "python");
//                String parameterString = new String("text=あなたの研究を教えて下さい");
//                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//                printWriter.print(parameterString);
//                printWriter.close();
                httpURLConnection.connect();
                Log.d("resultdata-summary", httpURLConnection.getURL().toString());
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

            //mSummaryText.setText(mResult);
            startReading(mResult);
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

    public void startReading(String text){
        // API キーの登録
        AuthApiKey.initializeAuth(MainActivity.APIKEY);
//        dlg = new android.app.AlertDialog.Builder(getActivity());

        try {
            ssml = new AiTalkSsml();
            ssml.startVoice("nozomi");
            ssml.addText(text);
            ssml.endVoice();
        } catch (Exception ex){
        }
        // 音声変換を実行し、音声を出力する。
        task = new AitestAsyncTask (dlg,AitestAsyncTask.henkan_ssml_sound);
        task.execute(ssml);


    }

    private class AitestAsyncTask extends AsyncTask<Object, Integer, byte[]> {
        // 警報ダイアログ
        private android.app.AlertDialog.Builder _dlg;
        // 変換タイプ
        private int _henkan;
        public static final int henkan_ssml_sound = 1;
        public static final int henkan_ssml_aikana = 2;
        public static final int henkan_ssml_jeitakana = 3;
        public static final int henkan_aikana_sound = 11;
        public static final int henkan_aikana_jeitakana = 13;
        public static final int henkan_jeitakana_sound = 21;
        // エラーフラグ
        private boolean isSdkException = false;
        private String exceptionMessage = null;

        // 非同期タスクのコンストラクタ
        public AitestAsyncTask(android.app.AlertDialog.Builder dlg, int henkan) {
            super();
            _dlg = dlg;
            _henkan = henkan;
        }

        // 非同期タスクのバックグラウンド実行部分
        @Override
        protected byte[] doInBackground(Object... params) {
            byte[] resultData = null;
            try {
                // 要求処理クラスを作成
                AiTalkTextToSpeech search = new AiTalkTextToSpeech();
                // 要求処理クラスにリクエストデータを渡し、レスポンスデータを取得する
                switch (_henkan) {
                    case henkan_ssml_sound:
                        resultData = search.requestAiTalkSsmlToSound(((AiTalkSsml) params[0]).makeSsml());
                        break;
                    case henkan_ssml_aikana:
                        resultData = search.requestAiTalkSsmlToAikana(((AiTalkSsml) params[0]).makeSsml()).getBytes();
                        break;
                    case henkan_ssml_jeitakana:
                        resultData = search.requestAiTalkSsmlToJeitakana(((AiTalkSsml) params[0]).makeSsml());
                        break;
                    case henkan_aikana_sound:
                        resultData = search.requestAikanaToSound((String) params[0]);
                        break;
                    case henkan_aikana_jeitakana:
                        resultData = search.requestAikanaToJeitakana((String) params[0]);
                        break;
                    case henkan_jeitakana_sound:
                        resultData = search.requestJeitakanaToSound(((String) params[0]).getBytes("Shift_Jis"));
                        break;
                    default:
                        return null;
                }
                // 音声変換の場合は、スピーカに出力
                switch (_henkan) {
                    case henkan_ssml_sound:
                    case henkan_aikana_sound:
                    case henkan_jeitakana_sound:
                        // 音声出力用バッファ作成
                        int bufSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                        // ビッグエディアンをリトルエディアンに変換
                        search.convertByteOrder16(resultData);
                        // 音声出力
                        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
                        at.play();
                        at.write(resultData, 0, resultData.length);
                        // 音声出力待ち
                        Thread.sleep(resultData.length / 32);
                        break;
                }
            } catch (SdkException ex) {
                isSdkException = true;
                exceptionMessage = "ErrorCode: " + ex.getErrorCode() + "\nMessage: " + ex.getMessage();
            } catch (ServerException ex) {
                exceptionMessage = "ErrorCode: " + ex.getErrorCode() + "\nMessage: " + ex.getMessage();
            } catch (Exception ex) {
                exceptionMessage = "ErrorCode: " + "**********" + "\nMessage: " + ex.getMessage();
            }
            return resultData;
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(byte[] resultData) {
            if (resultData == null) {
                // エラー表示
                if (isSdkException) {
                    _dlg.setTitle("SdkException 発生");

                } else {
                    _dlg.setTitle("ServerException 発生");
                }
                _dlg.setMessage(exceptionMessage + " ");
                _dlg.show();


            }

        }


    }


}
