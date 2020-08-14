package io.study.graph.bfs;

import io.study.graph.bfs.queue.JavaQueue;
import io.study.graph.bfs.vo.Graph;
import io.study.graph.bfs.vo.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BfsTest {

	private Graph graph = null;
	private Vertex v1 = null;
	private Vertex v2 = null;
	private Vertex v3 = null;
	private Vertex v4 = null;
	private Vertex v5 = null;
	private Vertex v6 = null;
	private Vertex v7 = null;
	private Vertex v8 = null;

	@BeforeEach
	void initGraph(){
		graph = new Graph();

		v1 = graph.createVertex(1);
		v2 = graph.createVertex(2);
		v3 = graph.createVertex(3);
		v4 = graph.createVertex(4);
		v5 = graph.createVertex(5);
		v6 = graph.createVertex(6);
		v7 = graph.createVertex(7);
		v8 = graph.createVertex(8);

		graph.addVertex(v1);
		graph.addVertex(v2);
		graph.addVertex(v3);
		graph.addVertex(v4);
		graph.addVertex(v5);

		v1.addEdge(v2)
			.addEdge(v3);

		v2.addEdge(v1)
			.addEdge(v5)
			.addEdge(v3)
			.addEdge(v4);

		v3.addEdge(v1)
			.addEdge(v5)
			.addEdge(v8)
			.addEdge(v7);

		v4.addEdge(v2)
			.addEdge(v5);

		v5.addEdge(v6)
			.addEdge(v2)
			.addEdge(v3);

		v6.addEdge(v5);

		v7.addEdge(v3);

		v8.addEdge(v3)
			.addEdge(v7);

		System.out.println(" ======= 인접리스트 목록들 (START) ======= ");
		v1.printAdjacentList();
		v2.printAdjacentList();
		v3.printAdjacentList();
		v4.printAdjacentList();
		v5.printAdjacentList();
		v6.printAdjacentList();
		v7.printAdjacentList();
		v8.printAdjacentList();
		System.out.println(" ======= 인접리스트 목록들 (END) ======= ");
	}

	@Test
	@DisplayName("Builder")
	public void testBuilder(){
		Vertex v = Vertex.builder()
			.isVisited(false)
			.vertexNo(1)
			.build();

		System.out.println(v);
	}

	@Test
	@DisplayName("Graph Test #1 > Graph 만들기 ")
	public void testGraph(){
		Graph g = new Graph();

		Vertex v1 = g.createVertex(1);
		Vertex v2 = g.createVertex(2);
		Vertex v3 = g.createVertex(3);
		Vertex v4 = g.createVertex(4);
		Vertex v5 = g.createVertex(5);

		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);

		v1.addEdge(v2)
			.addEdge(v5);

		v2.addEdge(v1)
			.addEdge(v5)
			.addEdge(v3)
			.addEdge(v4);

		v3.addEdge(v2)
			.addEdge(v4);

		v4.addEdge(v2)
			.addEdge(v4);

		v5.addEdge(v4)
			.addEdge(v1)
			.addEdge(v2);

		v1.printAdjacentList();
		v2.printAdjacentList();
		v3.printAdjacentList();
		v4.printAdjacentList();
		v5.printAdjacentList();
	}

	@Test
	@DisplayName("Java 내장 큐 테스트 ")
	void testJavaQueue(){
		JavaQueue q = new JavaQueue();

		Graph g = new Graph();

		Vertex v1 = g.createVertex(1);
		Vertex v2 = g.createVertex(2);
		Vertex v3 = g.createVertex(3);
		Vertex v4 = g.createVertex(4);
		Vertex v5 = g.createVertex(5);

		v1.addEdge(v2)
			.addEdge(v5);

		v2.addEdge(v1)
			.addEdge(v5)
			.addEdge(v3)
			.addEdge(v4);

		q.enqueue(v1);
		q.enqueue(v2);
		q.enqueue(v3);
		q.enqueue(v4);
		q.enqueue(v5);

	}

	@Test
	@DisplayName("그래프 BFS 순회")
	void testGraphBFS(){
		JavaQueue q = new JavaQueue();
		q.enqueue(v1);
		v1.setIsVisited(true);

		while(!q.isEmpty()){
			Vertex u = q.dequeue();
			if(u.getIsVisited()){
				System.out.print(u.getVertexNo() + "->");
			}

			u.getAdjacentList().stream().forEach(v->{
				if(!v.getIsVisited()){
					v.setIsVisited(true);
					q.enqueue(v);
				}
			});
		}
	}
}
