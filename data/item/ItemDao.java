package twins.data.item;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import twins.data.ItemEntity;

public interface ItemDao extends PagingAndSortingRepository<ItemEntity, String> {

	public List<ItemEntity> findAllByActive(@Param("active") boolean active, Pageable pageable);

	@Query(value = "select * " + "from items i " + "where lower(i.type) = :type and "
			+ "(lower(i.name) like :name or lower(i.item_attributes) like :itemAttributes)", nativeQuery = true)
	public List<ItemEntity> findAllByTypeAndNameLikeOrItemAttributesLike(@Param("type") String type,
			@Param("name") String name, @Param("itemAttributes") String itemAttributes, Pageable pageable);

	@Query(value = "select * " + "from items i " + "where i.active = :active and i.type = :type and "
			+ "(lower(i.name) like :name or lower(i.item_attributes) like :itemAttributes)", nativeQuery = true)
	public List<ItemEntity> findAllByActiveAndTypeAndNameLikeOrItemAttributesLike(@Param("active") boolean active,
			@Param("type") String type, @Param("name") String name, @Param("itemAttributes") String itemAttributes,
			Pageable pageable);
}
