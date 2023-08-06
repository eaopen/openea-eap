package org.openea.eap.extj.base.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface SuperMapper<T> extends BaseMapper<T> {
    int insertIgnoreLogic(T var1);

    int deleteByIdIgnoreLogic(Serializable var1);

    int deleteByIdIgnoreLogic(T var1);

    int deleteByMapIgnoreLogic(@Param("cm") Map<String, Object> var1);

    int deleteIgnoreLogic(@Param("ew") Wrapper<T> var1);

    int deleteBatchIdsIgnoreLogic(@Param("coll") Collection<?> var1);

    int updateByIdIgnoreLogic(@Param("et") T var1);

    int updateIgnoreLogic(@Param("et") T var1, @Param("ew") Wrapper<T> var2);

    T selectByIdIgnoreLogic(Serializable var1);

    List<T> selectBatchIdsIgnoreLogic(@Param("coll") Collection<? extends Serializable> var1);

    List<T> selectByMapIgnoreLogic(@Param("cm") Map<String, Object> var1);

    default T selectOneIgnoreLogic(@Param("ew") Wrapper<T> queryWrapper) {
        List<T> ts = this.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(ts)) {
            if (ts.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records", new Object[0]);
            } else {
                return ts.get(0);
            }
        } else {
            return null;
        }
    }

    default boolean existsIgnoreLogic(Wrapper<T> queryWrapper) {
        Long count = this.selectCount(queryWrapper);
        return null != count && count > 0L;
    }

    Long selectCountIgnoreLogic(@Param("ew") Wrapper<T> var1);

    List<T> selectListIgnoreLogic(@Param("ew") Wrapper<T> var1);

    List<Map<String, Object>> selectMapsIgnoreLogic(@Param("ew") Wrapper<T> var1);

    List<Object> selectObjsIgnoreLogic(@Param("ew") Wrapper<T> var1);

    <P extends IPage<T>> P selectPageIgnoreLogic(P var1, @Param("ew") Wrapper<T> var2);

    <P extends IPage<Map<String, Object>>> P selectMapsPageIgnoreLogic(P var1, @Param("ew") Wrapper<T> var2);
}
