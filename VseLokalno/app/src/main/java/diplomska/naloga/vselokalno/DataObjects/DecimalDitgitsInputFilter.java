package diplomska.naloga.vselokalno.DataObjects;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDitgitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDitgitsInputFilter(int digitsBefore, int digitsAfter) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBefore - 1) + "}+((\\.[0-9]{0," + (digitsAfter - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}
