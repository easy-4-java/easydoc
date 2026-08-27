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
  
public class MyTblFinder extends CallbackImpl {

    public List<Tbl> tblList = new ArrayList<Tbl>();

    /**
     * 访问节点：命中 {@link Tbl} 时记入 tblList。
     * <p>修复点：对齐其他 {@link CallbackImpl} 子类的约定 —— 返回当前节点的
     * 子节点列表（{@link #getChildren(Object)}），而不是 null。返回值本身不参与
     * docx4j 遍历决策，但 null 不是回调约定的返回值，且会让调用方在自定义遍历/
     * 调试时丢失继续下钻所需的信息；子节点获取失败时（非容器节点）仍按契约返回 null。
     */
    @Override
    public List<Object> apply(Object o) {
        if (o instanceof Tbl) {
            tblList.add((Tbl) o);
        }
        return getChildren(o);
    }
  
    public boolean shouldTraverse(Object o) {  
        return !(o instanceof Tbl);  
    }  
  
    public List<Tbl> getTbls() {  
        return tblList;  
    }  
}  