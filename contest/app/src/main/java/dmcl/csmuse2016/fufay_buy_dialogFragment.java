package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

/**
 * Created by chenhaowei on 16/3/23.
 */



public class fufay_buy_dialogFragment extends DialogFragment
        {

    public static fufay_buy_dialogFragment newInstance(String title,String message,String positivebutton,String negativebutton){
        fufay_buy_dialogFragment frag = new fufay_buy_dialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message",message);
        args.putString("positivebutton",positivebutton);
        args.putString("negativebutton",negativebutton);

        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final String message = getArguments().getString("message");
        final String positivebutton = getArguments().getString("positivebutton");
        final String negativebutton = getArguments().getString("negativebutton");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positivebutton, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((fufay)getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(negativebutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((fufay)getActivity()).doNegativeClick();
                    }
                })
                .setCancelable(false);
        //.show(); // show cann't be use here

        return builder.create();
    }


}