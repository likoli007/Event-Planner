package cz.muni.fi.pv168.project.business.filter;

public interface Filter<T> {
    public Boolean isMatch(T entity);
}
