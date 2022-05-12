package twins.logic.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -3852861819213897449L;

  public ItemNotFoundException() {
  }

  public ItemNotFoundException(String item) {
    super(item);
  }

  public ItemNotFoundException(Throwable cause) {
    super(cause);
  }

  public ItemNotFoundException(String item, Throwable cause) {
    super(item, cause);
  }
}
