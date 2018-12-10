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

/**
 * BasicEncryptKeyManager.
 *
 * @author Thibault Meyer
 * @version 16.11.24
 * @see EncryptKeyManager
 * @since 16.02.20
 */
public class BasicEncryptKeyManager implements EncryptKeyManager {

    /**
     * Handle to the {@code EncryptKey} to use.
     *
     * @see EncryptKey
     * @since 16.02.20
     */
    private EncryptKey encryptKey;

    @Override
    public void initialise() {
        this.encryptKey = new DefaultEncryptKeyImpl("123456");
    }

    @Override
    public EncryptKey getEncryptKey(final String tableName, final String columnName) {
        return this.encryptKey;
    }
}
