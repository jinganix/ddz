package demo.ddz.helper.repository.single;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractSingleValueRepository<T, ID> implements SingleValueRepository<T, ID> {

  protected final SingleValueRepositoryMeta<T, ID> meta;

  protected final SingleValueRepository<T, ID> delegate;

  @Override
  public String module() {
    return meta.module();
  }

  @Override
  public ID indexId(T entity) {
    return meta.indexId(entity);
  }

  @Override
  public Integer delete(T entity) {
    return this.deleteById(indexId(entity));
  }
}
