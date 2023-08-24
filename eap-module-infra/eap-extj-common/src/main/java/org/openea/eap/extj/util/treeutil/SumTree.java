package org.openea.eap.extj.util.treeutil;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(Include.NON_NULL)
public class SumTree<T> {

    protected String id;
    protected String parentId;
    protected Boolean hasChildren;
    protected List<SumTree<T>> children;

}