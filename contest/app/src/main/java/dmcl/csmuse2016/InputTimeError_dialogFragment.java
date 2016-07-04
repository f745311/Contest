package dmcl.csmuse2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by chenhaowei on 16/4/3.
 */
public class InputTimeError_dialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = "錯誤";
        final String message = "輸入時間錯誤";


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message);
        return builder.create();
    }
}
