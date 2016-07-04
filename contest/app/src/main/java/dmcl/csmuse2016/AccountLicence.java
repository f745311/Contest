package dmcl.csmuse2016;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.security.AccessControlContext;

public class AccountLicence extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acceptaccount);
        TextView licenceView = (TextView) findViewById(R.id.licence);
        licenceView.setMovementMethod(new ScrollingMovementMethod());


        final Button licenceAcceptButton = (Button)findViewById(R.id.licenceAcceptButton);

        View.OnClickListener handler = new View.OnClickListener(){
            public void onClick(View v){
                if (v == licenceAcceptButton){
                    Intent intentNewAccount = new Intent(AccountLicence.this,
                                                        CreateNewAccount.class);
                    AccountLicence.this.startActivity(intentNewAccount);
                    finish();
                }
            }
        };
        licenceAcceptButton.setOnClickListener(handler);

    }

    public void checkBoxchecked(View v){ //使用者需同意條款
        CheckBox seeifchecked = (CheckBox)findViewById(R.id.licenceCheckBox);
        Button nextstepbutton = (Button) findViewById(R.id.licenceAcceptButton);
        if (seeifchecked.isChecked()==true) {
            nextstepbutton.setEnabled(true);
        }
        else{
            nextstepbutton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
