package com.ewintory.footballscores.util;

import android.content.res.Resources;

import com.ewintory.footballscores.R;


public final class Utilities {

    public interface Leagues {
        int SERIE_A = 401;
        int LIGUE_2 = 397;
        int PREMIER_LEAGUE = 402;
        int CHAMPIONS_LEAGUE = 362;
        int PRIMERA_DIVISION = 399;
        int BUNDESLIGA = 403;
    }

    @Deprecated
    public static boolean isValidLeague(int leagueId) {
        return leagueId == Leagues.SERIE_A ||
                leagueId == Leagues.LIGUE_2 ||
                leagueId == Leagues.PREMIER_LEAGUE ||
                leagueId == Leagues.CHAMPIONS_LEAGUE ||
                leagueId == Leagues.PRIMERA_DIVISION ||
                leagueId == Leagues.BUNDESLIGA;
    }

    @Deprecated
    public static String getMatchDay(Resources res, int matchDay, int leagueId) {
        if (leagueId == Leagues.CHAMPIONS_LEAGUE) {
            if (matchDay <= 6) {
                return res.getString(R.string.group_stages, matchDay);
            } else if (matchDay == 7 || matchDay == 8) {
                return res.getString(R.string.first_knockout_round);
            } else if (matchDay == 9 || matchDay == 10) {
                return res.getString(R.string.quarter_final);
            } else if (matchDay == 11 || matchDay == 12) {
                return res.getString(R.string.semi_final);
            } else {
                return res.getString(R.string.final_text);
            }
        } else {
            return res.getString(R.string.match_day, matchDay);
        }
    }

    public static String getScores(Resources res, int homeGoals, int awayGoals) {
        if (homeGoals >= 0 && awayGoals >= 0) {
            return res.getString(R.string.scores, homeGoals, awayGoals);
        } else {
            return res.getString(R.string.scores, "?", "?");
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {return R.drawable.no_icon;}
        switch (teamname) {
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }

    private Utilities() {
        throw new AssertionError("No instances.");
    }
}
