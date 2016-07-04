package dmcl.csmuse2016;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentForMinsu extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    private Button button_A,button_B,button_C,button_D,button_start;//A命宮主星 B優缺 C課題 D風采
    private TextView content2;
    private LinearLayout linear ;
    String return_A,return_B,return_C,return_D;//這4個用來記錄回傳的資料
    String slogan = "最專業的生活命理顧問\n88Say幫您及時掌握生活與未來";//slogan
    @Override
    //資料送出後，顯示的資料應該要在這裡
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmentforminsu, container, false);
//這裡我有改

        return_A = getArguments().getString("Reslut_Star");
        return_B = getArguments().getString("Result_Good_Bad");
        return_C = getArguments().getString("Reslut_Issue");
        return_D = getArguments().getString("Reslut_Desc");

        button_start = (Button)v.findViewById(R.id.start);
        button_A = (Button)v.findViewById(R.id.button_A);
        button_B = (Button)v.findViewById(R.id.button_B);
        button_C = (Button)v.findViewById(R.id.button_C);
        button_D = (Button)v.findViewById(R.id.button_D);
        content2 = (TextView)v.findViewById(R.id.content2);
        linear = (LinearLayout)v.findViewById(R.id.linear);
        if (return_A==null){content2.setText(slogan);}
        else {content2.setText(return_A);}
        button_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_A==null){content2.setText(slogan);}
                else {content2.setText(return_A);}//把回傳的A命宮主星顯示出來
                linear.setBackgroundColor(Color.parseColor("#9257cfd9"));
                content2.setPadding(40, 0, 40, 0);
            }
        });
        button_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_B==null){content2.setText(slogan);}
                else {content2.setText(return_B);}//把回傳的B優缺顯示出來
                linear.setBackgroundColor(Color.parseColor("#8257cfd9"));
                content2.setPadding(40,0,40,0);
            }
        });
        button_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_C==null){content2.setText(slogan);}
                else {content2.setText(return_C);}//把回傳的C課題顯示出來
                linear.setBackgroundColor(Color.parseColor("#7257cfd9"));
                content2.setPadding(40,0,40,0);
            }
        });
        button_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_D==null){
                    content2.setText(slogan);
                    content2.setPadding(40, 0, 40, 0);
                }
                else {
                    content2.setText(return_D);
                    content2.setPadding(40, 0, 40, 0);
                }//把回傳的A命宮主星顯示出來
                linear.setBackgroundColor(Color.parseColor("#8257cfd9"));

            }
        });
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MinsuActivity)getActivity()).startuse();
            }
        });
        return v;
    }
}
