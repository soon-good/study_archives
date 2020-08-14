package io.study.graph.bfs.queue;

import io.study.graph.bfs.vo.Vertex;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Java 에서 제공하는 큐
 * 빠른 테스트를 위해 일단은 JAVA 내장 자료형 (Queue<T>, LinkedList<T> 사용)
 * 참고자료 :
 * 	https://pridiot.tistory.com/68
 */
public class JavaQueue {

	private Queue<Vertex> queue = new LinkedList<>();

	public void enqueue(Vertex v){
		queue.offer(v);
	}

	public Vertex dequeue(){
		Vertex poll = queue.poll();

//		System.out.println(poll.getIsVisited());
//		if(poll.getIsVisited()){
//			System.out.print(poll.getVertexNo());
//			System.out.print("->");
//		}

		return poll;
	}

	public Vertex showCurrent(){
		return queue.peek();
	}

	public void printQueue(){
		queue.stream().forEach(v->{
			System.out.print(v);
			System.out.print("->");
		});
		System.out.println("||");
	}

	public boolean isEmpty(){
		return queue.isEmpty();
	}
}
