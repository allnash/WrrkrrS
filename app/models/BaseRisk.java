// Copyright 2018 OmegaTrace Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License

package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass
public abstract class BaseRisk extends BaseTenantModel {

    @Column(nullable = false)
    public String name;
    public String description;
    @JsonProperty("label_class")
    public String labelClass;
    @JsonProperty("i_class")
    public String iClass;

    @OneToOne
    @JsonProperty("image")
    public TypeImage image;

    // YES MEANS 1 | NO MEANS 0
    @JsonProperty("available_publicly")
    public Boolean availablePublicly;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailablePublicly() {
        return availablePublicly;
    }

    public void setAvailablePublicly(Boolean availablePublicly) {
        this.availablePublicly = availablePublicly;
    }

    public TypeImage getImage() {
        return image;
    }

    public void setImage(TypeImage image) {
        this.image = image;
    }

    public String getLabelClass() {
        return labelClass;
    }

    public void setLabelClass(String labelClass) {
        this.labelClass = labelClass;
    }

    public String getiClass() {
        return iClass;
    }

    public void setiClass(String iClass) {
        this.iClass = iClass;
    }
}
