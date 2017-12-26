package com.blogspot.bihaika.android.simpletextlauncher;

import java.util.Comparator;

/**
 * Created by Baihaki Dwi on 22/12/2017.
 */

public class AppDetail {
    private static final int HOUR = 3600000;
    private static final int DAY = HOUR * 24;
    private static final int WEEK = DAY * 7;

    public static final String APP = "app";
    public static final String NAME = "name";
    public static final String FREQUENCY = "frequency";
    public static final String LAST_TIME_USED = "lasttimeused";

    String mLabel;
    String mName;
    int mFrequencies;
    long mLastTimeUsed;
    int mPriorityLevel;
    int mWeight;

    public String getLabel() {
        return mLabel;
    }

    public AppDetail setLabel(String label) {
        mLabel = label;
        return this;
    }

    public String getName() {
        return mName;
    }

    public AppDetail setName(String name) {
        mName = name;
        return this;
    }

    public int getFrequencies() {
        return mFrequencies;
    }

    public AppDetail setFrequencies(int frequencies) {
        mFrequencies = frequencies;
        return this;
    }

    public long getLastTimeUsed() {
        return mLastTimeUsed;
    }

    public AppDetail setLastTimeUsed(long lastTimeUsed) {
        mLastTimeUsed = lastTimeUsed;
        return this;
    }

    public int getPriorityLevel() {
        return mPriorityLevel;
    }

    public AppDetail setPriorityLevel(int priorityLevel) {
        mPriorityLevel = priorityLevel;
        return this;
    }

    public int getWeight() {
        return mWeight;
    }

    public void click() {
        mFrequencies++;
        mLastTimeUsed = System.currentTimeMillis();
    }

    public void calculateWeight(long currentTime) {
        long differences = currentTime - mLastTimeUsed;
        mWeight = 0;
        if (differences < HOUR) {
            mWeight += 7;
        } else if (differences < DAY) {
            mWeight += 5;
        } else if (differences < WEEK) {
            mWeight += 2;
        }
        mWeight += mFrequencies;
    }

    public static Comparator<AppDetail> LabelComparator = new Comparator<AppDetail>() {
        @Override
        public int compare(AppDetail o1, AppDetail o2) {
            return o1.getLabel().compareTo(o2.getLabel());
        }
    };

    private static int compareNumber(long n1, long n2) {
        if (n1 > n2) return 1;
        if (n1 < n2) return -1;
        return 0;
    }

    public static Comparator<AppDetail> WeightComparator = new Comparator<AppDetail>() {
        @Override
        public int compare(AppDetail o1, AppDetail o2) {
            int result = compareNumber(o2.getWeight(), o1.getWeight());
            if (result == 0) {
                return compareNumber(o1.getLastTimeUsed(), o2.getLastTimeUsed());
            }
            return result;
        }
    };

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("<" + APP + ">");
        s.append("<" + NAME + ">" + getName() + "</" + NAME + ">");
//        s.append("<label>" + getLabel() + "</label>");
        s.append("<" + FREQUENCY + ">" + getFrequencies() + "</" + FREQUENCY + ">");
        s.append("<" + LAST_TIME_USED + ">" + getLastTimeUsed() + "</" + LAST_TIME_USED + ">");
        s.append("</" + APP + ">");
        return s.toString();
    }
}

