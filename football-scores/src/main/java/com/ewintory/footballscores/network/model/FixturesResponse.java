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

import java.util.ArrayList;
import java.util.List;

public final class FixturesResponse {

    @Expose
    private String timeFrameStart;
    @Expose
    private String timeFrameEnd;
    @Expose
    private Long count;
    @Expose
    private List<Fixture> fixtures = new ArrayList<>();

    public String getTimeFrameStart() {
        return timeFrameStart;
    }

    public FixturesResponse setTimeFrameStart(String timeFrameStart) {
        this.timeFrameStart = timeFrameStart;
        return this;
    }

    public String getTimeFrameEnd() {
        return timeFrameEnd;
    }

    public FixturesResponse setTimeFrameEnd(String timeFrameEnd) {
        this.timeFrameEnd = timeFrameEnd;
        return this;
    }

    public Long getCount() {
        return count;
    }

    public FixturesResponse setCount(Long count) {
        this.count = count;
        return this;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public FixturesResponse setFixtures(List<Fixture> fixtures) {
        this.fixtures = fixtures;
        return this;
    }
}
