package beamerkun.scalemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import beamerkun.scalemanager.dao.Measurement;
import beamerkun.scalemanager.dao.User;

public class MainScreenFragment extends Fragment implements ScaleHelper.ScaleMeasurementListener {
    User test_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_screen, container, false);

        final Button bt_connect_button = (Button) v.findViewById(R.id.bt_connect_button);

        bt_connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleHelper.getInstance(getActivity().getApplicationContext()).startScan();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Context context = getActivity().getApplicationContext();
        test_user = StorageHelper.getInstance(context).getUser();
        ScaleHelper.getInstance(context).setUserData(test_user.toByteArray());
    }

    @Override
    public void onMeasurementStateChanged(ScaleHelper.MeasurementState new_state) {
    }

    @Override
    public void onMeasurementReceived(Measurement measurement) {
        measurement.setUserId(test_user.getId());
        StorageHelper.getInstance(getActivity().getApplicationContext()).saveMeasurement(measurement);
    }
}
