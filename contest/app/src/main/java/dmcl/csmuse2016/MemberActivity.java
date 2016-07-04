package dmcl.csmuse2016;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;

public class MemberActivity extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> userDatas;
    private String[] hour_list = {"0:00~0:59(子時)","1:00~1:59(丑時)","2:00~2:59(丑時)","3:00~3:59(寅時)","4:00~4:59(寅時)","5:00~5:59(卯時)",
            "6:00~6:59(卯時)","7:00~7:59(辰時)","8:00~8:59(辰時)","9:00~9:59(巳時)","10:00~10:59(巳時)","11:00~11:59(午時)",
            "12:00~12:59(午時)","13:00~13:59(未時)","14:00~14:59(未時)","15:00~15:59(申時)","16:00~16:59(申時)","17:00~17:59(酉時)",
            "18:00~18:59(酉時)","19:00~19:59(戌時)","20:00~20:59(戌時)","21:00~21:59(亥時)","22:00~22:59(亥時)","23:00~23:59(子時)",};

    int y = 1911; //年
    int M = 1; //月
    int d = 1; //日
    int h = 0; //時辰

    private String hidePassword="";

    private String[] collectDatas;
    private String formConnect;
    private String Surname ="";
    private String Name ="";
    private String Email="";
    private String Password="";
    private String sex ="";
    private String yyyy ="";
    private String MM ="";
    private String dd ="";
    private String hh ="";

    private Button editSurname;
    private Button editName;
    private Button editPassword;
    private Button editSex;
    private Button editBirthdays;
    private Button editBirthTime;
    private ImageButton connectToFufay;
    private final String filename="account.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //接收帳號傳給php當key 抓資料庫
        Bundle getExtra = getIntent().getExtras();
        if (getExtra != null){
            Email = getExtra.getString("mail");
        }

        setContentView(R.layout.member_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_member);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(0xFFFFFFFF);
        // App Logo
        toolbar.setLogo(R.mipmap.title02);
        // Title
        toolbar.setTitle("會員專區");
        toolbar.setTitleTextColor(Color.BLACK);
        // Sub Title
        toolbar.setSubtitle("88Say幫您及時掌握未來");
        toolbar.setSubtitleTextColor(Color.BLACK);

        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //thread call 抓資料庫
        new getDatas().execute();

        //連到付費專區的image button
        connectToFufay = (ImageButton)findViewById(R.id.payAdImageButton);

        View.OnClickListener imagelisten = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == connectToFufay) { //卜卦畫面
                    if (isNetwork()) {
                        Intent intent = new Intent(MemberActivity.this, fufay.class);
                        MemberActivity.this.startActivity(intent);
                        finish();

                    }
                    else {
                        notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
                        editNameDialog.show(getFragmentManager(), "EditNameDialog");
                    }
                }
            }
        };
        connectToFufay.setOnClickListener(imagelisten);

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.membermemu, menu);
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
                case R.id.action_logout://登出
                    new Write_and_Read(filename,getFilesDir()).WritetoFile_clear("");
                    tologin.setClass(MemberActivity.this,LoginActivity.class);
                    startActivity(tologin);
                    finish();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(MemberActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
    //編輯姓
    public void editSurnameOnClick(View v){
        if(isNetwork()) {
            editSurname = (Button) findViewById(R.id.edit_surname);
            String name = "您的姓:";
            int id = 1;
            showPopup(v, name, id);
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }
    //編輯名
    public void editNameOnClick(View v){
        if(isNetwork()) {
            editName = (Button) findViewById(R.id.edit_name);
            String name = "您的大名:";
            int id = 2;
            showPopup(v, name, id);
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }
    //編輯密碼
    public void editPasswordOnClick(View v){
        if(isNetwork()) {
            editPassword = (Button) findViewById(R.id.edit_password);
            String name = "原密碼:";
            int id = 3;
            showPopup(v, name, id);
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }
    //編輯性別
    public void editSexOnClick(View v){
        if(isNetwork()) {
            editSex = (Button) findViewById(R.id.edit_sex);
            String name = "您的性別:";
            int id = 4;
            showPopup(v, name, id);
        }
        else {
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }
    //編輯生日
    public void editBirthDaysOnClick(View v){
        if(isNetwork()) {
            editBirthdays = (Button) findViewById(R.id.edit_birthday);
            String name = "生日:";
            int id = 5;
            showPopup(v, name, id);
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }
    //編輯時辰
    public void editBirthTimesOnClick(View v){
        if(isNetwork()) {
            editBirthTime = (Button) findViewById(R.id.edit_birthtime);
            String name = "時辰:";
            int id = 6;
            showPopup(v, name, id);
        }
        else{
            notNetwork_dialogFragment editNameDialog = new notNetwork_dialogFragment();
            editNameDialog.show(getFragmentManager(), "EditNameDialog");
        }
    }

    //編輯按鈕按下時
    public void showPopup(View anchorView , String titles, final int id) {
        switch (id){
            case 3:
                View popupPassword = getLayoutInflater().inflate(R.layout.fragment_editpassword, null);

                final PopupWindow popupCaseThree = new PopupWindow(popupPassword,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView opassword = (TextView) popupPassword.findViewById(R.id.opassword);
                TextView npassword = (TextView) popupPassword.findViewById(R.id.npassword);
                TextView cpassword = (TextView) popupPassword.findViewById(R.id.cpassword);
                final EditText origin = (EditText) popupPassword.findViewById(R.id.origin_password);
                final EditText newpas = (EditText) popupPassword.findViewById(R.id.new_password);
                final EditText confirmpas = (EditText) popupPassword.findViewById(R.id.confirm_password);
                Button sendPassword = (Button)popupPassword.findViewById(R.id.edit_password_send);

                opassword.setText(titles);
                npassword.setText("新密碼:");
                cpassword.setText("確認新密碼:");
                // If the PopupWindow should be focusable
                popupCaseThree.setFocusable(true);

                // If you need the PopupWindow to dismiss when when touched outside
                popupCaseThree.setBackgroundDrawable(new ColorDrawable());

                int locationCaseThree[] = new int[2];

                // Get the View's(the one that was clicked in the Fragment) location
                anchorView.getLocationOnScreen(locationCaseThree);

                // Using location, the PopupWindow will be displayed right under anchorView
                popupCaseThree.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                        locationCaseThree[0], locationCaseThree[1] + anchorView.getHeight());

                sendPassword.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable OriginString;
                        Editable NewString;
                        Editable ConfirmString;
                        OriginString = origin.getText();
                        NewString = newpas.getText();
                        ConfirmString = confirmpas.getText();

                        String opas = OriginString.toString();
                        String npas = NewString.toString();
                        String cpas = ConfirmString.toString();

                        if (opas.equals(collectDatas[3])){ //如果原密碼正確
                            if (npas.equals(cpas)){ //如果新密碼和確認密碼相同
                                //TODO 編輯
                                Password = npas;
                                new updateDatas().execute(id); //execute thread
                                popupCaseThree.dismiss(); //清除畫面
                                Toast.makeText(MemberActivity.this, "密碼修改完成", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MemberActivity.this, "您輸入的密碼不相符", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(MemberActivity.this, "原始密碼輸入錯誤", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
            case 4:
                View popupSex = getLayoutInflater().inflate(R.layout.fragment_editsex, null);

                final PopupWindow popupCaseFour = new PopupWindow(popupSex,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView sext = (TextView) popupSex.findViewById(R.id.titlesex);
                RadioButton Man = (RadioButton) popupSex.findViewById(R.id.manradio);
                RadioButton Woman = (RadioButton) popupSex.findViewById(R.id.womanradio);
                RadioGroup rgroup = (RadioGroup) popupSex.findViewById(R.id.editsexgroup);
                Button sendSex = (Button) popupSex.findViewById(R.id.edit_sex_send);

                rgroup.setOnCheckedChangeListener(listener); // 監聽radio button

                sext.setText(titles);
                // If the PopupWindow should be focusable
                popupCaseFour.setFocusable(true);

                // If you need the PopupWindow to dismiss when when touched outside
                popupCaseFour.setBackgroundDrawable(new ColorDrawable());

                int locationCaseFour[] = new int[2];

                // Get the View's(the one that was clicked in the Fragment) location
                anchorView.getLocationOnScreen(locationCaseFour);

                // Using location, the PopupWindow will be displayed right under anchorView
                popupCaseFour.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                        locationCaseFour[0], locationCaseFour[1] + anchorView.getHeight());

                if (collectDatas[4].equals("1")){
                   Man.setChecked(true); //initial state
                    sex = "1";
                } else{
                    Woman.setChecked(true); //initial state
                    sex = "0";
                }
                sendSex.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new updateDatas().execute(id);
                        popupCaseFour.dismiss();
                        Toast.makeText(MemberActivity.this, "性別修改完成", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 5:
                final View popupBirth = getLayoutInflater().inflate(R.layout.fragment_editbirthday, null);

                final PopupWindow popupCaseFive = new PopupWindow(popupBirth,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView bd = (TextView) popupBirth.findViewById(R.id.birth);
                NumberPicker year = (NumberPicker)popupBirth.findViewById(R.id.edityypicker);
                NumberPicker month = (NumberPicker)popupBirth.findViewById(R.id.editmmpicker);
                final NumberPicker day = (NumberPicker)popupBirth.findViewById(R.id.editddpicker);
                Button sendBirthday = (Button)popupBirth.findViewById(R.id.edit_birthday_send);

                bd.setText(titles);
                //設定上限下限
                year.setMaxValue(2016);
                year.setMinValue(1911);
                //設定初始值為使用者的原本year
                int userOriginYear = Integer.parseInt(collectDatas[6]);
                year.setValue(userOriginYear);
                year.setWrapSelectorWheel(true);
                y = Integer.parseInt(collectDatas[6]);

                month.setMaxValue(12);
                month.setMinValue(1);
                //設定初始值為使用者的原本year
                int userOriginMonth = Integer.parseInt(collectDatas[7]);
                month.setValue(userOriginMonth);
                month.setWrapSelectorWheel(true);
                M = Integer.parseInt(collectDatas[7]);

                day.setMaxValue(31);
                day.setMinValue(1);
                //設定初始值為使用者的原本year
                int userOriginDay = Integer.parseInt(collectDatas[8]);
                d = Integer.parseInt(collectDatas[8]);

                day.setValue(userOriginDay);
                day.setWrapSelectorWheel(true);
                // If the PopupWindow should be focusable
                popupCaseFive.setFocusable(true);
                // If you need the PopupWindow to dismiss when when touched outside
                popupCaseFive.setBackgroundDrawable(new ColorDrawable());
                int locationCaseFive[] = new int[2];
                // Get the View's(the one that was clicked in the Fragment) location
                anchorView.getLocationOnScreen(locationCaseFive);
                // Using location, the PopupWindow will be displayed right under anchorView
                popupCaseFive.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                        locationCaseFive[0], locationCaseFive[1] + anchorView.getHeight());

                year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        //Display the newly selected number from picker
                        y = newVal;
                        if (M == 2) {
                            if (y % 4 == 0) { //算閏年 但並不完整
                                day.setMaxValue(29);
                                day.setMinValue(1);
                            } else {
                                day.setMaxValue(28);
                                day.setMinValue(1);
                            }
                        }
                    }
                });
                month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        M = newVal;
                        day.setWrapSelectorWheel(true);
                        if (M == 2) {
                            if (y % 4 == 0) { //算閏年 但並不完整
                                day.setMaxValue(29);
                                day.setMinValue(1);
                            } else {
                                day.setMaxValue(28);
                                day.setMinValue(1);
                            }
                        } else if (M == 4 || M == 6 || M == 9 || M == 11) {
                            day.setMaxValue(30);
                            day.setMinValue(1);
                        } else {
                            day.setMaxValue(31);
                            day.setMinValue(1);
                        }
                    }
                });
                day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        d = newVal;
                    }
                });

                sendBirthday.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        yyyy = String.valueOf(y);
                        MM = String.valueOf(M);
                        dd = String.valueOf(d);

                        new updateDatas().execute(id);
                        popupCaseFive.dismiss();
                        Toast.makeText(MemberActivity.this, "生日修改完成", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 6:
                View popupTime = getLayoutInflater().inflate(R.layout.fragment_edittime, null);

                final PopupWindow popupCaseSix = new PopupWindow(popupTime,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView bt = (TextView) popupTime.findViewById(R.id.birtht);
                NumberPicker time = (NumberPicker)popupTime.findViewById(R.id.edittimePick);
                Button sendtime = (Button)popupTime.findViewById(R.id.edit_time_send);

                bt.setText(titles);
                //設定上限下限
                time.setMaxValue(23);
                time.setMinValue(0);

                hh = collectDatas[9]; //先存到一個暫存以免值為null
                h = Integer.parseInt(collectDatas[9]); //設定初始值為使用者的原本時辰
                time.setValue(h);

                time.setDisplayedValues(hour_list);
                time.setWrapSelectorWheel(true);
                // If the PopupWindow should be focusable
                popupCaseSix.setFocusable(true);
                // If you need the PopupWindow to dismiss when when touched outside
                popupCaseSix.setBackgroundDrawable(new ColorDrawable());
                int locationCaseSix[] = new int[2];
                // Get the View's(the one that was clicked in the Fragment) location
                anchorView.getLocationOnScreen(locationCaseSix);
                // Using location, the PopupWindow will be displayed right under anchorView
                popupCaseSix.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                        locationCaseSix[0], locationCaseSix[1] + anchorView.getHeight());

                time.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        hh = String.valueOf(newVal);
                    }
                });
                sendtime.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new updateDatas().execute(id);
                        popupCaseSix.dismiss();
                        Toast.makeText(MemberActivity.this, "時辰修改完成", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            default:
                View popupView = getLayoutInflater().inflate(R.layout.fragment_edit_member, null);

                final PopupWindow popupWindow = new PopupWindow(popupView,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView tv = (TextView) popupView.findViewById(R.id.somewords);
                final EditText edit = (EditText) popupView.findViewById(R.id.editor);
                Button sendDefault = (Button)popupView.findViewById(R.id.edit_member_send);

                tv.setText(titles);
                if (id == 1){ //表示現在的畫面是“姓”
                    edit.setText(collectDatas[0]); //initial
                } else if (id == 2){ //畫面為“名"
                    edit.setText(collectDatas[1]); //initial
                }
                // If the PopupWindow should be focusable
                popupWindow.setFocusable(true);

                // If you need the PopupWindow to dismiss when when touched outside
                popupWindow.setBackgroundDrawable(new ColorDrawable());

                int location[] = new int[2];

                // Get the View's(the one that was clicked in the Fragment) location
                anchorView.getLocationOnScreen(location);

                // Using location, the PopupWindow will be displayed right under anchorView
                popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                        location[0], location[1] + anchorView.getHeight());

                sendDefault.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable strings;
                        strings = edit.getText();
                        if (id == 1) {
                            Surname = strings.toString();
                        } else if (id == 2) {
                            Name = strings.toString();
                        }
                        new updateDatas().execute(id);
                        popupWindow.dismiss();
                        Toast.makeText(MemberActivity.this, "修改完成", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.manradio:
                    sex = "1";
                    break;
                case R.id.womanradio:
                    sex = "0";
                    break;
            }

        }
    };

    //進來時抓資料的thread
    class getDatas extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            String command = "select * from Account where Account='" +Email + "'";
            String ans = new connect(command).getServerConnect();
            Log.i("ans",ans);
            collectDatas = ans.toString().split("###");
            Log.v("getL", ans);
            if(ans.equals("Warning")){
                Log.v("get","get an error from server");
            }else{
                userDatas = new ItemSeparate(ans).getItem();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            TextView Surname = (TextView)findViewById(R.id.SurName_content);
            TextView Name = (TextView)findViewById(R.id.Name_content);
            TextView E_mail = (TextView)findViewById(R.id.mail_content);
            TextView Password = (TextView)findViewById(R.id.password_content);
            TextView Sex = (TextView)findViewById(R.id.sex_content);
            TextView yyyy = (TextView)findViewById(R.id.BirthYY);
            TextView MM = (TextView)findViewById(R.id.BirthMM);
            TextView DD = (TextView)findViewById(R.id.BirthDD);
            TextView TT = (TextView)findViewById(R.id.BirthTT);

            Surname.setText(collectDatas[0]);
            Name.setText(collectDatas[1]);
            E_mail.setText(collectDatas[2]);
            //將密碼蓋成"*"
            for (int i = 0 ; i<collectDatas[3].length() ; i++){
                hidePassword += "*";
            }
            Password.setText(hidePassword);
            
            if(collectDatas[4].equals("1")){
                Sex.setText("男");
            }
            else{
                Sex.setText("女");
            }

            yyyy.setText(collectDatas[6]);
            MM.setText(collectDatas[7]);
            DD.setText(collectDatas[8]);
            int index = Integer.parseInt(collectDatas[9]);
            TT.setText(hour_list[index]);
        }

    }

    //按下編輯後 送出資料進行update的thread
    class updateDatas extends AsyncTask<Integer,Void,Integer> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected Integer doInBackground(Integer... Cases) {
            // TODO Auto-generated method stub
            String fromfile = new Write_and_Read(filename, getFilesDir()).ReadFromFile();
            String[] tem = fromfile.split("@@@@@");
            String[] fromfileArray = tem[0].split("###");
            String newAccountDatas = "";

            if (Cases[0] == 1){
                String command = "UPDATE Account SET Surname='"+ Surname +"' WHERE Account='" +Email + "'";
                String ans = new connect(command).getServerUpdate();
                Log.v("getL", ans);
                if(ans.equals("Warning")){
                    Log.v("get", "get an error from server");
                }
                //寫回file updated 2016/04/05 by 均
                for (int i=0;i<fromfileArray.length;i++){
                    if (i == 0){
                        newAccountDatas += Surname + "###";
                    }else{
                        if (i == fromfileArray.length-1){
                            newAccountDatas += fromfileArray[i] + "@@@@@";
                        }else{
                            newAccountDatas += fromfileArray[i] + "###";
                        }
                    }
                }
                new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(newAccountDatas);
                return 1;
            }
            else if (Cases[0] == 2){
                String command = "UPDATE Account SET Name='"+ Name +"' WHERE Account='" +Email + "'";
                String ans = new connect(command).getServerUpdate();
                Log.v("getL", ans);
                if(ans.equals("Warning")){
                    Log.v("get","get an error from server");
                }
                //寫回file updated 2016/04/05 by 均
                for (int i=0;i<fromfileArray.length;i++){
                    if (i == 1){
                        newAccountDatas += Name + "###";
                    }else{
                        if (i == fromfileArray.length-1){
                            newAccountDatas += fromfileArray[i] + "@@@@@";
                        }else{
                            newAccountDatas += fromfileArray[i] + "###";
                        }
                    }
                }
                new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(newAccountDatas);
                return 2;
            }
            else if (Cases[0] == 3){
                String command = "UPDATE Account SET Password='"+ Password +"' WHERE Account='" +Email + "'";
                String ans = new connect(command).getServerUpdate();
                Log.v("getL", ans);
                if(ans.equals("Warning")){
                    Log.v("get","get an error from server");
                }
                //寫回file updated 2016/04/05 by 均
                for (int i=0;i<fromfileArray.length;i++){
                    if (i == 3){
                        newAccountDatas += Password + "###";
                    }else{
                        if (i == fromfileArray.length-1){
                            newAccountDatas += fromfileArray[i] + "@@@@@";
                        }else{
                            newAccountDatas += fromfileArray[i] + "###";
                        }
                    }
                }
                new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(newAccountDatas);
                return 3;
            }
            else if (Cases[0] == 4){
                String command = "UPDATE Account SET sex='"+ sex +"' WHERE Account='" +Email + "'";
                String ans = new connect(command).getServerUpdate();
                Log.v("getL", ans);
                if(ans.equals("Warning")){
                    Log.v("get","get an error from server");
                }
                //寫回file updated 2016/04/05 by 均
                for (int i=0;i<fromfileArray.length;i++){
                    if (i == 4){
                        newAccountDatas += sex + "###";
                    }else{
                        if (i == fromfileArray.length-1){
                            newAccountDatas += fromfileArray[i] + "@@@@@";
                        }else{
                            newAccountDatas += fromfileArray[i] + "###";
                        }
                    }
                }
                new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(newAccountDatas);
                return 4;
            }
            else if (Cases[0] == 5){
                String command = "UPDATE Account SET year='"+ yyyy +"', month='"+ MM + "',day='" +dd+
                        "' WHERE Account='" +Email + "'";
                String ans = new connect(command).getServerUpdate();
                Log.v("getL", ans);
                if(ans.equals("Warning")){
                    Log.v("get","get an error from server");
                }
                //寫回file updated 2016/04/05 by 均
                for (int i=0;i<fromfileArray.length;i++){
                    if (i == 6){
                        newAccountDatas += yyyy + "###";
                    }else if (i == 7){
                        newAccountDatas += MM + "###";
                    }else if (i == 8){
                        newAccountDatas += dd + "###";
                    }else{
                        if (i == fromfileArray.length-1){
                            newAccountDatas += fromfileArray[i] + "@@@@@";
                        }else{
                            newAccountDatas += fromfileArray[i] + "###";
                        }
                    }
                }
                new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(newAccountDatas);
                return 5;
            }
            else if (Cases[0] == 6){
                String command = "UPDATE Account SET hour = '"+ hh +"' WHERE Account='" +Email + "'";
                String ans = new connect(command).getServerUpdate();
                Log.v("getL", ans);
                if(ans.equals("Warning")){
                    Log.v("get","get an error from server");
                }
                //寫回file updated 2016/04/05 by 均
                for (int i=0;i<fromfileArray.length;i++){
                    if (i == 9){
                        newAccountDatas += hh + "###";
                    }else{
                        if (i == fromfileArray.length-1){
                            newAccountDatas += fromfileArray[i] + "@@@@@";
                        }else{
                            newAccountDatas += fromfileArray[i] + "###";
                        }
                    }
                }
                new Write_and_Read(filename,getFilesDir()).WritetoFile_clear(newAccountDatas);
                return 6;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1){
                TextView Surnames = (TextView)findViewById(R.id.SurName_content);
                Surnames.setText(Surname);

                collectDatas[0] = Surname; //回存,這樣可以反覆更改


            }else if (result == 2){
                TextView Names = (TextView)findViewById(R.id.Name_content);
                Names.setText(Name);

                collectDatas[1] = Name;//回存,這樣可以反覆更改
            }else if (result == 3){
                TextView Passwords = (TextView)findViewById(R.id.password_content);
                hidePassword ="";
                for (int i =0;i<Password.length();i++){
                    hidePassword+="*";
                }
                Passwords.setText(hidePassword);
                collectDatas[3] = Password;//回存,這樣可以反覆更改
            }else if (result == 4){
                TextView Sexs = (TextView)findViewById(R.id.sex_content);
                if (sex == "1"){
                    Sexs.setText("男");
                    collectDatas[4] = sex;//回存,這樣可以反覆更改
                }else{
                    Sexs.setText("女");
                    collectDatas[4] = sex;//回存,這樣可以反覆更改
                }
            }else if (result == 5){
                TextView year = (TextView)findViewById(R.id.BirthYY);
                TextView month = (TextView)findViewById(R.id.BirthMM);
                TextView day = (TextView)findViewById(R.id.BirthDD);

                year.setText(yyyy);
                month.setText(MM);
                day.setText(dd);

                collectDatas[6] = yyyy;//回存,這樣可以反覆更改
                collectDatas[7] = MM;//回存,這樣可以反覆更改
                collectDatas[8] = dd;//回存,這樣可以反覆更改
            }else if (result == 6){
                TextView hour = (TextView)findViewById(R.id.BirthTT);
                int index = Integer.parseInt(hh);
                hour.setText(hour_list[index]);
                collectDatas[9] = hh;//回存,這樣可以反覆更改
            }


            }
        }

    }

