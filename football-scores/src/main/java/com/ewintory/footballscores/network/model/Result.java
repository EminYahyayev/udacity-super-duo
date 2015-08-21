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


public final class Result {

    @Expose
    private Long goalsHomeTeam;
    @Expose
    private Long goalsAwayTeam;

    public Long getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public Result setGoalsHomeTeam(Long goalsHomeTeam) {
        this.goalsHomeTeam = goalsHomeTeam;
        return this;
    }

    public Long getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public Result setGoalsAwayTeam(Long goalsAwayTeam) {
        this.goalsAwayTeam = goalsAwayTeam;
        return this;
    }
}
