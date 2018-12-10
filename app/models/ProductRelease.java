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
import io.ebean.annotation.CreatedTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"product_id", "version_x", "version_y", "version_z"})
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRelease extends BaseModel {

    private static final String SUFFIX = ".";
    @JsonIgnore
    public static final String TEMPLATE = "<blockquote>Provide brief information about the release.</blockquote><h5>" +
            "Features<span style=\"color: inherit;\">:</span></h5><p></p><ul><li>Feature 1:</li><li>Feature 2:</li>" +
            "<li>Feature 3:</li></ul><p></p><h5>Bugs:</h5><p></p><ul><li><span style=\"font-weight: bold;\">Bug 1 -</span>" +
            "&nbsp;Bug description</li><li><span style=\"font-weight: bold;\">Bug 2 </span>-&nbsp;Bug description</li>" +
            "<li><span style=\"font-weight: bold;\">Bug 3 -&nbsp;</span>Bug description</li>" +
            "</ul><p></p><h5>Security:&nbsp;</h5><p>(Any fixes made, mention and briefly as possible.)</p>";

    @JsonProperty("download_url")
    public String downloadUrl;

    // Ignore this on purpose
    @JsonIgnore
    @Column(nullable = false)
    @ManyToOne
    public Product product;

    @Column(length = 64, nullable = false)
    public String name;
    @Column(nullable = false)
    @JsonProperty("version_x")
    public Integer versionX;
    @Column(nullable = false)
    @JsonProperty("version_y")
    public Integer versionY;
    @Column(nullable = false)
    @JsonProperty("version_z")
    public Integer versionZ;

    @JsonProperty("release_version")
    @Transient
    public String releaseVersion;

    // Note: Beta release should mark this as true.
    public boolean beta;

    // Shipped releases SHOULD NOT BE EDITED.
    public boolean shipped;

    @CreatedTimestamp
    @JsonProperty("when_shipped")
    Timestamp whenShipped;

    @JsonIgnore
    @OneToOne
    public ProductRelease upgrade;

    @Transient
    @JsonProperty("upgrade_id")
    public String upgradeId;

    @JsonIgnore
    @ManyToOne
    public User by;

    @OneToOne
    @JsonIgnore
    public Html html;

    @Transient
    @JsonProperty("release_note_html")
    public String releaseNoteHtml;

    public ProductRelease(String name, Html html, User by) {
        this.name = name;
        this.html = html;
        this.by = by;
        this.shipped = false;
        this.beta = false;
    }

    public static Finder<String, ProductRelease> find = new Finder<>(ProductRelease.class);

    public static ProductRelease findByProductIdReleasId(String productId, String releaseId) {
        return find.query().where()
                .eq("id", releaseId)
                .eq("product_id", productId).findOne();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersionX() {
        return versionX;
    }

    public void setVersionX(Integer versionX) {
        this.versionX = versionX;
    }

    public Integer getVersionY() {
        return versionY;
    }

    public void setVersionY(Integer versionY) {
        this.versionY = versionY;
    }

    public Integer getVersionZ() {
        return versionZ;
    }

    public void setVersionZ(Integer versionZ) {
        this.versionZ = versionZ;
    }

    public boolean isBeta() {
        return beta;
    }

    public void setBeta(boolean beta) {
        this.beta = beta;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getReleaseVersion() {
        return this.versionX + SUFFIX + this.versionY + SUFFIX + this.versionZ;
    }

    public ProductRelease getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(ProductRelease upgrade) {
        this.upgrade = upgrade;
    }

    public String getReleaseNoteHtml() {
        if (this.html != null) {
            return this.html.getData();
        } else {
            return null;
        }
    }

    public boolean isShipped() {
        return shipped;
    }

    public void setShipped(boolean shipped) {
        this.shipped = shipped;
    }

    public Timestamp getWhenShipped() {
        return whenShipped;
    }

    public void setWhenShipped(Timestamp whenShipped) {
        this.whenShipped = whenShipped;
    }

    public String getUpgradeId() {
        if (this.upgrade != null) {
            return this.upgrade.getId();
        } else {
            return null;
        }
    }

    public void setUpgradeId(String upgradeId) {
        this.upgradeId = upgradeId;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public Html getHtml() {
        return html;
    }

    public void setHtml(Html html) {
        this.html = html;
    }
}

