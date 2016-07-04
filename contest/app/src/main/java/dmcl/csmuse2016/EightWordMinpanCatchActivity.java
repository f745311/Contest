package dmcl.csmuse2016;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//八字命盤的activity
public class EightWordMinpanCatchActivity extends AppCompatActivity {


    private final String filename="account.txt";
    private boolean loginornot;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eightwordcatch);



        //addFragment(); hao wei


          Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_catcheight);
          setSupportActionBar(toolbar);

        // App Logo
           toolbar.setLogo(R.mipmap.title02);
        // Title
           toolbar.setTitle("八字命盤");
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
        ReturnButton();
        //加入fragment的函式

        addFragment();
        //  loginornot = new Write_and_Read(filename,getFilesDir()).ifLogin();
    /*}
    void addFragment(){ //hao wei
        //建立一個 MyFirstFragment 的實例(Instantiate)
        Fragment newFragment = new holdFrgment();
        //使用getFragmentManager()獲得FragmentTransaction物件，並呼叫 beginTransaction() 開始執行Transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //使用FragmentTransaction物件add()的方法將Fragment增加到Activity中
        //add()有三個參數，第一個是Fragment的ViewGroup；第二個是Fragment 的實例(Instantiate)；第三個是Fragment 的Tag
        ft.add(R.id.left_drawer, newFragment, "first");
        //一旦FragmentTransaction出現變化，必須要呼叫commit()使之生效
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();*/


        loginornot = new Write_and_Read(filename,getFilesDir()).ifLogin();

    }
    void addFragment(){
        //建立一個 MyFirstFragment 的實例(Instantiate)
        Fragment newFragment = new EightWordMinpan();
        //使用getFragmentManager()獲得FragmentTransaction物件，並呼叫 beginTransaction() 開始執行Transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //使用FragmentTransaction物件add()的方法將Fragment增加到Activity中
        //add()有三個參數，第一個是Fragment的ViewGroup；第二個是Fragment 的實例(Instantiate)；第三個是Fragment 的Tag
        ft.add(R.id.left_drawer, newFragment, "first");
        //一旦FragmentTransaction出現變化，必須要呼叫commit()使之生效
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                    String fromfile =  new Write_and_Read(filename,getFilesDir()).ReadFromFile();
                    String[] fromfileArray = fromfile.split("###");
                    Intent intentMember = new Intent(getApplicationContext(),MemberActivity.class);
                    intentMember.putExtra("mail", fromfileArray[2]); //send mail to next activity
                    EightWordMinpanCatchActivity.this.startActivity(intentMember);
                    finish();
                    break;
                case R.id.action_logout://登出
                    new Write_and_Read(filename,getFilesDir()).WritetoFile_clear("");
                    tologin.setClass(EightWordMinpanCatchActivity.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
                case R.id.action_login://訪客登入
                    tologin.setClass(EightWordMinpanCatchActivity.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(EightWordMinpanCatchActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    public void getInfo(Bundle info_bundle) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout) ;
        drawerLayout.closeDrawer(GravityCompat.START);


            String Reslut_Sex = info_bundle.getString("Result_Sex");
            String Reslut_Age = info_bundle.getString("Result_Age");
            String Reslut_Birth = info_bundle.getString("Result_Birth");
            String Reslut_LunarBirth = info_bundle.getString("Result_LunarBirth");

            TextView character_Birth = (TextView) findViewById(R.id.character_Birth);
            character_Birth.setText(Reslut_Birth);
            TextView character_LunarBirth = (TextView) findViewById(R.id.character_LunarBirth);
            character_LunarBirth.setText(Reslut_LunarBirth);
            TextView character_Sex = (TextView) findViewById(R.id.character_Sex);
            character_Sex.setText(Reslut_Sex);
            TextView character_Age = (TextView) findViewById(R.id.character_Age);
            character_Age.setText(Reslut_Age);

            String Result_GanZhi_Gan1 = info_bundle.getString("Result_GanZhi_Gan1");
            String Result_GanZhi_GanInfo1 = info_bundle.getString("Result_GanZhi_GanInfo1");
            String Result_GanZhi_Zhi1 = info_bundle.getString("Result_GanZhi_Zhi1");
            int count1 = info_bundle.getInt("count1");
            TextView GanInfo1 = (TextView) findViewById(R.id.GanInfo1);
            GanInfo1.setText(Result_GanZhi_GanInfo1);
            TextView GanZhi1 = (TextView) findViewById(R.id.GanZhi1);
            GanZhi1.setText(Result_GanZhi_Gan1 + " " + Result_GanZhi_Zhi1);

            String Result_GanZhi_Gan2 = info_bundle.getString("Result_GanZhi_Gan2");
            String Result_GanZhi_GanInfo2 = info_bundle.getString("Result_GanZhi_GanInfo2");
            String Result_GanZhi_Zhi2 = info_bundle.getString("Result_GanZhi_Zhi2");
            int count2 = info_bundle.getInt("count2");
            TextView GanInfo2 = (TextView) findViewById(R.id.GanInfo2);
            GanInfo2.setText(Result_GanZhi_GanInfo2);
            TextView GanZhi2 = (TextView) findViewById(R.id.GanZhi2);
            GanZhi2.setText(Result_GanZhi_Gan2 + " " + Result_GanZhi_Zhi2);

            String Result_GanZhi_Gan3 = info_bundle.getString("Result_GanZhi_Gan3");
            String Result_GanZhi_GanInfo3 = info_bundle.getString("Result_GanZhi_GanInfo3");
            String Result_GanZhi_Zhi3 = info_bundle.getString("Result_GanZhi_Zhi3");
            int count3 = info_bundle.getInt("count3");
            TextView GanInfo3 = (TextView) findViewById(R.id.GanInfo3);
            GanInfo3.setText(Result_GanZhi_GanInfo3);
            TextView GanZhi3 = (TextView) findViewById(R.id.GanZhi3);
            GanZhi3.setText(Result_GanZhi_Gan3 + " " + Result_GanZhi_Zhi3);

            String Result_GanZhi_Gan4 = info_bundle.getString("Result_GanZhi_Gan4");
            String Result_GanZhi_GanInfo4 = info_bundle.getString("Result_GanZhi_GanInfo4");
            String Result_GanZhi_Zhi4 = info_bundle.getString("Result_GanZhi_Zhi4");
            int count4 = info_bundle.getInt("count4");
            TextView GanInfo4 = (TextView) findViewById(R.id.GanInfo4);
            GanInfo4.setText(Result_GanZhi_GanInfo4);
            TextView GanZhi4 = (TextView) findViewById(R.id.GanZhi4);
            GanZhi4.setText(Result_GanZhi_Gan4 + " " + Result_GanZhi_Zhi4);

            String[] Result_GanZhi_ZhiInfo1 = new String[5];
            String[] Result_GanZhi_ZhiInfo2 = new String[5];
            String[] Result_GanZhi_ZhiInfo3 = new String[5];
            String[] Result_GanZhi_ZhiInfo4 = new String[5];

            String GanZhi_ZhiInfo1 = "";
            for (int i = 0; i < count1; i++) {
                Result_GanZhi_ZhiInfo1[i] = info_bundle.getString("Result_GanZhi_Gan1_" + Integer.toString(i));
                GanZhi_ZhiInfo1 += Result_GanZhi_ZhiInfo1[i];
                if (count1 - 1 != i) {
                    GanZhi_ZhiInfo1 += " ";
                }
            }
            TextView ZhiInfo1 = (TextView) findViewById(R.id.ZhiInfo1);
            ZhiInfo1.setText(GanZhi_ZhiInfo1);

            String GanZhi_ZhiInfo2 = "";
            for (int i = 0; i < count2; i++) {
                Result_GanZhi_ZhiInfo2[i] = info_bundle.getString("Result_GanZhi_Gan2_" + Integer.toString(i));
                GanZhi_ZhiInfo2 += Result_GanZhi_ZhiInfo2[i];
                if (count2 - 1 != i) {
                    GanZhi_ZhiInfo2 += " ";
                }
            }
            TextView ZhiInfo2 = (TextView) findViewById(R.id.ZhiInfo2);
            ZhiInfo2.setText(GanZhi_ZhiInfo2);

            String GanZhi_ZhiInfo3 = "";
            for (int i = 0; i < count3; i++) {
                Result_GanZhi_ZhiInfo3[i] = info_bundle.getString("Result_GanZhi_Gan3_" + Integer.toString(i));
                GanZhi_ZhiInfo3 += Result_GanZhi_ZhiInfo3[i];
                if (count3 - 1 != i) {
                    GanZhi_ZhiInfo3 += " ";
                }
            }
            TextView ZhiInfo3 = (TextView) findViewById(R.id.ZhiInfo3);
            ZhiInfo3.setText(GanZhi_ZhiInfo3);

            String GanZhi_ZhiInfo4 = "";
            for (int i = 0; i < count4; i++) {
                Result_GanZhi_ZhiInfo4[i] = info_bundle.getString("Result_GanZhi_Gan4_" + Integer.toString(i));
                GanZhi_ZhiInfo4 += Result_GanZhi_ZhiInfo4[i];
                if (count4 - 1 != i) {
                    GanZhi_ZhiInfo4 += " ";
                }
            }
            TextView ZhiInfo4 = (TextView) findViewById(R.id.ZhiInfo4);
            ZhiInfo4.setText(GanZhi_ZhiInfo4);

            String[] Result_Decade_GanZhi_ZhiInfo1 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo2 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo3 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo4 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo5 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo6 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo7 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo8 = new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo9 = new String[5];

            String Result_Decade_GanZhi_Gan1 = info_bundle.getString("Result_Decade_GanZhi_Gan1");
            String Result_Decade_GanZhi_GanInfo1 = info_bundle.getString("Result_Decade_GanZhi_GanInfo1");
            String Result_Decade_GanZhi_Zhi1 = info_bundle.getString("Result_Decade_GanZhi_Zhi1");
            int count_de_1 = info_bundle.getInt("count_de_1");
            TextView D_GanInfo1 = (TextView) findViewById(R.id.D_GanInfo1);
            D_GanInfo1.setText(Result_Decade_GanZhi_GanInfo1);
            TextView D_GanZhi1 = (TextView) findViewById(R.id.D_GanZhi1);
            D_GanZhi1.setText(Result_Decade_GanZhi_Gan1 + " " + Result_Decade_GanZhi_Zhi1);

            String Result_Decade_GanZhi_Gan2 = info_bundle.getString("Result_Decade_GanZhi_Gan2");
            String Result_Decade_GanZhi_GanInfo2 = info_bundle.getString("Result_Decade_GanZhi_GanInfo2");
            String Result_Decade_GanZhi_Zhi2 = info_bundle.getString("Result_Decade_GanZhi_Zhi2");
            int count_de_2 = info_bundle.getInt("count_de_2");
            TextView D_GanInfo2 = (TextView) findViewById(R.id.D_GanInfo2);
            D_GanInfo2.setText(Result_Decade_GanZhi_GanInfo2);
            TextView D_GanZhi2 = (TextView) findViewById(R.id.D_GanZhi2);
            D_GanZhi2.setText(Result_Decade_GanZhi_Gan2 + " " + Result_Decade_GanZhi_Zhi2);

            String Result_Decade_GanZhi_Gan3 = info_bundle.getString("Result_Decade_GanZhi_Gan3");
            String Result_Decade_GanZhi_GanInfo3 = info_bundle.getString("Result_Decade_GanZhi_GanInfo3");
            String Result_Decade_GanZhi_Zhi3 = info_bundle.getString("Result_Decade_GanZhi_Zhi3");
            int count_de_3 = info_bundle.getInt("count_de_3");
            TextView D_GanInfo3 = (TextView) findViewById(R.id.D_GanInfo3);
            D_GanInfo3.setText(Result_Decade_GanZhi_GanInfo3);
            TextView D_GanZhi3 = (TextView) findViewById(R.id.D_GanZhi3);
            D_GanZhi3.setText(Result_Decade_GanZhi_Gan3 + " " + Result_Decade_GanZhi_Zhi3);

            String Result_Decade_GanZhi_Gan4 = info_bundle.getString("Result_Decade_GanZhi_Gan4");
            String Result_Decade_GanZhi_GanInfo4 = info_bundle.getString("Result_Decade_GanZhi_GanInfo4");
            String Result_Decade_GanZhi_Zhi4 = info_bundle.getString("Result_Decade_GanZhi_Zhi4");
            int count_de_4 = info_bundle.getInt("count_de_4");
            TextView D_GanInfo4 = (TextView) findViewById(R.id.D_GanInfo4);
            D_GanInfo4.setText(Result_Decade_GanZhi_GanInfo4);
            TextView D_GanZhi4 = (TextView) findViewById(R.id.D_GanZhi4);
            D_GanZhi4.setText(Result_Decade_GanZhi_Gan4 + " " + Result_Decade_GanZhi_Zhi4);

            String Result_Decade_GanZhi_Gan5 = info_bundle.getString("Result_Decade_GanZhi_Gan5");
            String Result_Decade_GanZhi_GanInfo5 = info_bundle.getString("Result_Decade_GanZhi_GanInfo5");
            String Result_Decade_GanZhi_Zhi5 = info_bundle.getString("Result_Decade_GanZhi_Zhi5");
            int count_de_5 = info_bundle.getInt("count_de_5");
            TextView D_GanInfo5 = (TextView) findViewById(R.id.D_GanInfo5);
            D_GanInfo5.setText(Result_Decade_GanZhi_GanInfo5);
            TextView D_GanZhi5 = (TextView) findViewById(R.id.D_GanZhi5);
            D_GanZhi5.setText(Result_Decade_GanZhi_Gan5 + " " + Result_Decade_GanZhi_Zhi5);

            String Result_Decade_GanZhi_Gan6 = info_bundle.getString("Result_Decade_GanZhi_Gan6");
            String Result_Decade_GanZhi_GanInfo6 = info_bundle.getString("Result_Decade_GanZhi_GanInfo6");
            String Result_Decade_GanZhi_Zhi6 = info_bundle.getString("Result_Decade_GanZhi_Zhi6");
            int count_de_6 = info_bundle.getInt("count_de_6");
            TextView D_GanInfo6 = (TextView) findViewById(R.id.D_GanInfo6);
            D_GanInfo6.setText(Result_Decade_GanZhi_GanInfo6);
            TextView D_GanZhi6 = (TextView) findViewById(R.id.D_GanZhi6);
            D_GanZhi6.setText(Result_Decade_GanZhi_Gan6 + " " + Result_Decade_GanZhi_Zhi6);

            String Result_Decade_GanZhi_Gan7 = info_bundle.getString("Result_Decade_GanZhi_Gan7");
            String Result_Decade_GanZhi_GanInfo7 = info_bundle.getString("Result_Decade_GanZhi_GanInfo7");
            String Result_Decade_GanZhi_Zhi7 = info_bundle.getString("Result_Decade_GanZhi_Zhi7");
            int count_de_7 = info_bundle.getInt("count_de_7");
            TextView D_GanInfo7 = (TextView) findViewById(R.id.D_GanInfo7);
            D_GanInfo7.setText(Result_Decade_GanZhi_GanInfo7);
            TextView D_GanZhi7 = (TextView) findViewById(R.id.D_GanZhi7);
            D_GanZhi7.setText(Result_Decade_GanZhi_Gan7 + " " + Result_Decade_GanZhi_Zhi7);

            String Result_Decade_GanZhi_Gan8 = info_bundle.getString("Result_Decade_GanZhi_Gan8");
            String Result_Decade_GanZhi_GanInfo8 = info_bundle.getString("Result_Decade_GanZhi_GanInfo8");
            String Result_Decade_GanZhi_Zhi8 = info_bundle.getString("Result_Decade_GanZhi_Zhi8");
            int count_de_8 = info_bundle.getInt("count_de_8");
            TextView D_GanInfo8 = (TextView) findViewById(R.id.D_GanInfo8);
            D_GanInfo8.setText(Result_Decade_GanZhi_GanInfo8);
            TextView D_GanZhi8 = (TextView) findViewById(R.id.D_GanZhi8);
            D_GanZhi8.setText(Result_Decade_GanZhi_Gan8 + " " + Result_Decade_GanZhi_Zhi8);


            String Result_Decade_GanZhi_Gan9 = info_bundle.getString("Result_Decade_GanZhi_Gan9");
            String Result_Decade_GanZhi_GanInfo9 = info_bundle.getString("Result_Decade_GanZhi_GanInfo9");
            String Result_Decade_GanZhi_Zhi9 = info_bundle.getString("Result_Decade_GanZhi_Zhi9");
            int count_de_9 = info_bundle.getInt("count_de_9");
            TextView D_GanInfo9 = (TextView) findViewById(R.id.D_GanInfo9);
            D_GanInfo9.setText(Result_Decade_GanZhi_GanInfo9);
            TextView D_GanZhi9 = (TextView) findViewById(R.id.D_GanZhi9);
            D_GanZhi9.setText(Result_Decade_GanZhi_Gan9 + " " + Result_Decade_GanZhi_Zhi9);


            String Decade_ZhiInfo1 = "";
            for (int i = 0; i < count_de_1; i++) {
                Result_Decade_GanZhi_ZhiInfo1[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo1_" + Integer.toString(i));
                Decade_ZhiInfo1 += Result_Decade_GanZhi_ZhiInfo1[i];
                if (i != count_de_1 - 1) {
                    Decade_ZhiInfo1 += "\n";
                }
            }
            TextView D_ZhiInfo1 = (TextView) findViewById(R.id.D_ZhiInfo1);
            D_ZhiInfo1.setText(Decade_ZhiInfo1);

            String Decade_ZhiInfo2 = "";
            for (int i = 0; i < count_de_2; i++) {
                Result_Decade_GanZhi_ZhiInfo2[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo2_" + Integer.toString(i));
                Decade_ZhiInfo2 += Result_Decade_GanZhi_ZhiInfo2[i];
                if (i != count_de_2 - 1) {
                    Decade_ZhiInfo2 += "\n";
                }
            }
            TextView D_ZhiInfo2 = (TextView) findViewById(R.id.D_ZhiInfo2);
            D_ZhiInfo2.setText(Decade_ZhiInfo2);

            String Decade_ZhiInfo3 = "";
            for (int i = 0; i < count_de_3; i++) {
                Result_Decade_GanZhi_ZhiInfo3[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo3_" + Integer.toString(i));
                Decade_ZhiInfo3 += Result_Decade_GanZhi_ZhiInfo3[i];
                if (i != count_de_3 - 1) {
                    Decade_ZhiInfo3 += "\n";
                }
            }
            TextView D_ZhiInfo3 = (TextView) findViewById(R.id.D_ZhiInfo3);
            D_ZhiInfo3.setText(Decade_ZhiInfo3);

            String Decade_ZhiInfo4 = "";
            for (int i = 0; i < count_de_4; i++) {
                Result_Decade_GanZhi_ZhiInfo4[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo4_" + Integer.toString(i));
                Decade_ZhiInfo4 += Result_Decade_GanZhi_ZhiInfo4[i];
                if (i != count_de_4 - 1) {
                    Decade_ZhiInfo4 += "\n";
                }
            }
            TextView D_ZhiInfo4 = (TextView) findViewById(R.id.D_ZhiInfo4);
            D_ZhiInfo4.setText(Decade_ZhiInfo4);

            String Decade_ZhiInfo5 = "";
            for (int i = 0; i < count_de_5; i++) {
                Result_Decade_GanZhi_ZhiInfo5[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo5_" + Integer.toString(i));
                Decade_ZhiInfo5 += Result_Decade_GanZhi_ZhiInfo5[i];
                if (i != count_de_5 - 1) {
                    Decade_ZhiInfo5 += "\n";
                }
            }
            TextView D_ZhiInfo5 = (TextView) findViewById(R.id.D_ZhiInfo5);
            D_ZhiInfo5.setText(Decade_ZhiInfo5);

            String Decade_ZhiInfo6 = "";
            for (int i = 0; i < count_de_6; i++) {
                Result_Decade_GanZhi_ZhiInfo6[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo6_" + Integer.toString(i));
                Decade_ZhiInfo6 += Result_Decade_GanZhi_ZhiInfo6[i];
                if (i != count_de_6 - 1) {
                    Decade_ZhiInfo6 += "\n";
                }
            }
            TextView D_ZhiInfo6 = (TextView) findViewById(R.id.D_ZhiInfo6);
            D_ZhiInfo6.setText(Decade_ZhiInfo6);

            String Decade_ZhiInfo7 = "";
            for (int i = 0; i < count_de_7; i++) {
                Result_Decade_GanZhi_ZhiInfo7[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo7_" + Integer.toString(i));
                Decade_ZhiInfo7 += Result_Decade_GanZhi_ZhiInfo7[i];
                if (i != count_de_7 - 1) {
                    Decade_ZhiInfo7 += "\n";
                }
            }
            TextView D_ZhiInfo7 = (TextView) findViewById(R.id.D_ZhiInfo7);
            D_ZhiInfo7.setText(Decade_ZhiInfo7);

            String Decade_ZhiInfo8 = "";
            for (int i = 0; i < count_de_8; i++) {
                Result_Decade_GanZhi_ZhiInfo8[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo8_" + Integer.toString(i));
                Decade_ZhiInfo8 += Result_Decade_GanZhi_ZhiInfo8[i];
                if (i != count_de_8 - 1) {
                    Decade_ZhiInfo8 += "\n";
                }
            }
            TextView D_ZhiInfo8 = (TextView) findViewById(R.id.D_ZhiInfo8);
            D_ZhiInfo8.setText(Decade_ZhiInfo8);

            String Decade_ZhiInfo9 = "";
            for (int i = 0; i < count_de_9; i++) {
                Result_Decade_GanZhi_ZhiInfo9[i] = info_bundle.getString("Result_Decade_GanZhi_ZhiInfo9_" + Integer.toString(i));
                Decade_ZhiInfo9 += Result_Decade_GanZhi_ZhiInfo9[i];
                if (i != count_de_9 - 1) {
                    Decade_ZhiInfo9 += "\n";
                }
            }
            TextView D_ZhiInfo9 = (TextView) findViewById(R.id.D_ZhiInfo9);
            D_ZhiInfo9.setText(Decade_ZhiInfo9);

            String Result_SolarTerm_Name1 = info_bundle.getString("Result_SolarTerm_Name1");
            String Result_SolarTerm_Date1 = info_bundle.getString("Result_SolarTerm_Date1");
            String Result_SolarTerm_Diff1 = info_bundle.getString("Result_SolarTerm_Diff1");
            TextView SolarName1 = (TextView) findViewById(R.id.SolarName1);
            SolarName1.setText(Result_SolarTerm_Name1);
            TextView SolarDate1 = (TextView) findViewById(R.id.SolarDate1);
            SolarDate1.setText(Result_SolarTerm_Date1);
            TextView SolarDiff1 = (TextView) findViewById(R.id.SolarDiff1);
            SolarDiff1.setText(Result_SolarTerm_Diff1 + "天");

            String Result_SolarTerm_Name2 = info_bundle.getString("Result_SolarTerm_Name2");
            String Result_SolarTerm_Date2 = info_bundle.getString("Result_SolarTerm_Date2");
            String Result_SolarTerm_Diff2 = info_bundle.getString("Result_SolarTerm_Diff2");
            TextView SolarName2 = (TextView) findViewById(R.id.SolarName2);
            SolarName2.setText(Result_SolarTerm_Name2);
            TextView SolarDate2 = (TextView) findViewById(R.id.SolarDate2);
            SolarDate2.setText(Result_SolarTerm_Date2);
            TextView SolarDiff2 = (TextView) findViewById(R.id.SolarDiff2);
            SolarDiff2.setText(Result_SolarTerm_Diff2 + "天");

    }
    public void ReturnButton(){
        Button ReturnButton = (Button)findViewById(R.id.button2);
        ReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout) ;
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }







    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static class EightWordMinpan extends Fragment {

        View buttonInteract;
        RadioGroup yearTpye , sexType;
        Button Send;
        String s_yearTpye,s_sexTpye;
        Bundle bundle_charater = new Bundle();
        RadioButton west , guo , non , male , female;
        private String[] hour_list = {"0:00~0:59(子時)","1:00~1:59(丑時)","2:00~2:59(丑時)","3:00~3:59(寅時)","4:00~4:59(寅時)","5:00~5:59(卯時)",
                "6:00~6:59(卯時)","7:00~7:59(辰時)","8:00~8:59(辰時)","9:00~9:59(巳時)","10:00~10:59(巳時)","11:00~11:59(午時)",
                "12:00~12:59(午時)","13:00~13:59(未時)","14:00~14:59(未時)","15:00~15:59(申時)","16:00~16:59(申時)","17:00~17:59(酉時)",
                "18:00~18:59(酉時)","19:00~19:59(戌時)","20:00~20:59(戌時)","21:00~21:59(亥時)","22:00~22:59(亥時)","23:00~23:59(子時)",};

        private String[] year_list = {"1901","1902","1903","1904","1905","1906","1907","1908","1909",
                "1910","1911","1912","1913","1914","1915","1916","1917","1918","1919",
                "1920","1921","1922","1923","1924","1925","1926","1927","1928","1929",
                "1930","1931","1932","1933","1934","1935","1936","1937","1938","1939",
                "1940","1941","1942","1943","1944","1945","1946","1947","1948","1949",
                "1950","1951","1952","1953","1954","1955","1956","1957","1958","1959",
                "1960","1961","1962","1963","1964","1965","1966","1967","1968","1969",
                "1970","1971","1972","1973","1974","1975","1976","1977","1978","1979",
                "1980","1981","1982","1983","1984","1985","1986","1987","1988","1989",
                "1990","1991","1992","1993","1994","1995","1996","1997","1998","1999",
                "2000","2001","2002","2003","2004","2005","2006","2007","2008","2009",
                "2010","2011","2012","2013","2014","2015","2016","2017","2018","2019",
                "2020","2021","2022","2023","2024","2025","2026","2027","2028","2029"};

        private  String[] month_list = {"1","2","3","4","5","6","7","8","9","10","11","12"};

        private  String[] day_list = {"1","2","3","4","5","6","7","8","9","10",
                "11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30",
                "31"};
        private ArrayAdapter<String> listAdapter , listAdapter_year,listAdapter_month,listAdapter_day;
        private Spinner spinner , spinner_year , spinner_month , spinner_day;
        private final String filename="account.txt";
        private boolean loginornot;
        public  TextView errorMs;
        String hour="",s_year="",s_month="",s_day="";
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.eightwordminpan, container, false);
            onClickButton(v);
            //加入fragment的函式
            //addFragment();
            loginornot = new Write_and_Read(filename,getActivity().getFilesDir()).ifLogin();

            //2016/4/10 update by 均 get from file
            if (loginornot) {
                String fromfile = new Write_and_Read(filename, getActivity().getFilesDir()).ReadFromFile();
                String[] tem = fromfile.split("@@@@@");
                String[] fromfileArray = tem[0].split("###");

                int fileYear = Integer.parseInt(fromfileArray[6]);
                int fileMonth = Integer.parseInt(fromfileArray[7]);
                int fileDay = Integer.parseInt(fromfileArray[8]);
                int fileHour = Integer.parseInt(fromfileArray[9]);
                int yearPosition = fileYear - 1901;
                int monthPosition = fileMonth - 1;
                int dayPosition = fileDay - 1;
                //設定年的初始值
                spinner_year = (Spinner)v.findViewById(R.id.spinner_year_char);
                listAdapter_year = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, year_list);
                listAdapter_year.setDropDownViewResource(R.layout.myspinner);
                spinner_year.setAdapter(listAdapter_year);
                spinner_year.setSelection(yearPosition);
                s_year = year_list[yearPosition]; //傳入value
                //設定月的初始值
                spinner_month = (Spinner)v.findViewById(R.id.spinner_month_char);
                listAdapter_month = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, month_list);
                listAdapter_month.setDropDownViewResource(R.layout.myspinner);
                spinner_month.setAdapter(listAdapter_month);
                spinner_month.setSelection(monthPosition);
                s_month = month_list[monthPosition]; //傳入value
                //設定日的初始值
                spinner_day = (Spinner)v.findViewById(R.id.spinner_day_char);
                listAdapter_day = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, day_list);
                listAdapter_day.setDropDownViewResource(R.layout.myspinner);
                spinner_day.setAdapter(listAdapter_day);
                spinner_day.setSelection(dayPosition);
                s_day = day_list[dayPosition]; //傳入value
                //設定性別初始值
                male = (RadioButton)v.findViewById(R.id.radio_Male_char);
                female = (RadioButton)v.findViewById(R.id.radio_Female_char);
                if (fromfileArray[4].equals("1")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
                //設定時間的初始值
                spinner = (Spinner)v.findViewById(R.id.spinner_char);
                listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, hour_list);
                listAdapter.setDropDownViewResource(R.layout.myspinner);
                spinner.setAdapter(listAdapter);
                spinner.setSelection(fileHour);
                hour = hour_list[fileHour];
            }else{
                //設定年的初始值
                spinner_year = (Spinner)v.findViewById(R.id.spinner_year_char);
                listAdapter_year = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, year_list);
                listAdapter_year.setDropDownViewResource(R.layout.myspinner);
                spinner_year.setAdapter(listAdapter_year);
                spinner_year.setSelection(90);
                s_year = year_list[90]; //傳入value
            }
            return v;
        }

        private boolean isNetwork()
        {
            boolean result = false;
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        //送出資料的button
        public void onClickButton(final View v){

            Send = (Button)v.findViewById(R.id.button_Submit3);
            yearTpye = (RadioGroup)v.findViewById(R.id.Gruop_YearType_char);
            sexType = (RadioGroup)v.findViewById(R.id.Gruop_Sex3);

            // 時辰
            spinner = (Spinner)v.findViewById(R.id.spinner_char);
            listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, hour_list);
            listAdapter.setDropDownViewResource(R.layout.myspinner);
            spinner.setAdapter(listAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    hour = Integer.toString(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

            // 年
            spinner_year = (Spinner)v.findViewById(R.id.spinner_year_char);
            listAdapter_year = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, year_list);
            listAdapter_year.setDropDownViewResource(R.layout.myspinner);
            spinner_year.setAdapter(listAdapter_year);
            spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    s_year = year_list[position];
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            // 月
            spinner_month = (Spinner)v.findViewById(R.id.spinner_month_char);
            listAdapter_month = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, month_list);
            listAdapter_month.setDropDownViewResource(R.layout.myspinner);
            spinner_month.setAdapter(listAdapter_month);
            spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    s_month =month_list[position];
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            // 日
            spinner_day = (Spinner)v.findViewById(R.id.spinner_day_char);
            listAdapter_day = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, day_list);
            listAdapter_day.setDropDownViewResource(R.layout.myspinner);
            spinner_day.setAdapter(listAdapter_day);
            spinner_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    s_day = day_list[position];
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

            Send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View ov) {
                    // 找性別ID，經過ID判斷是男是女
                    if (isNetwork()) {
                        errorMs = (TextView) v.findViewById(R.id.textViewErrorEightWord);

                        int select_year_type = yearTpye.getCheckedRadioButtonId();
                        int select_sex_tpye = sexType.getCheckedRadioButtonId();

                        west = (RadioButton) v.findViewById(R.id.radio_west_char);
                        guo = (RadioButton) v.findViewById(R.id.radio_guo_char);
                        non = (RadioButton) v.findViewById(R.id.radio_non_char);

                        male = (RadioButton) v.findViewById(R.id.radio_Male_char);
                        female = (RadioButton) v.findViewById(R.id.radio_Female_char);

                        if (select_year_type == west.getId()) {
                            s_yearTpye = "0";
                        } else if (select_year_type == guo.getId()) {
                            s_yearTpye = "1";
                        } else {
                            s_yearTpye = "2";
                        }

                        if (select_sex_tpye == female.getId()) {
                            s_sexTpye = "0";
                        } else {
                            s_sexTpye = "1";
                        }

                        String s_hour = hour;

                        errorMs.setText("資料讀取中，請稍候.....");
                        String url = Catch_say88_API_info(s_yearTpye, s_year, s_month, s_day, s_hour, s_sexTpye);
                        RequestTask request = new RequestTask();
                        request.execute(url);
                    } else {
                        notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
                        editNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                }
            });

        }

        public String Catch_say88_API_info(String birthType,String Year,String Month , String Day ,String Hour,String Sex){
            // token就是識別證
            String url = "http://newtest.88say.com/Api/product/Unit374.aspx?";
            url += "token=D5DF5A998BF46E8D37E3D600C022D8B0D76D68BABCF7CFC75304E8EF5168A48B";
            url += "&birthType=";
            url += birthType;
            url += "&year=";
            url += Year;
            url += "&month=";
            url += Month;
            url += "&day=";
            url += Day;
            url += "&hour=";
            url += Hour;
            url += "&sex=";
            url += Sex;
            return url;
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

                JsonTrasnfer_output(text);
                ((EightWordMinpanCatchActivity)getActivity()).getInfo(bundle_charater);


            }

            @Override
            protected void onProgressUpdate(String... result){

            }
        }

        public void JsonTrasnfer_output(String jsonInput) {
            String TxnCode="";
            String TxnMsg="";
            String Result_Sex="";
            String Result_Age="";
            String Result_Birth="";
            String Result_LunarBirth="";

            String all="";

            String Result_GanZhi_Gan1="";
            String Result_GanZhi_GanInfo1="";
            String Result_GanZhi_Zhi1="";
            int count1;

            String Result_GanZhi_Gan2="";
            String Result_GanZhi_GanInfo2="";
            String Result_GanZhi_Zhi2="";
            int count2;

            String Result_GanZhi_Gan3="";
            String Result_GanZhi_GanInfo3="";
            String Result_GanZhi_Zhi3="";
            int count3;

            String Result_GanZhi_Gan4="";
            String Result_GanZhi_GanInfo4="";
            String Result_GanZhi_Zhi4="";
            int count4;

            String[] Result_GanZhi_ZhiInfo1= new String[5];
            String[] Result_GanZhi_ZhiInfo2= new String[5];
            String[] Result_GanZhi_ZhiInfo3= new String[5];
            String[] Result_GanZhi_ZhiInfo4= new String[5];

            String Result_Decade_GanZhi_Gan1="";
            String Result_Decade_GanZhi_GanInfo1="";
            String Result_Decade_GanZhi_Zhi1="";
            int count_de_1;

            String Result_Decade_GanZhi_Gan2="";
            String Result_Decade_GanZhi_GanInfo2="";
            String Result_Decade_GanZhi_Zhi2="";
            int count_de_2;

            String Result_Decade_GanZhi_Gan3="";
            String Result_Decade_GanZhi_GanInfo3="";
            String Result_Decade_GanZhi_Zhi3="";
            int count_de_3;

            String Result_Decade_GanZhi_Gan4="";
            String Result_Decade_GanZhi_GanInfo4="";
            String Result_Decade_GanZhi_Zhi4="";
            int count_de_4;

            String Result_Decade_GanZhi_Gan5="";
            String Result_Decade_GanZhi_GanInfo5="";
            String Result_Decade_GanZhi_Zhi5="";
            int count_de_5;

            String Result_Decade_GanZhi_Gan6="";
            String Result_Decade_GanZhi_GanInfo6="";
            String Result_Decade_GanZhi_Zhi6="";
            int count_de_6;

            String Result_Decade_GanZhi_Gan7="";
            String Result_Decade_GanZhi_GanInfo7="";
            String Result_Decade_GanZhi_Zhi7="";
            int count_de_7;

            String Result_Decade_GanZhi_Gan8="";
            String Result_Decade_GanZhi_GanInfo8="";
            String Result_Decade_GanZhi_Zhi8="";
            int count_de_8;

            String Result_Decade_GanZhi_Gan9="";
            String Result_Decade_GanZhi_GanInfo9="";
            String Result_Decade_GanZhi_Zhi9="";
            int count_de_9;

            String[] Result_Decade_GanZhi_ZhiInfo1= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo2= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo3= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo4= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo5= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo6= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo7= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo8= new String[5];
            String[] Result_Decade_GanZhi_ZhiInfo9= new String[5];


            String Result_SolarTerm_Name1 = "";
            String Result_SolarTerm_Date1 = "";
            String Result_SolarTerm_Diff1 = "";

            String Result_SolarTerm_Name2 = "";
            String Result_SolarTerm_Date2 = "";
            String Result_SolarTerm_Diff2 = "";

            try{
                TxnCode = new JSONObject(jsonInput).getString("TxnCode");
                TxnMsg = new JSONObject(jsonInput).getString("TxnMsg");
                Result_Sex =new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Sex");
                Result_Age =new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Age");
                Result_Birth =new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Birth");
                Result_LunarBirth =new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("LunarBirth");
                all = Result_Sex +" " + Result_Age + " " + Result_Birth + " "+Result_LunarBirth +"\n";
                bundle_charater.putString("Result_Sex",Result_Sex);
                bundle_charater.putString("Result_Age", Result_Age);
                bundle_charater.putString("Result_Birth",Result_Birth);
                bundle_charater.putString("Result_LunarBirth",Result_LunarBirth);

                Result_GanZhi_Gan1 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(0).getString("Gan");
                Result_GanZhi_GanInfo1 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(0).getString("GanInfo");
                Result_GanZhi_Zhi1  = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(0).getString("Zhi");
                count1 = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(0).getString("ZhiInfo")).length();
                all = all +  Result_GanZhi_Gan1 + " " +  Result_GanZhi_GanInfo1 + " " + Result_GanZhi_Zhi1 + " ";
                bundle_charater.putString("Result_GanZhi_Gan1",Result_GanZhi_Gan1);
                bundle_charater.putString("Result_GanZhi_GanInfo1",Result_GanZhi_GanInfo1);
                bundle_charater.putString("Result_GanZhi_Zhi1", Result_GanZhi_Zhi1);
                bundle_charater.putInt("count1", count1);

                all += "\n";
                Result_GanZhi_Gan2 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(1).getString("Gan");
                Result_GanZhi_GanInfo2 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(1).getString("GanInfo");
                Result_GanZhi_Zhi2  = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(1).getString("Zhi");
                count2 = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(1).getString("ZhiInfo")).length();
                all = all +  Result_GanZhi_Gan2 + " " +  Result_GanZhi_GanInfo2 + " " + Result_GanZhi_Zhi2 + " ";
                bundle_charater.putString("Result_GanZhi_Gan2",Result_GanZhi_Gan2);
                bundle_charater.putString("Result_GanZhi_GanInfo2",Result_GanZhi_GanInfo2);
                bundle_charater.putString("Result_GanZhi_Zhi2", Result_GanZhi_Zhi2);
                bundle_charater.putInt("count2", count2);

                all += "\n";
                Result_GanZhi_Gan3 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(2).getString("Gan");
                Result_GanZhi_GanInfo3 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(2).getString("GanInfo");
                Result_GanZhi_Zhi3  = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(2).getString("Zhi");
                count3 = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(2).getString("ZhiInfo")).length();
                all = all +  Result_GanZhi_Gan3 + " " +  Result_GanZhi_GanInfo3 + " " + Result_GanZhi_Zhi3 + " ";
                bundle_charater.putString("Result_GanZhi_Gan3",Result_GanZhi_Gan3);
                bundle_charater.putString("Result_GanZhi_GanInfo3",Result_GanZhi_GanInfo3);
                bundle_charater.putString("Result_GanZhi_Zhi3", Result_GanZhi_Zhi3);
                bundle_charater.putInt("count3", count3);

                all += "\n";
                Result_GanZhi_Gan4 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(3).getString("Gan");
                Result_GanZhi_GanInfo4 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(3).getString("GanInfo");
                Result_GanZhi_Zhi4  = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(3).getString("Zhi");
                count4 = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(3).getString("ZhiInfo")).length();
                all = all +  Result_GanZhi_Gan4 + " " +  Result_GanZhi_GanInfo4 + " " + Result_GanZhi_Zhi4 + " ";
                bundle_charater.putString("Result_GanZhi_Gan4",Result_GanZhi_Gan4);
                bundle_charater.putString("Result_GanZhi_GanInfo4",Result_GanZhi_GanInfo4);
                bundle_charater.putString("Result_GanZhi_Zhi4", Result_GanZhi_Zhi4);
                bundle_charater.putInt("count4", count4);

                all += "\n";
                all += "\n";

                for(int i=0;i<count1;i++){
                    Result_GanZhi_ZhiInfo1[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(0).getString("ZhiInfo")).getString(i);
                    all = all + Result_GanZhi_ZhiInfo1[i] + " ";
                    bundle_charater.putString("Result_GanZhi_Gan1_"+Integer.toString(i),Result_GanZhi_ZhiInfo1[i]);
                }
                all += "\n";
                for(int i=0;i<count2;i++){
                    Result_GanZhi_ZhiInfo2[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(1).getString("ZhiInfo")).getString(i);
                    all = all + Result_GanZhi_ZhiInfo2[i] + " ";
                    bundle_charater.putString("Result_GanZhi_Gan2_"+Integer.toString(i),Result_GanZhi_ZhiInfo2[i]);
                }
                all += "\n";
                for(int i=0;i<count3;i++){
                    Result_GanZhi_ZhiInfo3[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(2).getString("ZhiInfo")).getString(i);
                    all = all + Result_GanZhi_ZhiInfo3[i] + " ";
                    bundle_charater.putString("Result_GanZhi_Gan3_"+Integer.toString(i),Result_GanZhi_ZhiInfo3[i]);
                }
                all += "\n";
                for(int i=0;i<count4;i++){
                    Result_GanZhi_ZhiInfo4[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("GanZhi")).getJSONObject(3).getString("ZhiInfo")).getString(i);
                    all = all + Result_GanZhi_ZhiInfo4[i] + " ";
                    bundle_charater.putString("Result_GanZhi_Gan4_"+Integer.toString(i),Result_GanZhi_ZhiInfo4[i]);
                }
                all += "\n";


                Result_Decade_GanZhi_Gan1 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(0).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo1 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(0).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi1 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(0).getString("GanZhi")).getString("Zhi");
                count_de_1 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(0).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan1 + " " +  Result_Decade_GanZhi_GanInfo1 + " " + Result_Decade_GanZhi_Zhi1 + " "+ Integer.toString(count_de_1);
                bundle_charater.putString("Result_Decade_GanZhi_Gan1",Result_Decade_GanZhi_Gan1);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo1",Result_Decade_GanZhi_GanInfo1);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi1", Result_Decade_GanZhi_Zhi1);
                bundle_charater.putInt("count_de_1", count_de_1);
                all += "\n";

                Result_Decade_GanZhi_Gan2 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(1).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo2 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(1).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi2 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(1).getString("GanZhi")).getString("Zhi");
                count_de_2 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(1).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan2 + " " +  Result_Decade_GanZhi_GanInfo2 + " " + Result_Decade_GanZhi_Zhi2 + " "+ Integer.toString(count_de_2);
                bundle_charater.putString("Result_Decade_GanZhi_Gan2",Result_Decade_GanZhi_Gan2);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo2",Result_Decade_GanZhi_GanInfo2);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi2", Result_Decade_GanZhi_Zhi2);
                bundle_charater.putInt("count_de_2", count_de_2);
                all += "\n";

                Result_Decade_GanZhi_Gan3 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(2).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo3 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(2).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi3 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(2).getString("GanZhi")).getString("Zhi");
                count_de_3 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(2).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan3 + " " +  Result_Decade_GanZhi_GanInfo3 + " " + Result_Decade_GanZhi_Zhi3 + " "+ Integer.toString(count_de_3);
                bundle_charater.putString("Result_Decade_GanZhi_Gan3",Result_Decade_GanZhi_Gan3);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo3",Result_Decade_GanZhi_GanInfo3);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi3", Result_Decade_GanZhi_Zhi3);
                bundle_charater.putInt("count_de_3", count_de_3);
                all += "\n";

                Result_Decade_GanZhi_Gan4 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(3).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo4 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(3).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi4 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(3).getString("GanZhi")).getString("Zhi");
                count_de_4 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(3).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan4 + " " +  Result_Decade_GanZhi_GanInfo4 + " " + Result_Decade_GanZhi_Zhi4 + " "+ Integer.toString(count_de_4);
                bundle_charater.putString("Result_Decade_GanZhi_Gan4",Result_Decade_GanZhi_Gan4);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo4",Result_Decade_GanZhi_GanInfo4);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi4", Result_Decade_GanZhi_Zhi4);
                bundle_charater.putInt("count_de_4", count_de_4);
                all += "\n";

                Result_Decade_GanZhi_Gan5 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(4).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo5 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(4).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi5 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(4).getString("GanZhi")).getString("Zhi");
                count_de_5 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(4).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan5 + " " +  Result_Decade_GanZhi_GanInfo5 + " " + Result_Decade_GanZhi_Zhi5 + " "+ Integer.toString(count_de_5);
                bundle_charater.putString("Result_Decade_GanZhi_Gan5",Result_Decade_GanZhi_Gan5);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo5",Result_Decade_GanZhi_GanInfo5);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi5", Result_Decade_GanZhi_Zhi5);
                bundle_charater.putInt("count_de_5", count_de_5);
                all += "\n";

                Result_Decade_GanZhi_Gan6 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(5).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo6 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(5).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi6 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(5).getString("GanZhi")).getString("Zhi");
                count_de_6 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(5).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan6 + " " +  Result_Decade_GanZhi_GanInfo6 + " " + Result_Decade_GanZhi_Zhi6 + " "+ Integer.toString(count_de_6);
                bundle_charater.putString("Result_Decade_GanZhi_Gan6",Result_Decade_GanZhi_Gan6);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo6",Result_Decade_GanZhi_GanInfo6);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi6", Result_Decade_GanZhi_Zhi6);
                bundle_charater.putInt("count_de_6", count_de_6);
                ;
                all += "\n";

                Result_Decade_GanZhi_Gan7 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(6).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo7 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(6).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi7 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(6).getString("GanZhi")).getString("Zhi");
                count_de_7 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(6).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan7 + " " +  Result_Decade_GanZhi_GanInfo7 + " " + Result_Decade_GanZhi_Zhi7 + " "+ Integer.toString(count_de_7);
                bundle_charater.putString("Result_Decade_GanZhi_Gan7",Result_Decade_GanZhi_Gan7);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo7",Result_Decade_GanZhi_GanInfo7);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi7", Result_Decade_GanZhi_Zhi7);
                bundle_charater.putInt("count_de_7", count_de_7);
                all += "\n";

                Result_Decade_GanZhi_Gan8 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(7).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo8 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(7).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi8 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(7).getString("GanZhi")).getString("Zhi");
                count_de_8 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(7).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan8 + " " +  Result_Decade_GanZhi_GanInfo8 + " " + Result_Decade_GanZhi_Zhi8 + " "+ Integer.toString(count_de_8);
                bundle_charater.putString("Result_Decade_GanZhi_Gan8",Result_Decade_GanZhi_Gan8);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo8",Result_Decade_GanZhi_GanInfo8);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi8", Result_Decade_GanZhi_Zhi8);
                bundle_charater.putInt("count_de_8", count_de_8);
                all += "\n";

                Result_Decade_GanZhi_Gan9= new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(8).getString("GanZhi")).getString("Gan");
                Result_Decade_GanZhi_GanInfo9 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(8).getString("GanZhi")).getString("GanInfo");
                Result_Decade_GanZhi_Zhi9 = new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(8).getString("GanZhi")).getString("Zhi");
                count_de_9 = new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(8).getString("GanZhi")).getString("ZhiInfo")).length();
                all = all +  Result_Decade_GanZhi_Gan9 + " " +  Result_Decade_GanZhi_GanInfo9 + " " + Result_Decade_GanZhi_Zhi9 + " "+ Integer.toString(count_de_9);
                bundle_charater.putString("Result_Decade_GanZhi_Gan9",Result_Decade_GanZhi_Gan9);
                bundle_charater.putString("Result_Decade_GanZhi_GanInfo9",Result_Decade_GanZhi_GanInfo9);
                bundle_charater.putString("Result_Decade_GanZhi_Zhi9", Result_Decade_GanZhi_Zhi9);
                bundle_charater.putInt("count_de_9", count_de_9);
                all += "\n";
                all += "\n";

                for(int i=0;i<count_de_1;i++){
                    Result_Decade_GanZhi_ZhiInfo1[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(0).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo1[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo1_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo1[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_2;i++){
                    Result_Decade_GanZhi_ZhiInfo2[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(1).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo2[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo2_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo2[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_3;i++){
                    Result_Decade_GanZhi_ZhiInfo3[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(2).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo3[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo3_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo3[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_4;i++){
                    Result_Decade_GanZhi_ZhiInfo4[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(3).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo4[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo4_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo4[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_5;i++){
                    Result_Decade_GanZhi_ZhiInfo5[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(4).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo5[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo5_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo5[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_6;i++){
                    Result_Decade_GanZhi_ZhiInfo6[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(5).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo6[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo6_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo6[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_7;i++){
                    Result_Decade_GanZhi_ZhiInfo7[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(6).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo7[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo7_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo7[i]);
                }
                all += "\n";

                for(int i=0;i<count_de_8;i++){
                    Result_Decade_GanZhi_ZhiInfo8[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(7).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo8[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo8_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo8[i]);
                }
                all += "\n";
                for(int i=0;i<count_de_9;i++){
                    Result_Decade_GanZhi_ZhiInfo9[i] =new JSONArray(new JSONObject(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Decade")).getJSONObject(8).getString("GanZhi")).getString("ZhiInfo")).getString(i);
                    all = all + Result_Decade_GanZhi_ZhiInfo9[i] + " ";
                    bundle_charater.putString("Result_Decade_GanZhi_ZhiInfo9_"+Integer.toString(i),Result_Decade_GanZhi_ZhiInfo9[i]);
                }
                all += "\n";


                Result_SolarTerm_Name1 =new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("SolarTerm")).getJSONObject(0).getString("Name");
                Result_SolarTerm_Date1 =new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("SolarTerm")).getJSONObject(0).getString("Date");
                Result_SolarTerm_Diff1 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("SolarTerm")).getJSONObject(0).getString("Diff");
                all = all + Result_SolarTerm_Name1  + " " +Result_SolarTerm_Date1 +" "+Result_SolarTerm_Diff1;
                bundle_charater.putString("Result_SolarTerm_Name1",Result_SolarTerm_Name1);
                bundle_charater.putString("Result_SolarTerm_Date1",Result_SolarTerm_Date1);
                bundle_charater.putString("Result_SolarTerm_Diff1",Result_SolarTerm_Diff1);

                Result_SolarTerm_Name2 =new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("SolarTerm")).getJSONObject(1).getString("Name");
                Result_SolarTerm_Date2 =new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("SolarTerm")).getJSONObject(1).getString("Date");
                Result_SolarTerm_Diff2 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("SolarTerm")).getJSONObject(1).getString("Diff");
                all = all + Result_SolarTerm_Name2  + " " +Result_SolarTerm_Date2 +" "+Result_SolarTerm_Diff2;
                bundle_charater.putString("Result_SolarTerm_Name2",Result_SolarTerm_Name2);
                bundle_charater.putString("Result_SolarTerm_Date2",Result_SolarTerm_Date2);
                bundle_charater.putString("Result_SolarTerm_Diff2", Result_SolarTerm_Diff2);



            }catch (JSONException e){
                e.printStackTrace();
            }

            buttonInteract = (getActivity()).findViewById(R.id.button2);
            buttonInteract.setVisibility(View.GONE);

        }


    }

}
