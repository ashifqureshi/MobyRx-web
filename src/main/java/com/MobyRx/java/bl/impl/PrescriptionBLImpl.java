package com.MobyRx.java.bl.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.MobyRx.java.entity.common.AccountEntity;
import com.MobyRx.java.entity.common.PrescriptionEntity;
import com.MobyRx.java.entity.common.PrescriptionItemEntity;
import com.MobyRx.java.entity.doctor.DoctorProfileEntity;
import com.MobyRx.java.entity.doctor.DrugsEntity;
import com.MobyRx.java.entity.patient.PatientProfileEntity;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.MobyRx.java.bl.PrescriptionBL;
import com.MobyRx.java.dao.PrescriptionDao;
import com.MobyRx.java.service.wso.PrescriptionItemWSO;
import com.MobyRx.java.service.wso.PrescriptionWSO;
import com.MobyRx.java.service.wso.StatusWSO;
import com.MobyRx.java.service.wso.WSOToEntityConversion;
import java.io.File;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;

@Repository("prescriptionBL")
@Transactional
public class PrescriptionBLImpl extends CommonBLImpl implements PrescriptionBL{
	@Autowired
	private PrescriptionDao  prescriptionDao;


	private Logger logger = LoggerFactory.getLogger(PrescriptionBLImpl.class);


	public void save(PrescriptionWSO prescriptionWSO, StatusWSO statusWSO) throws Exception {
		PrescriptionEntity prescriptionEntity = new PrescriptionEntity();
		AccountEntity accountEntity = prescriptionDao.get(AccountEntity.class, prescriptionWSO.getPharmacy().getId());
		prescriptionEntity.setPharmacy(accountEntity);
		prescriptionEntity.setCreatedAt(new Date());
		DoctorProfileEntity doctorProfileEntity = prescriptionDao.get(DoctorProfileEntity.class, prescriptionWSO.getDoctor().getId());
		prescriptionEntity.setDoctor(doctorProfileEntity);
		prescriptionEntity.setId(null);
		prescriptionEntity.setInstruction(prescriptionWSO.getInstruction());
		prescriptionEntity.setNextAppointment(prescriptionWSO.getNextAppointment());
		PatientProfileEntity patientProfileEntity = prescriptionDao.get(PatientProfileEntity.class, prescriptionWSO.getPatient().getId());
		prescriptionEntity.setPatient(patientProfileEntity);
		if(prescriptionWSO.getPrescriptionItems()!=null)
		{
		Set<PrescriptionItemEntity> prescriptionItems= new HashSet<PrescriptionItemEntity>();
		for (Iterator<PrescriptionItemWSO> it = prescriptionWSO.getPrescriptionItems().iterator(); it.hasNext(); ) {
			PrescriptionItemWSO  prescriptionItemWSO= it.next();
			PrescriptionItemEntity prescriptionItemEntity = new PrescriptionItemEntity();
			prescriptionItemEntity.setBeforeFood(prescriptionItemWSO.isBeforeFood());
			prescriptionItemEntity.setCreatedAt(new Date());
			prescriptionItemEntity.setDoseType(WSOToEntityConversion.transform(prescriptionItemWSO.getDoseType()));
			prescriptionItemEntity.setDrugName(prescriptionItemWSO.getDrugName());
			DrugsEntity drugsEntity =prescriptionDao.get(DrugsEntity.class, prescriptionItemWSO.getDrugs().getId());
			prescriptionItemEntity.setDrugs(drugsEntity);
			prescriptionItemEntity.setDuration(prescriptionItemWSO.getDuration());
			prescriptionItemEntity.setDurationType(WSOToEntityConversion.transform(prescriptionItemWSO.getDurationType()));
			prescriptionItemEntity.setId(null);
			prescriptionItemEntity.setInstruction(prescriptionItemWSO.getInstruction());
			prescriptionItemEntity.setPrescription(prescriptionEntity);
			prescriptionItemEntity.setQuantity(prescriptionItemWSO.getQuantity());
			prescriptionItemEntity.setUpdatedAt(new Date());
			prescriptionItems.add(prescriptionItemEntity);
		}
		prescriptionEntity.setPrescriptionItems(prescriptionItems);
		}
		prescriptionEntity.setPrescriptionNumber(prescriptionWSO.getPrescriptionNumber());
		prescriptionEntity.setUpdatedAt(new Date());
		prescriptionEntity.setStatus(com.MobyRx.java.service.converter.DataMapper.transform(prescriptionWSO.getStatus()));
		String filePath=com.MobyRx.java.util.MobyRxUtil.convertStringToImageByteArray(prescriptionWSO.getImageAsString(),patientProfileEntity.getId().toString());
		if(filePath!=null)
		prescriptionEntity.setFilePaths(filePath);
		prescriptionEntity.setPrescriptionType(com.MobyRx.java.service.converter.DataMapper.transform(prescriptionWSO.getPrescriptionType()));
		prescriptionDao.save(prescriptionEntity);
		statusWSO.setCode(HttpStatus.SC_OK);;
		statusWSO.setMessage("Sucessful");

	}


	public List<PrescriptionEntity> getPrescriptionByPatientId(long patientId, StatusWSO statusWSO) throws Exception {
		return (List<PrescriptionEntity>)prescriptionDao.get(PrescriptionEntity.class, "patient", patientId);
		
	}
}
