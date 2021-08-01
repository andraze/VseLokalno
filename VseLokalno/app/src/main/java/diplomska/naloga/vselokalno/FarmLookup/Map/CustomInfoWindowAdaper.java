package diplomska.naloga.vselokalno.FarmLookup.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.jetbrains.annotations.NotNull;

import diplomska.naloga.vselokalno.R;

public class CustomInfoWindowAdaper implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdaper(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.marker_title);

        if (title != null && !title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView) view.findViewById(R.id.marker_desc);

        if (snippet != null && !snippet.equals("")) {
            tvSnippet.setText(snippet);
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View getInfoWindow(@NonNull @NotNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View getInfoContents(@NonNull @NotNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
