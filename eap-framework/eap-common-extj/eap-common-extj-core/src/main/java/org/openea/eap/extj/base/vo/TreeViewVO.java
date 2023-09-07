package org.openea.eap.extj.base.vo;

import lombok.Data;

import java.util.List;


@Data
public class TreeViewVO<T> {
    private List<T> treeList;
}
