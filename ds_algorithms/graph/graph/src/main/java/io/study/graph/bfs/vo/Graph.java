package io.study.graph.bfs.vo;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Graph {

	//	private Map<Integer, List<Vertex>> vertexes = new HashMap<Integer, List<Vertex>>();
	private Map<Integer, Vertex> vertexes = new HashMap<Integer, Vertex>();

	// Set으로 변경할까??
//	private Set<Vertex> vertexes = new HashSet<Vertex>();

	public Vertex createVertex(Integer vertexNo){
		Vertex v = Vertex.builder()
			.isVisited(false)
			.vertexNo(vertexNo)
			.build();

		return v;
	}

	public void addVertex(Vertex v){
		Integer vertexNo = v.getVertexNo();

		if(!vertexes.containsKey(vertexNo)){
			vertexes.put(vertexNo, v);
		}
	}

}
