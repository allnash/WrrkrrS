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

package utils;

import com.neovisionaries.oui.Oui;
import com.neovisionaries.oui.OuiCsvParser;
import play.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class OUI {

    public static Oui ouiParser;
    public static Set<String> androidCompanies = new HashSet<>();
    public static Set<String> networkingCompanies = new HashSet<>();
    public static Set<String> laptopCompanies = new HashSet<>();
    public static Set<String> smarthomeCompanies = new HashSet<>();
    public static Set<String> printerCompanies = new HashSet<>();

    public static String getCompanyNameForOUI(String hexadecimalOUI) {
        if (ouiParser == null) {
            try {
                ouiParser = new Oui(new OuiCsvParser().parse(new URL("file:data/oui.csv")));
            } catch (IOException e) {
                Logger.error("Error loading OUI Parser.");
                return "error_in_oui_parser";
            }
        }
        String companyName = ouiParser.getName(hexadecimalOUI);
        if (companyName == null) {
            if (isLocallyAdministedEfficient(hexadecimalOUI)) {
                return "locally_administered_address";
            } else {
                return "not_in_oui";
            }
        } else
            return companyName;
    }

    public enum OUIDeviceType {
        ANDROID,
        APPLE,
        LAPTOP,
        NETWORKING_DEVICE,
        UNCLASSIFIED,
        NOT_IN_OUI,
        MASKED,
        SMART_HOME,
        PRINTERS,
        RASPBERRY_PI_OR_IOT
    }

    public static OUIDeviceType getOUIDeviceTypeForOUI(String hexadecimalOUI) {

        if (androidCompanies.size() == 0) {
            androidCompanies.add("Motorola Mobility LLC, a Lenovo Company");
            androidCompanies.add("Motorola (Wuhan) Mobility Technologies Communication Co., Ltd.");
            androidCompanies.add("GUANGDONG OPPO MOBILE TELECOMMUNICATIONS CORP.,LTD");
            androidCompanies.add("Huawei Symantec Technologies Co.,Ltd.");
            androidCompanies.add("HTC Corporation");
            androidCompanies.add("Samsung Electronics Co.,Ltd");
            androidCompanies.add("Samsung Electro Mechanics Co., Ltd.");
            androidCompanies.add("SAMSUNG Electronics. Co. LTD");
            androidCompanies.add("SAMSUNG ELECTRO-MECHANICS(THAILAND)");
            androidCompanies.add("BlackBerry RTS");
            androidCompanies.add("LG ELECTRONICS INC");
            androidCompanies.add("Murata Manufacturing Co., Ltd.");
            androidCompanies.add("Nokia Corporation");
            androidCompanies.add("BLU Products Inc.");
            androidCompanies.add("vivo Mobile Communication Co., Ltd.");
            androidCompanies.add("Alcatel-Lucent Shanghai Bell Co., Ltd");
            androidCompanies.add("LG Electronics");
            androidCompanies.add("LG Electronics (Mobile Communications)");
            androidCompanies.add("OnePlus Technology (Shenzhen) Co., Ltd");
            androidCompanies.add("OnePlus Tech (Shenzhen) Ltd");
            androidCompanies.add("Google, Inc.");
            androidCompanies.add("zte corporation");
            androidCompanies.add("Sony Corporation");
            androidCompanies.add("Sony Mobile Communications AB");
            androidCompanies.add("Gionee Communication Equipment Co.,Ltd.");
            androidCompanies.add("Lenovo Mobile Communication Technology Ltd.");
            androidCompanies.add("Xiaomi Communications Co Ltd");
            androidCompanies.add("HUAWEI TECHNOLOGIES CO.,LTD");
            androidCompanies.add("BLU Products Inc");
        }

        if (networkingCompanies.size() == 0) {
            networkingCompanies.add("Juniper Networks");
            networkingCompanies.add("Aruba Networks");
            networkingCompanies.add("Naray Information & Communication Enterprise");
            networkingCompanies.add("Hon Hai Precision Ind. Co.,Ltd.");
            networkingCompanies.add("HERO SYSTEMS, LTD.");
            networkingCompanies.add("NETGEAR");
            networkingCompanies.add("Belkin International Inc.");
            networkingCompanies.add("Ubiquiti Networks Inc.");
            networkingCompanies.add("ARRIS Group, Inc.");
            networkingCompanies.add("Zyxel Communications Corporation");
            networkingCompanies.add("Colubris Networks");
            networkingCompanies.add("Rivet Networks");
            networkingCompanies.add("Extreme Networks, Inc.");
            networkingCompanies.add("TP-LINK TECHNOLOGIES CO.,LTD.");
            networkingCompanies.add("Novatel Wireless Solutions, Inc.");
            networkingCompanies.add("Cisco Systems, Inc");
            networkingCompanies.add("Cisco Meraki");
            networkingCompanies.add("Cisco-Linksys, LLC");
            networkingCompanies.add("Wistron Neweb Corporation");
            networkingCompanies.add("Meru Networks Inc");
            networkingCompanies.add("Ruckus Wireless");
        }

        if (laptopCompanies.size() == 0) {
            laptopCompanies.add("Intel Corporate");
            laptopCompanies.add("Hewlett Packard");
            laptopCompanies.add("Microsoft");
            laptopCompanies.add("Microsoft Corporation");
            laptopCompanies.add("REALTEK SEMICONDUCTOR CORP.");
            laptopCompanies.add("ASUSTek COMPUTER INC.");
            laptopCompanies.add("GIGA-BYTE TECHNOLOGY CO.,LTD.");
            laptopCompanies.add("Hewlett Packard Enterprise");
            laptopCompanies.add("Broadcom");
            laptopCompanies.add("Acer Inc.");
            laptopCompanies.add("Microsoft");
        }

        if (smarthomeCompanies.size() == 0) {
            smarthomeCompanies.add("Sonos, Inc.");
            smarthomeCompanies.add("Amazon Technologies Inc.");
            smarthomeCompanies.add("Shenzhen Reecam Tech.Ltd.");
            smarthomeCompanies.add("Nintendo Co.,Ltd");
            smarthomeCompanies.add("Arcadyan Technology Corporation");
            smarthomeCompanies.add("Roku, Inc.");
            smarthomeCompanies.add("Nest Labs Inc.");
        }

        if (printerCompanies.size() == 0) {
            printerCompanies.add("Zebra Technologies Inc");
            printerCompanies.add("XEROX CORPORATION");
        }

        String companyName = getCompanyNameForOUI(hexadecimalOUI).trim();
        if (androidCompanies.contains(companyName)) {
            return OUIDeviceType.ANDROID;
        } else if (companyName.equals("Apple, Inc.")) {
            return OUIDeviceType.APPLE;
        } else if (companyName.equals("Raspberry Pi Foundation")) {
            return OUIDeviceType.RASPBERRY_PI_OR_IOT;
        } else if (networkingCompanies.contains(companyName)) {
            return OUIDeviceType.NETWORKING_DEVICE;
        } else if (laptopCompanies.contains(companyName)) {
            return OUIDeviceType.LAPTOP;
        } else if (smarthomeCompanies.contains(companyName)) {
            return OUIDeviceType.SMART_HOME;
        } else if (printerCompanies.contains(companyName)) {
            return OUIDeviceType.PRINTERS;
        } else if (companyName.equals("locally_administered_address")) {
            return OUIDeviceType.MASKED;
        } else if (companyName.equals("not_in_oui")) {
            return OUIDeviceType.NOT_IN_OUI;
        } else {
            return OUIDeviceType.UNCLASSIFIED;
        }
    }

    // Taken from https://github.com/cowtowncoder/java-uuid-generator/blob/163a0a09e404072ba05e10dcfbd73b3a4f3b8fa7/src/main/java/com/fasterxml/uuid/EthernetAddress.java
    public static boolean isLocallyAdministed(String addrStr) {
        long _address;
        int len = addrStr.length();
        long addr = 0L;
        /* Ugh. Although the most logical format would be the 17-char one
         * (12 hex digits separated by colons), apparently leading zeroes
         * can be omitted. Thus... Can't just check string length. :-/
         */
        for (int i = 0, j = 0; j < 6; ++j) {
            if (i >= len) {
                // Is valid if this would have been the last byte:
                if (j == 5) {
                    addr <<= 8;
                    break;
                }
                throw new NumberFormatException("Incomplete ethernet address (missing one or more digits");
            }

            char c = addrStr.charAt(i);
            ++i;
            int value;

            // The whole number may be omitted (if it was zero):
            if (c == ':') {
                value = 0;
            } else {
                // No, got at least one digit?
                if (c >= '0' && c <= '9') {
                    value = (c - '0');
                } else if (c >= 'a' && c <= 'f') {
                    value = (c - 'a' + 10);
                } else if (c >= 'A' && c <= 'F') {
                    value = (c - 'A' + 10);
                } else {
                    throw new NumberFormatException("Non-hex character '" + c + "'");
                }

                // How about another one?
                if (i < len) {
                    c = addrStr.charAt(i);
                    ++i;
                    if (c != ':') {
                        value = (value << 4);
                        if (c >= '0' && c <= '9') {
                            value |= (c - '0');
                        } else if (c >= 'a' && c <= 'f') {
                            value |= (c - 'a' + 10);
                        } else if (c >= 'A' && c <= 'F') {
                            value |= (c - 'A' + 10);
                        } else {
                            throw new NumberFormatException("Non-hex character '" + c + "'");
                        }
                    }
                }
            }

            addr = (addr << 8) | value;

            if (c != ':') {
                if (i < len) {
                    if (addrStr.charAt(i) != ':') {
                        throw new NumberFormatException("Expected ':', got ('" + addrStr.charAt(i) + "')");
                    }
                    ++i;
                } else if (j < 5) {
                    throw new NumberFormatException("Incomplete ethernet address (missing one or more digits");
                }
            }
        }
        _address = addr;
        return (((int) (_address >> 40)) & 0x02) != 0;
    }

    // Taken from https://github.com/cowtowncoder/java-uuid-generator/blob/163a0a09e404072ba05e10dcfbd73b3a4f3b8fa7/src/main/java/com/fasterxml/uuid/EthernetAddress.java
    public static boolean isLocallyAdministedEfficient(String addrStr) {
        String firstByte = addrStr.toLowerCase().split(":")[0];
        String secondHalf = firstByte.split("(?!^)")[1];
        if (secondHalf.contains("2") || secondHalf.contains("6") ||
                secondHalf.contains("a") || secondHalf.contains("e")) {
            return true;
        } else {
            return false;
        }
    }
}
