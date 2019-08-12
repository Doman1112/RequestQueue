class NativeRequestDecorator implements Request { 

  private Request mRequest;
  
  NativeRequestDecorator(Request request) {
    mRequest = request;
  }
  
  @Override 
  public void run() {
    Lock lock = new Lock();
    lock.acquire();
    mRequest.run();
    lock.release();
    
    RequestQueue.sharedInstance().addRequest(mRequest::callback);
  }
}
