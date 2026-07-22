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
package io.github.easy4j.doc.bus.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

/**
 * Slf4j记录事件错误信息
 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
 */
public class Slf4jLogger implements IPublicationErrorHandler {

	 private final boolean printStackTrace;
	 private Logger logger = LoggerFactory.getLogger(getClass()); 
	 
     public Slf4jLogger() {
         this(false);
     }

     public Slf4jLogger(boolean printStackTrace) {
         this.printStackTrace = printStackTrace;
     }

     /**
      * {@inheritDoc}
      */
     @Override
     public void handleError(final PublicationError error) {

         // Logger the error itself
    	 logger.error(error.toString());
         
         // Logger the stacktrace from the cause.
         if (printStackTrace && error.getCause() != null) {
        	 logger.error(error.getCause().getMessage());
         }
     }
	
}
