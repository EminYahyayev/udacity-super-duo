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

public final class Team {

    @Expose
    private String name;

    @Expose
    private String crestUrl;

    @SerializedName("_links")
    @Expose
    private TeamLinks links;

    public String getName() {
        return name;
    }

    public Team setName(String name) {
        this.name = name;
        return this;
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    public Team setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
        return this;
    }

    public TeamLinks getLinks() {
        return links;
    }

    public Team setLinks(TeamLinks links) {
        this.links = links;
        return this;
    }
}
