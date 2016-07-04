package dmcl.csmuse2016;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.TaskStackBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

//命盤的activity
public class FreeActivity extends AppCompatActivity {

    Bundle bundle = new Bundle();
    private Button button_Submit,button_Title,button_Result,button_Comment;
    private TextView content;
    private RadioGroup Gruop_Sex;
    private EditText editText_Question;//輸入問題的地方
    String question="";
    String which_sex="女";//性別，預設為女
    private final String filename="account.txt";
    private boolean loginornot;
    private ArrayAdapter<String> listAdapter_questions;
    private Spinner questionSpin;
    private final String usedfile = "used.txt";
    private final String ansfile = "answer.txt";
    private final String questionfile ="question.txt";
    private String[] hotQuestions = {
      "選擇熱門主題 ▼","愛情","工作","財運","今日運勢"
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.free);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_free);
        setSupportActionBar(toolbar);

        // App Logo
        toolbar.setLogo(R.mipmap.title02);
        // Title
        toolbar.setTitle("每日卜卦");
        toolbar.setTitleTextColor(Color.BLACK);
        // Sub Title
        toolbar.setSubtitle("88Say幫您及時掌握未來");
        toolbar.setSubtitleTextColor(Color.BLACK);

        setSupportActionBar(toolbar);

        // Navigation Icon 要設定在 setSupoortActionBar 才有作用
        // 否則會出現 back bottom
        //toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //函式
        ButtonSummit();
        //加入fragment的函式
        addFragment();
        loginornot = new Write_and_Read(filename,getFilesDir()).ifLogin();
        new Write_and_Read(usedfile,getFilesDir()).WritetoFile("");
        new Write_and_Read(ansfile,getFilesDir()).WritetoFile("");
        new Write_and_Read(questionfile,getFilesDir()).WritetoFile("");
        //2016/4/4 update by 均 get from file
        if (loginornot) {
            String fromfile = new Write_and_Read(filename, getFilesDir()).ReadFromFile();
            String[] tem = fromfile.split("@@@@@");
            String[] fromfileArray = tem[0].split("###");

            RadioButton Man = (RadioButton) findViewById(R.id.radio_Male);
            RadioButton Woman = (RadioButton) findViewById(R.id.radio_Female);

            if (fromfileArray[4].equals("1")) { //帳號的性別為男
                Man.setChecked(true); //select man
            } else {
                Woman.setChecked(true); //select woman
            }
        }

        questionSpin = (Spinner)findViewById(R.id.hotQuestionSpinner);
        listAdapter_questions = new ArrayAdapter<String>(this , R.layout.question_spinner , hotQuestions);
        listAdapter_questions.setDropDownViewResource(R.layout.question_spinner);
        questionSpin.setAdapter(listAdapter_questions);
        questionSpin.setSelection(0);
        editText_Question = (EditText) findViewById(R.id.editText_Question);
        questionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == 0){
                    editText_Question.setText("");
                    editText_Question.setHint("自行輸入您想卜的主題");
                }else{
                    editText_Question.setText(hotQuestions[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(loginornot)
            getMenuInflater().inflate(R.menu.mainmenu, menu);
        else
            getMenuInflater().inflate(R.menu.guestmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //toolbar按鈕被按時
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            Intent tologin =new Intent();
            switch (menuItem.getItemId()) {
                case R.id.action_home: //home鍵被按時
                    finish();
                    break;
                case R.id.action_settings: //setting鍵
                    if(isNetwork()) {
                        String fromfile = new Write_and_Read(filename, getFilesDir()).ReadFromFile();
                        String[] fromfileArray = fromfile.split("###");
                        Intent intentMember = new Intent(getApplicationContext(), MemberActivity.class);
                        intentMember.putExtra("mail", fromfileArray[2]); //send mail to next activity
                        FreeActivity.this.startActivity(intentMember);
                        finish();
                    }
                    else {
                        notNetwork_dialogFragment EditNameDialog = new notNetwork_dialogFragment();
                        EditNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                    break;
                case R.id.action_logout://登出
                    new Write_and_Read(filename,getFilesDir()).WritetoFile_clear("");
                    tologin.setClass(FreeActivity.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
                case R.id.action_login://訪客登入
                    tologin.setClass(FreeActivity.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(FreeActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    void addFragment(){
        //建立一個 MyFirstFragment 的實例(Instantiate)
        Fragment newFragment = new FragmentForFree();
        newFragment.setArguments(bundle);
        //使用getFragmentManager()獲得FragmentTransaction物件，並呼叫 beginTransaction() 開始執行Transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //使用FragmentTransaction物件add()的方法將Fragment增加到Activity中
        //add()有三個參數，第一個是Fragment的ViewGroup；第二個是Fragment 的實例(Instantiate)；第三個是Fragment 的Tag
        ft.add(R.id.L1, newFragment, "second");
        //一旦FragmentTransaction出現變化，必須要呼叫commit()使之生效
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void replaceFragment(){

        Fragment newFragment = new FragmentForFree();
        newFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.L1, newFragment, "second");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }
    //送出資料的button
    void ButtonSummit(){
        button_Submit = (Button)findViewById(R.id.button_Submit);

        button_Submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                String todayY = String.valueOf(today.get(Calendar.YEAR));
                //Log.e("Y",todayY);
                String todayM = String.valueOf(today.get(Calendar.MONTH) + 1);//0 is 1month 11 is Decamber(?)
                String todayD = String.valueOf(today.get(Calendar.DATE));
                int time = Integer.valueOf(todayY + todayM + todayD);
                int usedtime;

                if (new Write_and_Read(usedfile, getFilesDir()).ReadFromFile() != "") {
                    usedtime = Integer.valueOf(new Write_and_Read(usedfile, getFilesDir()).ReadFromFile());
                } else {
                    usedtime = 1;
                }

                if (time != usedtime) {
                    if (isNetwork()) {
                        editText_Question = (EditText) findViewById(R.id.editText_Question);
                        question = editText_Question.getText().toString();//記錄問題
                        Gruop_Sex = (RadioGroup) findViewById(R.id.Gruop_Sex);
                        int select_id = Gruop_Sex.getCheckedRadioButtonId();
                        // 問題輸入轉換為string
                        if (select_id == 2131492997) {
                            which_sex = "0"; //API上，女 = 0
                        } else {
                            which_sex = "1"; //API上，男 = 1
                        }
                        // 產生對映的url，使用Catch_say88_API_info函式
                        String url = Catch_say88_API_info(question, which_sex);
                        //產生異構Task，因為網路部分不能在main裡面進行，接著執行
                        RequestTask request = new RequestTask();
                        request.execute(url);
                    } else {
                        notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
                        editNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                } else {
                    if (isNetwork()) {
                        button_Submit.setText("今天測過了喔");
                        button_Submit.setTextColor(0xffff0000);
                        button_Submit.setTextSize(22);
                        editText_Question = (EditText) findViewById(R.id.editText_Question);
                        String usedans = new Write_and_Read(ansfile, getFilesDir()).ReadFromFile();
                        String usedquestion = new Write_and_Read(questionfile, getFilesDir()).ReadFromFile();
                        editText_Question.setText(usedquestion);
                       // Log.e("usedans",usedans);
                        String ans[] = usedans.split("@");
                        bundle = new Bundle();
                        bundle.putString("TxnCode", ans[0]);
                        bundle.putString("TxnMsg", ans[1]);
                        bundle.putString("Title", ans[2]);
                        //Log.e("Title",Title);"本日網友問題首選"
                        bundle.putString("All_result", ans[3]);
                        bundle.putString("Comment", ans[4]);
                        replaceFragment();
                    } else {
                        notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
                        editNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                }
            }
        });
    }


    public String Catch_say88_API_info(String ques,String sex){
        // token就是識別證，question和sex為使用者輸入
        String url = "http://newtest.88say.com/Api/FreeGua.aspx?";
        url += "token=D5DF5A998BF46E8D37E3D600C022D8B0D76D68BABCF7CFC75304E8EF5168A48B";
        url += "&question=";
        url += ques;
        url += "&sex=";
        url += sex;

        return url;

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
    public class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) { // 任務輸入的值
            this.publishProgress(null);
            try {
                // 連接httpget輸入
                HttpGet httpget = new HttpGet(params[0]);           // 剛剛輸入的url
                HttpClient client = new DefaultHttpClient();        //建立Httpclinet
                HttpResponse response = client.execute(httpget);    //建立response
                HttpEntity resEntityGet = response.getEntity();     //抓取回傳值
                if (resEntityGet != null) {
                    return EntityUtils.toString(resEntityGet, "utf-8"); // 有抓取到回傳值，並用utf-8編碼回傳
                }

            } catch (Exception e) {
                return e.toString();
            }
            return null;  // 沒抓到任何東西
        }

        @Override
        protected void onPostExecute(String text){  // doInBackground的結果會傳至這個涵式

            // 字串，記取回傳資料，這裡有些回傳東西的結構沒有仔細用好，但是執行上不會出錯，可能會少一些結果之類，我會在進行調整。
            String TxnCode="";
            String TxnMsg="";
            String  Title="";
            String Result_love="";
            String Result_marriage="";
            String Result_wish="";
            String Comment="";
            String All_result="";
            //Log.e("text", text);
            // 把JSON架構變成String
            try {
                TxnCode = new JSONObject(text).getString("TxnCode");
                TxnMsg = new JSONObject(text).getString("TxnMsg");
                Title = new JSONObject(new JSONObject(text).getString("Result")).getString("Title");
                Result_love = new JSONObject(new JSONObject(text).getString("Result")).getJSONArray("Result").getString(0);
                Result_marriage=new JSONObject(new JSONObject(text).getString("Result")).getJSONArray("Result").getString(1);
                Result_wish=new JSONObject(new JSONObject(text).getString("Result")).getJSONArray("Result").getString(2);
                Comment = new JSONObject(new JSONObject(text).getString("Result")).getString("Comment");
                 All_result = Result_love +"\n" +  Result_marriage + "\n" +  Result_wish;
                bundle.putString("TxnCode", TxnCode);
                bundle.putString("TxnMsg",  TxnMsg);
                bundle.putString("Title", Title);
                //Log.e("Title",Title);"本日網友問題首選"
                bundle.putString("All_result", All_result);
                bundle.putString("Comment",  Comment);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(Title !="本日網友問題首選"&&Integer.valueOf(TxnCode)!=0){
                Calendar today = Calendar.getInstance();
                String todayY =String.valueOf(today.get(Calendar.YEAR));
                //Log.e("Y",todayY);
                String todayM = String.valueOf(today.get(Calendar.MONTH) + 1);//0 is 1month 11 is Decamber(?)
                String todayD = String.valueOf(today.get(Calendar.DATE));
                new Write_and_Read(usedfile,getFilesDir()).WritetoFile_clear(todayY+todayM+todayD);
                new Write_and_Read(questionfile,getFilesDir()).WritetoFile_clear(question);
                new Write_and_Read(ansfile,getFilesDir()).WritetoFile_clear(TxnCode + "@" + TxnMsg + "@" + Title + "@" + All_result + "@" + Comment);
                //Log.e("ans",new Write_and_Read(ansfile,getFilesDir()).ReadFromFile());
            }
            replaceFragment();
        }

        @Override
        protected void onProgressUpdate(String... result){

        }
    }

}
