package mx.gob.inr.ceye
import grails.plugins.springsecurity.SpringSecurityService;

import javax.annotation.PostConstruct;

import mx.gob.inr.farmacia.ArticuloFarmacia;
import mx.gob.inr.farmacia.SalidaDetalleFarmacia;
import mx.gob.inr.utils.AutoCompleteService;
import mx.gob.inr.utils.UtilService;
import mx.gob.inr.utils.services.EntradaService;
import mx.gob.inr.materiales.ArticuloMaterial;

class EntradaCeyeService extends EntradaService<EntradaCeye> {

	static transactional = true
	
	UtilService utilService
	AutoCompleteService autoCompleteService	
	
	final int AREA_FARMACIA = 6220
	final int PERFIL_FARMACIA = 8
	final int PERFIL_CEYE  = 10
	
	public EntradaCeyeService(){
		super(EntradaCeye, EntradaDetalleCeye,SalidaDetalleFarmacia, ArticuloCeye, CatAreaCeye,CierreCeye)
	}
	
	@PostConstruct
	public void init(){
		super.utilService = this.utilService
		super.autoCompleteService = this.autoCompleteService		
	}
	
	def convertidora(Long cveArt,Integer cantidad){	
				
		def convertidora = ConvertidoraCeye.get(cveArt);
		
		Integer cantidadConvertida = cantidad
		Double precioConvertido  = precioAlmacen(cveArt)
		Double cociente = 1.0
		String ualma="", uceye="";
		Double calma=1.0, cceye = 1.0;
		
		if(convertidora){
			
			ualma = convertidora.unidadAlma
			calma = convertidora.cantidadAlma
			uceye  = convertidora.unidadCeye
			cceye = convertidora.cantidadCeye
			
			cociente = convertidora.cantidadAlma / convertidora.cantidadCeye
			cantidadConvertida = cantidadConvertida * cociente;
			precioConvertido = precioConvertido / cociente
						
		}				
		
		//(", precioConvertido)
		def precioFormat = String.format('%1$,.4f',precioConvertido)
		[ualma:ualma, calma:calma, uceye:uceye,cceye:cceye,cantidad:cantidadConvertida, precio:precioFormat, cociente:cociente]
	}
	
	
	def precioAlmacen(Long cveArt){
		
		def articulo = ArticuloMaterial.findWhere(cveArt:cveArt.intValue(),almacen:"F")
		def precio = 0.0
		
		if(articulo)
			 precio = articulo.movimientoProm
		return precio		
	}
	
	def consultarPaquete(String tipo){
		
		def detalle = PaqueteQuirurgicoCeye.createCriteria().list(){
			eq("tipo", tipo)
		}
		
		def results = detalle?.collect {		
			 
			//def convertido = convertidora(it.articulo.id, it.cantidad)
			def precioAlmacen = precioAlmacen(it.articulo.id)
					
			[cveArt:it.articulo.id,desArticulo:it.articulo.desArticulo?.trim(),unidad:it.articulo.unidad?.trim(),
			cantidad:it.cantidad,precioEntrada:precioAlmacen,noLote:null,fechaCaducidad:null]				
		}
		
		results
		
	}
	
    
}
