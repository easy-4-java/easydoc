/*
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
package io.github.easy4j.doc.xhtml.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.github.easy4j.doc.xhtml.DataMap;
import org.jsoup.nodes.Document;

/**
 * 
 * TODO
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public interface DocumentHandler {
	
	Document handle(File htmlFile) throws IOException;
	
	Document handle(String html, boolean fragment) throws IOException;
	
	Document handle(URL url) throws IOException;
	
	Document handle(String url, DataMap dataMap) throws IOException;
	
	Document handle(InputStream input) throws IOException;
	
}
