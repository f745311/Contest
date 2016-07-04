package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by chenhaowei on 16/4/4.
 */
public class whenGuestClickMember_dialogFragment extends DialogFragment {
    public static whenGuestClickMember_dialogFragment newInstance(String title,String message,String positivebutton,String negativebutton){
        whenGuestClickMember_dialogFragment frag = new whenGuestClickMember_dialogFragment();
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
                                ((HomePageActivity)getActivity()).notlogin_doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(negativebutton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((HomePageActivity)getActivity()).notlogin_doNegativeClick();
                    }
                })
                .setCancelable(false);
        //.show(); // show cann't be use here

        return builder.create();
    }
}
