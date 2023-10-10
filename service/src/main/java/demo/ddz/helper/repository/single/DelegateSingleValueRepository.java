package demo.ddz.helper.repository.single;

import java.util.List;

public abstract class DelegateSingleValueRepository<T, ID> implements SingleValueRepository<T, ID> {

  protected final SingleValueRepositoryMeta<T, ID> meta;

  protected final SingleValueRepository<T, ID> delegate;

  protected DelegateSingleValueRepository(
      SingleValueRepositoryMeta<T, ID> meta, SingleValueRepository<T, ID> delegate) {
    this.meta = meta;
    this.delegate = delegate;
  }

  @Override
  public String module() {
    return delegate.module();
  }

  @Override
  public ID indexId(T entity) {
    return delegate.indexId(entity);
  }

  @Override
  public T save(T entity) {
    return delegate.save(entity);
  }

  @Override
  public Integer delete(T entity) {
    return delegate.delete(entity);
  }

  @Override
  public Integer deleteById(ID id) {
    return delegate.deleteById(id);
  }

  @Override
  public T find(ID id) {
    return delegate.find(id);
  }

  @Override
  public List<T> findAll(List<ID> ids) {
    return delegate.findAll(ids);
  }
}
