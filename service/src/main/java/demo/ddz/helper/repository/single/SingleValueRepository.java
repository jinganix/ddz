package demo.ddz.helper.repository.single;

import java.util.List;

public interface SingleValueRepository<T, ID> extends SingleValueRepositoryMeta<T, ID> {

  T save(T entity);

  Integer delete(T entity);

  Integer deleteById(ID id);

  T find(ID id);

  List<T> findAll(List<ID> ids);
}
