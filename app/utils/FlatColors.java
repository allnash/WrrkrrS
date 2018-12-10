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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nashgadre on 5/3/17.
 */
public class FlatColors {

    public static final Map<String, String> REDS;

    static {
        Map<String, String> aRedsMap = new HashMap<>();
        aRedsMap.put("SOFT RED", "EC644B");
        aRedsMap.put("CHESTNUT ROSE", "D24D57");
        aRedsMap.put("POMEGRANATE", "F22613");
        aRedsMap.put("THUNDERBIRD", "D91E18");
        aRedsMap.put("OLD BRICK", "96281B");
        aRedsMap.put("FLAMINGO", "EF4836");
        aRedsMap.put("VALENCIA", "D64541");
        aRedsMap.put("TALL POPPY", "C0392B");
        aRedsMap.put("MONZA", "CF000F");
        aRedsMap.put("CINNABAR", "E74C3C");
        REDS = Collections.unmodifiableMap(aRedsMap);
    }

    public static final Map<String, String> PINKS;

    static {
        Map<String, String> aPinksMap = new HashMap<>();
        aPinksMap.put("RAZZMATAZZ", "DB0A5B");
        aPinksMap.put("SUNSET ORANGE", "F64747");
        aPinksMap.put("WAX FLOWER", "F1A9A0");
        aPinksMap.put("CABARET", "D2527F");
        aPinksMap.put("NEW YORK PINK", "E08283");
        aPinksMap.put("RADICAL RED", "F62459");
        aPinksMap.put("SUNGLO", "E26A6A");
        PINKS = Collections.unmodifiableMap(aPinksMap);
    }

    public static final Map<String, String> PURPLES;

    static {
        Map<String, String> aPurplesMap = new HashMap<>();
        aPurplesMap.put("SNUFF", "DCC6E0");
        aPurplesMap.put("REBECCAPURPLE", "663399");
        aPurplesMap.put("HONEY FLOWER", "674172");
        aPurplesMap.put("WISTFUL", "AEA8D3");
        aPurplesMap.put("PLUM", "913D88");
        aPurplesMap.put("SEANCE", "A12B3");
        aPurplesMap.put("MEDIUM PURPLE", "BF55EC");
        aPurplesMap.put("LIGHT WISTERIA", "BE90D4");
        aPurplesMap.put("STUDIO", "8E44AD");
        aPurplesMap.put("WISTERIA", "9B59B6");
        PURPLES = Collections.unmodifiableMap(aPurplesMap);
    }
}