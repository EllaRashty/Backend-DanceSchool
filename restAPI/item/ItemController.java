package twins.restAPI.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twins.logic.item.AdvancedItemsService;
import twins.logic.item.ItemBoundary;

//{
//    "itemId": {
//        "space": "DanceSchool",
//        "id": "461"
//    },
//    "type": "Beginners",
//    "name": "First steps in modern dance",
//    "active": true,
//    "createdTimestamp": "	-12-16T12:58:38.283+00:00",
//    "createdBy": {
//        "userId": {
//            "space": "SchoolDance",
//            "email": "teacher1@gmail.com"
//        }
//    },
//	 "numberOfLessons": 3,
//   "lessons": [
//		{"name": "lesson1", 
//		 "length": "10:00:00" , 
//		  "url": "www.youtubekkkkk"
//		},
//		{"name": "lesson2", 
//		 "length": "10:00:00" , 
// 		 "url": "www.youtubekkkkk"
//		},
//		{"name": "lesson3", 
//		 "length": "10:00:00" , 
// 		 "url": "www.youtubekkkkk"
//		},
//	],
// 	"itemAttributes":{
//   	"typeOfDance":"Modern dance",
//   	"numberOfLessons":"20",
//  	"workingHours":"10 hours",
//      "reviews":["good", "bad"],
//      "description":"A classical modern dance class suitable for beginners"
//  }
//}

@RestController
public class ItemController {
	private AdvancedItemsService itemsService;

	@Autowired
	public ItemController(AdvancedItemsService itemsService) {
		super();
		this.itemsService = itemsService;
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary getItem(@PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String userEmail, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId) {
		return this.itemsService.getSpecificItem(userSpace, userEmail, itemSpace, itemId);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ItemBoundary> getManyItems(@PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.itemsService.getAllItems(size, page, userSpace, userEmail);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary createItem(@PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String userEmail, @RequestBody ItemBoundary input) {
		return this.itemsService.createItem(userSpace, userEmail, input);
	}

	@RequestMapping(path = "/twins/items/{space}/{userEmail}/{itemId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateItem(@PathVariable("space") String space, @PathVariable("userEmail") String userEmail,
			@PathVariable("itemId") String itemId,
			@RequestBody ItemBoundary update) {
		this.itemsService.updateItem(space, userEmail, itemId, update);
	}
}
