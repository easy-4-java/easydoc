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
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
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
         * 创建ServletContext对象，用于获取RequestDispatcher对象
         */
        ServletContext sc = request.getSession().getServletContext();
        /**
         * 根据传过来的相对文件路径，生成一个reqeustDispatcher的包装类
         */
        RequestDispatcher rd = sc.getRequestDispatcher(requestURL);
        /**
         * 创建一个ByteArrayOutputStream的字节数组输出流,用来存放输出的信息
         */
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
         
        /**
         * ServletOutputStream是抽象类，必须实现write的方法
         */
        final ServletOutputStream outputStream = new ServletOutputStream(){
            
            public void write(int b) throws IOException {
                /**
                 * 将指定的字节写入此字节输出流
                 */
                baos.write(b);
            }

 			@SuppressWarnings("unused")
			public boolean isReady() {
 				return false;
 			}

        }; 
        /**
         * 通过现有的 OutputStream 创建新的 PrintWriter
         * OutputStreamWriter 是字符流通向字节流的桥梁：可使用指定的 charset 将要写入流中的字符编码成字节
         */
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, config.getOutputEncoding() ),true);
        /**
         * 生成HttpServletResponse的适配器，用来包装response
         */
        HttpServletResponse resp = new HttpServletResponseWrapper(response){
            /**
             * 调用getOutputStream的方法(此方法是ServletResponse中已有的)返回ServletOutputStream的对象
             * 用来在response中返回一个二进制输出对象
             * 此方法目的是把源文件写入byteArrayOutputStream
             */
            public ServletOutputStream getOutputStream(){
                return outputStream;
            }
             
            /**
             * 再调用getWriter的方法(此方法是ServletResponse中已有)返回PrintWriter的对象
             * 此方法用来发送字符文本到客户端
             */
            public PrintWriter getWriter(){
                return pw;
            }
        }; 
        /**
         * 在不跳转下访问目标jsp。 就是利用RequestDispatcher.include(ServletRequest request,
         * ServletResponse response)。 该方法把RequestDispatcher指向的目标页面写到response中。
         */
        rd.include(request, resp);
        pw.flush();
        /**
         * 使用ByteArrayOutputStream的writeTo方法来向文本输出流写入数据，这也是为什么要使用ByteArray的一个原因
         */
        baos.writeTo(output);
    }
    
	@Override
	public String getName() {
		return name;
	}

}

