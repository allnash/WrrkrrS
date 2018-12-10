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
import io.ebean.annotation.Encrypted;
import play.Logger;
import play.data.validation.Constraints;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends BaseModel {

    /**
     * Users Table
     */
    @Encrypted
    public String email;

    @JsonIgnore
    @Column(length = 512)
    public String strongPassword;

    @JsonProperty("first_name")
    public String firstName;

    @JsonIgnore
    @Constraints.Required
    @JsonProperty("external_id")
    public String externalId;

    @JsonProperty("last_name")
    public String lastName;

    @Column(length = 64)
    @Constraints.Required
    @JsonProperty("phone_number")
    public String phoneNumber;

    @JsonIgnore
    @Column(length = 512)
    @Constraints.Required
    public String bio;

    @JsonIgnore
    public String TOTPKey;

    @JsonProperty("totp_configured")
    public Boolean TOTPConfigured; // YES MEANS 1 | NO MEANS 0

    @Column(length = 6)
    @Constraints.Required
    @JsonProperty("country_code")
    public String countryCode;

    @Transient
    @JsonProperty("full_name")
    public String fullName;

    @Transient
    @JsonProperty("initials")
    public String initials;

    @OneToOne
    public TypeImage image;

    @JsonIgnore
    public String confirmationHash;

    public Boolean confirmed;

    @JsonIgnore
    public String resetToken;

    @OneToOne(cascade = CascadeType.ALL)
    public Place place;

    @JsonIgnore
    public String pin;

    @JsonIgnore
    @Column(name = "organization")
    @ManyToOne
    public Organization organization;

    @Transient
    @JsonProperty("organization_id")
    public String organizationId;
    @Transient
    @JsonProperty("organization_name")
    public String organizationName;

    @Column(name = "role")
    @ManyToOne
    public Role role;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    public Settings settings;

    @JsonIgnore
    @Transient
    public License license;

    @Transient
    @JsonProperty("license_name")
    public String licenseName;

    @Transient
    @JsonProperty("license_status")
    public String licenseStatus;

    @JsonProperty("enabled")
    public Boolean enabled; // YES MEANS 1 | NO MEANS 0

    @JsonProperty("is_admin")
    public Boolean isAdmin;

    @JsonProperty("is_superadmin")
    public Boolean isSuperadmin;

    @Transient
    @JsonProperty("is_anonymous")
    public Boolean isAnonymous;

    public void setEmail(String email) {
        this.email = email;
        //  this.email = this.organization.encrypt(email.toLowerCase());
    }

    public User() {
    }

    public User(String firstName, String lastName, String email, String totpKey, Place p, Organization o) {
        this.externalId = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = true;
        this.deleted = false;
        this.isAdmin = false;
        this.isSuperadmin = false;
        this.confirmed = false;
        // Set organization or tenant
        this.organization = o;
        this.place = p;

        // Add A Secret
        this.TOTPKey = totpKey;
        this.TOTPConfigured = false;
        this.email = email;
    }

    public static Finder<String, User> find = new Finder<>(User.class);

    public static List<User> all() {
        return find.query()
                .orderBy("when_updated desc")
                .setMaxRows(1000)
                .findList();
    }

    public static User findByEmailAndPassword(String email, String password, Organization o) {
//        email = o.encrypt(email.toLowerCase());
        User user = find.query().where()
                .eq("email", email.toLowerCase())
                .eq("organization_id", o.getId()).findOne();
        boolean authenticated;
        String storedPassword = user.getStrongPassword();
        try {
            authenticated = validatePassword(password, storedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Logger.error(" Error while running authentication algorithm - " + e.getMessage());
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Logger.error(" Error while running authentication algorithm - " + e.getMessage());
            return null;
        }
        if (authenticated) {
            return user;
        } else {
            return null;
        }
    }

    public static User findByEmail(String email, Organization o) {
        //  email = o.encrypt(email.toLowerCase());
        if (email == null || o == null) {
            return null;
        }
        return find.query()
                .where()
                .eq("email", email.toLowerCase())
                .eq("organization_id", o.getId())
                .findOne();
    }

    public static User findByEmailAndWorkspace(String email, String workspaceName) {
        Organization organization = Organization.findByWorkspaceName(workspaceName);
        if (organization == null) {
            return null;
        } else {
            return findByEmail(email, organization);
        }
    }

    public static User findByResetToken(String resetToken) {
        return find.query()
                .where()
                .eq("reset_token", resetToken)
                .findOne();
    }

    public static User findByConfirmationHash(String confirmationHash) {
        return find.query()
                .where()
                .eq("confirmation_hash", confirmationHash)
                .findOne();
    }

    public static User all(Map parameterMap) {
        return (User) find.query().where().allEq(parameterMap).findOne();
    }

    public static User findById(String userId) {
        return find.query().where().eq("id", userId).findOne();
    }

    public static User findByConfirmationHashId(String hashId) {
        return find.query().where().eq("confirmation_hash", hashId).findOne();
    }

    public static Boolean matchConfirmationHash(String hashId) {
        if (find.query().where().eq("confirmation_hash", hashId).findOne() == null) {
            Logger.info("Invalid Email Verification Hash - " + hashId);
            return false;
        } else {
            return true;
        }
    }

    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getSha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public String getEncodedHash(String password, String salt, int iterations) {
        // Returns only the last part of whole encoded password
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Could NOT retrieve PBKDF2WithHmacSHA256 algorithm");
            System.exit(1);
        }
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(Charset.forName("UTF-8")), iterations, 256);
        SecretKey secret = null;
        try {
            secret = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            System.out.println("Could NOT generate secret key");
            e.printStackTrace();
        }

        byte[] rawHash = secret.getEncoded();
        byte[] hashBase64 = Base64.getEncoder().encode(rawHash);

        return new String(hashBase64);
    }

    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public String getEmail() {
        return email;
    }

    public String getStrongPassword() {
        return strongPassword;
    }

    public void setStrongPassword(String strongPassword) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.strongPassword = generateStrongPasswordHash(strongPassword);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public TypeImage getImage() {
        return image;
    }

    public void setImage(TypeImage image) {
        this.image = image;
    }

    public String getConfirmationHash() {
        return confirmationHash;
    }

    public void setConfirmationHash(String confirmationHash) {
        this.confirmationHash = confirmationHash;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public License getLicense() {
        return this.organization.getLicense();
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static List<User> findAllByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).findList();
    }

    public String getInitials() {

        if (firstName == null) {
            if (email == null) {
                return "N.A.";
            } else {
                return email.substring(0, 2).toUpperCase();
            }

        } else if (firstName.trim().isEmpty()) {
            if (email == null) {
                return "N.A.";
            } else {
                return email.substring(0, 2).toUpperCase();
            }
        }

        if (lastName == null) {
            if (email == null) {
                return "N.A.";
            } else {
                return email.substring(0, 2).toUpperCase();
            }

        } else if (lastName.trim().isEmpty()) {
            if (email == null) {
                return "N.A.";
            } else {
                return email.substring(0, 2).toUpperCase();
            }
        }

        return firstName.substring(0, 1).toUpperCase() + " " + lastName.substring(0, 1).toUpperCase();

    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTOTPKey() {
        return TOTPKey;
    }

    public void setTOTPKey(String TOTPKey) {
        this.TOTPKey = TOTPKey;
    }

    public Boolean getTOTPConfigured() {
        return TOTPConfigured;
    }

    public void setTOTPConfigured(Boolean TOTPConfigured) {
        this.TOTPConfigured = TOTPConfigured;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getOrganizationName() {
        return this.organization.getName();
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLicenseName() {
        if (this.getOrganization().getLicense() != null) {
            return this.getOrganization().getLicenseName();
        } else {
            return null;
        }
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getOrganizationId() {
        return this.getOrganization().getId();
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getSuperadmin() {
        return isSuperadmin;
    }

    public void setSuperadmin(Boolean superadmin) {
        isSuperadmin = superadmin;
    }

    public Boolean getAnonymous() {
        if (email == null) {
            return true;
        } else {
            return false;
        }
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }
}
