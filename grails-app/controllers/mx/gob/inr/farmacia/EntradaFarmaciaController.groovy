package mx.gob.inr.farmacia

import javax.annotation.PostConstruct;

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

import mx.gob.inr.farmacia.EntradaFarmaciaService;
import mx.gob.inr.farmacia.EntradaFarmacia;
import mx.gob.inr.materiales.SalidaMaterial;
import mx.gob.inr.utils.EntradaController
import mx.gob.inr.utils.IOperacionService

class EntradaFarmaciaController extends EntradaController<EntradaFarmacia> {
    
	EntradaFarmaciaService entradaFarmaciaService	
	
	public EntradaFarmaciaController(){
		super(EntradaFarmacia,'F')
	}
	
	@PostConstruct
	public void init(){
		servicio = entradaFarmaciaService		
	}
	
	////////////METODOS PROPIOS////////////////////////////////
	
	def consultarDetalleMaterial(){
		def folioMaterial = params.int('folioAlmacen')
		def json = entradaFarmaciaService.consultarDetalleMaterial(folioMaterial,entradaFarmaciaService.AREA_FARMACIA) as JSON
		log.info(json)
		render json
	}
	
	
	
	def uniqueFolioSalAlma(){
				
		def result=true
				
		if(params.checkFolio){
			def folioSalAlma  = params.int('checkFolio')
			log.info("FolioSalAlma: " + folioSalAlma)
			result = !entradaFarmaciaService.checkFolioSalAlma(folioSalAlma)
		}
		
		render text: result, contentType:"text/html", encoding:"UTF-8"
	}
	
}
