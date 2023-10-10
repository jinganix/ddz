package demo.ddz.helper.repository;

public interface RepositoryMeta<T, ID> {

  String module();

  ID indexId(T entity);
}
