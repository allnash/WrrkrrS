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

package cmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ektorp.support.CouchDbDocument;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"id", "revision"})
public class DeviceHeartbeat extends CouchDbDocument {

    // for JSON serialization we are ignoring as this is a reference to floor ID.
    private String deviceId;
    @JsonProperty("when_received")
    private long whenReceived;
    private String type;

    public DeviceHeartbeat(){
        super();
        this.type = "DEVICE_HEARTBEAT";
    }

    /**
     * @param jsonSchemaId is a JSON SCHEMA.
     *        We can point to the schema for validation if needed.
     * The JSON schema ID
     */
    @JsonProperty("json_schema_id")
    private String jsonSchemaId;

    /**
     * @param data is a JSON MAP.
     *        We CHEAT the POJO and store the data as a JSON into the database so we can validate schema later.
     * The JSON data
     */
    private Map<String, Object> data = new HashMap<String, Object>();

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getJsonSchemaId() {
        return jsonSchemaId;
    }

    public void setJsonSchemaId(String jsonSchemaId) {
        this.jsonSchemaId = jsonSchemaId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public long getWhenReceived() {
        return whenReceived;
    }

    public void setWhenReceived(long whenReceived) {
        this.whenReceived = whenReceived;
    }

    public void setType(String type) {
        this.type = type;
    }
}
