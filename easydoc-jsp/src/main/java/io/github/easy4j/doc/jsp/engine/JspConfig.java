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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import io.github.easy4j.doc.utils.StringUtils;

/**
 * TODO
 * @author <a href="https://github.com/easy-4-java">hiwepy</a>
 */
public class JspConfig {

	public static final String DEFAULT_CONFIG_FILE = "classpath:/jsp-template.properties";

    public static final String IMPORT_CLASSES = "jsp.import.classes";
    public static final String IMPORT_METHODS = "jsp.import.methods";
    public static final String IMPORT_FUNCTIONS = "jsp.import.functions";
    public static final String IMPORT_TAGS = "jsp.import.tags";
    public static final String IMPORT_MACROS = "jsp.import.macros";
    public static final String IMPORT_DEFINES = "jsp.import.defines";
    public static final String TEMPLATE_SUFFIX = "jsp.template.suffix";
    public static final String INPUT_ENCODING = "jsp.input.encoding";
    public static final String OUTPUT_ENCODING = "jsp.output.encoding";
    public static final String TRIM_LEADING_WHITESPACES = "jsp.trim.leading.whitespaces";
    public static final String TRIM_DIRECTIVE_WHITESPACES = "jsp.trim.directive.whitespaces";
    public static final String TRIM_DIRECTIVE_COMMENTS = "jsp.trim.directive.comments";
    public static final String TRIM_DIRECTIVE_COMMENTS_PREFIX = "jsp.trim.directive.comments.prefix";
    public static final String TRIM_DIRECTIVE_COMMENTS_SUFFIX = "jsp.trim.directive.comments.suffix";
    public static final String IO_SKIPERRORS = "jsp.io.skiperrors";

    private List<String> importClasses;
    private List<String> importMethods;
    private List<String> importFunctions;
    private List<String> importTags;
    private List<String> importMacros;
    private List<String> importDefines;
    private List<String> autoscanPackages;
    private boolean autoscanSkiperrors;
    private String templateSuffix;
    private Charset inputEncoding;
    private Charset outputEncoding;
    private boolean syntaxStrict;
    private boolean syntaxSafecall;
    private boolean trimLeadingWhitespaces;
    private boolean trimDirectiveWhitespaces;
    private boolean trimDirectiveComments;
    private String trimDirectiveCommentsPrefix;
    private String trimDirectiveCommentsSuffix;
    private boolean ioSkiperrors;
    
    protected JspConfig(Properties config) {
        importClasses = Arrays.asList(StringUtils.tokenizeToStringArray(config.getProperty(IMPORT_CLASSES,"")));
        importMethods = Arrays.asList(StringUtils.tokenizeToStringArray(config.getProperty(IMPORT_METHODS,"")));
        importFunctions = Arrays.asList(StringUtils.tokenizeToStringArray(config.getProperty(IMPORT_FUNCTIONS,"")));
        importTags = Arrays.asList(StringUtils.tokenizeToStringArray(config.getProperty(IMPORT_TAGS,"")));
        importMacros = Arrays.asList(StringUtils.tokenizeToStringArray(config.getProperty(IMPORT_MACROS,"")));
        importDefines = Arrays.asList(StringUtils.tokenizeToStringArray(config.getProperty(IMPORT_DEFINES,"")));
        templateSuffix = config.getProperty(TEMPLATE_SUFFIX, ".jsp");
        inputEncoding = Charset.forName(config.getProperty(INPUT_ENCODING, "UTF-8"));
        outputEncoding = Charset.forName(config.getProperty(OUTPUT_ENCODING, "UTF-8"));
        trimLeadingWhitespaces = Boolean.parseBoolean(config.getProperty(TRIM_LEADING_WHITESPACES, "false"));
        trimDirectiveWhitespaces = Boolean.parseBoolean(config.getProperty(TRIM_DIRECTIVE_WHITESPACES, "true"));
        trimDirectiveComments = Boolean.parseBoolean(config.getProperty(TRIM_DIRECTIVE_COMMENTS, "false"));
        trimDirectiveCommentsPrefix = config.getProperty(TRIM_DIRECTIVE_COMMENTS_PREFIX, "<!--");
        trimDirectiveCommentsSuffix = config.getProperty(TRIM_DIRECTIVE_COMMENTS_SUFFIX, "-->");
        ioSkiperrors = Boolean.parseBoolean(config.getProperty(IO_SKIPERRORS, "false"));
    }

    public List<String> getImportClasses() {
        return importClasses;
    }

    public List<String> getImportMethods() {
        return importMethods;
    }

    public List<String> getImportFunctions() {
        return importFunctions;
    }

    public List<String> getImportTags() {
        return importTags;
    }

    public List<String> getImportMacros() {
        return importMacros;
    }

    public List<String> getImportDefines() {
        return importDefines;
    }

    public List<String> getAutoscanPackages() {
        return autoscanPackages;
    }

    public boolean isAutoscanSkiperrors() {
        return autoscanSkiperrors;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public Charset getInputEncoding() {
        return inputEncoding;
    }

    public Charset getOutputEncoding() {
        return outputEncoding;
    }

    public boolean isSyntaxStrict() {
        return syntaxStrict;
    }

    public boolean isSyntaxSafecall() {
        return syntaxSafecall;
    }

    public boolean isTrimLeadingWhitespaces() {
        return trimLeadingWhitespaces;
    }

    public boolean isTrimDirectiveWhitespaces() {
        return trimDirectiveWhitespaces;
    }

    public boolean isTrimDirectiveComments() {
        return trimDirectiveComments;
    }

    public String getTrimDirectiveCommentsPrefix() {
        return trimDirectiveCommentsPrefix;
    }

    public String getTrimDirectiveCommentsSuffix() {
        return trimDirectiveCommentsSuffix;
    }

    public boolean isIoSkiperrors() {
        return ioSkiperrors;
    }
    
}
