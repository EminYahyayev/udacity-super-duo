package com.ewintory.footballscores.util;

import android.content.res.Resources;

import com.ewintory.footballscores.R;


public final class Utilities {

    public interface Leagues {
        int BUNDESLIGA1 = 394;
        int BUNDESLIGA2 = 395;
        int LIGUE1 = 396;
        int LIGUE2 = 397;
        int PREMIER_LEAGUE = 398;
        int PRIMERA_DIVISION = 399;
        int SEGUNDA_DIVISION = 400;
        int SERIE_A = 401;
        int PRIMERA_LIGA = 402;
        int BUNDESLIGA3 = 403;
        int EREDIVISIE = 404;
        int CHAMPIONS_LEAGUE = 362;
    }

    public static String getLeague(Resources res, int leagueId) {
        switch (leagueId) {
            case Leagues.CHAMPIONS_LEAGUE:
                return res.getString(R.string.champions_league);
            case Leagues.SERIE_A:
                return res.getString(R.string.seriaa);
            case Leagues.PREMIER_LEAGUE:
                return res.getString(R.string.premierleague);
            case Leagues.PRIMERA_DIVISION:
                return res.getString(R.string.primeradivison);
            case Leagues.BUNDESLIGA1:
                return res.getString(R.string.bundesliga1);
            case Leagues.BUNDESLIGA2:
                return res.getString(R.string.bundesliga2);
            case Leagues.BUNDESLIGA3:
                return res.getString(R.string.bundesliga3);
            case Leagues.LIGUE1:
                return res.getString(R.string.ligue1);
            case Leagues.LIGUE2:
                return res.getString(R.string.ligue2);
            case Leagues.SEGUNDA_DIVISION:
                return res.getString(R.string.segunda_division);
            case Leagues.PRIMERA_LIGA:
                return res.getString(R.string.primera_liga);
            case Leagues.EREDIVISIE:
                return res.getString(R.string.eredivise);
            default:
                return res.getString(R.string.undefined_league);
        }
    }

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
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
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
