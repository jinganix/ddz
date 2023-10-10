package demo.ddz.module.table.repository;

import demo.ddz.helper.repository.single.DelegateSingleValueRepository;
import demo.ddz.helper.repository.single.SingleCacheRepository;
import demo.ddz.helper.repository.single.SingleValueRepositoryMeta;
import demo.ddz.module.table.Table;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class TableRepository extends DelegateSingleValueRepository<Table, Long> {

  public TableRepository(Meta meta, Cache cache) {
    super(meta, cache);
  }

  @Component
  public static class Meta implements SingleValueRepositoryMeta<Table, Long> {

    @Override
    public String module() {
      return "model_table";
    }

    @Override
    public Long indexId(Table entity) {
      return entity.getId();
    }
  }

  @Component
  public static class Cache extends SingleCacheRepository<Table, Long> {

    public Cache(Meta meta) {
      super(meta, null);
    }
  }
}
