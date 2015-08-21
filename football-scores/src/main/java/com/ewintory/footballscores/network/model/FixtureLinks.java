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

public final class FixtureLinks {

    @Expose
    private HrefWrapper self;
    @Expose
    @SerializedName("soccerseason")
    private HrefWrapper soccerSeason;
    @Expose
    private HrefWrapper homeTeam;
    @Expose
    private HrefWrapper awayTeam;

    public HrefWrapper getSelf() {
        return self;
    }

    public FixtureLinks setSelf(HrefWrapper self) {
        this.self = self;
        return this;
    }

    public HrefWrapper getSoccerSeason() {
        return soccerSeason;
    }

    public FixtureLinks setSoccerSeason(HrefWrapper soccerSeason) {
        this.soccerSeason = soccerSeason;
        return this;
    }

    public HrefWrapper getHomeTeam() {
        return homeTeam;
    }

    public FixtureLinks setHomeTeam(HrefWrapper homeTeam) {
        this.homeTeam = homeTeam;
        return this;
    }

    public HrefWrapper getAwayTeam() {
        return awayTeam;
    }

    public FixtureLinks setAwayTeam(HrefWrapper awayTeam) {
        this.awayTeam = awayTeam;
        return this;
    }
}
