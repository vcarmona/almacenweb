package mx.gob.inr.farmacia

import javax.annotation.PostConstruct;
import mx.gob.inr.utils.CierreController
import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_FARMACIA'])
class CierreFarmaciaController extends CierreController<CierreFarmacia> {

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	CierreFarmaciaService cierreFarmaciaService
	
	public CierreFarmaciaController(){
		super(CierreFarmacia)
	}
	
	
	@PostConstruct
	public void init(){
		cierreService = cierreFarmaciaService
	}

 
}
