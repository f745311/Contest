package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

/**
 * Created by chenhaowei on 16/4/7.
 */
public class notNetwork_dialogFragment extends DialogFragment{
    /*DialogInterface.OnClickListener negitive;
    DialogInterface.OnClickListener positive;
    public notNetwork_dialogFragment(DialogInterface.OnClickListener negitive,DialogInterface.OnClickListener positive){
        this.negitive = negitive;
        this.positive = positive;
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = "錯誤";
        final String message = "你沒有網路喔～";


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false);
        //.show(); // show cann't be use here

        return builder.create();
    }
}
