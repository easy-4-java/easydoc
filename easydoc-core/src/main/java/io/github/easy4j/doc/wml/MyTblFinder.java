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
package io.github.easy4j.doc.wml;

import java.util.ArrayList;
import java.util.List;

import org.docx4j.TraversalUtil.CallbackImpl;
import org.docx4j.wml.Tbl;  
  
/**
 * Class for MyTblFinder.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class MyTblFinder extends CallbackImpl { 
	
    public List<Tbl> tblList = new ArrayList<Tbl>();  
  
    public List<Object> apply(Object o) {  
        if (o instanceof Tbl) {  
            tblList.add((Tbl) o);  
        }  
        return null;  
    }  
  
    public boolean shouldTraverse(Object o) {  
        return !(o instanceof Tbl);  
    }  
  
    public List<Tbl> getTbls() {  
        return tblList;  
    }  
}  