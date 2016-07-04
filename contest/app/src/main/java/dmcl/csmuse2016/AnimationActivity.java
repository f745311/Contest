package dmcl.csmuse2016;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.util.DisplayMetrics;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class AnimationActivity extends ActionBarActivity {
    //開頭動畫
    private ImageView image = null;
    private int duration = 0;
    private final String filename="account.txt";
    private final String usedfile = "used.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timer timer = new Timer(true);
        timer.schedule(new MyTimerTask(), 1000,60*1000*60*12);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        DisplayMetrics monitorsize = new DisplayMetrics(); //偵測螢幕大小
        getWindowManager().getDefaultDisplay().getMetrics(monitorsize);

        int monitorWidth = monitorsize.widthPixels;   //偵測螢幕大小 暫時還沒用到
        int monitorHeight = monitorsize.heightPixels;


        getSupportActionBar().hide(); //隱藏title

        image = (ImageView)findViewById(R.id.demo);
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        image.setBackgroundResource(R.drawable.frame);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable anim = (AnimationDrawable) image.getBackground();
        //calculate the time for animation
        for(int i=0;i<anim.getNumberOfFrames();i++)
            duration+=anim.getDuration(i);
        // Start the animation (looped playback by default).
        anim.start();
        //when the animation finish,...
        checkIfAnimationDone(anim);
    }
    public void checkIfAnimationDone(AnimationDrawable anim){
        //Log.v("time",duration+"");
        initialFile();
        Handler handler = new Handler();
        boolean a=new Write_and_Read(filename,getFilesDir()).ifLogin();
        //Log.e("aa",Boolean.toString(a));
        if(!new Write_and_Read(filename,getFilesDir()).ifLogin()){
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(AnimationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, duration);
        }
        else{
            handler.postDelayed(new Runnable(){
                public void run(){
                    Intent intent = new Intent(AnimationActivity.this,HomePageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, duration);
        }
        }

    public void initialFile(){
        new Write_and_Read(filename,getFilesDir()).WritetoFile("");
        new Write_and_Read(usedfile,getFilesDir()).WritetoFile("");//if file exit writ,and not creat and write
    }
    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
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
            if(time!=usedtime){
                final int mId =1;
                int D=0;
                D |= Notification.DEFAULT_VIBRATE;
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.notify_icon)
                                .setContentTitle("測測你今天的氣運")
                                .setContentText("快來使用每日卜卦").setDefaults(D);
// Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(AnimationActivity.this, FreeActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(FreeActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(mId, mBuilder.build());
            }
        }
    };
}