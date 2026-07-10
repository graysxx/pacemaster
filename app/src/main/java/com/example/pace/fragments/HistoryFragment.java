package com.example.pace.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.Fragment;
import com.example.pace.R;
import com.example.pace.activities.ActivityDetailActivity;
import com.example.pace.activities.ActivityShareActivity;
import com.example.pace.models.ActivityRecord;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        View.OnClickListener toDetail = v -> {
            Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
            ActivityRecord data = null;
            if (v.getId() == R.id.itemRun1) data = getMockItem1();
            else if (v.getId() == R.id.itemRun2) data = getMockItem2();
            else if (v.getId() == R.id.itemRun3) data = getMockItem3();
            intent.putExtra("ACTIVITY_DATA", data);
            startActivity(intent);
        };

        View.OnClickListener toShare = v -> {
            Intent intent = new Intent(getActivity(), ActivityShareActivity.class);
            ActivityRecord data = null;
            if (v.getId() == R.id.btnShareItem1) data = getMockItem1();
            else if (v.getId() == R.id.btnShareItem2) data = getMockItem2();
            else if (v.getId() == R.id.btnShareItem3) data = getMockItem3();
            intent.putExtra("ACTIVITY_DATA", data);
            
            // Legacy extras for ActivityShareActivity (if not updated yet)
            if (data != null) {
                intent.putExtra("jarak", String.valueOf(data.getDistance()));
                intent.putExtra("waktu", data.getDuration());
                intent.putExtra("elev", data.getElevationGain() + " m");
                intent.putExtra("pace", data.getAvgPace());
            }
            startActivity(intent);
        };

        view.findViewById(R.id.itemRun1).setOnClickListener(toDetail);
        view.findViewById(R.id.itemRun2).setOnClickListener(toDetail);
        view.findViewById(R.id.itemRun3).setOnClickListener(toDetail);

        view.findViewById(R.id.btnShareItem1).setOnClickListener(toShare);
        view.findViewById(R.id.btnShareItem2).setOnClickListener(toShare);
        view.findViewById(R.id.btnShareItem3).setOnClickListener(toShare);

        return view;
    }

    private ActivityRecord getMockItem1() {
        ActivityRecord r = new ActivityRecord("1", "Morning Run", "Hari ini", "Jakarta", 5.20, "27:14", "5:14", 338, 6933, 12, 18);
        r.setXLabels(new String[]{"0", "1", "2", "3", "4", "5", "5.2"});
        r.setPaceData(new float[]{5.30f, 5.20f, 5.25f, 5.10f, 5.15f, 5.05f, 5.14f});
        r.setCadenceData(new float[]{165f, 170f, 168f, 172f, 175f, 170f, 172f});
        r.setElevationData(new float[]{5f, 8f, 12f, 10f, 15f, 18f, 15f});
        
        List<ActivityRecord.Split> splits = new ArrayList<>();
        splits.add(new ActivityRecord.Split(1, "5:26", "+0m", 60));
        splits.add(new ActivityRecord.Split(2, "5:11", "+6m", 75));
        splits.add(new ActivityRecord.Split(3, "5:03", "+8m", 90));
        splits.add(new ActivityRecord.Split(4, "5:20", "+2m", 65));
        splits.add(new ActivityRecord.Split(5, "5:15", "+4m", 70));
        r.setSplits(splits);
        return r;
    }

    private ActivityRecord getMockItem2() {
        return new ActivityRecord("2", "Long Run", "Kemarin", "Bandung", 7.80, "42:33", "5:27", 512, 10240, 24, 35);
    }

    private ActivityRecord getMockItem3() {
        return new ActivityRecord("3", "Evening Run", "2 hari lalu", "Jakarta", 4.10, "22:07", "5:23", 280, 5500, 8, 12);
    }
}