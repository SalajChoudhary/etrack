package dec.ny.gov.etrack.permit.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import dec.ny.gov.etrack.permit.entity.MessageEntity;
import dec.ny.gov.etrack.permit.entity.PermitTypeCodeEntity;
import dec.ny.gov.etrack.permit.entity.SystemParameterEntity;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.KeyValue;
import dec.ny.gov.etrack.permit.model.MaintanenceCodeTable;
import dec.ny.gov.etrack.permit.repo.MessageRepository;
import dec.ny.gov.etrack.permit.repo.PermitTypeCodeRepo;
import dec.ny.gov.etrack.permit.repo.SystemParamterRepo;
import dec.ny.gov.etrack.permit.service.ETrackCodeTableService;

@Service
public class ETrackCodeTableServiceImpl implements ETrackCodeTableService {

	
	@Autowired
	private SystemParamterRepo systemParamterRepo;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private PermitTypeCodeRepo permitTypeCodeRepo;
	
	
	 private static final Logger logger = LoggerFactory.getLogger(ETrackCodeTableServiceImpl.class.getName());
	

	@Override
	public void updateSystemParameter(MaintanenceCodeTable systemParameter) {
		try {
			List<String> ids = systemParameter.getKeyValues().stream().map(KeyValue::getUniquekey).collect(Collectors.toList());
				switch(systemParameter.getTableName()) {
				case "E_SYSTEM_PARAMETER":
					saveSystemParameter(systemParameter, ids);
					break;
				case "E_MESSAGE":
					saveMessages(systemParameter, ids);
					break;
				case "E_PERMIT_TYPE_CODE":
					savePermitType(systemParameter, ids);
					break;
				}
			
			
			SystemParameterEntity systemParameterEntity = new SystemParameterEntity();
			BeanUtils.copyProperties(systemParameter, systemParameterEntity);
			systemParamterRepo.save(systemParameterEntity);
		}catch (Exception e) {
			logger.error("Error while updating system parameter", e);
			throw new ETrackPermitException("SAVE_SYSTEM_PARAMETER_ERROR",
					"Error while saving system parameter", e);
		}
		
		
	}


	private void saveSystemParameter(MaintanenceCodeTable systemParameter, List<String> ids) {
		List<SystemParameterEntity> systemParameterEntities = systemParamterRepo.findAllById(ids);
		for(KeyValue keyValue : systemParameter.getKeyValues()) {
			SystemParameterEntity parameterEntity =  systemParameterEntities.stream().filter(obj -> obj.getUrlId().equalsIgnoreCase(keyValue.getUniquekey())).findFirst().get();
			parameterEntity.setUrlLink(keyValue.getValue());
		}
		systemParamterRepo.saveAll(systemParameterEntities);
	}
	
	private void saveMessages(MaintanenceCodeTable codeTableDto,  List<String> ids) {
		List<MessageEntity> messageEntities = messageRepository.findAllById(ids);
		for(KeyValue keyValue : codeTableDto.getKeyValues()) {
			MessageEntity messageEntity =  messageEntities.stream().filter(obj -> obj.getMessageCode().equalsIgnoreCase(keyValue.getUniquekey())).findFirst().get();
			messageEntity.setMessageDesc(keyValue.getValue());
		}
		messageRepository.saveAll(messageEntities);
	}
	
	private void savePermitType(MaintanenceCodeTable codeTableDto,  List<String> ids) {
		List<PermitTypeCodeEntity> permiTypeEntitis = permitTypeCodeRepo.findAllById(ids);
		for(KeyValue keyValue : codeTableDto.getKeyValues()) {
			PermitTypeCodeEntity permitType =  permiTypeEntitis.stream().filter(obj -> obj.getPermitTypeCode().equalsIgnoreCase(keyValue.getUniquekey())).findFirst().get();
			permitType.setPermitTypeDesc(keyValue.getValue());
		}
		permitTypeCodeRepo.saveAll(permiTypeEntitis);
	}

}
