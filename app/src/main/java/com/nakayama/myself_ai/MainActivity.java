package com.nakayama.myself_ai;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import jp.ne.docomo.smt.dev.aitalk.AiTalkTextToSpeech;
import jp.ne.docomo.smt.dev.aitalk.data.AiTalkSsml;
import jp.ne.docomo.smt.dev.common.exception.SdkException;
import jp.ne.docomo.smt.dev.common.exception.ServerException;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;

/**
 * @author OpenApi
 *
 */
public class MainActivity extends Activity {
    // 画面コントローラ
    private EditText ed1;
    // 警報ダイアログ
    private AlertDialog.Builder dlg;
    // 音声文字列ＳＳＭＬ
    private AiTalkSsml ssml;
    // 非同期タスク
    private AitestAsyncTask task;
    // ＡＰＩキー
    static final String APIKEY = "63665674784f766a76526c305632536c5778425a7651577644764b3971665242775a523853324e65573738";
    // 非同期タスクのクラス
    private class AitestAsyncTask extends AsyncTask<Object, Integer, byte[]> {
        // 警報ダイアログ
        private AlertDialog.Builder _dlg;
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
        public AitestAsyncTask(AlertDialog.Builder dlg, int henkan) {
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
                switch (_henkan){
                    case henkan_ssml_sound:
                        resultData = search.requestAiTalkSsmlToSound(((AiTalkSsml)params[0]).makeSsml());
                        break;
                    case henkan_ssml_aikana:
                        resultData = search.requestAiTalkSsmlToAikana(((AiTalkSsml)params[0]).makeSsml()).getBytes();
                        break;
                    case henkan_ssml_jeitakana:
                        resultData = search.requestAiTalkSsmlToJeitakana(((AiTalkSsml)params[0]).makeSsml());
                        break;
                    case henkan_aikana_sound:
                        resultData = search.requestAikanaToSound((String)params[0]);
                        break;
                    case henkan_aikana_jeitakana:
                        resultData = search.requestAikanaToJeitakana((String)params[0]);
                        break;
                    case henkan_jeitakana_sound:
                        resultData = search.requestJeitakanaToSound(((String)params[0]).getBytes("Shift_Jis"));
                        break;
                    default:
                        return null;
                }
                // 音声変換の場合は、スピーカに出力
                switch (_henkan){
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
                        Thread.sleep(resultData.length/32);
                        break;
                }
            } catch (SdkException ex) {
                isSdkException = true;
                exceptionMessage = "ErrorCode: " + ex.getErrorCode() + "\nMessage: " + ex.getMessage();
            } catch (ServerException ex) {
                exceptionMessage = "ErrorCode: " + ex.getErrorCode() + "\nMessage: " + ex.getMessage();
            } catch (Exception ex){
                exceptionMessage = "ErrorCode: " + "**********" + "\nMessage: " + ex.getMessage();
            }
            return resultData;
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(byte[] resultData) {
            if(resultData == null){
                // エラー表示
                if(isSdkException){
                    _dlg.setTitle("SdkException 発生");

                }else{
                    _dlg.setTitle("ServerException 発生");
                }
                _dlg.setMessage(exceptionMessage + " ");
                _dlg.show();

            }else{
                // 結果表示
                switch (_henkan){
                    case henkan_ssml_aikana:
                        // ＡＩカナ結果の表示
                        ed1.setText("");
                        break;
                    case henkan_ssml_jeitakana:
                    case henkan_aikana_jeitakana:
                        // Ｊｅｉｔａカナ結果の表示
                        try {
                            ed1.setText("");
                        } catch (Exception ex){
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // API キーの登録
        AuthApiKey.initializeAuth(MainActivity.APIKEY);
        // コントローラ取得
        ed1 = (EditText) findViewById(R.id.editText1);
        // クリック時の動作設定
        OnClickListener onclicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 警報用ダイアログ作成
                dlg = new AlertDialog.Builder(MainActivity.this);
                // ボタンによって動作を変える
                switch(v.getId()){
                    case R.id.button1:
                        // のぞみさんの音声で、エディットボックスの文字列を、ＳＳＭＬクラスに登録する
                        try {
                            ssml = new AiTalkSsml();
                            ssml.startVoice("nozomi");
                            ssml.addText(ed1.getText().toString());
                            ssml.endVoice();
                        } catch (Exception ex){
                        }
                        // 音声変換を実行し、音声を出力する。
                        task = new AitestAsyncTask (dlg,AitestAsyncTask.henkan_ssml_sound);
                        task.execute(ssml);
                        break;
                    case R.id.button2:
                        Intent intent = new Intent(MainActivity.this,ListenerActivity.class);
                        startActivity(intent);

                }
            }
        };
        // ボタンにクリック動作の設定
        findViewById(R.id.button1).setOnClickListener(onclicklistener);
        findViewById(R.id.button2).setOnClickListener(onclicklistener);

    }
}
