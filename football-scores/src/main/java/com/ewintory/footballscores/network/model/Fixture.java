/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.footballscores.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class Fixture {

    @SerializedName("_links")
    @Expose
    private FixtureLinks Links;
    @Expose
    private String date;
    @Expose
    private String status;
    @Expose
    @SerializedName("matchday")
    private Long matchDay;
    @Expose
    private String homeTeamName;
    @Expose
    private String awayTeamName;
    @Expose
    private Result result;

    public FixtureLinks getLinks() {
        return Links;
    }

    public Fixture setLinks(FixtureLinks links) {
        Links = links;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Fixture setDate(String date) {
        this.date = date;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Fixture setStatus(String status) {
        this.status = status;
        return this;
    }

    public Long getMatchDay() {
        return matchDay;
    }

    public Fixture setMatchDay(Long matchDay) {
        this.matchDay = matchDay;
        return this;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public Fixture setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
        return this;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Fixture setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public Fixture setResult(Result result) {
        this.result = result;
        return this;
    }
}
