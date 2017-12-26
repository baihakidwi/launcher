package com.blogspot.bihaika.android.simpletextlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Baihaki Dwi on 23/12/2017.
 */

public class DataManager {
    private static DataManager sDataManager;
    private Context mAppContext;

    private PackageManager mPackageManager;

    private ArrayList<AppDetail> mAppDetails;
    private ArrayList<AppDetail> mAppSortedWeight;
    private long mCurrent;

    private HashMap<String, Integer> mMapFrequencies;
    private HashMap<String, Long> mMapLastTimeUsed;
    private HashMap<String, TextView> mMapTextView;

    private int counter;

    private DataManager(Context context) {
        mAppContext = context.getApplicationContext();
        mMapFrequencies = new HashMap<>();
        mMapLastTimeUsed = new HashMap<>();
        mAppDetails = new ArrayList<>();
        mAppSortedWeight = new ArrayList<>();
        mMapTextView = new HashMap<>();
        mPackageManager = context.getPackageManager();
        counter = 0;
        load();
        getAppList();
    }

    public static DataManager getInstance(Context context) {
        if (sDataManager == null) {
            sDataManager = new DataManager(context);
        }
        return sDataManager;
    }

    public void getAppList() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = mPackageManager.queryIntentActivities(intent, 0);
        mCurrent = System.currentTimeMillis();
        for (ResolveInfo info : availableActivities) {
            AppDetail app = new AppDetail();
            app.setName(info.activityInfo.packageName)
                    .setLabel(info.loadLabel(mPackageManager).toString())
                    .setFrequencies(getFrequencies(app))
                    .setLastTimeUsed(getLastTimeUsed(app));
            app.calculateWeight(mCurrent);
            mAppDetails.add(app);
        }
        calculatePriority();
    }

    private void calculatePriority() {
        Collections.sort(mAppDetails, AppDetail.WeightComparator);
        int index = 0;
        int priorityLevel = 0;
        int mainGroupSize = getMainGroupSize();
        for (; index < mainGroupSize; index++) {
            mAppDetails.get(index).setPriorityLevel(priorityLevel);
        }
        priorityLevel++;
        int unitSize = (mAppDetails.size() - mainGroupSize) / 6;
        for (; index < mainGroupSize + unitSize; index++) {
            mAppDetails.get(index).setPriorityLevel(priorityLevel);
        }
        priorityLevel++;
        for (; index < mainGroupSize + unitSize * 3; index++) {
            mAppDetails.get(index).setPriorityLevel(priorityLevel);
        }
        priorityLevel++;
        for (; index < mAppDetails.size(); index++) {
            mAppDetails.get(index).setPriorityLevel(priorityLevel);
        }
        mAppSortedWeight.addAll(mAppDetails);
        Collections.sort(mAppDetails, AppDetail.LabelComparator);
    }

    public ArrayList<AppDetail> getAppDetails() {
        return mAppDetails;
    }

    public void click(AppDetail app) {
        counter++;
        mCurrent = System.currentTimeMillis();
        app.click();
        app.calculateWeight(mCurrent);

        for (int i = 0; i < mAppSortedWeight.size(); i++) {
            if (mAppSortedWeight.get(i).getName().equals(app.getName())) {
                if (i > 0) {
                    AppDetail app2 = mAppSortedWeight.get(i - 1);
                    if (app.getWeight() > app2.getWeight()) {
                        int app2priority = app2.getPriorityLevel();
                        app2.setPriorityLevel(app.getPriorityLevel());
                        app.setPriorityLevel(app2priority);

                        app2 = mAppSortedWeight.remove(i - 1);
                        mAppSortedWeight.add(i, app2);
                    }

                }
            }
        }

        if (counter == 50) {
            save();
        }
        return;
    }

    public float getTextSize(int priorityLevel) {
        switch (priorityLevel) {
            case 0:
                return mAppContext.getResources().getDimension(R.dimen.textsize_priority_0);
            case 1:
                return mAppContext.getResources().getDimension(R.dimen.textsize_priority_1);
            case 2:
                return mAppContext.getResources().getDimension(R.dimen.textsize_priority_2);
            default:
                return mAppContext.getResources().getDimension(R.dimen.textsize_priority_3);
        }
    }

    public void save() {
        Log.e("state", "save data");
        File saveFile = new File(mAppContext.getFilesDir()
                , mAppContext.getString(R.string.save_file));
        StringBuilder data = new StringBuilder();
        data.append("<data>");
        for (AppDetail app : mAppDetails) {
            data.append(app.toString());
        }
        data.append("</data>");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(saveFile));
            writer.println(data.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Log.e("state", "load data");
        mMapLastTimeUsed.clear();
        mMapFrequencies.clear();

        File saveFile = new File(mAppContext.getFilesDir()
                , mAppContext.getString(R.string.save_file));
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new BufferedReader(new FileReader(saveFile)));
            int eventType = parser.next();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName()
                        .equals(AppDetail.APP)) {
                    String name = "";
                    while (eventType != XmlPullParser.END_TAG || !parser.getName()
                            .equals(AppDetail.APP)) {
                        if (eventType == XmlPullParser.START_TAG) {
                            switch (parser.getName()) {
                                case AppDetail.NAME:
                                    parser.next();
                                    name = parser.getText();
                                    break;
                                case AppDetail.FREQUENCY:
                                    parser.next();
                                    mMapFrequencies.put(name, Integer.parseInt(parser.getText()));
                                    break;
                                case AppDetail.LAST_TIME_USED:
                                    parser.next();
                                    mMapLastTimeUsed.put(name, Long.parseLong(parser.getText()));
                                    break;
                            }
                        }
                        eventType = parser.next();
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e("error", e.getLocalizedMessage());
        } catch (FileNotFoundException e) {
            Log.e("error", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("error", e.getLocalizedMessage());
        }
    }

    public int getFrequencies(AppDetail appDetail) {
        if (mMapFrequencies.containsKey(appDetail.getName())) {
            return mMapFrequencies.get(appDetail.getName());
        }
        return 0;
    }

    public long getLastTimeUsed(AppDetail appDetail) {
        if (mMapLastTimeUsed.containsKey(appDetail.getName())) {
            return mMapLastTimeUsed.get(appDetail.getName());
        }
        return 0;
    }

    public int getMainGroupSize() {
        return 4;
    }

    public void putTextView(String key, TextView value) {
        mMapTextView.put(key, value);
    }
}
