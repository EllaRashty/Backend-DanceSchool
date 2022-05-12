package twins.logic.item;

import java.util.List;
import java.util.Map;

import twins.logic.ItemsService;

public interface AdvancedItemsService extends ItemsService {

	public List<ItemBoundary> getAllItems(int size, int page, String space, String email);

//	public ItemBoundary rateServiceProvider(String space, String email, Item item, Map<String, Object> itemAttributes);

//    public ItemBoundary createNewServiceProvider(String space, String email, Map<String, Object> newAttributes);

    public ItemBoundary updateServiceProvider(String space, String email, Item item, Map<String, Object> updatedAttributes);

    public List<ItemBoundary> getAllItemsByAttributes(int size, int page, String space, String email, String type,
			Map<String, Object> attributes);
}
