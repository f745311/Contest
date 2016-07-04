package dmcl.csmuse2016;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jack on 2016/3/23.
 */
public class FragmentForFree extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    private Button button_Title,button_Result,button_Comment;
    private TextView content;
    String return_Title,return_Result,return_Comment;//這三個用來記錄回傳的資料
    String slogan = "最專業的生活命理顧問\n88Say幫您及時掌握生活與未來";//slogan
    @Override
    //資料送出後，顯示的資料應該要在這裡
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmentforfree, container, false);

        return_Title = getArguments().getString("Title");
        return_Result = getArguments().getString("All_result");
        return_Comment = getArguments().getString("Comment");

        button_Title = (Button)v.findViewById(R.id.button_Title);
        button_Result = (Button)v.findViewById(R.id.button_Result);
        button_Comment = (Button)v.findViewById(R.id.button_Comment);
        content = (TextView)v.findViewById(R.id.content);

        if (return_Title==null){content.setText(slogan);}
        else {content.setText(return_Title);}

        button_Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_Title==null){content.setText(slogan);}
                else {content.setText(return_Title);}
                content.setBackgroundColor(Color.parseColor("#9257cfd9"));
            }
        });
        button_Result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_Result==null){content.setText(slogan);}
                else {content.setText(return_Result);}
                content.setBackgroundColor(Color.parseColor("#8257cfd9"));
            }
        });
        button_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (return_Comment==null){content.setText(slogan);}
                else {content.setText(return_Comment);}
                content.setBackgroundColor(Color.parseColor("#7257cfd9"));
            }
        });

        return v;
    }
}
