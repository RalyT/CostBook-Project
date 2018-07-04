package finalproject.comp3617.com.costbook.HelperClasses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Opens a DatePicker Dialog that returns year, month, and day.
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener dateSetListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        dateSetListener = (DatePickerDialog.OnDateSetListener)getTargetFragment();
        return new DatePickerDialog(getActivity(),
                dateSetListener,
                year, month, day);
    }
}
