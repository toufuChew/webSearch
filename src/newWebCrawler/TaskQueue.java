package newWebCrawler;

import java.util.TreeSet;

public class TaskQueue {
    private TreeSet<URLMessage> queue = null;
    private static int queueLength = 500;
    private static TaskQueue taskQueue = new TaskQueue();
    private TreeSet<URLMessage> waitQueue = null;

    public static TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getWaitQueueSize() {
        return waitQueue.size();
    }

    private TaskQueue() {
        queue = new TreeSet<>();
        waitQueue = new TreeSet<>();
    }

    public void addTaskFormWaitQueue() {
        while(queue.size() < TaskQueue.queueLength && !waitQueue.isEmpty()) {
            queue.add(waitQueue.pollFirst());
        }
    }

    public void addTask(URLMessage urlMessage) {
        synchronized (taskQueue) {
            if (queue.size() < TaskQueue.queueLength) {
                queue.add(urlMessage);
                taskQueue.notifyAll();
            } else {
                waitQueue.add(urlMessage);
                if (queue.size() <= queueLength / 2) {
                    while(!waitQueue.isEmpty() && queue.size() <= queueLength) {
                        queue.add(waitQueue.pollFirst());
                    }
                    taskQueue.notifyAll();
                }
            }
        }
    }

    public URLMessage getURLMessage() {
        synchronized (taskQueue) {
            if (!queue.isEmpty()) {
                URLMessage message = queue.pollFirst();
                return message;
            }
        }
        return null;
    }
}
