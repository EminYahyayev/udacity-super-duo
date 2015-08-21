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

public final class SeasonLinks {

    @Expose
    private HrefWrapper self;
    @Expose
    private HrefWrapper teams;
    @Expose
    private HrefWrapper fixtures;
    @Expose
    private HrefWrapper leagueTable;

    public HrefWrapper getSelf() {
        return self;
    }

    public SeasonLinks setSelf(HrefWrapper self) {
        this.self = self;
        return this;
    }

    public HrefWrapper getTeams() {
        return teams;
    }

    public SeasonLinks setTeams(HrefWrapper teams) {
        this.teams = teams;
        return this;
    }

    public HrefWrapper getFixtures() {
        return fixtures;
    }

    public SeasonLinks setFixtures(HrefWrapper fixtures) {
        this.fixtures = fixtures;
        return this;
    }

    public HrefWrapper getLeagueTable() {
        return leagueTable;
    }

    public SeasonLinks setLeagueTable(HrefWrapper leagueTable) {
        this.leagueTable = leagueTable;
        return this;
    }
}
