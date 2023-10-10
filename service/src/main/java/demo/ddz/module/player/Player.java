package demo.ddz.module.player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Player {

  private Long id;

  private Long tableId;
}
