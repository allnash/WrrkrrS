/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 - 2017 Thibault Meyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package utils;

import com.zero_x_baadf00d.ebean.encryption.DefaultEncryptKeyImpl;
import io.ebean.config.EncryptKey;
import io.ebean.config.EncryptKeyManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * BasicEncryptKeyManager.
 *
 * @author Thibault Meyer
 * @version 16.11.24
 * @see EncryptKeyManager
 * @since 16.02.27
 */
public class StandardEncryptKeyManager implements EncryptKeyManager {

    /**
     * Handle to all instantiated {@code EncryptKey}.
     *
     * @see EncryptKey
     * @since 16.02.27
     */
    private Map<String, EncryptKey> encryptKeys;

    @Override
    public void initialise() {
        this.encryptKeys = new HashMap<>();
        final InputStream is;
        String customEbeanEncryptKeysFile = System.getenv("EBEAN_PROPS_FILE");
        if (customEbeanEncryptKeysFile != null) {
            try {
                if (!customEbeanEncryptKeysFile.contains("://")) {
                    if (!customEbeanEncryptKeysFile.startsWith("/")) {
                        customEbeanEncryptKeysFile = "/" + customEbeanEncryptKeysFile;
                    }
                    customEbeanEncryptKeysFile = "file://" + customEbeanEncryptKeysFile;
                }
                final URL u = new URL(customEbeanEncryptKeysFile);
                is = u.openStream();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            is = BasicEncryptKeyManager.class.getResourceAsStream("/ebean.properties");
        }

        final Properties properties = new Properties();
        try {
            properties.load(is);
            for (final String property : properties.stringPropertyNames()) {
                try {
                    final String key = property.split("\\.", 3)[2];
                    this.encryptKeys.put(key, new DefaultEncryptKeyImpl(properties.get(property).toString()));
                } catch (IndexOutOfBoundsException ignore) {
                }
            }
            if (this.encryptKeys.isEmpty()) {
                throw new RuntimeException("Misconfigured Ebean encryption key manager");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Can't open ebean.properties", ex);
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public EncryptKey getEncryptKey(final String tableName, final String columnName) {
        final String key = tableName + "." + columnName;
        return this.encryptKeys.getOrDefault(key, this.encryptKeys.getOrDefault(tableName, this.encryptKeys.get("key")));
    }
}
