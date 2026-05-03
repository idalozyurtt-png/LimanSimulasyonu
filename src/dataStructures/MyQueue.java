
public class MyQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public MyQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()){ 
            return null;
        }
        T data = front.data;
        front = front.next;
        if (front == null){
            rear = null;
        }
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) {
           return null;
         }
      return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        front = rear = null;
        size = 0;
    }
}
