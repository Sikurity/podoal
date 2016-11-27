package com.android.podoal.project_podoal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by hp on 2016-01-26.
 */
public class LoginActivity  extends Activity {

    private SessionCallback callback;      //콜백 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add code to print out the key hash
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("MY KEY HASH:" + Base64.encodeBytes(md.digest(), Base64.NO_OPTIONS));
            }
        } catch (Exception e) {
            System.out.println("!!");
        }*/

        if( NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED )
        {
            final ProgressDialog dialog = ProgressDialog.show(this, "네트워크 연결 없음", "네트워크 연결 후 재실행해 주세요", true);

            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    dialog.dismiss();
                    System.exit(0);
                }
            }.start();
        }
        else
        {
            callback = new SessionCallback();
            new CheckURLConnection(this, callback).execute("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login); // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }

    protected void redirectSignupActivity() {
        //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}

class CheckURLConnection extends AsyncTask<String, Void, Boolean>
{
    private Activity mainActivity;
    private ISessionCallback sessionCallback;

    public CheckURLConnection(Activity currentActivity, ISessionCallback sessionCallback)
    {
        this.mainActivity = currentActivity;
        this.sessionCallback = sessionCallback;
    }

    protected void onPreExecute()
    {
        //display progress dialog.
    }

    protected Boolean doInBackground(String... urls)
    {
        try
        {
            URL url = new URL(urls[0]);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(3000);
            con.connect();
            return new Boolean(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new Boolean(false);
        }
    }

    protected void onPostExecute(Boolean result)
    {
        if( result )
        {
            mainActivity.setContentView(R.layout.activity_login);

            Session.getCurrentSession().addCallback(sessionCallback);
            Session.getCurrentSession().checkAndImplicitOpen();
        }
        else
        {
            final ProgressDialog dialog = ProgressDialog.show(mainActivity, "서버("+"http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + ") 닫힘", "010-7143-7047로 연락주세요", true);

            new CountDownTimer(10000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    dialog.dismiss();
                    System.exit(0);
                }
            }.start();
        }
        // dismiss progress dialog and update ui
    }
}