
public RequestQueue implements Runnable {

  private static RequestQueue me;
  
  private LinkedList<Request> mRequests;
  
  private RequestQueue() {
    mRequests = new LinkedList<>();
    new Thread(this, "Request Queue").start();
  }
  
  public static synchronized RequestQueue sharedInstance() {
    if (me == null) {
      me = new RequestQueue();
    }
    
    return me;
  }
  
  public void synchronized addRequest(Request request) {
    mRequests.add(request);
    this.notify();
  }
  
  public void synchronized addSwingNativeRequest(Request request) {
    mRequests.add(new SwingNativeRequestDecorator(request));
    this.notify();
  }
  
  public void synchronized addNativeRequest(Request request) {
    mRequests.add(new NativeRequestDecorator(request));
    this.notify();
  }
  
  
  @Override
  public void run() {
    while (true) {
      Request request = null;
      synchronized (this) {
        if (mRequests.size() > 0) {
          // Remove item at front of the requests queue
          request = mRequests.remove(0);
        } else {
          // Wait for more items to be added to the queue
          request = null;

          try {
            this.wait();
          } catch (InterruptedException e) {
            // Unexpectedly restarted
            e.printStackTrace();
          }
        }
      }

      if (request != null) {
        // Process the request
        try {
          request.run();
        } catch (Exception e) {
          // Catch exception to avoid stopping thread
          e.printStackTrace();
        }
      }
    }
  }
}
