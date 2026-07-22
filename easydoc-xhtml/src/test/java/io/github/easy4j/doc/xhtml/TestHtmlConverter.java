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
package io.github.easy4j.doc.xhtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * Created by noah on 3/12/15.
 */
public class TestHtmlConverter {
	
	@BeforeClass
	public static void before() {
	}
	
	@AfterClass
	public static void after() {
	}
	
	@Test
	public void test() throws Exception {
		
		String html = "<html><head><title>First parse</title></head>"
				  + "<body><p>Parsed HTML into a doc.</p></body></html>";
				Document doc = Jsoup.parse(html,"http://example.com/");
				System.out.println(doc.baseUri());
		/*HtmlConverter converter = new HtmlConverter();
		String url = null;//"http://127.0.0.1:" + htmlServer.getPort() + "/report.html"; //输入要转换的网址
		File fileDocx = converter.saveUrlToDocx(url);
		File filePdf = converter.saveUrlToPdf(url);
		Desktop.getDesktop().open(fileDocx); //由操作系统打开
		Desktop.getDesktop().open(filePdf);*/
	}
}