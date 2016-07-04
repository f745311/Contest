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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

/**
 * Created by Boyu on 2016/3/27.
 */
public class MinpanCatchActivity  extends AppCompatActivity {

    public static TextView errorMs;
    private final String filename="account.txt";
    private boolean loginornot;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minpan_catch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_catch);
        setSupportActionBar(toolbar);

        // App Logo
        toolbar.setLogo(R.mipmap.title02);
        // Title
        toolbar.setTitle("紫微命盤");
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
       // getInfo();
        addFragment();
        ReturnButton();
        loginornot = new Write_and_Read(filename,getFilesDir()).ifLogin();
    }
    void addFragment(){
        //建立一個 MyFirstFragment 的實例(Instantiate)
        Fragment newFragment = new MinpanActivity_F();
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
                    if (isNetwork()) {
                        String fromfile = new Write_and_Read(filename, getFilesDir()).ReadFromFile();
                        String[] fromfileArray = fromfile.split("###");
                        Intent intentMember = new Intent(getApplicationContext(), MemberActivity.class);
                        intentMember.putExtra("mail", fromfileArray[2]); //send mail to next activity
                        MinpanCatchActivity.this.startActivity(intentMember);
                        finish();
                    } else {
                        notNetwork_dialogFragment EditNameDialog = new notNetwork_dialogFragment();
                        EditNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                    break;
                case R.id.action_logout://登出
                    new Write_and_Read(filename, getFilesDir()).WritetoFile_clear("");
                    tologin.setClass(MinpanCatchActivity.this, LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
                case R.id.action_login://訪客登入
                    tologin.setClass(MinpanCatchActivity.this, LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(MinpanCatchActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    public void getInfo(Bundle info_bundle) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout) ;
        drawerLayout.closeDrawer(GravityCompat.START);
        //Bundle info_bundle = this.getIntent().getExtras();

            String Reslut_Sex = info_bundle.getString("Reslut_Sex");
            String Reslut_Age = info_bundle.getString("Reslut_Age");
            String Reslut_Birth = info_bundle.getString("Reslut_Birth");
            String Reslut_LunarBirth = info_bundle.getString("Reslut_LunarBirth");
            String Reslut_FateManner = info_bundle.getString("Reslut_FateManner");
            String Reslut_FateSex = info_bundle.getString("Reslut_FateSex");

            TextView Sex_Age = (TextView) findViewById(R.id.Sex_Age);
            Sex_Age.setText("性別 : " + Reslut_Sex + "\n年齡 : " + Reslut_Age);
            TextView Birth = (TextView) findViewById(R.id.Birth);
            Birth.setText("生日(陽曆) :\n " + Reslut_Birth + "\n生日(農曆) :\n" + Reslut_LunarBirth);
            TextView FateManner = (TextView) findViewById(R.id.Fate_Manner);
            FateManner.setText(Reslut_FateManner);
            TextView FateSex = (TextView) findViewById(R.id.Fate_Sex);
            FateSex.setText(Reslut_FateSex);

            //all tpye of onfo
            String Reslut_Houses_Name1 = info_bundle.getString("Reslut_Houses_Name1");
            String Reslut_Houses_Decade1 = info_bundle.getString("Reslut_Houses_Decade1");
            String Reslut_Houses_GanZhi1 = info_bundle.getString("Reslut_Houses_GanZhi1");
            TextView Name_Decade1 = (TextView) findViewById(R.id.name_decade1);
            Name_Decade1.setText(Reslut_Houses_Name1 + "\n" + Reslut_Houses_Decade1);
            TextView GanZhi1 = (TextView) findViewById(R.id.GanZhi1);
            GanZhi1.setText(Reslut_Houses_GanZhi1);

            String Reslut_Houses_Name2 = info_bundle.getString("Reslut_Houses_Name2");
            String Reslut_Houses_Decade2 = info_bundle.getString("Reslut_Houses_Decade2");
            String Reslut_Houses_GanZhi2 = info_bundle.getString("Reslut_Houses_GanZhi2");
            TextView Name_Decade2 = (TextView) findViewById(R.id.name_decade2);
            Name_Decade2.setText(Reslut_Houses_Name2 + "\n" + Reslut_Houses_Decade2);
            TextView GanZhi2 = (TextView) findViewById(R.id.GanZhi2);
            GanZhi2.setText(Reslut_Houses_GanZhi2);

            String Reslut_Houses_Name3 = info_bundle.getString("Reslut_Houses_Name3");
            String Reslut_Houses_Decade3 = info_bundle.getString("Reslut_Houses_Decade3");
            String Reslut_Houses_GanZhi3 = info_bundle.getString("Reslut_Houses_GanZhi3");
            TextView Name_Decade3 = (TextView) findViewById(R.id.name_decade3);
            Name_Decade3.setText(Reslut_Houses_Name3 + "\n" + Reslut_Houses_Decade3);
            TextView GanZhi3 = (TextView) findViewById(R.id.GanZhi3);
            GanZhi3.setText(Reslut_Houses_GanZhi3);

            String Reslut_Houses_Name4 = info_bundle.getString("Reslut_Houses_Name4");
            String Reslut_Houses_Decade4 = info_bundle.getString("Reslut_Houses_Decade4");
            String Reslut_Houses_GanZhi4 = info_bundle.getString("Reslut_Houses_GanZhi4");
            TextView Name_Decade4 = (TextView) findViewById(R.id.name_decade4);
            Name_Decade4.setText(Reslut_Houses_Name4 + "\n" + Reslut_Houses_Decade4);
            TextView GanZhi4 = (TextView) findViewById(R.id.GanZhi4);
            GanZhi4.setText(Reslut_Houses_GanZhi4);

            String Reslut_Houses_Name5 = info_bundle.getString("Reslut_Houses_Name5");
            String Reslut_Houses_Decade5 = info_bundle.getString("Reslut_Houses_Decade5");
            String Reslut_Houses_GanZhi5 = info_bundle.getString("Reslut_Houses_GanZhi5");
            TextView Name_Decade5 = (TextView) findViewById(R.id.name_decade5);
            Name_Decade5.setText(Reslut_Houses_Name5 + "\n" + Reslut_Houses_Decade5);
            TextView GanZhi5 = (TextView) findViewById(R.id.GanZhi5);
            GanZhi5.setText(Reslut_Houses_GanZhi5);

            String Reslut_Houses_Name6 = info_bundle.getString("Reslut_Houses_Name6");
            String Reslut_Houses_Decade6 = info_bundle.getString("Reslut_Houses_Decade6");
            String Reslut_Houses_GanZhi6 = info_bundle.getString("Reslut_Houses_GanZhi6");
            TextView Name_Decade6 = (TextView) findViewById(R.id.name_decade6);
            Name_Decade6.setText(Reslut_Houses_Name6 + "\n" + Reslut_Houses_Decade6);
            TextView GanZhi6 = (TextView) findViewById(R.id.GanZhi6);
            GanZhi6.setText(Reslut_Houses_GanZhi6);

            String Reslut_Houses_Name7 = info_bundle.getString("Reslut_Houses_Name7");
            String Reslut_Houses_Decade7 = info_bundle.getString("Reslut_Houses_Decade7");
            String Reslut_Houses_GanZhi7 = info_bundle.getString("Reslut_Houses_GanZhi7");
            TextView Name_Decade7 = (TextView) findViewById(R.id.name_decade7);
            Name_Decade7.setText(Reslut_Houses_Name7 + "\n" + Reslut_Houses_Decade7);
            TextView GanZhi7 = (TextView) findViewById(R.id.GanZhi7);
            GanZhi7.setText(Reslut_Houses_GanZhi7);

            String Reslut_Houses_Name8 = info_bundle.getString("Reslut_Houses_Name8");
            String Reslut_Houses_Decade8 = info_bundle.getString("Reslut_Houses_Decade8");
            String Reslut_Houses_GanZhi8 = info_bundle.getString("Reslut_Houses_GanZhi8");
            TextView Name_Decade8 = (TextView) findViewById(R.id.name_decade8);
            Name_Decade8.setText(Reslut_Houses_Name8 + "\n" + Reslut_Houses_Decade8);
            TextView GanZhi8 = (TextView) findViewById(R.id.GanZhi8);
            GanZhi8.setText(Reslut_Houses_GanZhi8);

            String Reslut_Houses_Name9 = info_bundle.getString("Reslut_Houses_Name9");
            String Reslut_Houses_Decade9 = info_bundle.getString("Reslut_Houses_Decade9");
            String Reslut_Houses_GanZhi9 = info_bundle.getString("Reslut_Houses_GanZhi9");
            TextView Name_Decade9 = (TextView) findViewById(R.id.name_decade9);
            Name_Decade9.setText(Reslut_Houses_Name9 + "\n" + Reslut_Houses_Decade9);
            TextView GanZhi9 = (TextView) findViewById(R.id.GanZhi9);
            GanZhi9.setText(Reslut_Houses_GanZhi9);

            String Reslut_Houses_Name10 = info_bundle.getString("Reslut_Houses_Name10");
            String Reslut_Houses_Decade10 = info_bundle.getString("Reslut_Houses_Decade10");
            String Reslut_Houses_GanZhi10 = info_bundle.getString("Reslut_Houses_GanZhi10");
            TextView Name_Decade10 = (TextView) findViewById(R.id.name_decade10);
            Name_Decade10.setText(Reslut_Houses_Name10 + "\n" + Reslut_Houses_Decade10);
            TextView GanZhi10 = (TextView) findViewById(R.id.GanZhi10);
            GanZhi10.setText(Reslut_Houses_GanZhi10);

            String Reslut_Houses_Name11 = info_bundle.getString("Reslut_Houses_Name11");
            String Reslut_Houses_Decade11 = info_bundle.getString("Reslut_Houses_Decade11");
            String Reslut_Houses_GanZhi11 = info_bundle.getString("Reslut_Houses_GanZhi11");
            TextView Name_Decade11 = (TextView) findViewById(R.id.name_decade11);
            Name_Decade11.setText(Reslut_Houses_Name11 + "\n" + Reslut_Houses_Decade11);
            TextView GanZhi11 = (TextView) findViewById(R.id.GanZhi11);
            GanZhi11.setText(Reslut_Houses_GanZhi11);

            String Reslut_Houses_Name12 = info_bundle.getString("Reslut_Houses_Name12");
            String Reslut_Houses_Decade12 = info_bundle.getString("Reslut_Houses_Decade12");
            String Reslut_Houses_GanZhi12 = info_bundle.getString("Reslut_Houses_GanZhi12");
            TextView Name_Decade12 = (TextView) findViewById(R.id.name_decade12);
            Name_Decade12.setText(Reslut_Houses_Name12 + "\n" + Reslut_Houses_Decade12);
            TextView GanZhi12 = (TextView) findViewById(R.id.GanZhi12);
            GanZhi12.setText(Reslut_Houses_GanZhi12);

            // all type of count
            Integer count1 = info_bundle.getInt("count1");
            Integer count2 = info_bundle.getInt("count2");
            Integer count3 = info_bundle.getInt("count3");
            Integer count4 = info_bundle.getInt("count4");
            Integer count5 = info_bundle.getInt("count5");
            Integer count6 = info_bundle.getInt("count6");
            Integer count7 = info_bundle.getInt("count7");
            Integer count8 = info_bundle.getInt("count8");
            Integer count9 = info_bundle.getInt("count9");
            Integer count10 = info_bundle.getInt("count10");
            Integer count11 = info_bundle.getInt("count11");
            Integer count12 = info_bundle.getInt("count12");

            // all type of result
            String[] Reslut_Houses_Stars_Name1 = new String[6];
            String[] Reslut_Houses_Stars_Flag1 = new String[6];

            String[] Reslut_Houses_Stars_Name2 = new String[6];
            String[] Reslut_Houses_Stars_Flag2 = new String[6];

            String[] Reslut_Houses_Stars_Name3 = new String[6];
            String[] Reslut_Houses_Stars_Flag3 = new String[6];

            String[] Reslut_Houses_Stars_Name4 = new String[6];
            String[] Reslut_Houses_Stars_Flag4 = new String[6];

            String[] Reslut_Houses_Stars_Name5 = new String[6];
            String[] Reslut_Houses_Stars_Flag5 = new String[6];

            String[] Reslut_Houses_Stars_Name6 = new String[6];
            String[] Reslut_Houses_Stars_Flag6 = new String[6];

            String[] Reslut_Houses_Stars_Name7 = new String[6];
            String[] Reslut_Houses_Stars_Flag7 = new String[6];

            String[] Reslut_Houses_Stars_Name8 = new String[6];
            String[] Reslut_Houses_Stars_Flag8 = new String[6];

            String[] Reslut_Houses_Stars_Name9 = new String[6];
            String[] Reslut_Houses_Stars_Flag9 = new String[6];

            String[] Reslut_Houses_Stars_Name10 = new String[6];
            String[] Reslut_Houses_Stars_Flag10 = new String[6];

            String[] Reslut_Houses_Stars_Name11 = new String[6];
            String[] Reslut_Houses_Stars_Flag11 = new String[6];

            String[] Reslut_Houses_Stars_Name12 = new String[6];
            String[] Reslut_Houses_Stars_Flag12 = new String[6];

            String nu = "null";
            String star1 = "";
            for (int i = 0; i < count1; i++) {
                Reslut_Houses_Stars_Name1[i] = info_bundle.getString("Reslut_Houses_Stars_Name1" + Integer.toString(i));
                star1 += Reslut_Houses_Stars_Name1[i];
                Reslut_Houses_Stars_Flag1[i] = info_bundle.getString("Reslut_Houses_Stars_Flag1" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag1[i].equals(nu)) {
                    star1 += " ";
                    star1 += Reslut_Houses_Stars_Flag1[i];
                } else {
                    star1 += "     ";
                }
                if (i != count1 - 1) {
                    star1 += "\n";
                }
            }
            TextView star_1 = (TextView) findViewById(R.id.typestar1);
            star_1.setText(star1);

            String star2 = "";
            for (int i = 0; i < count2; i++) {
                Reslut_Houses_Stars_Name2[i] = info_bundle.getString("Reslut_Houses_Stars_Name2" + Integer.toString(i));
                star2 += Reslut_Houses_Stars_Name2[i];
                Reslut_Houses_Stars_Flag2[i] = info_bundle.getString("Reslut_Houses_Stars_Flag2" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag2[i].equals(nu)) {
                    star2 += " ";
                    star2 += Reslut_Houses_Stars_Flag2[i];
                } else {
                    star2 += "     ";
                }
                if (i != count2 - 1) {
                    star2 += "\n";
                }
            }
            TextView star_2 = (TextView) findViewById(R.id.typestar2);
            star_2.setText(star2);

            String star3 = "";
            for (int i = 0; i < count3; i++) {
                Reslut_Houses_Stars_Name3[i] = info_bundle.getString("Reslut_Houses_Stars_Name3" + Integer.toString(i));
                star3 += Reslut_Houses_Stars_Name3[i];
                Reslut_Houses_Stars_Flag3[i] = info_bundle.getString("Reslut_Houses_Stars_Flag3" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag3[i].equals(nu)) {
                    star3 += " ";
                    star3 += Reslut_Houses_Stars_Flag3[i];
                } else {
                    star3 += "     ";
                }
                if (i != count3 - 1) {
                    star3 += "\n";
                }
            }
            TextView star_3 = (TextView) findViewById(R.id.typestar3);
            star_3.setText(star3);

            String star4 = "";
            for (int i = 0; i < count4; i++) {
                Reslut_Houses_Stars_Name4[i] = info_bundle.getString("Reslut_Houses_Stars_Name4" + Integer.toString(i));
                star4 += Reslut_Houses_Stars_Name4[i];
                Reslut_Houses_Stars_Flag4[i] = info_bundle.getString("Reslut_Houses_Stars_Flag4" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag4[i].equals(nu)) {
                    star4 += " ";
                    star4 += Reslut_Houses_Stars_Flag4[i];
                } else {
                    star4 += "     ";
                }
                if (i != count4 - 1) {
                    star4 += "\n";
                }
            }
            TextView star_4 = (TextView) findViewById(R.id.typestar4);
            star_4.setText(star4);

            String star5 = "";
            for (int i = 0; i < count5; i++) {
                Reslut_Houses_Stars_Name5[i] = info_bundle.getString("Reslut_Houses_Stars_Name5" + Integer.toString(i));
                star5 += Reslut_Houses_Stars_Name5[i];
                Reslut_Houses_Stars_Flag5[i] = info_bundle.getString("Reslut_Houses_Stars_Flag5" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag5[i].equals(nu)) {
                    star5 += " ";
                    star5 += Reslut_Houses_Stars_Flag5[i];
                } else {
                    star5 += "     ";
                }
                if (i != count5 - 1) {
                    star5 += "\n";
                }
            }
            TextView star_5 = (TextView) findViewById(R.id.typestar5);
            star_5.setText(star5);

            String star6 = "";
            for (int i = 0; i < count6; i++) {
                Reslut_Houses_Stars_Name6[i] = info_bundle.getString("Reslut_Houses_Stars_Name6" + Integer.toString(i));
                star6 += Reslut_Houses_Stars_Name6[i];
                Reslut_Houses_Stars_Flag6[i] = info_bundle.getString("Reslut_Houses_Stars_Flag6" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag6[i].equals(nu)) {
                    star6 += " ";
                    star6 += Reslut_Houses_Stars_Flag6[i];
                } else {
                    star6 += "     ";
                }
                if (i != count6 - 1) {
                    star6 += "\n";
                }
            }
            TextView star_6 = (TextView) findViewById(R.id.typestar6);
            star_6.setText(star6);

            String star7 = "";
            for (int i = 0; i < count7; i++) {
                Reslut_Houses_Stars_Name7[i] = info_bundle.getString("Reslut_Houses_Stars_Name7" + Integer.toString(i));
                star7 += Reslut_Houses_Stars_Name7[i];
                Reslut_Houses_Stars_Flag7[i] = info_bundle.getString("Reslut_Houses_Stars_Flag7" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag7[i].equals(nu)) {
                    star7 += " ";
                    star7 += Reslut_Houses_Stars_Flag7[i];
                } else {
                    star7 += "     ";
                }
                if (i != count7 - 1) {
                    star7 += "\n";
                }
            }
            TextView star_7 = (TextView) findViewById(R.id.typestar7);
            star_7.setText(star7);

            String star8 = "";
            for (int i = 0; i < count8; i++) {
                Reslut_Houses_Stars_Name8[i] = info_bundle.getString("Reslut_Houses_Stars_Name8" + Integer.toString(i));
                star8 += Reslut_Houses_Stars_Name8[i];
                Reslut_Houses_Stars_Flag8[i] = info_bundle.getString("Reslut_Houses_Stars_Flag8" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag8[i].equals(nu)) {
                    star8 += " ";
                    star8 += Reslut_Houses_Stars_Flag8[i];
                } else {
                    star8 += "     ";
                }
                if (i != count8 - 1) {
                    star8 += "\n";
                }
            }
            TextView star_8 = (TextView) findViewById(R.id.typestar8);
            star_8.setText(star8);

            String star9 = "";
            for (int i = 0; i < count9; i++) {
                Reslut_Houses_Stars_Name9[i] = info_bundle.getString("Reslut_Houses_Stars_Name9" + Integer.toString(i));
                star9 += Reslut_Houses_Stars_Name9[i];
                Reslut_Houses_Stars_Flag9[i] = info_bundle.getString("Reslut_Houses_Stars_Flag9" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag9[i].equals(nu)) {
                    star9 += " ";
                    star9 += Reslut_Houses_Stars_Flag9[i];
                } else {
                    star9 += "     ";
                }
                if (i != count9 - 1) {
                    star9 += "\n";
                }
            }
            TextView star_9 = (TextView) findViewById(R.id.typestar9);
            star_9.setText(star9);

            String star10 = "";
            for (int i = 0; i < count10; i++) {
                Reslut_Houses_Stars_Name10[i] = info_bundle.getString("Reslut_Houses_Stars_Name10" + Integer.toString(i));
                star10 += Reslut_Houses_Stars_Name10[i];
                Reslut_Houses_Stars_Flag10[i] = info_bundle.getString("Reslut_Houses_Stars_Flag10" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag10[i].equals(nu)) {
                    star10 += " ";
                    star10 += Reslut_Houses_Stars_Flag10[i];
                } else {
                    star10 += "     ";
                }
                if (i != count10 - 1) {
                    star10 += "\n";
                }
            }
            TextView star_10 = (TextView) findViewById(R.id.typestar10);
            star_10.setText(star10);

            String star11 = "";
            for (int i = 0; i < count11; i++) {
                Reslut_Houses_Stars_Name11[i] = info_bundle.getString("Reslut_Houses_Stars_Name11" + Integer.toString(i));
                star11 += Reslut_Houses_Stars_Name11[i];
                Reslut_Houses_Stars_Flag11[i] = info_bundle.getString("Reslut_Houses_Stars_Flag11" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag11[i].equals(nu)) {
                    star11 += " ";
                    star11 += Reslut_Houses_Stars_Flag11[i];
                } else {
                    star11 += "     ";
                }
                if (i != count11 - 1) {
                    star11 += "\n";
                }
            }
            TextView star_11 = (TextView) findViewById(R.id.typestar11);
            star_11.setText(star11);

            String star12 = "";
            for (int i = 0; i < count12; i++) {
                Reslut_Houses_Stars_Name12[i] = info_bundle.getString("Reslut_Houses_Stars_Name12" + Integer.toString(i));
                star12 += Reslut_Houses_Stars_Name12[i];
                Reslut_Houses_Stars_Flag12[i] = info_bundle.getString("Reslut_Houses_Stars_Flag12" + Integer.toString(i));
                if (!Reslut_Houses_Stars_Flag12[i].equals(nu)) {
                    star12 += " ";
                    star12 += Reslut_Houses_Stars_Flag12[i];
                } else {
                    star12 += "     ";
                }
                if (i != count12 - 1) {
                    star12 += "\n";
                }
            }
            TextView star_12 = (TextView) findViewById(R.id.typestar12);
            star_12.setText(star12);

    }

    public void ReturnButton(){
        Button ReturnButton = (Button)findViewById(R.id.ButtonReturn);
        ReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout) ;
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

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



/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static class MinpanActivity_F extends Fragment {


        Bundle bundle1 = new Bundle();
        View buttonInteract;
        private Button button_Submit2,buttonReturn;
        private RadioGroup Gruop_Sex2, Gruop_YearType;
        EditText input_year, input_month, input_day;
        String which_sex = "女";//性別，預設為女
        String which_yeartype = "";//記錄哪一個(西元or民國or農曆)
        RadioButton west, guo, non, male, female;
        int count;
        String hour = "", s_year = "", s_month = "", s_day = "";
        private String[] hour_list = {"0:00~0:59(子時)", "1:00~1:59(丑時)", "2:00~2:59(丑時)", "3:00~3:59(寅時)", "4:00~4:59(寅時)", "5:00~5:59(卯時)",
                "6:00~6:59(卯時)", "7:00~7:59(辰時)", "8:00~8:59(辰時)", "9:00~9:59(巳時)", "10:00~10:59(巳時)", "11:00~11:59(午時)",
                "12:00~12:59(午時)", "13:00~13:59(未時)", "14:00~14:59(未時)", "15:00~15:59(申時)", "16:00~16:59(申時)", "17:00~17:59(酉時)",
                "18:00~18:59(酉時)", "19:00~19:59(戌時)", "20:00~20:59(戌時)", "21:00~21:59(亥時)", "22:00~22:59(亥時)", "23:00~23:59(子時)",};

        private String[] year_list = {"1901", "1902", "1903", "1904", "1905", "1906", "1907", "1908", "1909",
                "1910", "1911", "1912", "1913", "1914", "1915", "1916", "1917", "1918", "1919",
                "1920", "1921", "1922", "1923", "1924", "1925", "1926", "1927", "1928", "1929",
                "1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937", "1938", "1939",
                "1940", "1941", "1942", "1943", "1944", "1945", "1946", "1947", "1948", "1949",
                "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959",
                "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969",
                "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979",
                "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989",
                "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999",
                "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
                "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019",
                "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029"};

        private String[] month_list = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

        private String[] day_list = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31"};
        private ArrayAdapter<String> listAdapter, listAdapter_year, listAdapter_month, listAdapter_day;
        private Spinner spinner, spinner_year, spinner_month, spinner_day;
        public  TextView errorMs;
        private final String filename = "account.txt";
        private boolean loginornot;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.minpan, container, false);
            ButtonSummit(v);
            loginornot = new Write_and_Read(filename, getActivity().getFilesDir()).ifLogin();

            //2016/4/4 update by 均 get from file
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
                spinner_year = (Spinner) v.findViewById(R.id.input_minpan_year);
                listAdapter_year = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, year_list);
                listAdapter_year.setDropDownViewResource(R.layout.myspinner);
                spinner_year.setAdapter(listAdapter_year);
                spinner_year.setSelection(yearPosition);
                s_year = year_list[yearPosition]; //傳入value
                //設定月的初始值
                spinner_month = (Spinner) v.findViewById(R.id.input_minpan_month);
                listAdapter_month = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, month_list);
                listAdapter_month.setDropDownViewResource(R.layout.myspinner);
                spinner_month.setAdapter(listAdapter_month);
                spinner_month.setSelection(monthPosition);
                s_month = month_list[monthPosition]; //傳入value
                //設定日的初始值
                spinner_day = (Spinner) v.findViewById(R.id.input_minpan_day);
                listAdapter_day = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, day_list);
                listAdapter_day.setDropDownViewResource(R.layout.myspinner);
                spinner_day.setAdapter(listAdapter_day);
                spinner_day.setSelection(dayPosition);
                s_day = day_list[dayPosition]; //傳入value
                //設定性別初始值
                male = (RadioButton) v.findViewById(R.id.radio_Male2);
                female = (RadioButton) v.findViewById(R.id.radio_Female2);
                if (fromfileArray[4].equals("1")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
                //設定時間的初始值
                spinner = (Spinner) v.findViewById(R.id.minpan_spinner);
                listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, hour_list);
                listAdapter.setDropDownViewResource(R.layout.myspinner);
                spinner.setAdapter(listAdapter);
                spinner.setSelection(fileHour);
                hour = hour_list[fileHour];
            }else{
                //設定年的初始值
                spinner_year = (Spinner) v.findViewById(R.id.input_minpan_year);
                listAdapter_year = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, year_list);
                listAdapter_year.setDropDownViewResource(R.layout.myspinner);
                spinner_year.setAdapter(listAdapter_year);
                spinner_year.setSelection(90);
                s_year = year_list[90]; //傳入value
            }
            return v;
        }


        void ButtonSummit(final View v) {
            button_Submit2 = (Button) v.findViewById(R.id.button_minpan_Submit2);


            // 時辰
            spinner = (Spinner) v.findViewById(R.id.minpan_spinner);
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
            spinner_year = (Spinner) v.findViewById(R.id.input_minpan_year);
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
            spinner_month = (Spinner) v.findViewById(R.id.input_minpan_month);
            listAdapter_month = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, month_list);
            listAdapter_month.setDropDownViewResource(R.layout.myspinner);
            spinner_month.setAdapter(listAdapter_month);
            spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    s_month = month_list[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            // 日
            spinner_day = (Spinner) v.findViewById(R.id.input_minpan_day);
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


            button_Submit2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vv) {
                    if (isNetwork()) {
                        errorMs = (TextView) v.findViewById(R.id.textViewError);


                        //radioGroup
                        Gruop_Sex2 = (RadioGroup) v.findViewById(R.id.Gruop_minpan_Sex2);
                        int select_id = Gruop_Sex2.getCheckedRadioButtonId();
                        Gruop_YearType = (RadioGroup) v.findViewById(R.id.Gruo_minpanp_YearType);
                        int select_type = Gruop_YearType.getCheckedRadioButtonId();//記錄選了哪一個(西元or民國or農曆)
                        //editText_Question2.setText(String.valueOf(select_type));//測試用

                        west = (RadioButton) v.findViewById(R.id.radio_west);
                        guo = (RadioButton) v.findViewById(R.id.radio_guo);
                        non = (RadioButton) v.findViewById(R.id.radio_non);

                        male = (RadioButton) v.findViewById(R.id.radio_Male2);
                        female = (RadioButton) v.findViewById(R.id.radio_Female2);

                        // 問題輸入轉換為string
                        if (select_id == female.getId()) {
                            which_sex = "0"; //API上，女 = 0
                        } else {
                            which_sex = "1"; //API上，男 = 0
                        }

                        if (select_type == west.getId()) {
                            which_yeartype = "0"; //API上，西元 = 0
                        } else if (select_id == guo.getId()) {
                            which_yeartype = "1"; //API上，國曆 = 1
                        } else {
                            which_yeartype = "2"; //API上，農曆 = 2
                        }
                        if (!s_year.equals("") && !s_month.equals("") && !s_day.equals("")) {
                            if (time_check(s_year, s_month, s_day)) {
                                // 產生對映的url，使用Catch_say88_API_info函式
                                errorMs.setText("資料讀取中，請稍候.....");
                                String url = Catch_say88_API_info(which_yeartype, s_year, s_month, s_day, hour, which_sex);
                                //產生異構Task，因為網路部分不能在main裡面進行，接著執行
                                RequestTask request = new RequestTask();
                                request.execute(url);

                            } else {
                                whenDateError_dialogFragment editNameDialog = new whenDateError_dialogFragment();
                                editNameDialog.show(getFragmentManager(), "EditNameDialog");
                                errorMs.setText("輸入時間有誤");
                            }
                        } else {
                            whenDateError_dialogFragment editNameDialog = new whenDateError_dialogFragment();
                            editNameDialog.show(getFragmentManager(), "EditNameDialog");
                            errorMs.setText("輸入時間有誤");
                        }
                    } else {
                        notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
                        editNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                }
            });
        }

        public boolean time_check(String s_year, String s_month, String s_day) {
            int i_year, i_month, i_day;
            i_year = Integer.parseInt(s_year);
            i_month = Integer.parseInt(s_month);
            i_day = Integer.parseInt(s_day);

            if (i_year >= 1900 && i_year < 2032 && i_month > 0 && i_month < 13 && i_day > 0) {
                if (i_year % 4 == 0 && i_month == 2 && i_day == 29) {
                    return true;
                } else if (i_month == 1 && i_day <= 31) {
                    return true;
                } else if (i_month == 2 && i_day <= 28) {
                    return true;
                } else if (i_month == 3 && i_day <= 31) {
                    return true;
                } else if (i_month == 4 && i_day <= 30) {
                    return true;
                } else if (i_month == 5 && i_day <= 31) {
                    return true;
                } else if (i_month == 6 && i_day <= 30) {
                    return true;
                } else if (i_month == 7 && i_day <= 31) {
                    return true;
                } else if (i_month == 8 && i_day <= 31) {
                    return true;
                } else if (i_month == 9 && i_day <= 30) {
                    return true;
                } else if (i_month == 10 && i_day <= 31) {
                    return true;
                } else if (i_month == 11 && i_day <= 30) {
                    return true;
                } else if (i_month == 12 && i_day <= 31) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public String Catch_say88_API_info(String birthType, String Year, String Month, String Day, String Hour, String Sex) {
            // token就是識別證
            String url = "http://newtest.88say.com/Api/product/Unit306.aspx?";
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

        private boolean isNetwork() {
            boolean result = false;
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connManager.getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                result = false;
            } else {
                if (!info.isAvailable()) {
                    result = false;
                } else {
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
            protected void onPostExecute(String text) {  // doInBackground的結果會傳至這個涵式

                JsonTrasnfer_output(text);
            }

            @Override
            protected void onProgressUpdate(String... result) {

            }
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public void JsonTrasnfer_output(String jsonInput) {
            String TxnCode = "";
            String TxnMsg = "";
            String Reslut_Sex = "";
            String Reslut_Age = "";
            String Reslut_Birth = "";
            String Reslut_LunarBirth = "";
            String Reslut_FateManner = "";
            String Reslut_FateSex = "";
            //12個框格
            //1
            String Reslut_Houses_Name1 = "";
            String Reslut_Houses_Decade1 = "";
            String Reslut_Houses_GanZhi1 = "";
            //2
            String Reslut_Houses_Name2 = "";
            String Reslut_Houses_Decade2 = "";
            String Reslut_Houses_GanZhi2 = "";
            //3
            String Reslut_Houses_Name3 = "";
            String Reslut_Houses_Decade3 = "";
            String Reslut_Houses_GanZhi3 = "";
            //4
            String Reslut_Houses_Name4 = "";
            String Reslut_Houses_Decade4 = "";
            String Reslut_Houses_GanZhi4 = "";
            //5
            String Reslut_Houses_Name5 = "";
            String Reslut_Houses_Decade5 = "";
            String Reslut_Houses_GanZhi5 = "";
            //6
            String Reslut_Houses_Name6 = "";
            String Reslut_Houses_Decade6 = "";
            String Reslut_Houses_GanZhi6 = "";
            //7
            String Reslut_Houses_Name7 = "";
            String Reslut_Houses_Decade7 = "";
            String Reslut_Houses_GanZhi7 = "";
            //8
            String Reslut_Houses_Name8 = "";
            String Reslut_Houses_Decade8 = "";
            String Reslut_Houses_GanZhi8 = "";
            //9
            String Reslut_Houses_Name9 = "";
            String Reslut_Houses_Decade9 = "";
            String Reslut_Houses_GanZhi9 = "";
            //10
            String Reslut_Houses_Name10 = "";
            String Reslut_Houses_Decade10 = "";
            String Reslut_Houses_GanZhi10 = "";
            //11
            String Reslut_Houses_Name11 = "";
            String Reslut_Houses_Decade11 = "";
            String Reslut_Houses_GanZhi11 = "";
            //12
            String Reslut_Houses_Name12 = "";
            String Reslut_Houses_Decade12 = "";
            String Reslut_Houses_GanZhi12 = "";
            // 12個匡格
            // 1
            String[] Reslut_Houses_Stars_Name1 = new String[6];
            String[] Reslut_Houses_Stars_Flag1 = new String[6];
            //2
            String[] Reslut_Houses_Stars_Name2 = new String[6];
            String[] Reslut_Houses_Stars_Flag2 = new String[6];
            //3
            String[] Reslut_Houses_Stars_Name3 = new String[6];
            String[] Reslut_Houses_Stars_Flag3 = new String[6];
            //4
            String[] Reslut_Houses_Stars_Name4 = new String[6];
            String[] Reslut_Houses_Stars_Flag4 = new String[6];
            //5
            String[] Reslut_Houses_Stars_Name5 = new String[6];
            String[] Reslut_Houses_Stars_Flag5 = new String[6];
            //6
            String[] Reslut_Houses_Stars_Name6 = new String[6];
            String[] Reslut_Houses_Stars_Flag6 = new String[6];
            //7
            String[] Reslut_Houses_Stars_Name7 = new String[6];
            String[] Reslut_Houses_Stars_Flag7 = new String[6];
            //8
            String[] Reslut_Houses_Stars_Name8 = new String[6];
            String[] Reslut_Houses_Stars_Flag8 = new String[6];
            //9
            String[] Reslut_Houses_Stars_Name9 = new String[6];
            String[] Reslut_Houses_Stars_Flag9 = new String[6];
            //10
            String[] Reslut_Houses_Stars_Name10 = new String[6];
            String[] Reslut_Houses_Stars_Flag10 = new String[6];
            //11
            String[] Reslut_Houses_Stars_Name11 = new String[6];
            String[] Reslut_Houses_Stars_Flag11 = new String[6];
            //12
            String[] Reslut_Houses_Stars_Name12 = new String[6];
            String[] Reslut_Houses_Stars_Flag12 = new String[6];


            try {
                TxnCode = new JSONObject(jsonInput).getString("TxnCode");
                TxnMsg = new JSONObject(jsonInput).getString("TxnMsg");
                Reslut_Sex = new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Sex");
                Reslut_Age = new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Age");
                Reslut_Birth = new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Birth");
                Reslut_LunarBirth = new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("LunarBirth");
                Reslut_FateManner = new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("FateManner");
                Reslut_FateSex = new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("FateSex");

                bundle1.putString("Reslut_Sex", Reslut_Sex);
                bundle1.putString("Reslut_Age", Reslut_Age);
                bundle1.putString("Reslut_Birth", Reslut_Birth);
                bundle1.putString("Reslut_LunarBirth", Reslut_LunarBirth);
                bundle1.putString("Reslut_FateManner", Reslut_FateManner);
                bundle1.putString("Reslut_FateSex", Reslut_FateSex);

                //1
                Reslut_Houses_Name1 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(0).getString("Name");
                Reslut_Houses_Decade1 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(0).getString("Decade");
                Reslut_Houses_GanZhi1 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(0).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name1", Reslut_Houses_Name1);
                bundle1.putString("Reslut_Houses_Decade1", Reslut_Houses_Decade1);
                bundle1.putString("Reslut_Houses_GanZhi1", Reslut_Houses_GanZhi1);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(0).getString("Stars")).length();
                bundle1.putInt("count1", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name1[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(0).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag1[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(0).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name1" + Integer.toString(i), Reslut_Houses_Stars_Name1[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag1" + Integer.toString(i), Reslut_Houses_Stars_Flag1[i]);
                }
                //2
                Reslut_Houses_Name2 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(1).getString("Name");
                Reslut_Houses_Decade2 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(1).getString("Decade");
                Reslut_Houses_GanZhi2 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(1).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name2", Reslut_Houses_Name2);
                bundle1.putString("Reslut_Houses_Decade2", Reslut_Houses_Decade2);
                bundle1.putString("Reslut_Houses_GanZhi2", Reslut_Houses_GanZhi2);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(1).getString("Stars")).length();
                bundle1.putInt("count2", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name2[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(1).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag2[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(1).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name2" + Integer.toString(i), Reslut_Houses_Stars_Name2[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag2" + Integer.toString(i), Reslut_Houses_Stars_Flag2[i]);
                }
                //3
                Reslut_Houses_Name3 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(2).getString("Name");
                Reslut_Houses_Decade3 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(2).getString("Decade");
                Reslut_Houses_GanZhi3 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(2).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name3", Reslut_Houses_Name3);
                bundle1.putString("Reslut_Houses_Decade3", Reslut_Houses_Decade3);
                bundle1.putString("Reslut_Houses_GanZhi3", Reslut_Houses_GanZhi3);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(2).getString("Stars")).length();
                bundle1.putInt("count3", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name3[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(2).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag3[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(2).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name3" + Integer.toString(i), Reslut_Houses_Stars_Name3[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag3" + Integer.toString(i), Reslut_Houses_Stars_Flag3[i]);
                }
                //4
                Reslut_Houses_Name4 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(3).getString("Name");
                Reslut_Houses_Decade4 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(3).getString("Decade");
                Reslut_Houses_GanZhi4 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(3).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name4", Reslut_Houses_Name4);
                bundle1.putString("Reslut_Houses_Decade4", Reslut_Houses_Decade4);
                bundle1.putString("Reslut_Houses_GanZhi4", Reslut_Houses_GanZhi4);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(3).getString("Stars")).length();
                bundle1.putInt("count4", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name4[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(3).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag4[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(3).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name4" + Integer.toString(i), Reslut_Houses_Stars_Name4[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag4" + Integer.toString(i), Reslut_Houses_Stars_Flag4[i]);
                }
                //5
                Reslut_Houses_Name5 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(4).getString("Name");
                Reslut_Houses_Decade5 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(4).getString("Decade");
                Reslut_Houses_GanZhi5 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(4).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name5", Reslut_Houses_Name5);
                bundle1.putString("Reslut_Houses_Decade5", Reslut_Houses_Decade5);
                bundle1.putString("Reslut_Houses_GanZhi5", Reslut_Houses_GanZhi5);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(4).getString("Stars")).length();
                bundle1.putInt("count5", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name5[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(4).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag5[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(4).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name5" + Integer.toString(i), Reslut_Houses_Stars_Name5[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag5" + Integer.toString(i), Reslut_Houses_Stars_Flag5[i]);
                }
                //6
                Reslut_Houses_Name6 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(5).getString("Name");
                Reslut_Houses_Decade6 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(5).getString("Decade");
                Reslut_Houses_GanZhi6 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(5).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name6", Reslut_Houses_Name6);
                bundle1.putString("Reslut_Houses_Decade6", Reslut_Houses_Decade6);
                bundle1.putString("Reslut_Houses_GanZhi6", Reslut_Houses_GanZhi6);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(5).getString("Stars")).length();
                bundle1.putInt("count6", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name6[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(5).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag6[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(5).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name6" + Integer.toString(i), Reslut_Houses_Stars_Name6[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag6" + Integer.toString(i), Reslut_Houses_Stars_Flag6[i]);
                }
                //7
                Reslut_Houses_Name7 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(6).getString("Name");
                Reslut_Houses_Decade7 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(6).getString("Decade");
                Reslut_Houses_GanZhi7 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(6).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name7", Reslut_Houses_Name7);
                bundle1.putString("Reslut_Houses_Decade7", Reslut_Houses_Decade7);
                bundle1.putString("Reslut_Houses_GanZhi7", Reslut_Houses_GanZhi7);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(6).getString("Stars")).length();
                bundle1.putInt("count7", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name7[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(6).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag7[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(6).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name7" + Integer.toString(i), Reslut_Houses_Stars_Name7[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag7" + Integer.toString(i), Reslut_Houses_Stars_Flag7[i]);
                }
                //8
                Reslut_Houses_Name8 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(7).getString("Name");
                Reslut_Houses_Decade8 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(7).getString("Decade");
                Reslut_Houses_GanZhi8 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(7).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name8", Reslut_Houses_Name8);
                bundle1.putString("Reslut_Houses_Decade8", Reslut_Houses_Decade8);
                bundle1.putString("Reslut_Houses_GanZhi8", Reslut_Houses_GanZhi8);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(7).getString("Stars")).length();
                bundle1.putInt("count8", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name8[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(7).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag8[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(7).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name8" + Integer.toString(i), Reslut_Houses_Stars_Name8[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag8" + Integer.toString(i), Reslut_Houses_Stars_Flag8[i]);
                }
                //9
                Reslut_Houses_Name9 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(8).getString("Name");
                Reslut_Houses_Decade9 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(8).getString("Decade");
                Reslut_Houses_GanZhi9 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(8).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name9", Reslut_Houses_Name9);
                bundle1.putString("Reslut_Houses_Decade9", Reslut_Houses_Decade9);
                bundle1.putString("Reslut_Houses_GanZhi9", Reslut_Houses_GanZhi9);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(8).getString("Stars")).length();
                bundle1.putInt("count9", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name9[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(8).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag9[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(8).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name9" + Integer.toString(i), Reslut_Houses_Stars_Name9[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag9" + Integer.toString(i), Reslut_Houses_Stars_Flag9[i]);
                }
                //10
                Reslut_Houses_Name10 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(9).getString("Name");
                Reslut_Houses_Decade10 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(9).getString("Decade");
                Reslut_Houses_GanZhi10 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(9).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name10", Reslut_Houses_Name10);
                bundle1.putString("Reslut_Houses_Decade10", Reslut_Houses_Decade10);
                bundle1.putString("Reslut_Houses_GanZhi10", Reslut_Houses_GanZhi10);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(9).getString("Stars")).length();
                bundle1.putInt("count10", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name10[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(9).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag10[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(9).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name10" + Integer.toString(i), Reslut_Houses_Stars_Name10[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag10" + Integer.toString(i), Reslut_Houses_Stars_Flag10[i]);
                }
                //11
                Reslut_Houses_Name11 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(10).getString("Name");
                Reslut_Houses_Decade11 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(10).getString("Decade");
                Reslut_Houses_GanZhi11 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(10).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name11", Reslut_Houses_Name11);
                bundle1.putString("Reslut_Houses_Decade11", Reslut_Houses_Decade11);
                bundle1.putString("Reslut_Houses_GanZhi11", Reslut_Houses_GanZhi11);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(10).getString("Stars")).length();
                bundle1.putInt("count11", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name11[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(10).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag11[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(10).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name11" + Integer.toString(i), Reslut_Houses_Stars_Name11[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag11" + Integer.toString(i), Reslut_Houses_Stars_Flag11[i]);
                }
                //12
                Reslut_Houses_Name12 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(11).getString("Name");
                Reslut_Houses_Decade12 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(11).getString("Decade");
                Reslut_Houses_GanZhi12 = new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(11).getString("GanZhi");
                bundle1.putString("Reslut_Houses_Name12", Reslut_Houses_Name12);
                bundle1.putString("Reslut_Houses_Decade12", Reslut_Houses_Decade12);
                bundle1.putString("Reslut_Houses_GanZhi12", Reslut_Houses_GanZhi12);

                count = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(11).getString("Stars")).length();
                bundle1.putInt("count12", count);
                for (int i = 0; i < count; i++) {
                    Reslut_Houses_Stars_Name12[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(11).getString("Stars")).getJSONObject(i).getString("Name");
                    Reslut_Houses_Stars_Flag12[i] = new JSONArray(new JSONArray(new JSONObject(new JSONObject(jsonInput).getString("Result")).getString("Houses")).getJSONObject(11).getString("Stars")).getJSONObject(i).getString("Flag");
                    bundle1.putString("Reslut_Houses_Stars_Name12" + Integer.toString(i), Reslut_Houses_Stars_Name12[i]);
                    bundle1.putString("Reslut_Houses_Stars_Flag12" + Integer.toString(i), Reslut_Houses_Stars_Flag12[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            buttonInteract =  (getActivity()).findViewById(R.id.ButtonReturn);
            buttonInteract.setVisibility(View.GONE);
            ((MinpanCatchActivity)getActivity()).getInfo(bundle1);
        }
    }
}
