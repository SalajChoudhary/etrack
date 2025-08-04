package dec.ny.gov.etrack.permit.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.permit.entity.ActionNoteEntity;
import dec.ny.gov.etrack.permit.entity.Invoice;
import dec.ny.gov.etrack.permit.entity.InvoiceFeeDetail;
import dec.ny.gov.etrack.permit.entity.ProjectAlert;
import dec.ny.gov.etrack.permit.entity.ProjectNote;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.MissingDocument;
import dec.ny.gov.etrack.permit.model.PaymentActionNote;
import dec.ny.gov.etrack.permit.model.ProjectNoteView;
import dec.ny.gov.etrack.permit.repo.ActionNoteRepo;
import dec.ny.gov.etrack.permit.repo.InvoiceFeeDetailRepo;
import dec.ny.gov.etrack.permit.repo.InvoiceRepo;
import dec.ny.gov.etrack.permit.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.permit.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.permit.service.ETrackNoteService;
import dec.ny.gov.etrack.permit.util.ETrackPermitConstant;

@Service
public class ETrackNoteServiceImpl implements ETrackNoteService {

  @Autowired
  private ActionNoteRepo actionNoteRepo;
  @Autowired
  private InvoiceRepo invoiceRepo;
  @Autowired
  private InvoiceFeeDetailRepo invoiceFeeDetailRepo;
  @Autowired
  private ProjectNoteRepo projectNoteRepo;
  @Autowired
  private ProjectAlertRepo projectAlertRepo;
  private final SimpleDateFormat MM_DD_YYYY_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
  private final SimpleDateFormat MM_DD_YYYY_AM_PM_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  private static final Logger logger = LoggerFactory.getLogger(ETrackNoteServiceImpl.class.getName());

  @Override
  public List<ProjectNoteView> getNotes(String userId, String contextId, Long projectId) {
    logger.info("Entering into retrieve the Notes User Id {}, Context Id {}", userId, contextId);

    // List<ProjectNote> notesList = projectNoteRepo.findAllByProjectId(projectId);

    List<ActionNoteEntity> actionNotes = actionNoteRepo.findActionNoteByProjectId(projectId);

    if (CollectionUtils.isEmpty(actionNotes)) {
      logger.info("There is no data found for the projectId {}, User Id {}, Context Id {} ",
          projectId, userId, contextId);
      return new ArrayList<>();
    }

    List<ProjectNoteView> projectNotes = new ArrayList<>();
    for (ActionNoteEntity noteDto : actionNotes) {
      ProjectNoteView note = new ProjectNoteView();
      MM_DD_YYYY_FORMAT.setLenient(false);
      note.setActionDate(MM_DD_YYYY_FORMAT.format(noteDto.getActionDate()));
      note.setActionType(noteDto.getActionTypeCode());
      if (noteDto.getActionTypeCode()
          .equals(ETrackPermitConstant.REQUIRED_DOCUMENTS_NOT_RECEIVED)) {
        if (StringUtils.hasLength(noteDto.getActionNote())) {
          Set<Integer> documentTitleList = new HashSet<>();
          String[] documentTitleIds = noteDto.getActionNote().split(",");
          for (String title : documentTitleIds) {
            documentTitleList.add(Integer.parseInt(title));
          }
          note.setMissingReqdDoc(projectNoteRepo.findAllDocumentTitleByIds(documentTitleList));
          note.setActionNote(note.getMissingReqdDoc().toString());
        }
      } else {
        note.setActionNote(noteDto.getActionNote());
      }
      note.setComments(noteDto.getComments());
      note.setProjectNoteId(noteDto.getProjectNoteId());
      note.setActionTypeDesc(noteDto.getActionTypeDesc());
      if (StringUtils.hasLength(noteDto.getCreatedById())
          && (ETrackPermitConstant.SYSTEM_USER_ID.equals(noteDto.getCreatedById())
              || ETrackPermitConstant.ENTERPRISE_SYSTEM_USER_ID.equals(noteDto.getCreatedById()))) {
        note.setSystemGenerated("Y");
      } else {
        note.setSystemGenerated("N");
      }
      projectNotes.add(note);
    }
    logger.info("Exiting from retrieve the Notes User Id {}, Context Id {}", userId, contextId);
    return projectNotes;
  }

  @Override
  public ProjectNoteView getNote(final String userId, final String contextId, final Long projectId,
      final Long noteId) {

    logger.info("Entering into retrieve the Note details User Id {}, Context Id {}", userId,
        contextId);
    ActionNoteEntity actionNote =
        actionNoteRepo.findActionNoteByNoteIdAndProjectId(projectId, noteId);

    if (actionNote == null) {
      throw new BadRequestException("INVALID_REQ",
          "There is no data available for the input note Id", noteId);
    }
    ProjectNoteView note = new ProjectNoteView();
    note.setActionType(actionNote.getActionTypeCode());
    note.setActionNote(actionNote.getActionNote());
    note.setComments(actionNote.getComments());
    note.setProjectNoteId(actionNote.getProjectNoteId());
    MM_DD_YYYY_FORMAT.setLenient(false);
    MM_DD_YYYY_AM_PM_FORMAT.setLenient(false);
    if (actionNote.getCreateDate() != null) {
      note.setCreateDate(MM_DD_YYYY_FORMAT.format(actionNote.getCreateDate()));
    }
    if (ETrackPermitConstant.SYSTEM_USER_ID.equals(actionNote.getCreatedById()) 
        || ETrackPermitConstant.ENTERPRISE_SYSTEM_USER_ID.equals(actionNote.getCreatedById())) {
      note.setActionDate(MM_DD_YYYY_FORMAT.format(actionNote.getActionDate()));
      if (actionNote.getActionTypeCode().equals(ETrackPermitConstant.INVOICE_CANCELATION)) {
        if (StringUtils.hasLength(actionNote.getActionNote())) {
          String[] notes = actionNote.getActionNote().split("\\|");
          note.setActionNote(notes[0]);
          if (notes.length >1) {
            note.setReason(notes[1]);
          }
        }
      }
      note.setSystemGenerated("Y");
      note.setActionTypeDesc(actionNote.getActionTypeDesc());
      note.setCancelledUserId(actionNote.getCancelUserId());
      note.setCancelledUserName(actionNote.getCancelUserNm());      
      if (actionNote.getModifiedDate() != null) {
        note.setUpdatedDate(MM_DD_YYYY_AM_PM_FORMAT.format(actionNote.getModifiedDate()));
      }
      if (StringUtils.hasText(actionNote.getModifiedById())) {
        note.setUpdatedBy(actionNote.getModifiedById());
      }
      if (actionNote.getActionTypeCode().equals(ETrackPermitConstant.PAYMENT_RECEIVED_DATE)) {
        Invoice invoice = invoiceRepo.findInvoiceByProjectIdAndStatus(projectId,
            ETrackPermitConstant.INVOICE_FEE_PAID);

        if (invoice == null) {
          throw new BadRequestException("INVALID_REQ",
              "There is no invoice available for this project Id ", projectId);
        }

        PaymentActionNote paymentActionNote = new PaymentActionNote();
        Integer totalAmount = 0;
        paymentActionNote.setPaymentReference(invoice.getPaymentConfirmnId());
        paymentActionNote.setInvoiceNumber(invoice.getFmisInvoiceNum());
        Map<String, InvoiceFeeDetail> invoiceFeeTypeMap = new HashMap<>();
        List<InvoiceFeeDetail> invoiceFeeDetails = invoiceFeeDetailRepo.findFeeDetailsForFeeTypes();

        invoiceFeeDetails.forEach(invoiceFeeDetail -> {
          invoiceFeeTypeMap.put(invoiceFeeDetail.getInvoiceFeeType(), invoiceFeeDetail);
        });
        logger.debug("Invoice Detail Map {}. User Id {}, Context Id {}", invoiceFeeTypeMap, userId,
            contextId);

        InvoiceFeeDetail invoiceFeeDetail = null;
        if (StringUtils.hasLength(invoice.getInvoiceFeeType1())) {
          invoiceFeeDetail = invoiceFeeTypeMap.get(invoice.getInvoiceFeeType1());
          totalAmount += invoiceFeeDetail.getInvoiceFee();
          paymentActionNote.setProjectTypeFee1(invoiceFeeDetail.getInvoiceFee());
          paymentActionNote.setProjectType1(invoiceFeeDetail.getPermitTypeDesc());
        }

        if (StringUtils.hasLength(invoice.getInvoiceFeeType2())) {
          invoiceFeeDetail = invoiceFeeTypeMap.get(invoice.getInvoiceFeeType2());
          totalAmount += invoiceFeeDetail.getInvoiceFee();
          paymentActionNote.setProjectTypeFee2(invoiceFeeDetail.getInvoiceFee());
          paymentActionNote.setProjectType2(invoiceFeeDetail.getPermitTypeDesc());
        }
        if (StringUtils.hasLength(invoice.getInvoiceFeeType3())) {
          invoiceFeeDetail = invoiceFeeTypeMap.get(invoice.getInvoiceFeeType3());
          totalAmount += invoiceFeeDetail.getInvoiceFee();
          paymentActionNote.setProjectTypeFee3(invoiceFeeDetail.getInvoiceFee());
          paymentActionNote.setProjectType3(invoiceFeeDetail.getPermitTypeDesc());
        }
        note.setPaymentActionNote(paymentActionNote);
        paymentActionNote.setTotalAmount(totalAmount);
      } else if (actionNote.getActionTypeCode()
          .equals(ETrackPermitConstant.REQUIRED_DOCUMENTS_NOT_RECEIVED)) {
        if (StringUtils.hasLength(actionNote.getActionNote())) {
          Set<Integer> documentTitleList = new HashSet<>();
          String[] documentTitleIds = actionNote.getActionNote().split(",");
          for (String title : documentTitleIds) {
            documentTitleList.add(Integer.parseInt(title));
          }
          note.setMissingReqdDoc(projectNoteRepo.findAllDocumentTitleByIds(documentTitleList));
          note.setActionNote("Project ID " + projectId + " missing the following documents");;
        }
      }

    } else {
      note.setActionDate(MM_DD_YYYY_FORMAT.format(actionNote.getActionDate()));
      if (actionNote.getModifiedDate() != null) {
        note.setUpdatedDate(MM_DD_YYYY_FORMAT.format(actionNote.getModifiedDate()));
      }
      note.setSystemGenerated("N");
    }
    logger.info("Exiting from retrieve the Note details User Id {}, Context Id {}", userId,
        contextId);
    return note;
  }

  @Override
  public ProjectNoteView addNotes(String userId, String contextId, Long projectId,
      ProjectNoteView projectNote) {
    logger.info("Entering into adding/storing the Notes User Id {}, Context Id {}", userId,
        contextId);

    MM_DD_YYYY_FORMAT.setLenient(false);
    if (StringUtils.isEmpty(projectNote.getActionDate())
        || StringUtils.isEmpty(projectNote.getActionNote())
        || StringUtils.isEmpty(projectNote.getActionType())) {
      throw new BadRequestException("INVALID_REQ", "Invalid input parameter", projectNote);
    }

    ProjectNote note = null;
    if (projectNote.getProjectNoteId() != null) {
      Optional<ProjectNote> noteAvailability =
          projectNoteRepo.findById(projectNote.getProjectNoteId());
      if (!noteAvailability.isPresent()) {
        throw new BadRequestException("NO_NOTE_AVAIL",
            "There is no note avaialble for the requested project note id", projectNote);
      }
      note = noteAvailability.get();
      note.setModifiedDate(new Date());
      note.setModifiedById(userId);
    } else {
      note = new ProjectNote();
      note.setCreateDate(new Date());
      note.setCreatedById(userId);
    }
    try {
      note.setActionDate(MM_DD_YYYY_FORMAT.parse(projectNote.getActionDate()));
    } catch (ParseException e) {
      throw new BadRequestException("INVALID_ACTION_DATE",
          "Action date in the note request not the valid format i.e. MM/dd/yyyy ", projectNote);
    }
    
    if (!ETrackPermitConstant.REQUIRED_DOCUMENTS_NOT_RECEIVED.equals(projectNote.getActionType())) {
      StringBuilder notes = new StringBuilder();
      notes.append(projectNote.getActionNote());
      if (StringUtils.hasLength(projectNote.getReason())) {
        notes.append("|").append(projectNote.getReason());
      }
      note.setActionNote(notes.toString());    
    }
    note.setComments(projectNote.getComments());
    note.setActionTypeCode(projectNote.getActionType());
    note.setProjectId(projectId);
    ProjectNote result = projectNoteRepo.save(note);
    projectNote.setProjectNoteId(result.getProjectNoteId());
    logger.info("Existing from adding/storing the Notes User Id {}, Context Id {}", userId,
        contextId);
    return projectNote;
  }

  @Override
  public void deleteNote(String userId, String contextId, Long projectId, Long projectNoteId) {
    logger.info("Entering into delete the Notes User Id {}, Context Id{}", userId, contextId);
    List<ProjectAlert> projectAlerts =
        projectAlertRepo.findByProjectIdAndProjectNoteId(projectId, projectNoteId);
    List<ProjectNote> projectNote =
        projectNoteRepo.findAllByProjectNoteIdAndProjectId(projectNoteId, projectId);
    if (CollectionUtils.isEmpty(projectNote)) {
      throw new BadRequestException("INVALID_REQ", "Invalid input parameter",
          projectId + " " + projectNoteId);
    }
    if (!CollectionUtils.isEmpty(projectAlerts)) {
      projectAlertRepo.deleteAll(projectAlerts);
    }
    projectNoteRepo.deleteById(projectNoteId);
    logger.info("Exiting into retrieve the Notes User Id {}, Context Id {}", userId, contextId);
  }

  @Override
  public void generateMissingReqDocumentNote(String userId, String contextId, Long projectId,
      MissingDocument missingDocumentsTitleIds) {
    logger.info("Entering into generateMissingReqDocumentNote User Id {}, Context Id {}", userId,
        contextId);
    ProjectNote projectNote = new ProjectNote();
    projectNote.setActionDate(new Date());
    if (missingDocumentsTitleIds != null
        && !CollectionUtils.isEmpty(missingDocumentsTitleIds.getDocumentTitleIds())) {
      projectNote.setActionNote(String.join(",", missingDocumentsTitleIds.getDocumentTitleIds()));
    } else {
      projectNote.setActionNote("0");
    }
    projectNote.setComments(missingDocumentsTitleIds.getReason());
    projectNote.setActionTypeCode(ETrackPermitConstant.REQUIRED_DOCUMENTS_NOT_RECEIVED);
    projectNote.setCreateDate(new Date());
    projectNote.setCreatedById("SYSTEM");
    projectNote.setProjectId(projectId);
    projectNoteRepo.save(projectNote);
    logger.info("Existing from generateMissingReqDocumentNote User Id {}, Context Id {}", userId,
        contextId);
  }
}
