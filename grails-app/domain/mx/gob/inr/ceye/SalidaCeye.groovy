package mx.gob.inr.ceye

import mx.gob.inr.utils.Cie09
import mx.gob.inr.utils.domain.Salida;

class SalidaCeye extends Salida {

	static auditable = true
	
    CatAreaCeye area
	Cie09 diagnostico
	Short nosala
	String paqueteq
	String tipoVale
	
	static hasMany = [salidasDetalle:SalidaDetalleCeye]
	
	static transients = ['dueno']
	
	static constraints = {
	
	}

	static mapping = {
		table 'salida_ceye'
		id column:'id_salida'
		
		folio column:'numero_salida'
		fecha column:'fecha_salida'
		estado column:'estado_salida'
				
		entrego column:'entrego'		
		usuario column:'id_usuario'
		paciente column:'id_paciente'
		diagnostico column:'id_diagnostico'
		area column:'cve_area'
		id generator:'sequence' ,params:[sequence:'sq_idsalidaceye']
		version false
		
		usuario updateable: false
		paqueteq updateable:false
		fechaCaptura updateable:false		
	}
}
