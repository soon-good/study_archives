import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2 {
	public static void main(String [] args){
		List<Map<String,Object>> list = new ArrayList<>();

		for(int i=0; i<10; i++){
			Map<String,Object> m = new HashMap<>();
			if(i == 9){
				m.put("a", null);
				list.add(m);
				break;
			}

			m.put("a", "a:"+String.valueOf(i));
			m.put("b", "bbbbbbbb");
			list.add(m);
		}

		list.stream().forEach(map -> {
			String data = String.valueOf(map.get("a"));
			data = data + ":: ^^ ";
			map.put("a", data);
		});

		list.stream().forEach(System.out::println);

		System.out.println("======= =======");
		list = new ArrayList<>();
		list.stream().forEach(System.out::println);
	}
}
