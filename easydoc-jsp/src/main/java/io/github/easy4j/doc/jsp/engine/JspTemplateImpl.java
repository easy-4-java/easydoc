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
package io.github.easy4j.doc.jsp.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
public class JspTemplateImpl implements JspTemplate {
	
    protected final JspEngine engine;
    protected final JspConfig config;
    protected final String name;
    protected HttpServletRequest request = null;
    protected HttpServletResponse response = null;
    
    public JspTemplateImpl(JspEngine engine,HttpServletRequest request,HttpServletResponse response,String name) {
        this.engine = engine;
        this.config = engine.getConfig();
        this.name = name;
        this.request = request;
        this.response = response;
    }

    @Override
    public void render(String requestURL,Map<String, Object> variables, Writer out) throws IOException, ServletException {
    	ByteArrayInputStream input = null;
    	ByteArrayOutputStream output = null;
    	try {
			output = new ByteArrayOutputStream();
			doInterpret(requestURL, variables, output);
			input = new ByteArrayInputStream(output.toByteArray());
			IOUtils.copy(input, out, config.getInputEncoding());
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}
    }

    @Override
    public void render(String requestURL,Map<String, Object> variables, OutputStream out)  throws IOException, ServletException {
    	doInterpret(requestURL, variables, out);
    }

    private void doInterpret(String requestURL,Map<String, Object> variables, OutputStream output) throws IOException, ServletException {
    	/**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        ServletContext sc = request.getSession().getServletContext();
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        RequestDispatcher rd = sc.getRequestDispatcher(requestURL);
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
         
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        final ServletOutputStream outputStream = new ServletOutputStream(){
            
            public void write(int b) throws IOException {
                /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
                baos.write(b);
            }

 			@SuppressWarnings("unused")
			public boolean isReady() {
 				return false;
 			}

        }; 
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, config.getOutputEncoding() ),true);
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        HttpServletResponse resp = new HttpServletResponseWrapper(response){
            /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
            public ServletOutputStream getOutputStream(){
                return outputStream;
            }
             
            /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
            public PrintWriter getWriter(){
                return pw;
            }
        }; 
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        rd.include(request, resp);
        pw.flush();
        /**
 * Implementation of jsp template impl functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
        baos.writeTo(output);
    }
    
	@Override
	public String getName() {
		return name;
	}

}

