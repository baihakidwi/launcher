package com.blogspot.bihaika.android.simpletextlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FlexboxLayout mFlexboxLayout;
    private PackageManager mPackageManager;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        fragmentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "frame long click", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mFlexboxLayout = fragmentView.findViewById(R.id.container_homefragment);
//        mFlexboxLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(getActivity(), "flexbox long click", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        mPackageManager = getActivity().getPackageManager();

        new AppSetter().execute();
        return fragmentView;
    }

    void setHomeApps(ArrayList<AppDetail> appDetails) {
        for (AppDetail app : appDetails) {
            mFlexboxLayout.addView(createTextView(app));
        }
        getView().findViewById(R.id.progressframe_homefragment).setVisibility(View.GONE);
        getView().invalidate();
    }

    View createTextView(AppDetail app) {
        View view = View.inflate(getActivity(), R.layout.text_homefragment, null);
        TextView textView = view.findViewById(R.id.txv_texthomefragment);
        textView.setText(app.getLabel());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX
                , DataManager.getInstance(getActivity()).getTextSize(app.getPriorityLevel()));
        DataManager.getInstance(getActivity()).putTextView(app.getName(), textView);
        setTextViewClickEvent(textView, app);
        return view;
    }

    void setTextViewClickEvent(final TextView textView, final AppDetail app) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "text view long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mPackageManager.getLaunchIntentForPackage(app.getName());
                DataManager.getInstance(getActivity()).click(app);
                getActivity().startActivity(intent);
            }
        });
    }

    class AppSetter extends AsyncTask<Void, Void, Void> {
        private DataManager mDataManager;

        @Override
        protected void onPostExecute(Void aVoid) {
            setHomeApps(mDataManager.getAppDetails());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDataManager = DataManager.getInstance(getActivity());
            return null;
        }
    }
}


