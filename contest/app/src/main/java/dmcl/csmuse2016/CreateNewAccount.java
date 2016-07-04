package dmcl.csmuse2016;

import android.app.Activity;

import java.util.ArrayList;
import org.apache.http.util.EntityUtils;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;


public class CreateNewAccount extends Activity {

    int flag1 = 0;
    int flagForConfirmData = 0;
    int sex = 1; //性別
    int yyyy = 1911; //年
    int MM = 1; //月
    int dd = 1; //日
    int hh = 0; //時辰
    int mailCheck = 0; //電子報
    private ProgressDialog pDialog;
    InputStream is=null;
    private RadioButton Man;
    private RadioButton Woman;
    private RadioGroup rgroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_account);
        flagForConfirmData = 0; // reset
        //性別
        Man = (RadioButton)findViewById(R.id.man);
        Woman = (RadioButton)findViewById(R.id.woman);
        rgroup = (RadioGroup)findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(listener);
        //生日
        NumberPicker year = (NumberPicker)findViewById(R.id.yearPick);
        NumberPicker month = (NumberPicker)findViewById(R.id.monthPick);
        final NumberPicker day = (NumberPicker)findViewById(R.id.dayPick);
        NumberPicker time = (NumberPicker)findViewById(R.id.timePick);
        time.setMaxValue(23);
        time.setMinValue(0);
        time.setDisplayedValues(new String[]{
                "00:00~00:59(子時)", "1:00~1:59(丑時)", "2:00~2:59(丑時)", "3:00~3:59(寅時)",
                "4:00~4:59(寅時)", "5:00~5:59(卯時)", "6:00~6:59(卯時)", "7:00~7:59(辰時)",
                "8:00~8:59(辰時)", "9:00~9:59(巳時)", "10:00~10:59(巳時)", "11:00~11:59(午時)",
                "12:00~12:59(午時)", "13:00~13:59(未時)", "14:00~14:59(未時)", "15:00~15:59(申時)",
                "16:00~16:59(申時)", "17:00~17:59(酉時)", "18:00~18:59(酉時)", "19:00~19:59(戌時)",
                "20:00~20:59(戌時)", "21:00~21:59(亥時)", "22:00~22:59(亥時)", "23:00~23:59(子時)",
        });
        time.setWrapSelectorWheel(true);
        year.setMaxValue(2016);
        year.setMinValue(1911);
        year.setWrapSelectorWheel(true);
        month.setMaxValue(12);
        month.setMinValue(1);
        month.setWrapSelectorWheel(true);


        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                yyyy = newVal;
                if (MM == 2){
                    if(yyyy % 4 == 0){ //算閏年 但並不完整
                        day.setMaxValue(29);
                        day.setMinValue(1);
                    }
                    else {
                        day.setMaxValue(28);
                        day.setMinValue(1);
                    }
                }
                flagForConfirmData = 1;
                //tv.setText("Selected year : " + yyyy); //創一個textview id="tv" 即可測試
            }
        });
        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
          @Override
          public void onValueChange(NumberPicker picker,int oldVal, int newVal){
              MM = newVal;
              day.setWrapSelectorWheel(true);
              if(MM == 2 ){
                  if(yyyy % 4 == 0){ //算閏年 但並不完整
                      day.setMaxValue(29);
                      day.setMinValue(1);
                  }
                  else {
                      day.setMaxValue(28);
                      day.setMinValue(1);
                  }
              }
              else if(MM == 4 || MM == 6 || MM == 9 || MM == 11){
                  day.setMaxValue(30);
                  day.setMinValue(1);
              }
              else{
                  day.setMaxValue(31);
                  day.setMinValue(1);
              }
              //tv.setText("Selected month : " + MM);
          }
        });
        day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                dd = newVal;

                //tv.setText("Selected day : " + dd);
            }
        });
        time.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hh = newVal;
                //tv.setText("Selected hour : " + hh);
            }
        });

    }

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.man:
                    sex = 1;
                    Log.v("sex","man");
                    break;
                case R.id.woman:
                    sex = 0;
                    Log.v("sex","woman");
                    break;
            }

        }
    };
    public void newsBoxChecked(View v){ //同意接收電子報
        CheckBox ifchecked = (CheckBox)findViewById(R.id.newsCheckBox);
        if (ifchecked.isChecked()==true) {
            mailCheck = 1;
        }
        else{
            mailCheck = 0;
        }
    }
    //抓edittext上使用者輸入的字

    public void applyNewAccount(View v){

        if(isNetwork()) {
            EditText EditTextname = (EditText) findViewById(R.id.newNameText);
            Editable nameName;
            nameName = EditTextname.getText();
            String username = nameName.toString(); //姓

            EditText EditTextSubName = (EditText) findViewById(R.id.newsubNameText);
            Editable subnameName;
            subnameName = EditTextSubName.getText();
            String userSubname = subnameName.toString(); //名

            EditText EditTextaccount = (EditText) findViewById(R.id.newAccountText);
            Editable AccountName;
            AccountName = EditTextaccount.getText();
            String Account = AccountName.toString(); //email(帳號)

            EditText EditTextpassword = (EditText) findViewById(R.id.newpasswordText);
            Editable passwordName;
            passwordName = EditTextpassword.getText();
            String password = passwordName.toString();//密碼


            flag1 = 0; //用一個flag來確認帳號是否重複
            if (username.trim().length() == 0 || userSubname.trim().length() == 0) {
                new AlertDialog.Builder(CreateNewAccount.this)
                        .setTitle("申請失敗")
                        .setMessage("請輸入姓名")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).show();
            } else if (Account.trim().length() == 0) {
                new AlertDialog.Builder(CreateNewAccount.this)
                        .setTitle("申請失敗")
                        .setMessage("請輸入帳號")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            } else if (password.trim().length() == 0) {
                new AlertDialog.Builder(CreateNewAccount.this)
                        .setTitle("申請失敗")
                        .setMessage("請輸入密碼")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            } else if (flagForConfirmData == 0) {
                new AlertDialog.Builder(CreateNewAccount.this)
                        .setTitle("申請失敗")
                        .setMessage("請確認資料輸入完整")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            } else {
                Log.v("click_apply", "1");

                new checkAccount().execute(); //a new thread

            }
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
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
    class checkAccount extends  AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){ //loading畫面
            super.onPreExecute();
            flag1 = 0; //initial
            pDialog = new ProgressDialog(CreateNewAccount.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) { //thread做的事:比對帳密是否重複
            // TODO Auto-generated method stub

            EditText EditTextaccount = (EditText)findViewById(R.id.newAccountText);
            Editable AccountName;
            AccountName = EditTextaccount.getText();
            String Account = AccountName.toString();

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("Account", Account));

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://dmcl.twbbs.org/csmuse/checkAccount.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                String s = EntityUtils.toString(entity); //這邊會抓取php的echo
                System.out.println(s);
                Log.v("pass 1", "connection success ");
                if (s.equals("Available")){ //如果php echo"available"就可申請
                    flag1 = 1;
                }
                else if (s.equals("Assigned")){ //反之
                    flag1 = 2;
                }


            }
            catch(Exception e) //exception
            {
                Log.e("Fail 1", e.toString());
                Toast.makeText(getApplicationContext(), "Invalid IP Address",
                        Toast.LENGTH_LONG).show();
            }

            return null ;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss(); //讓loading的提示消失
            if (flag1 == 1) { //申請成功
                new insertto().execute();
                Toast.makeText(getApplicationContext(), "Apply success",
                        Toast.LENGTH_SHORT).show();
                Intent intentmainview = new Intent(CreateNewAccount.this,
                                                    LoginActivity.class);
                CreateNewAccount.this.startActivity(intentmainview);
                finish();

            }
            else if(flag1 == 2){ //失敗
                new AlertDialog.Builder(CreateNewAccount.this)
                        .setTitle("申請失敗")
                        .setMessage("此帳號已經有人申請")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();
            }
            else{ //如果程式發生奇怪狀況的default
                new AlertDialog.Builder(CreateNewAccount.this)
                        .setTitle("申請失敗")
                        .setMessage("problem undefined|flags unexpected")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }

        }
    }
    //把帳密輸入到資料庫 (結束了上一個thread後 執行的new thread)
    class insertto extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            EditText EditTextname = (EditText)findViewById(R.id.newNameText);
            Editable nameName;
            nameName = EditTextname.getText();
            String username =nameName.toString(); //姓

            EditText EditTextSubName = (EditText)findViewById(R.id.newsubNameText);
            Editable subnameName;
            subnameName = EditTextSubName.getText();
            String userSubname = subnameName.toString(); //名

            EditText EditTextaccount = (EditText)findViewById(R.id.newAccountText);
            Editable AccountName;
            AccountName = EditTextaccount.getText();
            String Account =AccountName.toString(); //email(帳號)

            EditText EditTextpassword = (EditText)findViewById(R.id.newpasswordText);
            Editable passwordName;
            passwordName = EditTextpassword.getText();
            String password =passwordName.toString();//密碼
            //new connect();
            String userSex = String.valueOf(sex);//性別
            String userYear = String.valueOf(yyyy);//年
            String userMonth = String.valueOf(MM);//月
            String userDay = String.valueOf(dd);//日
            String userHour = String.valueOf(hh);//時辰
            String userNewsCheck = String.valueOf(mailCheck);//電子報


            String command = "INSERT INTO Account (Surname,Name,Account,Password,sex,year,month,day,hour,news)VALUES\n('" +
                    username+"','"+
                    userSubname+"','"+
                    Account+"','"+
                    password+"','"+
                    userSex+"','"+
                    userYear+"','"+
                    userMonth+"','"+
                    userDay+"','"+
                    userHour+"','"+
                    userNewsCheck+"')";
            String ans = new connect(command).getServerUpdate();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
