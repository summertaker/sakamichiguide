package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class BirthDayData implements Serializable, Comparable<BirthDayData> {

    private static final long serialVersionUID = 1L;

    private int month;
    private int day;
    private MemberData memberData;

    public BirthDayData() {

    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public void setMemberData(MemberData memberData) {
        this.memberData = memberData;
    }

    @Override
    public int compareTo(BirthDayData another) {
        //return countText.compareTo(another.countText);

        return this.day - another.getDay(); //ascending order
        //return another.getCount() - this.count; //descending order
    }
}

