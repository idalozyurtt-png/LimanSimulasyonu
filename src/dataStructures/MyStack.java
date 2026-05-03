
package datastructures;


public class MyStack<T> {
    private Node<T> top;
    private int size;
    private int capacity;
   

    public MyStack(int capacity) {
        this.top = null;
        this.size = 0;
        this.capacity = capacity;
    }

    public void push(T data) {
        
        if (isFull()) {
            System.out.println("Stack dolu! Eklenemiyor: " + data);
            return;
        } 
        Node<T> newNode = new Node<>(data);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public T pop() {
        if (isEmpty()) return null;
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    public T peek() {
        return isEmpty() ? null : top.data;
    }
    
     public boolean isFull() {
        return size == capacity;
    }
    
    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public void clear() {
        top = null;
        size = 0;
    }
}
