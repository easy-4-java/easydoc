/**
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.jsp.engine.runtime.writer;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Implementation of jsp writer printer ex functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class JspWriterPrinterEx extends JspWriter {
	
    private final Writer os;
    private final Charset charset;

    public JspWriterPrinterEx(Writer os, Charset charset) {
        this.os = os;
        this.charset = charset;
    }

    @Override
    public Object getOriginStream() {
        return os;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public boolean isSkipErrors() {
        return false;
    }

    @Override
    public void print(int x) throws IOException {
        os.write(x);
    }

    @Override
    public void print(byte x[]) throws IOException {
        if (x != null) {
            os.write(new String(x, charset));
        }
    }

    @Override
    public void print(byte x[], int offset, int length) throws IOException {
        if (x != null) {
            os.write(new String(x, offset, length, charset));
        }
    }

    @Override
    public void print(char x[]) throws IOException {
        if (x != null) {
            os.write(x);
        }
    }

    @Override
    public void print(char x[], int offset, int length) throws IOException {
        if (x != null) {
            os.write(x, offset, length);
        }
    }

    @Override
    public void print(String x) throws IOException {
        if (x != null) {
            os.write(x);
        }
    }

    @Override
    public void flush() throws IOException {
        os.flush();
    }

    @Override
    public void close() throws IOException {
        os.close();
    }
}
