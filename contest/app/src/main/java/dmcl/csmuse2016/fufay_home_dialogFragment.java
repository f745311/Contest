package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by chenhaowei on 16/3/24.
 */
public class fufay_home_dialogFragment extends DialogFragment
{

    public static fufay_home_dialogFragment newInstance(String title,String message,String positivebutton,String negativebutton){
        fufay_home_dialogFragment frag = new fufay_home_dialogFragment();
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
                                ((fufay)getActivity()).home_doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(negativebutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((fufay)getActivity()).home_doNegativeClick();
                    }
                })
                .setCancelable(false);
        //.show(); // show cann't be use here

        return builder.create();
    }


}
