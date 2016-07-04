package dmcl.csmuse2016;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
    private ProgressDialog pDialog;
    private final String filename="account.txt";
    private boolean check=false;
    private String formConnect;
    private String userAccount = ""; //儲存使用者的帳號用於傳遞activity之間



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        /*按鈕偵測*/
        final Button sign_in_button = (Button)findViewById(R.id.sign_in_button);
        //暫時沒用到登入按鈕的偵測 因為我寫在layout onclick上 (要處理的事情較多)
        final Button guest_button = (Button)findViewById(R.id.guestlogin);
        final Button newAccount = (Button)findViewById(R.id.back);

        View.OnClickListener handler = new View.OnClickListener(){
            public void onClick(View v){
                if (v == guest_button){
                    Intent intentHomePage = new Intent(getApplicationContext(),
                            HomePageActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("mail","");
                    intentHomePage.putExtras(extras);
                    LoginActivity.this.startActivity(intentHomePage);
                    finish();

                }
                if (v == newAccount){
                    Intent intentNewAccount = new Intent(LoginActivity.this,
                                                        AccountLicence.class);
                    LoginActivity.this.startActivity(intentNewAccount);
                }
            }
        };

        guest_button.setOnClickListener(handler);
        newAccount.setOnClickListener(handler);

        /*設定輸入帳密時,提示的字會消失*/
        final EditText account = (EditText)findViewById(R.id.Account);
        final EditText password = (EditText)findViewById(R.id.password);
        account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                account.setHint("");
                password.setHint("密碼");
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                password.setHint("");
                account.setHint("帳號");
            }
        });

    }

    @Override //原本就有的source code
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetwork()
    {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
        {
            result = false;
        }
        else
        {
            if (!info.isAvailable())
            {
                result =false;
            }
            else
            {
                result = true;
            }
        }

        return result;
    }
    public void Login(View view) {
     if(isNetwork())//按下登入按鈕
        new loginbuttonclick().execute(); //給他一個執行緒(main thread)
     else{
         notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
         editNameDialog.show(getFragmentManager(), "EditNameDialog");
     }

    }
    //執行緒code
    class loginbuttonclick extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){ //執行緒開始前做的
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) { //進行中做的

            EditText EditTextAccount = (EditText)findViewById(R.id.Account);
            Editable AccountName;
            AccountName = EditTextAccount.getText();
            String Account =AccountName.toString();
            userAccount = Account; //儲存使用者的帳號用於傳遞activity之間

            EditText EditTextPassword = (EditText)findViewById(R.id.password);
            Editable PasswordName;
            PasswordName = EditTextPassword.getText();
            String Password =PasswordName.toString();

            //sending to php
            String command="select * from Account where Account ='"+Account+"' and Password = '"+Password+"'";
            formConnect = new connect(command).LoginCheck();
            String[] fromConnectToArray=formConnect.toString().split("###");
            if(fromConnectToArray.length>2){
            check =true;
                //Log.e("formconnect",formConnect);
            new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(formConnect);

            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) { //完成後
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss(); //waiting dismiss
            if(check)
            { //set page to homepage//


                Intent intentHomePage = new Intent(getApplicationContext(),
                        HomePageActivity.class);
                Bundle extras = new Bundle();
                extras.putString("mail", userAccount);
                intentHomePage.putExtras(extras);
                LoginActivity.this.startActivity(intentHomePage);
                finish();
            }else{    // login denied by entering wrong account/password
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Login denied!!")
                        .setMessage("The account or the password is wrong!!")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();
            }
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
}

