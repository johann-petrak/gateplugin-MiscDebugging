package org.jpetrak.gate.plugins;

import gate.Controller;
import gate.Document;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ControllerAwarePR;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

@CreoleResource(
    name = "DebugControllerCallbacks",
    comment = "Processing resources to help with debugging controller callbacks and process flow.")
public class DebugControllerCallbacks 
  extends AbstractLanguageAnalyser
  implements ControllerAwarePR {

  private static final Logger LOGGER = Logger.getLogger(DebugControllerCallbacks.class);
  private static final long serialVersionUID = -2205966792313180770L;


  private String inputASName;
  @Optional
  @RunTime
  @CreoleParameter(comment = "The annotation set used for input annotations")
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }
  public String getInputASName() {
    return inputASName;
  }

  private String outputASName;
  @Optional
  @RunTime
  @CreoleParameter(comment = "The annotation set used for output annotations")
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }
  public String getOutputASName() {
    return outputASName;
  }

  protected AtomicInteger nDuplicates = null;
  
  @Sharable
  public void setNDuplicates(AtomicInteger n) {
    nDuplicates = n;
  }
  
  public AtomicInteger getNDuplicates() {
    return nDuplicates;
  }
  
  protected AtomicInteger remainingDuplicates = null;
  
  @Sharable
  public void setRemainingDuplicates(AtomicInteger n) {
    remainingDuplicates = n;
  }
  
  public AtomicInteger getRemainingDuplicates() {
    return remainingDuplicates;
  }
  
  protected int duplicateId = 0;
  public int getDuplicateId() {
    return duplicateId;
  }
  
  

  @Override
  public Resource init() throws ResourceInstantiationException {
    System.err.println("DEBUG DebugControllerCallbacks: running init()");
    if(getNDuplicates() == null || getNDuplicates().get() == 0) {        
      System.err.println("DEBUG DebugControllerCallbacks: creating first instance of PR "+this.getName());
      setNDuplicates(new AtomicInteger(1));
      duplicateId = 0;
      setRemainingDuplicates(new AtomicInteger(0));
    } else {
      int thisn = getNDuplicates().getAndAdd(1);
      duplicateId = thisn;
      System.err.println("DEBUG DebugControllerCallbacks: created duplicate "+duplicateId+" of PR "+this.getName());
    }
    return this;
  }

  @Override
  public void execute() throws ExecutionException {
    if(isInterrupted()) {
      throw new ExecutionInterruptedException("Execution of A processing resources to debug the invocation of controller callbacks and other stuff has been interrupted!");
    }
    interrupted = false;

    Document doc = getDocument();
    System.err.println("DEBUG DebugControllerCallbacks: execute() for doc="+doc.getName());
  }

  @Override
  public void controllerExecutionStarted(Controller cntrlr) throws ExecutionException {
    int tmp = getRemainingDuplicates().getAndIncrement();
    if(tmp==0) {
      System.err.println("DEBUG DebugControllerCallbacks: controllerExecutionStarted() first for controller="+
              cntrlr.getName()+" duplicateId="+duplicateId+" remaining="+tmp);
    } else {
      System.err.println("DEBUG DebugControllerCallbacks: controllerExecutionStarted() subsequent for controller="+
              cntrlr.getName()+" duplicateId="+duplicateId+" remaining="+tmp);
    }
  }

  @Override
  public void controllerExecutionFinished(Controller cntrlr) throws ExecutionException {
    int tmp = getRemainingDuplicates().decrementAndGet();
    System.err.println("DEBUG DebugControllerCallbacks: controllerExecutionFinished() for controller="+
            cntrlr.getName()+" duplicateId="+duplicateId+" remaining="+tmp);
  }

  @Override
  public void controllerExecutionAborted(Controller cntrlr, Throwable thrwbl) throws ExecutionException {
    int tmp = getRemainingDuplicates().decrementAndGet();
    System.err.println("DEBUG DebugControllerCallbacks: controllerExecutionAborted() for controller="+
            cntrlr.getName()+" duplicateId="+duplicateId+" remaining="+tmp);
  }

}

