package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class BirthMonthData implements Serializable, Comparable<BirthMonthData> {

    private static final long serialVersionUID = 1L;

    private String month;
    private int count;
    private MemberData memberData;

    public BirthMonthData() {

    }

    public BirthMonthData(String month, int count) {
        this.month = month;
        this.count = count;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public void setMemberData(MemberData memberData) {
        this.memberData = memberData;
    }

    @Override
    public int compareTo(BirthMonthData another) {
        //return countText.compareTo(another.countText);

        return this.count - another.getCount(); //ascending order
        //return another.getCount() - this.count; //descending order
    }
}
