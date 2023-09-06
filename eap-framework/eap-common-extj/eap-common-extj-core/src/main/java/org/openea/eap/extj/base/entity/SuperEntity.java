package org.openea.eap.extj.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuperEntity<T> extends SuperBaseEntity.SuperCUDBaseEntity<T> {




}
