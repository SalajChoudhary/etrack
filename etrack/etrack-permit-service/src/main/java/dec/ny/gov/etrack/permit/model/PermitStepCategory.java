package dec.ny.gov.etrack.permit.model;

public enum PermitStepCategory {
  
  PROJ("PROJ"),
  PROJ_INFO("PROJ-INFO"),
  SUPPORT_DOC("SUPPORT-DOC"), 
  SIGN_SUBMIT("SIGN-SUBMIT");
  
  private final String stepCategory;
  
  private PermitStepCategory(String stepCategory) {
    this.stepCategory=stepCategory;
  }
  
  public String getStepCategory() {
    return this.stepCategory;
  }
}
