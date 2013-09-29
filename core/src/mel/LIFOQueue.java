package mel;

import java.util.LinkedList;

/**
 * @author Warren S
 */
class LIFOQueue<E>
{
	private LinkedList<E> q = new LinkedList<>();

	public void push(E e) { q.addFirst(e); }
	public E pop() { return q.removeFirst(); }
	public E peek() { return q.getFirst(); }
	public boolean isEmpty() { return q.isEmpty(); }
}