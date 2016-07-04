package dmcl.csmuse2016;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;
import com.unionpaysdk.main.ICheckOrderCallback;
import com.unionpaysdk.main.IPaymentCallback;
import com.unionpaysdk.main.UnionPaySDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLConnection;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

public class fufay extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private UnionPaySDK unionPaySDK =null;
    private Context ctx =null;
    //this will be provided UnionSDK
    private static String scode="EID001301";
    //this should be applied by CP and will be provided UnionSDK
    private static String key="A&4&aHnh7N";
    //CP should provide this to UnionSDK
    private static String payCallBackUrl="https://payment.skillfully.com.tw/back.aspx";
    //this should be on the CP' own server, which has nothing to do with SDK, and should be handle by CP self
    private String orderid ="";
    private double amount = 1.0;
    private String memo ="This is a memo";
    public static ArrayList<HashMap<String,String>> listForm_mapFromJson = new ArrayList<HashMap<String,String>>();
    private  HashMap<String,String> mapFromJson = new HashMap<String,String>();
    private final String filename="account.txt";
    private boolean loginornot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fufay);
        new AsyncTaskParseJson().execute();
        //LeakCanary.install(getApplication());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(0xFFFFFFFF);
        // App Logo
        toolbar.setLogo(R.mipmap.title02);
        // Title
        toolbar.setTitle("付費專區");
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

        ctx=this;

        unionPaySDK = UnionPaySDK.getInstance();
        //must Initialize first
        unionPaySDK.Initialize(ctx, scode, key, true);

        loginornot = new Write_and_Read(filename,getFilesDir()).ifLogin();

    }


    private static String getRandomString(int len)
    {


        String str = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++)
        {
            int idx = (int)(Math.random() * str.length());
            sb.append(str.charAt(idx));
        }
        String result = sb.toString();


        return result;

    }

    private void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(fufay.this,message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private IPaymentCallback paymentCallback = new IPaymentCallback()
    {

        @Override
        public void onOrderFinished() {
            show("Order Finished~");
            // after the order is finished, the client should immediately request the response
            //from CP's own server to get to know the result of the order



        }

        @Override
        public void onOrderNotFinished() {
            show("Order NotFinished~");


        }


    };


    private ICheckOrderCallback checkOrderCallBack = new ICheckOrderCallback()
    {

        @Override
        public void onSuccess(String json) {

            show("json is "+ json);

        }

        @Override
        public void onFailed() {


        }

    };
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            int fragment_number = listForm_mapFromJson.size();//tem
            //Log.e("size",Integer.toString(fragment_number));

            return 5;
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
    public void toright(View view){
        int current = mViewPager.getCurrentItem();
        if(current!=4){
            mViewPager.setCurrentItem(current + 1, true);
        }
        else{

        }
    }
    public void toleft(View view){
        int current = mViewPager.getCurrentItem();
        if(current!=0){
            mViewPager.setCurrentItem(current - 1, true);
        }
        else{

        }
    }
    public void showEditDialog(View view)
    {
        if(isNetwork()) {
            if (loginornot) {
                String Price = "";
                Button fragment_btn = (Button) view.findViewById(R.id.fufay_btn);
                //Log.e("btn.text", fragment_btn.getText().toString());
                int count = 0;

                switch (fragment_btn.getText().toString()) {

                    case "購買：2016運勢詳批":
                        if (listForm_mapFromJson != null) {
                            Price = listForm_mapFromJson.get(0).get("Price");
                            //Log.i("case1", String.valueOf(Price));
                        }
                        break;
                    case "購買：財富命盤":
                        if (listForm_mapFromJson != null) {
                            Price = listForm_mapFromJson.get(1).get("Price");
                            //Log.i("case2", String.valueOf(Price));
                        }
                        break;
                    case "購買：超猛愛情靈卦":
                        if (listForm_mapFromJson != null) {
                            Price = listForm_mapFromJson.get(2).get("Price");
                            //Log.i("case3", String.valueOf(Price));
                        }
                        break;
                    case "購買：情人之間":
                        if (listForm_mapFromJson != null) {
                            Price = listForm_mapFromJson.get(3).get("Price");
                            //Log.i("case4", String.valueOf(Price));
                        }
                        break;
                    case "購買：職場運勢":
                        if (listForm_mapFromJson != null) {
                            Price = listForm_mapFromJson.get(4).get("Price");
                            //Log.i("case5", String.valueOf(Price));
                        }
                        break;
                    default:
                        //Log.i("no case",Price);
                        break;
                }
                amount = Integer.parseInt(Price);
                fufay_buy_dialogFragment editNameDialog = fufay_buy_dialogFragment.newInstance("確定要購買嗎？", "商品：\t" + Price, "取消", "確定");
                editNameDialog.show(getFragmentManager(), "EditNameDialog");
            } else {
                fufay_notlogin_dialogFragment editNameDialog = fufay_notlogin_dialogFragment.newInstance("無法購買", "需要登陸才能購買喔～", "取消", "去登陸");
                editNameDialog.show(getFragmentManager(), "EditNameDialog");
            }
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }
    public void home_showEditDialog(){
        fufay_home_dialogFragment editNameDialog =  fufay_home_dialogFragment.newInstance("確定要離開嗎？", "左右滑可以看到更多範例喔！", "取消", "確定");
        editNameDialog.show(getFragmentManager(), "EditNameDialog");
    }
    public void doPositiveClick() {//positive 在畫面右邊
        // Do stuff here.
        //Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        orderid = getRandomString(10);
        unionPaySDK.payOrderRequest(fufay.this, orderid, amount, memo, payCallBackUrl, paymentCallback);
        //Log.i("FragmentAlertDialog", "Negative click!");
    }
    public void home_doPositiveClick() {
        // Do stuff here.

    }

    public void home_doNegativeClick() {
        // Do stuff here.
        finish();
    }
    public void notlogin_doPositiveClick() {
        // Do stuff here.

    }

    public void notlogin_doNegativeClick() {
        // Do stuff here.
        Intent tologin = new Intent();
        tologin.setClass(fufay.this, LoginActivity.class);
        startActivity(tologin);
        finish();
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            Intent tologin =new Intent();
            switch (menuItem.getItemId()) {
                case R.id.action_home: //home鍵被按時
                    fufay_home_dialogFragment editNameDialog =  fufay_home_dialogFragment.newInstance("確定要離開嗎？", "左右滑可以看到更多範例喔！", "取消", "確定");
                    editNameDialog.show(getFragmentManager(), "EditNameDialog");

                    break;
                case R.id.action_settings: //setting鍵
                    if(isNetwork()) {
                        String fromfile = new Write_and_Read(filename, getFilesDir()).ReadFromFile();
                        String[] fromfileArray = fromfile.split("###");
                        Intent intentMember = new Intent(getApplicationContext(), MemberActivity.class);
                        intentMember.putExtra("mail", fromfileArray[2]); //send mail to next activity
                        fufay.this.startActivity(intentMember);
                        finish();
                    }
                    else{
                        notNetwork_dialogFragment EditNameDialog = new notNetwork_dialogFragment();
                        EditNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                    break;
                case R.id.action_logout://登出
                    new Write_and_Read(filename,getFilesDir()).WritetoFile_clear("");
                    tologin.setClass(fufay.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
                case R.id.action_login://訪客登入
                    tologin.setClass(fufay.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(fufay.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private final String url_2016="http://newtest.88say.com/views/product/262/saySample/sample_262.htm";
        private final String url_life="http://newtest.88say.com/product/517/sample_517.htm";
        private final String url_love="http://newtest.88say.com/product/524/sample_524.htm";
        private final String url_lover="http://newtest.88say.com/product/267/sample_267.htm";
        private final String url_work="http://newtest.88say.com/product/402/sample_402.htm";
       //public static ArrayList<HashMap<String,String>> listForm_mapFromJson = new ArrayList<HashMap<String,String>>();
        //private static HashMap<String,String> mapFromJson = new HashMap<String,String>();
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_fufay, container, false);
            Button fragment_btn = (Button) rootView.findViewById(R.id.fufay_btn);
            WebView wv =(WebView) rootView.findViewById(R.id.webView);
            WebSettings settings = wv.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {

                case 1:
                    wv.loadUrl(url_2016);
                    fragment_btn.setText("購買：2016運勢詳批");
                    break;
                case 2:
                    wv.loadUrl(url_life);
                    fragment_btn.setText("購買：財富命盤");
                    break;
                case 3:
                    wv.loadUrl(url_love);
                    fragment_btn.setText("購買：超猛愛情靈卦");
                    break;
                case 4:
                    wv.loadUrl(url_lover);
                    fragment_btn.setText("購買：情人之間");
                    break;
                case 5:
                    wv.loadUrl(url_work);
                    fragment_btn.setText("購買：職場運勢");
                    break;
            }
            return rootView;
        }
    }
    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        // set your json string url here
        String URL = "http://newtest.88say.com/Api/product/GetList.aspx?token=" +
                "D5DF5A998BF46E8D37E3D600C022D8B0D76D68BABCF7CFC75304E8EF5168A48B";



        // contacts JSONArray
        JSONArray dataJsonArr = null;

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {

            try {
                JSONObject jsobj= new getJson(URL).getJSONFromUrl();
                //Log.e("jsobj",jsobj.toString());
                dataJsonArr = jsobj.getJSONArray("Result");
                for (int i = 0; i < dataJsonArr.length(); i++) {
                    mapFromJson = new HashMap<String,String>();
                    JSONObject c = dataJsonArr.getJSONObject(i);
                    mapFromJson.put("Id", c.getString("Id"));
                    mapFromJson.put("Name", c.getString("Name"));
                    mapFromJson.put("Sample", c.getString("Sample"));
                    mapFromJson.put("Price", c.getString("Price"));
                    mapFromJson.put("Category1", c.getString("Category1"));
                    mapFromJson.put("Category2", c.getString("Category2"));
                    //Log.e("Price", mapFromJson.get("Price"));
                    listForm_mapFromJson.add(mapFromJson);
                   //Log.e("list"+i, listForm_mapFromJson.get(i).get("Price"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            //Log.e("fragment", listForm_mapFromJson.get(0).get("Name"));
            //Log.e("fragment", listForm_mapFromJson.get(1).get("Name"));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            fufay_home_dialogFragment editNameDialog =  fufay_home_dialogFragment.newInstance("確定要離開嗎？", "左右滑可以看到更多範例喔！", "取消", "確定");
            editNameDialog.show(getFragmentManager(), "EditNameDialog");

        }
        return false;
    }
}
