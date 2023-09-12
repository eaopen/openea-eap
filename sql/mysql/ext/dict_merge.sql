-- 字典类型
insert into system_dict_type(id, parent_id, type, name, is_tree)
select
    (200000000 + hex(F_id)%1000000) as id
     , (case F_ParentId when '-1' then 0 else 200000000 + hex(F_ParentId)%1000000 end) as parent_id
     , F_EnCode as type, F_FullName as name, F_IsTree as is_tree
from base_dictionarytype t2
where not EXISTS (select t1.type from system_dict_type t1 where t2.F_EnCode = t1.type);

-- 旧id
update system_dict_type t, base_dictionarytype t2
set t.ref_id = t2.F_Id
where t.ref_id is null and t.type=t2.F_EnCode;

-- 数据准备
-- 更新type id
update base_dictionarydata t
set t.type_id = (200000000 + hex(t.F_DictionaryTypeId)%1000000)
where t.type_id is null;

update base_dictionarydata t
set t.id = (20000000000 + hex(concat(left(t.F_Id,3),right(t.F_id,4)))%10000000000)
where t.id is null;

update base_dictionarydata t
set t.parent_id =  case F_ParentId when '0' then 0 else 20000000000 + hex(concat(left(t.F_ParentId,3),right(t.F_ParentId,4)))%10000000000 end
where t.parent_id is null;

-- 字典数据
insert into system_dict_data(id, parent_id, dict_type, value, label, remark)
select  t4.id
     ,   t4.parent_id
     , t1.type, F_EnCode as value, F_FullName as label, F_Description as remark
-- ,t4.*
from base_dictionarydata t4, system_dict_type t1
where t4.type_id=t1.id and not EXISTS (select id from system_dict_data where dict_type=t1.type);