package demo.ddz.module.player;

import demo.ddz.helper.repository.single.DelegateSingleValueRepository;
import demo.ddz.helper.repository.single.SingleCacheRepository;
import demo.ddz.helper.repository.single.SingleValueRepositoryMeta;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepository extends DelegateSingleValueRepository<Player, Long> {

  public PlayerRepository(Meta meta, Cache cache) {
    super(meta, cache);
  }

  @Component
  public static class Meta implements SingleValueRepositoryMeta<Player, Long> {

    @Override
    public String module() {
      return "model_player";
    }

    @Override
    public Long indexId(Player entity) {
      return entity.getId();
    }
  }

  @Component
  public static class Cache extends SingleCacheRepository<Player, Long> {

    public Cache(Meta meta) {
      super(meta, null);
    }
  }
}
