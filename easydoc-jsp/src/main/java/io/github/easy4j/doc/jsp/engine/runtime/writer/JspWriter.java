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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import io.github.easy4j.doc.jsp.engine.runtime.OriginalStream;


/**
 * Implementation of jsp writer functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public abstract class JspWriter implements OriginalStream, Closeable, Flushable {

    public static JspWriter create(Writer os, Charset charset, boolean trimLeadingWhitespaces, boolean skipErrors) {
        if (skipErrors || os instanceof PrintWriter) {
            if (trimLeadingWhitespaces) {
                os = new TrimLeadingWhitespacesWriter(os);
            }
            return new JspWriterPrinter(os, charset);
        } else {
            if (trimLeadingWhitespaces) {
                os = new TrimLeadingWhitespacesWriter(os);
            }
            return new JspWriterPrinterEx(os, charset);
        }
    }

    public static JspWriter create(OutputStream os, Charset charset, boolean trimLeadingWhitespaces, boolean skipErrors) {
        if (skipErrors || os instanceof PrintStream) {
            if (trimLeadingWhitespaces) {
                os = new TrimLeadingWhitespacesOutputStream(os);
            }
            return new JspOutputStreamPrinter(os, charset);
        } else {
            if (trimLeadingWhitespaces) {
                os = new TrimLeadingWhitespacesOutputStream(os);
            }
            return new JspOutputStreamPrinterEx(os, charset);
        }
    }

    public static JspWriter create(JspWriter os, boolean trimLeadingWhitespaces) {
        if (!trimLeadingWhitespaces) {
            return os;
        }

        Object out = os.getOriginStream();
        if (out instanceof OriginalStream) {
            out = ((OriginalStream) out).getOriginStream();
        }

        if (out instanceof OutputStream) {
            return create((OutputStream) out, os.getCharset(), trimLeadingWhitespaces, os.isSkipErrors());
        } else {
            return create((Writer) out, os.getCharset(), trimLeadingWhitespaces, os.isSkipErrors());
        }
    }

    /**
 * Implementation of jsp writer functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
    @Override
    public abstract Object getOriginStream();

    /**
 * Implementation of jsp writer functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
    public abstract boolean isStreaming();

    public abstract Charset getCharset();

    public abstract boolean isSkipErrors();

    public abstract void print(int x) throws IOException;

    public abstract void print(byte x[]) throws IOException;

    public abstract void print(byte x[], int offset, int length) throws IOException;

    public abstract void print(char x[]) throws IOException;

    public abstract void print(char x[], int offset, int length) throws IOException;

    public abstract void print(String x) throws IOException;

    @Override
    public abstract void flush() throws IOException;

    @Override
    public abstract void close() throws IOException;

}