
package com.MobyRx.java.service;
import com.MobyRx.java.bl.PrescriptionBL;
import com.MobyRx.java.entity.common.PrescriptionEntity;
import com.MobyRx.java.service.wso.DataMapper;
import com.MobyRx.java.service.wso.PrescriptionWSO;
import com.MobyRx.java.service.wso.StatusWSO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


/**
 * Created by IntelliJ IDEA.
 * User: uday chandu G N
 * Date: 9/5/16
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/prescription")
@Transactional
public class PrescriptionService extends BaseService{
	 private Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

	  
	    private PrescriptionBL prescriptionBL;


	    @Autowired(required = true)
	    public void setCommonBL(PrescriptionBL prescriptionBL) {
	        this.prescriptionBL = prescriptionBL;
	    }

	    @POST
	    public Response addPrescription(PrescriptionWSO prescriptionWSO, @Context UriInfo uriInfo) throws Exception {
	        StatusWSO statusWSO = new StatusWSO();
	        prescriptionBL.save(prescriptionWSO, statusWSO);
	        return sendResponse(statusWSO);
	    }
	    
	    @GET
	    @Path("/{patientId}")
	    public Response getPrescriptionByPatientId(@PathParam("patientId")String patientId,@Context UriInfo uriInfo) throws Exception{
	        StatusWSO statusWSO = new StatusWSO();
	       List<PrescriptionEntity> prescriptionEntity= prescriptionBL.getPrescriptionByPatientId(Long.parseLong(patientId), statusWSO);
	       List<PrescriptionWSO> prescriptionWSO=com.MobyRx.java.service.converter.DataMapper.transformPrescription(prescriptionEntity);
	        return sendResponse(prescriptionWSO);
	    }
	    
	    
}


