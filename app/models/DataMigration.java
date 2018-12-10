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

import com.github.javafaker.Faker;
import db.SeedAcme;
import db.SeedDevices;
import db.SeedOmegaTrace;
import db.SeedOtacos;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;
import play.Logger;
import utils.Utils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static utils.GoogleAuthenticator.getRandomSecretKey;


@Entity
public class DataMigration extends Model {

    public static String OMEGATRACE_DOMAIN = "omegatrace.com";
    public static String GIMBAL_DOMAIN = "gimbal.com";
    public static String DEFAULT_TEST_PASSWORD = "password";

    /**
     * Auto Server start Data Migrations Class.
     */
    @Id
    private String id;
    private String name;
    private String description;
    private String metaData;
    private Timestamp startDate;
    private Timestamp completedDate;
    private DataMigrationState migrationState;

    @Version
    Long version;

    @Transient
    History currentHistory;

    @CreatedTimestamp
    Timestamp whenCreated;

    @UpdatedTimestamp
    Timestamp whenUpdated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompletedDate(Timestamp completedDate) {
        this.completedDate = completedDate;
    }

    public DataMigration.DataMigrationState getMigrationState() {
        return migrationState;
    }

    public void setMigrationState(DataMigration.DataMigrationState migrationState) {
        this.migrationState = migrationState;
    }

    public enum DataMigrationState {
        RUNNING, COMPLETE
    }

    public DataMigration() {
    }

    public void save() {
        super.save();
        Logger.info(this.getClass().getCanonicalName() + " - " + this.getName() + " saved at " + Calendar.getInstance().getTime().toString());
    }

    public DataMigration(String id, String name, String description, String meta_data) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.metaData = meta_data;
        this.startDate = Utils.getCurrentTimestamp();
        this.migrationState = DataMigrationState.RUNNING;
    }

    private static Finder<String, DataMigration> find = new Finder<>(DataMigration.class);

    public static List<DataMigration> all() {
        return find.all();
    }

    // STATIC METHODS

    private static DataMigration findById(String migration_number) {
        return find.query().where().eq("id", migration_number).findOne();
    }

    public static void run_data_mirations() {


        // Load some sample data in non prod env
        if (Utils.isDebugMode()) {
            // Omegatrace tenancy
            createOrganizationsAndBootstrap();
            createUsers();
            seedTestDevicesForOmegaTraceAndOtherUsers();
            importGimbalBeaconDevices();
            createWrrKrrReaderProduct();

            // DEMO tenancies
            // Outstanding tacos Inc.
            createOtacosTenancy();
            createOtacosDemoData();
            // Acme Inc.
            createAcmeOfficeTenancy();
            createAcmeOfficeTDemoData();

            // Add Omegatrace as a collaborator to all organizations.
            addomegatraceAsCollaboratorToAllOrganizations();
            // createOmegaTraceDemoShifts();
            // createOmegaTraceDemoSightings();
            // updateOrphanedDevicesWithoutOwners();
            // updateReaderVisitReportLaptopCounts();
        }
        // Production Data migrations
        // Include them after this line <<<
        addBeaconDeviceTypeToAllOrganizations();

    }

    private static boolean present(String migrationNumber) {
        return DataMigration.findById(migrationNumber) != null;
    }

    private static void createOrganizationsAndBootstrap() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1513349156";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Add Organizations",
                "Add new organizations for our OmegaTrace / Test Users", "NONE");
        d.save();


        // RUN WHATEVER DATA MIGRATION YOU WANT
        // omegatrace Org
        Organization o = new Organization("OmegaTrace Inc", "omegatrace");
        o.setEmailDomain(OMEGATRACE_DOMAIN);
        License l = o.getLicense();
        l.setClient(false);
        o.setApproved(true);
        o.setEnabled(true);
        o.save();
        Bootstrap.organization(o);

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }


    private static void createUsers() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1513349167";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Add Users",
                "Add test users to sample organizations", "NONE");
        d.save();


        // RUN WHATEVER DATA MIGRATION YOU WANT

        // omegatrace Org User
        Organization o = Organization.findByEmailDomain(OMEGATRACE_DOMAIN);

        // Create an image
        TypeImage opsUserImage = new TypeImage(o,
                "https://s3.amazonaws.com/wrrkrr-development/" +
                        "profiles/8cc6f425-c289-4cee-a39c-c23366ea7da1/90854ba4-b5e8-4f55-9e03-147715f7e110/" +
                        "16a1k608d1th5h14ukrj6ltet7m412a289m6li6dbsgti2al6l4o.png");
        opsUserImage.save();

        Place opsUserPlace = new Place("home", PlaceType.findByName("HOME", o), o);
        opsUserPlace.save();
        // Ops User
        User opsUser = null;
        opsUser = new User("Ops", "OT", "ops@" + OMEGATRACE_DOMAIN, getRandomSecretKey(),
                opsUserPlace, o);
        try {
            opsUser.setStrongPassword(DEFAULT_TEST_PASSWORD);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        opsUser.setConfirmed(true);
        opsUser.setSuperadmin(true);
        opsUser.setAdmin(true);
        opsUser.setImage(opsUserImage);
        opsUser.setRole(Role.findByName("Admin", o));
        opsUser.save();


        // Create an image
        TypeImage testUserImage = new TypeImage(o,
                "https://s3.amazonaws.com/wrrkrr-development/" +
                        "profiles/8cc6f425-c289-4cee-a39c-c23366ea7da1/90854ba4-b5e8-4f55-9e03-147715f7e110/" +
                        "16a1k608d1th5h14ukrj6ltet7m412a289m6li6dbsgti2al6l4o.png");
        testUserImage.save();

        Place testUserPlace = new Place("home", PlaceType.findByName("HOME", o), o);
        testUserPlace.save();
        // Ops User
        User testUser = null;
        testUser = new User("Test", "OT", "test@" + OMEGATRACE_DOMAIN, getRandomSecretKey(),
                testUserPlace, o);
        try {
            testUser.setStrongPassword(DEFAULT_TEST_PASSWORD);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        testUser.setConfirmed(true);
        testUser.setSuperadmin(true);
        testUser.setAdmin(true);
        testUser.setImage(testUserImage);
        testUser.setRole(Role.findByName("Admin", o));
        testUser.save();

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void addomegatraceAsCollaboratorToAllOrganizations() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1502224950";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Add omegatrace as a Collaborator to all existing orgs",
                "Update all organizations and add omegatrace as a collaborator. Which is new and introduced in 0.0.17.",
                "NONE");
        d.save();

        Organization omegatraceOrganization = Organization.findByEmailDomain("" + OMEGATRACE_DOMAIN);

        // All orgs
        List<Organization> organizations = Organization.all();
        for (Organization o : organizations) {
            o.collaborators.add(omegatraceOrganization);
            o.save();
        }

        // COMPLETE DATA MIGRATION
        Logger.info("Data Migration " + d.getName() + " applied at " + Calendar.getInstance().getTime().toString());
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void seedTestDevicesForOmegaTraceAndOtherUsers() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1517334972";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Add omegatrace seed devices",
                "Add a device types, bootstrap organization with devices.",
                "NONE");
        d.save();

        // Omegatrace domain
        Organization omegatraceOrganization = Organization.findByEmailDomain(OMEGATRACE_DOMAIN);
        SeedDevices.setTestDevices(omegatraceOrganization);

        // COMPLETE DATA MIGRATION
        Logger.info("Data Migration " + d.getName() + " applied at " + Calendar.getInstance().getTime().toString());
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void importGimbalBeaconDevices() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1517336807";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Import Gimbal beacons from your account. (DISABLED)",
                "Add Gimbal devices by Gimbal ID into device external ID.",
                "NONE");
        d.save();
        d.save();

        // Create gimbal org if its not present
        /*
        Organization organization = Organization.findByEmailDomain(OMEGATRACE_DOMAIN);
        Organization manufacturer = Organization.findByEmailDomain(GIMBAL_DOMAIN);
        if (manufacturer == null) {
            manufacturer = new Organization("Gimbal (Qualcomm)", "gimbal");
            manufacturer.setEmailDomain(GIMBAL_DOMAIN);
            manufacturer.save();
        }

        // Import all into omegatrace
        User adminUser = User.findByEmail("ops@" + OMEGATRACE_DOMAIN, Organization.findByEmailDomain(OMEGATRACE_DOMAIN));

        SeedDevices.importGimbalBeaconDevices(adminUser, organization, manufacturer);
        */

        // COMPLETE DATA MIGRATION
        Logger.info("Data Migration " + d.getName() + " applied at " + Calendar.getInstance().getTime().toString());
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void createWrrKrrReaderProduct() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1524675623";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Add WrrKrr Reader Product",
                "Create WrrKrr Reader product, Add a sample release.",
                "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        // Fetch bootstrap info
        Organization o = Organization.findByEmailDomain(OMEGATRACE_DOMAIN);
        User adminUser = User.findByEmail("ops@" + OMEGATRACE_DOMAIN, o);

        // Create WrrKrr Product
        Product wrrKrrProduct = new Product();
        wrrKrrProduct.setName("WrrKrr Reader");
        wrrKrrProduct.setDescription("WrrKrr Reader is an on-site reader that " +
                "enables location sensing and automation.");
        wrrKrrProduct.setEnabled(true);
        wrrKrrProduct.setNotes("Provided by OmegaTrace INC.");
        wrrKrrProduct.setBy(adminUser);
        wrrKrrProduct.setVerified(true);
        wrrKrrProduct.setId("c56aa2a2-f6ef-4b90-94f1-87db978dec83");
        wrrKrrProduct.save();

        // Create Product release for WrrKrr
        Faker faker = new Faker();
        String releaseName = faker.ancient().god().toLowerCase() + "-" + faker.ancient().primordial().toLowerCase();
        Html html = new Html(ProductRelease.TEMPLATE, o);
        html.save();
        ProductRelease release = new ProductRelease(releaseName, html, adminUser);
        release.setDownloadUrl("");
        release.setName("ares-nyx");
        release.setVersionX(0);
        release.setVersionY(0);
        release.setVersionZ(1);
        release.setProduct(wrrKrrProduct);
        release.setId("999f8670-5767-4e0c-a515-df4b539cff68");
        release.save();

        // COMPLETE DATA MIGRATION
        Logger.info("Data Migration " + d.getName() + " applied at " + Calendar.getInstance().getTime().toString());
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void createOtacosTenancy() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1527013460";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Otacos tenancy",
                "Add Otacos tenancy", "NONE");
        d.save();

        SeedOtacos.createOrganization();

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void createOtacosDemoData() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1527013491";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Otacos demo data",
                "Populate all Otacos tenancy demo data", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        SeedOtacos.createTestUsers();
        SeedOtacos.createSampleProjectAndTasksInOtacosTenant();
        SeedOtacos.createSampleFloorPlansAndZones();
        SeedOtacos.createFakeUsers();
        SeedOtacos.createDemoDevices();
        SeedOtacos.createDemoShifts();

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void createAcmeOfficeTenancy() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1527013503";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Acme office tenancy",
                "Add Acme office tenancy", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        SeedAcme.createOrganization();

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();
    }


    private static void createAcmeOfficeTDemoData() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1527013511";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Acme demo data",
                "Populate all Acme tenancy demo data", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        SeedAcme.createTestUsers();
        SeedAcme.createSampleProjectAndTasksInOtacosTenant();
        SeedAcme.createSampleFloorPlansAndZones();
        SeedAcme.createFakeUsers();
        SeedAcme.createDemoDevices();

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void createOmegaTraceDemoShifts() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1527097205";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Omegatrace demo shift data",
                "Populate a few months of demo shift data", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        SeedOmegaTrace.createDemoShifts();

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }


    private static void createOmegaTraceDemoSightings() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1527690309";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Omegatrace demo sightings data",
                "Populate a few months of demo sighting data", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        // Fetch bootstrap info
        Organization organization = Organization.findByEmailDomain(DataMigration.OMEGATRACE_DOMAIN);
        User adminUser = User.findByEmail("ops@" + organization.getEmailDomain(), organization);
        User testUser = User.findByEmail("test@" + organization.getEmailDomain(), organization);
        // Update Owner for test devices.
        Device first = Device.findByExternalDeviceId("SZ4H-HVFA1");
        first.setOwner(adminUser);
        first.save();
        Device second = Device.findByExternalDeviceId("XCY9-NMZ6Y");
        second.setOwner(testUser);
        second.save();
        SeedOmegaTrace.createDemoSightings(adminUser, first);
        SeedOmegaTrace.createDemoSightings(testUser, second);

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }


    private static void updateOrphanedDevicesWithoutOwners() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1531233537";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Update orphaned devices with no owners",
                "Set ops@omegatrace as the owners", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        // Fetch bootstrap info
        Organization organization = Organization.findByEmailDomain(DataMigration.OMEGATRACE_DOMAIN);
        User adminUser = User.findByEmail("ops@" + organization.getEmailDomain(), organization);
        // Add Owner
        for (Device device : Device.findAllByOrganization(organization)) {
            if (device.getOwner() == null) {
                device.setOwner(adminUser);
                device.save();
            }
        }


        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void updateReaderVisitReportLaptopCounts() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1534452264";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Update ReaderVisitReport Laptop Counts",
                "These were messed up and need to be fixed.", "NONE");
        d.save();

        // RUN WHATEVER DATA MIGRATION YOU WANT
        // Fetch all records and recompute counts
        for (ReaderVisitReport rvp : ReaderVisitReport.find.all()) {
            int laptopCount = rvp.getDevicesCount() - (rvp.getAppleCount() + rvp.getAndroidCount());
            rvp.setLaptopCount(laptopCount);
            rvp.save();
        }

        // COMPLETE DATA MIGRATION
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

    private static void addBeaconDeviceTypeToAllOrganizations() {
        // MANDATORY INIT
        final String MIGRATION_NUMBER = "1543858548";
        if (present(MIGRATION_NUMBER))
            return;

        // CREATE DATA MIGRATION
        DataMigration d = new DataMigration(MIGRATION_NUMBER, "Add Beacon device type to all existing orgs",
                "Update all organizations and add BEACON device type. This device type will be used for " +
                        "OmegaTrace ID Cards.",
                "NONE");
        d.save();

        // All orgs
        List<Organization> organizations = Organization.all();
        for (Organization o : organizations) {
            if (DeviceType.findByNameAndOrganization("BEACON", o) == null) {
                // Create new device type "BEACON"
                DeviceType beaconDeviceType = DeviceType.add("BEACON", "Beacon device type", o);
                List<DeviceTypeProperty> properties;
                // Add properties to device types
                properties = new ArrayList<>();
                properties.add(DeviceTypeProperty.findByName("TURN_ON", o));
                properties.add(DeviceTypeProperty.findByName("TURN_OFF", o));
                TypeImage image = new TypeImage(o, "");
                image.setUrl("img/bluetooth_logo.png");
                image.setW(32);
                image.setH(32);
                image.save();
                beaconDeviceType.setProperties(properties);
                beaconDeviceType.setImage(image);
                beaconDeviceType.update();
            }
        }

        // COMPLETE DATA MIGRATION
        Logger.info("Data Migration " + d.getName() + " applied at " + Calendar.getInstance().getTime().toString());
        d.setCompletedDate(Utils.getCurrentTimestamp());
        d.setMigrationState(DataMigrationState.COMPLETE);
        d.save();

    }

}

