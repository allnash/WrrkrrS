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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product extends BaseModel
{
	/**
	 *  Product Table
	 */

	public String  name;
	public String  description;
	public String  notes;
	public Boolean verified;
	public Boolean enabled;
	@ManyToOne
    public User by;

    @ManyToMany(cascade=CascadeType.PERSIST)
	public Set<Tag> tags;

	@JsonIgnore
	@OneToMany(mappedBy = "product")
	public Set<ProductRelease> releases;

	@Transient
    @JsonProperty("releases_count")
    public Integer releasesCount;

	public Product() { }

	public Product(String name, String description, User by) {
		this.name = name;
		this.description =  description;
		this.verified = true;
		this.enabled = true;
		this.by = by;
	}
	
	public static Finder<Long, Product> find = new Finder<>(Product.class);

    public static List<Product> all(Map parameterMap) {
        if(parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }
	
	public static Product findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public User getBy() {
		return by;
	}

	public void setBy(User by) {
		this.by = by;
	}

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<ProductRelease> getReleases() {
        return releases;
    }

    public void setReleases(Set<ProductRelease> releases) {
        this.releases = releases;
    }

    public Integer getReleasesCount() {
        return this.releases.size();
    }
}

