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

public class Bootstrap {

    public static void organization(Organization o){

        // Device Type Property Types
        DeviceTypePropertyType.bootstrapDevicePropertyTypesForOrganization(o);
        // Device Type Property Statuses
        DeviceTypePropertyStatus.bootstrapDevicePropertyStatusesForOrganization(o);
        // Device Type Properties
        DeviceTypeProperty.bootstrapDevicePropertiesForOrganization(o);
        // Device Type(s)
        DeviceType.bootstrapDeviceTypesForOrganization(o);

        // Project Basic Types
        ProjectType.bootstrapProjectTypesForOrganization(o);
        ProjectPriority.bootstrapProjectPrioritiesForOrganization(o);
        ProjectStatus.bootstrapProjectStatusesForOrganization(o);

        // Task Basic Types
        TaskStatus.bootstrapTaskStatusesForOrganization(o);
        TaskType.bootstrapTaskTypesForOrganization(o);
        TaskPriority.bootstrapTaskPrioritiesForOrganization(o);

        // Place Basic Types
        PlaceType.bootstrapPlaceTypesForOrganization(o);

        // Product Basic Types
        ProductType.bootstrapProductTypesForOrganization(o);

        // Roles
        Role.bootstrapRolesForOrganization(o);

        // Create encryption / decryption AES key
        OrganizationKey.createKeyForOrganization(o);
    }
}
