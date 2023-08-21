package org.openea.eap.extj.util.treeutil.newtreeutil;

import org.apache.commons.collections4.CollectionUtils;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.treeutil.SumTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class TreeDotUtils {
    public TreeDotUtils() {
    }

    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDot(List<T> tList, String parentId) {
        List<SumTree<T>> sumTrees = new ArrayList();
        List<T> list = new ArrayList();
        CollectionUtils.addAll(list, tList);
        if (StringUtil.isNotEmpty(parentId)) {
            List<T> data = (List)list.stream().filter((tx) -> {
                return parentId.equals(tx.getParentId());
            }).collect(Collectors.toList());
            list.removeAll(data);

            for(int i = 0; i < data.size(); ++i) {
                T t = data.get(i);
                if (!isTreeDotExist(list, t.getParentId())) {
                    SumTree<T> tSumTree = getTreeDotByT(t, list);
                    sumTrees.add(tSumTree);
                }
            }
        }

        return sumTrees;
    }

    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDot(List<T> tList) {
        List<SumTree<T>> sumTrees = new ArrayList();
        if (tList != null && tList.size() > 0) {
            for(int i = 0; i < tList.size(); ++i) {
                T t = tList.get(i);
                if (!isTreeDotExist(tList, t.getParentId())) {
                    SumTree<T> tSumTree = getTreeDotByT(t, tList);
                    sumTrees.add(tSumTree);
                }
            }
        }

        return sumTrees;
    }

    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDotFilter(List<T> tList) {
        List<SumTree<T>> sumTrees = new ArrayList();
        if (tList != null && tList.size() > 0) {
            for(int i = 0; i < tList.size(); ++i) {
                T t = tList.get(i);
                if (!isTreeDotExist(tList, t.getParentId())) {
                    SumTree<T> tSumTree = getTreeDotByT(t, tList);
                    if ("-1".equals(tSumTree.getParentId()) || "0".equals(tSumTree.getParentId())) {
                        sumTrees.add(tSumTree);
                    }
                }
            }
        }

        return sumTrees;
    }

    private static <T extends SumTree> Boolean isTreeDotExist(List<T> tList, String id) {
        Iterator var2 = tList.iterator();

        SumTree t;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            t = (SumTree)var2.next();
        } while(!t.getId().equals(id));

        return true;
    }

    private static <T extends SumTree> List<SumTree<T>> getChildTreeDotList(SumTree<T> parentTreeDot, List<T> tList) {
        List<SumTree<T>> childTreeDotList = new ArrayList();
        List<T> data = (List)tList.stream().filter((tx) -> {
            return parentTreeDot.getId().equals(tx.getParentId());
        }).collect(Collectors.toList());
        Iterator<T> var4 = data.iterator();

        while(var4.hasNext()) {
            T t = var4.next();
            if (parentTreeDot.getId().equals(t.getParentId())) {
                SumTree<T> tSumTree = getTreeDotByT(t, tList);
                childTreeDotList.add(tSumTree);
            }
        }

        return childTreeDotList;
    }

    private static <T extends SumTree> SumTree<T> getTreeDotByT(T t, List<T> tList) {
        List<SumTree<T>> children = getChildTreeDotList(t, tList);
        t.setHasChildren(children.size() != 0);
        if (children.size() == 0) {
            children = null;
        }

        t.setChildren(children);
        return t;
    }
}
