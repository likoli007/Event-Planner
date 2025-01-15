package cz.muni.fi.pv168.project.storage.sql.dao;

import java.util.List;

public interface JoinTableDao<ParentId, ChildId> {
    void deleteAll();

    void deleteByParentId(ParentId parentId);

    void updateAssociations(ParentId parentId, List<ChildId> newChildIds);

    List<ChildId> findByParentId(ParentId parentId);

    void create(ParentId parentId, ChildId childId);
}
