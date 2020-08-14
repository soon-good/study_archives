package io.study.graph.bfs.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Vertex {
	private Boolean isVisited;
	private Integer vertexNo;
	private List<Vertex> adjacentList = new ArrayList<Vertex>();

	@Builder
	public Vertex(Boolean isVisited, Integer vertexNo){
		this.isVisited = isVisited;
		this.vertexNo = vertexNo;
	}

	public Vertex addEdge(Vertex v){
		adjacentList.add(v);
		return this;
	}

	public void printAdjacentList(){
		System.out.print(getVertexNo() + "->");
		this.adjacentList.stream()
			.forEach(v->{
				System.out.print(v.getVertexNo() + "->");
			});
		System.out.print("||");
		System.out.println("");
	}
}
