package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by chenhaowei on 16/3/29.
 */
public class HomePageActivity_back_dialogFragment extends DialogFragment {
    public static HomePageActivity_back_dialogFragment newInstance(String title,String message,String positivebutton,String negativebutton,String neutralbutton){
        HomePageActivity_back_dialogFragment frag = new HomePageActivity_back_dialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message",message);
        args.putString("positivebutton",positivebutton);
        args.putString("negativebutton",negativebutton);
        args.putString("neutralbutton",neutralbutton);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final String message = getArguments().getString("message");
        final String positivebutton = getArguments().getString("positivebutton");
        final String negativebutton = getArguments().getString("negativebutton");
        final String neutralbutton = getArguments().getString("neutralbutton");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positivebutton, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .setNegativeButton(negativebutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((HomePageActivity)getActivity()).back_doNegativeClick();
                    }
                })
                .setCancelable(false).setNeutralButton(neutralbutton,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((HomePageActivity)getActivity()).logout_doNegativeClick();
            }
        });
        //.show(); // show cann't be use here

        return builder.create();
    }
}
