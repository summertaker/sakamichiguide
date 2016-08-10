package com.summertaker.sakamichiguide.util;

import android.content.Context;
import android.content.res.Resources;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.Config;

public class Translator {

    Resources mResources;
    String mLocale;

    public Translator(Context context) {
        this.mResources = context.getResources();
        this.mLocale = Util.getLocaleStrng(context);
    }

    public String translateTeam(String groupId, String name) {
        String result = name;
        //Log.e("###", name);

        if (groupId.equals(Config.GROUP_ID_KEYAKIZAKA46)) {

        } else {
            result = Util.removeSpace(result.replace("チーム", "").replace("队", "").replace("Team", "").replace("TEAM", ""));
            result = String.format(mResources.getString(R.string.team_s), result);

            if (name.contains("Understudy") || name.contains("研究生")) {
                result = mResources.getString(R.string.understudy);
            } else if (name.contains("Part-time AKB")) {
                //result = result.replace("Part-timeAKB", "");
                result = mResources.getString(R.string.part_time_akb);
            }
        }

        return result;
    }

    public String translateGroupTeam(String groupTeam) { // AKB48チームA, AKB48チームK兼任, NGT48兼任 ...
        String result;

        if (groupTeam == null || groupTeam.isEmpty()) {
            return "";
        } else {
            String group;
            String team;
            String concurrentPositionText;

            if (groupTeam.contains("兼任")) {
                groupTeam = Util.removeSpace(groupTeam.replace("兼任", "")).trim();
                concurrentPositionText = " " + mResources.getString(R.string.concurrent_position);
            } else {
                concurrentPositionText = "";
            }

            if (groupTeam.contains("チーム")) {
                // AKB48チームK, AKB48チームK兼任
                String[] concurrentTeamArray = groupTeam.split("チーム");
                group = concurrentTeamArray[0];
                team = String.format(mResources.getString(R.string.team_s), concurrentTeamArray[1]);
                result = group + " " + team + concurrentPositionText;
            } else {
                // NGT48, NGT48兼任
                group = groupTeam;
                result = group + concurrentPositionText;
            }
        }
        return result;
    }
}
