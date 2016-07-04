package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by chenhaowei on 16/4/9.
 */
public class whenDateError_dialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = "錯誤";
        final String message = "請確認輸入正確";


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false);
        //.show(); // show cann't be use here

        return builder.create();
    }
}
